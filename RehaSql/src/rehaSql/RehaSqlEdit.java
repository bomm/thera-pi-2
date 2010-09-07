package rehaSql;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.swingx.JXPanel;

public class RehaSqlEdit extends JXPanel implements ListSelectionListener, ActionListener, TableModelListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	RehaSqlTab eltern = null;
	public RehaSqlEdit(RehaSqlTab xeltern){
		super();
		eltern = xeltern;
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		
	} 

}
