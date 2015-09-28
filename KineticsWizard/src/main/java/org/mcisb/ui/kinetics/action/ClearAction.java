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
package org.mcisb.ui.kinetics.action;

import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.util.*;

/**
 *
 * @author Neil Swainston
 */
public class ClearAction extends AbstractAction implements PropertyChangeListener, Disposable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.action.messages" ); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final transient GroupManager groupManager;
	
	/**
	 * 
	 */
	private final transient SampleManager sampleManager;
	
	/**
	 * 
	 */
	private boolean typesSet = false;
	
	/**
	 *
	 * @param groupManager
	 * @param sampleManager
	 */
	public ClearAction( final GroupManager groupManager, final SampleManager sampleManager )
	{
		super( resourceBundle.getString( "ClearAction.title" ) ); //$NON-NLS-1$
		this.groupManager = groupManager;
		this.sampleManager = sampleManager;
		groupManager.addPropertyChangeListener( this );
		sampleManager.addPropertyChangeListener( this );
		update();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		if( evt.getPropertyName().endsWith( SampleManager.TYPE ) )
		{
			typesSet = true;
		}
		
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		groupManager.clear();
		sampleManager.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public void dispose()
	{
		groupManager.removePropertyChangeListener( this );
		sampleManager.removePropertyChangeListener( this );
	}
	
	/**
	 * 
	 */
	private void update()
	{
		setEnabled( typesSet || groupManager.getGroups().size() > 0 );
	}
}