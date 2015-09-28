/**
 * 
 */
package org.mcisb.ui.kinetics;

import java.util.*;
import javax.swing.event.*;
import org.mcisb.kinetics.*;

/**
 * @author Neil Swainston
 *
 */
public class HillCoefficientListener implements TableModelListener
{
	/**
	 * 
	 */
	private final Map<String,KineticsCalculator> modelNameToKineticsCalculator;
	
	/**
	 * 
	 * @param modelNameToKineticsCalculator
	 */
	public HillCoefficientListener( final Map<String,KineticsCalculator> modelNameToKineticsCalculator )
	{
		this.modelNameToKineticsCalculator = modelNameToKineticsCalculator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged( final TableModelEvent e )
	{
		final Object source = e.getSource();

		if( e.getColumn() == KineticsTableModel.HILL_COEFFICIENT && source instanceof KineticsTableModel )
		{
			final KineticsTableModel kineticsTableModel = (KineticsTableModel)source;
			final Object hillCoefficient = kineticsTableModel.getValueAt( e.getFirstRow(), e.getColumn() );
			final Object modelName = kineticsTableModel.getValueAt( e.getFirstRow(), KineticsTableModel.MODEL );
				
			if( hillCoefficient instanceof Double && modelName != null )
			{
				final KineticsCalculator kineticsCalculator = modelNameToKineticsCalculator.get( modelName );
				kineticsCalculator.setHillCoefficient( ( (Double)hillCoefficient ).doubleValue() );
			}
		}
	}
}
