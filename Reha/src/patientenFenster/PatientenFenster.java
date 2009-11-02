package patientenFenster;

import hauptFenster.ContainerConfig;

import hauptFenster.SuchenDialog;
import hauptFenster.ProgLoader;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.border.DropShadowBorder;

import rehaContainer.RehaTP;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import events.PatStammEvent;
import events.PatStammEventClass;
import events.PatStammEventListener;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class PatientenFenster extends JXPanel implements RehaTPEventListener, PatStammEventListener, FocusListener, KeyListener, MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5824244708237950867L;
	private int setOben;
	JRtaTextField[] eingaben = {null,null,null,null,null,null,
										null,null,null,null,null,null};
	private JXPanel grundPanel=null;
	private JXPanel formPanel=null;
	private RehaTPEventClass rtp = null;
	private PatStammEventClass ptp = null;
	private Component reverseFocus = null;
	private Object sucheComponent = null;	
	public PatientenFenster thisClass;
	
	private PatientSuchen patThread = null;
	
	public boolean focusok = false;
	
	public PatientenFenster(int setOben,String name){
		super();
		this.setOben=setOben;
		this.setName(name);
		setLayout(new BorderLayout());
		thisClass = this;
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		ptp = new PatStammEventClass();
		ptp.addPatStammEventListener((PatStammEventListener)this);
		
		
		this.add(new JScrollPane(createFormPanel()),BorderLayout.CENTER);
		
		this.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent e) {    
				Reha.thisClass.shiftLabel.setText("Patient -Focus da");
				
			}	   
			public void focusLost(java.awt.event.FocusEvent e) {    
				Reha.thisClass.shiftLabel.setText("Patient -Focus weg");
			}   
		});	
		
		
	}
	/*******************************************************/
	private JPanel createFormPanel(){
		String name = this.getName();
		//formPanel.setAlpha(0.3f);
		FormLayout layout = 
			new FormLayout("20dlu,right:max(50dlu;p),4dlu,50dlu,4dlu,max(50dlu;p),right:max(50dlu;p),4dlu,4dlu,4dlu,75dlu",
					"20dlu,p,1dlu,p,1dlu,p,1dlu,p,1dlu,p,1dlu,p,1dlu,p,20dlu," +
					"20dlu,p,4dlu,p,4dlu,p,4dlu,p,20dlu,20dlu,p");
		
	
		PanelBuilder builder = new PanelBuilder(layout);
		builder.getPanel().setPreferredSize(new Dimension(1000,1000));
		builder.getPanel().setForeground(Color.WHITE);
		//builder.setPreferredSize(new Dimension(370,250));
		//builder.setBackgroundPainter(Reha.RehaPainter[0]);
		builder.getPanel().setPreferredSize(new Dimension(400,250));
		builder.getPanel().setOpaque(false);
		builder.getPanel().setName(this.getName());
		//builder.getPanel().setBackgroundPainter(Reha.RehaPainter[0]);
		CellConstraints cc = new CellConstraints();
		
		//JXLabel label = new JXLabel("Stammdaten");
		//label.setForeground(Color.WHITE);
		
		builder.addSeparator("Stammdaten",cc.xyw(2,1,7));
		JXLabel label = new JXLabel();
		label = new JXLabel("Anrede / Titel");
		//label.setForeground(Color.WHITE);
		builder.add(label,cc.xy(2, 2));
		eingaben[0] = new JRtaTextField("GROSS",false);
		eingaben[0].setName(name+".Anrede");
		eingaben[0].setBackground(Color.WHITE);
		eingaben[0].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[0],cc.xy(4, 2));

		//builder.add(new JXLabel("Titel"),cc.xy(6, 2));
		eingaben[1] = new JRtaTextField("GROSS",false);
		eingaben[1].setName(name+".Titel");		
		eingaben[1].setBackground(Color.WHITE);		
		eingaben[1].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[1],cc.xyw(6, 2,3));

		builder.add(new JXLabel("Nachname"),cc.xy(2, 4));
		eingaben[2] = new JRtaTextField("GROSS",false);
		eingaben[2].setName(name+".Nachname");		
		eingaben[2].setBackground(Color.WHITE);		
		eingaben[2].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[2],cc.xyw(4, 4,5));
		
		builder.add(new JXLabel("Vorname"),cc.xy(2, 6));
		eingaben[3] = new JRtaTextField("GROSS",false);
		eingaben[3].setName(name+".Vorname");		
		eingaben[3].setBackground(Color.WHITE);		
		eingaben[3].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[3],cc.xyw(4, 6,5));

		builder.add(new JXLabel("Straße"),cc.xy(2, 8));
		eingaben[4] = new JRtaTextField("GROSS",false);
		eingaben[4].setName(name+".Strasse");		
		eingaben[4].setBackground(Color.WHITE);		
		eingaben[4].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[4],cc.xyw(4, 8,5));
		
		builder.add(new JXLabel("PLZ / Ort"),cc.xy(2, 10));
		eingaben[5] = new JRtaTextField("ZAHLEN",false);
		eingaben[5].setName(name+".PLZ");		
		eingaben[5].setBackground(Color.WHITE);		
		eingaben[5].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[5],cc.xy(4, 10));


		eingaben[6] = new JRtaTextField("GROSS",false);
		eingaben[6].setName(name+".Ort");		
		eingaben[6].setBackground(Color.WHITE);		
		eingaben[6].setRtaType("GROSS",eingaben[6],false);		
		eingaben[6].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[6],cc.xyw(6, 10,3));
		
		label = new JXLabel("Geburtsdatum");
		builder.add(label,cc.xy(2, 12));
		eingaben[7] = new JRtaTextField("DATUM",false);
		eingaben[7].setName(name+".GEBOREN");		
		eingaben[7].setBackground(Color.WHITE);		
		eingaben[7].setPreferredSize(new Dimension(50,20));
		builder.add(eingaben[7],cc.xy(4, 12));

		
		builder.getPanel().addFocusListener(this);
		builder.getPanel().validate();
		//formPanel.add(builder.getPanel());
		//formPanel.setVisible(true);
		//formPanel.validate();
		//builder.addFocusListener(this);
		
		/*
		JScrollPane jps = new JScrollPane();
		//jps.setViewportView((JPanel) builder.getPanel());
		jps.setViewportView(xbuilder);
		jps.validate();
		return jps;
		*/
		//xbuilder.setVisible(true);
		//builder.getPanel().setVisible(true);
		builder.getPanel().validate();
		builder.getPanel().addKeyListener(this);
		return builder.getPanel();
		//return (xbuilder);
		//return builder;
		//formPanel.revalidate();
		//xbuilder.validate();
		//return xbuilder;
		
	}
	/*******************************************************/
	@Override
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		try{
		String ss = ((JXTitledPanel) this.getParent()).getContentContainer().getName();
		if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
			FensterSchliessen(evt.getDetails()[0]);
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			ptp.removePatStammEventListener((PatStammEventListener)this);
			System.out.println("Inhalt von ss = "+ss);
		}	
		if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="GRUEN"){
			if(setOben == 0){
				ContainerConfig conf = new ContainerConfig();
				conf.addContainer("personen16.gif",evt.getDetails()[0],this.getParent().getParent().getParent().getParent().getParent(),null);
				System.out.println("Name für Container verkleinern = "+ss);
				//	rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			}	
		}	
		if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="RequestFocus"){
			System.out.println("PatientenFenster - Focus erhalten**********");
			if(reverseFocus==null){
				eingaben[0].requestFocus();
			}else{
				reverseFocus.requestFocus();
			}
			if(setOben != 0){
				setzeGruen();
			}
		}
		}catch(NullPointerException ne){
			System.out.println(evt);
		}

		// TODO Auto-generated method stub
		
	}
	public void setzeGruen(){
		((RehaTP)this.getParent()).aktiviereIcon();
	}
	public void FensterSchliessen(String welches){
		System.out.println("Eltern-->"+welches+" "+this.getParent());
		if(setOben != 0){
			//Reha.thisClass.TPschliessen(setOben,(Object) this.getParent(),welches);
			if(sucheComponent != null){
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run()
				 	   {
				 		   ((SuchenDialog) sucheComponent).setVisible(false);
				 		   ((SuchenDialog) sucheComponent).dispose();
				 		   sucheComponent = null;
				 	   }
				}); 	   
			}	
		}else{
			//Reha.thisClass.TPschliessen(setOben,(Object) this.getParent().getParent().getParent().getParent().getParent(),welches);
		}
	}
	
			public void focusGained(FocusEvent e) {
			//displayMessage("Focus gained", e);
				if(!focusok && setOben>0){
					//((RehaTP)thisClass.getParent()).aktiviereIcon();
					focusok = true;
				}
				reverseFocus = null;
		    }

		    public void focusLost(FocusEvent e) {
			//displayMessage("Focus lost", e);
			if(focusok && setOben>0){
				if (e.getOppositeComponent() != null){
				if (e.getOppositeComponent().getName() == null){
						//((RehaTP)thisClass.getParent()).deaktiviereIcon();
						focusok = false;
						reverseFocus = e.getComponent();
				}else{
					if(!e.getOppositeComponent().getName().contains("PatStamm")){
						//((RehaTP)thisClass.getParent()).deaktiviereIcon();
						focusok = false;
						reverseFocus = e.getComponent();
					}
				}
				}else{
					((RehaTP)thisClass.getParent()).deaktiviereIcon();
					focusok = false;
					reverseFocus = e.getComponent();
				}
			}

		    }

		    void displayMessage(String prefix, FocusEvent e) {
			System.out.println(prefix
		                       + (e.isTemporary() ? " (temporary):" : ":")
		                       +  e.getComponent().getClass().getName()
		                       + (e.getComponent().getName() != null ?
		                    	  e.getComponent().getName() : "null")
		                       + "; Opposite component: " 
		                       + (e.getOppositeComponent() != null ?
		                          e.getOppositeComponent().getClass().getName() : "null")
				       + "<-- Ende"); 
		    }
			@Override
			public void patStammEventOccurred(PatStammEvent evt) {
				//System.out.println("Event-Empfangen für :"+((Component) evt.getSource()).getName());
				// TODO Auto-generated method stub
				if(evt.getDetails()[2].contains(this.getName())){
					if(evt.getPatStammEvent().equals("PatSuchen")){
						System.out.println("Event-Empfangen für :"+evt);
						System.out.println("PatIntern =  :"+evt.getDetails()[1]);
						patThread = new PatientSuchen(evt.getDetails()[1],this);
						patThread.run();
						System.out.println("Klasse = "+evt.getSource().getClass());
						System.out.println("Klasse Name = "+evt.getSource().getClass().getName());					
						System.out.println("CanonicalName = "+SuchenDialog.class.getCanonicalName());
						if(evt.getSource().getClass().getName().contains(hauptFenster.SuchenDialog.class.getName())){ 
							System.out.println("Source = "+evt.getSource());
							sucheComponent = (Object) evt.getSource();	
							((SuchenDialog) sucheComponent).setVisible(false);
							if(reverseFocus != null){					
								reverseFocus.requestFocus();
							}else{
								eingaben[0].requestFocus();
							}
						}
					}else if(evt.getPatStammEvent().equals("DialogAnmelden-PatSuchen")){
						if(evt.getSource().getClass().getName().contains(hauptFenster.SuchenDialog.class.getName())){ 
							System.out.println("Source = "+evt.getSource());
							sucheComponent = (Object) evt.getSource();	
							((SuchenDialog) sucheComponent).setVisible(false);
							/*
							if(reverseFocus != null){					
								reverseFocus.requestFocus();
							}else{
								eingaben[0].requestFocus();
							}
							*/
						}
						
					}
					
				}
					
			
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
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
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (sucheComponent != null){
					((SuchenDialog) sucheComponent).setVisible(true);
					((SuchenDialog) sucheComponent).setzeFocusAufSucheFeld();
				}	
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				System.out.println("PatientenFenster KeyEvent = :"+arg0);
				// TODO Auto-generated method stub
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		 
		
	

}

