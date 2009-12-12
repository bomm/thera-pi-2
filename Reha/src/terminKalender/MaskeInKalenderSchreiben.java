package terminKalender;


import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
//import org.jdesktop.swingx.decorator.SortOrder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import rehaContainer.RehaTP;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import systemTools.Verschluesseln;
import systemTools.WinNum;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;




public class MaskeInKalenderSchreiben extends RehaSmartDialog implements ActionListener, KeyListener, RehaTPEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3482074172384055074L;
	private int setOben;

	private RehaTPEventClass rtp = null;
	private JXPanel jp1 = null;

	private JXPanel jtp = null;
	private String dieserName = "";
	
	private JRtaTextField startDatum = null;
	private JRtaTextField endDatum = null;
	private JXLabel startLabel = null;
	private JXLabel endLabel = null;
	private JXLabel aktDatum = null;
	private JXLabel behandlerLabel = null;
	private JXButton starten = null;
	private JXButton anhalten = null;

	private JXTitledPanel  jp;
	private int maskenBehandler;
	private String sBehandler = "";
	public boolean stopUebertrag = false;

	private Vector vTerm = new Vector();
	public static MaskeInKalenderSchreiben thisClass = null;

	public MaskeInKalenderSchreiben(JXFrame owner,int maskenBehandler,Vector vTerm){
		//super(frame, titlePanel());
		super(owner,"MaskeSchreiben");
		dieserName = "MaskeSchreiben"+WinNum.NeueNummer();
		setName(dieserName);
		getSmartTitledPanel().setName(dieserName);
		this.vTerm = (Vector) vTerm.clone();
		this.maskenBehandler = maskenBehandler;
		this.sBehandler = (maskenBehandler < 10 ? "0"+maskenBehandler+"BEHANDLER" : Integer.toString(maskenBehandler)+"BEHANDLER");
		//System.out.println("Maskenbehandler = "+this.maskenBehandler);
		//System.out.println("Maske von Behandler "+this.sBehandler);
		this.setModal(true);
		this.setUndecorated(true);
		//ParameterLaden.vKKollegen.get(von).Matchcode
		this.setName(dieserName);
		this.setContentPanel(titlePanel() );
		this.jtp.setLayout(new BorderLayout());
		thisClass = this;
		JXPanel jp1 = new JXPanel();
		jp1.setBorder(null);
		jp1.setBackground(Color.WHITE);
        jp1.setLayout(new BorderLayout());
        //jp1.setLayout(new VerticalLayout(1));
        String ss = SystemConfig.homeDir+"icons/header-image.png";
        JXHeader header = new JXHeader("Hier schreiben Sie die Maskendefinition (=Wochenarbeitszeit) in den Terminkalender!\n\n",
        		"Äußerste Vorsicht im Umgang mit dieser Funktion ist sicherlich 'nicht ganz unangebracht'.....\n\n"+
                "Stellen Sie vor allen Dingen sicher, daß wirklich nur Sie als Super-User Zugang zu dieser Funktion haben!\n" +
                "In der Benutzerverwaltung können Sie die entsprechende Rechte erteilen bzw. wieder entziehen.\n\n" +
                "Wenn Sie jetzt die (leere) Maskendefinition in Ihren Terminkalender übertragen, werden alle\n"+
                "bereits existierenden Termine dieses Benutzers mit dessen leeren Maske überschrieben!!!!!\n\n"+
                "Wenn Sie diesen Vorgang starten, sollten Sie diesen wirklich nur im äußersten Notfall(!!!) unterbrechen!\n\n"+
                "Sie schließen dieses Fenster über den roten Punkt rechts oben, oder mit der Taste >>ESC<<.",
                new ImageIcon(ss));
        jp1.add(header,BorderLayout.CENTER);
        //jp1.add(eingabePanel(),BorderLayout.SOUTH);

		this.jtp.add(jp1,BorderLayout.NORTH);
		
		JScrollPane jscr = new JScrollPane();
		jscr.setBorder(null);
		jscr.setViewportView(eingabePanel());
		jscr.setVisible(true);
		jscr.validate();
		
		this.jtp.add(jscr,BorderLayout.CENTER);
		JXPanel dummy = new JXPanel();
		dummy.setBorder(null);
		dummy.setPreferredSize(new Dimension(0,10));
		this.jtp.add(dummy,BorderLayout.SOUTH);
		this.jtp.revalidate();
		getSmartTitledPanel().setTitle("Maskendefinition (=Wochenarbeitszeit) in Kalender schreiben");
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(dieserName);
		pinPanel.setzeName(dieserName);
		setPinPanel(pinPanel);
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		thisClass = this;

		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		  startFocus();
		 	   }
		});
 	   
	}		    
/*******************************************************/			
/*********************************************************/
public void FensterSchliessen(String welches){
	//System.out.println("Eltern-->"+this.getParent().getParent().getParent().getParent().getParent());
	//webBrowser.dispose();
	this.dispose();
}

