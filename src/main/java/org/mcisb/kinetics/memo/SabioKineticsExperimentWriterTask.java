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
import java.net.*;
import java.util.*;
import org.mcisb.kinetics.*;
import org.mcisb.kinetics.sabio.*;
import org.mcisb.util.xml.*;

/**
 * 
 * @author Neil Swainston
 */
public class SabioKineticsExperimentWriterTask extends KineticsExperimentWriterTask
{
	
	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.util.remote.RemoteTaskImpl#doTask()
	 */
	@Override
	protected Serializable doTask() throws Exception
	{
		super.doTask();
		
		SabioXmlWriter sabioXmlWriter = null;
		
		final URL schema = new URL( System.getProperty( "org.mcisb.kinetics.SabioSchemaUrl" ) ); //$NON-NLS-1$
		final String username = System.getProperty( "org.mcisb.kinetics.SabioUsername" ); //$NON-NLS-1$
		final File tempFile = File.createTempFile( "sabio", ".xml" ); //$NON-NLS-1$ //$NON-NLS-2$
		
		try( final OutputStream os = new FileOutputStream( tempFile ) )
		{
			final KineticsExperimentSet experimentSet = (KineticsExperimentSet)bean.getProperty( org.mcisb.kinetics.PropertyNames.EXPERIMENT );

			sabioXmlWriter = new SabioXmlWriter( os );
			sabioXmlWriter.write( experimentSet, schema, username );
			
			try( InputStream xmlIn = new FileInputStream( tempFile ) )
			{
				XmlUtils.validateXml( xmlIn );
			}
			
			final SabioSubmitter submitter = new SabioSubmitter( username, System.getProperty( "org.mcisb.kinetics.SabioPassword" ) ); //$NON-NLS-1$
			submitter.submit( Arrays.asList( new File[] { tempFile } ) );
		}
		finally
		{
			if( sabioXmlWriter != null )
			{
				sabioXmlWriter.close();
			}
			if( kineticsExperimentWriter != null )
			{
				kineticsExperimentWriter.close();
			}
		}

		return null;
	}
}