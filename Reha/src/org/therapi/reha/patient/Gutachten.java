package org.therapi.reha.patient;

import generalSplash.RehaSplash;
import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import jxTableTools.TableTool;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import patientenFenster.GutachtenWahl;
import patientenFenster.KeinRezept;
import rechteTools.Rechte;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.IconListRenderer;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.ToolsDialog;

public class Gutachten extends JXPanel implements ActionListener, TableModelListener, PropertyChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8660918000056202078L;
	//public static Gutachten gutachten = null;
	JXPanel leerPanel = null;
	JXPanel vollPanel = null;
	JXPanel wechselPanel = null;
	
	
	public String aktPanel = "";
	public JButton[] gutbut = {null,null,null,null};
	public JLabel anzahlGutachten = null;
	public JXTable tabbericht = null;
	public MyGutachtenTableModel dtblm;


	public Gutachten(){
		super();
		//gutachten = this;
		setOpaque(false);
		setLayout(new BorderLayout());
		/********zuerst das Leere Panel basteln**************/
		leerPanel = new KeinRezept("noch keine Gutachten angelegt für diesen Patient");
		leerPanel.setName("leerpanel");
		leerPanel.setOpaque(false);
		
		/********dann das volle**************/	
		JXPanel allesrein = new JXPanel(new BorderLayout());
		allesrein.setOpaque(false);
		allesrein.setBorder(null);
		
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.00),0dlu",
		"0dlu,p,2dlu,p,2dlu,fill:0:grow(1.00),5dlu");
		CellConstraints cc = new CellConstraints();
		allesrein.setLayout(lay);
		
		wechselPanel = new JXPanel(new BorderLayout());
		wechselPanel.setOpaque(false);
		wechselPanel.setBorder(null);
		wechselPanel.add(leerPanel,BorderLayout.CENTER);
		aktPanel = "leerPanel";
		
		allesrein.add(getToolbar(),cc.xy(2, 2));
		//allesrein.add(getTabelle(),cc.xy(2, 4));
		allesrein.add(wechselPanel,cc.xy(2, 6));

		add(JCompTools.getTransparentScrollPane(allesrein),BorderLayout.CENTER);
		validate();
		new Thread(){
			public void run(){
				new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
				
						// TODO Auto-generated method stub
						vollPanel = new JXPanel();
						FormLayout vplay = new FormLayout("5dlu,fill:0:grow(1.00),5dlu","13dlu,fill:0:grow(1.00),5dlu");
						//FormLayout vplay = new FormLayout("5dlu,fill:0:grow(1.00),5dlu","5dlu,fill:0:grow(1.00),5dlu");
						CellConstraints vpcc = new CellConstraints();
						vollPanel.setLayout(vplay);
						vollPanel.setOpaque(false);
						vollPanel.setBorder(null);
						anzahlGutachten = new JLabel("Anzahl sozialmed. Gutachten: 0");
						vollPanel.add(anzahlGutachten,vpcc.xy(2,1));
						vollPanel.add(getGutachtenTbl(),vpcc.xy(2,2));
			
						return null;

					}
					
				}.execute();
			}
		}.start();
		
		

	}
	private JXPanel getGutachtenTbl(){
		JXPanel dummypan = new JXPanel(new BorderLayout());
		dummypan.setOpaque(false);
		dummypan.setBorder(null);
		dtblm = new MyGutachtenTableModel();
		String[] column = 	{"ID","Titel","Verfasser","erstellt","Empfänger","letzte Änderung","",""};
		dtblm.setColumnIdentifiers(column);
		tabbericht = new JXTable(dtblm);
		//tabbericht.setEditable(true);
		tabbericht.setSortable(false);
		tabbericht.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getClickCount()==2){
					if(!Rechte.hatRecht(Rechte.Gutachten_editvoll, true)){
						return;
					}
					// hier pr�fen welcher Berichtstyp und dementsprechend das Berichtsfenster �ffnen
					///neuanlageRezept(false,"");
					int wahl = tabbericht.getSelectedRow();
					if(wahl < 0){
						return;
					}
					String verfas = (String)dtblm.getValueAt(wahl, 2); 
					if( verfas.toUpperCase().contains("REHAARZT") ||
							verfas.toUpperCase().contains("REHA-ARZT")	){
						doBerichtEdit();						
					}
				}
			}
		});
