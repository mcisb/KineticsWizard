/**
 * 
 */
package org.mcisb.ui.kinetics;

import org.mcisb.ui.util.table.*;

/**
 * @author Neil Swainston
 *
 */
public class KineticsTable extends ColumnClassTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param model
	 */
	public KineticsTable( final KineticsTableModel model )
	{
		super( model );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.JTable#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable( final int row, final int column )
	{
		final int modelColumnIndex = convertColumnIndexToModel( column );
		return modelColumnIndex == KineticsTableModel.HILL_COEFFICIENT;
	}	
}