/*******************************************************************************
 * Manchester Centre for Integrative Systems Biology
 * University of Manchester
 * Manchester M1 7ND
 * United Kingdom
 * 
 * Copyright (C) 2007 University of Manchester
 * 
 * This program is released under the Academic Free License ("AFL") v3.0.
 * (http://www.opensource.org/licenses/academic.php)
 *******************************************************************************/
package org.mcisb.kinetics.novostar;

import java.io.*;
import java.text.*;
import java.util.*;
import org.mcisb.kinetics.*;
import org.mcisb.ontology.sbo.*;
import org.mcisb.sbml.*;
import org.mcisb.tracking.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class NovostarExcelReader extends KineticsExcelReader
{
	/**
	 * 
	 */
	private final KineticsExperimentSet experimentSet;
	
	/**
	 *
	 * @param excelFile
	 * @param experimentSet
	 * @throws Exception
	 */
	public NovostarExcelReader( final File excelFile, final KineticsExperimentSet experimentSet ) throws Exception
	{
		super( excelFile );
		this.experimentSet = experimentSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.kinetics.KineticsExcelReader#getMetaData()
	 */
	@Override
	protected void getMetaData() throws Exception
	{
		final String META_DATA_WORKSHEET_NAME = "Sheet1"; //$NON-NLS-1$
		final int NAME_COLUMN = 0;
		final int EXPERIMENT_NAME_ROW = 0;
		final int EXPERIMENT_NAME_COLUMN = 5;
		final int EXPERIMENT_DATE_ROW = 0;
		final int EXPERIMENT_DATE_COLUMN = 10;
		final int EXPERIMENT_DESCRIPTION_ROW = 4;
		final int EXPERIMENT_DESCRIPTION_COLUMN = 0;
		final String DATE_PATTERN = "yyyy/MM/dd  HH:mm:ss"; //$NON-NLS-1$
		final String SPACE = " "; //$NON-NLS-1$
		final String NOVOSTAR = "NOVOstar"; //$NON-NLS-1$
		final String BMG_LABTECHNOLOGIES = "BMG Labtechnologies"; //$NON-NLS-1$
		final String TEMPERATURE = "Target temperature"; //$NON-NLS-1$
		final String USER = "User"; //$NON-NLS-1$
		final String COMMENT = "Comment"; //$NON-NLS-1$
		
		final UniqueObject instrument = new UniqueObject( StringUtils.getUniqueId(), BMG_LABTECHNOLOGIES + SPACE + NOVOSTAR );
		final History modelHistory = new History();
		final StringBuffer notes = new StringBuffer();
		
		int row = 0;
		
		for( Iterator<List<Object>> iterator = getData( META_DATA_WORKSHEET_NAME ).iterator(); iterator.hasNext(); )
		{
			final List<Object> rowData = iterator.next();
			String name = null;
			
			for( int column = 0; column < rowData.size(); column++ )
			{
				final Object data = rowData.get( column );
				
				if( data != null )
				{
					final String cell = data.toString().trim();
					
					if( row == EXPERIMENT_NAME_ROW && column == EXPERIMENT_NAME_COLUMN )
					{
						experimentSet.setName( cell );
					}
					else if( row == EXPERIMENT_DATE_ROW && column == EXPERIMENT_DATE_COLUMN )
					{
						final Calendar calendar = Calendar.getInstance();
						calendar.setTime( new SimpleDateFormat( DATE_PATTERN ).parse( cell ) );
						final Date date = calendar.getTime();
						modelHistory.setCreatedDate( date );
						modelHistory.setModifiedDate( date );
					}
					else if( row == EXPERIMENT_DESCRIPTION_ROW && column == EXPERIMENT_DESCRIPTION_COLUMN )
					{
						notes.append( cell );
					}
					else if( column == NAME_COLUMN )
					{
						name = cell;
					}
					else if( name != null && !name.equals( NOVOSTAR ) && !name.equals( BMG_LABTECHNOLOGIES ) )
					{
						if( name.equals( USER ) )
						{
							final String organisation = System.getProperty( "org.mcisb.kinetics.Organisation" ); //$NON-NLS-1$
							final Creator person = new Creator();
							person.setGivenName( cell );
							person.setFamilyName( cell );
							person.setEmail( cell );
							person.setOrganisation( organisation );
							modelHistory.addCreator( person );
						}
						else if( name.equals( TEMPERATURE ) && NumberUtils.isDecimal( cell ) )
						{
							experimentSet.getExperimentProtocol().setProperty( org.mcisb.tracking.PropertyNames.TEMPERATURE, Float.valueOf( cell ) );
						}
						else if( name.equals( COMMENT ) )
						{
							experimentSet.setDescription( cell );
						}
						else if( name.equals( org.mcisb.tracking.PropertyNames.SERIAL_NUMBER ) )
						{
							instrument.setProperty( name, cell );
						}
						else
						{
							experimentSet.getExperimentProtocol().setProperty( name, cell );
						}
						
						name = null;
					}
				}
			}
			
			row++;
		}
		
		instrument.setProperty( org.mcisb.tracking.PropertyNames.MANUFACTURER, BMG_LABTECHNOLOGIES );
		instrument.setProperty( org.mcisb.tracking.PropertyNames.MODEL, NOVOSTAR );
		
		experimentSet.getExperimentProtocol().setDescription( notes.toString() );
		experimentSet.setInstrument( instrument );
		experimentSet.setModelHistory( modelHistory );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.kinetics.KineticsExcelReader#getData()
	 */
	@Override
	protected void getData()
	{
		final int CELL_COLUMN = 0;
		final int SAMPLE_NAME_COLUMN = 1;
		final int SECOND_COLUMN = 2;
		final String RAW_DATA_WORKSHEET_NAME = "Sheet2"; //$NON-NLS-1$
		final double DEFAULT_CONCENTRATION = 0.0;
		
		final Collection<Spot> spots = new ArrayList<>();
		final List<Double> timepoints = new ArrayList<>();
		boolean timepointsSet = false;
		int rows = 0;
		int columns = 0;
		
		for( Iterator<List<Object>> iterator = getData( RAW_DATA_WORKSHEET_NAME ).iterator(); iterator.hasNext(); )
		{
			final List<Object> rowData = iterator.next();
			int row = NumberUtils.UNDEFINED;
			int column  = NumberUtils.UNDEFINED;
			List<Double> absorbanceData = null;
			Spot spot = null;
			
			for( int i = 0; i < rowData.size(); i++ )
			{
				final Object data = rowData.get( i );
				
				if( data != null )
				{
					if( i == CELL_COLUMN )
					{
						String cell = (String)data;
						row = cell.charAt( 0 ) - 'A';
						column = Integer.parseInt( cell.substring( 1 ) );
						rows = Math.max( row + 1, rows );
						columns = Math.max( column + 1, columns );
					}
					else if( i == SAMPLE_NAME_COLUMN )
					{						
						absorbanceData = new ArrayList<>();

						final String id = Character.valueOf( (char)( 'A' + row ) ).toString() + column;
						final Species species = new Species( SbmlUtils.DEFAULT_LEVEL, SbmlUtils.DEFAULT_VERSION );
						species.setNamespace( SbmlUtils.getDefaultSBMLNamespace() );
						species.setId( id );
						species.setName( id );
						species.setInitialConcentration( DEFAULT_CONCENTRATION );
	    				species.setUnits( org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT );
	    				species.setSBOTerm( SboUtils.SUBSTRATE );
						spot = new Spot( new SpotReading( species, absorbanceData ) );
						spot.setRow( row );
						spot.setColumn( column );
					}
					else if( i > SECOND_COLUMN )
					{
						if( !timepointsSet && data instanceof Double )
						{
							timepoints.add( (Double)data );
						}
						else if( absorbanceData != null && data instanceof Double )
						{
							absorbanceData.add( (Double)data );
						}
					}
				}
			}
			
			if( spot != null )
			{
				spots.add( spot );
			}
			
			timepointsSet = true;
		}
		
		final Plate plate = new Plate( rows, columns );

		for( Iterator<Spot> iterator = spots.iterator(); iterator.hasNext(); )
		{
			final Spot spot = iterator.next();
			plate.setValueAt( spot, spot.getRow(), spot.getColumn() );
		}
		
		experimentSet.setPlate( plate );
		experimentSet.setTimepoints( CollectionUtils.toDoubleArray( timepoints ) );
	}
}