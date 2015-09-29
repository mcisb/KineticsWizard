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
package org.mcisb.kinetics.memo;

import java.sql.*;
import java.util.*;
import org.mcisb.db.sql.*;
import org.mcisb.kinetics.*;
import org.mcisb.sbml.*;
import org.mcisb.util.*;
import org.mcisb.util.math.*;
import org.mcisb.util.xml.*;
import org.sbml.jsbml.*;
import org.w3c.dom.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsExperimentWriter
{
	/**
	 * 
	 */
	private static final boolean bigEndian = true;
	
	/**
	 * 
	 */
	private static final int ENCODING_PRECISION = 8; // double
	
	/**
	 * 
	 */
	protected final StatementExecutor statementExecutor;
	
	/**
	 * 
	 */
	protected final SbmlUtils sbmlUtils = new SbmlUtils();
	
	/**
	 * 
	 */
	private final Connection connection;
	
	/**
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public KineticsExperimentWriter( final Connection connection ) throws SQLException
	{
		this.connection = connection;
		statementExecutor = new StatementExecutor( connection );
	}
	
	/**
	 * 
	 * @throws SQLException 
	 */
	public void close() throws SQLException
	{
		connection.close();
	}
	
	/**
	 * 
	 * @param experimentSet
	 * @param modelNameToInitialRates
	 * @throws Exception
	 */
	public void writeExperiment( final KineticsExperimentSet experimentSet, final Map<String,double[]> modelNameToInitialRates ) throws Exception
	{
		final UniqueObject experimentProtocol = experimentSet.getExperimentProtocol();
		final String experimentTypeId = writeExperimentType( experimentProtocol.getName() );
		final String methodId = writeMethod( experimentProtocol.getName() );
		
		final String updatedExperimentProtocolId = writeExperimentProtocol( experimentProtocol, experimentTypeId, methodId );
		final int FIRST = 0;
		final Creator person = CollectionUtils.getFirst( experimentSet.getDocuments() ).getModel().getHistory().getCreator( FIRST );
		final String updatedLabId = writeLab( person.getOrganisation() );
		final String updatedPersonId = writePerson( person, updatedLabId );
		final String experimentSetId = writeExperimentSet( experimentSet, updatedExperimentProtocolId, updatedLabId, updatedPersonId );
		writeExperiments( experimentSet, experimentSetId, modelNameToInitialRates );
		writeInstrument( experimentSet.getInstrument(), experimentSetId, methodId );
	}
	
	/**
	 * 
	 * @param experimentTypeName
	 * @return String
	 * @throws Exception
	 */
	private String writeExperimentType( final String experimentTypeName ) throws Exception
	{
		// If adm_experimentprotocol doesn't exist, create it:
		final List<List<Object>> values = statementExecutor.getValues( Arrays.asList( "voc_experiment_type.entry" ), Arrays.asList( "voc_experiment_type.definition='" + experimentTypeName + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String id = null;
		
		if( values.size() == 0 )
		{
			id = StringUtils.getUniqueId();
			statementExecutor.insert( "voc_experiment_type", Arrays.asList( id, experimentTypeName ) ); //$NON-NLS-1$
		}
		else
		{
			final List<?> row = CollectionUtils.getFirst( values );
			id = (String)CollectionUtils.getFirst( row );
		}
		
		return id;
	}
	
	/**
	 * 
	 * @param methodName
	 * @return String
	 * @throws Exception
	 */
	private String writeMethod( final String methodName ) throws Exception
	{
		// If adm_experimentprotocol doesn't exist, create it:
		final List<List<Object>> values = statementExecutor.getValues( Arrays.asList( "voc_method.entry" ), Arrays.asList( "voc_method.name='" + methodName + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String id = null;
		
		if( values.size() == 0 )
		{
			id = StringUtils.getUniqueId();
			statementExecutor.insert( "voc_method", Arrays.asList( id, methodName ) ); //$NON-NLS-1$
		}
		else
		{
			final List<?> row = CollectionUtils.getFirst( values );
			id = (String)CollectionUtils.getFirst( row );
		}
		
		return id;
	}
	
	/**
	 * 
	 * @param instrument
	 * @param experimentSetId
	 * @param methodId
	 * @throws Exception
	 */
	private void writeInstrument( final UniqueObject instrument, final String experimentSetId, final String methodId ) throws Exception
	{
		// If adm_experimentprotocol doesn't exist, create it:
		final List<List<Object>> values = statementExecutor.getValues( Arrays.asList( "adm_instrument.id" ), Arrays.asList( "adm_instrument.model='" + instrument.getProperty( org.mcisb.tracking.PropertyNames.MODEL ) + "'", "adm_instrument.manufacturer='" + instrument.getProperty( org.mcisb.tracking.PropertyNames.MANUFACTURER ) + "'", "adm_instrument.serial_number='" + instrument.getProperty( org.mcisb.tracking.PropertyNames.SERIAL_NUMBER ) + "'", "adm_instrument.method='" + methodId + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
		String instrumentId = instrument.getId();
		
		if( values.size() == 0 )
		{
			statementExecutor.insert( "adm_instrument", Arrays.asList( instrumentId, instrument.getName(), methodId, instrument.getProperty( org.mcisb.tracking.PropertyNames.MODEL ), instrument.getProperty( org.mcisb.tracking.PropertyNames.MANUFACTURER ), instrument.getProperty( org.mcisb.tracking.PropertyNames.SERIAL_NUMBER ) ) ); //$NON-NLS-1$
		}
		else
		{
			final List<?> row = CollectionUtils.getFirst( values );
			instrumentId = (String)CollectionUtils.getFirst( row );
		}
		
		final List<List<Object>> instrumentUsedValues = statementExecutor.getValues( Arrays.asList( "adm_instrumentused.set_id" ), Arrays.asList( "adm_instrumentused.set_id='" + experimentSetId + "'", "adm_instrumentused.instrument_id='" + instrumentId + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

		if( instrumentUsedValues.size() == 0 )
		{
			statementExecutor.insert( "adm_instrumentused", Arrays.asList( experimentSetId, instrumentId ) ); //$NON-NLS-1$
		}		
	}
	
	/**
	 * 
	 * @param experimentProtocol
	 * @param experimentTypeId
	 * @param methodId
	 * @return String
	 * @throws Exception
	 */
	private String writeExperimentProtocol( final UniqueObject experimentProtocol, final String experimentTypeId, final String methodId ) throws Exception
	{
		// If adm_experimentprotocol doesn't exist, create it:
		List<List<Object>> values = statementExecutor.getValues( Arrays.asList( "adm_experimentprotocol.id", "adm_experimentprotocol.parameters" ), Arrays.asList( "adm_experimentprotocol.experiment_type='" + experimentTypeId + "'", "adm_experimentprotocol.method='" + methodId + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
		String experimentProtocolId = null;
		
		for( Iterator<List<Object>> iterator = values.iterator(); iterator.hasNext(); )
		{
			final int ID = 0;
			final int PARAMETERS = 1;
			final List<Object> row = iterator.next();
			
			final String existingParameters = (String)row.get( PARAMETERS );
			
			if( CollectionUtils.toXml( experimentProtocol.getProperties() ).equals( existingParameters ) )
			{
				experimentProtocolId = (String)row.get( ID );
				break;
			}
		}
		
		if( experimentProtocolId == null )
		{
			experimentProtocolId = experimentProtocol.getId();
			final String parameters = CollectionUtils.toXml( experimentProtocol.getProperties() );
			statementExecutor.insert( "adm_experimentprotocol", Arrays.asList( experimentProtocolId, experimentTypeId, methodId, parameters, experimentProtocol.getDescription() ) ); //$NON-NLS-1$
		}
		
		return experimentProtocolId;
	}
	
	/**
	 *
	 * @param lab
	 * @return String
	 * @throws Exception
	 */
	private String writeLab( final String lab ) throws Exception
	{
		final String ADM_LAB = "adm_lab"; //$NON-NLS-1$
		
		final List<String> columnNamesReturned = new ArrayList<>();
		final List<List<Object>> values = statementExecutor.getValues( ADM_LAB, Arrays.asList( ADM_LAB + SqlUtils.FIELD_SEPARATOR + "name='" + lab + "'" ), columnNamesReturned ); //$NON-NLS-1$ //$NON-NLS-2$
		
		if( values.size() != 0 )
		{
			final String ID = "id"; //$NON-NLS-1$
			final List<Object> row = CollectionUtils.getFirst( values );
			return (String)row.get( columnNamesReturned.indexOf( ID ) );
		}
		
		// else
		final String id = StringUtils.getUniqueId();
		statementExecutor.insert( "adm_lab", Arrays.asList( id, lab ) ); //$NON-NLS-1$
		return id;
	}
	
	/**
	 *
	 * @param person
	 * @param labId
	 * @return String
	 * @throws Exception
	 */
	private String writePerson( Creator person, final String labId ) throws Exception
	{
		if( person != null )
		{
			final String SPACE = " "; //$NON-NLS-1$
			final String personName = ( person.getGivenName() + SPACE + person.getFamilyName() ).trim();
			String personId = null;
			
			// If adm_experimentprotocol doesn't exist, create it:
			List<List<Object>> values = statementExecutor.getValues( Arrays.asList( "adm_person.id" ), Arrays.asList( "adm_person.name='" + personName + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			if( values.size() != 0 )
			{
				final List<?> row = CollectionUtils.getFirst( values );
				personId = (String)CollectionUtils.getFirst( row );
			}
			
			if( personId == null )
			{
				personId = StringUtils.getUniqueId();
				statementExecutor.insert( "adm_person", Arrays.asList( personId, personName, labId, null, null, null, person.getEmail(), null ) ); //$NON-NLS-1$	
			}
			
			// Ensure that adm_lab is updated if necessary:
			/*
			values = statementExecutor.getValues( Arrays.asList( "adm_lab.person_id" ), Arrays.asList( "adm_lab.id='" + labId + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			final List<?> row = CollectionUtils.getFirst( values );
			String databasePersonId = (String)CollectionUtils.getFirst( row );
			
			if( databasePersonId == null )
			{
				final Map<String,Object> nameValuePairs = new HashMap<String,Object>();
				nameValuePairs.put( "adm_lab.person_id", personId ); //$NON-NLS-1$
				statementExecutor.update( nameValuePairs, Arrays.asList( "adm_lab.id='" + labId + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			*/
			
			return personId;
		}
		
		final Creator newPerson = new Creator();
		newPerson.setFamilyName( StringUtils.getUniqueId() );
		
		return writePerson( newPerson, labId );
	}
	
	/**
	 *
	 * @param experimentSet
	 * @param protocolId
	 * @param labId
	 * @param personId
	 * @return String
	 * @throws Exception
	 */
	private String writeExperimentSet( final KineticsExperimentSet experimentSet, final String protocolId, final String labId, final String personId ) throws Exception
	{
		final List<List<Object>> values = statementExecutor.getValues( Arrays.asList( "adm_experimentset.id" ), Arrays.asList( "adm_experimentset.name='" + experimentSet.getName() + "'", "adm_experimentset.experiment_type='" + experimentSet.getExperimentProtocol().getName() + "'", "adm_experimentset.lab_id='" + labId + "'", "adm_experimentset.protocol_id='" + protocolId + "'", "adm_experimentset.conductor_id='" + personId + "'", "adm_experimentset.comment='" + experimentSet.getDescription() + "'" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
		
		if( values.size() != 0 )
		{
			List<?> row = CollectionUtils.getFirst( values );
			experimentSet.setId( (String)CollectionUtils.getFirst( row ) );
		}
		else
		{
			statementExecutor.insert( "adm_experimentset", Arrays.asList( experimentSet.getId(), experimentSet.getName(), experimentSet.getExperimentProtocol().getName(), labId, protocolId, personId, experimentSet.getDescription() ) ); //$NON-NLS-1$	
		}
		
		return experimentSet.getId();
	}
	
	/**
	 * 
	 * @param experimentSet
	 * @param experimentSetId
	 * @param modelNameToInitialRates
	 * @throws Exception
	 */
	private void writeExperiments( final KineticsExperimentSet experimentSet, final String experimentSetId, final Map<String,double[]> modelNameToInitialRates ) throws Exception
	{
		final int MAXIMUM_LENGTH = 50;
		final double[] timepoints = experimentSet.getTimepoints();
		final String timepointsEncoded = MathUtils.encode( timepoints, bigEndian );
		
		for( Iterator<SBMLDocument> iterator = experimentSet.getDocuments().iterator(); iterator.hasNext(); )
		{
			final SBMLDocument document = iterator.next();
			final Model model = document.getModel();
			final String name = model.getName().length() > MAXIMUM_LENGTH ? model.getName().substring( 0, MAXIMUM_LENGTH ) : model.getName();
			final Element notes = (Element)( ( model.getNotesString() == null || model.getNotesString().length() == 0 ) ? null : XmlUtils.getNode( model.getNotesString() ) );
			String notesString = null;
			
			if( notes != null )
			{
				notesString = XmlUtils.getSimpleElementText( XmlUtils.getFirstElement( XmlUtils.getFirstElement( notes, "body" ), "pre" ) ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			statementExecutor.insert( "adm_experiment", Arrays.asList( model.getId(), name, experimentSetId, new java.sql.Date( SbmlUtils.getCreatedDate( model ) ).toString(), new java.sql.Time( SbmlUtils.getCreatedDate( model ) ).toString(), new java.sql.Date( SbmlUtils.getCreatedDate( model ) ).toString(), new java.sql.Time( SbmlUtils.getCreatedDate( model ) ).toString(), notesString ) ); //$NON-NLS-1$	
		
			final Map<String,Object> properties = new HashMap<>();
			properties.put( org.mcisb.kinetics.absorbance.PropertyNames.ABSORPTION_COEFFICIENT, Double.valueOf( experimentSet.getAbsorptionCoefficient( model.getId() ) ) );
			properties.put( org.mcisb.kinetics.PropertyNames.BUFFER, KineticsUtils.getBuffer( experimentSet.getBuffer( model.getId() ) ) );
			writeExperimentParameters( model.getId(), properties );
			
			writeSamples( experimentSet, document, modelNameToInitialRates, timepointsEncoded );
		}
	}
	
	/**
	 * 
	 * @param experimentId
	 * @param properties
	 * @throws SQLException
	 */
	private void writeExperimentParameters( final String experimentId, final Map<String,Object> properties ) throws SQLException
	{
		for( Iterator<Map.Entry<String,Object>> iterator = properties.entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<String,Object> entry = iterator.next();
			statementExecutor.insert( "adm_experimentparameters", Arrays.asList( StringUtils.getUniqueId(), experimentId, entry.getKey(), entry.getValue() ) ); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * @param experimentSet
	 * @param document
	 * @param modelNameToInitialRates
	 * @param timepointsEncoded
	 * @throws Exception
	 */
	private void writeSamples( final KineticsExperimentSet experimentSet, final SBMLDocument document, final Map<String,double[]> modelNameToInitialRates, final String timepointsEncoded ) throws Exception
	{
		final Model model = document.getModel();
		final double[] initialRates = modelNameToInitialRates.get( model.getName() );
		int i = 0;
		
		for( int l = 0; l < model.getNumReactions(); l++ )
		{
			// Write Sample:
			final Reaction reaction = model.getReaction( l );
			
			for( int m = 0; m < reaction.getNumReactants(); m++ )
			{
				final Species species = model.getSpecies( reaction.getReactant( m ).getSpecies() );
				final double[] absorbanceData = experimentSet.getAbsorbanceData( species.getId() );
				
				if( absorbanceData.length > 0 )
				{
					// Write sub Sample:
					final SBMLDocument subDocument = SbmlUtils.getSubDocument( document, reaction.getId() );
					statementExecutor.insert( "smp_sample", Arrays.asList( reaction.getId(), model.getId(), new java.sql.Date( SbmlUtils.getCreatedDate( model ) ).toString(), new SBMLWriter().writeSBMLToString( subDocument ) ) ); //$NON-NLS-1$
					
					// Write Analysis:
					final String analysisId = StringUtils.getUniqueId();
					statementExecutor.insert( "anl_analysis", Arrays.asList( analysisId, model.getId(), reaction.getId() ) ); //$NON-NLS-1$
					
					// Write Timepoints:
					final String timeseriesId = StringUtils.getUniqueId();
					final String productReadingsEncoded = MathUtils.encode( absorbanceData, bigEndian );
					statementExecutor.insert( "anl_timeseries", Arrays.asList( timeseriesId, analysisId, Boolean.valueOf( bigEndian ), Integer.valueOf( ENCODING_PRECISION ), timepointsEncoded, productReadingsEncoded, ( reaction.getNumModifiers() > 0 ) ? Double.valueOf( initialRates[ i++ ] ) : null ) ); //$NON-NLS-1$
				}
			}
		}
	}
}