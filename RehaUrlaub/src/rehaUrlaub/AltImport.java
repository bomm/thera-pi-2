package rehaUrlaub;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.JDBFException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import Tools.JRtaComboBox;
import Tools.JRtaTextField;
import Tools.SqlInfo;

public class AltImport extends JXPanel{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3639615688821194744L;
	
	RehaUrlaubTab elternTab = null;
	
	JRtaComboBox usercombo = null;
	JRtaComboBox jahrecombo = null;
	
	JButton importholen = null;
	
	JXPanel content = null;
	
	JLabel importdatei = null;
	
	JButton importstart = null;
	
	ActionListener al = null;
	
	String importpfad = "l:\\projekte\\rta\\dbf\\urlaub";
	
	String urlaubtabelle = "urlaub2009";
	
	DBFReader dbfreader = null;
	private Vector<Vector<String>> vecKalZeile = new Vector<Vector<String>>();
	
	private Vector<Vector<String>> vecKollegenUrlaub = new Vector<Vector<String>>(); 
	
	private Vector<String> vectabellen = new Vector<String>(); 

	private Vector<String> vecimportfelder = new Vector<String>();
	
	StringBuffer buf = new StringBuffer();
	
	public AltImport(RehaUrlaubTab xeltern){
		super();
		this.elternTab = xeltern;
		setLayout(new BorderLayout());
		activateActionListener();
		add(getContent(),BorderLayout.CENTER);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				holeKalUser();
				holeTabellen();
				holeImportFelder();
				return null;
			}
			
		}.execute();
		validate();
	}
	
	private JXPanel getContent(){
		//                 1    2    3     4     5      6
		String xwerte = "30dlu,60dlu,5dlu,200dlu,5dlu,50dlu,0dlu:g";
		//                 1   2  3   4  5   6   7  8  9  10 
		String ywerte = "30dlu,p,5dlu,p,5dlu,p,5dlu,p,25dlu,p";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		
		JLabel lab = new JLabel("Kalenderjahr");
		content.add(lab,cc.xy(2,2));
		String[] jahre = {"2009","2010"};
		jahrecombo = new JRtaComboBox(jahre); 
		jahrecombo.setActionCommand("urlaubtabelle");
		jahrecombo.addActionListener(al);
		content.add(jahrecombo,cc.xyw(4, 2,3));
		
		lab = new JLabel("Kal.Benutzer");
		content.add(lab,cc.xy(2,4));
		usercombo = new JRtaComboBox(); 
		content.add(usercombo,cc.xyw(4, 4,3));
		
		lab = new JLabel("ImportDatei");
		content.add(lab,cc.xy(2,6));
		importholen = new JButton("Import-Datei auswählen");
		importholen.setActionCommand("importwahl");
		importholen.addActionListener(al);
		content.add(importholen,cc.xyw(4, 6,3));
		
		importdatei = new JLabel(""); 
		content.add(importdatei,cc.xyw(4, 8,3));
		
		importstart = new JButton("Import starten");
		importstart.setActionCommand("start");
		importstart.addActionListener(al);
		content.add(importstart,cc.xyw(4, 10,3));
		content.validate();
		return content;
	}
	private void activateActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("importwahl")){
					doImportWahl();
					return;
				}
				if(cmd.equals("start")){
					doStarten();
					return;
				}
				if(cmd.equals("urlaubtabelle")){
					urlaubtabelle = "urlaub"+jahrecombo.getSelectedItem().toString().trim();
					System.out.println("Urlaubtabelle = "+urlaubtabelle);
					return;
				}

			}
			
		};
	}
	private void doImportWahl(){
		System.out.println("In doImportWahl");
		RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		String ximportdatei = dirChooser(importpfad,"");
		if(ximportdatei.equals("")){
			importdatei.setText("");
			return;
		}
		if((!ximportdatei.contains(usercombo.getSelectedItem().toString())) || 
				(!ximportdatei.contains(jahrecombo.getSelectedItem().toString())) ){
			JOptionPane.showMessageDialog(null,"Der eingestellte Kalenderbenutzer und die ausgwählte Importdatei passen nicht zusammen");
			importdatei.setText("");
			return;
		}
		importdatei.setText(ximportdatei);
	}
	/*******************************************/
	@SuppressWarnings("unchecked")
	private void holeKalUser(){
		vecKalZeile = SqlInfo.holeFelder("select matchcode,kalzeile,astunden from kollegen2 order by matchcode");
		Vector<String> dummy = new Vector<String>();
		dummy.add("./.");
		dummy.add("0");
		dummy.add("0");
		vecKalZeile.insertElementAt((Vector<String>)dummy.clone(), 0);
		usercombo.setDataVectorVector(vecKalZeile, 0, 1);
	}
	
	private void holeTabellen(){
		Vector<Vector<String>> dummy = SqlInfo.holeFelder("show tables");
		vectabellen.clear();
		vectabellen.trimToSize();
		for(int i = 0; i < dummy.size();i++){
			vectabellen.add( String.valueOf( dummy.get(i).get(0) ) );
		}
	}
	
	private void holeImportFelder(){
		Vector<Vector<String>> dummy = new Vector<Vector<String>>();
		dummy = SqlInfo.holeFelder("describe urlaub");
		for(int i = 1; i < 22;i++){
			vecimportfelder.add(String.valueOf(dummy.get(i).get(0)));
		}
		System.out.println(vecimportfelder);
	}

	/*******************************************/
	 public static String dirChooser(String pfad,String titel){
			//String pfad = "C:/Lost+Found/verschluesselung/";
			String sret = "";
			final JFileChooser chooser = new JFileChooser(pfad);
	        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	        chooser.setDialogTitle(titel);
	        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        final File file = new File(pfad);

	        chooser.setCurrentDirectory(file);

	        chooser.addPropertyChangeListener(new PropertyChangeListener() {
	            public void propertyChange(PropertyChangeEvent e) {
	                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
	                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
	                }
	            }
	        });
	        chooser.setVisible(true);
			RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	        final int result = chooser.showOpenDialog(null);

	        if (result == JFileChooser.APPROVE_OPTION) {
	            File inputVerzFile = chooser.getSelectedFile();
	            if(inputVerzFile.getName().trim().equals("")){
	            	sret = "";
	            }else{
	            	sret = inputVerzFile.getAbsolutePath().trim();	
	            }
	        }else{
	        	sret = "";
	        }
	        return sret;
	 }

	private void doStarten(){
		System.out.println("In doImportStart");
		if(importdatei.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Eingestellte Auswahl ist nicht korrekt");
			return;
		}
		if(vectabellen.indexOf(urlaubtabelle) < 0){
			JOptionPane.showMessageDialog(null, "Die Tabelle "+urlaubtabelle+" existiert nicht.\nBitte im ersten Karteireiter anlegen");
			return;
		}
		vecKollegenUrlaub.clear();
		vecKollegenUrlaub.trimToSize();
		String jahr = this.jahrecombo.getSelectedItem().toString().trim();
		String kalzeile = usercombo.getValueAt(1).toString();
		String cmd = "select id from "+urlaubtabelle+" where jahr='"+jahr+"' and kal_zeile='"+kalzeile+"' order by kw LIMIT 55";
		System.out.println(cmd);
		vecKollegenUrlaub = SqlInfo.holeFelder(cmd);
		System.out.println("Größe es Urlaubsvectors = "+vecKollegenUrlaub.size());
		
		try {
			dbfreader = new DBFReader(importdatei.getText().trim());
			int durchlauf = 0;
			while(dbfreader.hasNextRecord()){
				Object aobj[] = dbfreader.nextRecord();
				buf.setLength(0);
				buf.trimToSize();
				buf.append("update "+urlaubtabelle+" set ");
				for(int i = 0; i < aobj.length;i++){
					if(i > 0){
						if(i == 16){
							buf.append(", "+vecimportfelder.get(i)+"='"+Double.toString(elternTab.getAZ())+"'");
						}else{
							buf.append(", "+vecimportfelder.get(i)+"='"+aobj[i]+"'");	
						}
					}else{
						buf.append(vecimportfelder.get(i)+"='"+aobj[i]+"'");							
					}
				}
				buf.append(", BERECHNET='T'");
				buf.append(" where id='"+vecKollegenUrlaub.get(durchlauf).get(0)+"' LIMIT 1");
				SqlInfo.sqlAusfuehren(buf.toString());
				//System.out.println(buf.toString());
				durchlauf++;
			}	
			System.out.println("feddisch");
			dbfreader.close();

		} catch (JDBFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
