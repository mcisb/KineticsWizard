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
package org.mcisb.ui.kinetics;

import java.util.*;
import org.mcisb.kinetics.*;
import org.mcisb.ontology.sbo.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class AbsorbanceSampleWizardComponent extends DefaultWizardComponent
{
	/**
	 * 
	 */
	private final KineticsExperimentSet experimentSet;
	
	/**
	 * 
	 */
	private final Model model;
	
	/**
	 * 
	 * @param bean
	 * @param component
	 * @param propertyNameToKey
	 * @param experimentSet
	 * @param model
	 */
	public AbsorbanceSampleWizardComponent( final GenericBean bean, final DefaultParameterPanel component, final Map<Object,Object> propertyNameToKey, final KineticsExperimentSet experimentSet, final Model model )
	{
		super( bean, component, propertyNameToKey );
		this.experimentSet = experimentSet;
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.DefaultWizardComponent#update()
	 */
	@Override
	public void update()
	{
		super.update();
		
		final double concentration = bean.getDouble( org.mcisb.kinetics.PropertyNames.ENZYME_CONCENTRATION ) * 1.0e-6; // nM to mM

		// Set concentration to all enzymes in the model:
		for( int l = 0; l < model.getNumSpecies(); l++ )
		{
			final Species species = model.getSpecies( l );
			
			if( species.getSBOTerm() == SboUtils.POLYPEPTIDE_CHAIN )
			{
				species.setInitialConcentration( concentration );
				species.setConstant( true );
			}
		}
		
		experimentSet.setAbsorptionCoefficient( model.getId(), (float)bean.getDouble( org.mcisb.kinetics.absorbance.PropertyNames.ABSORPTION_COEFFICIENT ) );
		experimentSet.getExperimentProtocol().setDouble( org.mcisb.kinetics.absorbance.PropertyNames.PATH_LENGTH, bean.getDouble( org.mcisb.kinetics.absorbance.PropertyNames.PATH_LENGTH ) );
	}
}