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
import java.util.prefs.*;
import org.mcisb.kinetics.*;
import org.mcisb.ui.db.sql.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.mcisb.util.task.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsExperimentWriterWizard extends Wizard
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param bean
	 * @param task
	 * @param experimentSet
	 * @param modelNameToInitialRates
	 */
	public KineticsExperimentWriterWizard( final GenericBean bean, final GenericBeanTask task, final KineticsExperimentSet experimentSet, final Map<String,double[]> modelNameToInitialRates )
	{
		super( bean, task, false );
		
		bean.setProperty( org.mcisb.kinetics.PropertyNames.EXPERIMENT, experimentSet );
		bean.setProperty( org.mcisb.kinetics.PropertyNames.INITIAL_RATES, modelNameToInitialRates );
		
		final String title = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.messages" ).getString( "KineticsExperimentWriterWizard.title" ); //$NON-NLS-1$ //$NON-NLS-2$
		addWizardComponent( DatabaseWizardComponent.getInstance( bean, title, Preferences.userNodeForPackage( getClass() ) ) );
		
		init();
	}
}
