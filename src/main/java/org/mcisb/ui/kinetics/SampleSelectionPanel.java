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
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.ui.kinetics.action.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.util.table.*;
import org.mcisb.ui.util.table.action.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class SampleSelectionPanel extends ParameterPanel implements PropertyChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private final JPopupMenu menu = new JPopupMenu();
	
	/**
	 * 
	 */
	private final transient GroupManager groupManager = new GroupManager();
	
	/**
	 * 
	 */
	private final transient SampleManager sampleManager;
	
	/**
	 * 
	 */
	private final JTable table;
	
	/**
	 * 
	 */
	private boolean grouped = false;
	
	/**
	 * 
	 */
	private int samplesTypeSet = 0;
	
	/**
	 *
	 * @param title
	 */
	public SampleSelectionPanel( final String title )
	{
		super( title );
		table = new PlateTable();
		
		final double MAX_CONCENTRATION = Double.MAX_VALUE;
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.messages" ); //$NON-NLS-1$
		sampleManager = new SampleManager();
		sampleManager.addPropertyChangeListener( this );
		groupManager.addPropertyChangeListener( this );
		
		table.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		table.setCellSelectionEnabled( true );
		table.addMouseListener( new JPopupMenuListener( menu ) );
		table.setDefaultRenderer( Object.class, new SampleTableCellRenderer( groupManager, sampleManager ) );
		table.setDefaultEditor( Object.class, new SampleSelectionTableCellEditor( MAX_CONCENTRATION ) );
		
		menu.add( new ClearAction( groupManager, sampleManager ) );
		menu.add( new GroupAction( table, groupManager ) );
		menu.add( new ConcentrationAction( table ) );
		menu.add( new SetAction( sampleManager, table, resourceBundle.getString( "SampleSelectionPanel.sampleTitle" ), org.mcisb.tracking.SampleConstants.SAMPLE ) ); //$NON-NLS-1$
		menu.add( new SetAction( sampleManager, table, resourceBundle.getString( "SampleSelectionPanel.blankTitle" ), org.mcisb.tracking.SampleConstants.BLANK ) ); //$NON-NLS-1$
		
		final JScrollPane scrollPane =  new JScrollPane( table );
		scrollPane.getViewport().setBackground( Color.WHITE );
		fill( scrollPane );
	}
	
	/**
	 *
	 * @return Collection
	 */
	public Map<Object,Collection<Object>> getGroups()
	{
		return groupManager.getGroups();
	}
	
	/**
	 * 
	 * @param species
	 * @return boolean
	 */
	public boolean isSample( final Species species )
	{
		return sampleManager.getType( species ) == org.mcisb.tracking.SampleConstants.SAMPLE;
	}
	
	/**
	 * 
	 * @param species
	 * @return boolean
	 */
	public boolean isBlank( final Species species )
	{
		return sampleManager.getType( species ) == org.mcisb.tracking.SampleConstants.BLANK;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		repaint();
		
		if( evt.getPropertyName().equals( GroupManager.UPDATED ) )
		{
			grouped = evt.getNewValue() != null;
		}
		else if( evt.getPropertyName().equals( SampleManager.TYPE ) )
		{
			if( evt.getNewValue() != null && ( ( (Integer)evt.getNewValue() ).intValue() == org.mcisb.tracking.SampleConstants.SAMPLE ) )
			{
				samplesTypeSet++;
			}
			else if( evt.getOldValue() != null && ( ( (Integer)evt.getOldValue() ).intValue() == org.mcisb.tracking.SampleConstants.SAMPLE ) )
			{
				samplesTypeSet--;
			}
		}
		else if( evt.getPropertyName().equals( AbsorbanceWizard.NEW_EXPERIMENT_SET ) )
		{
			final KineticsExperimentSet experimentSet = (KineticsExperimentSet)evt.getNewValue();
			table.setModel( new PlateTableModel( experimentSet.getPlate() ) );
			table.validate();
		}
		
		setValid( grouped && samplesTypeSet > 0 );
	}

	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public void dispose()
	{
		sampleManager.removePropertyChangeListener( this );
		groupManager.removePropertyChangeListener( this );
		
		final MouseListener[] mouseListeners = table.getMouseListeners();
		
		for( int i = 0; i < mouseListeners.length; i++ )
		{
			table.removeMouseListener( mouseListeners[ i ] );
		}
	}
}