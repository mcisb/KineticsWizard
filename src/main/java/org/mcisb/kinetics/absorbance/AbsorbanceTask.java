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

import java.io.*;
import org.mcisb.kinetics.*;
import org.mcisb.util.task.*;

/**
 * 
 * @author Neil Swainston
 */
public class AbsorbanceTask extends AbstractGenericBeanTask
{
	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.util.remote.RemoteTaskImpl#doTask()
	 */
	@Override
	protected Serializable doTask() throws Exception
	{
		final KineticsExperimentSet experimentSet = (KineticsExperimentSet)bean.getProperty( org.mcisb.kinetics.PropertyNames.EXPERIMENT );
		experimentSet.setExperimentStudy( bean.getString( org.mcisb.kinetics.PropertyNames.PROJECT ) );
		experimentSet.setProperty( org.mcisb.kinetics.PropertyNames.STRAIN, bean.getString( org.mcisb.kinetics.PropertyNames.STRAIN ) );
		return new KineticsExperimentCalculator( experimentSet ).calculate();
	}
}