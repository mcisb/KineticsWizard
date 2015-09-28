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
package org.mcisb.kinetics.absorbance;

import java.util.*;
import org.mcisb.util.*;

/**
 * 
 * @author Neil Swainston
 */
public class AbsorbanceUtils
{
	/**
	 * 
	 */
	private final Map<Double,Collection<double[]>> concentrationToSampleReadings = new TreeMap<>();
	
	/**
	 * 
	 */
	private final Map<Double,Collection<double[]>> concentrationToBackgroundReadings = new TreeMap<>();
	
	/**
	 * 
	 */
	private final List<Double> substrateConcentrations = new ArrayList<>();
	
	/**
	 * 
	 */
	private final List<double[]> productConcentrations = new ArrayList<>();
	
	/**
	 * 
	 */
	private final double absorptionCoefficient;
	
	/**
	 * 
	 */
	private final double pathLength;
	
	/**
	 * 
	 */
	private boolean calculated = false;
	
	/**
	 * 
	 * @param absorptionCoefficient
	 * @param pathLength
	 */
	public AbsorbanceUtils( final double absorptionCoefficient, final double pathLength )
	{
		this.absorptionCoefficient = absorptionCoefficient;
		this.pathLength = pathLength;
	}
	
	/**
	 * 
	 * @param data
	 * @param isBackground
	 * @param concentration
	 */
	public synchronized void addData( final double[] data, boolean isBackground, final double concentration )
	{
		final Map<Double,Collection<double[]>> concentrationToReadings = ( isBackground ) ? concentrationToBackgroundReadings : concentrationToSampleReadings;
		
		Collection<double[]> readings = concentrationToReadings.get( Double.valueOf( concentration ) );
		
		if( readings == null )
		{
			readings = new ArrayList<>();
			concentrationToReadings.put( Double.valueOf( concentration ), readings );
		}
		
		readings.add( data );
		calculated = false;
	}
	
	/**
	 * @return the substrateConcentrations
	 */
	public synchronized double[] getSubstrateConcentrations()
	{
		if( !calculated )
		{
			calculate();
		}
		
		return CollectionUtils.toDoubleArray( substrateConcentrations );
	}

	/**
	 * @return the productConcentrations
	 */
	public synchronized double[][] getProductConcentrations()
	{
		if( !calculated )
		{
			calculate();
		}
		
		return convertAbsorbanceToConcentration( CollectionUtils.to2dDoubleArray( productConcentrations ) );
	}

	/**
	 * 
	 */
	private synchronized void calculate()
	{
		final Map<Double,Collection<double[]>> concentrationToSubtractedReadings = substractBackground( concentrationToSampleReadings, concentrationToBackgroundReadings );

		for( Iterator<Map.Entry<Double,Collection<double[]>>> iterator = concentrationToSubtractedReadings.entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<Double,Collection<double[]>> entry = iterator.next();
			final Collection<double[]> values = entry.getValue();
			
			for( Iterator<double[]> iterator2 = values.iterator(); iterator2.hasNext(); )
			{
				substrateConcentrations.add( entry.getKey() );
				productConcentrations.add( iterator2.next() );
			}
		}
		
		calculated = true;
	}

	/**
	 *
	 * @param absorbance
	 * @return double
	 */
	private double convertAbsorbanceToConcentration( double absorbance )
	{
		return absorbance / ( absorptionCoefficient * pathLength );
	}
	
	/**
	 * 
	 * @param absorbances
	 * @return double[]
	 */
	private double[] convertAbsorbanceToConcentration( double[] absorbances )
	{
		double[] concentrations = new double[ absorbances.length ];
		
		for( int i = 0; i < absorbances.length; i++ )
		{
			concentrations[ i ] = convertAbsorbanceToConcentration( absorbances[ i ] );
		}
		
		return concentrations;
	}
	
	/**
	 * 
	 * @param absorbances
	 * @return double[][]
	 */
	private double[][] convertAbsorbanceToConcentration( double[][] absorbances )
	{
		double[][] concentrations = new double[ absorbances.length ][];
		
		for( int i = 0; i < absorbances.length; i++ )
		{
			concentrations[ i ] = convertAbsorbanceToConcentration( absorbances[ i ] );
		}
		
		return concentrations;
	}
	
	/**
	 * 
	 * @param concentrationToSampleReadings
	 * @param concentrationToBackgroundReadings
	 * @return Map
	 */
	private static Map<Double,Collection<double[]>> substractBackground( final Map<Double,Collection<double[]>> concentrationToSampleReadings, final Map<Double,Collection<double[]>> concentrationToBackgroundReadings )
	{
		final double ZERO = 0.0;
		final Map<Double,Collection<double[]>> concentrationToSubtractedReadings = new TreeMap<>();
		
		for( Iterator<Map.Entry<Double,Collection<double[]>>> iterator = concentrationToSampleReadings.entrySet().iterator(); iterator.hasNext(); )
		{
			final Map.Entry<Double,Collection<double[]>> entry = iterator.next();
			final Double concentration = entry.getKey();
			final Collection<double[]> sampleReadings = entry.getValue();
			final Collection<double[]> backgroundReadings = concentrationToBackgroundReadings.get( concentration );
			final Collection<double[]> subtractedReadings = new ArrayList<>();
			
			for( Iterator<double[]> iterator2 = sampleReadings.iterator(); iterator2.hasNext(); )
			{
				final double[] sampleReading = iterator2.next();
				final double[] subtratedReading = new double[ sampleReading.length ];
				System.arraycopy( sampleReading, 0, subtratedReading, 0, sampleReading.length );
				
				// Remove background if present:
				if( backgroundReadings != null )
				{
					final double[] meanBackgroundReading = getMeanBackgroundReading( backgroundReadings );
					
					for( int j = 0; j < subtratedReading.length; j++ )
					{
						subtratedReading[ j ] = Math.max( ZERO, subtratedReading[ j ] - meanBackgroundReading[ j ] );
					}
				}
				
				subtractedReadings.add( subtratedReading );
			}
			
			concentrationToSubtractedReadings.put( concentration, subtractedReadings );
		}
		
		return concentrationToSubtractedReadings;
	}
	
	/**
	 *
	 * @param backgroundReadings
	 * @return double[]
	 */
	private static double[] getMeanBackgroundReading( final Collection<double[]> backgroundReadings )
	{
		final int numBackgroundReadings = backgroundReadings.size();
		double[] meanBackgroundReading = null;
		
		for( Iterator<double[]> iterator = backgroundReadings.iterator(); iterator.hasNext(); )
		{
			final double[] backgroundReading = iterator.next();
			
			if( meanBackgroundReading == null )
			{
				meanBackgroundReading = backgroundReading;
			}
			else
			{
				for( int i = 0; i < backgroundReading.length; i++ )
				{
					meanBackgroundReading[ i ] += backgroundReading[ i ];
				}
			}
		}
		
		if( meanBackgroundReading != null )
		{
    		for( int i = 0; i < meanBackgroundReading.length; i++ )
    		{
    			meanBackgroundReading[ i ] /= numBackgroundReadings;
    		}
		}
		
		return meanBackgroundReading;
	}
}