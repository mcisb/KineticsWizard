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
package org.mcisb.kinetics;

import java.io.*;
import java.util.*;
import org.mcisb.ontology.sbo.*;
import org.mcisb.sbml.*;
import org.mcisb.tracking.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class DefaultKineticsExcelReader extends KineticsExcelReader
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
	public DefaultKineticsExcelReader( final File excelFile, final KineticsExperimentSet experimentSet ) throws Exception
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
		// No implementation.
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.kinetics.KineticsExcelReader#getData()
	 */
	@Override
	protected void getData()
	{
		final int CELL_COLUMN = 0;
		final double DEFAULT_CONCENTRATION = 0.0;
		
		final Collection<Spot> spots = new ArrayList<>();
		final List<Double> timepoints = new ArrayList<>();
		boolean timepointsSet = false;
		int rows = 0;
		int columns = 0;
		
		for( Iterator<List<Object>> iterator = getData( CollectionUtils.getFirst( getSheetNames() ) ).iterator(); iterator.hasNext(); )
		{
			final List<Object> rowData = iterator.next();
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
						final int row = cell.charAt( 0 ) - 'A';
						final int column = Integer.parseInt( cell.substring( 1 ) );
						rows = Math.max( row + 1, rows );
						columns = Math.max( column + 1, columns );
						
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
					else if( i > CELL_COLUMN )
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