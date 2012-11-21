package krankenKasse;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import CommonTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import CommonTools.JCompTools;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class KasseNeuanlage extends JXPanel implements ActionListener, KeyListener,FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7499352308908305654L;
	Vector<String> kasDaten = null;
	String kassenId = "";
	JRtaComboBox tarifGruppe = null;
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;	
	Object eltern = null;
	boolean neuAnlage;
	boolean ohneKuerzel=false;
	public JRtaTextField[] jtf = {null,null,null,null,null,
			                      null,null,null,null,null,
			                      null,null,null,null,null,
			                      null,null,null};
	JButton knopf1 = null;
	JButton knopf4 = null;
	JButton knopf5 = null;
	int[] fedits =  {0,2,3,4,5,6,7,8,9,13,14,15,16,17,12};
	int[] ffelder = {0,2,3,4,5,6,9,8,20,14,17,15,16,19,11};
	KassenPanel kpan;
	JLabel labKtraeger = null;
	JLabel labKuerzel = null;
	


	boolean mitButton = false;
	
	public KasseNeuanlage(Object eltern,KassenPanel xkpan,Vector<String> vec,String id){
		super();
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("KasseNeuanlage"));		
		this.kasDaten = vec;
		this.kassenId = id;
		this.eltern = eltern;
		kpan = xkpan;
		if(id.equals("")){
			this.neuAnlage = true;
		}else{
			this.neuAnlage = false;
		}

		hgicon = Reha.rehaBackImg;//new ImageIcon(Reha.proghome+"icons/therapieMT1.gif");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.07f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);			
		this.setDoubleBuffered(true);
		

		this.setLayout(new BorderLayout());
		
		add(getDatenPanel(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);
		if(!this.neuAnlage){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					fuelleFelder();
					return null;
				}
				
			}.execute();
		}
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}
	
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		  jtf[0].requestFocusInWindow();
		 	   }
		}); 	   		
	}
	public void fensterSchliessen(){
		((JDialog)this.eltern).dispose();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String comm = arg0.getActionCommand();
		final String xcomm = comm;
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			if(xcomm.equals("speichern")){
		 				datenSpeichern();
		 				tabelleAktualisieren();
		 				fensterSchliessen();
		 			}else if(xcomm.equals("abbrechen")){
		 				fensterSchliessen();			
		 			}else if(xcomm.equals ("vergleichKT")){
		 				doVergleichKT();
		 			}
		 	   }
		}); 	   				
	}
	private void doVergleichKT(){
			boolean validIKNummer = false;
				String iKNummer = "";
				JRtaTextField kVNummer;
				kVNummer = new JRtaTextField("ZAHLEN", true);
				if(neuAnlage == true || jtf[13].getText().trim().equals("") ){
					/*
					JRtaTextField f = new JRtaTextField("ZAHLEN",true);
					f.setColumns(9);
					Object[] message={"<html>Krankenkassennummer laut Rezept<br>" + 
							"bitte <b>7-stellige</b> Zahl eingeben</html>", f};
					Object[] options={"OK", "Cancel"};
					JOptionPane.showOptionDialog(null, message, "KV-Nummer eingeben", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, f);
					kVNummer.setText(f.getText());
					*/					
					
					kVNummer.setText(JOptionPane.showInputDialog(
									null,
									"<html>Krankenkassennummer laut Rezept<br>" + 
									"bitte <b>7-stellige</b> Zahl eingeben</html>",
									"KV-Nummer eingeben",
									JOptionPane.OK_CANCEL_OPTION));
									
					if(!kVNummer.getText().equals("")){
						if( (kVNummer.getText().length() < 7) && kVNummer.getText().length() != 9) {
							JOptionPane.showMessageDialog(null, "<html>die KV-Nummer <b>muss siebenstellig</b> sein</html>");
						}else if(kVNummer.getText().length() == 9){
							iKNummer = kVNummer.getText();
							validIKNummer = true;
						}else if(kVNummer.getText().length() == 7){
							iKNummer = "10"+kVNummer.getText();
							validIKNummer = true;
						}else{
							JOptionPane.showMessageDialog(null, "<html>die KV-Nummer <b>muss siebenstellig</b> sein</html>");
						}
					}
				}else /*neuAnlage == false*/{
					iKNummer.equals(jtf[13].getText());
					validIKNummer = true;
				}
				if(validIKNummer){
					if(jtf[13].getText().equals("")){
						jtf[13].setForeground(Color.BLUE);
						jtf[13].setText(iKNummer);
						jtf[12].setForeground(Color.BLUE);
						jtf[12].setText(kVNummer.getText());
					}
					ktraegerAuslesen();
				}
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						jtf[0].requestFocus();
					}
				});
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10){
			arg0.consume();
			if(((JComponent)arg0.getSource()).getName().equals("speichern")){
				datenSpeichern();
				tabelleAktualisieren();
				fensterSchliessen();				
			}
			if(((JComponent)arg0.getSource()).getName().equals("abbrechen")){
				fensterSchliessen();
			}		
		}
		if(arg0.getKeyCode() == 27){
			fensterSchliessen();
		}
		if( ((JComponent)arg0.getSource()).getName().equals("KUERZEL")){
			if(arg0.getKeyChar()=='?'){
				doVergleichKT();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if( ((JComponent)arg0.getSource()).getName().equals("KUERZEL")){
			jtf[0].setText(jtf[0].getText().replace("?", ""));
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(((JComponent)arg0.getSource()).getName().equals("TARIFGRUPPE") ){
			
			if(this.neuAnlage && (!jtf[0].getText().trim().equals("-")) ){
				//System.out.println("K�rzel = "+jtf[0].getText());
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						String kurz = jtf[0].getText().trim().toUpperCase();
						String stmt = "select id from kass_adr where kuerzel='"+kurz+"'";
						if(SqlInfo.gibtsSchon(stmt)){
							jtf[0].requestFocus();
							JOptionPane.showMessageDialog(null, "Krankenkasse mit dem Kürzel --> "+kurz+" <-- bereits vorhanden");
							jtf[0].setText("XXX-YY");
							SwingUtilities.invokeLater(new Runnable(){
								public  void run(){
									jtf[0].requestFocus();
								}
							});


						}
						return null;
					}
					
				}.execute();
			}
		}
	}
	@Override
	public void focusLost(FocusEvent arg0) {
	}
	
	private void fuelleFelder(){
 		List<String> nichtlesen = Arrays.asList(new String[] {"KMEMO"});
		Vector<String> felder = SqlInfo.holeSatz("kass_adr", "*", "id='"+this.kassenId+"'",nichtlesen);
		int gros = felder.size();
		int anzahlf = fedits.length;
		if(gros > 0){
			for(int i = 0; i < anzahlf;i++){
				jtf[fedits[i]].setText((String) felder.get(ffelder[i]) );
			}
			int preisG = Integer.valueOf((String) felder.get(1))-1 ;
			//System.out.println("In Preisgruppe einstellen Preisgruppe = "+preisG);
			tarifGruppe.setSelectedIndex((preisG >= 0 ? preisG : 0));
		}
	}
	
	public void ktraegerAuslesen (){
		boolean emailaddyok = false;
 		if(this.neuAnlage == true){
 				int iid = SqlInfo.holeId("kass_adr", "kmemo");
 				if(iid == -1){
 					JOptionPane.showMessageDialog(null, "Fehler beim Anlegen einer neuen Kasse, bitte erneut versuchen -> speichern");
 					return;
 				}
 				this.kassenId = Integer.toString(iid);
 		}
 		List<String> nichtlesen = Arrays.asList(new String[] {"KMEMO"});
 		Vector<String> felder = SqlInfo.holeSatz("kass_adr", "*", "id='"+this.kassenId+"'",nichtlesen);
 		felder.setElementAt(jtf[13].getText(), 14);
 		Vector<String> felder2 = SqlInfo.holeSatz("ktraeger", "*", "ikkasse='"+(String) felder.get(14)+"'",nichtlesen);
 		if(felder2.size() <= 0){
 			JOptionPane.showMessageDialog(null, "Kein Eintrag in der Kostenträgerdatei vorhanden für IK="+felder.get(14));
 			return;
 		}
 		//Wenn Datenannahmestelle fehlt
 		if(felder2.get(3).equals("")){
 			felder2.set(3, KTraegerTools.getDatenIK(felder2.get(1)));
 		}
 		//Wenn logischer Empfänger (Entschlüsselungsbefugnis fehlt)
 		if(felder2.get(4).equals("")){
 			felder2.set(4, KTraegerTools.getNutzerIK(felder2.get(1)));
 		}
 		//Wenn Papierannahmestelle fehlt
 		if(felder2.get(2).equals("")){
 			felder2.set(2, KTraegerTools.getPapierIK(felder2.get(1)));
 		}
 		//Wenn Emailadresse fehlt
 		String email = "";
 		if(felder2.get(11).equals("")){
 			//zunächst beim Kostenträger nachsehen
 			email = KTraegerTools.getEmailAdresse(felder2.get(1));
 			//Falls keine gefunden bei der Datenannahmestelle nachsehen
 		}else{
 			email = felder2.get(11);
 		}
 		// Jetzt nachsehen ob die Datenannahmestelle eine Emailadresse hat.
 		String email2 = KTraegerTools.getEmailAdresse(felder2.get(3));
 		if(! email2.trim().equals("")){
 			//alles palletti
 			//jetzt die Adresse aus dem Vector löschen
 			felder2.set(11, "");
 			emailaddyok = true;
 		}else{
 			//wenn in email eine Adresse steht.
 			if(! email.equals("")){
 	 			String cmd = "update ktraeger set email='"+email+"' where ikkasse='"+felder2.get(3)+"' LIMIT 1";
 	 			SqlInfo.sqlAusfuehren(cmd);
 	 			emailaddyok = true;
 	 			felder2.set(11, "");
 			}else{
 				felder2.set(11, "");
 			}
 		}
 		if( (felder2.get(0).equals("")) || (felder2.get(1).equals("")) || (felder2.get(2).equals(""))
 				|| (felder2.get(3).equals("")) || (felder2.get(4).equals("")) || (!emailaddyok)){
 			String htmlMeldung = "<html>Achtung mit den ermittelten Daten kann eine maschinenlesbare Abrechnung<br>"+
 			"nach §302 SGB V <b>nicht durchgeführt</b>werden</html>";
 			JOptionPane.showMessageDialog(null,htmlMeldung );
 		}
 		/*
 		MlaKassenChecker mlaChecker = new MlaKassenChecker(Reha.thisFrame,this);
 		mlaChecker.setPreferredSize(((JDialog)this.eltern).getSize());
 		mlaChecker.pack();
 		mlaChecker.setLocation(((JDialog)this.eltern).getLocationOnScreen());
 		mlaChecker.setVisible(true);
 		((JDialog)this.eltern).toFront();
		*/
 		
 		//		i=		    0  1  2  3  4 5 6 7 8  9 10
 		int[] fjtf =      {13,14,17,15,16,2,3,5,6, 4, 9};
 		int[] fktraeger = { 0, 1, 2, 3, 4,5,6,8,9,10,11};
 		if(felder2.size() > 0){
 			for(int i = 0; i < fjtf.length;i++){
 				//Änderungen übertragen außer Emailadresse
 				if(! jtf[fjtf[i]].getName().equals("EMAIL1")){
 	 				if(!jtf[fjtf[i]].getText().equals(felder2.get(fktraeger[i]))){
 	 					if(jtf[fjtf[i]].getText().length()!=0){
 	 						jtf[fjtf[i]].setForeground(Color.RED); //Ersetzte Daten rot markieren
 	 					} else{
 	 						jtf[fjtf[i]].setForeground(Color.BLUE); //Hinzugefügte Daten blau markieren
 	 					}
 	 				}
 	 				// wenn der neue Inhalt nicht leer ist ansonsten nimm den alten Inhalt
 	 	 				jtf[fjtf[i]].setText( (!felder2.get(fktraeger[i]).equals("") ? (String) felder2.get(fktraeger[i])
 	 	 						: jtf[fjtf[i]].getText()) );
 				}
 			}
 		}
	}
	public void datenSpeichern(){
		//int[] fedits =  {0,2,3,4,5,6,7,8,9,13,14,15,16,17};
		//int[] ffelder = {0,2,3,4,5,6,9,8,20,14,17,15,16,19};
		try{
		int anzahlf = fedits.length;
		String dbid = this.kassenId;
		StringBuffer kkBuffer = new StringBuffer();
		//String stmt = "update kass_adr set ";
		kkBuffer.append("update kass_adr set ");
		if(this.neuAnlage){
			int iid = SqlInfo.holeId("kass_adr", "kmemo");
			if(iid == -1){
				JOptionPane.showMessageDialog(null, "Fehler beim Anlegen einer neuen Kasse, bitte erneut versuchen -> speichern");
				return;
			}
			dbid = Integer.toString(iid);
			this.kassenId = dbid;
		}
		for(int i = 0; i < anzahlf; i++){

			kkBuffer.append(jtf[fedits[i]].getName() + "='"+jtf[fedits[i]].getText().trim()+"', ");
			
		}
		kkBuffer.append("kmemo ='', ");
		kkBuffer.append("preisgruppe ='"+ Integer.toString(this.tarifGruppe.getSelectedIndex()+1)+"', ");
		kkBuffer.append("pgkg ='"+ Integer.toString(this.tarifGruppe.getSelectedIndex()+1)+"', ");
		kkBuffer.append("pgma ='"+ Integer.toString(this.tarifGruppe.getSelectedIndex()+1)+"', ");
		kkBuffer.append("pger ='"+ Integer.toString(this.tarifGruppe.getSelectedIndex()+1)+"', ");
		kkBuffer.append("pglo ='"+ Integer.toString(this.tarifGruppe.getSelectedIndex()+1)+"', ");
		kkBuffer.append("pgrh ='"+ Integer.toString(this.tarifGruppe.getSelectedIndex()+1)+"', ");
		kkBuffer.append("pgpo ='"+ Integer.toString(this.tarifGruppe.getSelectedIndex()+1)+"' ");
		kkBuffer.append("where id='"+dbid+"' LIMIT 1");

		SqlInfo.sqlAusfuehren(kkBuffer.toString());
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler beim Abspeichern der Kasse");
		}

	}
	public void tabelleAktualisieren(){

		List<String> list = Arrays.asList(new String[] {jtf[0].getText(),jtf[2].getText(),
				jtf[3].getText(),jtf[6].getText(),jtf[7].getText(),jtf[8].getText(),jtf[13].getText(),this.kassenId});
		if(this.neuAnlage){
			Vector<String> vec = new Vector<String>();
			for(int i = 0; i < list.size();i++){
				vec.add(list.get(i));
			}
			kpan.ktblm.addRow((Vector<?>)vec);
		}else{
			int row = kpan.kassentbl.getSelectedRow();
			int model = kpan.kassentbl.convertRowIndexToModel(row);
			
			for(int i = 0; i < 8;i++){
				kpan.ktblm.setValueAt(list.get(i), model, i);
			}
			//System.out.println("Tabellenzeile aktualisiert");
		}
		kpan.kassentbl.revalidate();
		kpan.kassentbl.repaint();
	}
	
	private JXPanel getDatenPanel(){
		JXPanel but = new JXPanel(new BorderLayout());
		but.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		but.setOpaque(false);
		jtf[0] = new JRtaTextField("GROSS", true);
		jtf[0].setName("KUERZEL");
		jtf[0].setToolTipText("<html>Das Kürzele einer Kasse besteht aus insgesamt 6 Zeichen 'AAA-AA'<br>"+
		"Z.B. <b>AOK-RT</b> für AOK Reutlingen</html>");
		MaskFormatter uppercase = null;
		try {
			uppercase = new MaskFormatter("AAA-AA");
		} catch (ParseException e) {
			e.printStackTrace();
		}
        DefaultFormatterFactory factory = new DefaultFormatterFactory(uppercase);
        jtf[0].setFormatterFactory(factory);
        jtf[0].addFocusListener(this);
        jtf[0].addKeyListener(this);
        
        knopf1 = new JButton("Kostenträgerdatei");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);		
		knopf1.setActionCommand("vergleichKT");
		knopf1.setName("Kostenträgerdatei");
		knopf1.addKeyListener(this);
		knopf1.setMnemonic(KeyEvent.VK_K);
		if(neuAnlage==true){
			knopf1.setToolTipText("auf Basis der Daten der Kostenträgerdatei erstellen");	
		}else{
			knopf1.setToolTipText("mit Daten der Kostenträgerdatei vergleichen");	
		}
		jtf[1] = new JRtaTextField("", true);
		jtf[1].setName("PREISGRUPPE");
		jtf[2] = new JRtaTextField("", true);
		jtf[2].setName("KASSEN_NAM1");
		jtf[3] = new JRtaTextField("", true);
		jtf[3].setName("KASSEN_NAM2");
		jtf[4] = new JRtaTextField("", true);
		jtf[4].setName("STRASSE");
		jtf[5] = new JRtaTextField("", true);
		jtf[5].setName("PLZ");
		jtf[6] = new JRtaTextField("", true);
		jtf[6].setName("ORT");
		jtf[7] = new JRtaTextField("", true);
		jtf[7].setName("TELEFON");
		jtf[8] = new JRtaTextField("", true);
		jtf[8].setName("FAX");
		jtf[9] = new JRtaTextField("", true);
		jtf[9].setName("EMAIL1");
		jtf[10] = new JRtaTextField("", true);
		jtf[10].setName("EMAIL2");
		jtf[11] = new JRtaTextField("", true);
		jtf[11].setName("EMAIL3");
		jtf[12] = new JRtaTextField("ZAHLEN", true);
		jtf[12].setName("KV_NUMMER");
		jtf[13] = new JRtaTextField("ZAHLEN", true);
		jtf[13].setName("IK_KASSE"); //aus Kostentr�gerdatei/Karte einlesen?
		jtf[14] = new JRtaTextField("ZAHLEN", true);
		jtf[14].setName("IK_KOSTENT");
		jtf[15] = new JRtaTextField("ZAHLEN", true);
		jtf[15].setName("IK_PHYSIKA");
		jtf[16] = new JRtaTextField("ZAHLEN", true);
		jtf[16].setName("IK_NUTZER");
		jtf[17] = new JRtaTextField("ZAHLEN", true);
		jtf[17].setName("IK_PAPIER");				
		/*
											//  1.           2.     3.       4.               5.     6.
		FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.  3.   4.   5.   6  7   8    9   10   11  12  13  14   15   16  17  18   19   20   21  22   23  24   25   26   27  28  29   30   31   32  33  34    35  36   37  38   39    40   41   42  43   44   45  46  47  48    49   50   51 52   53  54   55  56   57   58   59   60  61   62   63  64   65
					"p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p");
											//  1.           2.     3.       4.               5.     6.
		*/
		FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.  3.   4.   5.   6  7   8    9   10   11  12  13  14   15   16  17  18   19   20   21  22   23  24   25   26   27  28  29   30   31   32  33  34  
					"p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 0dlu");
					PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);	
		CellConstraints cc = new CellConstraints();

		// abhängig von boolean mitButton entweder nur das Label mit Icon oder einen JButton
		labKuerzel = new JLabel("Kürzel");
		if(mitButton){
			builder.add(knopf1, cc.xyw(4,1,3));			
		}else{
			labKuerzel.setIcon(SystemConfig.hmSysIcons.get("kleinehilfe"));
			labKuerzel.setHorizontalTextPosition(JLabel.LEFT);
			labKuerzel.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent arg0) {
					doVergleichKT();
				}				
			});
		}
		//Ende Änderungsvorschlag
		
		builder.add(labKuerzel, cc.xy(1,1,CellConstraints.DEFAULT,CellConstraints.CENTER));
		builder.add(jtf[0], cc.xy(3,1,CellConstraints.DEFAULT,CellConstraints.CENTER));
		
		builder.addLabel("Tarifgruppe", cc.xy(1,3));
		tarifGruppe = new JRtaComboBox();
		tarifGruppe.setName("TARIFGRUPPE");
		tarifGruppe.addFocusListener(this);
		builder.add(tarifGruppe, cc.xyw(3, 3, 4));
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				int gruppen = SystemPreislisten.hmPreisGruppen.get("Common").size();
				for(int i = 0; i < gruppen;i++){
					tarifGruppe.addItem(SystemPreislisten.hmPreisGruppen.get("Common").get(i));
				}
				tarifGruppe.setSelectedIndex(0);
				return null;
			}
			
		}.execute();
		builder.addLabel("Name_1", cc.xy(1,5));
		builder.add(jtf[2], cc.xyw(3, 5, 4));
		builder.addLabel("Name_2", cc.xy(1,7));
		builder.add(jtf[3], cc.xyw(3, 7, 4));
		builder.addLabel("Strasse", cc.xy(1,9));
		builder.add(jtf[4], cc.xyw(3, 9,4));


		builder.addLabel("PLZ/Ort", cc.xy(1,11));
		builder.add(jtf[5], cc.xy(3, 11));
		builder.add(jtf[6], cc.xyw(4,11, 3));

		builder.addLabel("Telefon", cc.xy(1,13));
		builder.add(jtf[7], cc.xyw(3, 13, 4));
		builder.addLabel("Fax", cc.xy(1, 15));
		builder.add(jtf[8], cc.xyw(3, 15, 4));
		builder.addLabel("E-Mail", cc.xy(1, 17));
		builder.add(jtf[9], cc.xyw(3, 17, 4));
		
		builder.addSeparator("IK-Daten für maschinenlesbare Abrechnung", cc.xyw(1, 21, 6));		
		
		builder.addLabel("IK der Krankenkasse", cc.xy(1, 25));
		builder.add(jtf[13], cc.xyw(3, 25, 4));
		
		builder.addLabel("IK des Kostenträgers", cc.xy(1, 27));
		builder.add(jtf[14], cc.xyw(3, 27, 4));

		builder.addLabel("IK der Datenannahmestelle", cc.xy(1, 29));
		builder.add(jtf[15], cc.xyw(3, 29, 4));

		builder.addLabel("IK Nutzer/Entschlüssellung", cc.xy(1, 31));
		builder.add(jtf[16], cc.xyw(3, 31, 4));

		builder.addLabel("IK Papierannahmestelle", cc.xy(1, 33));
		builder.add(jtf[17], cc.xyw(3, 33, 4));
				
		JScrollPane jscr = JCompTools.getTransparentScrollPane(builder.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		
		//jscr.setViewportView(builder.getPanel());
		jscr.validate();
		jscr.addKeyListener(this);
		but.add(jscr,BorderLayout.CENTER);

		return but;
	}
	private JXPanel getButtonPanel(){
		JXPanel but = new JXPanel(new BorderLayout());
		but.setOpaque(false);
		
		
		knopf4 = new JButton("speichern");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);		
		knopf4.setActionCommand("speichern");
		knopf4.setName("speichern");
		knopf4.addKeyListener(this);
		knopf4.setMnemonic(KeyEvent.VK_S);
		
		knopf5 = new JButton("abbrechen");
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);		
		knopf5.setActionCommand("abbrechen");
		knopf5.setName("abbrechen");		
		knopf5.addKeyListener(this);
		knopf5.setMnemonic(KeyEvent.VK_A);
		
									//      1.                2.    3.     4.                 5.    6.    
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.  3.   4.   5.   
					"0dlu, p, 5dlu");
					PanelBuilder builder = new PanelBuilder(lay);
					builder.setDefaultDialogBorder();
					builder.getPanel().setOpaque(false);	
					CellConstraints cc = new CellConstraints();
		
		builder.add(knopf4, cc.xy(3,2));
		builder.add(knopf5, cc.xy(6,2));
					
		but.add(builder.getPanel(),BorderLayout.CENTER);
		return but;
	}

}