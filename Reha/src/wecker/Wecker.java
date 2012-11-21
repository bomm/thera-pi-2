package wecker;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import CommonTools.SqlInfo;
import systemTools.ButtonTools;
import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;
import terminKalender.ZeitFunk;


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class Wecker extends RehaSmartDialog implements RehaTPEventListener,WindowListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8884397927377259156L;
	private RehaTPEventClass rtp = null;
	JXPanel content = null;
	
	ActionListener al = null;
	KeyListener kl = null;
	
	JRtaTextField[] tfs = {null,null,null,null,null,null};
	JButton[] buts = {null,null,null,null};
	
	MyWeckerTableModel tabmod = null;
	JXTable tab = null;
	Font labelFont = new Font("Tahoma",Font.BOLD,15);
	String droppat = null;
	public static boolean dialogoffen = false;

	public static Wecker thisClass;
	public Wecker(String wenwecken){
		super(null,"WeckerNeuanlage");
		System.out.println(wenwecken);
		try{
			if(wenwecken != null){
				if(wenwecken.startsWith("TERMDAT")){
					droppat = wenwecken.split("°")[1];
				}else{
					droppat = "";
				}
			}else{
				droppat = "";
			}
		}catch(Exception ex){
			droppat = "";
		}
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName("WeckerNeuanlage");
		setPinPanel(pinPanel);
		activateListener();
		getSmartTitledPanel().setTitle("Thera-\u03C0"+" Erinnerungs-System");			
		getSmartTitledPanel().setContentContainer(getContent());
		getSmartTitledPanel().getContentContainer().setName("WeckerNeuanlage");
		setName("WeckerNeuanlage");
		//neuPat.setContentPane(new PatNeuanlage(new Vector()));
		setSize(640,400);
		this.setPreferredSize(new Dimension(640,400));
		setModal(true);
		setLocationRelativeTo(null);
		//neuPat.setLocation(new Point(200,50));
		setTitle("Thera-\u03C0"+" Erinnerungs-System");
		Reha.timerInBearbeitung = true;
		addWindowListener(this);
		addKeyListener(this);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus(0);
			}
		});
		dialogoffen = true;
		thisClass = this;
		doTabelle();
		
	}
	/*******************************************************/
	public static void testeWecker(){
		//System.out.println("Timeraufruf");
		if(Reha.timerInBearbeitung || dialogoffen){
			//System.out.println("Timer in Bearbeitung: "+Reha.timerInBearbeitung);
			//System.out.println("dialogoffen: "+dialogoffen);
			return;
		}
		
		if(Reha.timerVec.size() <= 0){
			//System.out.println("timer wird gestoppt");
			Reha.fangoTimer.stop();
			Reha.timerLaeuft = false;
			Reha.thisClass.messageLabel.setForeground(Color.BLACK);
			Reha.thisClass.messageLabel.setText("keine Timer-Termine");
			return;
		}
		try{
			boolean abgelaufen = false;
			int termin = -1;
			for(int i = 0; i < Reha.timerVec.size();i++){
				if(aktuelleMinuten() >= (Long)Reha.timerVec.get(i).get(3)){
					abgelaufen = true;
					termin = i;
					break;
				}
			}
			if(abgelaufen){
				Reha.timerInBearbeitung = true;
				new TerminAbgelaufen(null,Reha.timerVec.get(termin));
				Reha.timerVec.remove(termin);
				Reha.timerVec.trimToSize();
				Reha.timerInBearbeitung = false;
				
				if(Reha.timerVec.size() <= 0){
					Reha.fangoTimer.stop();
					Reha.timerLaeuft = false;
					Reha.thisClass.messageLabel.setForeground(Color.BLACK);
					Reha.thisClass.messageLabel.setText("keine Timer-Termine");
				}else{
					String msg = Long.toString((Long)Reha.timerVec.get(0).get(3)-aktuelleMinuten());
					Reha.thisClass.messageLabel.setForeground(Color.RED);
					Reha.thisClass.messageLabel.setText("in "+msg+(msg.equals("1") ? " Minute " : " Minuten ")+"nächster Timer-Termin" );
				}
				
			}else{
				String msg = Long.toString((Long)Reha.timerVec.get(0).get(3)-aktuelleMinuten());
				Reha.thisClass.messageLabel.setForeground(Color.RED);
				Reha.thisClass.messageLabel.setText("in "+msg+(msg.equals("1") ? " Minute " : " Minuten ")+"nächster Timer-Termin" );
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/*******************************************************/
	private void setzeFocus(int wohin){
		final int xwohin = wohin;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				tfs[xwohin].requestFocus();
			}
		});
	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		try{
			this.setVisible(false);
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp = null;
			this.pinPanel = null;
			this.dispose();
			dialogoffen = false;
			Reha.timerInBearbeitung = false;
			doAufraeumen();
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		dialogoffen = false;
		Reha.timerInBearbeitung = false;
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			this.pinPanel = null;
			doAufraeumen();
			//System.out.println("****************Arzt Neu/ändern -> Listener entfernt (Closed)**********");
		}
	}
	private void doAufraeumen(){
		//
		for(int i = 0; i < 2; i++){
			buts[i].removeActionListener(al);
			buts[i].removeKeyListener(kl);
			buts[i] = null;
		}
		for(int i = 0; i < 3; i++){
			tfs[i].listenerLoeschen();
			tfs[i].removeKeyListener(kl);
			tfs[i] = null;
		}
		al = null;
		Reha.timerInBearbeitung = false;
		dialogoffen = false;
	}
	
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if( cmd.equals("terminneu") ){
					doNeuTermin();
					return;
				}
				if( cmd.equals("termindelete") ){
					doDeleteTermin();
					return;
				}
			}
			
		};
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(((JComponent)arg0.getSource()).getName().equals("terminneu") &&
						arg0.getKeyCode() == 10){
						doNeuTermin();
						return;
				}
				if(arg0.getKeyCode()==27){
					thisClass.dispose();
				}
				

			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};
	}
	private void doDeleteTermin(){
		if(tab.getRowCount()<=0){return;}
		int row = tab.getSelectedRow();
		if(row < 0){
			return;
		}
		Reha.timerVec.remove(row);
		Reha.timerVec.trimToSize();
		doTabelle();
	}
	private JXPanel getContent(){
		//                1      2      3    4      5
		String xwerte = "10dlu,60dlu:g,5dlu,200dlu,10dlu";
		//                1    2  3   4  5   6  7   8  9         10           11 12 13 
		String ywerte = "10dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,fill:0:grow(1.0),5dlu,p,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		content.setBackgroundPainter(Reha.thisClass.compoundPainter.get("RezeptGebuehren"));

		JLabel lab = new JLabel("Patient/Termin");
		lab.setFont(labelFont);
		lab.setForeground(Color.RED);
		content.add(lab,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JRtaTextField("GROSS",true);
		tfs[0].setFont(labelFont);
		tfs[0].setName("tfs0");
		tfs[0].addKeyListener(kl);
		tfs[0].setText(droppat);
		content.add(tfs[0],cc.xy(4,2));
		
		lab = new JLabel("Raum/Kabine");
		lab.setFont(labelFont);
		lab.setForeground(Color.RED);		
		content.add(lab,cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[1] = new JRtaTextField("GROSS",true);
		tfs[1].setFont(labelFont);
		tfs[1].setName("tfs1");
		tfs[1].addKeyListener(kl);
		content.add(tfs[1],cc.xy(4,4));

		lab = new JLabel("Dauer in Minuten");
		lab.setFont(labelFont);
		lab.setForeground(Color.RED);		
		content.add(lab,cc.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[2] = new JRtaTextField("nix",true);
		tfs[2].setFont(labelFont);
		tfs[2].setName("tfs2");
		tfs[2].addKeyListener(kl);
		content.add(tfs[2],cc.xy(4,6));
		
		buts[0] = ButtonTools.macheButton("neuen Termin eintragen", "terminneu", al);
		buts[0].setMnemonic(KeyEvent.VK_N);
		buts[0].setName("terminneu");
		buts[0].addKeyListener(kl);
		content.add(buts[0],cc.xy(4,8));
		
		tabmod = new MyWeckerTableModel();
		tabmod.setColumnIdentifiers(new String[]{"fertig um","Raum/Kabine","Patient",""});
		tab = new JXTable(tabmod);
		tab.setSortable(false);
		tab.setEditable(false);
		tab.getColumn(0).setMaxWidth(120);
		tab.getColumn(1).setMaxWidth(120);
		tab.getColumn(3).setMinWidth(0);
		tab.getColumn(3).setMaxWidth(0);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		content.add(jscr,cc.xyw(2,10,3));
		
		buts[1] = ButtonTools.macheButton("löschen Termin", "termindelete", al);
		buts[1].setMnemonic(KeyEvent.VK_L);
		content.add(buts[1],cc.xyw(2,12,3));
		
		content.validate();
		return content;
	}
	
	class MyWeckerTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			return false;
		}
		   
	}
	
	@SuppressWarnings("unchecked")
	private void doNeuTermin(){
		int minuten = 0; 
		try{
			minuten = Integer.parseInt(tfs[2].getText().trim());
			if(minuten <= 0){
				JOptionPane.showMessageDialog(null, "Fehler in der Angabe 'Dauer des Termines'" );
				setzeFocus(2);
				return;
			}
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler in der Angabe 'Dauer des Termines'" );
			setzeFocus(2);
			return;
		}
		if(tfs[0].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Fehler in der Angabe 'Patient / Termin'" );
			setzeFocus(0);
			return;
		}
		if(tfs[1].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Fehler in der Angabe 'Raum / Kabine'" );
			setzeFocus(1);
			return;
		}
		long minsinceMidnight = aktuelleMinuten(); 
		
		String fertigstring = ZeitFunk.MinutenZuZeit( Integer.parseInt( Long.toString(minsinceMidnight) ) + minuten); 

		long minFertig = ZeitFunk.MinutenSeitMitternacht(fertigstring);

		Vector<Object> vec = new Vector<Object>();
		vec.add((String) fertigstring);
		vec.add((String) tfs[1].getText());
		vec.add((String) tfs[0].getText());
		vec.add((Long) minFertig);
		Reha.timerVec.add((Vector<Object>)vec.clone());
		doSortieren();
		doTabelle();
		doLeeren();
	}
	static long aktuelleMinuten(){
		Calendar cal = Calendar.getInstance();   
		cal.setTime(new Date()); 
		int hour = cal.get(Calendar.HOUR_OF_DAY); 
		int min = cal.get(Calendar.MINUTE);
		String zeitstring = (Integer.toString(hour).length() < 2 ? "0"+Integer.toString(hour) : Integer.toString(hour))+
			":"+(Integer.toString(min).length() < 2 ? "0"+Integer.toString(min) : Integer.toString(min))+":00";
		return ZeitFunk.MinutenSeitMitternacht(zeitstring);
		
	}
	private void doLeeren(){
		tfs[0].setText("");
		tfs[1].setText("");
		tfs[2].setText("");
		setzeFocus(0);
	}
	public static void doSortieren(){
		Comparator<Vector<Object>> comparator = new Comparator<Vector<Object>>() {
			@Override
			public int compare(Vector<Object> o1, Vector<Object> o2) {
				Long l1 = (Long)o1.get(3);
				Long l2 = (Long)o2.get(3);
				return l1.compareTo(l2);
			}
		};
		Collections.sort(Reha.timerVec,comparator);
	}
	public static void doTabelle(){
		thisClass.tabmod.setRowCount(0);
		for(int i = 0; i < Reha.timerVec.size();i++){
			thisClass.tabmod.addRow(Reha.timerVec.get(i));
		}
		thisClass.tab.validate();
		if(thisClass.tabmod.getRowCount() > 0){
			thisClass.tab.setRowSelectionInterval(0, 0);
			if(!Reha.timerLaeuft){
				//System.out.println("Timer gestaret");
				Reha.fangoTimer.restart();
				Reha.timerLaeuft = true;
			}else{
				//System.out.println("Timer läuft bereits");
			}
			String msg = Long.toString((Long)Reha.timerVec.get(0).get(3)-aktuelleMinuten());
			Reha.thisClass.messageLabel.setForeground(Color.RED);
			Reha.thisClass.messageLabel.setText("in "+msg+" Minuten nächster Timer-Termin" );
		}else{
			Reha.thisClass.messageLabel.setForeground(Color.BLACK);
			Reha.thisClass.messageLabel.setText("keine Timer-Termine");
			Reha.fangoTimer.stop();
			Reha.timerLaeuft = false;
			//System.out.println("Timer gestoppt");
		}
	}

}
/************************************************************************************/
final class TerminAbgelaufen extends RehaSmartDialog implements WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<Object> vec = null;
	JRtaTextField entdeckt = null;
	JButton ok = null;
	JEditorPane htmlPane = null;
	Timer tontimer = null;
	ActionListener alton = null; 
	KeyListener klton = null;
	JPasswordField pwfield = null;
	public TerminAbgelaufen(JXFrame owner, Vector<Object> xvec) {
		
		super(null,"TerminAbgelaufen");
		vec = xvec;
		setzeTon();
		activateListener();
		getSmartTitledPanel().setTitle("Thera-\u03C0"+" TerminAbgelaufen");			
		getSmartTitledPanel().setContentContainer(getHTMLContent());
		getSmartTitledPanel().getContentContainer().setName("TerminAbgelaufen");
		setName("TerminAbgelaufen");
		//neuPat.setContentPane(new PatNeuanlage(new Vector()));
		setSize(750,250);
		setPreferredSize(new Dimension(750,250));
		setAlwaysOnTop(true);
		addWindowListener(this);
		//addKeyListener(this);
		setModal(true);
		setLocationRelativeTo(null);
		tontimer = new Timer(10000,alton);
		tontimer.setActionCommand("neuerton");
		tontimer.start();

		pack();

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});

		setVisible(true);
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				pwfield.requestFocus();
			}
		});
	}
	private void activateListener(){
		alton = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("neuerton")){
					setzeTon();
					return;
				}
				if(cmd.equals("ok")){
					testeOk();
				}
				
			}
		};
		klton = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				String cname = ((JComponent)arg0.getSource()).getName(); 
				if(arg0.getKeyCode()==10){
					if(cname.equals("pwfield") || cname.equals("ok")){
						testeOk();
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};
	}
	private void testeOk(){
		String pw = String.valueOf(pwfield.getPassword()).trim();
		if(pw.equals("") || pw.toLowerCase().equals("rta")){
			pwfield.requestFocus();
			return;
		}
		boolean pwkorrekt = false;
		String pwuser = "";
		for(int i = 0; i < ParameterLaden.pKollegen.size();i++){
			if(pw.equals(ParameterLaden.pKollegen.get(i).get(1).trim())){
				pwkorrekt = true;
				pwuser = String.valueOf(ParameterLaden.pKollegen.get(i).get(0));
				break;
			}
		}
		if(pwkorrekt){
			String cmd = "insert into wecker set n_name='"+vec.get(2)+"', "+
				"raum='"+vec.get(1)+"', "+
				"fertig='"+vec.get(0)+"', "+
				"registervon='"+pwuser+"', "+
				"registriert='"+ZeitFunk.MinutenZuZeit(Integer.parseInt(Long.toString(Wecker.aktuelleMinuten()))  )+"', "+
				"datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"'";
			SqlInfo.sqlAusfuehren(cmd);
			doAufraeumen();
			//Hier in die Datenbank schreiben.....
			//dann Fenster schließen
		}else{
			pwfield.setText("");
			pwfield.requestFocus();
			return;
		}
	}
	private void setzeTon(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				new AePlayWave(Reha.proghome+"sounds/"+"carhorn.wav").start();
				return null;
			}
		}.execute();
	}
	private TerminAbgelaufen getInstance(){
		return this;
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
			@SuppressWarnings("unused")
			TerminAbgelaufen tab = getInstance();
			tab = null;
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
			this.doAufraeumen();
			@SuppressWarnings("unused")
			TerminAbgelaufen tab = getInstance();
			tab = null;
	}
	private void doAufraeumen(){
		if(htmlPane != null){
			htmlPane = null;			
		}
		if(tontimer != null){
			tontimer.stop();
			tontimer = null;
		}
		if(pwfield != null){
			pwfield.removeKeyListener(klton);
		}
		if(ok != null){
			ok.removeActionListener(alton);
			ok.removeKeyListener(klton);
		}
		alton = null;
		klton = null;
		this.dispose();
	}
	private JXPanel getHTMLContent(){
		String xwerte = "0dlu,fill:0:grow(1.0),0dlu";
		String ywerte = "0dlu,fill:0:grow(1.0),10dlu,p";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		jpan.setBackground(Color.WHITE);
		
		
		htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
		htmlPane.setText(getHtmlText());
		JScrollPane jscrk = JCompTools.getTransparentScrollPane(htmlPane);
		jscrk.validate();
		jpan.add(jscrk,cc.xy(2, 2,CellConstraints.FILL,CellConstraints.FILL));

		String xwerte2= "10dlu,80dlu,5dlu,fill:0:grow(1.0),5dlu,50dlu,10dlu";
		String ywerte2= "10dlu,p,10dlu";
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		JXPanel jpan2 = new JXPanel();
		jpan2.setLayout(lay2);
		

		JLabel lab = new JLabel("Bitte Passwort eingeben");
		jpan2.add(lab,cc2.xy(2, 2));
		pwfield = new JPasswordField();
		pwfield.setBackground(Color.YELLOW);
		pwfield.setName("pwfield");
		pwfield.addKeyListener(klton);
		jpan2.add(pwfield,cc2.xy(4, 2));
		
		ok = ButtonTools.macheButton("Ok", "ok", alton);
		ok.setName("ok");
		ok.addKeyListener(klton);
		jpan2.add(ok,cc2.xy(6, 2));
		jpan2.validate();
		
		jpan.add(jpan2,cc.xy(2,4));
		jpan.validate();
		return jpan;
	}
	private String getHtmlText(){
		StringBuffer buf1 = new StringBuffer();
		buf1.append("<html><head>");
		buf1.append("<STYLE TYPE=\"text/css\">");
		buf1.append("<!--");
		buf1.append("A{text-decoration:none;background-color:transparent;border:none}");
		buf1.append("TD{font-family: Arial; font-size: 20pt; padding-left:5px;padding-right:30px}");
		buf1.append(".spalte1{color:#0000FF;}");
		buf1.append(".spalte2{color:#333333;}");
		buf1.append(".spalte2{color:#333333;}");
		buf1.append("--->");
		buf1.append("</STYLE>");
		buf1.append("</head>");
		buf1.append("<div style=margin-left:30px;margin-top:5px;>");
		buf1.append("<font face=\"Tahoma\"><style=margin-left=30px;>");
		buf1.append("<br>");
		buf1.append("<table>");
		
		buf1.append("<tr>");
		buf1.append("<th rowspan=\"4\"><a href=\"http://rezedit.de\"><img src='file:///"+Reha.proghome+"icons/kontact_contacts.png' width=52 height=52 border=0></a></th>");
		buf1.append("<td class=\"spalte1\" align=\"right\">");
		buf1.append("Behandlungszeit abgelaufen");
		buf1.append("</td><td class=\"spalte2\" align=\"left\">");
		buf1.append("<b>"+(String) vec.get(0)+"</b>");
		buf1.append("</td>");
		buf1.append("</tr>");
		
		buf1.append("<tr>");
		buf1.append("<td class=\"spalte1\" align=\"right\">");
		buf1.append("Patient");
		buf1.append("</td><td class=\"spalte2\" align=\"left\">");
		buf1.append("<b>"+vec.get(2)+"</b>");
		buf1.append("</td>");
		buf1.append("</tr>");

		buf1.append("<tr>");
		buf1.append("<td class=\"spalte1\" align=\"right\">");
		buf1.append("Raum / Kabine");
		buf1.append("</td><td class=\"spalte2\" align=\"left\">");
		buf1.append("<b>"+vec.get(1)+"</b>");
		buf1.append("</td>");
		buf1.append("</tr>");
		
		buf1.append("</table>");
		buf1.append("</font>");
		buf1.append("</div>");
		buf1.append("</html>");

		return buf1.toString();
	}
}
