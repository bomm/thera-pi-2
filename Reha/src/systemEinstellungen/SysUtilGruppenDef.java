package systemEinstellungen;

import hauptFenster.Reha;
import hilfsFenster.NeueGruppe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import jxTableTools.TableTool;
import jxTableTools.ZeitTableCellEditor;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.table.TableColumnExt;


import systemTools.JRtaTextField;
import terminKalender.DatFunk;
import terminKalender.ZeitFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilGruppenDef extends JXPanel implements KeyListener, ActionListener {
	JComboBox cmbGrName = null;
	JComboBox cmbWochenTag = null;
	JComboBox cmbAktuell = null;
	JXTable  jTblGruppen = null;
	JButton jbuebertragen = null;
	JButton jbGrDel = null;
	JButton jbGrNeu = null;	
	JButton jbGrEdit = null;
	JButton jbGrSave = null;
	JButton jbneu = null;
	JButton jbdel = null;
	JButton jbedit = null;
	JButton jbsave = null;
	JButton jbbreak = null;
	JLabel gueltig = null;
	JRtaTextField[] pbeginn = {null,null};
	JRtaTextField[] pende = {null,null};	
	JRtaTextField[] pdruck = {null,null};
	JRtaTextField pdauer = null;
	JScrollPane  jscr = null;
	String[] agruppen = {};
	String[] wotags = {"Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag","Sonntag"};
	String[] column = 	{"Drucktext","Druckzeit","Beginn(Kal)","Ende(Kal)","Dauer"};
	GruppenTableModel dtblm = new GruppenTableModel();
	Vector<String> colVec = new Vector<String>();
	public static String neuGruppenName = "";
	public static String neuGruppenGueltigAb = "";
	public static String neuGruppenDauer = "";
	static String editGruppenName = null;
	static String editGruppenGueltigAb = null;
	static String gruppeninidat = Reha.proghome+"ini/"+Reha.aktIK+"/gruppen.ini";
	
	boolean ltermneu = false,ltermedit=false;
	boolean lgruppeneu = false,lgruppeedit=false;
	int termgewaehlt = -1;
	public SysUtilGruppenDef(){
		super(new GridLayout(1,1));
		//System.out.println("Aufruf SysUtilKalenderanlagen");
		this.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     int anzahl = SystemConfig.oGruppen.gruppenNamen.size(); 
	     if(anzahl > 0){
	    	 agruppen = new String[anzahl];
	    	 for(int i = 0; i< anzahl ; i++){
	    		 agruppen[i] = SystemConfig.oGruppen.gruppenNamen.get(i);
	    	 }
	     }
		final JScrollPane jscroll = new JScrollPane();
		jscroll.setOpaque(false);
		jscroll.getViewport().setOpaque(false);
		jscroll.setBorder(null);
		jscroll.getVerticalScrollBar().setUnitIncrement(15);
		jscroll.setViewportView(getVorlagenSeite());
		jscroll.validate();
		add(jscroll);
		
	    //add(getVorlagenSeite());
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
        //                                      1.            2.    3.    4.     5.     6.   7.    8.   9.    10.  11.  12.     13.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu,40dlu, 4dlu, 4dlu, 4dlu,40dlu,4dlu,40dlu" /*,80dlu, 40dlu:g"*/,
       //1.   2.   3.    4.   5.   6.   7.  8. 9.    10.  11.  12.  13.  14.  15.  16.   17.    18.     19.   20.    21.   22.   23.
		"p, 8dlu,  p  , 4dlu ,p , 2dlu, p, 8dlu, p,  4dlu, p,  8dlu, p,  4dlu, p, 4dlu, 60dlu,  4dlu,    p ");   //,  p, p,  10dlu ,10dlu, 10dlu, p");
		String tooltip;
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();

		builder.add(new JLabel("Gruppe auswählen"),cc.xy(1, 1));
		cmbGrName = new JComboBox(agruppen);
		cmbGrName.setActionCommand("GruppenName");
		cmbGrName.addActionListener(this);
		if(agruppen.length > 0){cmbGrName.setSelectedIndex(0);}
		builder.add(cmbGrName,cc.xyw(3, 1,3));

		builder.addSeparator("Gruppe neu /speichern / ändern / löschen",cc.xyw(1,3,11));

		jbGrNeu = new JButton("neue Gruppe");
		jbGrNeu.setActionCommand("GruppeNeu");
		jbGrNeu.addActionListener(this);
		builder.add(jbGrNeu,cc.xyw(3, 5, 3));

		
		jbGrSave = new JButton("Gruppe speichern");
		jbGrSave.setActionCommand("GruppeSave");
		jbGrSave.addActionListener(this);
		jbGrSave.setEnabled(false);
		builder.add(jbGrSave,cc.xyw(9, 5, 3));

		jbGrEdit = new JButton("Gruppe ändern");
		jbGrEdit.setActionCommand("GruppeAendern");
		jbGrEdit.addActionListener(this);
		builder.add(jbGrEdit,cc.xyw(3, 7, 3));
		
		jbGrDel = new JButton("Gruppe löschen");
		jbGrDel.setActionCommand("GruppeLoeschen");
		jbGrDel.addActionListener(this);
		builder.add(jbGrDel,cc.xyw(9, 7, 3));
		
		
		builder.addSeparator("Aktuelle oder alte Einstellung wählen",cc.xyw(1,9,11));

		gueltig =new JLabel("");
		gueltig.setForeground(Color.RED);
		builder.add(gueltig,cc.xy(1, 11));


		String[] akt = {"aktuelle Definition","alte Definition."};
		cmbAktuell = new JComboBox(akt);
		cmbAktuell.setActionCommand("Aktuell");
		cmbAktuell.addActionListener(this);
		cmbAktuell.setSelectedIndex(0);
		builder.add(cmbAktuell,cc.xyw(3, 11, 3));
		
		jbuebertragen = new JButton("auf alte Def. übertragen");
		jbuebertragen.setActionCommand("Uebertragen");
		jbuebertragen.addActionListener(this);
		tooltip = "<html>Drücken Sie diesen Knopf nur dann<br>"+
				"wenn Sie für die Zukunft eine neue<br>"+
				"Gruppendefinition planen.<br><br>"+
				"Sie werden dann gefragt ab wann<br>" +
				"die neue Gruppendefiniton angewendet<br>werden soll.</html>";
				jbuebertragen.setToolTipText(tooltip);
		builder.add(jbuebertragen,cc.xyw(9, 11,3));

		
		builder.addSeparator("Wochentag dieser Gruppe wählen",cc.xyw(1,13,11));
		
		cmbWochenTag = new JComboBox(wotags);
		cmbWochenTag.setActionCommand("Wochentag");
		cmbWochenTag.addActionListener(this);
		cmbWochenTag.setSelectedIndex(0);
		builder.add(cmbWochenTag,cc.xyw(3, 15, 3));

		tabellenPanel();
		jscr = new JScrollPane(jTblGruppen);
		jTblGruppen.validate();
		jscr.validate();
		builder.add(jscr,cc.xyw(1, 17, 11));

		JXPanel editPan = new JXPanel();
		editPan.setOpaque(false);
		FormLayout lay2 = new FormLayout("45dlu,4dlu,45dlu,4dlu,45dlu,4dlu,45dlu,fill:4dlu:grow(1.0),45dlu","p");
		CellConstraints cc2 = new CellConstraints();
		editPan.setLayout(lay2);

		
		jbneu = new JButton("neue");
		jbneu.setActionCommand("neuertermin");
		jbneu.addActionListener(this);
		editPan.add(jbneu,cc2.xy(1,1));		

		jbsave = new JButton("speichern");
		jbsave.setActionCommand("speicherntermin");
		jbsave.addActionListener(this);	
		jbsave.setEnabled(false);
		editPan.add(jbsave,cc2.xy(3,1));
		
		jbedit = new JButton("ändern");
		jbedit.setActionCommand("aenderntermin");
		jbedit.addActionListener(this);		
		editPan.add(jbedit,cc2.xy(5,1));
		
		jbdel = new JButton("löschen");
		jbdel.setActionCommand("loeschentermin");
		jbdel.addActionListener(this);		
		editPan.add(jbdel,cc2.xy(7,1));
		
		jbbreak = new JButton("abbrechen");
		jbbreak.setActionCommand("abbrechentermin");
		jbbreak.addActionListener(this);		
		editPan.add(jbbreak,cc2.xy(9,1));

		builder.add(editPan,cc.xyw(1, 19,11));
		/*
		builder.add(new JLabel("Wochentag ausw�hlen"),cc.xy(1, 3));
		cmbWochenTag = new JComboBox(wotags);
		cmbWochenTag.setActionCommand("Wochentag");
		cmbWochenTag.addActionListener(this);
		cmbWochenTag.setSelectedIndex(0);
		builder.add(cmbWochenTag,cc.xyw(3, 3, 3));
		builder.add(new JLabel("Definition ausw�hlen"),cc.xy(1, 5));
		String[] akt = {"aktuelle Definition","alte Definition."};
		cmbAktuell = new JComboBox(akt);
		cmbAktuell.setActionCommand("Aktuell");
		cmbAktuell.addActionListener(this);
		cmbAktuell.setSelectedIndex(0);
		builder.add(cmbAktuell,cc.xyw(3, 5, 3));
		builder.add(new JLabel("Aktuelle Definition..."),cc.xy(1, 7));		
		jbuebertragen = new JButton("..auf alte Def. �bertragen");
		jbuebertragen.setActionCommand("Uebertragen");
		jbuebertragen.addActionListener(this);
		builder.add(jbuebertragen,cc.xyw(3, 7,3));

		tabellenPanel();
		JScrollPane jscr = new JScrollPane(jTblGruppen);
		jscr.validate();
		builder.add(jscr,cc.xywh(1, 9, 8, 11));
		*/
		
		if(agruppen.length > 0){
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					for(int i = 0; i< column.length;i++){
						colVec.add(column[i]);
					}
					gruppeEinstellen();
					labelSetzen();
	       	  	}
			});
		}
		return builder.getPanel();
	}
	private void knopfGedoense(int[] werte){
		cmbGrName.setEnabled((werte[0]==1 ? true : false));
		jbGrNeu.setEnabled((werte[1]==1 ? true : false));
		jbGrSave.setEnabled((werte[2]==1 ? true : false));
		jbGrEdit.setEnabled((werte[3]==1 ? true : false));
		jbGrDel.setEnabled((werte[4]==1 ? true : false));
		cmbAktuell.setEnabled((werte[5]==1 ? true : false));
		jbuebertragen.setEnabled((werte[6]==1 ? true : false));
		cmbWochenTag.setEnabled((werte[7]==1 ? true : false));
		jbneu.setEnabled((werte[8]==1 ? true : false));
		jbsave.setEnabled((werte[9]==1 ? true : false));
		jbedit.setEnabled((werte[10]==1 ? true : false));
		jbdel.setEnabled((werte[11]==1 ? true : false));
		jbbreak.setEnabled((werte[12]==1 ? true : false));
		

	}
	private void insertGruppe(int reihe,String termindaten){
		
	}

	private void gruppeEinstellen(){
		int igruppe,iakt,itag;
		dtblm.getDataVector().clear();
		igruppe = cmbGrName.getSelectedIndex();
		iakt = cmbAktuell.getSelectedIndex();
		itag = cmbWochenTag.getSelectedIndex();
		Vector vec1 = new Vector();
		vec1 = (Vector)((Vector)SystemConfig.oGruppen.gruppeAlle.get(igruppe)).clone(); //.get(itag).clone();
		Vector vec2 = new Vector();
		if(((Vector)vec1.get(iakt)).size() == 0){
			//System.out.println("iakt = "+iakt+" = null also return");
			tabellenRefresh();
			return;
		}
		vec2 = (Vector)((Vector)vec1.get(iakt)).clone(); //.get(itag).clone();
		Vector vec3 = new Vector();
		if(((Vector)vec2.get(itag)).size() == 0){
			//System.out.println("tag = "+itag+" = null also return");
			tabellenRefresh();
			return;
		}
		vec3 = (Vector)((Vector)vec2.get(itag)).clone(); //.get(itag).clone();
		//System.out.println("Vektor 3 = "+vec3);
		
		
		jTblGruppen.removeAll();
		
		for(int i = 0;i<vec3.size();i++){
			if(((Vector)vec3.get(i)).size()>2){
				//Text
				Vector dummy = new Vector();
				String ttext = (String)((Vector) vec3.get(i)).get(3);
				dummy.add(ttext);
				//Druckzeit
				ttext = (String)((Vector) vec3.get(i)).get(2);
				dummy.add(ttext);
				//Beginn im Kalender
				int val = new Long(  (Long)((Vector) vec3.get(i)).get(0)).intValue();
				String start = (String) ZeitFunk.MinutenZuZeit(val); 
				dummy.add(start.substring(0,5) );
				//Ende im Kalender
				int val2 = new Long(  (Long)((Vector) vec3.get(i)).get(1)).intValue();
				String end = (String) ZeitFunk.MinutenZuZeit(val2); 
				dummy.add(end.substring(0,5) );
				//Dauer
				long val3 = new Long(  (String)((Vector) vec3.get(i)).get(4) );
				dummy.add(val3);
				//rowVec.add(dummy.clone());
				dtblm.addRow((Vector)dummy.clone());
			}
			tabellenRefresh();
		}
		if(jTblGruppen.getRowCount() > 0){
			jTblGruppen.setRowSelectionInterval(0,0);
		}

		
		//dtblm.setDataVector(colVec,(Vector)rowVec.clone());
		
	}
	private void tabellenRefresh(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				jTblGruppen.validate();
				jscr.validate();
				jTblGruppen.repaint();
       	  	}
		});
	}
	private void tabellenPanel(){

		dtblm = new GruppenTableModel();
		//dtblm.addTableModelListener(this);
 
		//String[] column = 	{"Drucktext","Druckzeit","Beginn(Kal)","Ende(Kal)","Dauer"};
		dtblm.setColumnIdentifiers(column);
		jTblGruppen = new JXTable(dtblm);
		jTblGruppen.getColumn(0).setMinWidth(160);
		jTblGruppen.setDoubleBuffered(true);
		jTblGruppen.setSelectionMode(0);

		/***************************/		
		//jxSucheTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		jTblGruppen.setHighlighters(HighlighterFactory.createSimpleStriping(new Color(204,255,255)));		
		((TableColumnExt)jTblGruppen.getColumn(1)).setCellEditor((TableCellEditor) new ZeitTableCellEditor());
		((TableColumnExt)jTblGruppen.getColumn(2)).setCellEditor((TableCellEditor) new ZeitTableCellEditor());
		((TableColumnExt)jTblGruppen.getColumn(3)).setCellEditor((TableCellEditor) new ZeitTableCellEditor());	
		jTblGruppen.setEditable(false);
		jTblGruppen.setName("tabelle");
		jTblGruppen.addKeyListener(this);
		jTblGruppen.setSortable(false);
		jTblGruppen.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "selectNextColumnCell");
		jTblGruppen.setDropTarget(new DropTarget(){
		    private boolean fAccept;
		    private boolean terminGedropt = false;
		    private String mitgebracht = "";
		    @Override
		    public void dragEnter(DropTargetDragEvent dtde)
		    {
		        fAccept = false;
		        DataFlavor flavors[] = dtde.getCurrentDataFlavors();
		        
		        int i;
		        fAccept = true;
		    }
		    @Override
		    public void dragExit(DropTargetEvent dte)
		    {
		        if (!fAccept) return;
		 
		    }
		    @Override
		    public void dragOver(DropTargetDragEvent dtde)
		    {
		    	//Reha.thisClass.shiftLabel.setText(dtde.getLocation().toString());
		    	////System.out.println("Drag-Support"+dtde);
		    	if (!fAccept) return;
		 
		    }
			
            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                Point point = dtde.getLocation();
                int column = jTblGruppen.columnAtPoint(point);
                int row = jTblGruppen.rowAtPoint(point);
                // handle drop inside current table
                //System.out.println("Gedroppt an Zeile="+row+" / Spalte="+column);
                //System.out.println(dtde);
                if (!fAccept) return;
        		try {
        			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        			terminGedropt = true;
        	        Transferable tr = dtde.getTransferable();
        	        DataFlavor[] flavors = tr.getTransferDataFlavors();
        	        for (int i = 0; i < flavors.length; i++){
        	        	if(flavors[i].getRepresentationClass().toString().equals("java.lang.String")){
        	        		mitgebracht  =(String) tr.getTransferData(flavors[i]).toString();
        	        	}
        	        	mitgebracht  = (String) tr.getTransferData(flavors[i]);
        	        }
        	      } catch (Throwable t) { t.printStackTrace(); }
        	      dtde.dropComplete(true);
        	      if(mitgebracht.startsWith("TERMDATINTERN") && (ltermedit || ltermneu)){
        	    	  dropAuswerten(row,mitgebracht);
        	      }
                super.drop(dtde);
            }
        });
		return; 
	}
	
	private void dropAuswerten(int row, String dropdaten){
		if(jTblGruppen.getRowCount()<= 0){
			return;
		}
		String[] ddaten = dropdaten.split("°");
		long zeit = ZeitFunk.MinutenSeitMitternacht(ddaten[4]);
		String sdauer = ddaten[3].split(" Min.")[0];
		int idauer = Integer.parseInt(sdauer);
		int gesamtminuten = Long.valueOf(zeit).intValue()+idauer;
		String endekal = ZeitFunk.MinutenZuZeit(gesamtminuten);
		int modrowselected = jTblGruppen.convertRowIndexToModel(jTblGruppen.getSelectedRow());
		dtblm.setValueAt(ddaten[4].substring(0,5), modrowselected, 2);
		dtblm.setValueAt(endekal.substring(0,5), modrowselected, 3);
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				jTblGruppen.editCellAt(jTblGruppen.getSelectedRow(), 1);
			}
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode()==10){
			e.consume();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(jTblGruppen == null){
			return;
		}
		for(int i = 0;i<1;i++){
			if(e.getActionCommand().equals("GruppenName")){
				gruppeEinstellen();
				labelSetzen();
				break;
			}
			if(e.getActionCommand().equals("Aktuell")){
				if(cmbAktuell.getSelectedIndex()==1){
					jbuebertragen.setEnabled(false);
					labelSetzen();
				}else{
					jbuebertragen.setEnabled(true);
					labelSetzen();					
				}
				gruppeEinstellen();
				break;
			}
			if(e.getActionCommand().equals("Wochentag")){
				gruppeEinstellen();
				break;
			}
			if(e.getActionCommand().equals("GruppeNeu")){
				knopfGedoense(new int[]{0,0,1,0,0,0,0,0,0,0,0,0,0});
				neuGruppenName = "";
				neuGruppenGueltigAb = "";
				neuGruppenDauer = "";
				lgruppeneu = true;
				NeueGruppe ng = new NeueGruppe("Neue Gruppe anlegen","",DatFunk.sHeute(),"0",true,true);
				if(neuGruppenName.trim().equals("")){
					knopfGedoense(new int[]{1,1,0,1,1,1,1,1,1,0,1,1,0});	
				}
				break;
			}
			if(e.getActionCommand().equals("GruppeAendern")){
				knopfGedoense(new int[]{0,0,1,0,0,0,0,0,0,0,0,0,0});
				neuGruppenName = "";
				neuGruppenGueltigAb = "";
				neuGruppenDauer = "";
				
				String sgruppe = (String) cmbGrName.getSelectedItem();
				int igruppe = cmbGrName.getSelectedIndex();
				String sgueltig = DatFunk.WertInDatum(SystemConfig.oGruppen.gruppenGueltig.get(igruppe)[0]);
				String sdauer = new Long(SystemConfig.oGruppen.gruppenGueltig.get(igruppe)[2]).toString();
				NeueGruppe ng = new NeueGruppe("Gruppe ändern",sgruppe,sgueltig,sdauer,true,false);
				if(neuGruppenName.trim().equals("")){
					knopfGedoense(new int[]{1,1,0,1,1,1,1,1,1,0,1,1,0});
				}
				break;
			}

			if(e.getActionCommand().equals("GruppeSave")){
				if(!neuGruppenName.trim().equals("")){
					if(lgruppeneu){
						macheGruppeNeu();
					}else{
						gruppeAendern(neuGruppenName);		
					}
					lgruppeneu = false;
				}
				knopfGedoense(new int[]{1,1,0,1,1,1,1,1,1,0,1,1,0});
				break;
			}
			if(e.getActionCommand().equals("Uebertragen")){
				int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie wirklich die aktuelle Gruppendefinition auf die - alte Definition - übertragen?", "Achtung wichtige Benutzeranfrage!!!!", JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							definitionUebertragen();
						}
					});
				}
				break;
			}

			if(e.getActionCommand().equals("GruppeLoeschen")){
				int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie dies komplette Gruppe tatsächlich löschen?", "Achtung wichtige Benutzeranfrage!!!!", JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							loescheGruppe();
						}
					});
				}
				break;
			}
			
			if(e.getActionCommand().equals("neuertermin")){
				termgewaehlt = -1;
				ltermneu = true;
				knopfGedoense(new int[]{0,0,0,0,0,0,0,0,0,1,0,0,1});
				macheRowVec("","00:00","00:00","00:00","15");
				jTblGruppen.setEditable(true);
				jTblGruppen.requestFocus();
				jTblGruppen.setRowSelectionInterval(jTblGruppen.getRowCount()-1,jTblGruppen.getRowCount()-1);
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						jTblGruppen.editCellAt(jTblGruppen.getRowCount()-1, 0);
					}
				});


				break;
			}
			if(e.getActionCommand().equals("speicherntermin")){
				knopfGedoense(new int[]{1,1,0,1,1,1,1,1,1,0,1,1,0});
				jTblGruppen.setEditable(false);
				macheSysVec(jTblGruppen.getSelectedRows()[0]);
				ltermneu = false;
				ltermedit = false;
				termgewaehlt = -1;
				break;
			}
			if(e.getActionCommand().equals("aenderntermin")){
				termgewaehlt = -1;
				if(jTblGruppen.getRowCount()==0){
					JOptionPane.showMessageDialog(null,"Aha, Sie haben zwar keinen Termin definiert,\n"+
					"wollen aber schon mal einen Termin ändern....");
					return;
				}
				ltermedit = true;
				knopfGedoense(new int[]{0,0,0,0,0,0,0,0,0,1,0,0,1});
				jTblGruppen.setEditable(true);
				jTblGruppen.requestFocus();
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						int[] wahl = jTblGruppen.getSelectedRows();
						if(wahl.length > 0){
							termgewaehlt = wahl[0];
							jTblGruppen.setRowSelectionInterval(wahl[0],wahl[0]);
							jTblGruppen.editCellAt(wahl[0], 0);
						}else{
							JOptionPane.showMessageDialog(null,"Sie haben keinen Termin zum - ändern - ausgewählt\n"+
									"Die Software wählt für Sie den ersten Termin");
							jTblGruppen.editCellAt(0, 0);
						}
					}
				});
				break;
			}
			if(e.getActionCommand().equals("loeschentermin")){
				termgewaehlt = -1;
				if(jTblGruppen.getRowCount()==0){
					JOptionPane.showMessageDialog(null,"Aha, Sie haben zwar keinen Termin definiert,\n"+
					"wollen aber schon mal einen Termin löschen....");
					return;
				}
				int[] wahl = jTblGruppen.getSelectedRows();
				if(wahl.length == 0){
					JOptionPane.showMessageDialog(null,"Um einen Termin löschen zu können muß dieser zuerst markiert werden");
					return;
				}
				int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Gruppentermin wirklich löschen?", "Achtung wichtige Benutzeranfrage!!!!", JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							int[] wahl = jTblGruppen.getSelectedRows();
							if(wahl.length > 0){
								dtblm.removeRow(wahl[0]);
								loescheTermin(wahl[0]);
							}
						}
					});
				}
				break;
			}
			if(e.getActionCommand().equals("abbrechentermin")){
				knopfGedoense(new int[]{1,1,0,1,1,1,1,1,1,0,1,1,0});
				gruppeEinstellen();
				termgewaehlt = -1;
			}
			

			//jbneu.setActionCommand("neuertermin");
			//


		}
		
	}
	private void labelSetzen(){
		String s;
		Date dDatum;
		int igruppe, iAktuell;
		long anz_milli = 0;
		igruppe = cmbGrName.getSelectedIndex();
		iAktuell = cmbAktuell.getSelectedIndex();

		if(iAktuell == 0){
			anz_milli = SystemConfig.oGruppen.gruppenGueltig.get(igruppe)[0];
		}else{
			anz_milli = SystemConfig.oGruppen.gruppenGueltig.get(igruppe)[1];			
		}
 
		DateFormat df = DateFormat.getDateTimeInstance( DateFormat.MEDIUM,
				DateFormat.MEDIUM,  
				Locale.GERMANY);

		if (anz_milli==0){
			dDatum = new Date();
		}else{
			dDatum = new Date(anz_milli);
		}
		s = new String(df.format(dDatum));
		s = s.substring(0,s.indexOf( ' ' )); 
		if(iAktuell == 0){
			gueltig.setText("gültig ab: "+s);
		}else{
			gueltig.setText("gültig bis: "+s);			
		}
		
		
	}
