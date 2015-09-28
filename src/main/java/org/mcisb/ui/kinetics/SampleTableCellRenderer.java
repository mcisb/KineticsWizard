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
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.mcisb.kinetics.*;
import org.mcisb.tracking.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 *
 * @author Neil Swainston
 */
public class SampleTableCellRenderer extends DefaultTableCellRenderer implements PropertyChangeListener, Disposable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private transient final GroupManager groupManager;
	
	/**
	 * 
	 */
	private transient final SampleManager sampleManager;
	
	/**
	 * 
	 */
	private final Map<Color,Object> colourToGroup = new HashMap<>();
	
	/**
	 * 
	 * @param groupManager
	 * @param sampleManager
	 */
	public SampleTableCellRenderer( final GroupManager groupManager, final SampleManager sampleManager )
	{
		this.groupManager = groupManager;
		this.sampleManager = sampleManager;
		
		groupManager.addPropertyChangeListener( this );
		
		colourToGroup.put( new Color( 255, 224, 255 ), null );
		colourToGroup.put( new Color( 224, 255, 255 ), null );
		colourToGroup.put( new Color( 255, 255, 224 ), null );
		colourToGroup.put( new Color( 255, 224, 224 ), null );
		colourToGroup.put( new Color( 224, 255, 224 ), null );
		colourToGroup.put( new Color( 224, 224, 255 ), null );
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
	{
		JLabel label = null;
		Color foreground = Color.GRAY;
		Species species = null;
		
		if( value != null )
		{
    		if( value instanceof Spot )
    		{
    			final SpotReading spotReading = ( (Spot)value ).getUserValue();
    			final Object spotReadingUserObject = spotReading.getUserObject();
    				
				if( spotReadingUserObject instanceof Species )
				{
					species = (Species)spotReadingUserObject;
				}
    		}
		}
		
		if( !isSelected )
		{
			final Object group = groupManager.getGroup( value );
			
    		if( group != null )
    		{
    			for( Iterator<Map.Entry<Color,Object>> iterator = colourToGroup.entrySet().iterator(); iterator.hasNext(); )
    			{
    				final Map.Entry<Color,Object> entry = iterator.next();
    				
    				if( group.equals( entry.getValue() ) )
    				{
    					label = new JLabel();
    					label.setOpaque( true );
    					label.setBackground( entry.getKey() );
    					break;
    				}
    			}
    		}
    		
    		if( species != null )
    		{
				if( label == null )
				{
					label = new JLabel();
					label.setOpaque( false );
				}

				foreground = org.mcisb.ui.util.SampleConstants.getColor( sampleManager.getType( species ) );
    		}
		}
		
		label = ( label == null ) ? (JLabel)super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column ) : label;
		
		if( species != null )
		{
			label.setText( Double.toString( species.getInitialConcentration() ) );
		}
		
		label.setFont( label.getFont().deriveFont( Font.PLAIN ) );
		label.setHorizontalAlignment( SwingConstants.TRAILING );
		label.setForeground( foreground );
		
		return label;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		final Object oldValue = evt.getOldValue();
		final Object newValue = evt.getNewValue();
		
		if( oldValue == null )
		{
			for( Iterator<Map.Entry<Color,Object>> iterator = colourToGroup.entrySet().iterator(); iterator.hasNext(); )
			{
				final Map.Entry<Color,Object> entry = iterator.next();
				
				if( entry.getValue() == null )
				{
					colourToGroup.put( entry.getKey(), newValue );
					break;
				}
			}
		}
		else
		{
			for( Iterator<Map.Entry<Color,Object>> iterator = colourToGroup.entrySet().iterator(); iterator.hasNext(); )
			{
				final Map.Entry<Color,Object> entry = iterator.next();
				
				if( oldValue.equals( entry.getValue() ) )
				{
					colourToGroup.put( entry.getKey(), null );
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public void dispose()
	{
		groupManager.removePropertyChangeListener( this );
	}
}