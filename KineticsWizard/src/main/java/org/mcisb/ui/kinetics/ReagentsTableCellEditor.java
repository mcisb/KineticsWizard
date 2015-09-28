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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.mcisb.ontology.*;
import org.mcisb.ontology.chebi.*;
import org.mcisb.ontology.uniprot.*;
import org.mcisb.ui.ontology.*;
import org.mcisb.ui.util.*;
import org.mcisb.util.*;

/**
 *
 * @author Neil Swainston
 */
public class ReagentsTableCellEditor extends AbstractCellEditor implements TableCellEditor, MouseListener, Disposable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.kinetics.messages" ); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private final transient UniProtUtils uniProtUtils = new UniProtUtils();
	
	/**
	 * 
	 */
	private final JLabel label = new JLabel();
	
	/**
	 * 
	 */
	private final Dialog owner;
	
	/**
	 * 
	 */
	private final OntologyTermDialog dialog;
	
    /**
     *
     * @param owner
     * @throws Exception
     */
    public ReagentsTableCellEditor( final Dialog owner ) throws Exception
    {
    	this.owner = owner;
        label.addMouseListener( this );

        final Map<Object,OntologySource> ontologySourceMap = new LinkedHashMap<>();
        ontologySourceMap.put( resourceBundle.getString( "ReagentsTableCellEditor.chebiPrompt" ), ChebiUtils.getInstance() ); //$NON-NLS-1$
        ontologySourceMap.put( resourceBundle.getString( "ReagentsTableCellEditor.uniprotPrompt" ), uniProtUtils ); //$NON-NLS-1$
        dialog = new OntologyTermSearchDialog( owner, resourceBundle.getString( "ReagentsTableCellEditor.title" ), true, resourceBundle.getString( "ReagentsTableCellEditor.title" ), ontologySourceMap ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked( final MouseEvent e )
	{
		ComponentUtils.setLocationCentral( dialog );
		dialog.setVisible( true );
        fireEditingStopped(); // Make the renderer reappear.
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
	{
		try
		{
    		if( value instanceof OntologyTerm )
    		{
    			label.setText( value.toString() );
    			dialog.setSearchTerm( value.toString() );
    		}
		}
		catch( Exception ex )
		{
			final JDialog errorDialog = new ExceptionComponentFactory( true ).getExceptionDialog( owner, ExceptionUtils.toString( ex ), ex );
			ComponentUtils.setLocationCentral( errorDialog );
			errorDialog.setVisible( true );
		}
		
		return label;
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue()
	{
		return dialog.getOntologyTerm();
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered( MouseEvent e )
	{
		// No implementation.
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited( MouseEvent e )
	{
		// No implementation.
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed( MouseEvent e )
	{
		// No implementation.
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased( MouseEvent e )
	{
		// No implementation.
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public void dispose()
	{
		label.removeMouseListener( this );
		dialog.dispose();
	}
}