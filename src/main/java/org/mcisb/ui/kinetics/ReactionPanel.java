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
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import org.mcisb.ui.util.*;
import org.mcisb.util.*;

/**
 * 
 * @author Neil Swainston
 */
public class ReactionPanel extends ParameterPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private static final double DEFAULT_TEMP = 37.0;
	
	/**
	 * 
	 */
	private static final double DEFAULT_PH = 7.0;
	
	/**
	 * 
	 */
	private static final double MINIMUM_TEMP = -273.15;
	
	/**
	 * 
	 */
	private static final double MAXIMUM_TEMP = 1000.0;
	
	/**
	 * 
	 */
	private static final double STEP_TEMP = 0.05;
	
	/**
	 * 
	 */
	private static final double MINIMUM_PH = 0.0;
	
	/**
	 * 
	 */
	private static final double MAXIMUM_PH = 14.0;
	
	/**
	 * 
	 */
	private static final double STEP_PH = 0.1;
	
	/**
	 * 
	 */
	private final JSpinner temperatureSpinner;
	
	/**
	 * 
	 */
	private final JSpinner pHSpinner;
	
	/**
	 * 
	 */
	private final transient Preferences preferences;
	
	/**
	 * 
	 *
	 * @param title
	 * @param preferences
	 * @param temperature
	 * @throws Exception
	 */
	public ReactionPanel( final String title, final Preferences preferences, final double temperature ) throws Exception
	{
		super( title );
		this.preferences = preferences;
		
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.messages" ); //$NON-NLS-1$

		temperatureSpinner = new JSpinner( new SpinnerNumberModel( DEFAULT_TEMP, MINIMUM_TEMP, MAXIMUM_TEMP, STEP_TEMP ) );
		pHSpinner = new JSpinner( new SpinnerNumberModel( DEFAULT_PH, MINIMUM_PH, MAXIMUM_PH, STEP_PH ) );
		
		add( new JLabel( resourceBundle.getString( "ReactionPanel.temperatureLabel" ) ), 0, 0, false, false ); //$NON-NLS-1$
		add( temperatureSpinner, 1, 0, true, false, GridBagConstraints.HORIZONTAL, GridBagConstraints.REMAINDER );
		add( new JLabel( resourceBundle.getString( "ReactionPanel.pHLabel" ) ), 0, 1, false, true ); //$NON-NLS-1$
		add( pHSpinner, 1, 1, true, true, GridBagConstraints.HORIZONTAL, GridBagConstraints.REMAINDER );
		
		setPreferences();
		
		if( temperature != NumberUtils.UNDEFINED )
		{
			temperatureSpinner.setValue( Double.valueOf( temperature ) );
			temperatureSpinner.setEnabled( false );
		}
		
		setValid( true );
	}
	
	/**
	 * 
	 * @param title
	 * @param preferences
	 * @throws Exception
	 */
	public ReactionPanel( final String title, final Preferences preferences ) throws Exception
	{
		this( title, preferences, NumberUtils.UNDEFINED );
	}
	
	/**
	 * 
	 *
	 * @return double
	 */
	public double getPH()
	{
		return ( (Double)pHSpinner.getValue() ).doubleValue();
	}
	
	/**
	 * 
	 *
	 * @return double
	 */
	public double getTemperature()
	{
		return ( (Double)temperatureSpinner.getValue() ).doubleValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public void dispose() throws Exception
	{
		preferences.put( org.mcisb.tracking.PropertyNames.PH, pHSpinner.getValue().toString() );
		preferences.put( org.mcisb.tracking.PropertyNames.TEMPERATURE, temperatureSpinner.getValue().toString() );
	}

	/**
	 * 
	 *
	 * @throws Exception
	 */
	private void setPreferences() throws Exception
	{
		pHSpinner.setValue( Double.valueOf( preferences.get( org.mcisb.tracking.PropertyNames.PH, Double.toString( DEFAULT_PH ) ) ) );
		temperatureSpinner.setValue( Double.valueOf( preferences.get( org.mcisb.tracking.PropertyNames.TEMPERATURE, Double.toString( DEFAULT_TEMP ) ) ) );
	}
}