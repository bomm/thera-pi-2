package patientenFenster;

import hauptFenster.Reha;
import hauptFenster.SuchenDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.rtf.RTFEditorKit;

import krankenKasse.KassenFormulare;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;


import rehaContainer.RehaTP;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.PatTools;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.TerminFenster;
import terminKalender.datFunk;

import RehaInternalFrame.JPatientInternal;

import benutzerVerwaltung.BenutzerVerwaltung;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.mysql.jdbc.ResultSetMetaData;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;

import events.PatStammEvent;
import events.PatStammEventClass;
import events.PatStammEventListener;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


public class PatGrundPanel extends JXPanel implements KeyListener, PatStammEventListener{
	
public JXPanel patGrund = null;
public static PatGrundPanel thisClass = null;
public JFormattedTextField tfsuchen;
public JPatTextField[] ptfield = {null,null,null,null,null,null,
		null,null,null,null,null,null,null,null,null};
public JTextArea ta = null;
public ActionListener gplst = null;
public MouseListener ml = null;
public FocusListener fl = null;
public JComboBox jcom;
/*****************/
public JEditorPane jtp;
public DefaultStyledDocument m_doc;
public StyleContext  m_context;
public RTFEditorKit redit;
/*****************/
public KeyListener kli = null;
public String aktPatID = "";
private Object sucheComponent = null;
private PatStammEventClass ptp = null;
private Component reverseFocus = null;
static MyStammFocusTraversalPolicy myPolicy;
public JPatientInternal jry = null;
public JComponent lastFocus = null;
/*********Die einzelnen Tab-Seiten***********/
AktuelleRezepte aktRezept = null;
Historie historie = null;
TherapieBerichte berichte = null;
/********************/
public JButton[] memobut = {null,null,null,null,null,null};

public JTextArea[] pmemo = {null,null};
public JTabbedPane memotab = null;
public int inMemo = -1;

public JButton[] jbut = {null,null,null,null}; 
public Vector<String> patDaten = new Vector<String>();
public String lastseek = "";
public boolean neuDlgOffen = false;
private Vector<String> titel = new Vector<String>();
private Vector<String> formular = new Vector<String>();

public JLabel[] rezlabs = {null,null,null,null,null,
							null,null,null,null,null,
							null,null,null,null,null};
public JTextArea rezdiag = null;

public ImageIcon[] imgzuzahl = {null,null,null};

public Vector vecaktrez = null;

public ImageIcon[] imgs = {null,null,null,null,null};
public JLabel[] imglabs = {null,null,null,null,null};

public Font font = null;
public Font fehler = new Font("Courier",Font.ITALIC,13);
public int aid = -1;
public int kid = -1;
public int autoPatid = -1;


private JRtaTextField formularid = new JRtaTextField("NIX",false);
private int iformular;
public PatGrundPanel(JPatientInternal jry){
	super();
	thisClass = this;
	ptp = new PatStammEventClass();
	ptp.addPatStammEventListener((PatStammEventListener)this);
	this.addFocusListener(new FocusListener(){
		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
			 		   //System.out.println("GrundPanel hat Focus erhalten");
						tfsuchen.requestFocus();			 		   
			 	   }
			}); 	   
		}
		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			lastFocus = ((JComponent)e.getSource());
	 		   //System.out.println("GrundPanel hat Focus verloren");
		}
		
	});


	this.jry = jry;
	kli = new KeyListener(){
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyCode() == 10){
				if(((JComponent)e.getSource()).getName() != null){
					if( ((JComponent) e.getSource()).getName().equals("suchenach") ){
						starteSuche();
					}
					
				}
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
		
	};
	
	 gplst = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			doMemoAction(arg0);
		}
		
	};
	ml = new MouseListener(){

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			if(arg0.getSource() instanceof JLabel){
				if(((JComponent)arg0.getSource()).getName().equals("Suchen")){
					PatGrundPanel.thisClass.starteSuche();
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			//arg0.getComponent().requestFocus();
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
		
	};
	
	setOpaque(false);
	setBackground(Color.WHITE);
	setBorder(BorderFactory.createEmptyBorder());
	setLayout(new BorderLayout());
	
	ObenPanel op = new ObenPanel();
	op.addFocusListener(getFocusListener());
	add(op,BorderLayout.NORTH);

	JXPanel gridp = new JXPanel(new GridLayout(1,1));
	gridp.setOpaque(false);

	HauptPanel hp = new HauptPanel();
	hp.addFocusListener(getFocusListener());

	gridp.add(hp);
	add(gridp,BorderLayout.CENTER);	

	new SwingWorker<Void,Void>(){

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			imgzuzahl[0] = SystemConfig.hmSysIcons.get("zuzahlfrei");
			imgzuzahl[1] = SystemConfig.hmSysIcons.get("zuzahlok");			
			imgzuzahl[2] = SystemConfig.hmSysIcons.get("zuzahlnichtok");
			return null;
		}
		
	}.execute();
	add(new ButtonPanel(),BorderLayout.SOUTH);
	new SwingWorker<Void,Void>(){

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}.execute();


	final ObenPanel xop = op;
	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run()
	 	   {
	 		    Vector<Component> newPolicy = new Vector<Component>();
	 		   font = new Font("Courier",Font.BOLD,13);
	 		   
	 		    //Font font = new Font("Tahoma",Font.BOLD,11);
	 		    for(int i = 0;i < ptfield.length;i++){
	 		    	newPolicy.add(ptfield[i]);
	 		    	ptfield[i].setForeground(Color.BLUE);
	 		    	ptfield[i].setFont(font);
	 		    	ptfield[i].addFocusListener(getTextFieldFocusListener());
	 		    }
	 		    myPolicy = new MyStammFocusTraversalPolicy(newPolicy);
	 		   xop.setFocusCycleRoot(true);
	 		    xop.setFocusTraversalPolicy(myPolicy);
	 		    //JPatientInternal.thisClass.setFocusTraversalPolicy(myPolicy);
	 		   ptfield[2].setForeground(Color.RED);
	 		   ptfield[3].setForeground(Color.RED);
	 		   ptfield[4].setForeground(Color.RED);
	 		   ptfield[5].setEditable(false);
	 		   ptfield[5].setBackground(Color.WHITE);
	 		   tfsuchen.requestFocus();
	 		   PatGrundPanel.thisClass.jry.setSpecialActive(true);
	 		   	//tf[0].requestFocusInWindow();
	 	   }
	});
	
	SwingUtilities.invokeLater(new Runnable(){
		public  void run(){
			KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
			PatGrundPanel.thisClass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
			PatGrundPanel.thisClass.getActionMap().put("doSuchen", new PatientAction());
			stroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.ALT_MASK);
			PatGrundPanel.thisClass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doNeu");
			PatGrundPanel.thisClass.getActionMap().put("doNeu", new PatientAction());	
			stroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK);
			PatGrundPanel.thisClass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doEdit");
			PatGrundPanel.thisClass.getActionMap().put("doEdit", new PatientAction());
			stroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK);
			PatGrundPanel.thisClass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doDelete");
			PatGrundPanel.thisClass.getActionMap().put("doDelete", new PatientAction());
			stroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.ALT_MASK);
			PatGrundPanel.thisClass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doFormulare");
			PatGrundPanel.thisClass.getActionMap().put("doFormulare", new PatientAction());
			holeFormulare();
			if(TerminFenster.thisClass != null){
		    	//TerminFenster.thisClass.setUpdateVerbot(true);
		    }

   	  	}
	});
	
	
	validate();
}
public void setzeFocus(){
	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run(){
		 		  if(! tfsuchen.hasFocus()){
		 			  if(! jry.getActive()){
		 				  //System.out.println("Frame ist nicht aktiviert");
		 				  jry.activateInternal();
		 				  SwingUtilities.invokeLater(new Runnable(){
		 					  public  void run(){
		 						  tfsuchen.requestFocus();
		 					  }
		 				  }); 	   	
	 				  }else{
	 					 //System.out.println("Frame ist bereits aktiviert");
	 					 SwingUtilities.invokeLater(new Runnable(){
							  public  void run(){
								  tfsuchen.requestFocus();
							  }
	 					 }); 	   	
	 				  }
				  }else{
					  //System.out.println("Frame ist bereits aktiviert");
					  SwingUtilities.invokeLater(new Runnable(){
						  public  void run(){
							  tfsuchen.requestFocus();
						  }
					  }); 	   	
				  }
		 	   }
	}); 	   	
}
public boolean sucheHatFocus(){
	if(tfsuchen.hasFocus()){
		return true;
	}else{
		return false;
	}
}

