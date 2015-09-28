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

import java.io.*;
import org.mcisb.excel.*;

/**
 * 
 * @author Neil Swainston
 */
public abstract class KineticsExcelReader extends ExcelReader
{
	/**
	 *
	 * @param excelFile
	 * @throws Exception
	 */
	public KineticsExcelReader( final File excelFile ) throws Exception
	{
		super( excelFile );
	}
	
	/**
	 *
	 * @throws Exception
	 */
	public void parse() throws Exception
	{
		getMetaData();
		getData();
	}
	
	/**
	 *
	 * @throws Exception
	 */
	protected abstract void getMetaData() throws Exception;
	
	/**
	 * 
	 * @throws Exception 
	 */
	protected abstract void getData() throws Exception;
}