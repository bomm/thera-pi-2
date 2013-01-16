package Tools;

import java.text.DecimalFormat;

import javax.swing.JLabel;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

public class HMKTreeTableModel extends DefaultTreeTableModel {
	//SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	DecimalFormat dfx = new DecimalFormat( "0.00" );
	
    public HMKTreeTableModel(JXHMKTreeTableNode jXHMKTreeTableNode) {
        super(jXHMKTreeTableNode);
    }
    
    public int getRow(Object node){
    	JXHMKTreeTableNode jXHMKreeTableNode = (JXHMKTreeTableNode) node;
    	return jXHMKreeTableNode.getIndex((JXHMKTreeTableNode)node);
    }
    public Object getValueAt(Object node, int column) {
    	JXHMKTreeTableNode jXHMKreeTableNode = (JXHMKTreeTableNode) node;

    	IndiKey o = null;

    	try{
    		o =  (IndiKey) jXHMKreeTableNode.getUserObject();
    	}catch(ClassCastException cex){
    		return super.getValueAt(node, column);
    	}

        switch (column) {
        	case 0:
        		return o.indischl;
        	case 1:
                return o.grunddaten;
            case 2:
                return o.vorrangig;
            case 3:
                return o.ergaenzend;
        }
        return super.getValueAt(node, column);
    }
    
    public void setValueAt(Object value, Object node, int column){
    	JXHMKTreeTableNode jXTreeTableNode = (JXHMKTreeTableNode) node;
    	IndiKey o;
    	
       	try{
        	o =  (IndiKey) jXTreeTableNode.getUserObject();
        }catch(ClassCastException cex){
        	return;
        } 
        switch (column) {
        case 0:
			o.indischl =((String) ((JLabel)value).getText()) ;
			break;
        case 1:
			o.grunddaten =((String) value) ;
        	break;
        case 2:
        	o.vorrangig = ((String) value);
        	break;
        case 3:
        	o.ergaenzend = ((String) value);
        	break;
        }
    }

    public boolean isCellEditable(java.lang.Object node,int column){
        switch (column) {
        case 0:
            return false;
        case 1:
            return false;
        case 2:
            return false;
        case 3:
            return false;
        default:
            return false;
        }
    }
    
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
            return JLabel.class;
        case 1:
            return String.class;
        case 2:
            return String.class;
        case 3:
            return String.class;
        case 4:
            return String.class;
        default:
            return Object.class;
        }
    }

    public int getColumnCount() {
        return 4;
    }


    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Indik.Schlüssel";
        case 1:
            return "Grunddaten";
        case 2:
            return "vorrangige HM";
        case 3:
            return "ergänzende HM";
        default:
            return "Column " + (column + 1);
        }
    }
}
