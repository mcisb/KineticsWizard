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

import java.beans.*;
import java.util.*;
import org.mcisb.kinetics.*;
import org.mcisb.ontology.sbo.*;
import org.mcisb.sbml.*;
import org.mcisb.tracking.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class SampleSelectionWizardComponent extends WizardComponent
{
	/**
	 * 
	 */
	private KineticsExperimentSet experimentSet;
	
	/**
	 * 
	 */
	private boolean updated = false;
	
	/**
	 *
	 * @param bean
	 * @param component
	 */
	public SampleSelectionWizardComponent( final GenericBean bean, final SampleSelectionPanel component )
	{
		super( bean, component );
	}

	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.WizardComponent#display()
	 */
	@Override
	public void display() throws Exception
	{
		updated = false;
		super.display();
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.DefaultWizardComponent#update()
	 */
	@Override
	public void update() throws Exception
	{
		if( !updated )
		{
			final SampleSelectionPanel sampleSelectionPanel = (SampleSelectionPanel)parameterPanel;
			
			for( Iterator<Map.Entry<Object,Collection<Object>>> iterator = sampleSelectionPanel.getGroups().entrySet().iterator(); iterator.hasNext(); )
			{
				final Map.Entry<Object,Collection<Object>> entry = iterator.next();
				final Object group = entry.getKey();
				
				// Create model for each group:
				final SBMLDocument document = new SBMLDocument();
				final Model model = document.createModel( StringUtils.getUniqueId() );
				model.setLevel( SbmlUtils.DEFAULT_LEVEL );
				model.setVersion( SbmlUtils.DEFAULT_VERSION );
				model.setId( StringUtils.getUniqueId() );
				model.setName( group.toString() );
				KineticsUtils.addConcentrationUnitDefinition( model );
				KineticsUtils.addKcatUnitDefinition( model );
				
				if( experimentSet != null && experimentSet.getModelHistory() != null )
				{
					model.setHistory( experimentSet.getModelHistory() );
				}
				
				// Add modifier:
				final Species enzyme = model.createSpecies();
				enzyme.setId( StringUtils.getUniqueId() );
				enzyme.setSBOTerm( SboUtils.POLYPEPTIDE_CHAIN );
				
				for( Iterator<Object> iterator2 = entry.getValue().iterator(); iterator2.hasNext(); )
				{
					final Object value = iterator2.next();

					if( value instanceof Spot )
		    		{
						final SpotReading spotReading = ( (Spot)value ).getUserValue();
		    			final Object spotReadingUserObject = spotReading.getUserObject();
		    				
	    				if( spotReadingUserObject instanceof Species )
	    				{
	    					final Species spotSpecies = (Species)spotReadingUserObject;
	    					
	    					if( sampleSelectionPanel.isBlank( spotSpecies ) || sampleSelectionPanel.isSample( spotSpecies ) )
	    					{
	    						final Species species = model.createSpecies();
	    						species.setId( spotSpecies.getId() );
	    						species.setName( spotSpecies.getName() );
	    						species.setInitialConcentration( spotSpecies.getInitialConcentration() );
	    						species.setUnits( spotSpecies.getUnits() );
	    						species.setSBOTerm( spotSpecies.getSBOTerm() );
            					
            					final Reaction reaction = model.createReaction();
            					reaction.setId( StringUtils.getUniqueId() );
            					reaction.setName( group.toString() );
            					
            					final SpeciesReference speciesReference = reaction.createReactant();
            					speciesReference.setSpecies( species.getId() );
            					
            					// Add modifier to each reaction that is not a blank:
            					if( sampleSelectionPanel.isSample( spotSpecies ) )
            					{
            						final ModifierSpeciesReference modifierSpeciesReference = reaction.createModifier();
            						modifierSpeciesReference.setSpecies( enzyme.getId() );
            					}
	    					}
	    				}
	    			}
				}
				
				if( experimentSet != null )
				{
					experimentSet.addDocument( document );
				}
			}

			updated = true;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		super.propertyChange( evt );
		
		if( evt.getPropertyName().equals( AbsorbanceWizard.NEW_EXPERIMENT_SET ) )
		{
			experimentSet = (KineticsExperimentSet)evt.getNewValue();
		}
	}
}