public JScrollPane eingabePanel(){
/*
	private JRtaTextField startDatum = null;
	private JRtaTextField endDatum = null;
	private JXLabel startLabel = null;
	private JXLabel endLabel = null;
	private JXLabel aktDatum = null;
	private JXLabel behandlerLabel = null;
*/	
	//,40dlu,80dlu,4dlu,80dlu,15dlu,80dlu,4dlu
	FormLayout flay = new FormLayout("40dlu,right:120dlu,4dlu,80dlu,80dlu",
	"15dlu,25dlu,15dlu,5dlu,15dlu,5dlu,15dlu,5dlu,15dlu"); //,30dlu,15dlu
	JXPanel eingabep = new JXPanel(flay);
	flay.layoutContainer(eingabep);
	eingabep.setBorder(null);
	//eingabep.setPreferredSize(new Dimension(350,250));
	
	//eingabep.setLayout(flay);
	CellConstraints cc = new CellConstraints();

	behandlerLabel = new JXLabel("Wochenarbeitszeit übertragen von  -->  "+ParameterLaden.getKollegenUeberDBZeile(maskenBehandler));
	
	behandlerLabel.setForeground(Color.RED);
	eingabep.add(behandlerLabel,cc.xyw(2,2,3));

	startLabel = new JXLabel("Starte Maskenübertrag am:");
	eingabep.add(startLabel,cc.xy(2,3));

	startDatum = new JRtaTextField("DATUM",false);
	startDatum.setText(DatFunk.sHeute());
	startDatum.addKeyListener(this);
	startDatum.setName("StartDatum");
	eingabep.add(startDatum,cc.xy(4,3));
	
	endLabel = new JXLabel("Übertrage Maske bis (einschließlich):");
	eingabep.add(endLabel,cc.xy(2,5));

	endDatum = new JRtaTextField("DATUM",false);
	endDatum.setText(DatFunk.sHeute());
	endDatum.addKeyListener(this);
	endDatum.setName("EndDatum");
	eingabep.add(endDatum,cc.xy(4,5));

	starten = new JXButton("Übertrag starten");
	//starten.setPreferredSize(new Dimension(80,15));
	starten.addActionListener(this);
	starten.addKeyListener(this);	
	eingabep.add(starten,cc.xy(2,7));

	anhalten = new JXButton("Übertrag anhalten");
	//anhalten.setPreferredSize(new Dimension(80,15));	
	anhalten.setEnabled(false);
	anhalten.addActionListener(this);
	anhalten.addKeyListener(this);
	eingabep.add(anhalten,cc.xy(4,7));
	
	aktDatum = new JXLabel("            ");
	eingabep.add(aktDatum,cc.xy(2,9));
	eingabep.setVisible(true);
	eingabep.validate();
	JScrollPane jrueck = new JScrollPane();
	jrueck.setBorder(null);
	jrueck.setViewportView(eingabep);
	jrueck.validate();
	return jrueck;
}


private JXPanel titlePanel(){
	jp = new RehaTP(0);
	jp.setName(dieserName);
	jtp = (JXPanel) jp.getContentContainer();
	jtp.setSize(new Dimension(200,200));
	jtp.setVisible(true);
	return jtp;
}


public String dieserName(){
	return this.getName();
}

public void rehaTPEventOccurred(RehaTPEvent evt) {
	// TODO Auto-generated method stub
	System.out.println("****************das darf doch nicht wahr sein in DruckFenster**************");
	String ss =  this.getName();
	System.out.println("MaskeInKalenderSchreiben "+this.getName()+" Eltern "+ss);
	try{
		//if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
			FensterSchliessen(evt.getDetails()[0]);
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		//}	
	}catch(NullPointerException ne){
		System.out.println("In DruckFenster" +evt);
	}


}

public void actionPerformed(ActionEvent arg0) {
	String cmd = arg0.getActionCommand();
	for(int i = 0; i< 1;i++){
		if(cmd.equals("Übertrag starten")){
			stopUebertrag = false;
			SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {	
			 		   new Thread(){
			 			   public void run(){
			 				   String hinweis = "Sie schreiben im Anschluß eine neue Wochenarbeitszeit in den Terminkalender!\n"+
			 				   					"Dabei werden evtl. bestehende Feiertage oder Betriebsferien überschrieben.\n"+
			 				   					"Die Feiertage lassen sich bequem in der System-Initialisierung organisieren, oder\n"+
			 				   					"Sie schreiben die Feiertage von Hand in den Kalender";
			 				   JOptionPane.showMessageDialog(null,hinweis);
			 				   anhalten.setEnabled(true);
			 				   starten.setEnabled(false);
			 				   maskenEintragen();
			 			   }
			 		   }.start();
						
			 	   }
			});
			break;
		}
		if(cmd.equals("Übertrag anhalten")){
			SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
						stopUebertrag = true;
						anhalten.setEnabled(false);
						starten.setEnabled(true);
			 	   }
			});
			
			break;
		}
	}
	
}

public String getStartDatum(){
	return startDatum.getText().trim();
}
public String getEndDatum(){
	return endDatum.getText().trim();
}
public void setAktuellesDatum(String aktDat){
	aktDatum.setText("übertrage "+aktDat);
}