class PatientSuchen implements Runnable{
	private String patIntern;
	private PatientenFenster patClass;

	public PatientSuchen(String string,PatientenFenster patClass) {
		this.patIntern = string;
		this.patClass = patClass;
	}

	private void SucheStarten(){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = "";
		if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				sstmt = "Select *  from pat5 where PAT_INTERN = "+this.patIntern;
		}else{ //ADS
			sstmt = "Select *  from pat5 where PAT_INTERN = "+this.patIntern;
				System.out.println("Statement = "+sstmt);
		}
		try {
			
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = stmt.executeQuery(sstmt);		
				rs.next();
				patClass.eingaben[0].setText(rs.getString("ANREDE"));
				//patClass.eingaben[1].setText(rs.getString("TITEL"));
				patClass.eingaben[2].setText(rs.getString("N_NAME"));
				patClass.eingaben[3].setText(rs.getString("V_NAME"));				
				patClass.eingaben[4].setText(rs.getString("STRASSE"));				
				patClass.eingaben[5].setText(rs.getString("PLZ"));				
				patClass.eingaben[6].setText(rs.getString("ORT"));		
				patClass.eingaben[7].setText( DatFunk.sDatInDeutsch(rs.getString("GEBOREN")) );				
				
				
				//this.jtable.setModel(tblDataModel);
				//this.jtable.updateUI();
			}catch(SQLException ev){
        		System.out.println("SQLException: " + ev.getMessage());
        		System.out.println("SQLState: " + ev.getSQLState());
        		System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;}
			}
			
		}
	}	

		

	public void run() {
		SucheStarten();
	}

}
/**********************************************************/
final class RezepteSuchen implements Runnable{
	private String patIntern = "";
	private PatientenFenster con = null;
	private void SucheStarten(){
		
	}

	
	public void run() {
		SucheStarten();
	}
	public void setKriterium(String patIntern,PatientenFenster con){
		this.patIntern = patIntern;
		this.con = con;
	}
}
/**********************************************************/
final class HitorieSuchen implements Runnable{
	private String patIntern;
	private void SucheStarten(){
	}	

	public void run() {
		SucheStarten();
	}
	public void setKriterium(String patIntern){
		this.patIntern = patIntern;
	}
}