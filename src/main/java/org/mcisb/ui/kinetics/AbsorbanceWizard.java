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

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.ontology.*;
import org.mcisb.ui.ontology.kegg.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.mcisb.util.task.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class AbsorbanceWizard extends Wizard
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public static final String NEW_EXPERIMENT_SET = "NEW_EXPERIMENT_SET"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final double DEFAULT_VALUE = 1.0;
	
	/**
	 * 
	 */
	private static final double MIN_VALUE = 0;
	
	/**
	 * 
	 */
	private static final double MAX_VALUE = 10000;
	
	/**
	 * 
	 */
	private static final double STEP = 0.1;
	
	/**
	 * 
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.messages" ); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final transient Preferences preferences = Preferences.userNodeForPackage( getClass() );
	
	/**
	 * 
	 */
	private final transient Archiver archiver;
	
	/**
	 * 
	 */
	private final Collection<OntologyTerm> organismTerms;
	
	/**
	 * 
	 */
	private final SampleSelectionPanel sampleSelectionPanel;
	
	/**
	 * 
	 */
	private final transient SampleSelectionWizardComponent sampleSelectionWizardComponent;
	
	/**
	 * 
	 */
	private transient KineticsExperimentSet experimentSet;
	
	/**
	 *
	 * @param parent 
	 * @param bean
	 * @param task
	 * @param experimentSet
	 * @param organismTerms
	 * @param archiver
	 */
	public AbsorbanceWizard( final Component parent, final GenericBean bean, final GenericBeanTask task, final KineticsExperimentSet experimentSet, final Collection<OntologyTerm> organismTerms, final Archiver archiver )
	{
		super( bean, task, true );
		this.archiver = archiver;
		this.organismTerms = organismTerms;

		final Map<Object,Object> metaDataPropertyNameToKey = new HashMap<>();
		final Map<Object,Object> metaDataOptions = new LinkedHashMap<>();
		String prompt = resourceBundle.getString( "AbsorbanceWizard.projectPrompt" ); //$NON-NLS-1$
		metaDataPropertyNameToKey.put( org.mcisb.kinetics.PropertyNames.PROJECT, prompt ); 
		metaDataOptions.put( prompt, null );
		prompt = resourceBundle.getString( "AbsorbanceWizard.strainPrompt" ); //$NON-NLS-1$
		metaDataPropertyNameToKey.put( org.mcisb.kinetics.PropertyNames.STRAIN, prompt ); 
		metaDataOptions.put( prompt, null );
		
		sampleSelectionPanel = new SampleSelectionPanel( resourceBundle.getString( "AbsorbanceWizard.sampleSelectionTitle" ) ); //$NON-NLS-1$
		sampleSelectionWizardComponent = new SampleSelectionWizardComponent( bean, sampleSelectionPanel );
		addPropertyChangeListener( sampleSelectionPanel );
		addPropertyChangeListener( sampleSelectionWizardComponent );
		
		if( experimentSet != null )
		{
			setExperimentSet( experimentSet );
		}
		else
		{
			final String EXCEL_EXTENSION = "xls"; //$NON-NLS-1$
			final Collection<String> fileExtensions = new TreeSet<>();
			fileExtensions.add( EXCEL_EXTENSION );
			final JFileChooser fileChooser = new JFileChooser();
			addWizardComponent( new FileChooserWizardComponent( bean, new FileChooserPanel( parent, resourceBundle.getString( "AbsorbanceWizard.inputDataTitle" ), ParameterPanel.DEFAULT_COLUMNS, fileChooser, false, true, false, JFileChooser.FILES_ONLY, fileExtensions ), org.mcisb.util.PropertyNames.IMPORT_FILEPATHS ) ); //$NON-NLS-1$
		}
		
		addWizardComponent( new DefaultWizardComponent( bean, new DefaultParameterPanel( resourceBundle.getString( "AbsorbanceWizard.metaDataTitle" ), metaDataOptions, preferences ), metaDataPropertyNameToKey ) ); //$NON-NLS-1$
		addWizardComponent( sampleSelectionWizardComponent );
		
		init();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.Wizard#forward()
	 */
	@Override
	protected void forward()
	{
		if( currentWizardComponent != CONFIRMATION_PANEL )
		{
			final WizardComponent wizardComponent = wizardComponents.get( currentWizardComponent );
		
			try
			{
				if( wizardComponent instanceof FileChooserWizardComponent )
	    		{
					final FileChooserWizardComponent fileChooserWizardComponent = (FileChooserWizardComponent)wizardComponent;
					final FileChooserPanel fileChooserPanel = (FileChooserPanel)fileChooserWizardComponent.getComponent();
					final File file = CollectionUtils.getFirst( fileChooserPanel.getFiles() );
					
					final KineticsExperimentSet newExperimentSet = new KineticsExperimentSet( StringUtils.getUniqueId(), org.mcisb.kinetics.absorbance.PropertyNames.ABSORBANCE_EXPERIMENT_TYPE_ID );
					final KineticsExcelReader kineticsExcelReader = new DefaultKineticsExcelReader( file, newExperimentSet );
					kineticsExcelReader.parse();
					setExperimentSet( newExperimentSet );
	    		}
				else if( wizardComponent instanceof SampleSelectionWizardComponent )
	    		{
    				wizardComponent.update();
    				
        			for( Iterator<SBMLDocument> iterator = experimentSet.getDocuments().iterator(); iterator.hasNext(); )
        			{
        				final Model model = iterator.next().getModel();
        				final String modelName = model.getName();
        				
        				final Map<Object,Object> parameterPropertyNameToKey = new HashMap<>();
        				final Map<Object,Object> parameterOptions = new LinkedHashMap<>();
        				
        				String prompt = resourceBundle.getString( "AbsorbanceWizard.enzymeConcentrationPrompt" ); //$NON-NLS-1$
        				parameterPropertyNameToKey.put( org.mcisb.kinetics.PropertyNames.ENZYME_CONCENTRATION, prompt ); 
        				parameterOptions.put( prompt, new SpinnerNumberModel( DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, STEP ) );
        				
        				prompt = resourceBundle.getString( "AbsorbanceWizard.absorptionCoefficientPrompt" ); //$NON-NLS-1$
        				parameterPropertyNameToKey.put( org.mcisb.kinetics.absorbance.PropertyNames.ABSORPTION_COEFFICIENT, prompt ); 
        				parameterOptions.put( prompt, new SpinnerNumberModel( DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, STEP ) );
        				
        				prompt = resourceBundle.getString( "AbsorbanceWizard.pathLengthPrompt" ); //$NON-NLS-1$
        				parameterPropertyNameToKey.put( org.mcisb.kinetics.absorbance.PropertyNames.PATH_LENGTH, prompt ); 
        				parameterOptions.put( prompt, new SpinnerNumberModel( DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, STEP ) );
        				
        				final Float temperature = (Float)experimentSet.getExperimentProtocol().getProperty( org.mcisb.tracking.PropertyNames.TEMPERATURE );
        				
        				addWizardComponent( new KeggWizardComponent( bean, new KeggPanel( resourceBundle.getString( "AbsorbanceWizard.keggTitle" ) + modelName, organismTerms, preferences ), experimentSet, model ) ); //$NON-NLS-1$
        				addWizardComponent( new ReactionWizardComponent( bean, new ReactionPanel( resourceBundle.getString( "AbsorbanceWizard.reactionTitle" ) + modelName, preferences, temperature == null ? NumberUtils.UNDEFINED : temperature.floatValue() ), model ) ); //$NON-NLS-1$
        				addWizardComponent( new ReagentsTableWizardComponent( bean, new ReagentsTableParameterPanel( resourceBundle.getString( "AbsorbanceWizard.reagentsTitle" ) + modelName ), experimentSet, model ) ); //$NON-NLS-1$
        				addWizardComponent( new AbsorbanceSampleWizardComponent( bean, new DefaultParameterPanel( resourceBundle.getString( "AbsorbanceWizard.parametersTitle" ) + modelName, parameterOptions, preferences ), parameterPropertyNameToKey, experimentSet, model ) ); //$NON-NLS-1$
        				addWizardComponent( new CommentWizardComponent( bean, new InformationPanel( resourceBundle.getString( "AbsorbanceWizard.freeTextTitle" ) + modelName, true ), model ) ); //$NON-NLS-1$
        			}
    			}
			}
			catch( Exception e )
			{
				showException( e );
			}
		}
		
		super.forward();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.Wizard#dispose()
	 */
	@Override
	protected void dispose()
	{
		super.dispose();
		removePropertyChangeListener( sampleSelectionPanel );
		removePropertyChangeListener( sampleSelectionWizardComponent );
	}
	
	/**
	 * @param experimentSet
	 */
	private void setExperimentSet( KineticsExperimentSet experimentSet )
	{
		firePropertyChange( NEW_EXPERIMENT_SET, this.experimentSet, experimentSet );
		this.experimentSet = experimentSet;
		bean.setProperty( org.mcisb.kinetics.PropertyNames.EXPERIMENT, experimentSet );
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.Wizard#getResultsComponent()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Component getResultsComponent() throws Exception
	{
		return new KineticsResultsPanel( experimentSet, (Map<String,KineticsCalculator>)returnValue, archiver );
	}
}