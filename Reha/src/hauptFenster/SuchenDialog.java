package hauptFenster;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;

import systemEinstellungen.SystemConfig;
import systemTools.StringTools;
import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaTPEvent;
import events.RehaTPEventListener;

public class SuchenDialog extends JXDialog implements RehaTPEventListener{

	private static final long serialVersionUID = 1L;
	private JXPanel jContentPane = null;
	private JXTitledPanel jXTitledPanel = null;
	private JXPanel jContent = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JXButton jButton = null;
	private JXTable jtable = null;
	public JTextField jTextField = null;
	
	private JTextField jtext = null;

	private int clickX;
	private int clickY;
	
	private Cursor cmove = Reha.thisClass.cmove;
	private Cursor cnsize = Reha.thisClass.cnsize;
	private Cursor cnwsize = Reha.thisClass.cnwsize;
	private Cursor cnesize = Reha.thisClass.cnesize;
	private Cursor cswsize = Reha.thisClass.cswsize;
	private Cursor cwsize = Reha.thisClass.cwsize;
	private Cursor csesize = Reha.thisClass.csesize;
	private Cursor cssize = Reha.thisClass.cssize;
	private Cursor cesize = Reha.thisClass.cesize;	
	private Cursor cdefault = Reha.thisClass.cdefault;

	//private Cursor cmove = new Cursor(Cursor.MOVE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor cnsize = new Cursor(Cursor.N_RESIZE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor cnwsize = new Cursor(Cursor.NW_RESIZE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor cnesize = new Cursor(Cursor.NE_RESIZE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor cswsize = new Cursor(Cursor.SW_RESIZE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor cwsize = new Cursor(Cursor.W_RESIZE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor csesize = new Cursor(Cursor.SE_RESIZE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor cssize = new Cursor(Cursor.S_RESIZE_CURSOR);  //  @jve:decl-index=0:
	//private Cursor cesize = new Cursor(Cursor.E_RESIZE_CURSOR);  //  @jve:decl-index=0:	
	//private Cursor cdefault = Reha.thisClass.normalCursor;  //  @jve:decl-index=0:

	private boolean insize;
	private int[] orgbounds = {0,0};
	private int sizeart;
	private String[] sEventDetails ={null,null};
	
	public JComponent focusBack = null;
	private String fname = "";  //  @jve:decl-index=0:
	public DefaultTableModel tblDataModel;
	
