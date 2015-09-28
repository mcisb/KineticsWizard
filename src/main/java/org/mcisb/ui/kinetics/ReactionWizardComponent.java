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
public class ReactionWizardComponent extends WizardComponent
{	
	/**
	 * 
	 */
	private final Model model;
	
	/**
	 *
	 * @param bean
	 * @param component
	 * @param model
	 */
	public ReactionWizardComponent( final GenericBean bean, final ParameterPanel component, final Model model )
	{
		super( bean, component );
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.WizardComponent#update()
	 */
	@Override
	public void update() throws Exception
	{
		final ReactionPanel reactionPanel = (ReactionPanel)parameterPanel;
		
		for( int l = 0; l < model.getNumReactions(); l++ )
		{
			final Reaction reaction = model.getReaction( l );
			KineticsUtils.addKineticLaw( reaction, SboUtils.ENZYMATIC_RATE_LAW_FOR_IRREVERSIBLE_NON_MODULATED_NON_INTERACTING_UNIREACTANT_ENZYMES );
		}
		
		final Parameter phParameter = model.createParameter();
		phParameter.setId( org.mcisb.tracking.PropertyNames.PH );
		phParameter.setValue( reactionPanel.getPH() );
		
		final Parameter temperatureParameter = model.createParameter();
		temperatureParameter.setId( org.mcisb.tracking.PropertyNames.TEMPERATURE );
		temperatureParameter.setValue( reactionPanel.getTemperature() );
		
		model.addParameter( phParameter );
		model.addParameter( temperatureParameter );
	}
}