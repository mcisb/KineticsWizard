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

import org.mcisb.ui.util.*;
import org.mcisb.ui.wizard.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class CommentWizardComponent extends WizardComponent
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
	public CommentWizardComponent( final GenericBean bean, final InformationPanel component, final Model model ) 
	{
		super( bean, component );
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.wizard.DefaultWizardComponent#update()
	 */
	@Override
	public void update() throws Exception
	{
		final String NOTES_PREFIX = "<body xmlns=\"http://www.w3.org/1999/xhtml\"><pre xmlns=\"http://www.w3.org/1999/xhtml\">"; //$NON-NLS-1$
		final String NOTES_SUFFIX = "</pre></body>"; //$NON-NLS-1$
		model.setNotes( NOTES_PREFIX + ( (InformationPanel )getComponent() ).getText() + NOTES_SUFFIX );
	}
}