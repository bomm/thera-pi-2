package verkauf;

import javax.swing.table.AbstractTableModel;

public class MyLagerTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	private String[] column = 	{"Artikel-ID","Beschreibung","Preis","MwSt.","Lagerstand",""};
	private Object[][] data;
	
	@Override
	public int getColumnCount() {
		return column.length;
	}
	
	@Override
	public int getRowCount() {
		if(data == null) {
			return 0;
		} else {
			return data.length;
		}
	}
	
	@Override
	public String getColumnName(int col) {
		return column[col];
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	public void update(Object[][] data) {
		this.data = data;
		fireTableDataChanged();
	}
}
