package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import systemTools.JRtaTextField;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilRoogleGruppen extends JXPanel implements ActionListener,KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton knopf1 = null;
	JButton knopf2 = null;
	JButton knopf3 = null;
	JButton knopf4 = null;
	JButton knopf5 = null;
	JButton knopf6 = null;
	JButton knopf7 = null;
	JComboBox jcomboWahl = null;
	JRtaTextField RGname = null;
	
	JXTable Source = null;
	//JXList RGmembers = null;
	JXTable RGmembers = null;	
	
	String[] kollegen = null;
	String[][] mitglieder = null;
	String[] gruppenname = null;
	Vector vmitglieder = new Vector();
	Vector vkollegen = new Vector();
	String saktItem = "";
	int iaktItem = 0;
	GruppenListModel glm = null;
	KollegenListModel klm = null;
	private boolean lneu = false;
	private boolean lspeichern = false;
	
	JScrollPane jscroll = null;
	
	public SysUtilRoogleGruppen(){
		super(new GridLayout(1,1));
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/

		jscroll = new JScrollPane();
		jscroll.setOpaque(false);
		jscroll.getViewport().setOpaque(false);
		jscroll.setBorder(null);
		jscroll.getVerticalScrollBar().setUnitIncrement(15);

		
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				//System.out.println("Aufruf der Seite RoogleGruppen definieren");
				jscroll.setViewportView(getRoogleGruppenLayout());
				jscroll.validate();
				add(jscroll);
				//add(getRoogleGruppenLayout());
				knopfGedoense(new int[]{1,1,1,0,0, 0,0});
				validate();
       	  	}
		});
		
		
		return;
	}
	private JPanel getRoogleGruppenLayout(){
		
			

			// buttons
			knopf1 = new JButton("neu"); 
			knopf1.setPreferredSize(new Dimension(70, 20));
			knopf1.addActionListener(this);
			knopf1.setActionCommand("neu");
			knopf1.addKeyListener(this);

			knopf2 = new JButton("löschen");
			knopf2.setPreferredSize(new Dimension(70, 20));
			knopf2.addActionListener(this);
			knopf2.setActionCommand("loeschen");
			knopf2.addKeyListener(this);
			
			knopf3 = new JButton("ändern");
			knopf3.setPreferredSize(new Dimension(70, 20));
			knopf3.addActionListener(this);
			knopf3.setActionCommand("aendern");
			knopf3.addKeyListener(this);
			
			knopf4 = new JButton("speichern");
			knopf4.setPreferredSize(new Dimension(70, 20));
			knopf4.addActionListener(this);		
			knopf4.setActionCommand("speichern");
			knopf4.addKeyListener(this);		
			
			knopf5 = new JButton("abbrechen");
			knopf5.setPreferredSize(new Dimension(70, 20));
			knopf5.addActionListener(this);		
			knopf5.setActionCommand("abbrechen");
			knopf5.addKeyListener(this);
			
			knopf6 = new JButton(">>");
			knopf6.setPreferredSize(new Dimension(35, 20));
			knopf6.addActionListener(this);		
			knopf6.setActionCommand("take");
			knopf6.addKeyListener(this);
			
			knopf7 = new JButton("<<");
			knopf7.setPreferredSize(new Dimension(35, 20));
			knopf7.addActionListener(this);		
			knopf7.setActionCommand("delete");
			knopf7.addKeyListener(this);
			
			jcomboWahl = null;
			RGname = null;
			
	
		
		
	        //                                 1.     2.    3.    4.     5.     6.    7.   8.     9.    10.    11.
			FormLayout lay = new FormLayout("40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
	       //1.    2.  3.   4.    5.    6.     7.    8.   9.    10.  11.  12.  13.    14.   15.    16.   17.
			"p, 10dlu, p, 10dlu, 10dlu, 10dlu, p, 10dlu, 20dlu, p, 20dlu, p, 20dlu, 10dlu, 10dlu, 10dlu, p");
			PanelBuilder builder = new PanelBuilder(lay);
			builder.setDefaultDialogBorder();
			builder.getPanel().setOpaque(false);

			CellConstraints cc = new CellConstraints();
			
			builder.addLabel("bestehende Gruppe wählen", cc.xyw(1,1,3));


			comboFuellen(-1);
			jcomboWahl = new JComboBox(gruppenname);
			jcomboWahl.setSelectedIndex(0);		
			jcomboWahl.addActionListener(this);
			jcomboWahl.setActionCommand("comboaktion");
			builder.add(jcomboWahl, cc.xyw(5,1,4));

		
			builder.addLabel("Name der Gruppe", cc.xyw(1,3,3));
			RGname = new JRtaTextField("nix",true);
			RGname.setText(gruppenname[0]);
			RGname.setEnabled(false);
			builder.add(RGname, cc.xyw(5,3,4));
			
			builder.addSeparator("RoogleGruppe bearbeiten", cc.xyw(1, 5, 9));
		
			builder.addLabel("Mitarbeiter auswählen", cc.xyw(1, 7,3));
			builder.addLabel("Gruppenmitglieder", cc.xyw(7, 7,3));

			RGmembers =	new JXTable();
			Source = new JXTable();
			/*******************************/
			final PanelBuilder xbuilder = builder;
			final CellConstraints xcc = cc;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){

			macheKollegen();
			klm = new KollegenListModel();
			String[] kcolumn = {"Mitarbeiter"};
			klm.setDataVector((Vector) vkollegen.clone(), getColVector(kcolumn));			
			//Source = new JXTable(klm);
			Source.setModel(klm);
			//Source.setSortOrder(0,SortOrder.ASCENDING);
			Source.validate();
			Source.setEnabled(false);			
			/*******************************/
			macheGruppenListModel(0);
			glm = new GruppenListModel();
			String[] column = {"Mitglied"};
			glm.setDataVector((Vector)vmitglieder.clone(), getColVector(column));
			RGmembers.setModel(glm);
			//RGmembers =	new JXTable(glm);
			//RGmembers.setSortOrder(0,SortOrder.ASCENDING);
			RGmembers.validate();
			RGmembers.setEnabled(false);
			//kollegenEntfernen();
			/*******************************/
			xbuilder.add(new JScrollPane(Source), xcc.xywh(1, 9, 3, 5));
			xbuilder.add(new JScrollPane(RGmembers), xcc.xywh(7, 9, 3, 5));
			////System.out.println("Aufruf der Seite RoogleGruppen definieren");
			kollegenEntfernen();
			xbuilder.getPanel().validate();
				}
			});
			
			builder.add(knopf6, cc.xy(5, 10));
			builder.add(knopf7, cc.xy(5,12));
			
			builder.addSeparator("", cc.xyw(1, 15, 9));
			
			builder.add(knopf1,cc.xy(1,17));
			builder.add(knopf2, cc.xy(3,17));
			builder.add(knopf3, cc.xy(5,17));
			builder.add(knopf4, cc.xy(7, 17));
			builder.add(knopf5,cc.xy(9,17));
			
			
		//und abschlie�end anstatt return new JPanel(); -> return builder.getPanel();
			return builder.getPanel();
		}
		private Vector getColVector(String[] cols){
			Vector col = new Vector();
			for(int i = 0;i<cols.length;i++){
				col.add(cols[i]);				
			}
			return col;
		}
	
		private void knopfGedoense(int[] knopfstatus){
			knopf1.setEnabled((knopfstatus[0]== 0 ? false : true));
			knopf2.setEnabled((knopfstatus[1]== 0 ? false : true));
			knopf3.setEnabled((knopfstatus[2]== 0 ? false : true));
			knopf4.setEnabled((knopfstatus[3]== 0 ? false : true));		
			knopf5.setEnabled((knopfstatus[4]== 0 ? false : true));
			knopf6.setEnabled((knopfstatus[5]== 0 ? false : true));		
			knopf7.setEnabled((knopfstatus[6]== 0 ? false : true));			
		}
		
		private void comboAuswerten(){
			macheGruppenListModel(jcomboWahl.getSelectedIndex());
			macheKollegen();
			String[] column = {"Mitglied"};
			glm.setDataVector(vmitglieder, getColVector(column));
			RGname.setText((String)jcomboWahl.getSelectedItem());
			//RGmembers.setSortOrder(0,SortOrder.ASCENDING);
			RGmembers.validate();
			kollegenEntfernen();
		}
		
		private void kollegenEntfernen(){
			int i,lang;
			lang = glm.getDataVector().size();
			Vector vect = new Vector(vkollegen);
			//Vector vect = new Vector(klm.getDataVector());
			for(i=0;i<lang;i++){
					int x = vect.indexOf(glm.getDataVector().get(i));

					if(x>=0){
						////System.out.println("index = "+x+" entferne Kollege "+((Vector)vmitglieder.get(i)).get(0));
						vkollegen.removeElement(glm.getDataVector().get(i));
						//klm.removeRow(x);
					}
			}
			String[] kcolumn = {"Mitarbeiter"};
			vkollegen.trimToSize();
			klm.setDataVector((Vector) vkollegen.clone(), getColVector(kcolumn));
			//Source.setSortOrder(0,SortOrder.ASCENDING);
			Source.validate();
			////System.out.println("nach entfernen"+klm.getDataVector().size());
			
		}
		
		private void speichernHandeln(){
			boolean abbruch = false;
			if(RGmembers.getRowCount()==0){
				JOptionPane.showMessageDialog(null, "Eine RoogleGruppe ohne Mitglieder kann nicht abgespeichert werden!");
				return;
			}
			if(RGname.getText().equals("")){
				JOptionPane.showMessageDialog(null, "Eine RoogleGruppe braucht einen Gruppennamen!");
				return;
			}
			int anzahl =  gruppenname.length;
			int i;
			String setXname = RGname.getText(); 
			if(lneu){
				for(i=0;i<anzahl;i++){
					if(  gruppenname[i].trim().equals(setXname) ){
						abbruch = true;
						break;
					}
				}				
			}else{
				for(i=0;i<anzahl;i++){
					if( (gruppenname[i].trim().equals(setXname)) &&  (i != jcomboWahl.getSelectedIndex())){
						abbruch = true;
						break;
					}
				}				
			}
			if(abbruch){
				JOptionPane.showMessageDialog(null, "Die RoogleGruppe "+setXname+" ist bereits vorhanden.\n"+
				"Jeder Gruppen-Name darf nur einmal vorkommen");
				return;
			}
			
			String inigruppe = "",inimitglied="",inianzahl="",iniakt="";
			inianzahl = (!lneu ? Integer.valueOf(jcomboWahl.getItemCount()).toString() : Integer.valueOf(jcomboWahl.getItemCount()+1).toString() );
			////System.out.println(inianzahl);
			iniakt = (lneu ? 
						Integer.valueOf( Integer.valueOf(inianzahl)).toString() :  
						Integer.valueOf( jcomboWahl.getSelectedIndex()+1).toString() );
			for(i = 0; i < RGmembers.getRowCount();i++){
				if(i==0){
					inimitglied = inimitglied+RGmembers.getValueAt(i,0);
				}else{
					inimitglied = inimitglied+","+RGmembers.getValueAt(i,0);
				}
			}
			if(lneu){
				SystemConfig.UpdateIni("terminkalender.ini","RoogleEinstellungen", "RoogleAnzahlGruppen", Integer.valueOf(inianzahl).toString());
			}
			SystemConfig.UpdateIni("terminkalender.ini","RoogleEinstellungen", "RoogleNameGruppen"+iniakt, setXname);
			SystemConfig.UpdateIni("terminkalender.ini","RoogleEinstellungen", "RoogleFelderGruppen"+iniakt, inimitglied);
			SystemConfig.RoogleGruppen();
			if(lneu){
				comboFuellen(Integer.valueOf(iniakt)-1);
			}else{
				comboFuellen(iaktItem);
			}
			comboAuswerten();
			RGmembers.setEnabled(false);
			Source.setEnabled(false);
			jcomboWahl.setEnabled(true);
			RGname.setEnabled(false);
			knopfGedoense(new int[]{1,1,1,0,0, 0,0});
			lneu = false;
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if(arg0.getActionCommand().equals("comboaktion")){comboAuswerten();}
			if(arg0.getActionCommand().equals("take")){takeHandeln();}
			if(arg0.getActionCommand().equals("delete")){deleteHandeln();}			

			if(arg0.getActionCommand().equals("neu")){neuHandeln();}

			if(arg0.getActionCommand().equals("aendern")){aendernHandeln();}

			if(arg0.getActionCommand().equals("abbrechen")){abbrechenHandeln();}
						
			if(arg0.getActionCommand().equals("speichern")){speichernHandeln();}

			if(arg0.getActionCommand().equals("loeschen")){loeschenHandeln();}
			
		}
		
		private void comboFuellen(int akt){
			int i,j,lang;
			lang = SystemConfig.aRoogleGruppen.size();
			//String[] test = new String[lang];
			gruppenname = new  String[lang];
			mitglieder = new String[lang][];
			for(i=0;i<lang;i++){
				gruppenname[i] = (String)((ArrayList)SystemConfig.aRoogleGruppen.get(i).get(0)).get(0);
				mitglieder[i] = ((ArrayList<String[]>)SystemConfig.aRoogleGruppen.get(i).get(1)).get(0);

			}
			if(akt >= 0){
				jcomboWahl.removeAllItems();
				for(i=0;i<gruppenname.length;i++){
					jcomboWahl.addItem(gruppenname[i]);
				}
				jcomboWahl.setSelectedIndex(akt);
				RGname.setText((String) jcomboWahl.getSelectedItem());
			}
		}
		private void macheGruppenListModel(int mgl){
			if (mgl<0){
				return;
			}
			int lang = mitglieder[mgl].length;
			int i;

			vmitglieder.clear();
			Vector vec;
			for(i=0;i<lang;i++){
				vec = new Vector();
				vec.add(String.valueOf(mitglieder[mgl][i]));				
 				vmitglieder.add(vec.clone());
 			}
		}	
		private void macheKollegen(){
			int von = 0;
			int bis = ParameterLaden.vKKollegen.size();
			vkollegen = new Vector();
			for(von=1;von<bis;von++){
				Vector vec = new Vector();
				vec.add(String.valueOf(ParameterLaden.getMatchcode(von)));				
 				vkollegen.add(vec.clone());
 			}
			////System.out.println("Anzahl Kollegen nach f�llen = "+vkollegen.size());
		}
		private void aendernHandeln(){
			if(RGmembers.getRowCount()>0){
				knopfGedoense(new int[]{0,0,0,1,1, 1,1});
			}else{
				knopfGedoense(new int[]{0,0,0,1,1, 1,0});				
			}
			jcomboWahl.setEnabled(false);
			RGmembers.setEnabled(true);
			Source.setEnabled(true);
			RGname.setEnabled(true);
			RGname.requestFocus(true);
			saktItem = (String) jcomboWahl.getSelectedItem();
			iaktItem = jcomboWahl.getSelectedIndex();
			kollegenEntfernen();
			lneu = false;
		}
		private void neuHandeln(){
			lneu = true;
			knopfGedoense(new int[]{0,0,0,1,1, 1,1});
			RGmembers.setEnabled(true);
			String[] column = {"Mitglied"};			
			glm.setDataVector(new Vector(), getColVector(column));
			//RGmembers.setSortOrder(0,SortOrder.ASCENDING);
			RGmembers.validate();
			Source.setEnabled(true);
			macheKollegen();
			String[] kcolumn = {"Mitarbeiter"};
			klm.setDataVector((Vector) vkollegen.clone(), getColVector(kcolumn));
			Source.setEnabled(true);
			jcomboWahl.setEnabled(false);
			jcomboWahl.setSelectedItem("");
			RGname.setEnabled(true);
			RGname.setText("");
			RGname.requestFocus();
		}
		
		private void abbrechenHandeln(){
			macheKollegen();
			comboAuswerten();
			RGname.setEnabled(false);
			jcomboWahl.setEnabled(true);
			RGmembers.setEnabled(false);
			Source.setEnabled(false);
			knopfGedoense(new int[]{1,1,1,0,0, 0,0});
			kollegenEntfernen();
			lneu = false;
		}
		

		private void deleteHandeln(){
			int i = 0,lang=0,count=0,j=0;
			int [] select = RGmembers.getSelectedRows();
			Vector vec = new Vector();
			if (select.length == 0){
				JOptionPane.showMessageDialog(null,"Sie haben keine Gruppenmitglieder ausgewahlt");
				return;
			}else{
				lang = select.length;
				//Vector vect = new Vector(glm.getDataVector());
				for(i=0;i<lang;i++){
					String st = (String)RGmembers.getValueAt(select[i],0);
					klm.addRow(macheVektor(st));
					vec.add(st);
					////System.out.println("Selektiert = "+st);					
				}
				
				for(i=0;i<lang;i++){
					j = ((Vector)glm.getDataVector()).size();
					//System.out.println("Anzahl = "+j);
					for(count= 0;count<j;count++){
						if(((Vector)glm.getDataVector().get(count)).get(0).equals(vec.get(i))){
							////System.out.println("Treffer an "+count+ " = "+vec.get(i));
							glm.removeRow(count);
							break;
						}
					}
				}
			}
		}
		
		/***********************************************************************/	
		private void loeschenHandeln(){
			int aktSet = 0;
			int anzahlSets = jcomboWahl.getItemCount();
			if(anzahlSets==1){
				JOptionPane.showMessageDialog(null, "Diese Roogle-Gruppe ist die einzige Gruppe!\n"+
				"Die letzte Gruppe darf nicht gelöscht werden!");
				return;
			}	

			int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie dieses Gruppe wirlich löschen", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.YES_OPTION){
				aktSet = jcomboWahl.getSelectedIndex();
				int i,j;
				int iniSet = 0;
				String set = "";
				SystemConfig.UpdateIni("terminkalender.ini","RoogleEinstellungen", "RoogleAnzahlGruppen", Integer.valueOf(anzahlSets-1).toString());
				lspeichern = true;
				for(i=0;i<anzahlSets;i++){
					if(i != aktSet){
						//gruppenname[i] = (String)((ArrayList)SystemConfig.aRoogleGruppen.get(i).get(0)).get(0);
						//mitglieder[i] = ((ArrayList<String[]>)SystemConfig.aRoogleGruppen.get(i).get(1)).get(0);
						set = "";
						int lang =  mitglieder[i].length;
						for(j=0;j<lang;j++){
							set = set+(j>0 ? ","+mitglieder[i][j] : mitglieder[i][j]);
						}
						SystemConfig.UpdateIni("terminkalender.ini","RoogleEinstellungen", "RoogleNameGruppen"+(++iniSet), gruppenname[i]);
						SystemConfig.UpdateIni("terminkalender.ini","RoogleEinstellungen", "RoogleFelderGruppen"+(++iniSet), set);
					}
				}
				if(aktSet > 0){
					aktSet = aktSet-1;
				}
				SystemConfig.RoogleGruppen();
				//knopfGedoense(new int[]{1,1,1,0,0});
				lspeichern = false;
				lneu = false;
				comboFuellen((aktSet>0 ? aktSet-1 : aktSet));
				comboAuswerten();
				RGmembers.setEnabled(false);
				Source.setEnabled(false);
				jcomboWahl.setEnabled(true);
				RGname.setEnabled(false);
				knopfGedoense(new int[]{1,1,1,0,0, 0,0});

			}

		}
		/***********************************************************************/		
		private void takeHandeln(){
			int i = 0,lang=0,count=0,j=0;
			int [] select = Source.getSelectedRows();
			Vector vec = new Vector();
			if (select.length == 0){
				JOptionPane.showMessageDialog(null,"Sie haben keinen Mitarbeiter ausgewahlt");
				return;
			}else{
				lang = select.length;
				//Vector vect = new Vector(klm.getDataVector());
				for(i=0;i<lang;i++){
					String st = (String)Source.getValueAt(select[i],0);
					glm.addRow(macheVektor(st));
					vec.add(st);
				}
				for(i=0;i<lang;i++){
					j = ((Vector)klm.getDataVector()).size();
					for(count= 0;count<j;count++){
						if(((Vector)klm.getDataVector().get(count)).get(0).equals(vec.get(i))){
							klm.removeRow(count);
							break;
						}
					}
				}
				
			}
		}
		
		private Vector macheVektor(String strg){
			Vector vec = new Vector();
			//vec.add(new Boolean(bool));
			vec.add(String.valueOf(strg));
			return vec;
		}
		
		
		

}

class GruppenListModel extends DefaultTableModel{
	   public Class getColumnClass(int columnIndex) {
		   if(columnIndex>0){return Boolean.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
           //return (columnIndex == 0) ? Boolean.class : String.class;
       }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col == 0 ) {
	          return false;
	        } else {
	          return true;
	        }
	      }
}

class KollegenListModel extends DefaultTableModel{
	   public Class getColumnClass(int columnIndex) {
		   if(columnIndex>0){return Boolean.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
           //return (columnIndex == 0) ? Boolean.class : String.class;
       }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col == 0 ) {
	          return false;
	        } else {
	          return true;
	        }
	      }
}