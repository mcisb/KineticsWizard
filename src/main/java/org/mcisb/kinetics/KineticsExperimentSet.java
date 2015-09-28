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

import java.util.*;
import org.mcisb.ontology.*;
import org.mcisb.tracking.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsExperimentSet extends NamedObject
{
	/**
	 * 
	 */
	private final Collection<SBMLDocument> documents = new ArrayList<>();
	
	/**
	 * 
	 */
	private final Map<String,Double> modelIdToAbsorptionCoefficient = new HashMap<>();

	/**
	 * 
	 */
	private final Map<String,Map<OntologyTerm,Double>> modelIdToBuffer = new HashMap<>();
	
	/**
	 * 
	 */
	private final Map<String,Boolean> modelIdToConsiderHillCoefficient = new HashMap<>();
	
	/**
	 * 
	 */
	private final Map<String,Map<String,String>> sbaseIdToConditions = new HashMap<>();
    
	/**
	 * 
	 */
	private String id;
	
	/**
	 * 
	 */
	private String experimentStudy;
	
	/**
	 * 
	 */
	private final UniqueObject experimentProtocol;
	
	/**
	 * 
	 */
	private UniqueObject instrument;
	
	/**
	 * 
	 */
	private double[] timepoints;
	
	/**
	 * 
	 */
	private Plate plate;
	
	/**
	 * 
	 */
	private transient History modelHistory;
	
	/**
	 * 
	 * @param id
	 * @param experimentType 
	 * @throws IllegalArgumentException 
	 */
    public KineticsExperimentSet( final String id, final String experimentType ) throws IllegalArgumentException
    {
		this.setId( id );
		experimentProtocol = new UniqueObject( StringUtils.getUniqueId(), experimentType );
	}
    
    /**
     * 
     * @return id
     */
    public String getId()
	{
		return id;
	}
    
    /**
     * 
     * @param id
     * @throws IllegalArgumentException 
     */
	public void setId( String id ) throws IllegalArgumentException
	{
		if( id == null )
		{
			throw new IllegalArgumentException();
		}
		
		this.id = id;
	}

	/**
     * 
     * @return UniqueObject
     */
	public UniqueObject getInstrument()
	{
		return instrument;
	}

	/**
	 * 
	 * @param instrument
	 */
	public void setInstrument( final UniqueObject instrument )
	{
		this.instrument = instrument;
	}

	/**
     * 
     * @return String
     */
    public String getExperimentStudy()
    {
    	return experimentStudy;
    }
    
    /**
     * 
     * @param experimentStudy
     */
    public void setExperimentStudy( final String experimentStudy )
    {
    	this.experimentStudy = experimentStudy;
    }

	/**
	 *
	 * @param timepoints
	 */
	public void setTimepoints( final double[] timepoints )
	{
		this.timepoints =  new double[ timepoints.length ];
		System.arraycopy( timepoints, 0, this.timepoints, 0, timepoints.length );
	}

	/**
	 *
	 * @return plate
	 */
	public Plate getPlate()
	{
		return plate;
	}

	/**
	 *
	 * @param plate
	 */
	public void setPlate( final Plate plate )
	{
		this.plate = plate;
	}

	/**
	 *
	 * @param document
	 */
	public void addDocument( final SBMLDocument document )
	{
		documents.add( document );
	}
	
	/**
	 *
	 * @return Collection
	 */
	public Collection<SBMLDocument> getDocuments()
	{
		return documents;
	}
	
	/**
	 * 
	 * @return double[]
	 */
	public double[] getTimepoints()
	{
		final double[] timepointsCopy =  new double[ timepoints.length ];
		System.arraycopy( timepoints, 0, timepointsCopy, 0, timepointsCopy.length );
		return timepointsCopy;
	}
	
	/**
	 * 
	 *
	 * @param modelId
	 * @return Map
	 */
	public Map<OntologyTerm,Double> getBuffer( final String modelId )
	{
		return modelIdToBuffer.get( modelId );
	}
	
	/**
	 *
	 * @param modelId
	 * @param buffer
	 */
	public void setBuffer( final String modelId, final Map<OntologyTerm,Double> buffer )
	{
		modelIdToBuffer.put( modelId, buffer );
	}
	
	/**
	 * 
	 *
	 * @param speciesId
	 * @return double[]
	 */
	@SuppressWarnings("unchecked")
	public double[] getAbsorbanceData( final String speciesId )
	{
		for( Iterator<Spot> iterator = plate.getSpots().iterator(); iterator.hasNext(); )
		{
			final SpotReading spotReading = iterator.next().getUserValue();
			final Object userObject = spotReading.getUserObject();
				
			if( userObject instanceof Species && ( (Species)userObject ).getId().equals( speciesId ) ) 
			{
				final Object data = spotReading.getData();
				return CollectionUtils.toDoubleArray( (Collection<Double>)data );
			}
		}
		
		return new double[ 0 ];
	}
	
	/**
	 * 
	 *
	 * @param modelId
	 * @return double
	 */
	public double getAbsorptionCoefficient( final String modelId )
	{
		return modelIdToAbsorptionCoefficient.get( modelId ).doubleValue();
	}
	
	/**
	 * 
	 *
	 * @param modelId
	 * @param absorptionCoefficient
	 */
	public void setAbsorptionCoefficient( final String modelId, final double absorptionCoefficient )
	{
		modelIdToAbsorptionCoefficient.put( modelId, Double.valueOf( absorptionCoefficient ) );
	}
	
	/**
	 * 
	 *
	 * @param modelId
	 * @return boolean
	 */
	public boolean getConsiderHillCoefficient( final String modelId )
	{
		return modelIdToConsiderHillCoefficient.get( modelId ).booleanValue();
	}

	/**
	 * 
	 * @param modelId
	 * @param considerHillCoefficient
	 */
	public void setConsiderHillCoefficient( final String modelId, final boolean considerHillCoefficient )
	{
		modelIdToConsiderHillCoefficient.put( modelId, Boolean.valueOf( considerHillCoefficient ) );
	}
   
	/**
	 * 
	 * @return UniqueObject
	 */
	public UniqueObject getExperimentProtocol()
	{
		return experimentProtocol;
	}
	
	/**
	 * 
	 * @param sbase
	 * @param conditionName
	 * @param value
	 */
	public void addCondition( final LocalParameter sbase, final String conditionName, final String value )
	{
		Map<String,String> conditions = sbaseIdToConditions.get( sbase.getId() );
		
		if( conditions == null )
		{
			conditions = new HashMap<>();
			sbaseIdToConditions.put( sbase.getId(), conditions );
		}
		
		conditions.put( conditionName, value );
	}
	
	/**
	 * 
	 *
	 * @param parameter
	 * @param conditionName
	 * @return String
	 */
	public String getCondition( final LocalParameter parameter, final String conditionName )
	{
		final Map<String,String> conditions = sbaseIdToConditions.get( parameter.getId() );
		
		if( conditions != null )
		{
			return conditions.get( conditionName );
		}
		
		return null;
	}

	/**
	 * @param modelHistory the modelHistory to set
	 */
	public void setModelHistory( History modelHistory )
	{
		this.modelHistory = modelHistory;
	}

	/**
	 * @return the modelHistory
	 */
	public History getModelHistory()
	{
		return modelHistory;
	}

	/*
	 * 
	 */
	@Override
	public boolean equals( final Object object )
	{
		if( object instanceof KineticsExperimentSet )
		{
			return id.equals( ( (KineticsExperimentSet)object ).id );
		}
		
		return false;
	}

	/*
	 * 
	 */
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}