public void holeFormulare(){
	new SwingWorker<Void,Void>(){

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
			int forms = inif.getIntegerProperty("Formulare", "PatientFormulareAnzahl");
			for(int i = 1; i <= forms; i++){
				titel.add(inif.getStringProperty("Formulare","PFormularText"+i));			
				formular.add(inif.getStringProperty("Formulare","PFormularName"+i));
			}	
			return null;
		}
		
	}.execute();
	
}
private void starteFormulare(){
	iformular = -1;
	KassenFormulare kf = new KassenFormulare(Reha.thisFrame,titel,formularid);
	Point pt = jbut[3].getLocationOnScreen();
	kf.setLocation(pt.x-100,pt.y+25);
	kf.setModal(true);
	kf.setVisible(true);
	iformular = new Integer(formularid.getText());
	kf = null;
	if(iformular >=0){
		String sdatei = formular.get(iformular);
		OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+sdatei);
	}
	/*
	Set entries = SystemConfig.hmAdrPDaten.entrySet();
    Iterator it = entries.iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      System.out.println(entry.getKey() + "-->" + entry.getValue());
    }
    */
}

public void delete(){
	if(!aktPatID.equals("")){
		String spat = ptfield[2].getText().trim()+", "+ptfield[3].getText().trim()+", geb.am "+ptfield[4].getText().trim();
    	int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie Patient -> "+spat+" <- wirklich löschen??", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
    	if(frage != JOptionPane.YES_OPTION){
    		return;
    	}
		String stmt = "delete from pat5 where pat_intern='"+aktPatID+"'";
		new ExUndHop().setzeStatement(stmt);
		allesAufNull();
		setzeFocus();
	}else{
		JOptionPane.showMessageDialog(null, "Depp - welchen Patient bitteschön wollen Sie löschen?");
		setzeFocus();
		return;
	}
}
public void edit(){
	if(!aktPatID.equals("")){
		PatGrundPanel.thisClass.neuanlagePatient(false,"");	
	}else{
		JOptionPane.showMessageDialog(null, "Depp - welchen Patient bitteschön wollen Sie editieren?");
		setzeFocus();
		return;
	}
}
public void neu(){
	PatGrundPanel.thisClass.neuanlagePatient(true,"");		
}

private void allesAufNull(){
	/******************************************************************************/
	// erst die sichtbaren Edits löschen
	for(int i = 0; i <15;i++){
		ptfield[i].setText("");
	}
	// aktPatID zurücksetzten dann ist in weiteres löschen nicht mehr möglich
	aktPatID = "";
	autoPatid = -1;
	// jetzt das RezeptPanel KeinRezept anhängen
	aktRezept.setzeRezeptPanelAufNull(true);
	// dann die Icons löschen
	for(int i = 0; i <imglabs.length;i++){
		if(imglabs[i].getIcon() != null){
			imglabs[i].setIcon(null);			
		}
	}
	// Text der Memofelder löschen
	PatGrundPanel.thisClass.pmemo[0].setText("");
	PatGrundPanel.thisClass.pmemo[1].setText("");

	
}

