package reha301Panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.JRtaComboBox;
import Tools.SqlInfo;
import Tools.StringTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Reha301PatAuswahl extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox combo = null;
	ActionListener al = null;
	JButton[] buts = {null,null};
	JEditorPane htmlPane = null;
	Reha301Auswerten eltern = null;
	String nachrichtenid = null;
	Vector<Vector<String>> patvec = new Vector<Vector<String>>();
	Vector<Vector<String>> nachrichtenvec = new Vector<Vector<String>>();
	String verordnungsart = null;
	String eilfall = null;
	public Reha301PatAuswahl(Reha301Auswerten xeltern,String id){
		super();
		setTitle("Patient auswählen");
		this.setPreferredSize(new Dimension(300,300));
		this.activateListener();
		this.setContentPane(getContent());
		this.eltern = xeltern;
		this.nachrichtenid = id;
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					holePatienten();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
	
	private JXPanel getContent(){
		JXPanel pan = new JXPanel();
		//Color piColor = new Color(231,120,23);
		//pan.setBackground(piColor.brighter().brighter().brighter());

		pan.setBackground(Color.WHITE);
		String xwerte = "5dlu,fill:0:grow(1.0),5dlu";
		//                1   2  3        4            5   6  7   
		String ywerte = "5dlu,p,2dlu,fill:0:grow(1.0),5dlu,p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		combo = new JRtaComboBox();
		combo.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						macheHtml(combo.getSelectedIndex(),null);						
					}
				});
			}
		});
		pan.add(combo,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		htmlPane = new JEditorPane();
		htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        pan.add(htmlPane,cc.xy(2,4,CellConstraints.FILL,CellConstraints.FILL));
        
        pan.add( (buts[0] = ButtonTools.macheButton("Einweisungsunterlagen einlesen", "einlesen", al)) ,cc.xy(2,6));
		pan.validate();
		return pan;
		
	}
	private void holePatienten(){
		nachrichtenvec = SqlInfo.holeFelder("select * from dta301 where id ='"+this.nachrichtenid+"' LIMIT 1");
		String[] patdaten = nachrichtenvec.get(0).get(22).split("#");
		String cmd = "select concat(n_name,', ',v_name) as name,geboren,strasse,plz,ort,pat_intern,id from pat5 where n_name='"+patdaten[1]+"' and "+
		"v_name='"+patdaten[2]+"' and geboren = '"+patdaten[3]+"'";
		patvec.clear();
		patvec = SqlInfo.holeFelder(cmd);
		for(int i = 0; i < patvec.size();i++){
			combo.addItem(StringTools.EGross((String) patvec.get(i).get(0))+" - "+Integer.toString(i+1));
		}
		macheHtml(combo.getSelectedIndex(),nachrichtenvec);
	}
	private void macheHtml(int item,Vector<Vector<String>> nachricht){
		Vector<Vector<String>> pnachricht = null;
		if(nachricht==null){
			pnachricht = SqlInfo.holeFelder("select * from dta301 where id ='"+this.nachrichtenid+"' LIMIT 1");
		}else{
			pnachricht = nachricht;
		}
		String vart = pnachricht.get(0).get(25);
		verordnungsart = "unbekannt";
		if(vart.equals("MED")){
			verordnungsart = "medizinische Reha";
		}else if(vart.equals("AR")){
			verordnungsart = "Anschlußheilbehandlung";
		}else if(vart.equals("NACHSORGE")){
			verordnungsart = "Reha-Nachsorge";
		}
		eilfall = pnachricht.get(0).get(24);


		String html = "<html><font face=\"Tahoma\"><br><b>"+StringTools.EGross(patvec.get(item).get(0))+"<br><br>"+
		DatFunk.sDatInDeutsch(patvec.get(item).get(1))+"<br><br>"+
		StringTools.EGross(patvec.get(item).get(2))+"<br><br>"+
		StringTools.EGross(patvec.get(item).get(3))+" "+StringTools.EGross(patvec.get(item).get(4))+"<br><br>"+
		verordnungsart+"<br>"+eilfall+"</b></font></html>";
		htmlPane.setText(html);
	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("einlesen")){
					eltern.doSetPatientFuerNachricht(patvec.get(combo.getSelectedIndex()).get(5));
					dispose();
				}
			}
		};
	}

}
