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
import org.mcisb.ontology.*;
import org.mcisb.ontology.kegg.*;
import org.mcisb.ontology.sbo.*;
import org.mcisb.sbml.*;
import org.mcisb.ui.ontology.kegg.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class KeggWizardComponent extends WizardComponent
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
	 * @param experimentSet 
	 * @param model
	 */
	public KeggWizardComponent( final GenericBean bean, final ParameterPanel component, final KineticsExperimentSet experimentSet, final Model model )
	{
		super( bean, component );
		this.model = model;
		this.experimentSet = experimentSet;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.WizardComponent#update()
	 */
	@Override
	public void update() throws Exception
	{
		final KeggPanel keggPanel = (KeggPanel)parameterPanel;
		
		// Annotate model with taxonomy:
		final Map<OntologyTerm,Object[]> ontologyTerms = new HashMap<>();
		ontologyTerms.put( keggPanel.getOrganism(), new Object[] { CVTerm.Type.BIOLOGICAL_QUALIFIER, CVTerm.Qualifier.BQB_IS } );
		SbmlUtils.addOntologyTerm( model, CollectionUtils.getFirst( OntologyUtils.getInstance().getXrefs( ontologyTerms.keySet(), Ontology.TAXONOMY ) ), CVTerm.Type.MODEL_QUALIFIER, CVTerm.Qualifier.BQM_IS_DESCRIBED_BY );
		
		// Get enzyme OntologyTerms:
		final OntologyTerm geneTerm = keggPanel.getGene();
		final Map<OntologyTerm,Object[]> xrefs = new HashMap<>();
		xrefs.put( geneTerm, new Object[] { CVTerm.Type.BIOLOGICAL_QUALIFIER, CVTerm.Qualifier.BQB_IS } );
		OntologyUtils.getInstance().getXrefs( xrefs );
		
		// Annotate enyzmes:
		for( int l = 0; l < model.getNumSpecies(); l++ )
		{
			final Species species = model.getSpecies( l );
			
			if( species.getSBOTerm() == SboUtils.POLYPEPTIDE_CHAIN )
			{
				species.setName( geneTerm.getName() );
				SbmlUtils.addOntologyTerms( species, xrefs );	
			}
		}

		// Annotate reactions:
		final KeggReactionTerm reactionKeggTerm = keggPanel.getReaction();
		
		for( int l = 0; l < model.getNumReactions(); l++ )
		{
			final Reaction subReaction = model.getReaction( l );
			SbmlUtils.addOntologyTerm( subReaction, reactionKeggTerm, CVTerm.Type.BIOLOGICAL_QUALIFIER, CVTerm.Qualifier.BQB_IS );
		}
		
		final OntologyTerm variableSubstrateTerm = keggPanel.getSubstrate();
		final boolean isForward = keggPanel.isForward();
		final Map<OntologyTerm,Double> substrates = ( isForward ) ? reactionKeggTerm.getSubstrates() : reactionKeggTerm.getProducts();
		final Map<OntologyTerm,Double> products = ( isForward ) ? reactionKeggTerm.getProducts() : reactionKeggTerm.getSubstrates();
		
		for( Iterator<Map.Entry<OntologyTerm,Double>> iterator = substrates.entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<OntologyTerm,Double> entry = iterator.next();
			final OntologyTerm substrateTerm = entry.getKey();
			final double stoichiometry = entry.getValue().doubleValue();
			
			if( substrateTerm.equals( variableSubstrateTerm ) )
			{
				// Variable substrate already exists in model. Update name and cvTerms:
				for( int l = 0; l < model.getNumReactions(); l++ )
				{
					final Reaction reaction = model.getReaction( l );
					final SpeciesReference reference = reaction.getReactant( 0 );
					final Species species = model.getSpecies( reference.getSpecies() );
					reference.setStoichiometry( stoichiometry );
					
					species.setName( substrateTerm.getName() );
					
					final Map<OntologyTerm,Object[]> substrateTerms = new HashMap<>();
					substrateTerms.put( substrateTerm, new Object[] { CVTerm.Type.BIOLOGICAL_QUALIFIER, CVTerm.Qualifier.BQB_IS } );
					OntologyUtils.getInstance().getXrefs( substrateTerms );
					SbmlUtils.addOntologyTerms( species, substrateTerms );
				}
			}
			else
			{
				final Species reactantSpecies = getReactionSpecies( substrateTerm );
				reactantSpecies.setInitialConcentration( NumberUtils.UNDEFINED );
				
				// Add other reactants:
				for( int l = 0; l < model.getNumReactions(); l++ )
				{
					final Reaction reaction = model.getReaction( l );
					final SpeciesReference reference = reaction.createReactant();
					reference.setSpecies( reactantSpecies.getId() );
					reference.setStoichiometry( stoichiometry );
				}
			}
		}
		
		for( Iterator<Map.Entry<OntologyTerm,Double>> iterator = products.entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<OntologyTerm,Double> entry = iterator.next();
			final OntologyTerm product = entry.getKey();
			final double stoichiometry = entry.getValue().doubleValue();
			
			final Species productSpecies = getReactionSpecies( product );
			
			// Add products:
			for( int l = 0; l < model.getNumReactions(); l++ )
			{
				final Reaction reaction = model.getReaction( l );
				final SpeciesReference reference = reaction.createProduct();
				reference.setSpecies( productSpecies.getId() );
				reference.setStoichiometry( stoichiometry );
			}
		}
		
		// Add Hill consideration:
		experimentSet.setConsiderHillCoefficient( model.getId(), keggPanel.considerHillCoefficient() );
	}
	
	/**
	 * 
	 *
	 * @param ontologyTerm
	 * @return Species
	 * @throws Exception
	 */
	private Species getReactionSpecies( final OntologyTerm ontologyTerm ) throws Exception
	{
		final Species species = model.createSpecies();
		species.setId( StringUtils.getUniqueId() );
		species.setName( ontologyTerm.getName() );
		
		final Map<OntologyTerm,Object[]> xrefs = new HashMap<>();
		xrefs.put( ontologyTerm, new Object[] { CVTerm.Type.BIOLOGICAL_QUALIFIER, CVTerm.Qualifier.BQB_IS } );
		OntologyUtils.getInstance().getXrefs( xrefs );
		SbmlUtils.addOntologyTerms( species, xrefs );
		
		return species;
	}
}