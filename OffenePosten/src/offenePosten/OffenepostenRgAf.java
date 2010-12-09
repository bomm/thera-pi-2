package offenePosten;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import offenePosten.OffenepostenPanel.MyOffenePostenTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

public class OffenepostenRgAf extends JXPanel implements TableModelListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	OffenepostenTab eltern = null;
	MyOffenePostenRgAfTableModel tabmod = null;
	JXTable tab = null;
	JLabel summeOffen;
	JLabel summeRechnung;
	JLabel summeGesamtOffen;
	JLabel anzahlSaetze;
	
	BigDecimal gesamtOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal suchOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal suchGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
	DecimalFormat dcf = new DecimalFormat("###0.00");
	int gefunden;

	OffenepostenRgAf(OffenepostenTab xeltern){
		eltern = xeltern;
	}
	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	class MyOffenePostenRgAfTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex){
			case 0:
				return Integer.class;
			case 1:
				return Date.class;				
			case 2:
				return String.class;
			case 3:
				return String.class;				
			case 4:
				return String.class;
			case 5:
				return Double.class;				
			case 6:
				return Double.class;
			case 7:
				return Date.class;				
			case 8:
				return Double.class;				
			case 9:
				return Date.class;
			case 10:
				return Date.class;				
			case 11:
				return Date.class;				
			case 12:
				return Boolean.class;				
			case 13:
				return String.class;				
			case 14:
				return String.class;				
			case 15:
				return Integer.class;				

			}
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			
			if(col < 15){
				return true;				
			}
			return false;
		}
		   
	}
	

}