/*
		0		"berichtid," +
		1		"bertitel," +
		2		"verfasser," +
		3		"DATE_FORMAT(erstelldat,'%d.%m.%Y') AS derstelldat," +
		4		"empfaenger," +
		5		"DATE_FORMAT(editdat,'%d.%m.%Y') AS deditdat,
		6		"empfid",
*/
		tabbericht.getColumn(0).setMinWidth(50);
		tabbericht.getColumn(0).setMaxWidth(50);
		tabbericht.getColumn(1).setMinWidth(140);
		tabbericht.getColumn(3).setMaxWidth(80);
		tabbericht.getColumn(5).setMaxWidth(80);
		tabbericht.getColumn(6).setMinWidth(0);
		tabbericht.getColumn(6).setMaxWidth(0);
		tabbericht.getColumn(7).setMinWidth(0);
		tabbericht.getColumn(7).setMaxWidth(0);
		tabbericht.validate();

		JScrollPane jscr = JCompTools.getTransparentScrollPane(tabbericht);
		jscr.validate();
		dummypan.add(jscr,BorderLayout.CENTER);
		return dummypan;
	}
//	Gutachten.gutachten.aktualisiereGutachten(datFunk.sHeute(),(empf.contains("DRV") ? "DRV E-Bericht" : "GKV E-Bericht"),empf,"Rehaarzt",berichtid);
	
	public void aktualisiereGutachten(String editdat,String bertype,String empf,String verfasser,int berid,String patintern){
		String cmd = "update berhist set berichttyp='"+bertype+"', empfaenger='"+empf+"', editdat='"+
		DatFunk.sDatInSQL(editdat)+"', verfasser='"+verfasser+"' where berichtid='"+berid+"'";
		SqlInfo.sqlAusfuehren(cmd);
		int row = tabbericht.getSelectedRow();
		if(! Reha.thisClass.patpanel.aktPatID.equals(patintern)){
			JOptionPane.showMessageDialog(null, "Der aktuelle Patient und das zu speichernde Gutachten passen nicht zusammen...");
			return;
		}
		if( dtblm.getValueAt(row,0).equals(Integer.toString(berid)) ){
			dtblm.setValueAt(bertype, row, 1);
			dtblm.setValueAt(verfasser, row, 2);
			dtblm.setValueAt(empf, row, 4);	
			dtblm.setValueAt(editdat, row, 5);
		}
		
	}
	public void neuesGutachten(String berid,
			String bertype,
			String verfasser,
			String erstellt,
			String empfang,
			String patintern,
			String bertitel){
		Vector<String> xvec = new Vector<String>();
		xvec.add(berid);
		xvec.add(bertype);
		xvec.add(verfasser);
		xvec.add(erstellt);
		xvec.add(empfang);
		xvec.add("");
		xvec.add("0");
		xvec.add(patintern);
		if(Reha.thisClass.patpanel.aktPatID.equals(patintern)){
			dtblm.addRow((Vector<?>) xvec.clone());
			tabbericht.setRowSelectionInterval(tabbericht.getRowCount()-1, tabbericht.getRowCount()-1);
		}
		Reha.thisClass.patpanel.getTab().setTitleAt(4,macheHtmlTitel(tabbericht.getRowCount(),"Gutachten"));
		anzahlGutachten.setText("Anzahl Gutachten: "+Integer.toString(tabbericht.getRowCount()));
	}
	public void holeGutachten(String patint,String rez){
		/**********/
					final String xpatint = patint;
					final String xrez_nr = rez;

					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							try{
							//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
							Vector<Vector<String>> vec = SqlInfo.holeSaetze("berhist", 
									"berichtid," +
									"berichttyp," +
									"verfasser," +
									"DATE_FORMAT(erstelldat,'%d.%m.%Y') AS derstelldat," +
									"empfaenger," +
									"DATE_FORMAT(editdat,'%d.%m.%Y') AS deditdat," +
									"empfid,"+
									"bertitel",
									"pat_intern='"+xpatint+"' ORDER BY berichtid DESC", Arrays.asList(new String[]{}));
							int anz = vec.size();
							dtblm.setRowCount(0);
							for(int i = 0; i < anz;i++){
								if(i==0){
									//dtblm.setRowCount(0);						
								}
								if(((String)((Vector<?>)vec.get(i)).get(2)).toUpperCase().contains("REHAARZT") ||
										((String)((Vector<?>)vec.get(i)).get(2)).toUpperCase().contains("REHA-ARZT")	){
									dtblm.addRow((Vector<?>)vec.get(i));							
								}
							}
							anz = dtblm.getRowCount();
							Reha.thisClass.patpanel.getTab().setTitleAt(4,macheHtmlTitel(anz,"Gutachten"));
							anzahlGutachten.setText("Anzahl sozialmed. Gutachten: "+anz);
							if(anz > 0){
								setzeGutachtenPanelAufNull(false);
								if(xrez_nr.equals("")){
									tabbericht.setRowSelectionInterval(0,0);							
								}else{
									for(int i = 0;i<anz;i++){
										if(((String)dtblm.getValueAt(i,1)).contains(xrez_nr)){
											tabbericht.setRowSelectionInterval(i,i);
											break;
										}
									}
								}

								//int anzeigen = -1;
								wechselPanel.revalidate();
								wechselPanel.repaint();					
							}else{
								setzeGutachtenPanelAufNull(true);
								gutbut[0].setEnabled(true);
								wechselPanel.revalidate();
								wechselPanel.repaint();
								dtblm.setRowCount(0);
							}
							}catch(Exception ex){
								ex.printStackTrace();
							}
							
							return null;
						}
						
					}.execute();
					
					
			}
	private String macheHtmlTitel(int anz,String titel){
		String ret = titel+" - "+Integer.toString(anz);
		return ret;
	}
	
	
	public JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);

		gutbut[0] = new JButton();
		gutbut[0].setIcon(SystemConfig.hmSysIcons.get("neu"));
		gutbut[0].setToolTipText("Neues Gutachten erstellen");
		gutbut[0].setActionCommand("gutneu");
		gutbut[0].addActionListener(this);		
		jtb.add(gutbut[0]);

		gutbut[1] = new JButton();
		gutbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		gutbut[1].setToolTipText("Bestehendes Gutachten ändern/editieren");
		gutbut[1].setActionCommand("gutedit");
		gutbut[1].addActionListener(this);		
		jtb.add(gutbut[1]);

		gutbut[2] = new JButton();
		gutbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		gutbut[2].setToolTipText("Gutachten löschen");
		gutbut[2].setActionCommand("gutdelete");
		gutbut[2].addActionListener(this);		
		jtb.add(gutbut[2]);
		
		jtb.addSeparator(new Dimension(30,0));
		
		gutbut[3] = new JButton();
		gutbut[3].setIcon(SystemConfig.hmSysIcons.get("tools"));
		gutbut[3].setToolTipText("Werkzeuge für Gutachte");
		gutbut[3].setActionCommand("guttools");
		gutbut[3].addActionListener(this);		
		jtb.add(gutbut[3]);
		for(int i = 0; i < 4;i++){
			gutbut[i].setEnabled(false);
		}
		return jtb;
	}
	public void setzeGutachtenPanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				for(int i = 1; i < 4;i++){
					gutbut[i].setEnabled(false);
				}
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				wechselPanel.remove(leerPanel);
				wechselPanel.add(vollPanel);
				aktPanel = "vollPanel";
				for(int i = 0; i < 4;i++){
					gutbut[i].setEnabled(true);
				}
			}
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("gutneu")){
			if(!Rechte.hatRecht(Rechte.Gutachten_anlegen, true)){
				return;
			}
			final JComponent comp = ((JComponent)arg0.getSource());
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						JRtaTextField tf = new JRtaTextField("nix",false);
						//System.out.println("in GutachtenWahl");
						GutachtenWahl gwahl = new GutachtenWahl( (Point)comp.getLocationOnScreen(),tf,"Neues Gutachten erstellen" );
						//System.out.println("Aufruf des Focus***********");
						gwahl.setzeFocus();
						gwahl.setVisible(true);
						//gwahl.setModal(true);
						
						//System.out.println("Der Rückgabewert der Auswahl = "+tf.getText() );
						if(tf.getText().equalsIgnoreCase("ebericht")){
							Reha.thisClass.progLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,-1,"E-Bericht",true,"",-1); 
							//ProgLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,-1,"E-Bericht",true,"" );			
							return null;
						}
						if(tf.getText().equalsIgnoreCase("nachsorge")){
							Reha.thisClass.progLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,-1,"Nachsorge",true,"",-1 );
							//ProgLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,-1,"Nachsorge",true,"" );			
							return null;
						}
						gwahl = null;
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					return null;
				}
			}.execute();
			return;
		}
		if(cmd.equals("gutedit")){
			if(!Rechte.hatRecht(Rechte.Gutachten_editvoll, true)){
				return;
			}
			doBerichtEdit();
			return;
		}
		if(cmd.equals("gutdelete")){
			if(aktPanel.equals("leerPanel")){
				JOptionPane.showMessageDialog(null,"Oh Herr laß halten...\n\n"+
						"....und welches der nicht vorhandenen Gutachten möchten Sie bitteschön löschen....");
				return;
			}
			if(!Rechte.hatRecht(Rechte.Gutachten_delete, true)){
				return;
			}
			int currow = tabbericht.getSelectedRow();
			//int anzrow = tabbericht.getRowCount();
			if(currow == -1){
				JOptionPane.showMessageDialog(null,"Kein Gutachten zum -> löschen <- ausgewählt");
				return;
			}
			String berichtid = (String)tabbericht.getValueAt(currow, 0);
			int frage = JOptionPane.showConfirmDialog(null,"Wollen Sie das Gutachten mit der ID:"+berichtid+" wirklich löschen?","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
			if(frage == JOptionPane.NO_OPTION){
				return;
			}
			String sqlcmd = "delete from berhist where berichtid='"+berichtid+"'";
			new ExUndHop().setzeStatement(sqlcmd);
			sqlcmd = "delete from bericht2 where berichtid='"+berichtid+"'";
			new ExUndHop().setzeStatement(sqlcmd);
			sqlcmd = "delete from bericht2ktl where berichtid='"+berichtid+"'";
			new ExUndHop().setzeStatement(sqlcmd);
			
			currow = TableTool.loescheRow(tabbericht, Integer.valueOf(currow));
			int uebrig = tabbericht.getRowCount();
			
			anzahlGutachten.setText("Anzahl Gutachten: "+Integer.toString(uebrig));
			Reha.thisClass.patpanel.getTab().setTitleAt(4,macheHtmlTitel(uebrig,"Gutachten"));
			if(uebrig <= 0){
				holeGutachten(Reha.thisClass.patpanel.patDaten.get(29),"");
			}else{
			}
			
			return;
		}
		if(cmd.equals("guttools")){
			new ToolsDlgGutachten("",gutbut[3].getLocationOnScreen());
			return;
		}

		
	}
	private void doBerichtEdit(){
		int row = tabbericht.getSelectedRow();
		//System.out.println("in doBerichtEdit");
		if(row < 0){
			//System.out.println("keine Zeile vorhanden also return");
			return;
		}
		String bertyp = (String) tabbericht.getValueAt(row,1);
		int berid = Integer.parseInt(tabbericht.getValueAt(row,0).toString());
		String berempfaenger = (String) tabbericht.getValueAt(row,4);
		Reha.thisClass.progLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,berid,bertyp,false,berempfaenger,-1 );
		//ProgLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,berid,bertyp,false,berempfaenger );
		//ProgLoader.InternalGut2();
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	class GutachtenPanel extends JXPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5989619503530843816L;
		ImageIcon hgicon;
		int icx,icy;
		AlphaComposite xac1 = null;
		AlphaComposite xac2 = null;		
		GutachtenPanel(){
			super();
			setOpaque(false);
			hgicon = SystemConfig.hmSysIcons.get("historie"); 
			//hgicon = new ImageIcon(Reha.proghome+"icons/ChipKarte.png");
			//hgicon = new ImageIcon(Reha.proghome+"icons/Chip.png");
			icx = hgicon.getIconWidth()/2;
			icy = hgicon.getIconHeight()/2;
			xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.075f); 
			xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);			
			
		}

		//@Override
		public void paintComponent( Graphics g ) { 
			super.paintComponent( g );
			Graphics2D g2d = (Graphics2D)g;
			
			if(hgicon != null){
				g2d.setComposite(this.xac1);
				//g2d.drawImage(hgicon.getImage(), 0 , 0,null);
				g2d.drawImage(hgicon.getImage(), (getWidth()/3)-(icx+20) , (getHeight()/2)-(icy-40),null);
				g2d.setComposite(this.xac2);
			}
		}
	}
	class MyGutachtenTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==1){
				   //return JLabel.class;}
			   		return String.class;}
			   else{
				   return String.class;
			   }
	        //return (columnIndex == 0) ? Boolean.class : String.class;
	    }

		    public boolean isCellEditable(int row, int col) {
		        //Note that the data/cell address is constant,
		        //no matter where the cell appears onscreen.
		    	return false;
		    	/*
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
		        */
		      }
		   
	}
	@SuppressWarnings("unused")
	private  void doArztBausteine(){
		new LadeProg(Reha.proghome+"ArztBaustein.jar "+
				Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");	
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				RehaSplash rspl = new RehaSplash(null,"Textbaustein-Editor laden....dieser Vorgang kann einige Sekunden dauern...");
				long zeit = System.currentTimeMillis();
				while(true){
					Thread.sleep(20);
					if(System.currentTimeMillis()-zeit > 2000){
						break;
					}
				}
				rspl.dispose();
				return null;
			}
		}.execute();
	}
	private void doBerichtCopy(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					JRtaTextField tf = new JRtaTextField("nix",false);
					//System.out.println("in GutachtenWahl");
					Point pt = (Point)gutbut[3].getLocationOnScreen();
					pt.x = pt.x+150;
					GutachtenWahl gwahl = new GutachtenWahl(pt ,tf,"<html><font color='#ffff66'>Stammdaten auf neues Gutachten übertragen</font></html>" );
					//System.out.println("Aufruf des Focus***********");
					gwahl.setzeFocus();
					gwahl.setVisible(true);
					//gwahl.setModal(true);
					
					//System.out.println("Der Rückgabewert der Auswahl = "+tf.getText() );
					if(tf.getText().equalsIgnoreCase("ebericht")){
						int row = tabbericht.getSelectedRow();
						if(row < 0){
							JOptionPane.showMessageDialog(null,"Kein Bericht für Datenübernahme ausgewählt");
							return null;
						}
						int uebernahme = Integer.parseInt(tabbericht.getValueAt(row,0).toString());
						Reha.thisClass.progLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,-1,"E-Bericht",true,"",uebernahme); 
						return null;
					}
					if(tf.getText().equalsIgnoreCase("nachsorge")){
						int row = tabbericht.getSelectedRow();
						if(row < 0){
							JOptionPane.showMessageDialog(null,"Kein Bericht für Datenübernahme ausgewählt");
							return null;
						}
						int uebernahme = Integer.parseInt(tabbericht.getValueAt(row,0).toString());
						Reha.thisClass.progLoader.GutachenFenster(1,Reha.thisClass.patpanel.aktPatID ,-1,"Nachsorge",true,"",uebernahme );
						return null;
					}
					gwahl = null;
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				return null;
			}
		}.execute();
		
		
	}
	private void doPdfStarten(int art){
		int row = tabbericht.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null,"Kein Bericht für Datenübernahme ausgewählt");
			return;
		}
		new LadeProg(Reha.proghome+"PDFLoader.jar "+
				Integer.toString(art)+" "+
				tabbericht.getValueAt(row, 0).toString()+" "+
				SystemConfig.PDFformularPfad+" "+
				Reha.proghome+"ini/"+Reha.aktIK+"/fremdprog.ini"+" "+
				Reha.thisClass.patpanel.patDaten.get(29)+" "+
				Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
	}

	class ToolsDlgGutachten{
		public ToolsDlgGutachten(String command,Point pt){
			Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
			icons.put("Stammdaten in neues Gutachten übertragen",SystemConfig.hmSysIcons.get("neu"));
			icons.put("ASP-Patienten-Information",SystemConfig.hmSysIcons.get("info2"));
			icons.put("ASP-Verordnung",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("ASP-Anwesenheitsliste",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("ASP-Zustimmung des Patienten",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("IRENA-Patienten-Information",SystemConfig.hmSysIcons.get("info2"));
			icons.put("IRENA-Verordnung",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("IRENA-Anwesenheitsliste",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("IRENA-Zustimmung des Patienten",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("IRENA-Beginnmitteilung",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("BfA - Reha-Verlängerung",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("DRV-Wiedereingliederung Pat./AG-Infos",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("DRV-Wiedereingliederung",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("DRV-Wiedereingliederung Beginn > 2 Wo.",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("BfA - AHB-Aufnahmemitteilung",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("ASP-Rechnungsformular",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("IRENA-Rechnungsformular",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("Reha-Anwesenheitsbescheinigung",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("GKV-Verlängerungsantrag",SystemConfig.hmSysIcons.get("pdf"));
			icons.put("LVA-Entlassmitteilung",SystemConfig.hmSysIcons.get("neu"));

			
			JList list = new JList(	new Object[] {"Stammdaten in neues Gutachten übertragen", 
					"ASP-Patienten-Information","ASP-Verordnung",
					"ASP-Anwesenheitsliste","ASP-Zustimmung des Patienten",
					"IRENA-Patienten-Information","IRENA-Verordnung","IRENA-Anwesenheitsliste","IRENA-Zustimmung des Patienten",
					"IRENA-Beginnmitteilung",
					"BfA - Reha-Verlängerung","DRV-Wiedereingliederung Pat./AG-Infos","DRV-Wiedereingliederung",
					"DRV-Wiedereingliederung Beginn > 2 Wo.",
					"BfA - AHB-Aufnahmemitteilung","ASP-Rechnungsformular","IRENA-Rechnungsformular",
					"Reha-Anwesenheitsbescheinigung","GKV-Verlängerungsantrag","LVA-Entlassmitteilung"});
					
			
			list.setCellRenderer(new IconListRenderer(icons));	
			Reha.toolsDlgRueckgabe = -1;
			ToolsDialog tDlg = new ToolsDialog(Reha.thisFrame,"Werkzeuge: Gutachten / E-Bericht",list);
			tDlg.setPreferredSize(new Dimension(300,200+
					((Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton")? 25 : 0) ));
			tDlg.setLocation(pt.x-70,pt.y+30);
			tDlg.pack();
			tDlg.setModal(true);
			tDlg.activateListener();
			tDlg.setVisible(true);
			switch(Reha.toolsDlgRueckgabe){
			case 0:
				if(!Rechte.hatRecht(Rechte.Gutachten_copy, true)){return;}
				doBerichtCopy();
				break;
			case 1:
				//ASP-Patienteninformation
				doPdfStarten(90);
				break;
			case 2:
				//ASP-Verordnung
				doPdfStarten(1);
				break;
			case 3:
				//ASP-Anwesenheitsliste
				doPdfStarten(2);
				break;
			case 4:
				//ASP-Zustimmungserklärung				
				doPdfStarten(3);
				break;
			case 5:
				//Irena-Patienteninformation
				doPdfStarten(91);
				break;
			case 6:
				//Irena-Verordnung
				doPdfStarten(4);
				break;
			case 7:
				//Irena-Anwesenheitsliste
				doPdfStarten(5);
				break;
			case 8:
				//Irena-Zustimmungserklärung
				doPdfStarten(6);
				break;
			case 9:
				//Irena-Beginnmitteilung
				doPdfStarten(7);
				break;
			case 10:
				//BfA-Rehaverlängerung
				doPdfStarten(8);
				break;
			case 11:
				//DRV-Wiedereingliederung Pat/AG Info
				doPdfStarten(9);
				break;
			case 12:
				//DRV-Wiedereingliederung
				doPdfStarten(10);
				break;
			case 13:
				//DRV-Wiedereingliederung Beginn später als 2 Wo.
				doPdfStarten(16);
				break;
			case 14:
				//BfA-AHB-Aufnahmemitteilung
				doPdfStarten(11);
				break;
			case 15:
				//ASP-Rechnung
				doPdfStarten(12);
				break;
			case 16:
				//IRENA-Rechnung
				doPdfStarten(13);
				break;
			case 17:
				//Reha-Anwesenheitsbescheinigung
				doPdfStarten(14);
				break;
			case 18:
				doPdfStarten(15);
				break;
			case 19:
				//LVA-Entlassmitteilung
				try{
					new LadeProg(Reha.proghome+"LVAEntlass.jar "+
							" "+Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini"+" "+
							Reha.thisClass.patpanel.vecaktrez.get(1)+" "+
							Reha.proghome+"ini/"+Reha.aktIK+"/fremdprog.ini");
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
	
			}

			tDlg = null;
		}
	}

	

}