class PatientAction extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
        //System.out.println("Patient Action = "+e.getActionCommand());
        //System.out.println(e);
        if(e.getActionCommand().equals("f")){
        	 PatGrundPanel.thisClass.tfsuchen.requestFocus();
        }
        if(e.getActionCommand().equals("n")){
        	neu();
        }
        if(e.getActionCommand().equals("e")){
        	edit();

        }	            
        if(e.getActionCommand().equals("l")){
       		delete();
        }	            
        if(e.getActionCommand().equals("b")){
        	PatGrundPanel.thisClass.starteFormulare();
        }	            
    }
}


public FocusListener getTextFieldFocusListener(){
	FocusListener fl = new FocusListener(){
		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			//System.out.println(((JComponent)e.getSource()).getParent().getParent());
			if(((JComponent)e.getSource()).getName().equals("suchenach") && inMemo > -1 ){
				//JOptionPane.showMessageDialog(null,"Bitte zuerst das Memofeld abspeichern");
				PatGrundPanel.thisClass.pmemo[inMemo].requestFocus();
				//return;
			}
			if(!jry.getActive()){
				jry.setSpecialActive(true);

			}	
			
		}
		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	};
	return fl;
}

public FocusListener getFocusListener(){
	FocusListener fl = new FocusListener(){
		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			//System.out.println(((JComponent)e.getSource()).getParent().getParent());
			if(!jry.getActive()){
				jry.setSpecialActive(true);
			}	
			//tfsuchen.requestFocusInWindow();
		}
		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	};
	return fl;
}



public void starteSuche(){
	//System.out.println("Suche wurde aktiviert");
	if(tfsuchen.getText().trim().equals("")){
		String cmd = "<html>Sie haben <b>kein</b> Suchkriterium eingegeben.<br>"+
		"Das bedeutet Sie laden den <b>kompletten Patientenstamm!!!<b><br><br>"+
		"Wollen Sie das wirklich?";
		int anfrage = JOptionPane.showConfirmDialog(null, cmd,"Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.NO_OPTION){
			return;
		}
	}
	if (sucheComponent != null){
		Point thispoint = PatGrundPanel.thisClass.getLocationOnScreen();
		((SuchenDialog) sucheComponent).setLocation(thispoint.x+30, thispoint.y+80);
		if(! tfsuchen.getText().trim().equals(lastseek)){
			((SuchenDialog) sucheComponent).suchDasDing(new String(tfsuchen.getText()));
			lastseek = tfsuchen.getText().trim();
		}
		((SuchenDialog) sucheComponent).setVisible(true);
		//((SuchenDialog) sucheComponent).setzeFocusAufSucheFeld();
	}else{
		sucheComponent = new SuchenDialog(null,PatGrundPanel.thisClass,tfsuchen.getText());
		Point thispoint = PatGrundPanel.thisClass.getLocationOnScreen();
		((SuchenDialog) sucheComponent).setLocation(thispoint.x+30, thispoint.y+80);
		((SuchenDialog) sucheComponent).setVisible(true);
		lastseek = tfsuchen.getText().trim();
	}
	
}

public void doMemoAction(ActionEvent arg0){
	if(autoPatid == -1){
		return;
	}
	String sc = arg0.getActionCommand();
	if(sc.equals("kedit")){
		inMemo = 0;
		PatGrundPanel.thisClass.memobut[0].setEnabled(false);
		PatGrundPanel.thisClass.memobut[1].setEnabled(true);
		PatGrundPanel.thisClass.memobut[2].setEnabled(true);
		PatGrundPanel.thisClass.pmemo[0].setForeground(Color.RED);
		PatGrundPanel.thisClass.pmemo[0].setEditable(true);
		PatGrundPanel.thisClass.pmemo[0].setCaretPosition(0);
		PatGrundPanel.thisClass.memobut[3].setEnabled(false);
		return;
	}
	if(sc.equals("kedit2")){
		inMemo = 1;
		PatGrundPanel.thisClass.memobut[3].setEnabled(false);
		PatGrundPanel.thisClass.memobut[4].setEnabled(true);
		PatGrundPanel.thisClass.memobut[5].setEnabled(true);
		PatGrundPanel.thisClass.pmemo[1].setForeground(Color.RED);
		PatGrundPanel.thisClass.pmemo[1].setEditable(true);
		PatGrundPanel.thisClass.pmemo[1].setCaretPosition(0);
		PatGrundPanel.thisClass.memobut[0].setEnabled(false);
		return;
	}
	if(sc.equals("ksave")){
		PatGrundPanel.thisClass.memobut[0].setEnabled(true);
		PatGrundPanel.thisClass.memobut[1].setEnabled(false);
		PatGrundPanel.thisClass.memobut[2].setEnabled(false);
		PatGrundPanel.thisClass.pmemo[0].setForeground(Color.BLUE);
		PatGrundPanel.thisClass.pmemo[0].setEditable(false);
		PatGrundPanel.thisClass.memobut[3].setEnabled(true);
		String cmd = "update pat5 set anamnese='"+StringTools.Escaped(PatGrundPanel.thisClass.pmemo[0].getText())+"' where id='"+
		PatGrundPanel.thisClass.autoPatid+"'";
		new ExUndHop().setzeStatement(cmd);
		inMemo = -1;
		return;
	}
	if(sc.equals("ksave2")){
		PatGrundPanel.thisClass.memobut[3].setEnabled(true);
		PatGrundPanel.thisClass.memobut[4].setEnabled(false);
		PatGrundPanel.thisClass.memobut[5].setEnabled(false);
		PatGrundPanel.thisClass.pmemo[1].setForeground(Color.BLUE);
		PatGrundPanel.thisClass.pmemo[1].setEditable(false);
		PatGrundPanel.thisClass.memobut[0].setEnabled(true);
		String cmd = "update pat5 set pat_text='"+StringTools.Escaped(PatGrundPanel.thisClass.pmemo[1].getText())+"' where id='"+
		PatGrundPanel.thisClass.autoPatid+"'";
		new ExUndHop().setzeStatement(cmd);
		inMemo = -1;
		return;
	}
	if(sc.equals("kbreak")){
		PatGrundPanel.thisClass.memobut[0].setEnabled(true);
		PatGrundPanel.thisClass.memobut[1].setEnabled(false);
		PatGrundPanel.thisClass.memobut[2].setEnabled(false);
		PatGrundPanel.thisClass.pmemo[0].setForeground(Color.BLUE);
		PatGrundPanel.thisClass.pmemo[0].setEditable(false);
		PatGrundPanel.thisClass.memobut[3].setEnabled(true);
		PatGrundPanel.thisClass.pmemo[0].setText((String) SqlInfo.holeSatz("pat5", "anamnese", "id='"+autoPatid+"'", Arrays.asList(new String[] {})).get(0) );
		inMemo = -1;
		return;
	}
	if(sc.equals("kbreak2")){
		PatGrundPanel.thisClass.memobut[3].setEnabled(true);
		PatGrundPanel.thisClass.memobut[4].setEnabled(false);
		PatGrundPanel.thisClass.memobut[5].setEnabled(false);
		PatGrundPanel.thisClass.pmemo[1].setForeground(Color.BLUE);
		PatGrundPanel.thisClass.pmemo[1].setEditable(false);
		PatGrundPanel.thisClass.memobut[0].setEnabled(true);		
		PatGrundPanel.thisClass.pmemo[1].setText((String) SqlInfo.holeSatz("pat5", "pat_text", "id='"+autoPatid+"'", Arrays.asList(new String[] {})).get(0) );
		inMemo = -1;
		return;
	}

}


