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

import java.io.*;
import java.util.*;
import javax.swing.*;
import org.mcisb.kinetics.*;
import org.mcisb.kinetics.novostar.*;
import org.mcisb.ontology.*;
import org.mcisb.ontology.kegg.*;
import org.mcisb.util.*;

/**
 * 
 * @author Neil Swainston
 */
public class NovostarAbsorbanceApp
{
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */ 
	public static void main( String[] args ) throws Exception
	{
		final KineticsExperimentSet experimentSet = new KineticsExperimentSet( StringUtils.getUniqueId(), org.mcisb.kinetics.absorbance.PropertyNames.ABSORBANCE_EXPERIMENT_TYPE_ID );
		final KineticsExcelReader kineticsExcelReader = new NovostarExcelReader( new File( args[ 0 ] ), experimentSet );
		kineticsExcelReader.parse();
		
		final OntologySource keggGenomeUtils = KeggGenomeUtils.getInstance();
		final Collection<OntologyTerm> organismTerms = new ArrayList<>();
		
		for( int i = 1; i < args.length; i++ )
		{
			organismTerms.add( keggGenomeUtils.getOntologyTerm( args[ i ] ) );
		}
		
		new AbsorbanceApp( new JFrame(), experimentSet, organismTerms, new SabioKineticsArchiver() ).show();
	}
}