@Override
public void keyPressed(KeyEvent arg0) {
	//System.out.println(arg0.getKeyCode()+" - "+arg0.getSource());
	if(arg0.getKeyCode() == 27){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		FensterSchliessen(null);
	}
	if(arg0.getKeyCode() == 10){
		if(arg0.getComponent().getName() != null){
			if(arg0.getComponent().getName().equals("Übertrag starten")){
				maskenEintragen();
				arg0.consume();
			}
		}

	}

	
}
@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	if(arg0.getKeyCode() == 27){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		FensterSchliessen(null);
	}	
}

public void maskenEintragen(){
	stopUebertrag = false;
	String startTag = null;
	String stopTag = null;
	String aktTag = null;
	startTag = getStartDatum();
	stopTag = getEndDatum();
	aktTag = new String(startTag);
	int wochenTag = 0;
	int i = 0;
	if(DatFunk.DatumsWert(startTag) > DatFunk.DatumsWert(stopTag)){
		stopUebertrag = true;
		JOptionPane.showMessageDialog(null,"Ihr angegebenes Startdatum ist größer als das Stopdatum -> Depp!");
		return;
	}
	//System.out.println("Anzahl Vector-Elemente = "+this.vTerm.size());
	int j = 0;
	for(i = 0; i < this.vTerm.size();i++){
		//System.out.println("Inhalt des Vector-Element "+i+" = "+this.vTerm.get(i));
		//System.out.println("Anzahl Elemente von get("+i+") = "+((ArrayList)this.vTerm.get(i)).size());
		/*
		for(j=0; j< ((ArrayList)this.vTerm.get(i)).size();j++){
			System.out.println("Inhalte get("+i+") an Position "+j+" = "+((ArrayList)this.vTerm.get(i)).get(j));
		}
		*/	
	}
	//System.out.println("Vector-Element1 = "+this.vTerm.size());	
	while(!stopUebertrag){
		//System.out.println("Bearbeite Tag "+aktTag);
		aktDatum.setText(aktTag);
		wochenTag = DatFunk.TagDerWoche(aktTag);
		String statement = macheStatement(DatFunk.sDatInSQL(aktTag),(ArrayList) this.vTerm.get(wochenTag-1));
		SchreibeMaskeInKalender smk = new SchreibeMaskeInKalender();
		smk.setzeStatement(statement);
		aktTag = DatFunk.sDatPlusTage(aktTag, 1);
		if(DatFunk.DatumsWert(aktTag) > DatFunk.DatumsWert(stopTag)){
			stopUebertrag = true;
		}
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	anhalten.setEnabled(false);
	starten.setEnabled(true);

	//1.Feststellen welcher Behandler betroffen ist
	//2.Feststellen ab welchem Tag
	//3.Feststellen bis zu welchem Tag
	//while(! lstopp)
	//4. Feststellen welcher Kalendertag betroffen ist,
	//5. den Vector auslesen
	//6. statement zurechtbasteln
	//7. statement abschicken
	//8. pr�fen ob tag +1 > bis zu welchem Tag wenn ja exit;
}

	private String macheStatement(String sqldat,ArrayList list){
		String sret = null;
		int i,j;
		int bloecke =new Integer( (String)  ((Vector) list.get(5)).get(0) );
		sret = "Update flexkc set ";
		String nummer;
		for (i = 0; i<bloecke;i++){
			  if(((String)((Vector) list.get(1)).get(i)).contains("\\")){
				  String replace =  ((String)((Vector) list.get(1)).get(i));
				  String [] split = {null,null};
				  split = replace.split("\\\\");
				  nummer =  split[0]+"\\\\"+split[1];
				  //System.out.println("Backslashtermin = "+nummer);
			  	
			  }else{
				  nummer = ((String)((Vector) list.get(1)).get(i));
			  }

			sret = sret + "T"+ (i+1) + "='" + ((Vector) list.get(0)).get(i) + "', " ;
			sret = sret + "N"+ (i+1) + "='" + nummer + "', "; 
			sret = sret + "TS"+ (i+1) + "='" + ((Vector) list.get(2)).get(i) + "', ";			
			sret = sret + "TD"+ (i+1) + "='" + ((Vector) list.get(3)).get(i) + "', ";			
			sret = sret + "TE"+ (i+1) + "='" + ((Vector) list.get(4)).get(i) + "', ";
		}
		sret = sret + "BELEGT='"+Integer.toString(bloecke)+"' WHERE DATUM='"+sqldat+"' AND BEHANDLER='"+this.sBehandler+"'";
		return sret;
	}

public void startFocus(){
	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run()
	 	   {
	 		  startDatum.requestFocus();
	 	   }
	}); 
}	

/*******************************************/
}
/******************************************/
final class SchreibeMaskeInKalender extends Thread implements Runnable{
	Statement stmt = null;
	ResultSet rs = null;
	String statement;
	boolean geklappt = false;
 
	public void setzeStatement(String statement){
		this.statement = statement;
		start();
	}
	public void run(){
		//Vector treadVect = new Vector();
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
					geklappt =  stmt.execute(this.statement);
					
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


