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
import java.awt.event.*;
import java.beans.*;
import java.nio.charset.*;
import javax.swing.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.util.data.*;
import org.mcisb.util.*;
import org.mcisb.util.data.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsTimecourseDataDisplayPanel extends ContinuousDataDisplayPanel implements PropertyChangeListener, MouseListener, MouseMotionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public static final String INITIAL_RATE = "INITIAL_RATE"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final String ABSORBANCE = "Absorbance"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final String ABSORBANCE_UNITS = ""; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final String CONCENTRATION = "[P]"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final String CONCENTRATION_UNITS = "mM"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final int BOX_SIZE = 6;
	
	/**
	 * 
	 */
	private final boolean rateManipulatable;
	
	/**
	 * 
	 */
	private final JPopupMenu menu = new JPopupMenu();
	
	/**
	 * 
	 */
	private final Action showAbsorbanceAction = new ShowAbsorbanceAction( true );
	
	/**
	 * 
	 */
	private final Action showConcentrationAction = new ShowAbsorbanceAction( false );
	
	/**
	 * 
	 */
	private double absorbanceCoefficient = NumberUtils.UNDEFINED;
	
	/**
	 * 
	 */
	private double pathLength = NumberUtils.UNDEFINED;
	
	/**
	 * 
	 */
	private boolean inEnd = false;
	
	/**
	 * 
	 */
	private boolean showAbsorbance = true;
	
	/**
	 * 
	 */
	private boolean negative;
	
	/**
	 * 
	 */
	private double firstX;
	
	/**
	 * 
	 */
	private double firstY;
	
	/**
	 * 
	 */
	private double lastX;
	
	/**
	 * 
	 */
	private double lastY;
	
	/**
	 * 
	 */
	private double initialRate;
	
	/**
	 * 
	 * @param rateManipulatable
	 */
	public KineticsTimecourseDataDisplayPanel( final boolean rateManipulatable )
	{
		this.rateManipulatable = rateManipulatable;
		
		if( this.rateManipulatable )
		{
			addMouseListener( this );
			addMouseMotionListener( this );
		}
		
		addMouseListener( new JPopupMenuListener( menu ) );
		showAbsorbanceAction.addPropertyChangeListener( this );
		showConcentrationAction.addPropertyChangeListener( this );
		menu.add( showAbsorbanceAction );
		menu.add( showConcentrationAction );
		setShowAbsorbance( true );
		showConcentrationAction.setEnabled( absorbanceCoefficient != NumberUtils.UNDEFINED && pathLength != NumberUtils.UNDEFINED );
	}
	
	/**
	 * 
	 * @param initialRate
	 */
	public void setInitialRate( double initialRate )
	{
		this.initialRate = initialRate;
		update();
	}
	
	/**
	 * 
	 * @param absorbanceCoefficient
	 */
	public void setAbsorbanceCoefficient( final double absorbanceCoefficient )
	{
		this.absorbanceCoefficient = absorbanceCoefficient;
		showConcentrationAction.setEnabled( absorbanceCoefficient != NumberUtils.UNDEFINED && pathLength != NumberUtils.UNDEFINED );
		setShowAbsorbance( showAbsorbance );
	}
	
	/**
	 * 
	 * @param pathLength
	 */
	public void setPathLength( final double pathLength )
	{
		this.pathLength = pathLength;
		showConcentrationAction.setEnabled( absorbanceCoefficient != NumberUtils.UNDEFINED && pathLength != NumberUtils.UNDEFINED );
		setShowAbsorbance( showAbsorbance );
	}

	/**
	 * 
	 * @param showAbsorbance
	 */
	public void setShowAbsorbance( final boolean showAbsorbance )
	{
		if( showAbsorbance )
		{
			setScaleY( 1.0 );
			setYAxisLabel( ABSORBANCE );
			setYAxisUnits( ABSORBANCE_UNITS );
		}
		else if( absorbanceCoefficient != NumberUtils.UNDEFINED && pathLength != NumberUtils.UNDEFINED )
		{
			setScaleY( 1.0 / ( absorbanceCoefficient * pathLength ) ); // convert to [P]
			setYAxisLabel( CONCENTRATION );
			setYAxisUnits( CONCENTRATION_UNITS );
		}
		this.showAbsorbance = showAbsorbance;
	}
	
	/**
	 * 
	 * @param negative
	 */
	public void setNegative( final boolean negative )
	{
		this.negative = negative;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.data.ContinuousDataDisplayPanel#paintSpectrum(java.awt.Graphics2D, org.mcisb.util.data.Spectrum)
	 */
	@Override
	protected void paintSpectrum( final Graphics2D g, final Spectrum spectrum )
	{
		if( !spectrum.isBackground() )
		{
			final int FIRST = 0;
			firstX = spectrum.getXValues()[ FIRST ];
			firstY = spectrum.getYValues()[ FIRST ];
			update();
			paintRate( g );
		}
		
		super.paintSpectrum( g, spectrum );
	}

	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.data.DataDisplayPanel#getColor(double, double)
	 */
	@Override
	protected Color getColor( double x, double y )
	{
		return org.mcisb.ui.util.SampleConstants.getColor( spectra.get( spectrumIndex ).isBackground() ? org.mcisb.tracking.SampleConstants.BLANK : org.mcisb.tracking.SampleConstants.SAMPLE );
	}

	/*
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange( final PropertyChangeEvent e )
	{
		if( e.getPropertyName().equals( ShowAbsorbanceAction.SHOW_ABSORBANCE ) )
		{
			setShowAbsorbance( ( (Boolean)e.getNewValue() ).booleanValue() );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved( MouseEvent e )
	{
		inEnd = Math.abs( e.getX() - getXPosition( lastX ) ) < BOX_SIZE && Math.abs( e.getY() - getYPosition( lastY ) ) < BOX_SIZE;
		repaint();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased( MouseEvent e )
	{
		processDrag( e );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged( MouseEvent e )
	{
		processDrag( e );
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public void dispose()
	{
		if( rateManipulatable )
		{
			removeMouseListener( this );
			removeMouseMotionListener( this );
		}
		
		for( int l = getMouseListeners().length - 1; l >= 0; l-- )
		{
			removeMouseListener( getMouseListeners()[ l ] );
		}
		
		showAbsorbanceAction.removePropertyChangeListener( this );
		showConcentrationAction.removePropertyChangeListener( this );
	}
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked( MouseEvent e )
	{
		// No implementation.
	}

	/*
	 * 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered( MouseEvent e )
	{
		// No implementation.
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited( MouseEvent e )
	{
		// No implementation.
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed( MouseEvent e )
	{
		// No implementation.
	}

	/**
	 * 
	 * @param g
	 */
	protected void paintRate( Graphics2D g )
	{
		final Color RATE_COLOR = Color.RED;

		g.setColor( RATE_COLOR );
		g.drawLine( getXPosition( firstX ), getYPosition( firstY ), getXPosition( lastX ), getYPosition( lastY ) );
		
		if( inEnd )	
		{
			g.drawRect( getXPosition( lastX ) - ( BOX_SIZE / 2 ), getYPosition( lastY ) - ( BOX_SIZE / 2 ), BOX_SIZE, BOX_SIZE );
		}
		
		final String RATE_ANNOTATION = "v = " + StringUtils.getEngineeringNotation( initialRate ) + " " + PropertyNames.RATE_UNITS; //$NON-NLS-1$ //$NON-NLS-2$
		g.drawBytes( RATE_ANNOTATION.getBytes( Charset.defaultCharset() ), 0, RATE_ANNOTATION.length(), AXES_BORDER, AXES_BORDER - TEXT_BORDER );
	}

	/**
	 * 
	 * @param e
	 */
	private void processDrag( final MouseEvent e )
	{
		if( e.getButton() == MouseEvent.BUTTON1 && inEnd )
		{
			final double convertedXPosition = convertXPosition( e.getX() );
			
			if( convertedXPosition < firstX )
			{
				return;
			}
			
			final double oldInitialRate = initialRate;
			
			lastX = Math.max( firstX, convertedXPosition );
			lastY = ( negative ? Math.min( firstY, convertYPosition( e.getY() ) ) : Math.max( firstY, convertYPosition( e.getY() ) ) );
			initialRate = Math.abs( lastY - firstY ) / ( lastX - firstX );
			
			firePropertyChange( INITIAL_RATE, oldInitialRate, initialRate );
			
			update();
			repaint();
		}
	}
	
	/**
	 * 
	 */
	private void update()
	{
		final double MIN_Y = 0;
		final double convertedInitialRate = ( negative ? -1 : 1 ) * initialRate;
		
		lastY = negative ? MIN_Y : maxY;

		if( initialRate != 0.0 && ( lastY - firstY ) / convertedInitialRate < convertXPosition( getWidth() - AXES_BORDER ) )
		{
			lastX = ( lastY - firstY ) / convertedInitialRate;
		}
		else
		{
			lastX = convertXPosition( getWidth() - AXES_BORDER );
			lastY = ( lastX * convertedInitialRate ) + firstY;
		}
		
		// repaint();
	}
}