public void neuanlagePatient(boolean lneu,String feldname){
	if(neuDlgOffen){
		return;
	}
	neuDlgOffen = true;
	PatNeuDlg neuPat = new PatNeuDlg();
	//JDialog neuPat = new JDialog();
	PinPanel pinPanel = new PinPanel();
	pinPanel.setName("PatientenNeuanlage");
	pinPanel.getGruen().setVisible(false);
	neuPat.setPinPanel(pinPanel);
	if(lneu){
		neuPat.getSmartTitledPanel().setTitle("Patient Neuanlage");	
	}else{
		neuPat.getSmartTitledPanel().setTitle("editieren ---> "+ptfield[2].getText().trim()+", "+ptfield[3].getText().trim()+", geboren am: "+ptfield[4].getText().trim());		
	}
	neuPat.setSize(480,768);
	neuPat.setPreferredSize(new Dimension(480,768));
	neuPat.getSmartTitledPanel().setPreferredSize(new Dimension (480,768));
	neuPat.setPinPanel(pinPanel);
	neuPat.getSmartTitledPanel().setContentContainer(new PatNeuanlage(new Vector(),lneu,feldname));
	neuPat.getSmartTitledPanel().getContentContainer().setName("PatientenNeuanlage");
    neuPat.setName("PatientenNeuanlage");
	/*
	try {
		Thread.sleep(100);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	*/
    
	
	neuPat.setLocationRelativeTo(null);
	neuPat.setTitle("Patienten Neuanlage");

	neuPat.setModal(true);
	neuPat.pack();	
	neuPat.setVisible(true);

	
	//neuPat.setVisible(false);

	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run(){
	 		   setzeFocus();
	 	   }
	}); 	   	
	//neuPat = null;
	neuPat.dispose();
	neuPat = null;

	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run(){
				Runtime r = Runtime.getRuntime();
			    r.gc();
			    long freeMem = r.freeMemory();
			    System.out.println("Freier Speicher nach  gc():    " + freeMem);
	 	   }
	});


	System.out.println("Pat Neu/Ändern ist disposed");
	neuDlgOffen = false;
	

}
/*********************************
 *  * 
 * @author admin
 * Suche Panel
 *********************************/
