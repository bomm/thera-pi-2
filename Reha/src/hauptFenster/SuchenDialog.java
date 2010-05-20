package hauptFenster;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import org.jdesktop.swingworker.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;

import dialoge.PinPanel;

import systemEinstellungen.SystemConfig;
import systemTools.ListenerTools;
import systemTools.StringTools;
import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaTPEvent;
import events.RehaTPEventClass;
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
	
	//private SuchenDialog thisClass = null;
	private int clickX;
	private int clickY;
	private int weiteX;
	private int hoeheY;
	//private int locationX;
	//private int locationY;
	private Cursor cmove = new Cursor(Cursor.MOVE_CURSOR);  //  @jve:decl-index=0:
	private Cursor cnsize = new Cursor(Cursor.N_RESIZE_CURSOR);  //  @jve:decl-index=0:
	private Cursor cnwsize = new Cursor(Cursor.NW_RESIZE_CURSOR);  //  @jve:decl-index=0:
	private Cursor cnesize = new Cursor(Cursor.NE_RESIZE_CURSOR);  //  @jve:decl-index=0:
	private Cursor cswsize = new Cursor(Cursor.SW_RESIZE_CURSOR);  //  @jve:decl-index=0:
	private Cursor cwsize = new Cursor(Cursor.W_RESIZE_CURSOR);  //  @jve:decl-index=0:
	private Cursor csesize = new Cursor(Cursor.SE_RESIZE_CURSOR);  //  @jve:decl-index=0:
	private Cursor cssize = new Cursor(Cursor.S_RESIZE_CURSOR);  //  @jve:decl-index=0:
	private Cursor cesize = new Cursor(Cursor.E_RESIZE_CURSOR);  //  @jve:decl-index=0:	
	private Cursor cdefault = new Cursor(Cursor.DEFAULT_CURSOR);  //  @jve:decl-index=0:

	private boolean insize;
	private int[] orgbounds = {0,0};
	private int sizeart;
	private RehaEventClass rEvent;  //  @jve:decl-index=0:
	private String sEvent = "";  //  @jve:decl-index=0:
	private String[] sEventDetails ={null,null};
	
	private JComponent focusBack = null;
	private String fname = "";  //  @jve:decl-index=0:
	public DefaultTableModel tblDataModel;
	
	private RehaTPEventClass rtp = null;
	private PinPanel pinPanel = null;
	/**
	 * @param 
	 */
	public SuchenDialog(JXFrame owner,JComponent focusBack,String fname) {
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		this.focusBack = (focusBack == null ? null : focusBack);
		this.fname = (String) (fname.equals("") ? "" : fname);
		initialize();
		jTextField.setText(fname);
		new suchePatient().init(tblDataModel);
		//rtp = new RehaTPEventClass();
		//rtp.addRehaTPEventListener((RehaTPEventListener) this);
		//suchePatient();
		
	}
	public void	setzeReihe(Vector vec){
		tblDataModel.addRow(vec);
		jtable.validate();
		
	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {

		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					System.out.println("****************GutachtenWahl -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			System.out.println("In PatNeuanlage" +evt);
		}
	}	
	

	public void suchDasDing(String suchkrit){
		jTextField.setText(suchkrit);
		jXTitledPanel.setTitle("Suche Patient..."+suchkrit);
		System.out.println("**************Test*****************");
		System.out.println("Suchkriterium = "+suchkrit);
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
		//thisClass = this;
		SuchenDialog.this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.out.println("Weshalb windowClosing()"); // TODO Auto-generated Event stub windowClosing()
			}
		});
		this.setName("PatSuchen");
		this.setModal(false);
		this.setResizable(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		rEvent = new RehaEventClass();
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
			//jp1.setBackground(Color.WHITE);
			JLabel jlb = new JLabel("Patient suchen: ");
			jp1.add(jlb);
			
			jtext = getJTextField();
			jtext.setPreferredSize(new Dimension(100,20));
			jtext.addKeyListener(new java.awt.event.KeyAdapter() {   
				public void keyPressed(java.awt.event.KeyEvent e) {    
					System.out.println("keyPressed()"); // TODO Auto-generated Event stub keyPressed()
					if (e.getKeyCode() == 10){
						e.consume();
						new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								// TODO Auto-generated method stub
								new suchePatient().init(tblDataModel);
								//suchePatient();
								return null;
							}
							
						}.execute();
						
					}
					if (e.getKeyCode() == 27){
						e.consume();
						setVisible(false);
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
							//sucheAbfeuern();
							
						}
					}
				}   
				public void keyReleased(java.awt.event.KeyEvent e) {    
					if (e.getKeyCode() == 10){
						//suchePatient();
					}
				}
				public void keyTyped(java.awt.event.KeyEvent e) {
					System.out.println("keyTyped()"); // TODO Auto-generated Event stub keyTyped()
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
			
			//tblDataModel.setDataVector(new Vector(),reiheVector);
			//tblDataModel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tblDataModel.setColumnIdentifiers(reiheVector);
			jtable = new JXTable(tblDataModel);
			//jtable = new JXTable(new Vector(),reiheVector);
			//jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			this.jtable.getColumnModel().getColumn(0).setPreferredWidth(100);
			this.jtable.getColumn(3).setMinWidth(0);	
			this.jtable.getColumn(3).setMaxWidth(0);
			
			InputMap inputMap = jtable.getInputMap(JComponent.WHEN_FOCUSED);
			System.out.println("InputMap = "+inputMap);
			inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0));
			jtable.setInputMap(JComponent.WHEN_FOCUSED,inputMap);			
			//((DefaultTableModel) jtable.getModel()).setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			jtable.setEditable(false);
			//jtable.setModel(tblDataModel);
			jtable.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					//System.out.println("keyPressed() in JXTable"); // TODO Auto-generated Event stub keyPressed()
					if (e.getKeyCode() == 10){
						/*
						String s1 = new String("#PATSUCHEN");
						String s2 = new String((String) jtable.getValueAt(jtable.getSelectedRow(), 3));
						setDetails(s1,s2) ;
						RehaEvent rEvt = new RehaEvent(this);
						rEvt.setRehaEvent("PatSuchen");
						rEvt.setDetails(s1,s2) ;
						RehaEventClass.fireRehaEvent(rEvt);
						*/
						/*
						PatStammEvent pEvt = new PatStammEvent(SuchenDialog.this);
						pEvt.setPatStammEvent("PatSuchen");
						pEvt.setDetails(s1,s2,fname) ;
						PatStammEventClass.firePatStammEvent(pEvt);
						*/
						sucheAbfeuern();
						e.consume();	
						setVisible(false);
//						sucheHistorie((String) jtable.getValueAt( jtable.getSelectedRow(),3) );
//						e.consume();
					}
					if (e.getKeyCode() == 40 ||e.getKeyCode() == 38){
						//sucheAbfeuern();
					}
					if (e.getKeyCode() == KeyEvent.VK_F && e.isAltDown()){
						jTextField.requestFocus();
					}
					if (e.getKeyCode() == 27){
						setVisible(false);
					}

				}
			});
						
			jtable.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseEntered(java.awt.event.MouseEvent e) {
					//System.out.println("mouseEntered()"); // TODO Auto-generated Event stub mouseEntered()
					SuchenDialog.this.setCursor(cdefault);
				}
				public void mouseClicked(java.awt.event.MouseEvent e) {
					//System.out.println("mouseEntered()"); // TODO Auto-generated Event stub mouseEntered()
					if(e.getClickCount() == 2){
						sucheAbfeuern();
						e.consume();
						setVisible(false);
//						sucheHistorie((String) jtable.getValueAt( jtable.getSelectedRow(),3) );
//						e.consume();
					}	
				}
			});
			jtable.updateUI();
			jscr.setViewportView(jtable);

			//jp2.add(jtable,BorderLayout.CENTER);
			jp2.add(jscr,BorderLayout.CENTER);
			
			jContent.add(jp2, BorderLayout.CENTER);
			gridJx.add(jContent,gridBagConstraints);
			jtp.setContentContainer(gridJx);
			jtp.validate();
			jContentPane.add(jtp, BorderLayout.CENTER);
			/*
			PatStammEvent pEvt = new PatStammEvent(SuchenDialog.this);
			pEvt.setPatStammEvent("DialogAnmelden-PatSuchen");
			pEvt.setDetails("","",fname) ;
			PatStammEventClass.firePatStammEvent(pEvt);
			*/
			jtext.requestFocus();
			
		}
		return jContentPane;
	}
	public void sucheAbfeuern(){
		String s1 = new String("#PATSUCHEN");
		String s2 = (String) jtable.getValueAt(jtable.getSelectedRow(), 3);
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
			/*
			pinPanel = new PinPanel();
			pinPanel.setName("PatSuchen");
			pinPanel.getGruen().setVisible(false);
			jXTitledPanel.setRightDecoration(pinPanel);
			*/
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
//							System.out.println("nordwest");
							break;
						}
						if( (e.getX()>=  (((JComponent) e.getSource()).getWidth()-4)) && e.getY() <= 4){//nord-ost
							insize = true;
							sizeart = 2;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cnesize);
							//System.out.println("nordost");
							break;
						}
						if(e.getY() <= 4){//nord
							insize = true;
							sizeart = 3;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cnsize);
