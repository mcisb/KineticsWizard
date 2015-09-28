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
package org.mcisb.ui.kinetics.data;

import java.awt.event.*;
import javax.swing.*;

/**
 * 
 * @author Neil Swainston
 */
public class ShowAbsorbanceAction extends AbstractAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public static final String SHOW_ABSORBANCE = "SHOW_ABSORBANCE"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final boolean showAbsorbance;

	/**
	 * 
	 * @param showAbsorbance
	 */
	public ShowAbsorbanceAction( final boolean showAbsorbance )
	{
		super( ( showAbsorbance ) ? "Show absorbance" : "Show concentration" ); //$NON-NLS-1$ //$NON-NLS-2$
		this.showAbsorbance = showAbsorbance;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( ActionEvent e )
	{
		this.firePropertyChange( SHOW_ABSORBANCE, Boolean.valueOf( !showAbsorbance ), Boolean.valueOf( showAbsorbance ) );
	}
}