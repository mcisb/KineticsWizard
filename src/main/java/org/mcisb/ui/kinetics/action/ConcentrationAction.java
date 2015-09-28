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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.mcisb.tracking.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.util.table.action.*;
import org.mcisb.ui.wizard.parameter.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 *
 * @author Neil Swainston
 */
public class ConcentrationAction extends TableSelectionAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private static final double DEFAULT_VALUE = 1;
	
	/**
	 * 
	 */
	private static final double MIN_VALUE = 0;
	
	/**
	 * 
	 */
	private static final double MAX_VALUE = 1000;
	
	/**
	 * 
	 */
	private static final double STEP = 1;
	
	/**
	 * 
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.action.messages" ); //$NON-NLS-1$
	
	/**
	 *
	 * @param table
	 */
	public ConcentrationAction( final JTable table )
	{
		super( table, resourceBundle.getString( "ConcentrationAction.title" ) ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.table.action.TableSelectionAction#performAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void performAction( final ActionEvent e )
	{
		final String CONCENTRATION = "CONCENTRATION"; //$NON-NLS-1$
		final GenericBean bean = new GenericBean();
		final Map<Object,Object> propertyNameToKey = new HashMap<>();
		final Map<Object,Object> options = new LinkedHashMap<>();
		final String prompt = resourceBundle.getString( "ConcentrationAction.concentrationPrompt" ); //$NON-NLS-1$
		propertyNameToKey.put( CONCENTRATION, prompt ); 
		options.put( prompt, new SpinnerNumberModel( DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, STEP ) );
		final DefaultParameterPanel component = new DefaultParameterPanel( resourceBundle.getString( "ConcentrationAction.dialogTitle" ), options ); //$NON-NLS-1$
		
		try
		{
			final Container topLevelAncestor = table.getTopLevelAncestor();
    		final JDialog dialog = new JDialog( ( topLevelAncestor instanceof Frame ) ? (Frame)topLevelAncestor : null, true );
        	new DefaultParameterApp( dialog, resourceBundle.getString( "ConcentrationAction.dialogTitle" ), bean, component, propertyNameToKey ).show(); //$NON-NLS-1$
        	
        	final double concentration = bean.getDouble( CONCENTRATION );
        	
        	if( concentration != NumberUtils.UNDEFINED )
        	{
        		for( int i = 0; i < table.getRowCount(); i++ )
        		{
        			for( int j = 0; j < table.getColumnCount(); j++ )
        			{
        				if( table.isCellSelected( i, j ) )
        				{
        					final Object value = table.getValueAt( i, j );
        					
        					if( value instanceof Spot )
        		    		{
        		    			final SpotReading spotReading = ( (Spot)value ).getUserValue();
        		    			final Object spotReadingUserObject = spotReading.getUserObject();
        		    				
    		    				if( spotReadingUserObject instanceof Species )
    		    				{
    		    					( (Species)spotReadingUserObject ).setInitialConcentration( concentration );
    		    				}
        		    		}
        				}
        			}
        		}
        	}
		}
		catch( Exception ex )
		{
			final JDialog dialog = new ExceptionComponentFactory( true ).getExceptionDialog( null, resourceBundle.getString( "ConcentrationAction.error" ), ex ); //$NON-NLS-1$
			ComponentUtils.setLocationCentral( dialog );
			dialog.setVisible( true );
		}
	}
}