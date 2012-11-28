package rehaKassenbuch;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import CommonTools.ButtonTools;
import CommonTools.DatFunk;
import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RehaKassenbuchPanel extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5639305640753853306L;
	
	RehaKassenbuchTab eltern = null;
	JXPanel content = null;
	JList kbvorhanden = null;
	JRtaTextField[] tfs = {null,null,null,null};
	JButton[] buts = {null,null,null,null};
	ActionListener al = null;
	Vector<String> datavec = new Vector<String>();	
	
	public RehaKassenbuchPanel(RehaKassenbuchTab rkbtab){
		super();
		eltern = rkbtab;
		setLayout(new BorderLayout());
		activateListener();
		add(getContent(),BorderLayout.CENTER);
	}

	private JXPanel getContent(){
		//                 1     2     3     4      5     6    7     8      9
		String xwerte = "10dlu,200dlu,40dlu,120dlu,40dlu,65dlu,2dlu,60dlu,10dlu";
		//                1    2   3  4   5  6  7   8  9  10  11   12   13
		String ywerte = "10dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu:g,2dlu,p,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		/*** rechte Seite ***/
		tfs[0] = new JRtaTextField("DATUM",true);
		tfs[0].setText(DatFunk.sHeute());
		tfs[1] = new JRtaTextField("DATUM",true);
		tfs[1].setText(DatFunk.sHeute());
		tfs[2] = new JRtaTextField("nix",true);
		JLabel lab = new JLabel("neues Kassenbuch erzeugen");
		jpan.add(lab,cc.xyw(6, 2,3));
		lab = new JLabel("von Datum");
		jpan.add(lab,cc.xy(6, 4));
		jpan.add(tfs[0],cc.xy(8, 4));
		lab = new JLabel("bis Datum");
		jpan.add(lab,cc.xy(6, 6));
		jpan.add(tfs[1],cc.xy(8, 6));
		lab = new JLabel("Name d. Kassenbuches");
		jpan.add(lab,cc.xy(6, 8));
		jpan.add(tfs[2],cc.xy(8, 8));
		buts[1] = ButtonTools.macheButton("erzeugen", "newtable", al);
		jpan.add(buts[1],cc.xy(8, 10));
		/****Mitte****/
		lab = new JLabel("bislang vorhandene Kassenbücher");
		jpan.add(lab,cc.xy(4, 2));
		
		kbvorhanden = new JList();
		JScrollPane jscr = JCompTools.getTransparentScrollPane(kbvorhanden);
		jscr.validate();
		jpan.add(jscr,cc.xywh(4,4,1,8,CellConstraints.DEFAULT,CellConstraints.FILL));
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				long zeit = System.currentTimeMillis();
				while(!RehaKassenbuch.DbOk){
					Thread.sleep(50);
					if(System.currentTimeMillis()-zeit > 5000){
						break;
					}
				}
				if(RehaKassenbuch.DbOk){
					doKBErmitteln();					
				}else{
					System.exit(0);
				}
				return null;
			}
			
		}.execute();
		buts[0] = ButtonTools.macheButton("löschen", "deletetable", al);
		jpan.add(buts[0],cc.xy(4,13));

		/*** Linke Seite ***/
		lab = new JLabel("");
		lab.setIcon(new ImageIcon(RehaKassenbuch.progHome+"icons/GutenbergBibel.png"));
		jpan.add(lab,cc.xywh(2,2,1,11,CellConstraints.DEFAULT,CellConstraints.CENTER));
		
		jpan.validate();
		return jpan;
	}
	
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("deletetable")){
					doDelete();
					return;
				}
				if(cmd.equals("newtable")){
					doNew();
					return;
				}
			}
		};
	}
	
	private void doKBErmitteln(){
		Vector<Vector<String>> vec = SqlInfo.holeFelder("show tables");
		datavec.clear();
		for(int i = 0; i < vec.size();i++){
			if(vec.get(i).get(0).startsWith("kb_")){
				datavec.add(vec.get(i).get(0));
			}
		}
		kbvorhanden.setListData(datavec);
		eltern.tabellenAktualisieren();
	}
	private void doDelete(){
		int item = kbvorhanden.getSelectedIndex(); 
		if(item >= 0){
			int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie das Kassenbuch --> "+kbvorhanden.getSelectedValue().toString()+" <-- wirklich löschen?", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.YES_OPTION){
				String cmd = "drop table "+ kbvorhanden.getSelectedValue().toString();
				SqlInfo.sqlAusfuehren(cmd);
				doKBErmitteln();
			}
		}
	}
	private void doNew(){
		try{
			if(tfs[2].getText().trim().equals("")){
				JOptionPane.showMessageDialog(null,"Depp!!\nWie soll das Kassenbuch nochmal hei�en????");
				return;
			}
			if(tfs[2].getText().trim().indexOf(" ") >= 0){
				JOptionPane.showMessageDialog(null,"Depp!!\nKeine Leerzeichen im Namen des Kassenbuches");
				return;
			}
			String von = DatFunk.sDatInSQL(tfs[0].getText());
			String bis = DatFunk.sDatInSQL(tfs[1].getText());
			String tabelle = "kb_"+tfs[2].getText().trim();
			if(datavec.contains(tabelle)){
				JOptionPane.showMessageDialog(null,"Ein Kassenbuch mit diesem Namen existiert bereits");
				return;
			}
			//doKBErzeugen(tabelle);
			doKBFuellen(tabelle,von, bis);
			doKBErmitteln();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Fehler bei der Ausführung neues Kassenbuch erzeugen");
		}
	}
	public void doKBErzeugen(String tabelle){
		StringBuffer buf = new StringBuffer();
		
		buf.append("CREATE TABLE IF NOT EXISTS "+tabelle+" (" );
		buf.append("EINNAHME decimal(12,2) DEFAULT NULL,");
		buf.append("AUSGABE decimal(12,2) DEFAULT NULL,");
		buf.append("DATUM date DEFAULT NULL,");
		buf.append("KTEXT varchar(35) DEFAULT NULL,");
		buf.append("KTO varchar(20) DEFAULT NULL,");
		buf.append("KSTAND decimal(12,2) DEFAULT NULL,");
		buf.append("id int(11) NOT NULL AUTO_INCREMENT,");
		buf.append("PAT_INTERN int(11) NOT NULL DEFAULT '0',");
		buf.append("REZ_NR varchar(25) DEFAULT NULL,");
		buf.append("PRIMARY KEY (id),");
		buf.append("KEY REZ_NR (REZ_NR)");
		buf.append(") ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=41552") ;
		SqlInfo.sqlAusfuehren(buf.toString());
		
	}
	private void doKBFuellen(String tabelle,String von,String bis){
		//String cmd = "insert intoselect * into '"+tabelle+"' from kasse where datum >= '"+von+"' and datum <= '"+bis+"'";
		String cmd2 = "CREATE TABLE "+tabelle+" SELECT * FROM kasse where datum >= '"+von+"' and datum <= '"+bis+"'";
		System.out.println(cmd2);
		SqlInfo.sqlAusfuehren(cmd2);
		SqlInfo.sqlAusfuehren("ALTER TABLE "+tabelle+" ADD PRIMARY KEY (ID)"); 
		SqlInfo.sqlAusfuehren("ALTER TABLE "+tabelle+" CHANGE ID ID INT( 11 ) NOT NULL AUTO_INCREMENT");

	}
}
