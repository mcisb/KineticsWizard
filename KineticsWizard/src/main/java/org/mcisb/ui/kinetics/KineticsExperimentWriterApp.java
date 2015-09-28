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
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.ui.app.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.mcisb.util.task.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsExperimentWriterApp extends App
{
	/**
	 * 
	 */
	private final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.messages" ); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final KineticsExperimentSet experimentSet;
	
	/**
	 * 
	 */
	private final Map<String,double[]> modelNameToInitialRates;
	
	/**
	 * 
	 */
	private final GenericBeanTask task;
	
	/**
	 * 
	 * @param frame
	 * @param experimentSet
	 * @param modelNameToInitialRates
	 * @param task
	 */
	public KineticsExperimentWriterApp( final JFrame frame, final KineticsExperimentSet experimentSet, final Map<String,double[]> modelNameToInitialRates, final GenericBeanTask task )
	{
		super( frame, new GenericBean() );
		this.experimentSet = experimentSet;
		this.modelNameToInitialRates = modelNameToInitialRates;
		this.task = task;
		init( resourceBundle.getString( "ExperimentWriterApp.title" ), resourceBundle.getString( "ExperimentWriterApp.error" ), new ResourceFactory().getImageIcon( resourceBundle.getString( "ExperimentWriterApp.icon" ) ).getImage() ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.ui.app.App#getWizard(org.mcisb.util.GenericBean)
	 */
	@Override
	protected Wizard getWizard() throws Exception
	{
		return new KineticsExperimentWriterWizard( bean, task, experimentSet, modelNameToInitialRates );
	}
}