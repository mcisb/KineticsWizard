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
import javax.swing.*;
import org.mcisb.tracking.*;
import org.mcisb.ui.util.table.*;
import org.sbml.jsbml.*;

/**
 *
 * @author Neil Swainston
 */
public class SampleSelectionTableCellEditor extends NumberCellEditor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private Object currentValue;

	/**
	 *
	 * @param max
	 */
	public SampleSelectionTableCellEditor( double max )
	{
		super( 0.0, max );
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.table.NumberCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
	{
		this.currentValue = value;
		
		if( value instanceof Spot )
		{
			final SpotReading spotReading = ( (Spot)value ).getUserValue();
			
			final Object spotReadingUserObject = spotReading.getUserObject();
    			
			if( spotReadingUserObject instanceof Species )
			{
				final Component component = super.getTableCellEditorComponent( table, Double.valueOf( ( (Species)spotReadingUserObject ).getInitialConcentration() ), isSelected, row, column );
				
				if( component instanceof JTextField )
				{
					final JTextField textField = ( (JTextField)component );
					textField.setSelectionStart( 0 );
					
				    if( textField.getText() != null )
				    { 
				    	textField.setSelectionEnd( textField.getText().length() ); 
				    } 
				}
				
				return component;
			}
		}
		
		return super.getTableCellEditorComponent( table, value, isSelected, row, column );
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue()
	{
		final Object cellEditorValue = super.getCellEditorValue();
		
		if( cellEditorValue instanceof Number && currentValue instanceof Spot )
		{
			final SpotReading spotReading = ( (Spot)currentValue ).getUserValue();
			final Object spotReadingUserObject = spotReading.getUserObject();
    			
			if( spotReadingUserObject instanceof Species )
			{
				( (Species)spotReadingUserObject ).setInitialConcentration( ( (Number)cellEditorValue ).doubleValue() );
				return currentValue;
			}
		}
			
		return cellEditorValue;
	}
}