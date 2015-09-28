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
import org.mcisb.kinetics.absorbance.*;
import org.mcisb.ontology.*;
import org.mcisb.ui.app.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;

/**
 * 
 * @author Neil Swainston
 */
public class AbsorbanceApp extends App
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
	private final Collection<OntologyTerm> organismTerms;
	
	/**
	 * 
	 */
	private final Archiver archiver;
	
	/**
	 * 
	 *
	 * @param frame
	 * @param experimentSet
	 * @param organismTerms
	 * @param archiver
	 */
	public AbsorbanceApp( final JFrame frame, final KineticsExperimentSet experimentSet, final Collection<OntologyTerm> organismTerms, final Archiver archiver )
	{
		super( frame, new GenericBean() );
		this.experimentSet = experimentSet;
		this.organismTerms = organismTerms;
		this.archiver = archiver;
		
		init( resourceBundle.getString( "AbsorbanceApp.title" ), resourceBundle.getString( "AbsorbanceApp.error" ), new ResourceFactory().getImageIcon( resourceBundle.getString( "AbsorbanceApp.icon" ) ).getImage() ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.ui.app.App#getWizard(org.mcisb.util.GenericBean)
	 */
	@Override
	protected Wizard getWizard() throws Exception
	{
		return new AbsorbanceWizard( window, bean, new AbsorbanceTask(), experimentSet, organismTerms, archiver );
	}
}