	public  int suchart = 0;
	/**
	 * @param 
	 */
	public SuchenDialog(JXFrame owner,JComponent focusBack,String fname,int art) {
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		this.focusBack = (focusBack == null ? null : focusBack);
		this.fname = (String) (fname.equals("") ? "" : fname);
		this.suchart = art;
		initialize();
		jTextField.setText(fname);
		new suchePatient().init(tblDataModel);
		this.setAlwaysOnTop(true);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});
		
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				jTextField.requestFocus();
			}
		});
	}
	public void	setzeReihe(Vector<?> vec){
		tblDataModel.addRow(vec);
		jtable.validate();
		
	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {

		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
				}
			}
		}catch(NullPointerException ne){

		}
	}	
	

	public void suchDasDing(String suchkrit){
		jTextField.setText(suchkrit);
		jXTitledPanel.setTitle("Suche Patient..."+suchkrit);
		new suchePatient().init(tblDataModel);
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setUndecorated(true);
		this.setSize(300, 400);
		this.setTitle("Dialog-Test");
		this.setContentPane(getJContentPane());
		/*
		SuchenDialog.this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				////System.out.println("Weshalb windowClosing()"); // TODO Auto-generated Event stub windowClosing()
			}
		});
		*/
		this.setName("PatSuchen");
		this.setModal(false);
		this.setResizable(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	public void setzeFocusAufSucheFeld(){
		jtext.requestFocus();
		
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {

			GridBagConstraints gridBagConstraints = new GridBagConstraints() ;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);

			JXPanel gridJx = new JXPanel();
			gridJx.setLayout(new GridBagLayout());
			gridJx.setBackground(Color.WHITE);
			gridJx.setBorder(null);
			
			jContentPane = new JXPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.setBorder(null);
			JXTitledPanel jtp = getJXTitledPanel(); 

			
			jContent = new JXPanel(new BorderLayout());
			jContent.setSize(new Dimension(286, 162));
			jContent.add(getJButton(), BorderLayout.SOUTH);
			JXPanel jp1 = new JXPanel(new FlowLayout());
			jp1.setBorder(null);
			JLabel jlb = new JLabel("Patient suchen: ");
			jp1.add(jlb);
			
			jtext = getJTextField();
			jtext.setPreferredSize(new Dimension(100,20));
			jtext.addKeyListener(new java.awt.event.KeyAdapter() {   
				public void keyPressed(java.awt.event.KeyEvent e) {    
					if (e.getKeyCode() == 10){
						e.consume();
						new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								new suchePatient().init(tblDataModel);
								return null;
							}
						}.execute();
						
					}
					if (e.getKeyCode() == 27){
						e.consume();
						setVisible(false);
						sucheBeenden();
					}

					if (e.getKeyCode() == 40){
						e.consume();
						if (jtable.getRowCount() > 0){
							jtable.requestFocus();
							if(jtable.getSelectedRow()>=0){
								jtable.requestFocus();
							}else{
								jtable.setRowSelectionInterval(0, 0);	
							}
						}
					}
				}   
				public void keyReleased(java.awt.event.KeyEvent e) {    
					if (e.getKeyCode() == 10){
						//suchePatient();
					}
				}
				public void keyTyped(java.awt.event.KeyEvent e) {
					////System.out.println("keyTyped()"); // TODO Auto-generated Event stub keyTyped()
				}
			});
			jp1.add(jtext);
			jContent.add(jp1, BorderLayout.NORTH);
			
			/**
			 * JXTable
			 */
			JScrollPane jscr = new JScrollPane();
			JXPanel jp2 = new JXPanel(new BorderLayout());

			Vector <String>reiheVector = new Vector<String>();
			reiheVector.addElement("Nachname");
			reiheVector.addElement("Vorname");
			reiheVector.addElement("Geboren");
			reiheVector.addElement("Pat-Nr.");			
			tblDataModel = new DefaultTableModel();
			tblDataModel.setColumnIdentifiers(reiheVector);
			jtable = new JXTable(tblDataModel);
			this.jtable.getColumnModel().getColumn(0).setPreferredWidth(100);
			this.jtable.getColumn(3).setMinWidth(0);	
			this.jtable.getColumn(3).setMaxWidth(0);
			
			InputMap inputMap = jtable.getInputMap(JComponent.WHEN_FOCUSED);

			inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0));
			jtable.setInputMap(JComponent.WHEN_FOCUSED,inputMap);			

			jtable.setEditable(false);

			jtable.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					if (e.getKeyCode() == 10){
						sucheAbfeuern();
						e.consume();	
						setVisible(false);
					}
					if (e.getKeyCode() == 40 ||e.getKeyCode() == 38){
						//sucheAbfeuern();
					}
					if (e.getKeyCode() == KeyEvent.VK_F && e.isAltDown()){
						jTextField.requestFocus();
					}
					if (e.getKeyCode() == 27){
						e.consume();
						setVisible(false);
						sucheBeenden();						
					}

				}
			});
						
			jtable.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseEntered(java.awt.event.MouseEvent e) {
					SuchenDialog.this.setCursor(cdefault);
				}
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if(e.getClickCount() == 2){
						sucheAbfeuern();
						e.consume();
						setVisible(false);
					}	
				}
			});
			jtable.updateUI();
			jscr.setViewportView(jtable);
			jp2.add(jscr,BorderLayout.CENTER);
			
			jContent.add(jp2, BorderLayout.CENTER);
			gridJx.add(jContent,gridBagConstraints);
			jtp.setContentContainer(gridJx);
			jtp.validate();
			jContentPane.add(jtp, BorderLayout.CENTER);
			jtext.requestFocus();
			
		}
		return jContentPane;
	}
	public void sucheAbfeuern(){
		String s1 = String.valueOf("#PATSUCHEN");
		String s2 = (String) jtable.getValueAt(jtable.getSelectedRow(), 3);
		setDetails(s1,s2) ;
		PatStammEvent pEvt = new PatStammEvent(SuchenDialog.this);
		pEvt.setPatStammEvent("PatSuchen");
		pEvt.setDetails(s1,s2,fname) ;
		PatStammEventClass.firePatStammEvent(pEvt);		
	}
	public void sucheBeenden(){
		String s1 = String.valueOf("#SUCHENBEENDEN");
		String s2 = (String) "";
		setDetails(s1,s2) ;
		PatStammEvent pEvt = new PatStammEvent(SuchenDialog.this);
		pEvt.setPatStammEvent("PatSuchen");
		pEvt.setDetails(s1,s2,fname) ;
		PatStammEventClass.firePatStammEvent(pEvt);		
	}
	

	/**
	 * This method initializes JXTitledPanel	
	 * 	
	 * @return org.jdesktop.swingx.JXTitledPanel	
	 */
	private JXTitledPanel getJXTitledPanel() {
		if (jXTitledPanel == null) {
			jXTitledPanel = new JXTitledPanel();
			
			jXTitledPanel.setTitle("Suche Patient..."+this.fname);
			jXTitledPanel.setTitleForeground(Color.WHITE);
			jXTitledPanel.setName("PatSuchen");
			JXButton jb2 = new JXButton();
			jb2.setBorder(null);
			jb2.setOpaque(false);
			jb2.setPreferredSize(new Dimension(16,16));
			jb2.setIcon(SystemConfig.hmSysIcons.get("rot"));
			jb2.addMouseListener(new java.awt.event.MouseAdapter(){
				public void mouseClicked(java.awt.event.MouseEvent e) {
					e.consume();
					setVisible(false);
					sucheBeenden();
				}
			});
			jXTitledPanel.setRightDecoration(jb2);
			jXTitledPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {   
				public void mouseMoved(java.awt.event.MouseEvent e) {    
					for(int i = 0; i < 1; i++){
						sizeart=-1;
						setCursor(cdefault);
						if ((e.getX() <= 4 && e.getY() <= 4)){ //nord-west
							insize = true;
							sizeart = 1;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cnwsize);
							break;
						}
						if( (e.getX()>=  (((JComponent) e.getSource()).getWidth()-4)) && e.getY() <= 4){//nord-ost
							insize = true;
							sizeart = 2;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cnesize);
							break;
						}
						if(e.getY() <= 4){//nord
							insize = true;
							sizeart = 3;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cnsize);
							break;
						}
						if ((e.getX() <= 4 && e.getY() >= (((JComponent) e.getSource()).getHeight()-4))){ //s�d-west
							insize = true;
							sizeart = 4;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cswsize);
							break;
						}
						if ((e.getX() <= 4)){ //west
							insize = true;
							sizeart = 5;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cwsize);
							break;
						}
						if ((e.getX()>=  (((JComponent) e.getSource()).getWidth()-4)) && //s�d-ost
								e.getY() >= (((JComponent) e.getSource()).getHeight()-4)){ 
							insize = true;
							sizeart = 6;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(csesize);
							break;
						}
						if (e.getY() >= (((JComponent) e.getSource()).getHeight()-2)){ //s�d
							insize = true;
							sizeart = 7;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cssize);
							break;
						}
						if (e.getX() >= (((JComponent) e.getSource()).getWidth()-4)){ //ost
							insize = true;
							sizeart = 8;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cesize);
							break;
						}

						insize = false;
						sizeart = -1;
						setCursor(cdefault);
					}
				}
				public void mouseDragged(java.awt.event.MouseEvent e) {
					if (! insize && clickY > 0){
						SuchenDialog.this.getLocationOnScreen();
						SuchenDialog.this.setLocation(e.getXOnScreen()-clickX,e.getYOnScreen()-clickY);
						SuchenDialog.this.repaint();
						setCursor(cmove);
					}else if (insize){
						Dimension dim = SuchenDialog.this.getSize();
						int oX = e.getXOnScreen();
						int oY = e.getYOnScreen();
						for(int i = 0;i<1;i++){
							if(sizeart==1){ //nord-west
								dim.width = (oX > orgbounds[0] ? dim.width-(oX-orgbounds[0]) : dim.width+(orgbounds[0]-oX));
								dim.height = (oY > orgbounds[1] ? dim.height-(oY-orgbounds[1]) : dim.height+(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen(),e.getYOnScreen());
								setCursor(cnwsize);
								break;
							}
							if(sizeart==2){ //nord-ost
								dim.width = (oX > orgbounds[0] ? dim.width+(oX-orgbounds[0]) : dim.width-(orgbounds[0]-oX));
								dim.height = (oY > orgbounds[1] ? dim.height-(oY-orgbounds[1]) : dim.height+(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen()-dim.width,e.getYOnScreen());
								setCursor(cnesize);
								break;
							}
							if(sizeart==3){ //nord
								//dim.width = (oX > orgbounds[0] ? dim.width+(oX-orgbounds[0]) : dim.width-(orgbounds[0]-oX));
								dim.height = (oY > orgbounds[1] ? dim.height-(oY-orgbounds[1]) : dim.height+(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen()-e.getX(),e.getYOnScreen());
								setCursor(cnsize);
								break;
							}	
							if(sizeart==4){ //s�d-west
								dim.width = (oX > orgbounds[0] ? dim.width-(oX-orgbounds[0]) : dim.width+(orgbounds[0]-oX));
								dim.height = (oY > orgbounds[1] ? dim.height+(oY-orgbounds[1]) : dim.height-(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen(),e.getYOnScreen()-dim.height);
								setCursor(cswsize);
								break;
							}
							if(sizeart==5){ //west
								dim.width = (oX > orgbounds[0] ? dim.width-(oX-orgbounds[0]) : dim.width+(orgbounds[0]-oX));
								//dim.height = (oY > orgbounds[1] ? dim.height+(oY-orgbounds[1]) : dim.height-(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen(),e.getYOnScreen()-e.getY());
								setCursor(cwsize);
								break;
							}
							if(sizeart==6){ //s�d-ost
								dim.width = (oX > orgbounds[0] ? dim.width+(oX-orgbounds[0]) : dim.width-(orgbounds[0]-oX));
								dim.height = (oY > orgbounds[1] ? dim.height+(oY-orgbounds[1]) : dim.height-(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen()-dim.width,e.getYOnScreen()-dim.height);
								setCursor(cwsize);
								break;
							}
							if(sizeart==7){ //s�d
								//dim.width = (oX > orgbounds[0] ? dim.width+(oX-orgbounds[0]) : dim.width-(orgbounds[0]-oX));
								dim.height = (oY > orgbounds[1] ? dim.height+(oY-orgbounds[1]) : dim.height-(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen()-e.getX(),e.getYOnScreen()-dim.height);
								setCursor(cssize);
								break;
							}
							if(sizeart==8){ //ost
								dim.width = (oX > orgbounds[0] ? dim.width+(oX-orgbounds[0]) : dim.width-(orgbounds[0]-oX));
								//dim.height = (oY > orgbounds[1] ? dim.height+(oY-orgbounds[1]) : dim.height-(orgbounds[1]-oY));						
								dim.width = (dim.width < 185 ? 185 : dim.width);
								dim.height = (dim.height < 125 ? 125 : dim.height);
								orgbounds[0] = oX;
								orgbounds[1] = oY;
								SuchenDialog.this.setSize(dim);
								SuchenDialog.this.setLocation(e.getXOnScreen()-e.getX(),e.getYOnScreen()-e.getY());
								setCursor(cesize);
								break;
							}
							insize = false;
							setCursor(cdefault);
						}
					}else{
						insize = false;
						setCursor(cdefault);
					}
				}
			});

			jXTitledPanel.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseExited(java.awt.event.MouseEvent e) {    
				}   
				public void mouseReleased(java.awt.event.MouseEvent e) {    
					clickX = -1;
					clickY = -1;
					orgbounds[0] = -1;
					orgbounds[1] = -1;					
					insize = false;
					setCursor(cdefault);
				}
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (e.getY() <= 25){
						clickY = e.getY();
						clickX = e.getX();
					}
				}
			});
		}
		return jXTitledPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JXButton getJButton() {
		if (jButton == null) {
			jButton = new JXButton();
			jButton.setText("Schliessen");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					RehaEvent rEvt = new RehaEvent(this);
					rEvt.setRehaEvent("Am Arsch lecken");
					RehaEventClass.fireRehaEvent(rEvt);
					SuchenDialog.this.setVisible(false);
					SuchenDialog.this.dispose();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new Dimension(40, 20));
		}
		return jTextField;
	}
	class suchePatient extends SwingWorker<Void,Void>{
		
	DefaultTableModel tblDataModel;
	
	@SuppressWarnings("unchecked")
	private void suchePatienten(){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = "";
		//Vector <Vector<String[]>>dataVector = new Vector<Vector<String[]>>();
		Vector<String> reiheVector = new Vector<String>();
		reiheVector.addElement("Nachname");
		reiheVector.addElement("Nachname");
		reiheVector.addElement("Geboren");
		reiheVector.addElement("Patientennummer");
		String[] suche;
		setCursor(Reha.thisClass.wartenCursor);	
		if(suchart == 0){
			
		
		if (jTextField.getText().trim().contains(" ") ){
			suche = jTextField.getText().split(" ");
			if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				sstmt = "Select n_name,v_name,DATE_FORMAT(geboren,'%d.%m.%Y') AS geboren,pat_intern  from pat5 where n_name LIKE '"+
				StringTools.Escaped(suche[0].trim()) +"%' AND v_name LIKE '"+StringTools.Escaped(suche[1].trim())+"%' order by n_name,v_name";
			}else{ //ADS
				sstmt = "Select n_name,v_name,geboren,pat_intern  from pat5 where n_name LIKE UPPER('"+
				suche[0].trim() + "%') AND v_name LIKE UPPER('" + StringTools.Escaped(suche[1].trim()) +"%') order by n_name,v_name";
			}
		}else{
			if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				sstmt = "Select n_name,v_name,DATE_FORMAT(geboren,'%d.%m.%Y') AS geboren,pat_intern from pat5 where n_name LIKE '"+
				StringTools.Escaped(jTextField.getText().trim()) +"%'  order by n_name,v_name";
			}else{ //ADS
				sstmt = "Select n_name,v_name,geboren,pat_intern from pat5 where n_name LIKE UPPER('"+
				StringTools.Escaped(jTextField.getText().trim()) +"%') order by n_name,v_name";
			}
		}
		
		}else if(suchart == 1){
			sstmt = "select n_name,v_name,geboren,pat_intern from pat5 where pat_intern = '"+jTextField.getText().trim()+"' LIMIT 1";
		}else{
			return;
		}
		try {
			
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = stmt.executeQuery(sstmt);
				Vector<String> rowVector = new Vector<String>();
				while( rs.next()){
					rowVector.clear();
					for(int i = 1; i <= 4; i++){
						rowVector.addElement(rs.getString(i) != null ? rs.getString(i) : "");
					}
					setzeReihe((Vector<String>)rowVector.clone());
				}
				setCursor(Reha.thisClass.normalCursor);
				
			}catch(SQLException ev){
			}	

		}catch(SQLException ex) {
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
	public void init(DefaultTableModel tblDataModel){
		this.tblDataModel = tblDataModel;
		this.tblDataModel.setRowCount(0);
		execute();
	}
	@Override
	protected Void doInBackground() throws Exception {
		suchePatienten();
		return null;
	}
	}
	
	/*
	private void sucheHistorie(String patnr){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = "";
		Vector <Vector<String[]>>dataVector = new Vector<Vector<String[]>>();
		Vector<String> reiheVector = new Vector<String>();
		reiheVector.addElement("RezeptNr");
		reiheVector.addElement("Datum");
		reiheVector.addElement("PatNr.");
		reiheVector.addElement("Anzahl1");
		String[] suche;
		if (jTextField.getText().trim().contains(" ") ){
			suche = jTextField.getText().split(" ");
			if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				sstmt = "Select REZ_NR,DATE_FORMAT(REZ_DATUM,'%d.%m.%Y') AS REZ_DATUM,PAT_INTERN,ANZAHL1  from lza where PAT_INTERN = '"+
				patnr+"' order by DATE_FORMAT(REZ_DATUM,'%Y.%m.%d')";
			}else{
				sstmt = "Select REZ_NR,REZ_DATUM,PAT_INTERN,ANZAHL1  from lza where PAT_INTERN = '"+
				patnr+"' order by REZ_DATUM";
			}
			
		}else{
			if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				sstmt = "Select REZ_NR,DATE_FORMAT(REZ_DATUM,'%d.%m.%Y') AS REZ_DATUM,PAT_INTERN,ANZAHL1  from lza where PAT_INTERN = '"+
				patnr+"' order by DATE_FORMAT(REZ_DATUM,'%Y.%m.%d')";
			}else{
				sstmt = "Select REZ_NR,REZ_DATUM,PAT_INTERN,ANZAHL1  from lza where PAT_INTERN = '"+
				patnr+"' order by REZ_DATUM";
			}
		}
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = (ResultSet) stmt.executeQuery(sstmt);		
				while( rs.next()){
					Vector rowVector = new Vector();
					for(int i = 1; i <= 4; i++){
						rowVector.addElement(rs.getString(i));
					}
					dataVector.addElement(rowVector);
				}
				((DefaultTableModel) this.jtable.getModel()).setDataVector(dataVector,reiheVector);
				this.jtable.getColumnModel().getColumn(0).setPreferredWidth(100);
				this.jtable.getColumn(3).setMinWidth(0);	
				this.jtable.getColumn(3).setMaxWidth(0);
			}catch(SQLException ev){
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
	*/
	private void setDetails(String Event,String PatNummer){
			this.sEventDetails[0] = Event;
			this.sEventDetails[1] = PatNummer;
	}	
	public String[] getDetails(String Event,String PatNummer){
			return this.sEventDetails;
	}
	public void fensterSchliessen(){
		this.setVisible(false);
		this.dispose();
	}

}  //  @jve:decl-index=0:visual-constraint="387,36"
