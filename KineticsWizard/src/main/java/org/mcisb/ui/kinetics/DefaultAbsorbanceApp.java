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
import org.mcisb.ontology.*;
import org.mcisb.ontology.kegg.*;

/**
 * 
 * @author Neil Swainston
 */
public class DefaultAbsorbanceApp
{
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */ 
	public static void main( String[] args ) throws Exception
	{
		final OntologySource keggGenomeUtils = KeggGenomeUtils.getInstance();
		final Collection<OntologyTerm> organismTerms = new ArrayList<>();
		
		for( int i = 0; i < args.length; i++ )
		{
			organismTerms.add( keggGenomeUtils.getOntologyTerm( args[ i ] ) );
		}
		
		new AbsorbanceApp( new JFrame(), null, organismTerms, null ).show();
	}
}