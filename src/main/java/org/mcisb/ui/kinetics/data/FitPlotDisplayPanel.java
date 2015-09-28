/*******************************************************************************
 * Manchester Centre for Integrative Systems Biology
 * University of Manchester
 * Manchester M1 7ND
 * United Kingdom
 * 
 * Copyright (C) 2008 University of Manchester
 * 
 * This program is released under the Academic Free License ("AFL") v3.0.
 * (http://www.opensource.org/licenses/academic.php)
 *******************************************************************************/
package org.mcisb.ui.kinetics.data;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.ontology.sbo.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.util.data.*;
import org.mcisb.util.data.*;

/**
 * 
 * @author Neil Swainston
 */
public class FitPlotDisplayPanel extends ContinuousDataDisplayPanel implements PropertyChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private final static ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.data.messages" ); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final KineticsCalculator kineticsCalculator;
	
	/**
	 * 
	 * @param kineticsCalculator
	 * @throws Exception
	 */
	public FitPlotDisplayPanel( final KineticsCalculator kineticsCalculator ) throws Exception
	{
		final String X_AXIS_LABEL = "[S] / " + org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT; //$NON-NLS-1$
		final String Y_AXIS_LABEL = "v"; //$NON-NLS-1$
		final double DEFAULT_MIN_X = 0.0;
		
		this.kineticsCalculator = kineticsCalculator;
		
		final double[] substrateConcentrations = kineticsCalculator.getSubstrateConcentrations();
		
		setBackground( Color.WHITE );
		setXRange( DEFAULT_MIN_X, substrateConcentrations[ substrateConcentrations.length - 1 ] );
		setMaxY();
		setXAxisLabel( X_AXIS_LABEL );
		setYAxisLabel( Y_AXIS_LABEL );
		setYAxisUnits( PropertyNames.RATE_UNITS );
		
		update();
	}

	/* 
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		if( evt.getPropertyName().equals( KineticsCalculator.UPDATE ) )
		{
			try
			{
				update();
			}
			catch( Exception e )
			{
				final JDialog dialog = new ExceptionComponentFactory().getExceptionDialog( getTopLevelAncestor(), resourceBundle.getString( "FitPlotDisplayPanel.error" ), e ); //$NON-NLS-1$
				ComponentUtils.setLocationCentral( dialog );
				dialog.setVisible( true );
			}
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.data.DataDisplayPanel#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent( Graphics g0 )
	{
		super.paintComponent( g0 );
		
		try
		{
			final int SIZE = 6;
			final double[] initialRates = kineticsCalculator.getInitialRates();
			final double[] substrateConcentrations = kineticsCalculator.getSubstrateConcentrations();
			
			for( int i = 0; i < substrateConcentrations.length; i++ )
			{
				g0.drawOval( getXPosition( substrateConcentrations[ i ] ) - ( SIZE / 2 ), getYPosition( initialRates[ i ] ) - ( SIZE / 2 ), SIZE, SIZE );
			}
		}
		catch( Exception e )
		{
			final JDialog dialog = new ExceptionComponentFactory().getExceptionDialog( getTopLevelAncestor(), resourceBundle.getString( "FitPlotDisplayPanel.error" ), e ); //$NON-NLS-1$
			ComponentUtils.setLocationCentral( dialog );
			dialog.setVisible( true );
		}
	}

	/**
	 * 
	 * @throws Exception 
	 */
	private void update() throws Exception
	{
		final int DATA_POINTS = 1000;
		final java.util.List<Spectrum> newSpectra = new ArrayList<>();
		
		final double[] xValues = new double[ DATA_POINTS ];
		final double[] yValues = new double[ DATA_POINTS ];
		final double[] substrateConcentrations = kineticsCalculator.getSubstrateConcentrations();
		final double increment = ( substrateConcentrations[ substrateConcentrations.length - 1 ] - minX ) / DATA_POINTS;
		
		for( int i = 0; i < xValues.length; i++ )
		{
			if( i == 0 )
			{
				xValues[ i ] = minX;
			}
			else
			{
				xValues[ i ] = xValues[ i - 1 ] + increment;
			}
			
			final double hillCoefficient = kineticsCalculator.getValue( SboUtils.HILL_COEFFICIENT );
			yValues[ i ] = kineticsCalculator.getValue( SboUtils.CATALYTIC_RATE_CONSTANT ) * kineticsCalculator.getEnzymeConcentration() * Math.pow( xValues[ i ], hillCoefficient ) / ( Math.pow( kineticsCalculator.getValue( SboUtils.MICHAELIS_CONSTANT ), hillCoefficient ) + Math.pow( xValues[ i ], hillCoefficient ) );
		}
		
		final Spectrum spectrum = new Spectrum( resourceBundle.getString( "FitPlotDisplayPanel.label" ), xValues, yValues ); //$NON-NLS-1$
		newSpectra.add( spectrum );
		setSpectra( newSpectra );
		
		setMaxY();
	}
	
	/**
	 * 
	 */
	private void setMaxY()
	{
		final double[] initialRates = kineticsCalculator.getInitialRates();
		Arrays.sort( initialRates );
		setMaxY( initialRates[ initialRates.length - 1 ] );
	}
}