class ObenPanel extends JXPanel{
	public ObenPanel(){
		super();
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder());
		add(new SuchePanel(),BorderLayout.NORTH);
		add(new StammDatenPanel(),BorderLayout.SOUTH);
		validate();
	}
}
class SuchePanel extends JXPanel implements ActionListener{
	public SuchePanel(){
		super();
		/*
		R 231
		G 120
		B 23
		*/
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),100);
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Color.WHITE,new Color(231,120,23)};
	     //Color[] colors = {Color.WHITE,Colors.TaskPaneBlau.alpha(0.5f)};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));		
		
		setBorder(BorderFactory.createEmptyBorder());
		setPreferredSize(new Dimension(0,35));
		
		FormLayout lay = new FormLayout("3dlu,right:max(35dlu;p),3dlu,p,45dlu,fill:0:grow(0.10),0dlu ,right:max(39dlu;p),3dlu, p,45dlu,7dlu,"+
				//  2-teSpalte (13)  14  15 16     17            18   19      20             21   22  23    24 25  26      27                28
				"right:max(39dlu;p),3dlu,p,90,fill:0:grow(0.60),0dlu,7dlu,right:max(39dlu;p),3dlu,p,40dlu,2dlu,p,50dlu,fill:0:grow(0.30),0dlu,10dlu",
				// 1                 2  3  4   5  6	  7  8   9 10     11
		"fill:0:grow(0.50),p,fill:0:grow(0.50)");

		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		JLabel lbl = new JLabel("Kriterium:");
		add(lbl,cc.xy(2,2));
		jcom = new JComboBox(new String[] {"Name Vorname","Telefonnummer","Notizen","Versäumte Termine"});
		jcom.setBackground(new Color(247,209,176));
		add(jcom,cc.xyw(15, 2, 3));
		add(jcom,cc.xyw(4,2,8));
		
		lbl = new JLabel("finde Pat. -->");
		lbl.setName("Suchen");
		//lbl.setIcon(new ImageIcon(Reha.proghome+"icons/mag.png"));
		lbl.setIcon(SystemConfig.hmSysIcons.get("find"));
		lbl.addMouseListener(ml);
		add(lbl,cc.xy(13,2));
		tfsuchen = new JFormattedTextField();
		tfsuchen.setFont(new Font("Tahoma",Font.BOLD,11));
		tfsuchen.setBackground(Colors.PiOrange.alpha(0.15f));
		tfsuchen.setForeground(Color.WHITE);
		tfsuchen.setName("suchenach");
		tfsuchen.addKeyListener(kli);
    	tfsuchen.addFocusListener(getTextFieldFocusListener());		
		add(tfsuchen,cc.xyw(15, 2, 3));
		
		JToolBar jtb = new JToolBar();
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);
		/*
		ActionListener lst = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  String comm = e.getActionCommand();
		    	  if(comm.equals("patneuanlage")){
		    		  PatGrundPanel.thisClass.neuanlagePatient(true,"");
		    	  }
		      }
		};
		*/
		jbut[0] = new JButton();
		//jbut[0].setIcon(new ImageIcon(Reha.proghome+"icons/list-add.png"));
		jbut[0].setIcon(SystemConfig.hmSysIcons.get("neu"));
		jbut[0].setToolTipText("neuen Patient anlegen (Alt+N)");
		jbut[0].setActionCommand("neu");
		jbut[0].addActionListener(this);
		jtb.add(jbut[0]);
		jbut[1] = new JButton();
		//jbut[1].setIcon(new ImageIcon(Reha.proghome+"icons/edit.png"));
		jbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		jbut[1].setToolTipText("aktuellen Patient ändern/editieren (Alt+E)");		
		jbut[1].setActionCommand("edit");
		jbut[1].addActionListener(this);
		jtb.add(jbut[1]);
		jbut[2] = new JButton();
		//jbut[2].setIcon(new ImageIcon(Reha.proghome+"icons/list-remove.png"));
		jbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		jbut[2].setToolTipText("Patient löschen (Alt+L)");
		jbut[2].setActionCommand("delete");
		jbut[2].addActionListener(this);
		jtb.add(jbut[2]);
		jtb.addSeparator(new Dimension(40,0));
		jbut[3] = new JButton();
		//jbut[3].setIcon(new ImageIcon(Reha.proghome+"icons/mail_write_22.png"));
		//jbut[3].setIcon(new ImageIcon(Reha.proghome+"icons/drucker22.png"));
		jbut[3].setIcon(SystemConfig.hmSysIcons.get("print"));
		jbut[3].setToolTipText("Brief/Formular für Patient erstellen (Alt+B)");
		jbut[3].setActionCommand("formulare");
		jbut[3].addActionListener(this);
		jtb.add(jbut[3]);
 

		/*
		JButton jbut = new JButton();
		jbut.setIcon(new ImageIcon(Reha.proghome+"icons/list-add.png"));
		jbut.setToolTipText("neuen Patienten anlegen");
		jbut.setActionCommand("patneuanlage");
		jbut.addActionListener(lst);
		jtb.add(jbut);
		jbut = new JButton();
		jbut.setIcon(new ImageIcon(Reha.proghome+"icons/process-stop.png"));
		jbut.setToolTipText("aktuellen Patienten löschen");
		jtb.add(jbut);
		jtb.addSeparator(new Dimension(20,0));
		jbut = new JButton();
		jbut.setIcon(new ImageIcon(Reha.proghome+"icons/edit.png"));
		jbut.setToolTipText("aktuellen Patienten ändern");		
		jtb.add(jbut);
		jbut = new JButton();
		jbut.setIcon(new ImageIcon(Reha.proghome+"icons/Save_22x22.png"));
		jbut.setToolTipText("Änderungen speichern");		
		jtb.add(jbut);
*/
		add(jtb,cc.xyw(20,2,7));
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String sc = arg0.getActionCommand();
		if(sc.equals("neu")){
			neu();
			}
		if(sc.equals("edit")){
			edit();
		}
		if(sc.equals("delete")){
			delete();
		}
		if(sc.equals("formulare")){
			starteFormulare();
			setzeFocus();
		}
		System.out.println("ActionCommand = "+sc);
	}
}
/*********************************
 *  * 
 * @author admin
 * Suche Panel
 *********************************/
class ButtonPanel extends JXPanel{
	public ButtonPanel(){
		super();
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),15);//vorher 45
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Colors.PiOrange.alpha(0.5f),Color.WHITE};
	     //Color[] colors = {Colors.TaskPaneBlau.alpha(0.5f),Color.WHITE};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));		

		setBorder(BorderFactory.createEmptyBorder());
		setPreferredSize(new Dimension(0,10)); //vorher 35
		//JLabel lbl = new JLabel("Hie werden die Knöpfe für - neu, ändern, löschen, etc. - platziert");
		//lbl.setForeground(Color.RED);
		//add(lbl);		
	}
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
public void PatStammEventOccurred(PatStammEvent evt) {
	// TODO Auto-generated method stub
	//System.out.println("Event im Neuen PatStamm = "+evt);
	//System.out.println("Detail 0 = "+evt.getDetails()[0]);
	//System.out.println("Detail 1 = "+evt.getDetails()[1]);	
	if(evt.getDetails()[0].equals("#PATSUCHEN")){
		final String xpatint = evt.getDetails()[1].trim();
		aktPatID = new String(xpatint);
		// Anzeigedaten holen
		new Thread(){
			public void run(){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						new DatenHolen(xpatint);
						SwingUtilities.invokeLater(new Runnable(){
						 	   public  void run()
						 	   {
						 		   String titel = new String("Patient: "+PatGrundPanel.thisClass.ptfield[2].getText()+", "+
						 					PatGrundPanel.thisClass.ptfield[3].getText()+" geboren am: "+
						 					PatGrundPanel.thisClass.ptfield[4].getText());
						 			PatGrundPanel.thisClass.jry.setzeTitel(titel);
						 			//System.out.println("neuer Titel = "+titel);
						 	   }
						});
						return null;
					}
				}.execute();
			}
		}.start();
		// kmplette Patdaten holen		
		new Thread(){
			public void run(){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
									PatGrundPanel.thisClass.patDaten.clear();
									PatGrundPanel.thisClass.patDaten = SqlInfo.holeSatz("pat5", " * ", "pat_intern='"+xpatint+"'", Arrays.asList(new String[] {}));
									//System.out.println("Fertig mit einlesen der kompletten Pat-Daten");
									new Thread(){
										public void run(){
											PatTools.constructPatHMap();		
										}
									}.start();
						return null;
					}
				}.execute();
			}
		}.start();
		// Rezeptdaten holen
		new Thread(){
			public void run(){		
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						aktRezept.holeRezepte(xpatint,"");
						return null;
					}
				}.execute();
			}
		}.start();
		// Historie holen
		new Thread(){
			public void run(){		
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						historie.holeRezepte(xpatint,"");
						return null;
					}
				}.execute();
			}
		}.start();
		new Thread(){
			public void run(){		
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						berichte.holeBerichte(xpatint,"");
						return null;
					}
				}.execute();
			}
		}.start();

	}
	if(evt.getDetails()[0].equals("#CLOSING")){
		if(sucheComponent != null){
			this.ptp.removePatStammEventListener((PatStammEventListener) this);
			((SuchenDialog) sucheComponent).dispose();	
			//System.out.println("******Suchendialog wird gelöscht, Eventlistener removed*********");
		}
	}
	if(evt.getDetails()[0].equals("#FOCUSIEREN")){
		tfsuchen.requestFocus();
	}
	if(evt.getDetails()[0].equals("#KORRIGIEREN")){
		final PatStammEvent evx = evt;
		final String feld = (String)evt.getDetails()[1];
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//System.out.println("Korrigieren->"+evx.getSource());
				editFeld(feld);
				return null;
			}
		}.execute();
	}


}

