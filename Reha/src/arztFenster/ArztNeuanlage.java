package arztFenster;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.thera_pi.swingx.JCompTools;
import org.thera_pi.swingx.JRtaTextField;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaComboBox;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ArztNeuanlage extends JXPanel implements ActionListener,KeyListener,FocusListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2705163104113657236L;

	public JRtaTextField tfs[] = {null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null};

	public int[] felderpos = {0,1,2,3,4,5,6,11,17,8,9,14,12};
	public JButton speichern;
	public JButton abbrechen;
	public ArztNeuDlg eltern; 
	public JScrollPane jscr;  
	public JRtaComboBox arztgruppe;
	public JTextArea jta;
	private ArztPanel apan;
	Vector<String> arztDaten = null;
	String arztId = "";
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;	
	boolean neuAnlage;
	
	public ArztNeuanlage(ArztNeuDlg eltern,ArztPanel apanel,Vector<String> vec,String id){
		super();
	     setBackgroundPainter(Reha.thisClass.compoundPainter.get("ArztNeuanlage"));		
		this.arztDaten = vec;
		this.arztId = id;
		this.eltern = eltern;
		this.apan = apanel;
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
		
		add(getFelderPanel(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);
		if(!this.neuAnlage){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
					fuelleFelder();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
				
			}.execute();
		}
		validate();
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
		

	public JScrollPane getFelderPanel(){
		FormLayout lay = new FormLayout(
		        // 1                  2      3      4                 5    6
				"right:max(60dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
				//"0dlu,right:max(50dlu;p),3dlu,150dlu,3dlu,p,fill:0:grow(1.00)",
				// 1  2  3   4  5   6  7   8  9  10  11 12 13   14 15 16  17    18  19  20 21 22 23   24  25   26  27  28 29  30  31   32    33
				"3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p,5dlu,p,2dlu,p, 5dlu, p, 5dlu,p,2dlu,p,2dlu, p, 5dlu, p, 5dlu,p,2dlu,p,2dlu,0dlu,2dlu,5dlu");
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.setDefaultDialogBorder();
		jpan.getPanel().setOpaque(false);	
		jpan.getPanel().addKeyListener(this);
		CellConstraints cc = new CellConstraints();

		jpan.add(new JLabel("Anrede"),cc.xy(1,2));
		tfs[0] = new JRtaTextField("NIX",true);
		tfs[0].addKeyListener(this);
		tfs[0].addFocusListener(this);
		tfs[0].setName("anrede");
		jpan.add(tfs[0],cc.xy(3, 2));

		jpan.add(new JLabel("Titel"),cc.xy(4,2));
		tfs[1] = new JRtaTextField("NIX",true);
		tfs[1].addKeyListener(this);
		tfs[1].addFocusListener(this);
		tfs[1].setName("titel");
		jpan.add(tfs[1],cc.xy(6, 2));

		jpan.add(new JLabel("Nachname"),cc.xy(1,4));
		tfs[2] = new JRtaTextField("NIX",true);
		tfs[2].addKeyListener(this);
		tfs[2].addFocusListener(this);
		tfs[2].setName("nachname");
		jpan.add(tfs[2],cc.xyw(3, 4, 4));

		jpan.add(new JLabel("Vorname"),cc.xy(1,6));
		tfs[3] = new JRtaTextField("NIX",true);
		tfs[3].addKeyListener(this);
		tfs[3].addFocusListener(this);
		tfs[3].setName("vorname");
		jpan.add(tfs[3],cc.xyw(3, 6, 4));

		jpan.add(new JLabel("Strasse"),cc.xy(1, 8));
		tfs[4] = new JRtaTextField("NIX",true);
		tfs[4].addKeyListener(this);
		tfs[4].addFocusListener(this);		
		tfs[4].setName("strasse");
		jpan.add(tfs[4],cc.xyw(3, 8,4));
		
		jpan.add(new JLabel("Plz/Ort"),cc.xy(1, 10));
		tfs[5] = new JRtaTextField("ZAHLEN",true);
		tfs[5].addKeyListener(this);
		tfs[5].addFocusListener(this);		
		tfs[5].setName("plz");
		jpan.add(tfs[5],cc.xy(3, 10));

		tfs[6] = new JRtaTextField("NIX",true);
		tfs[6].addKeyListener(this);
		tfs[6].addFocusListener(this);
		tfs[6].setName("ort");
		jpan.add(tfs[6],cc.xyw(4, 10,3));


		jpan.addSeparator("Arztidentifikation",cc.xyw(1, 12, 6));

		
		jpan.add(new JLabel("LANR"),cc.xy(1, 14));
		tfs[7] = new JRtaTextField("ZAHLEN",true);
		tfs[7].addKeyListener(this);
		tfs[7].addFocusListener(this);		
		tfs[7].setName("arztnum");
		jpan.add(tfs[7],cc.xyw(3, 14,4));

		jpan.add(new JLabel("Betriebsstätte"),cc.xy(1, 16));
		tfs[8] = new JRtaTextField("ZAHLEN",true);
		tfs[8].addKeyListener(this);
		tfs[8].addFocusListener(this);		
		tfs[8].setName("bsnr");
		jpan.add(tfs[8],cc.xyw(3, 16,4));
		
		jpan.addSeparator("Kontakt",cc.xyw(1, 18, 6));

		jpan.add(new JLabel("Telefon"),cc.xy(1, 20));
		tfs[9] = new JRtaTextField("NORMAL",true);
		tfs[9].addKeyListener(this);
		tfs[9].addFocusListener(this);		
		tfs[9].setName("telefon");
		jpan.add(tfs[9],cc.xyw(3, 20,4));

		jpan.add(new JLabel("Telefax"),cc.xy(1, 22));
		tfs[10] = new JRtaTextField("NORMAL",true);
		tfs[10].addKeyListener(this);
		tfs[10].addFocusListener(this);		
		tfs[10].setName("fax");
		jpan.add(tfs[10],cc.xyw(3, 22,4));

		jpan.add(new JLabel("Email"),cc.xy(1, 24));
		tfs[11] = new JRtaTextField("NIX",true);
		tfs[11].addKeyListener(this);
		tfs[11].addFocusListener(this);
		tfs[11].setName("email1");
		jpan.add(tfs[11],cc.xyw(3, 24,4));
		
		jpan.addSeparator("Zusätze",cc.xyw(1, 26, 6));		

		jpan.add(new JLabel("Facharzt"),cc.xy(1, 28));
		arztgruppe = new JRtaComboBox(SystemConfig.arztGruppen);
		arztgruppe.addFocusListener(this);
		jpan.add(arztgruppe,cc.xyw(3, 28,4));

		jpan.add(new JLabel("Klinik"),cc.xy(1, 30));
		tfs[12] = new JRtaTextField("NIX",true);
		tfs[12].addKeyListener(this);
		tfs[12].addFocusListener(this);
		tfs[12].setName("klinik");
		jpan.add(tfs[12],cc.xyw(3, 30,4));

		tfs[13] = new JRtaTextField("NIX",true);
		tfs[13].setName("facharzt");
		tfs[14] = new JRtaTextField("NIX",true);
		tfs[14].setName("id");

		/*
		jpan.add(new JLabel("Notizen"),cc.xy(1, 32));
		jta = new JTextArea();
		jta.setFont(new Font("Courier",Font.PLAIN,11));
		jta.setLineWrap(true);
		jta.setName("mtext");
		jta.setWrapStyleWord(true);
		jta.setEditable(true);
		jta.setBackground(Color.WHITE);
		jta.setForeground(Color.RED);
		JScrollPane span = JCompTools.getTransparentScrollPane(jta);
		span.validate();
		jpan.add(span,cc.xywh(3, 32,4,2));
		*/
		
		jpan.getPanel().validate();
		jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
}
	/**************************************************/
	public JXPanel getButtonPanel(){
		JXPanel	jpan = JCompTools.getEmptyJXPanel();
		jpan.addKeyListener(this);
		jpan.setOpaque(false);
		FormLayout lay = new FormLayout(
		        // 1                2          3             4      5    
				"fill:0:grow(0.33),50dlu,fill:0:grow(0.33),50dlu,fill:0:grow(0.33)",
				// 1  2  3  
				"5dlu,p,5dlu");
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		speichern = new JButton("speichern");
		speichern.setActionCommand("speichern");
		speichern.addActionListener(this);
		speichern.addKeyListener(this);
		speichern.setMnemonic(KeyEvent.VK_S);
		jpan.add(speichern,cc.xy(2,2));
		
		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);
		abbrechen.setMnemonic(KeyEvent.VK_A);		
		jpan.add(abbrechen,cc.xy(4,2));

		return jpan;
	}
	public JXPanel getNeuKurzPanel(){
		return this;
	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				tfs[0].requestFocus();
			}
		});
	}
	private void drecksFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				setzeFocus();
			}
		});
	}
	private void toClipboard(){
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("@Thera-Pi-Arztdaten@\n");
			for(int i = 0; i < tfs.length;i++){
				if(tfs[i] != null){
					buf.append("TF-"+tfs[i].getName()+"="+tfs[i].getText()+"\n");
				}
			}
			buf.append("CO-facharzt"+"="+arztgruppe.getSelectedItem().toString()+"\n");
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(buf.toString()), null);
			JOptionPane.showMessageDialog(this, "Arztdaten erfolgreich in die Zwischenablage übertragen");
			drecksFocus();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(this, "Fehler beim Übertrag der Arztdaten in die Zwischenablage");
			drecksFocus();
		}
	}
	private void fromClipboard(){
		String dummy1 = null;
		String dummy2 = null;
		String dummy3 = null;
		String dummy4 = null;
		int i = -1;
		int i1 = -1;

		try{
			int frage = JOptionPane.showConfirmDialog(this,"Soll der Inhalt der Zwischenablage auf Arztdaten untersucht werden\nund ggfls. die Daten übernommen werden?","Wichtige Benutheranfrage",JOptionPane.YES_NO_OPTION);
			String clipstring = null;
			if(frage != JOptionPane.YES_OPTION){
				drecksFocus();
				return;
			}
			Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				clipstring = (String)t.getTransferData(DataFlavor.stringFlavor);
				String[] clip = clipstring.split("\n");
				if(! clip[0].startsWith("@Thera-Pi-Arztdaten@")){
					JOptionPane.showMessageDialog(this, "Daten der Zwischenablage sind keine verwertbaren Arztdaten");
					drecksFocus();
					return;
				}
				for(i = 1; i < clip.length;i++){
					try{
						dummy1 = clip[i].split("=")[0];
						dummy2 = clip[i].split("=")[1];
						dummy3 = dummy1.split("-")[0];
						dummy4 = dummy1.split("-")[1];
					}catch(Exception ex){
						dummy2 = "";
						continue;
					}
					if(dummy3.equals("TF")){
						for(i1 = 0; i1 < tfs.length;i1++){
							if(tfs[i1] != null){
								if(tfs[i1].getName().equals(dummy4) && (!tfs[i1].getName().equals("id")) ){
									tfs[i1].setText(dummy2.replace("\n",""));
								}	
							}
						}
					}else if(dummy3.equals("CO")){
						arztgruppe.setSelectedItem(dummy2.replace("\n",""));
					}
				}
				JOptionPane.showMessageDialog(this, "Inhalt der Zwischenablage wurde erfolgreich übertragen");
				drecksFocus();
				return;
			}else{
				JOptionPane.showMessageDialog(this, "Daten der Zwischenablage sind keine verwertbaren Arztdaten");
				drecksFocus();
				return;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			//System.out.println("i = "+i);
			//System.out.println("i1 = "+i1);
			JOptionPane.showMessageDialog(this, "Fehler beim Übertrag der Zwischenablage");
			drecksFocus();
		}
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
		 			}
		 	   }
		}); 	   				
	}
	public void tabelleAktualisieren(){

		List<String> list = Arrays.asList(new String[] {tfs[7].getText(),tfs[2].getText(),
				tfs[3].getText(),tfs[4].getText(),tfs[6].getText(),tfs[9].getText(),tfs[10].getText(),
				tfs[12].getText(),
				(arztgruppe.getSelectedItem()==null ? "" : (String)arztgruppe.getSelectedItem() ),this.arztId});
		if(this.neuAnlage){
			Vector<String> vec = new Vector<String>();
			for(int i = 0; i < list.size();i++){
				vec.add(list.get(i));
			}
			apan.atblm.addRow((Vector<String>)vec);
			//System.out.println("Tabellenzeile eingefügt");
		}else{
			int row = apan.arzttbl.getSelectedRow();
			int model = apan.arzttbl.convertRowIndexToModel(row);
			
			for(int i = 0; i < 8;i++){
				apan.atblm.setValueAt(list.get(i), model, i);
				//KassenPanel.thisClass.kassentbl.setValueAt(list.get(i), row, i);
			}
			//System.out.println("Tabellenzeile aktualisiert");
		}
		apan.arzttbl.revalidate();
		apan.arzttbl.repaint();
	}
	

	public void allesAufNull(){
		for(int i = 0; i < 15; i++){
			tfs[i].setText("");
		}
	}
	
	public void datenSpeichern(){
		//int[] fedits =  {0,2,3,4,5,6,7,8,9,13,14,15,16,17};
		//int[] ffelder = {0,2,3,4,5,6,9,8,20,14,17,15,16,19};
		int anzahlf = felderpos.length;
		String dbid = this.arztId;
		StringBuffer stmt = new StringBuffer();
		stmt.append("update arzt set ");
		if(this.neuAnlage){
			int iid = SqlInfo.holeId("arzt", "NACHNAME");
			if(iid==-1){
				JOptionPane.showMessageDialog(null, "Fehler beim Anlegen einer neuen Arzt-ID, bitte erneut versuchen -> speichern");
				return;
			}
			dbid = Integer.toString(iid);
			this.arztId = dbid;
		}
		for(int i = 0; i < anzahlf; i++){
			stmt.append( tfs[i].getName() + "='"+tfs[i].getText().trim()+"' ,");
		}
		stmt.append("facharzt ='"+ (arztgruppe.getSelectedItem()==null ? "" : (String)arztgruppe.getSelectedItem() )+"'");
		stmt.append(" where id='"+dbid+"'");
		//System.out.println("Kommando = "+stmt);
		new ExUndHop().setzeStatement(stmt.toString());

	}
	
	public void fensterSchliessen(){
		((JDialog)this.eltern).dispose();
	}

	

	private void fuelleFelder(){
 		List<String> nichtlesen = Arrays.asList(new String[] {});
		Vector<String> felder = SqlInfo.holeSatz("arzt", "*", "id='"+this.arztId+"'",nichtlesen);
		int gros = felder.size();
		//System.out.println("Arztdaten von id"+this.arztId+" = "+felder);
		int anzahlf = felderpos.length;
		if(gros > 0){
			for(int i = 0; i < anzahlf;i++){
				tfs[i].setText((String) felder.get(felderpos[i]) );
			}
			arztgruppe.setSelectedItem((String) felder.get(7));
		}
		
	}
	
	public void panelWechsel(boolean uebernahme){
		if(uebernahme){
			new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					if(tfs[2].getText().trim().equals("")){
						JOptionPane.showMessageDialog(null, "Also der Name des Arztes sollte wenigstens angegeben werden!");
						return null;
					}
					int iid;
					tfs[13].setText((String)arztgruppe.getSelectedItem());
					tfs[14].setText(Integer.toString( iid = SqlInfo.holeId("arzt", "nachname")));
					String stmt = "update arzt set ";
					for(int i = 0; i < 14; i++){
						stmt = stmt+ (i==0 ? "": ", ")+tfs[i].getName()+"='"+tfs[i].getText()+"'";
					}
					stmt = stmt + " where id ='"+Integer.toString(iid)+"'";
					////System.out.println(stmt);
					new ExUndHop().setzeStatement(stmt);
					//eltern.zurueckZurTabelle(tfs); **************wichtig
					return null;
				}
				
			}.execute();
		}else{
			//eltern.zurueckZurTabelle(null);******wichtig
		}

		
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
	}	
	@Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_F3){
			toClipboard();
		}else if(arg0.getKeyCode() == KeyEvent.VK_F2){
			fromClipboard();
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		Rectangle rec1 =((JComponent)arg0.getSource()).getBounds();
		Rectangle rec2 = jscr.getViewport().getViewRect();
		JViewport vp = jscr.getViewport();
		vp.getVisibleRect();
		if((rec1.y+((JComponent)arg0.getSource()).getHeight()) > (rec2.y+rec2.height)){
			vp.setViewPosition(new Point(0,(rec2.y+rec2.height)-rec1.height));
		}
		if(rec1.y < (rec2.y)){
			vp.setViewPosition(new Point(0,rec1.y));
		}
		
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
