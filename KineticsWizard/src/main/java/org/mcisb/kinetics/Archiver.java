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

/**
 * 
 * @author Neil Swainston
 */
public interface Archiver
{
	/**
	 * 
	 * @param experimentSet
	 * @param modelNameToInitialRates
	 * @throws Exception
	 */
	public void archive( final KineticsExperimentSet experimentSet, final Map<String,double[]> modelNameToInitialRates ) throws Exception;
}