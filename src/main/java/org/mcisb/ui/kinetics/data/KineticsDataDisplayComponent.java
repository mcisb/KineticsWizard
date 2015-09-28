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
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.ui.util.data.*;
import org.mcisb.util.*;
import org.mcisb.util.data.*;

/**
 * @author Neil Swainston
 *
 */
public class KineticsDataDisplayComponent extends JSplitPane implements Disposable
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
	 */
	private final FitPlotDisplayPanel michaelisMentenPanel;
	
	/**
	 * 
	 * @param rateManipulatable
	 * @param kineticsCalculator
	 * @throws Exception
	 */
	public KineticsDataDisplayComponent( final boolean rateManipulatable, final KineticsCalculator kineticsCalculator ) throws Exception
	{
		this( rateManipulatable, kineticsCalculator, NumberUtils.UNDEFINED, NumberUtils.UNDEFINED );
	}
	
	/**
	 * 
	 * @param rateManipulatable
	 * @param kineticsCalculator
	 * @param absorbanceCoefficient
	 * @param pathLength
	 * @throws Exception
	 */
	public KineticsDataDisplayComponent( final boolean rateManipulatable, final KineticsCalculator kineticsCalculator, final double absorbanceCoefficient, final double pathLength ) throws Exception
	{
		super( JSplitPane.HORIZONTAL_SPLIT );
		
		this.kineticsCalculator = kineticsCalculator;
		
		final String DATA_X_AXIS_LABEL = "Time / s"; //$NON-NLS-1$
		final String MICHAELIS_MENTEN_TITLE = resourceBundle.getString( "FitPlotDisplayPanel.label" ); //$NON-NLS-1$
		
		final java.util.List<Spectra> spectra = new ArrayList<>();
		final double[][] productConcentrations = kineticsCalculator.getProductConcentrations();
		final double[] substrateConcentrations = kineticsCalculator.getSubstrateConcentrations();
		final double[] timepoints = kineticsCalculator.getTimepoints();
		
		for( int i = 0; i < productConcentrations.length; i++ )
		{
			final Spectra currentSpectra = new Spectra();
			currentSpectra.add( new Spectrum( "[S]=" + Double.toString( substrateConcentrations[ i ] ) + org.mcisb.kinetics.PropertyNames.CONCENTRATION_UNIT, timepoints, productConcentrations[ i ] ) ); //$NON-NLS-1$
			spectra.add( currentSpectra );
		}
		
		final int FIRST = 0;
		final KineticsTimecourseDataDisplayManagerPanel dataDisplayManagerPanel = new KineticsTimecourseDataDisplayManagerPanel( rateManipulatable, spectra, kineticsCalculator );
		final KineticsTimecourseDataDisplayPanel kineticsDataDisplayPanel = (KineticsTimecourseDataDisplayPanel)dataDisplayManagerPanel.getDataDisplayPanel( FIRST );
		kineticsDataDisplayPanel.setXAxisLabel( DATA_X_AXIS_LABEL );
		kineticsDataDisplayPanel.reset();
		kineticsDataDisplayPanel.setXRange( timepoints[ 0 ], timepoints[ timepoints.length - 1 ] );
		
		if( absorbanceCoefficient != NumberUtils.UNDEFINED && pathLength != NumberUtils.UNDEFINED )
		{
			kineticsDataDisplayPanel.setAbsorbanceCoefficient( absorbanceCoefficient );
			kineticsDataDisplayPanel.setPathLength( pathLength );
		}
		
		michaelisMentenPanel = new FitPlotDisplayPanel( kineticsCalculator ); 
		kineticsCalculator.addPropertyChangeListener( michaelisMentenPanel );
		
		final JPanel panel = new JPanel( new BorderLayout() );
		panel.setBackground( Color.WHITE );
		panel.add( new JLabel( MICHAELIS_MENTEN_TITLE, SwingConstants.CENTER ), BorderLayout.NORTH );
		panel.add( michaelisMentenPanel, BorderLayout.CENTER );
		
		final double HALF = 0.5;
		setTopComponent( dataDisplayManagerPanel );
		setBottomComponent( panel );
		setResizeWeight( HALF );
		setDividerLocation( HALF );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public void dispose()
	{
		kineticsCalculator.removePropertyChangeListener( michaelisMentenPanel );
	}
}