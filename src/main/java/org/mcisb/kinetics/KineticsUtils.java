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
import org.mcisb.ontology.sbo.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public abstract class KineticsUtils
{
	/**
	 * 
	 * @param model
	 * @return Compartment
	 */
	public static UnitDefinition addConcentrationUnitDefinition( final Model model )
	{
		final UnitDefinition unitDefinition = model.createUnitDefinition();
		unitDefinition.setId( org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT );
		unitDefinition.setName( org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT );
		
		final Unit milliMoles = unitDefinition.createUnit();
		milliMoles.setKind( Unit.Kind.MOLE );
		milliMoles.setScale( -3 );
		final Unit perLitre = unitDefinition.createUnit();
		perLitre.setKind( Unit.Kind.LITRE );
		perLitre.setExponent( -1.0 );
		
		return unitDefinition;
	}
	
	/**
	 * 
	 * @param model
	 * @return Compartment
	 */
	public static UnitDefinition addKcatUnitDefinition( final Model model )
	{
		final UnitDefinition unitDefinition = model.createUnitDefinition();
		unitDefinition.setId( org.mcisb.kinetics.PropertyNames.KCAT_UNIT );
		unitDefinition.setName( org.mcisb.kinetics.PropertyNames.KCAT_UNIT_NAME );
		
		final Unit perSecond = unitDefinition.createUnit();
		perSecond.setKind( Unit.Kind.SECOND );
		perSecond.setExponent( -1.0 );
		
		return unitDefinition;
	}
	
	/**
	 * 
	 * @param bufferMap
	 * @return String
	 */
	public static String getBuffer( final Map<OntologyTerm,Double> bufferMap )
	{
		final String SPACE = " "; //$NON-NLS-1$
		final String SEPARATOR = ", "; //$NON-NLS-1$
		final String EMPTY_STRING = ""; //$NON-NLS-1$
		final double ZERO = 0.0;
		final StringBuffer buffer = new StringBuffer();
		
		for( Iterator<Map.Entry<OntologyTerm,Double>> iterator = bufferMap.entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<OntologyTerm,Double> entry = iterator.next();
			buffer.append( ( ( entry.getValue().doubleValue() > ZERO ) ? ( entry.getValue() + org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT + SPACE ) : EMPTY_STRING ) + entry.getKey() + SEPARATOR );
		}
		
		if( buffer.length() > 0 )
		{
			buffer.setLength( buffer.length() - SEPARATOR.length() );
		}
		
		return buffer.toString();
	}
	
	/**
	 *
	 * @param reaction 
	 * @param sboTermId 
	 * @return KineticLaw
	 * @throws Exception
	 */
	public static KineticLaw addKineticLaw( final Reaction reaction, final int sboTermId ) throws Exception
	{
		SboUtils.getInstance();
		final SboTerm ontologyTerm = (SboTerm)SboUtils.getOntologyTerm( sboTermId );
		final KineticLaw kineticLaw = reaction.createKineticLaw();
		kineticLaw.setMath( JSBML.readMathMLFromString( ontologyTerm.getMath() ) );
		kineticLaw.setSBOTerm( sboTermId );
		return kineticLaw;
	}
	
	/**
	 * 
	 * @param kineticLaw
	 * @param sboTermId
	 * @param unitsId 
	 * @return Parameter
	 * @throws Exception
	 */
	public static LocalParameter addParameter( final KineticLaw kineticLaw, final int sboTermId, final String unitsId ) throws Exception
	{
		final LocalParameter parameter = kineticLaw.createLocalParameter();
		parameter.setId( StringUtils.getUniqueId() );
		
		if( unitsId != null )
		{
			parameter.setUnits( unitsId );
		}
		
		SboUtils.getInstance();
		//TODO: check-out effect of parameter.setConstant()
		// parameter.setConstant( true );
		parameter.setName( SboUtils.getOntologyTerm( sboTermId ).getName() );
		parameter.setSBOTerm( sboTermId );
		return parameter;
	}

	/**
	 * 
	 * @param kineticLaw
	 * @param sboTermId
	 * @return LocalParameter
	 * @throws Exception
	 */
	public static LocalParameter addParameter( final KineticLaw kineticLaw, final int sboTermId ) throws Exception
	{
		return addParameter( kineticLaw, sboTermId, null );
	}
}