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
package org.mcisb.kinetics;

import java.util.*;
import org.mcisb.tracking.*;
import org.mcisb.util.*;
import org.sbml.jsbml.*;

/**
 *
 * @author Neil Swainston
 */
public class SampleManager extends PropertyChangeSupported
{
	/**
	 * 
	 */
	public static final String TYPE = "TYPE"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final Map<Species,Integer> sampleToType = new HashMap<>();
	
	/**
	 * 
	 *
	 */
	public SampleManager()
	{
		resetTypes();
	}
	
	/**
	 * 
	 */
	public void clear()
	{
		resetTypes();
	}
	
	/**
	 *
	 * @param sample
	 * @param type
	 */
	public void setType( final Species sample, final int type )
	{
		support.firePropertyChange( TYPE, getType( sample ), type );
		sampleToType.put( sample, Integer.valueOf( type ) );
	}
	
	/**
	 *
	 * @param sample
	 * @return int
	 */
	public int getType( final Species sample )
	{
		final Object type = sampleToType.get( sample );
		return type == null ? SampleConstants.UNDEFINED : ( (Integer)type ).intValue();
	}
	
	/**
	 * 
	 */
	private void resetTypes()
	{
		for( Iterator<Species> iterator = sampleToType.keySet().iterator(); iterator.hasNext(); )
		{
			setType( iterator.next(), SampleConstants.UNDEFINED );
		}
	}
}