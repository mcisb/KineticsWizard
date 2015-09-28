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
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.tracking.*;
import org.mcisb.ui.util.table.action.*;
import org.sbml.jsbml.*;

/**
 *
 * @author Neil Swainston
 */
public class SetAction extends TableSelectionAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private final transient SampleManager sampleManager;
	
	/**
	 * 
	 */
	private final int type;
	
	/**
	 *
	 * @param sampleManager
	 * @param table
	 * @param title
	 * @param type
	 */
	public SetAction( final SampleManager sampleManager, final JTable table, final String title, final int type )
	{
		super( table, title );
		this.sampleManager = sampleManager;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.table.action.TableSelectionAction#performAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void performAction( final ActionEvent e )
	{
		for( Iterator<Object> iterator = getSelection().iterator(); iterator.hasNext(); )
		{
			final Object object = iterator.next();
			
			if( object instanceof Spot )
			{
				final SpotReading spotReading = ( (Spot)object ).getUserValue();
				final Object spotReadingUserObject = spotReading.getUserObject();
    				
				if( spotReadingUserObject instanceof Species )
				{
					sampleManager.setType( ( (Species)spotReadingUserObject ), type );
				}
			}
		}
	}
}