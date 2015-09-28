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
package org.mcisb.kinetics.memo;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.mcisb.db.sql.*;
import org.mcisb.kinetics.*;
import org.mcisb.util.*;
import org.mcisb.util.task.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsExperimentWriterTask extends AbstractGenericBeanTask
{
	/**
	 * 
	 */
	protected KineticsExperimentWriter kineticsExperimentWriter = null;
	
	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.util.remote.RemoteTaskImpl#doTask()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Serializable doTask() throws Exception
	{
		final String dbServer = (String)CollectionUtils.getFirst( (Object[])bean.getProperty( org.mcisb.db.PropertyNames.DB_SERVER_NAME ) );
		final String dbCollectionName = (String)CollectionUtils.getFirst( (Object[])bean.getProperty( org.mcisb.db.PropertyNames.DB_COLLECTION_NAME ) );
		
		try( final Connection connection = ConnectionManager.getConnection( (String)CollectionUtils.getFirst( (Object[])bean.getProperty( org.mcisb.db.PropertyNames.DB_DRIVER ) ), dbServer, dbCollectionName, (String)CollectionUtils.getFirst( (Object[])bean.getProperty( org.mcisb.db.PropertyNames.USERNAME ) ), (String)CollectionUtils.getFirst( (Object[])bean.getProperty( org.mcisb.db.PropertyNames.PASSWORD ) ) ) )
		{
			final KineticsExperimentSet experimentSet = (KineticsExperimentSet)bean.getProperty( org.mcisb.kinetics.PropertyNames.EXPERIMENT );
			final Map<String,double[]> modelNameToInitialRates = (Map<String,double[]>)bean.getProperty( org.mcisb.kinetics.PropertyNames.INITIAL_RATES );
			kineticsExperimentWriter = new KineticsExperimentWriter( connection );
			kineticsExperimentWriter.writeExperiment( experimentSet, modelNameToInitialRates );
			return null;
		}
	}
}