package roogle;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;



public class RtaTableModel extends DefaultTableModel{

	Vector vtype = new Vector();


	public RtaTableModel(ResultSet rs) {

	try{
	ResultSetMetaData meta = rs.getMetaData();
	int count = meta.getColumnCount();
	Vector vec = new Vector();

	setColumnCount(count);

	// Überschriften der JTable
	for(int i = 1; i<=count;i++){
	vec.add(meta.getColumnName(i).toUpperCase());
	}
	setColumnIdentifiers(vec);

	// speichern der ersten Row um Typen später zuordnen zu können
	for(int i = 0; i<count;i++){
	this.vtype.add(" ");
	}

	// Abfragen der Datentypen

	String auss = "";
	Short aussh;
	Long ausl;
	int ausi = 0;
	boolean ausb = false;
	java.util.Date ausd = new java.util.Date();

	Object[] row = new Object[count];
	while (rs.next()){
	for (int i = 1; i <= count; i++){
	boolean found = false;

	if (rs.getObject(i) instanceof Integer ){
	ausi = (Integer)rs.getInt(i);
	row[i-1] = ausi;
	found = true;
	}
	if (rs.getObject(i) instanceof Short ){
	aussh = (Short)rs.getShort(i);
	row[i-1] = ausi;
	found = true;
	}
	if (rs.getObject(i) instanceof Long ){
	ausl = (Long)rs.getLong(i);
	row[i-1] = ausi;
	found = true;
	}
	if (rs.getObject(i) instanceof java.util.Date ){
	ausd = (java.util.Date)rs.getDate(i);
	row[i-1] = ausd;
	found = true;
	}
	if (rs.getObject(i) instanceof Boolean ){
	ausb = (boolean)rs.getBoolean(i);
	row[i-1] = ausb;
	found = true;
	}
	if (!found ){
	auss = (String)rs.getString(i);
	row[i-1] = auss;
	}

	}


	addRow(row);

	}
	}

	catch(Exception e){
	e.printStackTrace();
	}
	}


	public Class getColumnClass(int columnIndex) {
	// Datentypen der ersten Row werden ausgelesen
	Class c = vtype.get(columnIndex).getClass();

	return c;
	}

	}