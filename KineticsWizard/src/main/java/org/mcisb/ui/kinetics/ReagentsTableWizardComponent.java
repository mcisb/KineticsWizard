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
import org.mcisb.sbml.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class ReagentsTableWizardComponent extends WizardComponent
{
	/**
	 * 
	 */
	private final Map<OntologyTerm,Species> ontologyTermToSpecies = new HashMap<>();

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
	public ReagentsTableWizardComponent( final GenericBean bean, final ParameterPanel component, final KineticsExperimentSet experimentSet, final Model model )
	{
		super( bean, component );
		this.experimentSet = experimentSet;
		this.model = model;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.WizardComponent#display()
	 */
	@Override
	public void display() throws Exception
	{
		final Collection<OntologyTerm> defaultOntologyTerms = new HashSet<>();
		
		for( int l = 0; l < model.getNumSpecies(); l++ )
		{
			final Species species = model.getSpecies( l );
			
			if( species.getInitialConcentration() == NumberUtils.UNDEFINED )
			{
				final OntologyTerm ontologyTerm = SbmlUtils.getOntologyTerm( species, Ontology.CHEBI );
				defaultOntologyTerms.add( ontologyTerm );
				ontologyTermToSpecies.put( ontologyTerm, species );
			}
		}
		
		( (ReagentsTableParameterPanel)parameterPanel ).setDefaultOntologyTerms( defaultOntologyTerms );
		
		/*
		final Collection<String> ncbiTaxonomyTerms = new HashSet<String>();
		final OntologyTerm taxonomyTerm = sbmlUtils.getOntologyTerm( model, Ontology.TAXONOMY );
		ncbiTaxonomyTerms.add( taxonomyTerm.getId() );
		( (ReagentsTableParameterPanel)parameterPanel ).setTaxonomy( ncbiTaxonomyTerms );
		*/
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.DefaultWizardComponent#update()
	 */
	@Override
	public void update() throws Exception
	{
		final ReagentsTableParameterPanel panel = (ReagentsTableParameterPanel)parameterPanel;
		experimentSet.setBuffer( model.getId(), panel.getBuffer() );
		
		for( Iterator<Map.Entry<OntologyTerm,Double>> iterator = panel.getDefaultOntologyTerms().entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<OntologyTerm,Double> entry = iterator.next();
			final Species species = ontologyTermToSpecies.get( entry.getKey() );
			species.setInitialConcentration( entry.getValue().doubleValue() );
			species.setUnits( org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT );
		}
	}
}