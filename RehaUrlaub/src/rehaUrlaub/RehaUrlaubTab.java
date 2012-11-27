package rehaUrlaub;



import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import CommonTools.SqlInfo;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class RehaUrlaubTab extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7580783032048353314L;
	
	public Vector<Vector<String>> vecKalUser = new Vector<Vector<String>>();
	public Vector<Vector<String>> vecKalZeile = new Vector<Vector<String>>();

	JTabbedPane billEditTab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	
	//RehaUrlaubPanel editPanel = null;
	
	RehaUrlaubPanel urlaubPanel = null;
	RehaUrlaubTabelle urlaubTabelle = null;
	//AltImport altimportPanel = null;
	
	//RehaUrlaubTabelle = urlaubTabelle = null;
	
	public RehaUrlaubTab(){
		super();
		setLayout(new BorderLayout());
		
		String cmd = UrlaubFunktionen.getUrlaubTableDef("");
		SqlInfo.sqlAusfuehren(cmd);
		
		billEditTab = new JTabbedPane();
		billEditTab.setUI(new WindowsTabbedPaneUI());
		
		urlaubPanel = new RehaUrlaubPanel(this);
		billEditTab.add("Stundenerfassung",urlaubPanel);

		/*
		altimportPanel = new AltImport(this);
		billEditTab.add("alten RTA-Urlaub importieren",altimportPanel);
		*/
		
		urlaubTabelle = new RehaUrlaubTabelle(this);
		billEditTab.add("Urlaubtabelle",urlaubTabelle);
		
		jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(billEditTab, BorderLayout.CENTER);

        jxh.validate();
        billEditTab.validate();
		validate();
		new SwingWorker(){
			@Override
			protected Object doInBackground() throws Exception {
				holeKalUser();
				return null;
			}
			
		}.execute();
	}
	private void holeKalUser(){
		vecKalUser = SqlInfo.holeFelder("select kalzeile,matchcode from kollegen2 order by kalzeile");
	}
	
	public Double getAZ(){
		return urlaubPanel.getAktArbeitszeit();
	}

}