public void editFeld(String feldname) {
	// TODO Auto-generated method stub
//	System.out.println("Feld ->"+xname+" <- hat editieren angefordert");
	if(! thisClass.aktPatID.equals("")){
		neuanlagePatient(false, feldname);		
	}
}


/*********************************
 *  * 
 * @author admin
 * Ende Klasse PatGrundPanel()
 *********************************/
}
class DatenHolen{
	DatenHolen(String patint){
	Statement stmt = null;
	ResultSet rs = null;
	String sstmt = new String();

	sstmt = "select * from pat5 where PAT_INTERN ='"+patint+"'";
		
	try {
		stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try{
		rs = stmt.executeQuery(sstmt);
		Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		while( rs.next()){
			
			ResultSetMetaData met = (ResultSetMetaData) rs.getMetaData();
			int cols = met.getColumnCount();
			int type;
			String colname = "";
			String colvalue = "";
			for(int i = 0; i < 15; i++){
				colname = PatGrundPanel.thisClass.ptfield[i].getName();
				if(colname.equals("GEBOREN")){
					colvalue = datFunk.sDatInDeutsch(rs.getString("GEBOREN"))+" "; 	
				}else{
					colvalue = rs.getString(colname);
				}
				PatGrundPanel.thisClass.ptfield[i].setText(StringTools.EGross(colvalue));
			}
			//InputStream ins = null;
			String instring = (rs.getString("ANAMNESE")==null ? "" : rs.getString("ANAMNESE"));
			//System.out.println("Anamnese inhalt = "+instring);
			if(instring.equals("")){
				PatGrundPanel.thisClass.pmemo[0].setText("");
			}else{
				PatGrundPanel.thisClass.pmemo[0].setText(instring);				
			}
			instring = (rs.getString("PAT_TEXT")==null ? "" : rs.getString("PAT_TEXT"));
			//System.out.println("Pat_text inhalt = "+instring);			
			if(instring.equals("")){
				PatGrundPanel.thisClass.pmemo[1].setText("");
			}else{
				PatGrundPanel.thisClass.pmemo[1].setText(instring);				
			}
			PatGrundPanel.thisClass.autoPatid = rs.getInt("id");
			PatGrundPanel.thisClass.aid = StringTools.ZahlTest(rs.getString("arztid"));
			PatGrundPanel.thisClass.kid = StringTools.ZahlTest(rs.getString("kassenid"));
			if(PatGrundPanel.thisClass.aid < 0){
				PatGrundPanel.thisClass.ptfield[13].setForeground(Color.RED);
				PatGrundPanel.thisClass.ptfield[13].setFont(PatGrundPanel.thisClass.fehler);
			}else{
				PatGrundPanel.thisClass.ptfield[13].setForeground(Color.BLUE);
				PatGrundPanel.thisClass.ptfield[13].setFont(PatGrundPanel.thisClass.font);
			}
			if(PatGrundPanel.thisClass.kid < 0){
				PatGrundPanel.thisClass.ptfield[14].setForeground(Color.RED);				
				PatGrundPanel.thisClass.ptfield[14].setFont(PatGrundPanel.thisClass.fehler);
			}else{
				PatGrundPanel.thisClass.ptfield[14].setForeground(Color.BLUE);				
				PatGrundPanel.thisClass.ptfield[14].setFont(PatGrundPanel.thisClass.font);
			}

		}
		Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));


	}catch(SQLException ev){
		System.out.println("SQLException: " + ev.getMessage());
		System.out.println("SQLState: " + ev.getSQLState());
		System.out.println("VendorError: " + ev.getErrorCode());
	} 


	finally {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqlEx) { // ignore }
				rs = null;
			}
		}
	
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqlEx) { // ignore }
				stmt = null;
			}
		}
	}
  }
}

