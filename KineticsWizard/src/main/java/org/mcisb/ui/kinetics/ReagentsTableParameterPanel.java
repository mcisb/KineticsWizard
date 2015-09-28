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

import java.awt.*;
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
public class ReagentsTableParameterPanel extends TableParameterPanel implements TableModelListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private final static ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.messages" ); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final static int REAGENT_COLUMN_INDEX = 0;
	
	/**
	 * 
	 */
	private final ReagentsTableCellEditor cellEditor;
	
	/**
	 *
	 * @param title
	 * @throws Exception
	 */
	public ReagentsTableParameterPanel( final String title ) throws Exception
	{
		super( title, new ReagentsTable( new DefaultTableModel() ) );
		
		final DefaultTableModel model = (DefaultTableModel)table.getModel();
		model.setColumnIdentifiers( new Object[] { resourceBundle.getString( "ReagentsTableParameterPanel.reagentPrompt" ), resourceBundle.getString( "ReagentsTableParameterPanel.concentrationPrompt" ) } ); //$NON-NLS-1$ //$NON-NLS-2$
		model.addTableModelListener( this );
		
		final Container topLevelAncestor = getTopLevelAncestor();
		JDialog owner;
		
		if( topLevelAncestor instanceof Frame )
		{
			owner = new JDialog( (Frame)topLevelAncestor );
		}
		else
		{
			owner = new JDialog();
			owner.setModal( true );
			// owner.setIconImage( new ResourceFactory().getImageIcon( resourceBundle.getString( "ReagentsTableParameterPanel.icon" ) ).getImage() ); //$NON-NLS-1$ // Java 1.6
		}
		
		cellEditor = new ReagentsTableCellEditor( owner );
		table.getColumn( resourceBundle.getString( "ReagentsTableParameterPanel.reagentPrompt" ) ).setCellEditor( cellEditor ); //$NON-NLS-1$
		setValid( true );
	}
	
	/**
	 *
	 * @param defaultOntologyTerms
	 */
	public void setDefaultOntologyTerms( final Collection<OntologyTerm> defaultOntologyTerms )
	{
		( (ReagentsTable)table ).setDefaultOntologyTerms( defaultOntologyTerms );
	}
	
	/**
	 *
	 * @return Map
	 */
	public Map<OntologyTerm,Double> getBuffer()
	{
		return ( (ReagentsTable)table ).getNonDefaultOntologyTerms();
	}
	
	/**
	 *
	 * @return Map
	 */
	public Map<OntologyTerm,Double> getDefaultOntologyTerms()
	{
		return ( (ReagentsTable)table ).getDefaultOntologyTerms();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.mcisb.ui.tracking.Manager#newObject()
	 */
	@Override
	public void newObject() throws Exception
	{
		( (DefaultTableModel)table.getModel() ).addRow( new Object[] { null, Double.valueOf( 0.0 ) } );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.table.TableParameterPanel#deleteObject()
	 */
	@Override
	public void deleteObject()
	{
		final int selectedRow = table.getSelectedRow();
		
		if( table.isCellEditable( selectedRow, REAGENT_COLUMN_INDEX ) )
		{
			super.deleteObject();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged( ListSelectionEvent e )
	{
		final int selectedRow = table.getSelectedRow();
		
		if( selectedRow == -1 || table.isCellEditable( selectedRow, REAGENT_COLUMN_INDEX ) )
		{
			super.valueChanged( e );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged( TableModelEvent e )
	{
		try
		{
    		final TableModel model = table.getModel();
    
    		for( int i = 0; i < model.getRowCount(); i++ )
    		{
    			if( model.getValueAt( i, REAGENT_COLUMN_INDEX ) == null )
    			{
    				setValid( false );
    				return;
    			}
    		}
    		
    		setValid( true );
		}
		catch( Exception ex )
		{
			final JDialog dialog = new ExceptionComponentFactory( true ).getExceptionDialog( this.getParent(), ExceptionUtils.toString( ex ), ex );
			ComponentUtils.setLocationCentral( dialog );
			dialog.setVisible( true );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.ui.util.table.TableParameterPanel#dispose()
	 */
	@Override
	public void dispose()
	{
		final DefaultTableModel model = (DefaultTableModel)table.getModel();
		model.removeTableModelListener( this );
		cellEditor.dispose();
	}
}