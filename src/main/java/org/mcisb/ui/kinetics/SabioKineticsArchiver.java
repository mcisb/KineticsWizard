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

import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.kinetics.memo.*;
import org.mcisb.ui.app.*;

/**
 * 
 * @author Neil Swainston
 */
public class SabioKineticsArchiver implements Archiver
{
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.kinetics.Archiver#archive(org.mcisb.kinetics.KineticsExperimentSet, java.util.Map)
	 */
	@Override
	public void archive( final KineticsExperimentSet experimentSet, final Map<String,double[]> modelNameToInitialRates ) throws Exception
	{
		final App app = new KineticsExperimentWriterApp( new JFrame(), experimentSet, modelNameToInitialRates, new SabioKineticsExperimentWriterTask() );
		app.show();
	}
}