/****************************************/
	private void macheGruppeNeu(){
		
		
		INIFile ini = new INIFile(gruppeninidat);
		int anzahl = SystemConfig.oGruppen.gruppenNamen.size()+1;
		ini.setStringProperty("Gruppen", "GruppenAnzahl", new Integer(anzahl).toString(), null);
		ini.setStringProperty("Gruppen", "GruppenName"+anzahl,neuGruppenName , null);
		ini.setStringProperty("Gruppen", "Gruppe"+anzahl+"NeuAb",neuGruppenGueltigAb , null);
		ini.setStringProperty("Gruppen", "Gruppe"+anzahl+"AltBis",DatFunk.sDatPlusTage(neuGruppenGueltigAb, -1) , null);
		ini.setStringProperty("Gruppen", "Gruppe"+anzahl+"Dauer",(neuGruppenDauer.trim().equals("") ? "0" : neuGruppenDauer.trim() ) , null);
		for(int i = 1; i <= 2;i++){
			String sektion  = neuGruppenName+"_"+i; 
			ini.addSection(sektion, null);
			for(int v = 1; v <= 7; v++){
				ini.setStringProperty(sektion,"WOTA"+v,"0",null);
			}
		}
		ini.save();
		SystemConfig.GruppenLesen();
		cmbGrName.addItem(neuGruppenName);
		cmbGrName.setSelectedItem(neuGruppenName);
		gruppeEinstellen();
	}
	private void macheRowVec(String text,String druck,String plan1,String plan2,String dauer){
		Vector vec = new Vector();
		vec.add(text);
		vec.add(druck);
		vec.add(plan1);
		vec.add(plan2);
		vec.add(new Long(dauer));
		dtblm.addRow((Vector)vec.clone());
		vec.clear();
	}
	private void macheSysVec(int row){
		Vector vec = new Vector();
		String plan1 = (String)jTblGruppen.getValueAt(row,2); 
		String plan2 = (String)jTblGruppen.getValueAt(row,3);
		String druck = (String)jTblGruppen.getValueAt(row,1);
		String text = (String)jTblGruppen.getValueAt(row,0);
		String dauer = new Long((Long)jTblGruppen.getValueAt(row,4)).toString();		
		vec.add(ZeitFunk.MinutenSeitMitternacht(plan1));
		vec.add(ZeitFunk.MinutenSeitMitternacht(plan2));
		vec.add(druck);
		vec.add(text);
		vec.add(dauer);
		int igruppe = cmbGrName.getSelectedIndex();
		int iakt = cmbAktuell.getSelectedIndex();
		int itag = cmbWochenTag.getSelectedIndex();
		if(ltermneu){
			((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)).add(vec);
		}else{
			((Vector)((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)).get(row)).set(0, vec.get(0));			
			((Vector)((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)).get(row)).set(1, vec.get(1));
			((Vector)((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)).get(row)).set(2, vec.get(2));			
			((Vector)((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)).get(row)).set(3, vec.get(3));
			((Vector)((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)).get(row)).set(4, vec.get(4));			
		}
		String sektion = ((String)cmbGrName.getSelectedItem()).trim()+"_"+(iakt+1);
		//System.out.println("Sektion = "+sektion);
		itag = itag+1;
		INIFile ini = new INIFile(gruppeninidat);
		ini.setStringProperty(sektion, "WOTA"+itag, new Integer(jTblGruppen.getRowCount()).toString(), null);
		ini.setStringProperty(sektion, "TA"+itag+"GR"+(row+1), plan1+"-"+plan2, null);
		ini.setStringProperty(sektion, "TA"+itag+"ZE"+(row+1),druck, null);
		ini.setStringProperty(sektion, "TA"+itag+"TX"+(row+1),text, null);
		ini.setStringProperty(sektion, "TA"+itag+"DA"+(row+1),new Integer(dauer).toString(), null);
		ini.save();
	}
	private void loescheTermin(int row){
		int igruppe = cmbGrName.getSelectedIndex();
		int iakt = cmbAktuell.getSelectedIndex();
		int itag = cmbWochenTag.getSelectedIndex();

		String sektion = ((String)cmbGrName.getSelectedItem()).trim()+"_"+(iakt+1);
		//System.out.println("Sektion = "+sektion);
		//System.out.println("Zum löschen markiert"+((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)));
		((Vector)((Vector)((Vector) SystemConfig.oGruppen.gruppeAlle.get(igruppe)).get(iakt)).get(itag)).remove(row);
		itag = itag+1;
		INIFile ini = new INIFile(gruppeninidat);
		ini.setStringProperty(sektion, "WOTA"+itag, new Integer(jTblGruppen.getRowCount()).toString(), null);
		for(int i = 0;i<jTblGruppen.getRowCount();i++){
			String zeit1 = (String)jTblGruppen.getValueAt(i,2);
			String zeit2 = (String)jTblGruppen.getValueAt(i,3);
			ini.setStringProperty(sektion, "TA"+itag+"GR"+(i+1), zeit1+"-"+zeit2, null);
			String druckzeit = (String)jTblGruppen.getValueAt(i,1);
			ini.setStringProperty(sektion, "TA"+itag+"ZE"+(i+1),druckzeit, null);
			String text = (String)jTblGruppen.getValueAt(i,0);
			ini.setStringProperty(sektion, "TA"+itag+"TX"+(i+1),text, null);
			String dauer = new Integer((Integer)jTblGruppen.getValueAt(i,4)).toString();
			ini.setStringProperty(sektion, "TA"+itag+"DA"+(i+1),dauer, null);
		}
		ini.save();
	}
	
	private void gruppeAendern(String name){
		INIFile ini = new INIFile(gruppeninidat);
		int anzahl = cmbGrName.getSelectedIndex();
		ini.setStringProperty("Gruppen", "GruppenName"+(anzahl+1),neuGruppenName , null);
		ini.setStringProperty("Gruppen", "Gruppe"+(anzahl+1)+"NeuAb",neuGruppenGueltigAb , null);
		ini.setStringProperty("Gruppen", "Gruppe"+(anzahl+1)+"AltBis",DatFunk.sDatPlusTage(neuGruppenGueltigAb, -1) , null);		
		ini.setStringProperty("Gruppen", "Gruppe"+(anzahl+1)+"Dauer",(neuGruppenDauer.trim().equals("") ? "0" : neuGruppenDauer.trim() ) , null);
		ini.renameSection(((String)cmbGrName.getSelectedItem()).trim()+"_1",neuGruppenName+"_1",null);
		ini.renameSection(((String)cmbGrName.getSelectedItem()).trim()+"_2",neuGruppenName+"_2",null);
		ini.save();
		cmbGrName.insertItemAt(neuGruppenName,anzahl);
		cmbGrName.removeItemAt(anzahl+1);
		cmbGrName.setSelectedIndex(anzahl);
		Long [] neuwert = SystemConfig.oGruppen.gruppenGueltig.get(anzahl);
		neuwert[0] = DatFunk.DatumsWert(neuGruppenGueltigAb);
		neuwert[1] = DatFunk.DatumsWert(DatFunk.sDatPlusTage(neuGruppenGueltigAb, -1));
		neuwert[2] = new Long(neuGruppenDauer);
		SystemConfig.oGruppen.gruppenGueltig.set(anzahl, neuwert);
		SystemConfig.oGruppen.gruppenNamen.set(anzahl,neuGruppenName);
		labelSetzen();
	}
	
	private void loescheGruppe(){
		int akt = cmbGrName.getSelectedIndex();
		int ges = cmbGrName.getItemCount();
		String gruppe = (String) cmbGrName.getSelectedItem();
		int neupos = akt;
		if(ges==1){
			JOptionPane.showMessageDialog(null,"Dies ist die letzte Gruppe!\n\n"+
						"Die letzte Gruppe kann nicht gelöscht werden. Wenn Sie keine Gruppendefinition benötigen,\n"+
						"Dann weisen Sie in den Kalenderbenutzern einfach keine Gruppe zu.");
						return;
		}
		if(akt == (ges-1) && akt > 0){
			neupos = akt-1;
		}else{
			neupos = akt;
		}
		if(akt == 0){
			neupos = akt;
		}
		((Vector)SystemConfig.oGruppen.gruppeAlle).remove(akt);
		((Vector)SystemConfig.oGruppen.gruppeAlle).trimToSize();
		//((Vector)SystemConfig.oGruppen.gruppeAlle.get(akt)).remove(0);
		//((Vector)SystemConfig.oGruppen.gruppeAlle.get(akt)).trimToSize();		
		((Vector)SystemConfig.oGruppen.gruppenNamen).remove(akt);
		((Vector)SystemConfig.oGruppen.gruppenNamen).trimToSize();		
		((Vector)SystemConfig.oGruppen.gruppenGueltig).remove(akt);
		((Vector)SystemConfig.oGruppen.gruppenGueltig).trimToSize();
		
		
		INIFile ini = new INIFile(gruppeninidat);
		int neuzahl = ((Vector)SystemConfig.oGruppen.gruppenNamen).size();
		ini.setStringProperty("Gruppen", "GruppenAnzahl",new Integer(neuzahl).toString(),null);
		for(int i = 0;i<neuzahl;i++){
			String gname = (String) ((Vector)SystemConfig.oGruppen.gruppenNamen).get(i); 
			ini.setStringProperty("Gruppen", "GruppenName"+(i+1),gname.trim() , null);
			
			String sdat1 = DatFunk.WertInDatum(SystemConfig.oGruppen.gruppenGueltig.get(i)[0]);
			ini.setStringProperty("Gruppen", "Gruppe"+(i+1)+"NeuAb",sdat1 , null);
			
			ini.setStringProperty("Gruppen", "Gruppe"+(i+1)+"AltBis",DatFunk.sDatPlusTage(sdat1, -1), null);		

			ini.setStringProperty("Gruppen", "Gruppe"+(i+1)+"Dauer", new Long(SystemConfig.oGruppen.gruppenGueltig.get(i)[2]).toString()  , null);
			
		}
		ini.removeSection(gruppe+"_1");
		ini.removeSection(gruppe+"_2");
		ini.save();
		cmbGrName.removeItemAt(akt);
		cmbGrName.setSelectedItem(((Vector)SystemConfig.oGruppen.gruppenNamen).get(0));
		//System.out.println("Neue Position = "+neupos);
		//System.out.println("Neues Item = "+(String) cmbGrName.getSelectedItem());
		gruppeEinstellen();
	}
	private void definitionUebertragen(){
		int akt = cmbGrName.getSelectedIndex();
		String sektion = (String)cmbGrName.getSelectedItem()+"_2";
		Vector vec = ((Vector)((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle).get(akt)).get(0));
		((Vector)((Vector)SystemConfig.oGruppen.gruppeAlle).get(akt)).set(1, vec.clone());

		INIFile ini = new INIFile(gruppeninidat);
		for(int i = 0;i<7;i++){
			int anzahl = ((Vector)vec.get(i)).size();
			//System.out.println("Anzahl Termine am Tag "+(i+1)+" = "+anzahl);
			ini.setStringProperty(sektion, "WOTA"+(i+1), new Integer(anzahl).toString(),null);
			for(int v = 0;v<anzahl;v++){
				int anzahl2 = ((Vector)((Vector)vec.get(i)).get(v)).size();
				String szeit1 = ZeitFunk.MinutenZuZeit( new Long((Long) ((Vector)((Vector)vec.get(i)).get(v)).get(0) ).intValue() );
				String szeit2 = ZeitFunk.MinutenZuZeit( new Long((Long) ((Vector)((Vector)vec.get(i)).get(v)).get(1) ).intValue() );				
				String szeit3 = (String) ((Vector)((Vector)vec.get(i)).get(v)).get(2) ;
				String stext  = (String) ((Vector)((Vector)vec.get(i)).get(v)).get(3) ;
				String sdauer  = (String) ((Vector)((Vector)vec.get(i)).get(v)).get(4) ;
				ini.setStringProperty(sektion, "TA"+(i+1)+"GR"+(v+1),szeit1+"-"+szeit2,null);
				ini.setStringProperty(sektion, "TA"+(i+1)+"ZE"+(v+1),szeit3,null);				
				ini.setStringProperty(sektion, "TA"+(i+1)+"TX"+(v+1),stext,null);				
				ini.setStringProperty(sektion, "TA"+(i+1)+"DA"+(v+1),sdauer,null);				
			}
			
		}
		ini.save();

	}
/****************************************/	
}
@SuppressWarnings("unchecked")	
class GruppenTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex<0){return Boolean.class;}
		   else if(columnIndex==4){return Long.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
           //return (columnIndex == 0) ? Boolean.class : String.class;
       }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	    	/*
	    	if (col == 0){
	        	return true;
	        }else if(col == 6){
	        	return true;
	        }else if(col == 7){
	        	return true;
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	        */
	    	return true;
	      }
	   
}
