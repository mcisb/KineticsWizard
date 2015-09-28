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
package org.mcisb.kinetics.sabio;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.stream.*;
import org.mcisb.kinetics.*;
import org.mcisb.ontology.*;
import org.mcisb.ontology.chebi.*;
import org.mcisb.ontology.sbo.*;
import org.mcisb.sbml.*;
import org.mcisb.util.*;
import org.mcisb.util.math.*;
import org.mcisb.util.xml.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class SabioXmlWriter extends XmlWriter
{
	/**
	 * 
	 */
	private static final String SPACE = " "; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final String MODIFIER_CATALYST = "Modifier-Catalyst"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private int speciesCount = 0;
	
	/**
	 * 
	 * @param os
	 * @throws Exception
	 */
	public SabioXmlWriter( final OutputStream os ) throws Exception
	{
		super( XMLOutputFactory.newInstance().createXMLEventWriter( os ) );
	}
	
	/**
	 * 
	 * @param experimentSet
	 * @param schema
	 * @param username 
	 * @throws Exception
	 */
	public void write( final KineticsExperimentSet experimentSet, final URL schema, final String username ) throws Exception
	{
		final String PREFIX = "xsi"; //$NON-NLS-1$
		final String URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
		final String XMLNS = "xmlns"; //$NON-NLS-1$
		final String SABIO = "submissionTool"; //$NON-NLS-1$
		final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation"; //$NON-NLS-1$
		final String SCHEMA_LOCATION = SABIO + SPACE + schema;
		final String KINETICS_REPORT = "KineticsReport"; //$NON-NLS-1$
		final String LABORATORY = "laboratory"; //$NON-NLS-1$
		final String PROJECT = "project"; //$NON-NLS-1$
		final String SUBMITTER = "submitter"; //$NON-NLS-1$
		final String LOCAL_EXPERIMENT_ID = "localexpid"; //$NON-NLS-1$
		final String COMMENTS = "comments"; //$NON-NLS-1$
		final String REPORT_ENTRIES = "reportentries"; //$NON-NLS-1$
		
		final int FIRST = 0;
		final String providerId = System.getProperty( "org.mcisb.kinetics.Provider" ); //$NON-NLS-1$
		final Creator person = CollectionUtils.getFirst( experimentSet.getDocuments() ).getModel().getHistory().getCreator( FIRST );
		
		writeStartDocument();
		writeStartElement( KINETICS_REPORT );
		writeNamespace( PREFIX, URI );
		writeAttribute( XMLNS, SABIO );
		writeAttribute( XSI_SCHEMA_LOCATION, SCHEMA_LOCATION );
		writeAttribute( LABORATORY, person.getOrganisation() );
		writeAttribute( PROJECT, experimentSet.getExperimentStudy() );
		writeAttribute( SUBMITTER, username );
		writeAttribute( LOCAL_EXPERIMENT_ID, providerId + experimentSet.getId() );
		writeAttribute( COMMENTS, experimentSet.getExperimentProtocol().getDescription() );
		
		writeStartElement( REPORT_ENTRIES );
		writeExperiment( experimentSet, providerId );
		writeEndElement( REPORT_ENTRIES );
		
		writeEndElement( KINETICS_REPORT );
		writeEndDocument();

		close();
	}
	
	/**
	 *
	 * @param experimentSet
	 * @param localExperimentId
	 * @throws Exception
	 */
	private void writeExperiment( final KineticsExperimentSet experimentSet, final String localExperimentId ) throws Exception
	{
		final String REACTIONS_STUDIED = "reactionsstudied"; //$NON-NLS-1$
		final String EC_CLASS_ID = "ecclassid"; //$NON-NLS-1$
		final String ORGANISM = "organism"; //$NON-NLS-1$
		final String VIVO_VITRO = "vivo_vitro"; //$NON-NLS-1$
		final String IN_VITRO = "In Vitro"; //$NON-NLS-1$
		final String WILDTYPE = "wildtype"; //$NON-NLS-1$
		final int FIRST = 0;
		
		final Collection<SBMLDocument> documents = experimentSet.getDocuments();
		final OntologyTerm taxonomyTerm = SbmlUtils.getOntologyTerm( CollectionUtils.getFirst( documents ).getModel(), Ontology.TAXONOMY );
		
		writeAttribute( ORGANISM, taxonomyTerm.getName() );
		writeAttribute( org.mcisb.kinetics.PropertyNames.STRAIN, experimentSet.getString( org.mcisb.kinetics.PropertyNames.STRAIN ) );
		writeAttribute( VIVO_VITRO, IN_VITRO );
		
		outer: for( Iterator<SBMLDocument> iterator = documents.iterator(); iterator.hasNext(); )
		{
			final Model model = iterator.next().getModel();
			
			for( int l = 0; l < model.getNumReactions(); l++ )
			{
				final Reaction reaction = model.getReaction( l );
				final ModifierSpeciesReference modifierSpeciesReference = reaction.getModifier( FIRST );
				
				if( modifierSpeciesReference != null )
				{
					final Species modifier = model.getSpecies( modifierSpeciesReference.getSpecies() );
					final String buffer = KineticsUtils.getBuffer( experimentSet.getBuffer( model.getId() ) );
					
					writeStartElement( REACTIONS_STUDIED );
					
					final OntologyTerm ecTerm = SbmlUtils.getOntologyTerm( modifier, Ontology.EC );
					final OntologyTerm keggReactionTerm = SbmlUtils.getOntologyTerm( reaction, Ontology.KEGG_REACTION );
					
					if( ecTerm != null )
					{
						writeAttribute( EC_CLASS_ID, ecTerm.getId() );
					}
					
					final Map<String,Object[]> speciesNameToValues = new LinkedHashMap<>();
					final String speciesId = writeSpecies( model, speciesNameToValues );    			
					writeKineticLaw( experimentSet, model, reaction.getKineticLaw(), localExperimentId, model.getId(), buffer, speciesId, speciesNameToValues );
					
					if( keggReactionTerm != null )
					{
						writeComplementaryInfo( Arrays.asList( keggReactionTerm ) );
					}
					
					writeModifier( modifier, WILDTYPE, true, taxonomyTerm.getName() );
					writeEndElement( REACTIONS_STUDIED );
					continue outer;
				}
			}
		}
		
		writeComplementaryInfo( Arrays.asList( taxonomyTerm ) );
	}
	
	/**
	 * 
	 * @param reaction
	 * @param model
	 * @param speciesNameToValues
	 * @return String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private String writeSpecies( final Model model, final Map<String,Object[]> speciesNameToValues ) throws Exception
	{
		final String SUBSTRATE = "Substrate"; //$NON-NLS-1$
		final String PRODUCT = "Product"; //$NON-NLS-1$
		
		String speciesId = null;
		
		for( int r = 0; r < model.getNumReactions(); r++ )
		{
			final Reaction reaction = model.getReaction( r );
			
			for( int l = 0; l < reaction.getNumReactants(); l++ )
			{
				final SpeciesReference speciesReference = reaction.getReactant( l );
				final Species species = model.getSpecies( speciesReference.getSpecies() );
				final Object[] values = speciesNameToValues.get( species.getName() );
				Collection<Double> concentrations = null;
				
				if( values == null )
				{
					final String currentSpeciesId = writeSpecies( species, SUBSTRATE, speciesReference.getStoichiometry() );
					
					if( speciesId == null )
					{
						speciesId = currentSpeciesId;
					}
					
					concentrations = new TreeSet<>();
					speciesNameToValues.put( species.getName(), Arrays.asList( currentSpeciesId, species.getName(), concentrations, Integer.valueOf( species.isSetSBOTerm() ? species.getSBOTerm() : NumberUtils.UNDEFINED ) ).toArray() );
				}
				else
				{
					concentrations = (Collection<Double>)values[ 2 ];
				}
				
				if( species.isSetInitialConcentration() )
				{
					concentrations.add( Double.valueOf( species.getInitialConcentration() ) );
				}
				else
				{
					for( int m = 0; m < model.getNumReactions(); m++ )
					{
						final Reaction subReaction = model.getReaction( m );
						
						for( int n = 0; n < subReaction.getNumReactants(); n++ )
						{
							final Species reactant = model.getSpecies( subReaction.getReactant( n ).getSpecies() );
							
							if( reactant.getSBOTerm() == SboUtils.SUBSTRATE )
							{
								concentrations.add( Double.valueOf( reactant.getInitialConcentration() ) );
							}
						}
					}
				}
			}
			
			for( int l = 0; l < reaction.getNumProducts(); l++ )
			{
				final SpeciesReference speciesReference = reaction.getProduct( l );
				final Species species = model.getSpecies( speciesReference.getSpecies() );
				
				if( !speciesNameToValues.containsKey( species.getName() ) )
				{
					writeSpecies( species, PRODUCT, speciesReference.getStoichiometry() );
					speciesNameToValues.put( species.getName(), null );
				}
			}
			
			for( int l = 0; l < reaction.getNumModifiers(); l++ )
			{
				final double DEFAULT_STOICHIOMETRY = 1.0;
				final ModifierSpeciesReference speciesReference = reaction.getModifier( l );
				final Species species = model.getSpecies( speciesReference.getSpecies() );
				
				if( !speciesNameToValues.containsKey( species.getName() ) )
				{
					final String currentSpeciesId = writeSpecies( species, MODIFIER_CATALYST, DEFAULT_STOICHIOMETRY );
					speciesNameToValues.put( species.getName(), Arrays.asList( currentSpeciesId, "Et", Arrays.asList( Double.valueOf( species.getInitialConcentration() ) ), Integer.valueOf( species.isSetSBOTerm() ? species.getSBOTerm() : NumberUtils.UNDEFINED ) ).toArray() ); //$NON-NLS-1$
				}	
			}
		}
		
		return speciesId;
	}
	
	/**
	 * 
	 * @param species
	 * @param role
	 * @param stoichiometry
	 * @return String
	 * @throws Exception
	 */
	private String writeSpecies( final Species species, final String role, final double stoichiometry ) throws Exception
	{
		final String SPECIES = "species"; //$NON-NLS-1$
		final String NAME = "name"; //$NON-NLS-1$
		final String ENZYME = "Enzyme"; //$NON-NLS-1$
		final String ROLE = "role"; //$NON-NLS-1$
		final String STOICHIOMETRIC_VALUE = "stoichiometricvalue"; //$NON-NLS-1$
		final String SPECIES_ID = "speciesid"; //$NON-NLS-1$
		final String COMPOUND = "compound"; //$NON-NLS-1$
		final String COMPOUND_TYPE = "compoundtype"; //$NON-NLS-1$
		final String PROTEIN_PEPTIDE = "ProteinPeptide"; //$NON-NLS-1$
		final String SIMPLE_MOLECULE = "SimpleMolecule"; //$NON-NLS-1$
		final String LOCATION = "location"; //$NON-NLS-1$
		final String DESCRIPT = "descript"; //$NON-NLS-1$
		final String UNDEFINED = "UNDEFINED"; //$NON-NLS-1$
		final String INCHI_STRING = "inchi_string"; //$NON-NLS-1$
		
		final ChebiTerm chebiTerm = (ChebiTerm)SbmlUtils.getOntologyTerm( species, Ontology.CHEBI );
		final OntologyTerm keggTerm = SbmlUtils.getOntologyTerm( species, Ontology.KEGG_COMPOUND );
		final OntologyTerm inchiTerm = chebiTerm == null ? null : CollectionUtils.getFirst( OntologyUtils.getInstance().getXrefs( chebiTerm.getXrefs().keySet(), Ontology.INCHI ) );
		final OntologyTerm uniProtTerm = SbmlUtils.getOntologyTerm( species, Ontology.UNIPROT );
		
		final String speciesId = SPECIES + ++speciesCount;
		
		writeStartElement( SPECIES );
		writeAttribute( NAME, role.equals( MODIFIER_CATALYST ) ? ENZYME : species.getName() );
		writeAttribute( ROLE, role );
		writeAttribute( STOICHIOMETRIC_VALUE, Double.toString( stoichiometry ) );
		writeAttribute( SPECIES_ID, speciesId );

		writeStartElement( COMPOUND );
		writeAttribute( NAME, species.getName() );
		writeAttribute( COMPOUND_TYPE, role.equals( MODIFIER_CATALYST ) ? PROTEIN_PEPTIDE : SIMPLE_MOLECULE );
		writeAttribute( STOICHIOMETRIC_VALUE, Double.toString( stoichiometry ) );
		
		if( inchiTerm != null )
		{
			writeAttribute( INCHI_STRING, inchiTerm.getId() );
		}
		
		if( chebiTerm != null || keggTerm != null || uniProtTerm != null )
		{
			writeComplementaryInfo( Arrays.asList( chebiTerm, keggTerm, uniProtTerm ) );
		}
		
		writeEndElement( COMPOUND );
		
		writeStartElement( LOCATION );
		writeAttribute( DESCRIPT, UNDEFINED );
		writeEndElement( LOCATION );
		writeEndElement( SPECIES );
		
		return speciesId;
	}
	
	/**
	 *
	 * @param modifier
	 * @param wildtype
	 * @param recombinant
	 * @param expressedIn
	 * @throws Exception
	 */
	private void writeModifier( final Species modifier, final String wildtype, final boolean recombinant, final String expressedIn ) throws Exception
	{
		final String PROTEIN_IN_REACTION = "protein_in_reaction"; //$NON-NLS-1$
		final String NAME = "name"; //$NON-NLS-1$
		final String UNIPROTID = "uniprotid"; //$NON-NLS-1$
		final String WILDTYPE = "wildtype"; //$NON-NLS-1$
		final String RECOMBINANT = "recombinant"; //$NON-NLS-1$
		final String NATIVE = "native"; //$NON-NLS-1$
		final String EXPRESSED_IN = "expressed_in"; //$NON-NLS-1$
		final String UNKNOWN = "UNKNOWN"; //$NON-NLS-1$
		
		final OntologyTerm uniProtTerm = SbmlUtils.getOntologyTerm( modifier, Ontology.UNIPROT );
		writeStartElement( PROTEIN_IN_REACTION );
		writeAttribute( NAME, uniProtTerm.getName() == null ? ( CollectionUtils.getFirst( uniProtTerm.getSynonyms() ) == null ? UNKNOWN : CollectionUtils.getFirst( uniProtTerm.getSynonyms() ) ) : uniProtTerm.getName() );
		writeAttribute( UNIPROTID, uniProtTerm.getId() );
		writeAttribute( WILDTYPE, wildtype );
		writeAttribute( RECOMBINANT, recombinant ? RECOMBINANT : NATIVE );
		writeAttribute( EXPRESSED_IN, expressedIn );
		writeEndElement( PROTEIN_IN_REACTION );
	}
	
	/**
	 *
	 * @param comment
	 * @param ontologyTerms
	 * @throws Exception
	 */
	private void writeComplementaryInfo( final Collection<OntologyTerm> ontologyTerms ) throws Exception
	{
		final String COMPLEMENTARY_INFO = "complementaryinfo"; //$NON-NLS-1$
		final String ANNOTATION = "annotation"; //$NON-NLS-1$
		final String URI = "uri"; //$NON-NLS-1$
		final String ELEMID = "elemid"; //$NON-NLS-1$
		
		writeStartElement( COMPLEMENTARY_INFO );
		
		for( Iterator<OntologyTerm> iterator = ontologyTerms.iterator(); iterator.hasNext(); )
		{
			final OntologyTerm ontologyTerm = iterator.next();
			
			if( ontologyTerm != null )
			{
				writeStartElement( ANNOTATION );
				writeAttribute( URI, ontologyTerm.getOntology().getUrnIdentifier() );
				writeAttribute( ELEMID, ontologyTerm.getId() );
				writeEndElement( ANNOTATION );
			}
		}

		writeEndElement( COMPLEMENTARY_INFO );
	}
	
	/**
	 * 
	 * @param experimentSet 
	 * @param model 
	 * @param kineticLaw
	 * @param provider
	 * @param providerId
	 * @param buffer
	 * @param reaction
	 * @param speciesId
	 * @param speciesNameToValues
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void writeKineticLaw( final KineticsExperimentSet experimentSet, final Model model, final KineticLaw kineticLaw, final String provider, final String providerId, final String buffer, final String speciesId, final Map<String,Object[]> speciesNameToValues ) throws Exception
	{
		final String KINETIC_LAW = "kineticlaw"; //$NON-NLS-1$
		final String SBO_ID = "sboid"; //$NON-NLS-1$
		final String FORMULA = "formula"; //$NON-NLS-1$
		final String PROVIDER_ID = "provider_id"; //$NON-NLS-1$
		final String PROVIDER = "provider"; //$NON-NLS-1$
		
		writeStartElement( KINETIC_LAW );
		writeAttribute( SBO_ID, Integer.toString( kineticLaw.getSBOTerm() ) );
		writeAttribute( FORMULA, getFormula( JSBML.formulaToString( kineticLaw.getMath() ) ) );
		writeAttribute( PROVIDER, provider );
		writeAttribute( PROVIDER_ID, providerId );
		
		final int MICHAELIS_MENTEN_SBO = 28;
		final String KL_TYPE = "kltype"; //$NON-NLS-1$
		final String MICHAELIS_MENTEN = "Michaelis-Menten"; //$NON-NLS-1$
		
		if( kineticLaw.getSBOTerm() == MICHAELIS_MENTEN_SBO )
		{
			writeAttribute( KL_TYPE, MICHAELIS_MENTEN );
		}
		// end
		
		SboUtils.getInstance();
		final String math = ( (SboTerm)SboUtils.getOntologyTerm( kineticLaw.getSBOTerm() ) ).getMath();
		
		for( int l = 0; l < kineticLaw.getLocalParameterCount(); l++ )
		{
			final LocalParameter parameter = kineticLaw.getLocalParameter( l );
			SboUtils.getInstance();
			final OntologyTerm parameterTerm = SboUtils.getOntologyTerm( parameter.getSBOTerm() );
			SboUtils.getInstance();
			//TODO: check-out effect of parameter.getConstant()
			writeParameter( SboUtils.getShortName( math, parameter.getSBOTerm() ), parameter.getValue(), Double.parseDouble( experimentSet.getCondition( parameter, org.mcisb.util.PropertyNames.ERROR ) ), model.getUnitDefinition( parameter.getUnits() ).getName(), /* parameter.getConstant() */ true, parameterTerm.getName(), parameter.getSBOTerm(), parameter.getSBOTerm() == SboUtils.CATALYTIC_RATE_CONSTANT ? null : speciesId, false );
		}
		
		final int CONCENTRATION_SBO_TERM = 196;
		
		for( Iterator<Map.Entry<String,Object[]>> iterator = speciesNameToValues.entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<String,Object[]> entry = iterator.next();
			final Object[] values = entry.getValue();
			
			if( values != null )
			{
				final List<Double> concentrations = new ArrayList<>( (Collection<Double>)values[ 2 ] );
				double startValue;
				double error;
				boolean range = false;
				
				if( concentrations.size() == 1 )
				{
					startValue = CollectionUtils.getFirst( concentrations ).doubleValue();
					error = 0;
				}
				else
				{
					final double firstValue = CollectionUtils.getFirst( concentrations ).doubleValue();
					final double lastValue = concentrations.get( concentrations.size() - 1 ).doubleValue();
					error = ( lastValue - firstValue ) / 2;
					startValue = firstValue + error;
					range = true;
				}
				
				// TODO: is this right? SBO shows that S and Et correspond with diferent terms than CONCENTRATION_SBO_TERM
				// final int sboId = values[ 3 ].equals( Integer.valueOf( NumberUtils.UNDEFINED ) ) ? CONCENTRATION_SBO_TERM : ( (Integer)values[ 3 ] ).intValue();
				final int sboId = CONCENTRATION_SBO_TERM;
				SboUtils.getInstance();
				final SboTerm sboTerm = (SboTerm)SboUtils.getOntologyTerm( sboId );
				writeParameter( range ? "S" : (String)values[ 1 ], startValue, error, org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT, false, "concentration", sboTerm.getIntId(), (String)values[ 0 ], range ); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		final double pH = model.getParameter( org.mcisb.tracking.PropertyNames.PH ).getValue();
		final double temperature = model.getParameter( org.mcisb.tracking.PropertyNames.TEMPERATURE ).getValue();
		
		writeExperimentConditionDescription( buffer, Arrays.asList( new Value( org.mcisb.tracking.PropertyNames.PH, pH, Value.UNITLESS, false ), new Value( org.mcisb.tracking.PropertyNames.TEMPERATURE, temperature, org.mcisb.tracking.PropertyNames.CELSIUS, false ) ) );
		writeEndElement( KINETIC_LAW );
	}
	
	/**
	 *
	 * @param buffer
	 * @param values
	 * @throws XMLStreamException
	 */
	private void writeExperimentConditionDescription( final String buffer, final Collection<Value> values ) throws XMLStreamException
	{
		final String EXP_COND_DESCRIPTION = "expconddescription"; //$NON-NLS-1$
		final String EXP_COND_VALUE = "expcondvalue"; //$NON-NLS-1$
		final String CONDITION_TYPE = "condtype"; //$NON-NLS-1$
		
		writeStartElement( EXP_COND_DESCRIPTION );
		writeAttribute( org.mcisb.kinetics.PropertyNames.BUFFER, buffer );
		
		for( Iterator<Value> iterator = values.iterator(); iterator.hasNext(); )
		{
			final Value value = iterator.next();
    		writeStartElement( EXP_COND_VALUE );
    		writeValue( value, false );
    		writeAttribute( CONDITION_TYPE, value.getName() );
    		writeEndElement( EXP_COND_VALUE );
		}
		
		writeEndElement( EXP_COND_DESCRIPTION );
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 * @param error
	 * @param units
	 * @param constant
	 * @param parameterType
	 * @param sboTerm
	 * @param speciesId
	 * @param range
	 * @throws Exception
	 */
	private void writeParameter( final String name, final double value, final double error, final String units, final boolean constant, final String parameterType, final int sboTerm, final String speciesId, final boolean range ) throws Exception
	{
		final String KIN_PARAMETER = "kinparameter"; //$NON-NLS-1$
		final String SBO_ID = "sboid"; //$NON-NLS-1$
		final String PARAMETER_TYPE = "partype"; //$NON-NLS-1$
		final String NAME = "name"; //$NON-NLS-1$
		final String VARIABLE_OR_CONSTRANT = "var_or_const"; //$NON-NLS-1$
		final String CONSTRANT = "Constant"; //$NON-NLS-1$
		final String VARIABLE = "Variable"; //$NON-NLS-1$
		final String SPECIES = "species"; //$NON-NLS-1$
		
		writeStartElement( KIN_PARAMETER );
		writeValue( new Value( name, value, value - error, value + error, error, units, constant ), range );
		writeAttribute( PARAMETER_TYPE, parameterType );
		writeAttribute( NAME, name );
		writeAttribute( SBO_ID, Integer.toString( sboTerm ) );
		writeAttribute( VARIABLE_OR_CONSTRANT, constant ? CONSTRANT : VARIABLE );
		
		if( speciesId != null )
		{
			writeAttribute( SPECIES, speciesId );
		}
		
		writeEndElement( KIN_PARAMETER );
	}

	/**
	 * 
	 * @param value
	 * @param range
	 * @throws XMLStreamException
	 */
	private void writeValue( final Value value, final boolean range ) throws XMLStreamException
	{
		final String START_VALUE = "startval"; //$NON-NLS-1$
		final String END_VALUE = "endvalue"; //$NON-NLS-1$
		final String STANDARD_DEVIATION = "st_dev"; //$NON-NLS-1$
		final String UNIT = "unit"; //$NON-NLS-1$
		
		if( range )
		{
			writeAttribute( START_VALUE, Double.toString( MathUtils.getSignificantFigures( value.getValue() - value.getError() ) ) );
			writeAttribute( END_VALUE, Double.toString( MathUtils.getSignificantFigures( value.getValue() + value.getError() ) ) );
		}
		else
		{
			writeAttribute( START_VALUE, Double.toString( MathUtils.getSignificantFigures( value.getValue() ) ) );
			
			final double error = value.getError();
			
			if( error != 0.0 )
			{
				writeAttribute( STANDARD_DEVIATION, Double.toString( MathUtils.getSignificantFigures( error ) ) );
			}
		}
		
		if( !value.getUnit().equals( Value.UNITLESS ) )
		{
			writeAttribute( UNIT, value.getUnit() );
		}
	}
	
	/**
	 * 
	 * @param formula
	 * @return String
	 */
	private static String getFormula( final String formula )
	{
		final String LAMBDA = "lambda("; //$NON-NLS-1$
		final String SEPARATOR = ","; //$NON-NLS-1$
		String formattedFormula = formula;
		
		if( formattedFormula.startsWith( LAMBDA ) )
		{
			formattedFormula = formattedFormula.substring( LAMBDA.length() );
			formattedFormula = formattedFormula.substring( 0, formattedFormula.length() - 1 ); // Remove final bracket )
		}
		
		final StringTokenizer tokenizer = new StringTokenizer( formattedFormula, SEPARATOR );
		
		while( tokenizer.hasMoreTokens() )
		{
			formattedFormula = tokenizer.nextToken().trim();
		}
		
		return formattedFormula;
	}
}