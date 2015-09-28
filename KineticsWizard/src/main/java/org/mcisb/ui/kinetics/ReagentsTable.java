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
import javax.swing.event.*;
import javax.swing.table.*;
import org.mcisb.ontology.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.util.table.*;
import org.mcisb.util.*;

/**
 *
 * @author Neil Swainston
 */
public class ReagentsTable extends ColumnClassTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private static final int ONTOLOGY_TERM_COLUMN = 0;
	
	/**
	 * 
	 */
	private static final int CONCENTRATION_COLUMN = 1;
	
	/**
	 * 
	 */
	private ArrayList<OntologyTerm> defaultOntologyTerms = new ArrayList<>();
	
	/**
	 *
	 * @param model
	 */
	public ReagentsTable( TableModel model )
	{
		super( model );
		setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	}
	
	/**
	 *
	 * @param defaultOntologyTerms
	 */
	public void setDefaultOntologyTerms( final Collection<OntologyTerm> defaultOntologyTerms )
	{
		outer: for( Iterator<OntologyTerm> iterator = this.defaultOntologyTerms.iterator(); iterator.hasNext(); )
		{
			final Object ontologyTerm = iterator.next();
			
			if( !defaultOntologyTerms.contains( ontologyTerm ) )
			{
				// Remove:
    			for( int i = 0; i < dataModel.getRowCount(); i++ )
    			{
    				if( dataModel.getValueAt( i, ONTOLOGY_TERM_COLUMN ).equals( ontologyTerm ) )
    				{
    					( (DefaultTableModel)dataModel ).removeRow( i );
    					iterator.remove();
    					continue outer;
    				}
    			}
			}
		}
		
		for( Iterator<OntologyTerm> iterator = defaultOntologyTerms.iterator(); iterator.hasNext(); )
		{
			final Object ontologyTerm = iterator.next();
			
			if( !this.defaultOntologyTerms.contains( ontologyTerm ) )
			{
				// Add...
				( (DefaultTableModel)dataModel ).addRow( new Object[] { ontologyTerm, Double.valueOf( 0.0 ) } );
			}
		}
		
		this.defaultOntologyTerms = new ArrayList<>( defaultOntologyTerms );
	}
	
	/**
	 *
	 * @return Map
	 */
	public Map<OntologyTerm,Double> getDefaultOntologyTerms()
	{
		return getOntologyTerms( true );
	}
	
	/**
	 *
	 * @return Map
	 */
	public Map<OntologyTerm,Double> getNonDefaultOntologyTerms()
	{
		return getOntologyTerms( false );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass( final int column )
	{
		switch( column )
		{
    		case ONTOLOGY_TERM_COLUMN:
    		{
    			return OntologyTerm.class;
    		}
    		case CONCENTRATION_COLUMN:
    		{
    			return Double.class;
    		}
    		default:
    		{
    			return super.getColumnClass( column );
    		}
		}
	}
	
	/**
	 *
	 * @param isDefault
	 * @return Map
	 */
	private Map<OntologyTerm,Double> getOntologyTerms( final boolean isDefault )
	{
		final Map<OntologyTerm,Double> ontologyTerms = new HashMap<>();
		
		for( int i = 0; i < dataModel.getRowCount(); i++ )
		{
			final OntologyTerm ontologyTerm = (OntologyTerm)dataModel.getValueAt( i, ONTOLOGY_TERM_COLUMN );
			
			if( isDefault && defaultOntologyTerms.contains( ontologyTerm ) || !isDefault && !defaultOntologyTerms.contains( ontologyTerm ) )
			{
				ontologyTerms.put( ontologyTerm, (Double)dataModel.getValueAt( i, CONCENTRATION_COLUMN ) );
			}
		}
		
		return ontologyTerms;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable( final int rowIndex, final int columnIndex )
	{
		return !defaultOntologyTerms.contains( getValueAt( rowIndex, columnIndex ) );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.JTable#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt( Object value, int row, int column )
	{
		if( !defaultOntologyTerms.contains( value ) )
		{
			super.setValueAt( value, row, column );
		}
	}
	
    /*
     * 
     * (non-Javadoc)
     * @see javax.swing.JTable#editingStopped(javax.swing.event.ChangeEvent)
     */
    @Override
	public void editingStopped( ChangeEvent e )
    {
    	try
    	{
        	if( getCellEditor().getCellEditorValue() == null )
        	{
        		removeEditor();
        	}
        	else
        	{
        		super.editingStopped( e );
        	}
    	}
		catch( Exception ex )
		{
			final JDialog errorDialog = new ExceptionComponentFactory( true ).getExceptionDialog( getParent(), ExceptionUtils.toString( ex ), ex );
			ComponentUtils.setLocationCentral( errorDialog );
			errorDialog.setVisible( true );
		}
    }
}