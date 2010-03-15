package textBlockTherapeuten;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import jxTableTools.RechtsRenderer;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import patientenFenster.ArztBericht;
import patientenFenster.ArztNeuKurz;
import patientenFenster.PatGrundPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;
import dialoge.RehaSmartDialog;

public class ThTextBlock extends RehaSmartDialog{
	public JFormattedTextField tf = null;
	String suchkrit = "";
	String suchid = "";
	
	public JButton neuarzt = null;
	public JRtaTextField[] elterntfs;
	public Container dummyPan = null;
	public Container dummyArzt = null;
	/***************/
	public JRtaTextField suchenach = null;
	public JXTable textblock = null;
	public MyTextBlockModel modtextblock = null;
	public JTextArea tbtext = null;
	boolean blockneugefunden = true;
	boolean incheckundstart = false;
	boolean invecbuild = false;
	boolean inholetext = false;
	int akttbid = -1;
	Vector<String> vectb = null;
	TbEingabeNeu tbEingabeNeu = null;
	String alttitel = "";
	JRtaTextField jtfrueck = null;
	/***************/	
	JPanel content = null;
	public JXPanel grundPanel = null;
	public String arztbisher;
	ArztBericht abr = null;
	
	List<String> sysVars1;
	List<String> sysVars2;
	String[][] sysInhalt1;
	String[] sysInhalt2;

	
		public ThTextBlock(JXFrame owner, String name,String diag,ArztBericht abr) {
			super(owner, name);
			//setSize(430,300);
			setSize(450,600);
			this.suchkrit = diag;
			this.abr = abr;
			grundPanel = new JXPanel(new BorderLayout());			
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					grundPanel.setBackgroundPainter(Reha.thisClass.compoundPainter.get("TextBlock"));
					macheSysVars1();
					macheSysVars2();
					return null;
				}
				
			}.execute();
		    super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