//							System.out.println("nord");
							break;
						}
						if ((e.getX() <= 4 && e.getY() >= (((JComponent) e.getSource()).getHeight()-4))){ //s�d-west
							insize = true;
							sizeart = 4;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cswsize);
//							System.out.println("s�dwest");
							break;
						}
						if ((e.getX() <= 4)){ //west
							insize = true;
							sizeart = 5;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cwsize);
//							System.out.println("west");
							break;
						}
						if ((e.getX()>=  (((JComponent) e.getSource()).getWidth()-4)) && //s�d-ost
								e.getY() >= (((JComponent) e.getSource()).getHeight()-4)){ 
							insize = true;
							sizeart = 6;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(csesize);
//							System.out.println("s�dost");
							break;
						}
						if (e.getY() >= (((JComponent) e.getSource()).getHeight()-2)){ //s�d
							insize = true;
							sizeart = 7;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cssize);
//							System.out.println("s�d");
							break;
						}
						if (e.getX() >= (((JComponent) e.getSource()).getWidth()-4)){ //ost
							insize = true;
							sizeart = 8;
							orgbounds[0]=e.getXOnScreen();
							orgbounds[1]=e.getYOnScreen();						
							setCursor(cesize);
//							System.out.println("ost");							
							break;
						}

						insize = false;
						sizeart = -1;
						setCursor(cdefault);
						//System.out.println("default");

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
					/*
					clickX = -1;
					clickY = -1;
					orgbounds[0] = -1;
					orgbounds[1] = -1;					
					insize = false;
					setCursor(cdefault);
					*/
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
						weiteX = (int) ((Component) e.getSource()).getBounds().getWidth();
						hoeheY = (int) ((Component) e.getSource()).getBounds().getHeight();						
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
			//jButton.setIcon(new ImageIcon(getClass().getResource("/icons/exit.png")));
			jButton.setText("Schliessen");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					RehaEvent rEvt = new RehaEvent(this);
					rEvt.setRehaEvent("Am Arsch lecken");
					RehaEventClass.fireRehaEvent(rEvt);
					SuchenDialog.this.setVisible(false);
					SuchenDialog.this.dispose();
					//SuchenDialog.this = null;
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
	
	private void suchePatient(){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = "";
		Vector <Vector<String[]>>dataVector = new Vector<Vector<String[]>>();
		//DefaultTableModel tblDataModel = new DefaultTableModel();
		Vector<String> reiheVector = new Vector<String>();
		reiheVector.addElement("Nachname");
		reiheVector.addElement("Nachname");
		reiheVector.addElement("Geboren");
		reiheVector.addElement("Patientennummer");
		String[] suche;
		System.out.println("DbType = "+SystemConfig.vDatenBank.get(0).get(2));
		setCursor(new Cursor(Cursor.WAIT_CURSOR));		
		if (jTextField.getText().trim().contains(" ") ){
			suche = jTextField.getText().split(" ");
			 
//			sstmt = "Select n_name,v_name,DATE_FORMAT(geboren,'%d.%m.%Y') AS geboren,pat_intern  from pat5 where n_name LIKE '"+
//			suche[0] +"%' AND v_name LIKE '"+suche[1]+"%' order by n_name,v_name";			
			if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				sstmt = "Select n_name,v_name,DATE_FORMAT(geboren,'%d.%m.%Y') AS geboren,pat_intern  from pat5 where n_name LIKE '"+
				StringTools.Escaped(suche[0].trim()) +"%' AND v_name LIKE '"+StringTools.Escaped(suche[1].trim())+"%' order by n_name,v_name";
			}else{ //ADS
				sstmt = "Select n_name,v_name,geboren,pat_intern  from pat5 where n_name LIKE UPPER('"+
				suche[0].trim() + "%') AND v_name LIKE UPPER('" + StringTools.Escaped(suche[1].trim()) +"%') order by n_name,v_name";
				System.out.println("Statement = "+StringTools.Escaped(sstmt));
			}
		}else{
			if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				sstmt = "Select n_name,v_name,DATE_FORMAT(geboren,'%d.%m.%Y') AS geboren,pat_intern from pat5 where n_name LIKE '"+
				StringTools.Escaped(jTextField.getText().trim()) +"%'  order by n_name,v_name";
			}else{ //ADS
				System.out.println("in der richtigen Suche DbType = "+SystemConfig.vDatenBank.get(0).get(2));
				sstmt = "Select n_name,v_name,geboren,pat_intern from pat5 where n_name LIKE UPPER('"+
				StringTools.Escaped(jTextField.getText().trim()) +"%') order by n_name,v_name";
				System.out.println("Statement = "+sstmt);
			}

		}
		try {
			
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = stmt.executeQuery(sstmt);
				Vector rowVector = new Vector();
				while( rs.next()){
					rowVector.clear();
					for(int i = 1; i <= 4; i++){
						rowVector.addElement(rs.getString(i) != null ? rs.getString(i) : "");
					}
					//System.out.println("lesen");
					setzeReihe((Vector)rowVector.clone());
					//tblDataModel.addRow((Vector)rowVector.clone());
					//jtable.validate();
					//dataVector.addElement(rowVector);
				}
				
				System.out.println("Gr��e ds Result Satzes = "+dataVector.size());
				/*
				((DefaultTableModel) jtable.getModel()).setDataVector(dataVector,reiheVector);
				jtable.getColumnModel().getColumn(0).setPreferredWidth(100);
				jtable.getColumn(3).setMinWidth(0);	
				jtable.getColumn(3).setMaxWidth(0);
				*/
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
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
	public void init(DefaultTableModel tblDataModel){
		this.tblDataModel = tblDataModel;
		this.tblDataModel.setRowCount(0);
		execute();
	}
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		suchePatient();
		return null;
	}
	}
	
	private void sucheHistorie(String patnr){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = "";
		Vector <Vector<String[]>>dataVector = new Vector<Vector<String[]>>();
		//DefaultTableModel tblDataModel = new DefaultTableModel();
		Vector reiheVector = new Vector();
		reiheVector.addElement("RezeptNr");
		reiheVector.addElement("Datum");
		reiheVector.addElement("PatNr.");
		reiheVector.addElement("Anzahl1");
		String[] suche;
		System.out.println(SystemConfig.vDatenBank.get(0).get(2));
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
						//System.out.println(rs.getString(i));
						rowVector.addElement(rs.getString(i));
						//rowVector.addElement((i==2 ? (rs.getString(i) != null ? datFunk.sDatInDeutsch((String) rs.getString(i)) : "  .  .  ") : rs.getString(i)) );
					//rowVector.addElement(rs.getString(i) );
					}
					dataVector.addElement(rowVector);
				}
				//tblDataModel.setDataVector(dataVector,reiheVector);
				((DefaultTableModel) this.jtable.getModel()).setDataVector(dataVector,reiheVector);
				this.jtable.getColumnModel().getColumn(0).setPreferredWidth(100);
				this.jtable.getColumn(3).setMinWidth(0);	
				this.jtable.getColumn(3).setMaxWidth(0);
				//this.jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

				
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
