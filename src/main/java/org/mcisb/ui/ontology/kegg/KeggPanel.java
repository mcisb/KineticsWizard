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
package org.mcisb.ui.ontology.kegg;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.event.*;
import org.mcisb.ontology.*;
import org.mcisb.ontology.kegg.*;
import org.mcisb.ui.util.*;
import org.mcisb.ui.util.list.*;
import org.mcisb.util.*;

/**
 * 
 * @author Neil Swainston
 */
public class KeggPanel extends ParameterPanel implements ActionListener, ItemListener, ListSelectionListener, MouseMotionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private static final String ORGANISM = "ORGANISM"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final String GENE = "GENE"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	final JTextField geneTextField = new JTextField( DEFAULT_COLUMNS );
	
	/**
	 * 
	 */
	final DefaultListModel<Object> reactionsListModel = new PropertyChangeListenerListModel();
	
	/**
	 * 
	 */
	final JList<?> reactionsList = new JList<>( reactionsListModel );
	
	/**
	 * 
	 */
	final JList<?> organismList;
	
	/**
	 * 
	 */
	final JComboBox<OntologyTerm> substrateComboBox = new JComboBox<>();
	
	/**
	 * 
	 */
	KeggTerm organism = null;
	
	/**
	 * 
	 */
	String geneId = null;
	
	/**
	 * 
	 */
	final JButton geneButton;
	
	/**
	 * 
	 */
	private final DefaultListModel<Object> organismListModel = new DefaultListModel<>();
	
	/**
	 * 
	 */
	private final JCheckBox forwardCheckBox = new JCheckBox();
	
	/**
	 * 
	 */
	private final JCheckBox hillCheckBox = new JCheckBox();
	
	/**
	 * 
	 */
	private final transient Preferences preferences;
	
	/**
	 * 
	 */
	private transient MouseListener mouseListener;
	
	/**
	 *
	 * @param title
	 * @param organismTerms
	 * @param preferences
	 * @throws Exception
	 */
	public KeggPanel( final String title, final Collection<OntologyTerm> organismTerms, final Preferences preferences ) throws Exception
	{
		super( title );
		this.preferences = preferences;
		
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( "org.mcisb.ui.ontology.kegg.messages" ); //$NON-NLS-1$
		geneButton = new JButton( resourceBundle.getString( "KeggPanel.searchLabel" ) ); //$NON-NLS-1$
		
		ListModelUtils.add( organismListModel, ( organismTerms != null && organismTerms.size() > 0 ? organismTerms : KeggGenomeUtils.getInstance().getOrganisms() ).toArray() );
		organismList = new JList<>( organismListModel );
		organismList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		organismList.addListSelectionListener( this );
		geneButton.addActionListener( this );
		reactionsList.addListSelectionListener( this );

		geneTextField.addMouseListener( getMouseListener() );
		reactionsList.addMouseMotionListener( this );
		
		forwardCheckBox.setSelected( true );
		forwardCheckBox.addItemListener( this );
		
		hillCheckBox.setSelected( false );
		hillCheckBox.addItemListener( this );
		
		add( new JLabel( resourceBundle.getString( "KeggPanel.organismLabel" ) ), 0, 0, false, false ); //$NON-NLS-1$
		add( new JScrollPane( organismList ), 1, 0, true, false, GridBagConstraints.BOTH, GridBagConstraints.REMAINDER );
		
		add( new JLabel( resourceBundle.getString( "KeggPanel.geneLabel" ) ), 0, 1, false, false ); //$NON-NLS-1$
		add( geneTextField, 1, 1, true, false, false, false, GridBagConstraints.HORIZONTAL );
		add( geneButton, 2, 1, false, true, false, false, GridBagConstraints.NONE );
		
		add( new JLabel( resourceBundle.getString( "KeggPanel.reactionsLabel" ) ), 0, 2, false, true ); //$NON-NLS-1$
		add( new JScrollPane( reactionsList ), 1, 2, true, false, GridBagConstraints.BOTH, GridBagConstraints.REMAINDER );
		
		add( new JLabel( resourceBundle.getString( "KeggPanel.forwardLabel" ) ), 0, 3, false, false ); //$NON-NLS-1$
		add( forwardCheckBox, 1, 3, true, false );
		
		add( new JLabel( resourceBundle.getString( "KeggPanel.substrateLabel" ) ), 0, 4, false, false ); //$NON-NLS-1$
		add( substrateComboBox, 1, 4, true, false );
		
		add( new JLabel( resourceBundle.getString( "KeggPanel.hillLabel" ) ), 0, 5, false, false, false, true, GridBagConstraints.NONE ); //$NON-NLS-1$
		add( hillCheckBox, 1, 5, false, true, false, true, GridBagConstraints.NONE );
		
		setPreferences();
		update();
	}
	
	/**
	 * 
	 * @return KeggTerm
	 */
	public KeggTerm getOrganism()
	{
		return organism;
	}
	
	/**
	 * 
	 * @return KeggTerm
	 * @throws Exception
	 */
	public KeggTerm getGene() throws Exception
	{
		return (KeggTerm)KeggGeneUtils.getInstance().getOntologyTerm( geneId );
	}
	
	/**
	 * 
	 * @return KeggTerm
	 */
	public synchronized KeggReactionTerm getReaction()
	{
		return (KeggReactionTerm)reactionsList.getSelectedValue();
	}
	
	/**
	 * 
	 *
	 * @return OntologyTerm
	 */
	public OntologyTerm getSubstrate()
	{
		return (OntologyTerm)substrateComboBox.getSelectedItem();
	}
	
	/**
	 *
	 * @return boolean
	 */
	public boolean isForward()
	{
		return forwardCheckBox.isSelected();
	}
	
	/**
	 *
	 * @return boolean
	 */
	public boolean considerHillCoefficient()
	{
		return hillCheckBox.isSelected();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent ev )
	{
		new Thread( new ReactionSearcher( this ) ).start();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged( final ListSelectionEvent e )
	{
		if( !e.getValueIsAdjusting() )
		{
			new Thread( new Runnable()
			{
				/*
				 * (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run()
				{
        			setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        			
            		if( e.getSource().equals( organismList ) )
            		{
            			organism = (KeggTerm)organismList.getSelectedValue();
            			update();
            		}
        
            		final KeggReactionTerm reactionTerm = getReaction();
            		final String GLYCAN_REG_EXP = "(?=.*)G[\\d]{5}(?=.*)"; //$NON-NLS-1$
            		
            		try
            		{
	            		if( reactionTerm != null && RegularExpressionUtils.getMatches( reactionTerm.getEquation(), GLYCAN_REG_EXP ).size() > 0 )
	            		{
	            			final Runnable reactionTermRemover = new ReactionTermRemover( reactionTerm );
	            			SwingUtilities.invokeLater( reactionTermRemover );
	            		}
	            		else
	            		{
	                		setValid( reactionTerm != null );
	                		updateSubstrate();
	            		}
            		}
            		catch( Exception ex )
            		{
            			final JDialog errorDialog = new ExceptionComponentFactory( true ).getExceptionDialog( getParent(), ExceptionUtils.toString( ex ), ex );
            			ComponentUtils.setLocationCentral( errorDialog );
            			errorDialog.setVisible( true );
            		}
            		finally
            		{
            			setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            		}
				}
			} ).start();
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged( final ItemEvent e )
	{
		try
		{
			updateSubstrate();
		}
		catch( Exception ex )
		{
			final JDialog errorDialog = new ExceptionComponentFactory( true ).getExceptionDialog( getParent(), ExceptionUtils.toString( ex ), ex );
			ComponentUtils.setLocationCentral( errorDialog );
			errorDialog.setVisible( true );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.mcisb.util.Disposable#dispose()
	 */
	@Override
	public synchronized void dispose() throws Exception
	{
		geneTextField.removeMouseListener( getMouseListener() );
		
		organismList.removeListSelectionListener( this );
		geneButton.removeActionListener( this );
		reactionsList.removeListSelectionListener( this );
		
		preferences.put( ORGANISM, ( (KeggTerm)organismList.getSelectedValue() ).getId() );
		preferences.put( GENE, geneTextField.getText() );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged( MouseEvent e )
	{
		// No implementation.	
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public synchronized void mouseMoved( MouseEvent e )
	{
		String tooltipText = null;
		
		try
		{
			final int index = reactionsList.locationToIndex( e.getPoint() );
			
			if( index != -1 )
			{
				final OntologyTerm reactionTerm = (OntologyTerm)reactionsList.getModel().getElementAt( index );
				tooltipText = "<html><img src=\"" + getReactionImageUrl( reactionTerm.getId() ) + "\"></html>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		catch( IOException ex )
		{
			// No implementation.
		}
		
		reactionsList.setToolTipText( tooltipText );
	}

	/**
	 * 
	 *
	 */
	void update()
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
        		final boolean enabled = organism != null;
        		geneTextField.setEnabled( enabled );
        		geneButton.setEnabled( enabled );
        		
        		if( !enabled )
        		{
        			geneId = null;
        		}
        		
        		reactionsListModel.clear();
			}
		} );
	}
	
	/**
	 *
	 * @throws Exception
	 */
	void updateSubstrate() throws Exception
	{
		final KeggReactionTerm reactionTerm = getReaction();
		final Map<OntologyTerm,Double> substrates = ( reactionTerm == null ) ? new HashMap<OntologyTerm,Double>() : ( forwardCheckBox.isSelected() ) ? reactionTerm.getSubstrates() : reactionTerm.getProducts();
		
		SwingUtilities.invokeLater( new Runnable()
		{
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				substrateComboBox.removeAllItems();

    			// Update substrates:
    			for( Iterator<OntologyTerm> iterator = substrates.keySet().iterator(); iterator.hasNext(); )
    			{
    				substrateComboBox.addItem( iterator.next() );
    			}
    		}
		} );
	}
	
	/**
	 * 
	 */
	private void setPreferences()
	{
		final String organismId = preferences.get( ORGANISM, null );
		final String geneName = preferences.get( GENE, null );
		
		for( int i = 0; i < organismListModel.getSize(); i++ )
		{
			final KeggTerm currentOrganism = (KeggTerm)organismListModel.get( i );
			
			if( currentOrganism.getId().equals( organismId ) )
			{
				SwingUtilities.invokeLater( new Runnable()
				{
					/*
					 * (non-Javadoc)
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run()
					{
						organismList.setSelectedValue( currentOrganism, true );
						geneTextField.setText( geneName );
					}
				} );
				
				break;
			}
		}
	}
	
	/**
	 * 
	 *
	 * @param reactionId
	 * @return URL
	 * @throws IOException
	 */
	private static URL getReactionImageUrl( final String reactionId ) throws IOException
	{
		return new URL( "http://www.genome.ad.jp/Fig/reaction/" + reactionId + ".gif" ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * 
	 * @return MouseListener
	 */
	private MouseListener getMouseListener()
	{
		if( mouseListener == null )
		{
			mouseListener = new JMenuMouseListener( new JTextComponentMenu() );
		}
		
		return mouseListener;
	}
	
	/**
	 * 
	 * @author Neil Swainston
	 */
	public class ReactionSearcher implements Runnable
	{
		/**
		 * 
		 */
		final Component component;
		
		/**
		 * 
		 * @param component
		 */
		public ReactionSearcher( final Component component )
		{
			this.component = component;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public synchronized void run()
		{
			final String SEPARATOR = ":"; //$NON-NLS-1$
			geneId = organism.getId() + SEPARATOR + geneTextField.getText().trim();
			
			component.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
			
			try
			{
				final Collection<?> reactions = KeggReactionUtils.getInstance().getReactionsFromGeneId( geneId );
				final Runnable reactionTermUpdater = new ReactionTermUpdater( reactions );
				SwingUtilities.invokeLater( reactionTermUpdater );
			}
			catch( Exception ex )
			{
				final JDialog errorDialog = new ExceptionComponentFactory( true ).getExceptionDialog( getParent(), ExceptionUtils.toString( ex ), ex );
				ComponentUtils.setLocationCentral( errorDialog );
				errorDialog.setVisible( true );
			}
			finally
			{
				component.setCursor( Cursor.getDefaultCursor() );
			}
		}
	}
	
	/**
	 * 
	 * @author neilswainston
	 */
	private class ReactionTermRemover implements Runnable
	{
		/**
		 * 
		 */
		private final Object reactionTerm;
		
		/**
		 * 
		 * @param reactionTerm
		 */
		ReactionTermRemover( final Object reactionTerm )
		{
			this.reactionTerm = reactionTerm;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			try
			{
				reactionsListModel.removeElement( reactionTerm );
			}
			catch( Exception ex )
			{
				final JDialog errorDialog = new ExceptionComponentFactory( true ).getExceptionDialog( getParent(), ExceptionUtils.toString( ex ), ex );
				ComponentUtils.setLocationCentral( errorDialog );
				errorDialog.setVisible( true );
			}
		}
	}
	
	private class ReactionTermUpdater implements Runnable
	{
		/**
		 * 
		 */
		private final Collection<?> reactions;
		
		/**
		 * 
		 * @param reactions
		 */
		ReactionTermUpdater( final Collection<?> reactions )
		{
			this.reactions = reactions;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			ListModelUtils.add( reactionsListModel, (Collection<Object>)reactions );

			if( reactionsListModel.size() == 1 )
			{
				reactionsList.setSelectedIndex( 0 );
			}
		}
	}
}