//		    String xtitel = "<html>Textbausteine -->&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#ffffff'>"+suchkrit+"</font></b>";
//		    super.getSmartTitledPanel().setTitle(xtitel);
			content = getAuswahl();
			grundPanel.add(content,BorderLayout.CENTER);
			getSmartTitledPanel().setContentContainer(grundPanel);

			//this.getContentPanel().add(content);
			
			final JPanel thispan = content;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
					grundPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
					grundPanel.getActionMap().put("doSuchen", new BausteinAction());
				}
			});
			
			//pack();
			new Thread(){
				public void run(){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							fuelleTabelle(suchkrit);
							return null;
						}
					}.execute();
				}
			}.start();
		}
		private JPanel getAuswahl(){
			suchenach = new JRtaTextField("NIX",true);
			suchenach.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent arg0) {
					if(arg0.getKeyCode()==10){
						arg0.consume();
						suchenach.requestFocus();
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								String mwk = macheWhereKlausel(" (tbthema='"+suchkrit+"') AND ",suchenach.getText(),new String[] {"tbtitel","tbtext"});
								//System.out.println(mwk);
								fuelleSucheInTabelle(mwk);
								return null;
							}
						}.execute();
					}
					
					if(arg0.getKeyCode()==27){
						arg0.consume();
						dispose();
					}
					
				}
			});
			tbtext = new JTextArea();
			tbtext.setFont(new Font("Courier New",Font.PLAIN,11));
			tbtext.setForeground(Color.BLUE);
			tbtext.setLineWrap(true);
			tbtext.setName("tbtext");
			tbtext.setWrapStyleWord(true);
			tbtext.setEditable(false);
			

			modtextblock = new MyTextBlockModel();
			modtextblock.setColumnIdentifiers(new String[] {"Block-Rang","Titel","ID"});
			textblock = new JXTable(modtextblock);
			textblock.setEditable(false);
			textblock.setSortable(true);
			textblock.getColumn(0).setMaxWidth(75);
			//textblock.getColumn(2).setMinWidth(0);
			textblock.getColumn(2).setMaxWidth(40);
			textblock.getColumn(2).setCellRenderer(new RechtsRenderer());			
			textblock.setSelectionMode(0);
			textblock.getSelectionModel().addListSelectionListener( new TblockListSelectionHandler());
			textblock.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent arg0) {
					if(arg0.getClickCount()==2){
						tbCheckundStart();						
					}
				}	
			});
			textblock.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent arg0) {
					if(arg0.getKeyCode()==10){
						arg0.consume();
						tbCheckundStart();
					}
					if(arg0.getKeyCode()==27){
						dispose();
					}

				}	
			});

			FormLayout lay = new FormLayout(
				//x   1    2          3           4         5          6    
					"5dlu,0dlu,right:max(40dlu;p),2dlu,fill:300dlu:grow(1.00),5dlu",
				//y	  1   2   3       4                  5    6     7                
					"5dlu,p,2dlu,fill:100dlu:grow(1.00),2dlu,30dlu,10dlu,5dlu"
					);
			PanelBuilder pb = new PanelBuilder(lay);
			CellConstraints cc = new CellConstraints();
			pb.getPanel().setOpaque(false);
			pb.addLabel("finde Textblock",cc.xy(3,2));
			pb.add(suchenach,cc.xy(5,2));
			JScrollPane jscr = JCompTools.getTransparentScrollPane(textblock);
			jscr.getVerticalScrollBar().setUnitIncrement(15);
			jscr.validate();
			pb.add(jscr,cc.xyw(2, 4,4));
			jscr = JCompTools.getTransparentScrollPane(tbtext);
			jscr.getVerticalScrollBar().setUnitIncrement(15);
			jscr.validate();
			pb.add(jscr,cc.xywh(2, 6,4,2));
			pb.getPanel().validate();
			return pb.getPanel();
		}
		public void fuelleTabelle(String diag){
		    String xtitel = "<html>Textbausteine -->&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#ffffff'>"+diag+"</font></b>";
		    super.getSmartTitledPanel().setTitle(xtitel);
		    this.suchkrit = diag;
			Vector vec = SqlInfo.holeSaetze("tbkg", "CONCAT(tbblock,' - ',tbrang) AS blockrang,tbtitel,id", "tbthema='"+diag+"' ORDER BY blockrang" , Arrays.asList(new String[] {}));
			int anz = vec.size();
			//Vector<String> vec2 = new Vector<String>();
			modtextblock.setRowCount(0);
			if(anz>0){
				for(int i = 0;i<anz;i++){
					modtextblock.addRow((Vector)((Vector)vec.get(i)).clone() );
					if(i==0){
						holeTbText( new Integer((String)((Vector)vec.get(i)).get(2)) );
					}
				}
				textblock.setRowSelectionInterval(0, 0);
			}
			textblock.validate();
		}
		private void fuelleSucheInTabelle(String whereKlausel){
			Vector vec = SqlInfo.holeSaetze("tbkg", "CONCAT(tbblock,' - ',tbrang) AS blockrang,tbtitel,id", whereKlausel , Arrays.asList(new String[] {}));
			int anz = vec.size();
			modtextblock.setRowCount(0);
			//Vector<String> vec2 = new Vector<String>();
			if(anz>0){
				for(int i = 0;i<anz;i++){
					modtextblock.addRow((Vector)((Vector)vec.get(i)).clone() );
					if(i==0){
						holeTbText( new Integer((String)((Vector)vec.get(i)).get(2)) );
					}
				}
				textblock.setRowSelectionInterval(0, 0);
			}
			textblock.validate();
		}
		private void testeTbText(String text){
			/// Hier den SytemVariablen-Check einbauen!!!!!!!!!!!!!!!!!!!!
			Vector<String> tbvars = new Vector<String>();
			int lang =  text.length();
			int i = 0;
			boolean start = false;
			boolean stop = false;
			String var = "";
			String test = "";
			for(i = 0;i < lang;i++){
				for(int i2 = 0; i2 < 1;i2++){
					test = text.substring(i,i+1);
					if(test.equals("^") && (!start)){
						var = var+test;
						start = true;
						break;
					}
					if(start){
						var = var+test;
						if(test.equals("^")){
							if(!var.equals("^CRLF^")){
								tbvars.add(new String(var));								
							}
							start = false;
							var = "";

						}
						break;
					}
				}
			}
			vectb = (Vector)tbvars.clone();
			//System.out.println("Variablen Vector = "+tbvars);
		}
		private void infoPosition(int diff,int i,int lang){
			//System.out.println("Längendifferenz ="+diff+"  /  neuer Wert für Position i ="+i+" / neuer Wert für Textlänge lang="+lang);
		}
		private void holeTbText(int tbid){
			String text = (String) SqlInfo.holeSatz("tbkg", "tbtext", "id='"+tbid+"'", Arrays.asList(new String[] {})).get(0);
			text = testeAufSysVars(text);
			tbtext.setText(text );
			final int xtbid = tbid;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					invecbuild = true;
					testeTbText(tbtext.getText());
					akttbid = xtbid;
					invecbuild = false;
					inholetext = false;
					return null;
				}
			}.execute();
		}
		public void setzeSucheAufNull(){
			suchenach.setText("");
		}
		public void setzeEingabeTitel(String titel){
			super.getSmartTitledPanel().setTitle(titel);
		}
		/******************************************************/
		private void tbCheckundStart(){
			if(incheckundstart){
				return;
			}
			incheckundstart = true;
			int row = textblock.getSelectedRow();
			if(row < 0){
				//System.out.println("Keine Tabellenzeile ausgewählt");
				incheckundstart = false;
				return;
			}
			//int testeid1 = textblock.convertRowIndexToModel(row);
			int testeid2 = new Integer((String)textblock.getValueAt(row, 2));
			if(testeid2 != akttbid){
				long zeit = System.currentTimeMillis();
				while( akttbid != testeid2 ){
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if((System.currentTimeMillis()-zeit) > 2000){
						System.out.println("Zwangsabbruch akttbid immer noch nicht identisch");
						inholetext = false;
						break;
					}
				}
				if(akttbid != testeid2){
					JOptionPane.showMessageDialog(null,"Konnte den Platzhalter-Vector nicht erstellen");
					incheckundstart = false;
					return;
				}
			}
			if(vectb.size() > 0){
				if(tbEingabeNeu == null){
					jtfrueck = new JRtaTextField("NIX",false);
					tbEingabeNeu = new TbEingabeNeu(tbtext.getText(),vectb,this,jtfrueck);			
				}else{
					tbEingabeNeu.neueDaten(tbtext.getText(),vectb);
				}
				alttitel = super.getSmartTitledPanel().getTitle();
				grundPanel.remove(this.content);
				grundPanel.add(tbEingabeNeu,BorderLayout.CENTER);
				grundPanel.validate();
				repaint();
			}else{
				
				String sblock = (String) textblock.getValueAt(textblock.getSelectedRow(), 0);
				int block = new Integer(sblock.substring(0,1))-1;
				abr.schreibeTextBlock(block,new String(tbtext.getText()) );
				incheckundstart = false;
				inholetext = false;
				this.dispose();
			}
			incheckundstart = false;
			inholetext = false;
		}
		public void wechsleRueckwaerts(){
			grundPanel.remove(tbEingabeNeu);
			grundPanel.add(content,BorderLayout.CENTER);
			super.getSmartTitledPanel().setTitle(alttitel);
			grundPanel.validate();
			repaint();
			if(!jtfrueck.getText().equals("")){
				String sblock = (String) textblock.getValueAt(textblock.getSelectedRow(), 0);
				int block = new Integer(sblock.substring(0,1))-1;
				abr.schreibeTextBlock(block,new String(jtfrueck.getText()) );
				incheckundstart = false;
				inholetext = false;
				this.dispose();
			}
			//JOptionPane.showMessageDialog(null,jtfrueck.getText());
			inholetext = false;
			incheckundstart = false;
			textblock.requestFocus();
		}
		/******************************************************/
		
		private String macheWhereKlausel(String praefix,String test,String[] suchein){
			String ret = praefix;
			String cmd = test;
			cmd = new String(cmd.replaceAll("   ", " "));
			cmd = new String(cmd.replaceAll("  ", " "));
			// wer jetzt immer noch Leerzeichen in der Suchbedingung hat ist selbst schuld da� er nix finder!!!
			String[] felder = suchein;
			String[] split = cmd.split(" ");
			if(split.length==1){
				ret = ret +" (";
				for(int i = 0; i < felder.length;i++){
					ret = ret+felder[i]+" like '%"+cmd+"%'";
					if(i < felder.length-1){
						ret = ret+ " OR ";
					}
				}
				ret = ret +") ";
				return ret;
			}
			
			
			ret = ret +"( ";
			for(int i = 0; i < split.length;i++){
				if(! split[i].equals("")){
					ret = ret +" (";
					for(int i2 = 0; i2 < felder.length;i2++){
						ret = ret+felder[i2]+" like '%"+split[i]+"%'";
						if(i2 < felder.length-1){
							ret = ret+ " OR ";
						}
					}
					ret = ret +") ";
					if(i < split.length-1){
						ret = ret+ " AND ";
					}
				}
				
			}
			ret = ret +") ";
			return ret;
		}
		class MyTextBlockModel extends DefaultTableModel{
			   /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Class getColumnClass(int columnIndex) {
				   if(columnIndex==0){
					   return String.class;
				   }else{
					   return String.class;
				   }
			}
		    public boolean isCellEditable(int row, int col) {
		        //Note that the data/cell address is constant,
		        //no matter where the cell appears onscreen.
		    	return true;
		    }
			public Object getValueAt(int rowIndex, int columnIndex) {
				String theData = (String) ((Vector)getDataVector().get(rowIndex)).get(columnIndex); 
				Object result = null;
				result = theData;
				return result;
			}
		}
		class TblockListSelectionHandler implements ListSelectionListener {

		    public void valueChanged(ListSelectionEvent e) {
				if(blockneugefunden || inholetext){
					blockneugefunden = false;
					//System.out.println("Wert von inholetext = "+inholetext);
					return;
				}
		        ListSelectionModel lsm = (ListSelectionModel)e.getSource();

		        int firstIndex = e.getFirstIndex();
		        int lastIndex = e.getLastIndex();
		        boolean isAdjusting = e.getValueIsAdjusting();
		        if(isAdjusting){
		        	return;
		        }
				//StringBuffer output = new StringBuffer();
		        if (lsm.isSelectionEmpty()) {

		        } else {
		            int minIndex = lsm.getMinSelectionIndex();
		            int maxIndex = lsm.getMaxSelectionIndex();
		            for (int i = minIndex; i <= maxIndex; i++) {
		                if (lsm.isSelectedIndex(i)) {
		                	final int ix = i;
		                	new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground() throws Exception {
									inholetext = true;
									holeTbText(new Integer((String)textblock.getValueAt(ix, 2)));
									return null;
								}
		                	}.execute();
		                    break;
		                }
		            }
		        }
		    }
		}

		class BausteinAction extends AbstractAction {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				suchenach.requestFocus();
			}
			 
		}
		private void macheSysVars1(){
			sysVars1 = Arrays.asList(new String[] {"^DerPat/DiePat^","^derPat/diePat^",
					"^DemPat/DerPat^","^demPat/derPat^","^DenPat/DiePat^","^denPat/diePat^",
					"^desPat/derPat^","^ihm/ihr^","^Sein/Ihr^","^sein/ihr^","^Seine/Ihre^","^seine/ihre^",
					"^Er/Sie^","^er/sie^"
		
			});
			sysInhalt1 = new String[][] {{"Der Patient","Die Patientin"},{"der Patient","die Patientin"},
					{"Dem Patient","Der Patientin"},{"dem Patient","der Patientin"},{"Den Patient","Die Patientin"},
					{"den Patient","die Patientin"},{"des Patient","der Patientin"},{"ihm","ihr"},
					{"Sein","Ihr"},{"sein","ihr"},{"Seine","Ihre"},{"seine","ihre"},
					{"Er","Sie"},{"er","sie"}};
		}
		private void macheSysVars2(){
			sysVars2 = Arrays.asList(new String[] {
					"^ErstDatum^","^LetztDatum^","^RezDatum^","^Anrede^","^PatName^","^PatVName^","^Heute^"	
			});
			sysInhalt2 = new String[] {SystemConfig.hmAdrRDaten.get("<Rerstdat>"),
					SystemConfig.hmAdrRDaten.get("<Rletztdat>"),
					SystemConfig.hmAdrRDaten.get("<Rdatum>"),
					SystemConfig.hmAdrPDaten.get("<Panrede>"),
					SystemConfig.hmAdrPDaten.get("<Pnname>"),
					SystemConfig.hmAdrPDaten.get("<Pvname>"),
					DatFunk.sHeute()};
		}
		private String testeAufSysVars(String text){
			String replacement ="";
			String origtext= "";
			boolean frau = (Reha.thisClass.patpanel.patDaten.get(0).equalsIgnoreCase("FRAU") ? true : false);
			origtext = text;
			try{
				for(int i = 0; i < sysVars1.size();i++){
					if(origtext.contains(sysVars1.get(i))){
						replacement =  sysInhalt1[i][(frau ? 1 : 0)];
						origtext = origtext.replaceAll(Pattern.quote(sysVars1.get(i)),replacement);
						//origtext = origtext.replace(sysVars1.get(i),replacement);
					}
				}
				for(int i = 0; i < sysVars2.size();i++){
					if(origtext.contains(sysVars2.get(i))){
						replacement =  sysInhalt2[i];
						origtext = origtext.replaceAll(Pattern.quote(sysVars2.get(i)),replacement);
						//origtext = origtext.replace(sysVars2.get(i),replacement);
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return origtext;
		}

		
/*************************************************/		
}		
