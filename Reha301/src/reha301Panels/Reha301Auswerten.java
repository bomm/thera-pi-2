package reha301Panels;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import Tools.DatFunk;
import Tools.INIFile;
import Tools.JCompTools;
import Tools.SqlInfo;
import Tools.WartenAufDB;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import reha301.Reha301;
import reha301.Reha301Tab;

public class Reha301Auswerten extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Reha301Tab eltern = null;
	public JXTable tab = null;
	public MyTableModel tabmod;
	public JEditorPane[] editpan = {null,null,null};
	
	ActionListener al = null;
	int anzeigeart = -1;
	public String[] artderNachricht = {"Nachr.Typ unbekannt","Bewilligung","Ablehnung","Verl. Zustimmung","Verl. Ablehnung","sonstige Nachricht"};
	public String patBetroffen = null; 
	public Reha301Auswerten(Reha301Tab xeltern){
		super(new BorderLayout());
		eltern = xeltern;
		add(getContent(),BorderLayout.CENTER);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					String cmd = "select eingelesen,sender,nachrichtentyp,patangaben,versicherungsnr,ktraeger,datum,id,leistung,eilfall from dta301 where eingelesen='F' order by datum";
					regleTabelle(cmd,1);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		validate();
	}

	public JXPanel getContent(){
		JXPanel pan = new JXPanel();
		String xwerte = "10dlu,fill:0:grow(1.0),10dlu";
		String ywerte = "10dlu,200dlu,10dlu:g";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		tabmod = new MyTableModel();
		tabmod.setColumnIdentifiers(new String[] {"lfnr","import","Sender","Nachrichtentyp","Name, Vorname","Adresse","VSNR","Kostenträger","Krankenkasse","Datum","",""});
		tab = new JXTable(tabmod);
		tab.getColumn(0).setMaxWidth(25);
		tab.getColumn(1).setMaxWidth(45);
		tab.getColumn(2).setMaxWidth(65);
		tab.getColumn(3).setMinWidth(150);
		tab.getColumn(4).setMinWidth(150);
		tab.getColumn(5).setMinWidth(150);
		tab.getColumn(6).setMinWidth(85);
		tab.getColumn(6).setMaxWidth(85);
		tab.getColumn(7).setMaxWidth(65);
		tab.getColumn(9).setMaxWidth(75);
		tab.getColumn(10).setMinWidth(0);
		tab.getColumn(10).setMaxWidth(0);
		tab.getColumn(11).setMinWidth(0);
		tab.getColumn(11).setMaxWidth(0);

		tab.setEditable(false);
		tab.setSortable(false);
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		tab.validate();
		tab.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					if(anzeigeart==1){
						if(tab.getSelectedRow()>=0){
							doPatUntersuchen(tab.getSelectedRow(),arg0.getLocationOnScreen());							
						}
					}
					
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		pan.add(jscr,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));

		pan.add(getActionPanel(),cc.xy(2,3,CellConstraints.FILL,CellConstraints.FILL));
		
		pan.validate();
		return pan;
	}
	private JXPanel getActionPanel(){
		JXPanel pan = new JXPanel();
		//                1         2            3            4         5        6             7
		String xwerte = "0dlu,fill:0:grow(0.33),5dlu,fill:0:grow(0.33),5dlu,fill:0:grow(0.33),0dlu";
		//                1    2   3    4              5    6  7
		String ywerte = "10dlu,p,2dlu,fill:0:grow(1.0),2dlu,p,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		JLabel lab = new JLabel("301-er Daten");
		pan.add(lab,cc.xy(4,2,CellConstraints.FILL,CellConstraints.FILL));
		editpan[0] = new JEditorPane();
		editpan[0].setContentType("text/html");
		editpan[0].setEditable(false);
		editpan[0].setOpaque(true);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(editpan[0]);
		jscr.validate();
        pan.add(jscr,cc.xy(4,4,CellConstraints.FILL,CellConstraints.FILL));
        
		lab = new JLabel("Patienten Stammdaten");
		pan.add(lab,cc.xy(6,2,CellConstraints.FILL,CellConstraints.FILL));
        editpan[1] = new JEditorPane();
		editpan[1].setContentType("text/html");
		editpan[1].setEditable(false);
		editpan[1].setOpaque(true);
		jscr = JCompTools.getTransparentScrollPane(editpan[1]);
		jscr.validate();
        pan.add(jscr,cc.xy(6,4,CellConstraints.FILL,CellConstraints.FILL));
        
		pan.validate();
		return pan;
	}
	
	public void ActivateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("einlesen")){

				}
			}
		};
	}
	private void doPatUntersuchen(int row,Point pos){
		String[] teilen = tab.getValueAt(row,4).toString().split("#");
		String cmd = "select * from pat5 where n_name='"+teilen[1]+"' and v_name='"+teilen[2]+"' and "+
		"geboren='"+tab.getValueAt(row,10).toString()+"'";
		//System.out.println(tab.getValueAt(row,10));
		Vector<Vector<String>> patvec = SqlInfo.holeFelder(cmd);
		if(patvec.size()<=0){
			JOptionPane.showMessageDialog(null, "Patient nicht in Datenbank vorhanden");
		}else{
			doPatientenWahl(tab.getValueAt(row,11).toString(),pos);
		}
	}
	private void doPatientenWahl(String id,Point pos){
		Reha301PatAuswahl patwahl = new Reha301PatAuswahl(this,id);
		patwahl.setModal(true);
		patwahl.setLocation(pos);
		patwahl.pack();
		patwahl.setVisible(true);
		patwahl = null;
		
	}
	/********************************************/
	public void doSetPatientFuerNachricht(String patid){
		patBetroffen = String.valueOf(patid);
		String ktraeger = tab.getValueAt(tab.getSelectedRow(), 7).toString();
		String kkasse = tab.getValueAt(tab.getSelectedRow(), 8).toString();
		String id = tab.getValueAt(tab.getSelectedRow(), 11).toString();
		String diag1 = SqlInfo.holeEinzelFeld("select diagschluessel from dta301 where id ='"+id+"' LIMIT 1");
		String[] diag2 = diag1.split("\\+");
		String diaggruppe = null;
		if(diag2[2].split(":")[0].startsWith("M") || diag2[2].split(":")[0].startsWith("S") ){
			diaggruppe = "04";
		}
		Object[] rVTraeger = testeDTAIni(ktraeger,diaggruppe);
		String diagnose = diag2[2].split(":")[0];
		String fallart = SqlInfo.holeEinzelFeld("select leistung from dta301 where id ='"+id+"' LIMIT 1");
		String eilfall = SqlInfo.holeEinzelFeld("select eilfall from dta301 where id ='"+id+"' LIMIT 1");
		String preisgruppe = SqlInfo.holeEinzelFeld("select preisgruppe from kass_adr where ik_kostent ='"+ktraeger+"' LIMIT 1");
		System.out.println();
		if( ((Integer)rVTraeger[0]) >= 0){
			//RV-Träger
			System.out.println("Patient="+patBetroffen);
			System.out.println("Kostenträger="+ktraeger);
			System.out.println("Diagnose="+diagnose);
			System.out.println("Fallart="+fallart);
			System.out.println("Eilfall="+eilfall);
			System.out.println("Preisgruppe="+preisgruppe);
			System.out.println("Diagnosegruppe="+diaggruppe);
			for(int i = 0; i < rVTraeger.length;i++){
				System.out.println(rVTraeger[i]);
			}
			
		}else{
			//KrankenKasse
		}
		
		//Verordnung überpüfen
		//Verordnung anlegen
		//
	}
	/********************************************/	
	private Object[] testeDTAIni(String ktraeger,String diaggruppe){
		Object[] retobject = {-1,null,null,null,null,null};
		System.out.println(ktraeger+" - "+diaggruppe);
		INIFile ini = new INIFile(Reha301.progHome+"ini/"+Reha301.aktIK+"/dta301.ini");
		if(ini==null){System.out.println("ini=NULL !!!!!");}
		String gruppe = ini.getStringProperty("RehaGruppen", diaggruppe);
		if(gruppe == null){
			return retobject.clone();
		}
		int anzahl = ini.getIntegerProperty("RVTraeger_"+gruppe, "RVTraegerAnzahl");
		for(int i = 1 ; i <= anzahl;i++){
			if(ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerIK"+Integer.toString(i)).equals(ktraeger)){
				retobject[0] = i;
				retobject[1] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerIK"+Integer.toString(i)); 
				retobject[2] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerMed"+Integer.toString(i));
				retobject[3] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerAHB"+Integer.toString(i));
				retobject[4] = ini.getStringProperty("RVTraeger_"+gruppe, "RVTraegerNS"+Integer.toString(i));
				retobject[5] = String.valueOf(gruppe);
				return retobject.clone();
			}
		}
		return retobject.clone();
	}
	private void regleTabelle(String statement,int tabart){
//		"select eingelesen,sender,nachrichtentyp,patangaben,versicherungsnr,ktraeger,datum from dta301 where eingelesen='F' order by datum";

		System.out.println(statement);
		try{
			if(! WartenAufDB.IsDbOk()){
				JOptionPane.showMessageDialog(null, "Datenbank konnte nicht gestartet werden");
				return;
			}
			Vector<Vector<String>> vec = SqlInfo.holeFelder(statement);
			tabmod.setRowCount(0);
			anzeigeart = tabart;
			Vector<Object> vecobj = new Vector<Object>();
			String[] pat = null;
			String patangaben = null;
			String ortsangaben = null;
			String kassenangaben = null;
			String patgeboren = null;
			if(vec.size()> 0){
				for(int i = 0; i < vec.size();i++){
					vecobj.clear();
					
					vecobj.add((String) Integer.toString(i+1));
					vecobj.add((Boolean) (vec.get(i).get(0).equals("T")? true : false) );
					vecobj.add((String)vec.get(i).get(1));
					vecobj.add((String) artderNachricht[Integer.parseInt(vec.get(i).get(2))]+"-"+vec.get(i).get(8)+"-"+vec.get(i).get(9));
					pat = vec.get(i).get(3).split("#");
					patangaben = "";
					ortsangaben = "";
					kassenangaben = "";
					patgeboren = null;
					if(pat.length >=7){
						patangaben =pat[0]+"#"+pat[1]+"#"+pat[2];
						ortsangaben = pat[4]+"#"+pat[5]+"#"+pat[6];
						try{
							patgeboren = pat[3];
						}catch(Exception ex){
							
						}
					}
					vecobj.add((String) patangaben);
					vecobj.add((String) ortsangaben);
					vecobj.add((String) vec.get(i).get(4));
					vecobj.add((String) vec.get(i).get(5));
					if(pat.length >=8){
						kassenangaben = SqlInfo.holeEinzelFeld("select name1 from ktraeger where ikkasse='"+pat[7]+"' LIMIT 1");
						kassenangaben = kassenangaben+String.valueOf("#"+pat[7]);
					}
					vecobj.add((String) kassenangaben);
					try{
						vecobj.add((String) DatFunk.sDatInDeutsch(vec.get(i).get(6)));	
					}catch(Exception ex){
						vecobj.add("01.01.0000");
					}
					vecobj.add((String) patgeboren);
					vecobj.add((String) vec.get(i).get(7));
					tabmod.addRow( (Vector<?>)vecobj.clone());
					
					
				}
				tab.setRowSelectionInterval(0, 0);
				show301PatData(0);
			}
			tab.validate();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void show301PatData(int row){
		StringBuffer buf = new StringBuffer();
		String pat = tab.getValueAt(tab.getSelectedRow(), 4).toString();
		String adress = tab.getValueAt(tab.getSelectedRow(), 5).toString();
		buf.append("<html>");
		buf.append("<table>");
		buf.append("<tr><td textalign='align-right'>Anrede</td><td>"+pat.split("#")[0]+"</td></tr>");
		buf.append("<tr><td textalign='align-right'>Name</td><td>"+pat.split("#")[1]+", "+pat.split("#")[2]+"</td></tr>");
		buf.append("<tr><td>Strasse</td><td>"+adress.split("#")[0]+"</td></tr>");
		buf.append("<tr><td>Ort</td><td>"+adress.split("#")[1]+" "+adress.split("#")[2]+"</td></tr>");
		buf.append("</table>");
		buf.append("</html>");
		editpan[0].setText(buf.toString());
	}
	
/********************************************************/	
	class MyTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==1 ){
				   return Boolean.class;}
			   else{
				   return String.class;
			   }
	       }

	    public boolean isCellEditable(int row, int col) {

	    	if (col == 0){
	    		return true;
		    }else if(col == 3){
		    	return true;
		    }else if(col == 7){
	        	return true;
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	    }
	}
	
	
}
