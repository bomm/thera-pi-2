package patientenFenster;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.IntegerTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.RehaSmartDialog;

public class KVKRohDaten extends RehaSmartDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2430523963874902470L;
	JButton knopf1 = null;
	JButton knopf2 = null;
	RohKeyListener kl = null;
	PatNeuanlage thisPat = null;
	String gerGeboren = "";

	public KVKRohDaten(PatNeuanlage pat){
		super(null, "KVKDaten");
		thisPat = pat;

		getSmartTitledPanel().setTitle("Daten der Versicherungskarte");


		getSmartTitledPanel().getContentContainer().setName("KVKDaten");
	    setName("KVKDaten");
		

		JXRohDaten jroot = new JXRohDaten();
		jroot.setLayout(new BorderLayout());
		jroot.setBackground(Color.WHITE);

		jroot.setBackgroundPainter(Reha.thisClass.compoundPainter.get("KVKRohDaten"));
		jroot.setBorder(null);
		jroot.add(getTextPanel(),BorderLayout.CENTER);
		jroot.add(getButtonPanel(),BorderLayout.SOUTH);

		getSmartTitledPanel().setContentContainer(jroot);		
		//setContentPane(jroot);
		setSize(450,400);
		
	}
	class JXRohDaten extends JXPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4983579331335529L;
		ImageIcon hgicon;
		int icx,icy;
		AlphaComposite xac1 = null;
		AlphaComposite xac2 = null;	
		
		public JXRohDaten(){
			super();
			hgicon = SystemConfig.hmSysIcons.get("kvkarte"); 
			icx = hgicon.getIconWidth()/2;
			icy = hgicon.getIconHeight()/2;
			xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.25f); 
			xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);			
		}


		@Override
		public void paintComponent( Graphics g ) { 
			super.paintComponent( g );
			Graphics2D g2d = (Graphics2D)g;
			
			if(hgicon != null){
				g2d.setComposite(this.xac1);
				g2d.drawImage(hgicon.getImage(), 0 , 0,null);
				g2d.setComposite(this.xac2);
			}
		}
	}
	private JXPanel getButtonPanel(){
		JXPanel but = new JXPanel(new BorderLayout());
		but.setOpaque(false);
		knopf1 = new JButton("übernehmen");
		knopf1.setActionCommand("uebernehmen");
		knopf1.setName("uebernehmen");
		knopf1.addActionListener(this);
		knopf1.addKeyListener(new RohKeyListener());
		knopf2 = new JButton("abbrechen");
		knopf2.setActionCommand("abbrechen");
		knopf2.setName("abbrechen");
		knopf2.addActionListener(this);
		knopf2.addKeyListener(new RohKeyListener());
		knopf2.setMnemonic(KeyEvent.VK_A);

		FormLayout lay = new FormLayout("fill:0:grow(0.50), 60dlu,15dlu, 60dlu,fill:0:grow(0.50) ",
			       //1.   2.  3.   4.   5.   
					"7dlu, p, 7dlu");
		CellConstraints cc = new CellConstraints();
		but.setLayout(lay);
		but.add(knopf1,cc.xy(2,2));
		but.add(knopf2,cc.xy(4,2));
		return but;
	}	
		
	private JXPanel getTextPanel(){
		JXPanel tpan = new JXPanel(new BorderLayout());
		//tpan.setBackground(Color.WHITE);
		tpan.setOpaque(false);
		tpan.setPreferredSize(new Dimension(450,400));
		tpan.setBorder(BorderFactory.createEmptyBorder(0, 10,0, 10));
		String geboren = SystemConfig.hmKVKDaten.get("Geboren");
		gerGeboren = geboren.substring(0, 2)+"."+geboren.substring(2, 4)+"."+geboren.substring(4); 
		String gueltig = SystemConfig.hmKVKDaten.get("Gueltigkeit");
		String[] monate = {"Januar","Februar","März","April","Mai","Juni","Juli",
				"August","September","Oktober","November","Dezember"}; 
		 String initialText = "<html>\n" +
		 /*
		 "<font size=+2>"+
		 "<font color=blue>"+
         "- Daten der Versicherungskarte -<br><br>" +
         "</font>"+
         */
         "<table align=center>\n"+
         //         
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Krankenkasse:"+
		 "</font>"+         
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Krankenkasse")+
         "</font>"+
         "</td></tr>\n" +
         //         
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "IK der Krankenkasse:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         "10"+SystemConfig.hmKVKDaten.get("Kassennummer")+
         "</font>"+         
         "</td></tr>\n" +
         //         
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Status:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Statusext")+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Versichertennummer:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Versichertennummer")+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Vorname:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Vorname")+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Nachname:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Nachname")+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Geboren:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         gerGeboren+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Strasse:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Strasse")+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Postleitzahl:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Plz")+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Ort:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=red>"+
         "<font size=+1>"+
         SystemConfig.hmKVKDaten.get("Ort")+
         "</font>"+         
         "</td></tr>\n" +
         //
         "<tr>\n" +
         "<td align=right>\n" +
		 "<font size=+1>"+
         "Karte gültig bis:"+
		 "</font>"+
         "</td>\n" +
         "<td>\n" +
         "<font color=blue><b>"+
         "<font size=+1>"+
         monate[IntegerTools.trailNullAndRetInt(gueltig.substring(0, 2))-1]+" - 20"+gueltig.substring(2)+
         "</b>"+
         "</font>"+         
         "</td></tr>\n" +

         "</table>\n"+
		 "</html>";
		JLabel jlbl = new JLabel(initialText);
		jlbl.setOpaque(false);
		tpan.add(jlbl,BorderLayout.CENTER);
		return tpan;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String comm = arg0.getActionCommand();
		if(comm.equals("uebernehmen")){
			werteUebernehmen();
			aufraeumen();
			this.dispose();
		}
		if(comm.equals("abbrechen")){
			aufraeumen();
			this.dispose();
		}
		
	}
	public void aufraeumen(){
		knopf1.removeActionListener(getInstance());
		knopf1.removeKeyListener(this);
		knopf2.removeActionListener(getInstance());
		knopf2.removeKeyListener(this);
		knopf1 = null;
		knopf2 = null;
	}
	/*
	SystemConfig.hmKVKDaten.put("Krankenkasse", hmdaten[0].split("=")[1]);
	SystemConfig.hmKVKDaten.put("Kassennummer", hmdaten[1].split("=")[1]);
	SystemConfig.hmKVKDaten.put("Kartennummer", hmdaten[2].split("=")[1]);
	SystemConfig.hmKVKDaten.put("Versichertennummer", hmdaten[3].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Status", hmdaten[4].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Statusext", hmdaten[5].split("=")[1]);
	SystemConfig.hmKVKDaten.put("Vorname", hmdaten[6].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Nachname", hmdaten[7].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Geboren", hmdaten[8].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Strasse", hmdaten[9].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Land", hmdaten[10].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Plz", hmdaten[11].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Ort", hmdaten[12].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Gueltigkeit", hmdaten[13].split("=")[1]);			
	SystemConfig.hmKVKDaten.put("Checksumme", hmdaten[14].split("=")[1]);
	*/
	private void werteUebernehmen(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				
				if(!thisPat.inNeu ){
					String test1 = thisPat.jtf[11].getText().trim();
					String test2 = gerGeboren.trim();
					if(!test1.equals(test2)){
						String dlg = "Das Geburtsdatum des aktuellen Patienten und das Geburtsdatum der KV-Karte stimmen nicht überein!\n"+
						"übertragen wird daher (sicherheitshalber) --> nix!";
			        	JOptionPane.showMessageDialog(null, dlg);
			        		return null;
					}
				}
 
				//boolean inkassenstamm = true;
				String[] list = {};
				//Für den Echtbetrieb!
				String kassik = "10"+SystemConfig.hmKVKDaten.get("Kassennummer").trim();
				Vector<String> vec = SqlInfo.holeSatz("kass_adr", "kassen_nam1,ik_kasse,id", "ik_kasse='"+kassik+"'", Arrays.asList(list));

				if(vec.size()==0){
					Vector<String> vec2 = SqlInfo.holeSatz("ktraeger", " * ", "ikkasse='"+kassik+"'", Arrays.asList(list));
					if(vec2.size() > 0){
						int rueck = kasseAnlegen(vec2);
						if(rueck >= 0){
							//kasse soll angelegt werden
							//kassenid setzen
							thisPat.jtf[34].setText(Integer.toString(rueck));
						}else{
							JOptionPane.showMessageDialog(null, "Die Krankenkasse muß manuell angelegt werden!\n\n"+
									"Bitte informieren Sie den Systemadministrator");
						}
					}else{
						JOptionPane.showMessageDialog(null, "Die Krankenkasse ist weder im Kassenstamm noch in der Kostenträgerdatei vorhanden!\n"+
								"Die Kasse muß deshalb manuell angelegt werden!\n\n"+
								"Bitte informieren Sie den Systemadministrator");
					}
				}else{
					//Kasse existiert!!! 
					//kassenid setzen
					thisPat.jtf[34].setText(vec.get(2));
				}
				thisPat.jtf[12].setText(SystemConfig.hmKVKDaten.get("Krankenkasse"));
				thisPat.jtf[13].setText("10"+SystemConfig.hmKVKDaten.get("Kassennummer"));
				thisPat.jtf[14].setText(SystemConfig.hmKVKDaten.get("Versichertennummer"));
				thisPat.jtf[15].setText(SystemConfig.hmKVKDaten.get("Statusext"));
				
				thisPat.jtf[2].setText(SystemConfig.hmKVKDaten.get("Nachname").toUpperCase());
				thisPat.jtf[3].setText(SystemConfig.hmKVKDaten.get("Vorname").toUpperCase());
				thisPat.jtf[4].setText(SystemConfig.hmKVKDaten.get("Strasse").toUpperCase());
				thisPat.jtf[5].setText(SystemConfig.hmKVKDaten.get("Plz").toUpperCase());				
				thisPat.jtf[6].setText(SystemConfig.hmKVKDaten.get("Ort").toUpperCase());				
				thisPat.jtf[11].setText(gerGeboren);

				return null;
			}
			
		}.execute();
	}
	private int kasseAnlegen(Vector<String> vec){
		int id = -1;
		String ikkasse = vec.get(0);
		String ikktraeger = vec.get(1);
		String iknutzer = vec.get(4);
		String ikdaten = vec.get(3);
		String ikpapier = vec.get(2);
		String email = vec.get(11);
		String name1 = vec.get(5);
		String name2 = vec.get(6);
		String plz = vec.get(8);
		String ort = vec.get(9);
		String strasse = vec.get(10);

		if(ikdaten.trim().equals("")){
			ikdaten = SqlInfo.holeEinzelFeld("select ikdaten from ktraeger where ikkasse='"+ikktraeger+"' LIMIT 1");
		}
		if(ikpapier.trim().equals("")){
			ikpapier = SqlInfo.holeEinzelFeld("select ikpapier from ktraeger where ikkasse='"+ikktraeger+"' LIMIT 1");
		}
		if(email.trim().equals("") && (!ikdaten.trim().equals(""))){
			email = SqlInfo.holeEinzelFeld("select email from ktraeger where ikkasse='"+ikdaten+"' LIMIT 1");
		}
		
		String cmd = "<html><b>Die Kasse ist im aktuellen Kassenstamm nicht enthalten!<br><br>"+
		"Die Kasse wurde jedoch in der Kostenträgerdatei gefunden!"+"<br><br><font color=blue>"+
		name1+"<br>"+name2+"<br>"+strasse+"<br>"+plz+" "+ort+"<br><br></font></b>"+
		"IK-Kasse: "+ikkasse+"<br>"+
		"IK-Kostenträger: "+ikktraeger+"<br>"+
		"IK-Nutzer für Entschl.: "+iknutzer+"<br>"+
		"IK-Datenannahmestelle: "+ikdaten+"<br>"+
		"Email Datenannahmest.: "+email+"<br>"+
		"IK-Papierannahmestelle: "+ikpapier+"<br><br>"+
		"<b>Soll die Kassen mit diesen Daten in den Kassenstamm übernommen werden?</b><br><br></html>";
		int anfrage = JOptionPane.showConfirmDialog(null, cmd, "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);

		if(anfrage == JOptionPane.YES_OPTION){
			try{
				  String[] list = new String[SystemPreislisten.hmPreisGruppen.get("Common").size()];
				  for(int i = 0; i < list.length;i++){
					  list[i] = SystemPreislisten.hmPreisGruppen.get("Common").get(i);
				  }
				 String value = (String) JOptionPane.showInputDialog(
				                   null,
				                   "Bitte wählen Sie die zutreffende Preisgruppe aus",
				                   "Title",
				                   JOptionPane.QUESTION_MESSAGE,
				                   null,
				                   list,        // selection values
				                   SystemPreislisten.hmPreisGruppen.get("Common").get(0));      //
				String pg =  Integer.toString( SystemPreislisten.hmPreisGruppen.get("Common").indexOf(value)+1 );
				id = SqlInfo.holeId("kass_adr", "kmemo");
				StringBuffer buf = new StringBuffer();
				buf.append("update kass_adr set ");
				buf.append("kassen_nam1='"+name1+"', ");
				buf.append("kassen_nam2='"+name2+"', ");
				buf.append("strasse='"+strasse+"', ");
				buf.append("plz='"+plz+"', ");
				buf.append("ort='"+ort+"', ");
				buf.append("ik_kasse='"+ikkasse+"', ");
				buf.append("ik_kostent='"+ikktraeger+"', ");
				buf.append("ik_physika='"+ikdaten+"', ");
				buf.append("ik_nutzer='"+iknutzer+"', ");
				buf.append("ik_papier='"+ikpapier+"', ");
				buf.append("kmemo='', ");
				buf.append("preisgruppe='"+pg+"', ");
				buf.append("pgkg='"+pg+"', ");
				buf.append("pgma='"+pg+"', ");
				buf.append("pger='"+pg+"', ");
				buf.append("pglo='"+pg+"', ");
				buf.append("pgrh='"+pg+"' ");
				buf.append("where id='"+Integer.toString(id)+"' LIMIT 1");
				/*
				String sql = "update kass_adr set kassen_nam1='"+name1+"', kassen_nam2='"+name2+"', strasse='"+
				strasse+"', plz='"+plz+"', ort='"+ort+"', ik_kasse='"+ikkasse+"', ik_kostent='"+
				ikktraeger+"', ik_physika='"+ikdaten+"', ik_nutzer='"+iknutzer+"', ik_papier='"+ikpapier+"', "+
				"kmemo='' where id='"+Integer.toString(id)+"' LIMIT 1";
				SqlInfo.sqlAusfuehren(sql);
				*/
				SqlInfo.sqlAusfuehren(buf.toString());
				
				/**********Datenannahmestelle**********/
				if(SqlInfo.holeEinzelFeld("select ik_kasse from kass_adr where ik_kasse='"+ikdaten+"' LIMIT 1").equals("")){
					JOptionPane.showMessageDialog(null, "Datenannahmestelle wird ebenfalls importiert");
					Vector<Vector<String>> dvec = SqlInfo.holeFelder("select * from ktraeger where ikkasse='"+ikdaten+"' LIMIT 1");
					if(dvec.size()<=0){
						JOptionPane.showMessageDialog(null,"Datenannahmestelle konnte nicht gefunden!\n\nDatenannahmestelle bitte manuell anlegen\n");
					}else{
						String sql = "insert into kass_adr set kassen_nam1='"+dvec.get(0).get(5)+"', "+
						"kassen_nam2='"+dvec.get(0).get(6)+"', "+
						"strasse='"+dvec.get(0).get(10)+"', "+
						"plz='"+dvec.get(0).get(8)+"', "+
						"ort='"+dvec.get(0).get(9)+"', "+
						"ik_kasse='"+dvec.get(0).get(0)+"', "+
						"email1='"+dvec.get(0).get(11)+"'";
						SqlInfo.sqlAusfuehren(sql);
					}
				}
				/**********Papierannahmestelle**********/
				if(SqlInfo.holeEinzelFeld("select ik_kasse from kass_adr where ik_kasse='"+ikpapier+"' LIMIT 1").equals("")){
					JOptionPane.showMessageDialog(null, "Papierannahmestelle wird ebenfalls importiert");				
					Vector<Vector<String>> dvec = SqlInfo.holeFelder("select * from ktraeger where ikkasse='"+ikpapier+"' LIMIT 1");
					if(dvec.size()<=0 || ikpapier.equals("")){
						JOptionPane.showMessageDialog(null,"Papierannahmestelle konnte nicht gefunden!\n\nPapierannahmestelle bitte manuell anlegen\n");
					}else{
						String sql = "insert into kass_adr set kassen_nam1='"+dvec.get(0).get(5)+"', "+
						"kassen_nam2='"+dvec.get(0).get(6)+"', "+
						"strasse='"+dvec.get(0).get(10)+"', "+
						"plz='"+dvec.get(0).get(8)+"', "+
						"ort='"+dvec.get(0).get(9)+"', "+
						"ik_kasse='"+dvec.get(0).get(0)+"', "+
						"email1='"+dvec.get(0).get(11)+"'";
						SqlInfo.sqlAusfuehren(sql);
					}
				}
				/**********Papierannahmestelle**********/
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Fehler bei der Erstellung der neuen Krankenkasse");
			}
		}
		return id;
		
	}

	public KVKRohDaten getInstance(){
		return this;
	}
	class RohKeyListener implements KeyListener{
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == 10){
				e.consume();
				if(((JComponent)e.getSource()).getName().equals("uebernehmen")){
					werteUebernehmen();
					knopf1.removeActionListener(getInstance());
					knopf1.removeKeyListener(this);
					knopf2.removeActionListener(getInstance());
					knopf2.removeKeyListener(this);
					knopf1 = null;
					knopf2 = null;
					dispose();
					return;
				}
				if(((JComponent)e.getSource()).getName().equals("abbrechen")){
					knopf1.removeActionListener(getInstance());
					knopf1.removeKeyListener(this);
					knopf2.removeActionListener(getInstance());
					knopf2.removeKeyListener(this);
					knopf1 = null;
					knopf2 = null;
					dispose();
					return;
				}
			}
			if(e.getKeyCode() == 27){
				knopf1.removeActionListener(getInstance());
				knopf1.removeKeyListener(this);
				knopf2.removeActionListener(getInstance());
				knopf2.removeKeyListener(this);
				knopf1 = null;
				knopf2 = null;
				dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
