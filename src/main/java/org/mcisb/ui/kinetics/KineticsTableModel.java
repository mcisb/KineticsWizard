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
import javax.swing.table.*;
import org.mcisb.kinetics.*;
import org.mcisb.ontology.sbo.*;
import org.sbml.jsbml.*;

/**
 * 
 * @author Neil Swainston
 */
public class KineticsTableModel extends DefaultTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public static final int MODEL = 0;
	
	/**
	 * 
	 */
	public static final int KCAT = 1;
	
	/**
	 * 
	 */
	public static final int KCAT_STANDARD_ERROR = 2;
	
	/**
	 * 
	 */
	public static final int KCAT_PERCENTAGE_ERROR = 3;
	
	/**
	 * 
	 */
	public static final int KM = 4;
	
	/**
	 * 
	 */
	public static final int KM_STANDARD_ERROR = 5;
	
	/**
	 * 
	 */
	public static final int KM_PERCENTAGE_ERROR = 6;

	/**
	 * 
	 */
	public static final int HILL_COEFFICIENT = 7;

	/**
	 * 
	 */
	public static final int HILL_COEFFICIENT_STANDARD_ERROR = 8;

	/**
	 * 
	 */
	public static final int HILL_COEFFICIENT_PERCENTAGE_ERROR = 9;
	
	/**
	 * 
	 * @param experimentSet
	 * @throws Exception
	 */
	public KineticsTableModel( final KineticsExperimentSet experimentSet ) throws Exception
	{
		boolean hasHillCoefficients = false;
		
		dataVector = new Vector<Vector<Object>>();

		outer: for( Iterator<SBMLDocument> iterator = experimentSet.getDocuments().iterator(); iterator.hasNext(); )
		{
			final Model model = iterator.next().getModel();
			
			for( int l = 0; l < model.getNumReactions(); )
			{
				final Reaction reaction = model.getReaction( l );
				final KineticLaw kineticLaw = reaction.getKineticLaw();
				LocalParameter kcat = null;
				LocalParameter km = null;
				LocalParameter hillCoefficient = null;
				
				for( int m = 0; m < kineticLaw.getLocalParameterCount(); m++ )
				{
					final LocalParameter parameter = kineticLaw.getLocalParameter( m );
					
					if( parameter.getSBOTerm() == SboUtils.CATALYTIC_RATE_CONSTANT )
					{
						kcat = parameter;
					}
					else if( parameter.getSBOTerm() == SboUtils.MICHAELIS_CONSTANT )
					{
						km = parameter;
					}
					else if( parameter.getSBOTerm() == SboUtils.HILL_COEFFICIENT )
					{
						hillCoefficient = parameter;
					}
				}
				
				if( kcat != null && km != null && hillCoefficient != null )
				{
					final double kCat = kcat.getValue();
					final double kCatStandardError = Double.parseDouble( experimentSet.getCondition( kcat, org.mcisb.util.PropertyNames.ERROR ) );
					final double kM = km.getValue();
					final double kMStandardError = Double.parseDouble( experimentSet.getCondition( km, org.mcisb.util.PropertyNames.ERROR ) );
					
					final Vector<Object> rowData = new Vector<>();
					rowData.add( model.getName() );
					rowData.add( Double.valueOf( kCat ) );
					rowData.add( Double.valueOf( kCatStandardError ) );
					rowData.add( Double.valueOf( ( kCatStandardError / kCat ) * 100.0f ) );
					rowData.add( Double.valueOf( kM ) );
					rowData.add( Double.valueOf( kMStandardError ) );
					rowData.add( Double.valueOf( ( kMStandardError / kM ) * 100.0f ) );
					
					final double hillcoefficient = hillCoefficient.getValue();
					final double hillcoefficientStandardError = Double.parseDouble( experimentSet.getCondition( hillCoefficient, org.mcisb.util.PropertyNames.ERROR ) );
						
					if( hillcoefficient != KineticsCalculator.DEFAULT_HILL_COEFFICIENT || hillcoefficientStandardError != KineticsCalculator.DEFAULT_HILL_COEFFICIENT_ERROR )
					{
						hasHillCoefficients = true;
					}
					
					rowData.add( Double.valueOf( hillcoefficient ) );
					rowData.add( Double.valueOf( hillcoefficientStandardError ) );
					rowData.add( Double.valueOf( ( hillcoefficientStandardError / hillcoefficient ) * 100.0f ) );
					
					dataVector.add( rowData );
				}
				
				continue outer;
			}
		}
		
		columnIdentifiers = new Vector<>( Arrays.asList( "Sample", "kcat / s-1", "kcat standard error / s-1", "kcat % standard error", "KM / mM", "KM standard error / mM", "KM % standard error" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		
		if( hasHillCoefficients )
		{
			columnIdentifiers.add( "Hill coefficient" ); //$NON-NLS-1$
			columnIdentifiers.add( "Hill coefficient standard error" ); //$NON-NLS-1$
			columnIdentifiers.add( "Hill coefficient % standard error" ); //$NON-NLS-1$
		}
		
		setDataVector( dataVector, columnIdentifiers );
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass( int column )
	{
		final int FIRST_ROW = 0;
		
		if( getRowCount() > FIRST_ROW )
		{
			return getValueAt( FIRST_ROW, column ).getClass();
		}
		
		return null;
	}
}