/*************************************/
class XRezepteHolen{
	XRezepteHolen(String patint){
	Statement stmt = null;
	ResultSet rs = null;
	String sstmt = new String();

	sstmt = "select * from verordn where PAT_INTERN ='"+patint+"' ORDER BY REZ_DATUM";
		
	try {
		stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try{
		rs = stmt.executeQuery(sstmt);
		Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		while( rs.next()){
			//PatGrundPanel.thisClass.tabaktrez.get
			//System.out.println("Aktuelles Rezept gefunden -> "+rs.getString("REZ_NR"));			
		}
		Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}catch(SQLException ev){
		System.out.println("SQLException: " + ev.getMessage());
		System.out.println("SQLState: " + ev.getSQLState());
		System.out.println("VendorError: " + ev.getErrorCode());
	}	
	finally {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqlEx) { // ignore }
				rs = null;
			}
		}	
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqlEx) { // ignore }
				stmt = null;
			}
		}
	}
  }
}
class StammDatenPanel extends JXPanel{
	public StammDatenPanel(){
		super();
		setPreferredSize(new Dimension(0,100));
		setOpaque(true);
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),40);
	     //Point2D end = new Point2D.Float(getParent().getParent().getWidth(),getParent().getParent().getHeight());
	     float[] dist = {0.0f, 1.00f};
	     Color[] colors = {Colors.PiOrange.alpha(0.5f),Color.WHITE};	     
	     //Color[] colors = {Colors.TaskPaneBlau.alpha(0.50f), Color.WHITE};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));
		
		getStammDaten();
	}
	public void getStammDaten(){
/*
		FormLayout lay = new FormLayout(
	               "0:grow(0.33), 2px, 0:grow(0.33),2px,0:grow(0.33)",
	               "fill:0:grow(1.00)");
*/
		FormLayout lay = new FormLayout(
	               "fill:0:grow(1.00)",
	               "fill:0:grow(1.00)");
			CellConstraints cc = new CellConstraints();
			setLayout(lay);
	    JScrollPane span = getAnrede();
	    //span.setPreferredSize(new Dimension(270,100));
	    JScrollPane jscr = new JScrollPane();
	    jscr.setOpaque(false);
	    jscr.setBorder(null);
	    jscr.setViewportBorder(null);
	    jscr.getViewport().setOpaque(false);	    
	    jscr.setViewportView(span);
	    jscr.getVerticalScrollBar().setUnitIncrement(15);
	    jscr.validate();
	    add(jscr,cc.xy(1,1));
/*	    
	    JXPanel pan = new JXPanel();
	    pan.setBackground(Color.YELLOW);
	    add(pan,cc.xy(3,1));
	    pan = new JXPanel();
	    pan.setBackground(Color.GREEN);
	    add(pan,cc.xy(5,1));
*/	    
		validate();
	}
	public JScrollPane getAnrede(){
		JXPanel anredepan = new JXPanel(new BorderLayout());
		anredepan.setBorder(null);
		//anredepan.setOpaque(false);
		//Reha.RehaPainter[0]
		setOpaque(true);
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),100);
	     //Point2D end = new Point2D.Float(getParent().getParent().getWidth(),getParent().getParent().getHeight());
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Colors.PiOrange.alpha(0.70f),Color.WHITE};
	     //Color[] colors = {Colors.TaskPaneBlau.alpha(0.70f), Color.WHITE};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     anredepan.setBackgroundPainter(new CompoundPainter(mp));
		
		
	
/*
 *                             //            1           2           3   4   5         6            7     8                  9    10
		FormLayout lay = new FormLayout("3dlu,right:max(35dlu;p),3dlu,p,45dlu,fill:0:grow(1.00),0dlu ,right:max(35dlu;p),3dlu, p,45dlu,7dlu",
				// 1                 2  3  4   5  6	  7  8   9 10     11
				"fill:0:grow(0.50),2dlu,p,1dlu,p,1dlu,p,1dlu,p,2dlu,fill:0:grow(0.50)");

 */
		
							//            1           2           3   4      5         6            7     8                  9    10  11   12
		FormLayout lay = new FormLayout("3dlu,right:max(38dlu;p),3dlu,p,53dlu,fill:0:grow(0.10),0dlu ,right:max(39dlu;p),3dlu, p,45dlu,7dlu,"+
				//  2-teSpalte (13)  14  15 16     17            18   19      20             21   22  23    24 25  26      27                28
				"right:max(38dlu;p),3dlu,p,90,fill:0:grow(0.60),0dlu,7dlu,right:max(38dlu;p),3dlu,p,40dlu,2dlu,p,50dlu,fill:0:grow(0.30),0dlu,10dlu",
				// 1                 2  3  4   5  6	  7  8   9 10     11
				"fill:0:grow(0.50),0dlu,p,1px,p,1px,p,1px,p,0dlu,fill:0:grow(0.50)");
		PanelBuilder pbui = new PanelBuilder(lay);
		pbui.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		//pbui.setLayout(lay);
		pbui.getPanel().addFocusListener(PatGrundPanel.thisClass.getFocusListener());
		pbui.add(new JLabel("Anrede"),cc.xy(2,3));
		PatGrundPanel.thisClass.ptfield[0] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[0].setName("ANREDE");
		pbui.add(PatGrundPanel.thisClass.ptfield[0],cc.xyw(4,3,2));
		
		pbui.add(new JLabel("Titel"),cc.xy(8,3));
		PatGrundPanel.thisClass.ptfield[1] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[1].setName("TITEL");
		pbui.add(PatGrundPanel.thisClass.ptfield[1],cc.xyw(10,3,2));		
		/***************/
		pbui.add(new JLabel("Name"),cc.xy(2,5));
		PatGrundPanel.thisClass.ptfield[2] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[2].setName("N_NAME");		
		pbui.add(PatGrundPanel.thisClass.ptfield[2],cc.xyw(4,5,8));		

		pbui.add(new JLabel("Vorname"),cc.xy(2,7));
		PatGrundPanel.thisClass.ptfield[3] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[3].setName("V_NAME");		
		pbui.add(PatGrundPanel.thisClass.ptfield[3],cc.xyw(4,7,8));		

		pbui.add(new JLabel("Geboren"),cc.xy(2,9));
		PatGrundPanel.thisClass.ptfield[4] = new JPatTextField("XGROSS",false); //new JPatTextField("DATUM",true);
		PatGrundPanel.thisClass.ptfield[4].setName("GEBOREN");		
		pbui.add(PatGrundPanel.thisClass.ptfield[4],cc.xyw(4,9,2));
		
		pbui.add(new JLabel("Kunden-Nr."),cc.xy(8,9));
		PatGrundPanel.thisClass.ptfield[5] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[5].setName("PAT_INTERN");
		pbui.add(PatGrundPanel.thisClass.ptfield[5],cc.xyw(10,9,2));		
