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

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.kinetics.absorbance.*;
import org.mcisb.ui.util.*;
import org.mcisb.util.*;
import org.mcisb.util.data.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsDataDisplayPanelApplet extends JApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private KineticsDataDisplayComponent dataDisplayComponent;
	
	/**
	 * 
	 * @throws HeadlessException
	 */
	public KineticsDataDisplayPanelApplet() throws HeadlessException
	{
		// No implementation.
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init()
	{
		super.init();
		
		try
		{
			final String SEPARATOR = ","; //$NON-NLS-1$
			final double absorbanceCoefficient = Double.parseDouble( getParameter( "absorbanceCoefficient" ) ); //$NON-NLS-1$
			final double pathLength = Double.parseDouble( getParameter( "pathLength" ) ); //$NON-NLS-1$
			final AbsorbanceUtils absorbanceUtils = new AbsorbanceUtils( absorbanceCoefficient, pathLength );
		
			final StringBuffer labels = new StringBuffer();
			final double[] substrateConcentrations = CollectionUtils.toDoubleArray( getParameter( "substrateConcentrations" ) ); //$NON-NLS-1$
			
			for( int s = 0; s < substrateConcentrations.length; s++ )
			{
				labels.append( substrateConcentrations[ s ] + SEPARATOR );
			}
			
			java.util.List<Spectrum> spectra = Spectrum.getSpectra( Boolean.parseBoolean( getParameter( "bigEndian" ) ), Boolean.parseBoolean( getParameter( "doublePrecision" ) ), labels.toString(), getParameter( "background" ), getParameter( "encodedXValues" ), getParameter( "encodedYValues" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			double[] timepoints = null;
			
			for( Iterator<Spectrum> iterator = spectra.iterator(); iterator.hasNext(); )
			{
				final Spectrum spectrum = iterator.next();
				timepoints = spectrum.getXValues();
				absorbanceUtils.addData( spectrum.getYValues(), spectrum.isBackground(), Double.parseDouble( spectrum.getLabel() ) );
			}
			
			final KineticsCalculator kineticsCalculator = new KineticsCalculator( timepoints, absorbanceUtils.getSubstrateConcentrations(), absorbanceUtils.getProductConcentrations(), Double.parseDouble( getParameter( "enzymeConcentration" ) ), CollectionUtils.toDoubleArray( getParameter( "initialRates" ) ), false ); //$NON-NLS-1$ //$NON-NLS-2$
			
			dataDisplayComponent = new KineticsDataDisplayComponent( Boolean.parseBoolean( getParameter( "rateManipulatable" ) ), kineticsCalculator, absorbanceCoefficient, pathLength ); //$NON-NLS-1$
    		
			setContentPane( dataDisplayComponent );
    		setFocusable( true );
    		requestFocus();
		}
		catch( Exception e )
		{
			add( new ExceptionComponentFactory().getExceptionPanel( "Error", e ), BorderLayout.CENTER ); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.applet.Applet#destroy()
	 */
	@Override
	public void destroy()
	{
		super.destroy();
		
		try
		{
			if( dataDisplayComponent != null )
			{
				dataDisplayComponent.dispose();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}