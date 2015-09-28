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

import java.beans.*;
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.util.data.*;
import org.mcisb.util.*;
import org.mcisb.util.data.*;

/**
 *
 * @author Neil Swainston
 */
public class KineticsTimecourseDataDisplayManagerPanel extends DataDisplayManagerPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private static final double MIN_RATE = 0.0;
	
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
	private final DataDisplayPanel dataDisplayPanel;
	
	/**
	 * 
	 */
	private int index = 0;
	
	/**
	 * @param rateManipulatable
	 * @param data
	 * @param kineticsCalculator
	 */
	public KineticsTimecourseDataDisplayManagerPanel( final boolean rateManipulatable, final List<Spectra> data, final KineticsCalculator kineticsCalculator )
	{
		super( new KineticsTimecourseDataDisplayPanel( rateManipulatable ), data, false, false );
		this.kineticsCalculator = kineticsCalculator;
		
		final int FIRST = 0;
		dataDisplayPanel = getDataDisplayPanel( FIRST );
		dataDisplayPanel.addPropertyChangeListener( this );
		
		final KineticsTimecourseDataDisplayPanel kineticsTimecourseDataDisplayPanel = (KineticsTimecourseDataDisplayPanel)dataDisplayPanel;
		kineticsTimecourseDataDisplayPanel.setNegative( kineticsCalculator.isNegative() );
		
		double maxY = 0.0;
		
		for( Iterator<Spectra> iterator = data.iterator(); iterator.hasNext(); )
		{
			for( Iterator<Spectrum> iterator2 = iterator.next().iterator(); iterator2.hasNext(); )
			{
				final Spectrum spectrum = iterator2.next();
				final double[] yValues = spectrum.getYValues();
				
				for( int i = 0; i < spectrum.getYValues().length; i++ )
				{
					maxY = Math.max( maxY, yValues[ i ] );
				}
			}
		}
		dataDisplayPanel.setMaxY( maxY );
		
		setInitialRate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange( final PropertyChangeEvent e )
	{
		super.propertyChange( e );
		
		final String propertyName = e.getPropertyName();
		
		if( propertyName.equals( ListManager.INDEX ) )
		{
			final Object data = e.getNewValue();
			
			if( data instanceof Integer && kineticsCalculator != null )
			{
				index = ( (Integer)data ).intValue();
				
				try
				{
					setInitialRate();
				}
				catch( Exception ex )
				{
					final JDialog dialog = new ExceptionComponentFactory().getExceptionDialog( getTopLevelAncestor(), resourceBundle.getString( "KineticsTimecourseDataDisplayManagerPanel.error" ), ex ); //$NON-NLS-1$
					ComponentUtils.setLocationCentral( dialog );
					dialog.setVisible( true );
				}
			}
		}
		else if( propertyName.equals( KineticsTimecourseDataDisplayPanel.INITIAL_RATE ) )
		{
			final Object data = e.getNewValue();
			
			if( data instanceof Double )
			{
				final double initialRate = ( (Double)data ).doubleValue();
				kineticsCalculator.setInitialRate( index, initialRate );
			}
		}
	}
	
	/**
	 * 
	 */
	private void setInitialRate()
	{
		( (KineticsTimecourseDataDisplayPanel)dataDisplayPanel ).setInitialRate( Math.max( MIN_RATE, kineticsCalculator.getInitialRates()[ index ] ) );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.data.DataDisplayManagerPanel#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		dataDisplayPanel.removePropertyChangeListener( this );
	}
}