/**********2-te Spalte**************/
		pbui.add(new JLabel("Telefon(p)"),cc.xy(13,3));
		PatGrundPanel.thisClass.ptfield[6] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[6].setName("TELEFONP");
		pbui.add(PatGrundPanel.thisClass.ptfield[6],cc.xyw(15,3,3));		

		pbui.add(new JLabel("Telefon(g)"),cc.xy(13,5));
		PatGrundPanel.thisClass.ptfield[7] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[7].setName("TELEFONG");		
		pbui.add(PatGrundPanel.thisClass.ptfield[7],cc.xyw(15,5,3));		

		pbui.add(new JLabel("Mobil"),cc.xy(13,7));
		PatGrundPanel.thisClass.ptfield[8] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[8].setName("TELEFONM");
		pbui.add(PatGrundPanel.thisClass.ptfield[8],cc.xyw(15,7,3));		

		pbui.add(new JLabel("Email"),cc.xy(13,9));
		PatGrundPanel.thisClass.ptfield[9] = new JPatTextField("KLEIN",false);
		PatGrundPanel.thisClass.ptfield[9].setName("EMAILA");
		pbui.add(PatGrundPanel.thisClass.ptfield[9],cc.xyw(15,9,3));		

/**********3-te Spalte**************/
		pbui.add(new JLabel("Strasse"),cc.xy(20,3));
		PatGrundPanel.thisClass.ptfield[10] = new JPatTextField("XROSS",false);
		PatGrundPanel.thisClass.ptfield[10].setName("STRASSE");
		pbui.add(PatGrundPanel.thisClass.ptfield[10],cc.xyw(22,3,6));		

		pbui.add(new JLabel("PLZ, Ort"),cc.xy(20,5));
		PatGrundPanel.thisClass.ptfield[11] = new JPatTextField("ZAHLEN",false);
		PatGrundPanel.thisClass.ptfield[11].setName("PLZ");		
		pbui.add(PatGrundPanel.thisClass.ptfield[11],cc.xyw(22,5,2));		

		PatGrundPanel.thisClass.ptfield[12] = new JPatTextField("XGROSS",false);
		PatGrundPanel.thisClass.ptfield[12].setName("ORT");		
		pbui.add(PatGrundPanel.thisClass.ptfield[12],cc.xyw(25,5,4));		
		
		pbui.add(new JLabel("Krankenkasse"),cc.xy(20,7));
		PatGrundPanel.thisClass.ptfield[14] = new JPatTextField("XROSS",false);
		PatGrundPanel.thisClass.ptfield[14].setName("KASSE");
		pbui.add(PatGrundPanel.thisClass.ptfield[14],cc.xyw(22,7,6));		
		
		pbui.add(new JLabel("Hausarzt"),cc.xy(20,9));
		PatGrundPanel.thisClass.ptfield[13] = new JPatTextField("XROSS",false);
		PatGrundPanel.thisClass.ptfield[13].setName("ARZT");
		pbui.add(PatGrundPanel.thisClass.ptfield[13],cc.xyw(22,9,6));		

		/*
		JLabel patlab = new JLabel("");
		patlab.setIcon(new ImageIcon(Reha.proghome+"icons/patient-48x48.png"));
		pbui.add(patlab,cc.xywh(25,7,4,3));
		*/
		//anredepan.addMouseListener(HauptPanel.thisClass.ml);		
		pbui.getPanel().validate();
		
		anredepan.add(pbui.getPanel(),BorderLayout.CENTER);
		JScrollPane scrp = new JScrollPane(anredepan);
		scrp.setBorder(null);
		scrp.setViewportBorder(null);
		scrp.getViewport().setOpaque(false);
		scrp.setOpaque(false);
		return scrp;
	}
}
/***********************************************/

class MyStammFocusTraversalPolicy extends FocusTraversalPolicy{
Vector<Component> order;

public MyStammFocusTraversalPolicy(Vector<Component> order) {
this.order = new Vector<Component>(order.size());
this.order.addAll(order);
}

public Component getComponentAfter(Container focusCycleRoot,
        Component aComponent)
{
int idx = (order.indexOf(aComponent) + 1) % order.size();
return order.get(idx);
}

public Component getComponentBefore(Container focusCycleRoot,
         Component aComponent)
{
int idx = order.indexOf(aComponent) - 1;
if (idx < 0) {
idx = order.size() - 1;
}
return order.get(idx);
}

public Component getDefaultComponent(Container focusCycleRoot) {
return order.get(0);
}

public Component getLastComponent(Container focusCycleRoot) {
return order.lastElement();
}

public Component getFirstComponent(Container focusCycleRoot) {
return order.get(0);
}
}

class PatNeuDlg extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	private RehaTPEventClass rtp = null;
	public PatNeuDlg(){
		super(null,"PatientenNeuanlage");
		this.setName("PatientenNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void RehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					this.dispose();
					System.out.println("****************Patient Neu/Ändern -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			dispose();
			System.out.println("****************Patient Neu/Ändern -> Listener entfernt (Closed)**********");
		}
		
		
	}
	
	
}
class JPatTextField extends JRtaTextField{

	public JPatTextField(String type, boolean selectWhenFocus) {
		super(type, selectWhenFocus);
		// TODO Auto-generated constructor stub
		setOpaque(false);
		setEditable(false);
		setBorder(null);
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					final String xname = getName();
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							// TODO Auto-generated method stub
							//System.out.println("In Mousedoppelklick "+xname);
							String s1 = new String("#KORRIGIEREN");
							String s2 = xname;
							PatStammEvent pEvt = new PatStammEvent(this);
							pEvt.setPatStammEvent("PatSuchen");
							pEvt.setDetails(s1,s2,"") ;
							PatStammEventClass.firePatStammEvent(pEvt);	
							return null;
						}
						
					}.execute();
				}
			}
			
			
		});
	}
	
}