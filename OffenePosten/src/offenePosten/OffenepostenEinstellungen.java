package offenePosten;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXPanel;

import CommonTools.DatFunk;
import CommonTools.INIFile;
import CommonTools.INITool;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OffenepostenEinstellungen extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 311874030698635355L;
	OffenepostenTab eltern = null;
	JXPanel content = null;
	JRtaTextField[] tfs = {null,null,null,null,null,null,null};
	JRtaComboBox combo = null;
	JRtaCheckBox[] chk = {null,null};
	JButton speichern = null;
	ActionListener al = null;
	public OffenepostenEinstellungen(OffenepostenTab xtab){
		super();
		this.eltern = xtab;
		this.setLayout(new BorderLayout());
		activateActionListener();
		add(getContent(),BorderLayout.CENTER);
	}
	
	private JXPanel getContent(){
		//                 1     2      3      4
		String xwerte = "15dlu,200dlu,5dlu,125dlu,10dlu:g";
		//                 1   2  3   4  5  6   7   8  9   10 11 12  13 14  15 16  17  18
		String ywerte = "30dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,25dlu,p";
		content = new JXPanel();
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content.setLayout(lay);
		
		JLabel lab = new JLabel("Frist in Tagen bis zur 1.Mahnung");
		content.add(lab,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		tfs[0] = new JRtaTextField("ZAHLEN",true);
		tfs[0].setText( Integer.toString( (Integer)OffenePosten.mahnParameter.get("frist1") ) );
		content.add(tfs[0], cc.xy(4, 2));
		
		lab = new JLabel("Frist in Tagen von 1.Mahnung bis zur 2.Mahnung");
		content.add(lab,cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		tfs[1] = new JRtaTextField("ZAHLEN",true);
		tfs[1].setText( Integer.toString( (Integer)OffenePosten.mahnParameter.get("frist2") ) );
		content.add(tfs[1], cc.xy(4, 4));

		lab = new JLabel("Frist in Tagen von 2.Mahnung bis zur 3.Mahnung");
		content.add(lab,cc.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		tfs[2] = new JRtaTextField("ZAHLEN",true);
		tfs[2].setText( Integer.toString( (Integer)OffenePosten.mahnParameter.get("frist3") ) );
		content.add(tfs[2], cc.xy(4, 6));

		lab = new JLabel("Mahnungen einzeln drucken");
		content.add(lab,cc.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		chk[0] = new JRtaCheckBox("einzeln drucken");
		chk[0].setSelected((Boolean)OffenePosten.mahnParameter.get("einzelmahnung")  );
		content.add(chk[0], cc.xy(4, 8));
		
		/**************/
		lab = new JLabel("Drucker für den Mahnungsdruck");
		content.add(lab,cc.xy(2,10,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
			String[] drucker = new String[services.length];
			
			for (int i = 0 ; i < services.length; i++){
				drucker[i] = services[i].getName();
			}

			
		combo = new JRtaComboBox(drucker);
		combo.setSelectedItem((String)OffenePosten.mahnParameter.get("drucker") );
		content.add(combo, cc.xy(4, 10));
		/**************/
		
		lab = new JLabel("Wieviel Exeplare sollen beim (Direkt-)Drucken erstellt werden");
		content.add(lab,cc.xy(2,12,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		tfs[3] = new JRtaTextField("ZAHLEN",true);
		tfs[3].setText( Integer.toString( (Integer)OffenePosten.mahnParameter.get("exemplare") ) );
		content.add(tfs[3], cc.xy(4, 12));
		
		lab = new JLabel("Mahnung immer in OpenOffice öffnen");
		content.add(lab,cc.xy(2,14,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		chk[1] = new JRtaCheckBox("OpenOffice anstatt Direktdruck");
		chk[1].setSelected((Boolean)OffenePosten.mahnParameter.get("inofficestarten")  );
		content.add(chk[1], cc.xy(4, 14));

		lab = new JLabel("Alle Rechnungen ausblenden vor Datum");
		content.add(lab,cc.xy(2,16,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		tfs[4] = new JRtaTextField("DATUM",true);
		tfs[4].setText( DatFunk.sDatInDeutsch((String)OffenePosten.mahnParameter.get("erstsuchenab")) );
		content.add(tfs[4], cc.xy(4, 16));
		
		speichern = new JButton("Einstellungen speichern");
		speichern.setActionCommand("speichern");
		speichern.addActionListener(al);
		content.add(speichern, cc.xy(4, 18));
		
		content.validate();
		return content;
	}
	
	private void activateActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("speichern")){
					doSpeichern();
				}
			}
		};
	}
	private void doSpeichern(){
		int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie die aktuellen Einstellungen dauerhaft übernehmen?","Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if( ! (anfrage == JOptionPane.YES_OPTION) ){
			return;
		}
		INIFile inif = INITool.openIni(OffenePosten.progHome+"ini/"+OffenePosten.aktIK+"/", "offeneposten.ini");

		int iwert = Integer.parseInt( (tfs[0].getText().equals("") ? "31" : tfs[0].getText() ) );
		inif.setIntegerProperty("General", "TageBisMahnung1", iwert, null);
		OffenePosten.mahnParameter.put("frist1",(Integer)iwert);
		
		iwert = Integer.parseInt( (tfs[1].getText().equals("") ? "11" : tfs[1].getText() ) );
		inif.setIntegerProperty("General", "TageBisMahnung2", iwert, null);
		OffenePosten.mahnParameter.put("frist2",(Integer)iwert);
		
		iwert = Integer.parseInt( (tfs[2].getText().equals("") ? "11" : tfs[2].getText() ) );
		inif.setIntegerProperty("General", "TageBisMahnung3", iwert, null);
		OffenePosten.mahnParameter.put("frist3",(Integer)iwert);
		
		boolean bwert = chk[0].isSelected();
		inif.setStringProperty("General", "EinzelMahnung", (bwert ? "1" : "0"), null);
		OffenePosten.mahnParameter.put("einzelmahnung",(Boolean)bwert);
		
		String swert = combo.getSelectedItem().toString();
		inif.setStringProperty("General", "MahnungDrucker", swert, null);
		OffenePosten.mahnParameter.put("drucker",(String)swert);
		
		iwert = Integer.parseInt( (tfs[3].getText().equals("") ? "2" : tfs[3].getText() ) );
		inif.setIntegerProperty("General", "MahnungExemplare", iwert, null);
		OffenePosten.mahnParameter.put("exemplare",(Integer) iwert);
		
		bwert = chk[1].isSelected();
		inif.setStringProperty("General", "InOfficeStarten", (bwert ? "1" : "0"), null);
		OffenePosten.mahnParameter.put("inofficestarten",(Boolean)bwert);

		try{
			swert = DatFunk.sDatInSQL(tfs[4].getText());
		}catch(Exception ex){
			swert = "1995-01-01";
			JOptionPane.showMessageDialog(null,"Datumswert für 'Alles ausblenden..' falsch eingegeben, nehme den 01.01.1995");
		}
		inif.setStringProperty("General", "AuswahlErstAb", (String) swert, null);
		OffenePosten.mahnParameter.put("erstsuchenab",(String)swert);
		INITool.saveIni(inif);
		JOptionPane.showMessageDialog(null,"Einstellungen ind offeneposten.ini erfolgreich gespeichert");
	}

	

}
