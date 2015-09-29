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
package org.mcisb.kinetics.sabio;

import java.io.*;
import java.nio.charset.*;
import java.rmi.*;
import java.util.*;
// import javax.xml.ws.*;
// import org.h_its.sabiork.*;
import org.mcisb.util.io.*;

/**
 * 
 *
 * @author Neil Swainston
 * @deprecated
 */
@Deprecated
public class SabioSubmitter
{
	/**
	 * 
	 */
	// private final XmlFileListSubmissionService port = new XmlFileListSubmissionService_Service().getXmlFileListSubmissionServiceWsdlPort();
	
	/**
	 * 
	 * @param username
	 * @param password
	 */
	@SuppressWarnings("unused")
	public SabioSubmitter( final String username, final String password )
	{
		/*
		( (BindingProvider)port ).getRequestContext().put( BindingProvider.USERNAME_PROPERTY, username );
		( (BindingProvider)port ).getRequestContext().put( BindingProvider.PASSWORD_PROPERTY, password );
		*/
	}
	/**
	 * 
	 * @param files
	 * @return Object
	 * @throws IOException
	 * @throws RemoteException
	 */
	@SuppressWarnings("static-method")
	public Object submit( List<File> files ) throws IOException, RemoteException
	{
		// final String SUCCESS = "finish"; //$NON-NLS-1$
        final String[][] fileContent = new String[ files.size() ][ 2 ];
        
		for( int i = 0; i < files.size(); i++ )
		{
			final File file = files.get( i );
			fileContent[ i ][ 0 ] = new String( FileUtils.read( file.toURI().toURL() ), Charset.defaultCharset() );
			fileContent[ i ][ 1 ] = file.getName();
		}

		/*
		final List<net.java.dev.jaxb.array.StringArray> stringArrays = new LinkedList<>();

		for( final String[] lines : fileContent )
		{
			final net.java.dev.jaxb.array.StringArray stringArray = new net.java.dev.jaxb.array.StringArray();

			for( final String line : lines )
			{
				stringArray.getItem().add( line );
			}

			stringArrays.add( stringArray );
		}
		
		final Object response = port.getXMLFile( stringArrays );
		
		if( !response.equals( SUCCESS ) )
		{
			throw new IOException( response.toString() );
		}
		
		return response;
		*/
		
		return null;
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws RemoteException 
	 */
	public static void main( final String[] args ) throws RemoteException, IOException
	{
		final File sabioML = new File( args[ 2 ] );
		final List<File> sabioMLfiles = ( sabioML.isDirectory() ) ? Arrays.asList( sabioML.listFiles() ) : Arrays.asList( sabioML );
		System.out.println( new SabioSubmitter( args[ 0 ], args[ 1 ] ).submit( sabioMLfiles ) );
	}
}