package abrechnung;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.JRtaTextField;
import dialoge.DragWin;
import dialoge.PinPanel;

import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class AbrechnungPrivat extends JXDialog implements FocusListener, ActionListener, MouseListener, KeyListener,RehaTPEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1036517682792665034L;

	private JXTitledPanel jtp = null;
	private MouseAdapter mymouse = null;
	private PinPanel pinPanel = null;
	private JXPanel content = null;
	private RehaTPEventClass rtp = null;
	public int rueckgabe;
	private int preisgruppe;
	private JRtaComboBox jcmb = null;
	private JRtaRadioButton[] jrb = {null,null};
	private JLabel[] labs = {null,null,null,null,null,null};
	//private JRtaTextField[] tfs = {null,null,null,null,null};
	private JButton[] but = {null,null};
	private HashMap<String,String> hmRezgeb = null;
	DecimalFormat dcf = new DecimalFormat ( "#########0.00" );
	ButtonGroup bg = new ButtonGroup();
	String rgnrNummer;
	String[] diszis = {"KG","MA","ER","LO"};
	private Vector<Vector<String>> preisliste = null;
	boolean preisok = false;

	public AbrechnungPrivat(JXFrame owner,String titel,int rueckgabe,int preisgruppe) {
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				preisliste = RezTools.holePreisVector(Reha.thisClass.patpanel.vecaktrez.get(1).substring(0,2));
				preisok = true;
				return null;
			}
		}.execute();
		this.rueckgabe = rueckgabe;
		this.preisgruppe = preisgruppe;
		this.setUndecorated(true);
		this.setName("Privatrechnung");
		this.jtp = new JXTitledPanel();
		this.jtp.setName("Privatrechnung");
		this.mymouse = new DragWin(this);
		this.jtp.addMouseListener(mymouse);
		this.jtp.addMouseMotionListener(mymouse);
		this.jtp.setContentContainer(getContent());
		this.jtp.setTitleForeground(Color.WHITE);
		this.jtp.setTitle(titel);
		this.pinPanel = new PinPanel();
		this.pinPanel.getGruen().setVisible(false);
		this.pinPanel.setName("Privatrechnung");
		this.jtp.setRightDecoration(this.pinPanel);
		this.setContentPane(jtp);
		this.setModal(true);
		this.setResizable(false);
		this.rtp = new RehaTPEventClass();
		this.rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.add(getFields(),BorderLayout.CENTER);
		content.add(getButtons(),BorderLayout.SOUTH);
		content.addKeyListener(this);
		return content;
	}
	private JXPanel getFields(){
		JXPanel pan = new JXPanel();
		//                                1           2             3             4     5     6                7
		FormLayout lay = new FormLayout("20dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),20dlu",
				//1          2         3   4   5  6    7  8    9  10  11  12 13 14  15 16  17  18  19
				"20dlu,fill:0:grow(0.5),p, 2dlu,p,10dlu,p,3dlu,p,5dlu, p,1dlu,p,1dlu,p,1dlu,p ,1dlu,p, fill:0:grow(0.5),5dlu");
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.setOpaque(false);
		JLabel lab = new JLabel("Preisgruppe wählen:");
		pan.add(lab,cc.xy(3,3));
		jcmb = new JRtaComboBox(SystemConfig.vPreisGruppen);
		jcmb.setSelectedIndex(this.preisgruppe-1);
		pan.add(jcmb,cc.xy(3,5));
		jrb[0] = new JRtaRadioButton("Formular für Privatrechnung verwenden");
		pan.add(jrb[0],cc.xy(3,7));
		jrb[1] = new JRtaRadioButton("Formular für BGE-Rechnung verwenden");
		pan.add(jrb[1],cc.xy(3,9));
		bg.add(jrb[0]);
		bg.add(jrb[1]);
		if(preisgruppe==4){
			jrb[1].setSelected(true);
		}else{
			jrb[0].setSelected(true);
		}
		if(!Reha.thisClass.patpanel.vecaktrez.get(8).equals("0")){
			labs[0] = new JLabel(Reha.thisClass.patpanel.vecaktrez.get(3)+" * "+
					RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(8), preisliste));
			labs[0].setForeground(Color.BLUE);
			pan.add(labs[0],cc.xy(3, 11));
		}
		if(!Reha.thisClass.patpanel.vecaktrez.get(9).equals("0")){
			labs[1] = new JLabel(Reha.thisClass.patpanel.vecaktrez.get(4)+" * "+
					RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(9), preisliste));
			labs[1].setForeground(Color.BLUE);
			pan.add(labs[1],cc.xy(3, 13));
		}
		if(!Reha.thisClass.patpanel.vecaktrez.get(10).equals("0")){
			labs[2] = new JLabel(Reha.thisClass.patpanel.vecaktrez.get(5)+" * "+
					RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(10), preisliste));
			labs[2].setForeground(Color.BLUE);			
			pan.add(labs[2],cc.xy(3, 15));
		}
		if(!Reha.thisClass.patpanel.vecaktrez.get(11).equals("0")){
			labs[3] = new JLabel(Reha.thisClass.patpanel.vecaktrez.get(6)+" * "+
					RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(11), preisliste));
			labs[3].setForeground(Color.BLUE);			
			pan.add(labs[3],cc.xy(3, 17));
		}
		// Mit Hausbesuch
		if(!Reha.thisClass.patpanel.vecaktrez.get(43).equals("T")){
			//Hausbesuch voll (Einzeln) abrechnen 
			if(!Reha.thisClass.patpanel.vecaktrez.get(61).equals("T")){
				
			}
		}

		
		pan.validate();
		return pan;
	}
	private JXPanel getButtons(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),50dlu,10dlu,50dlu,fill:0:grow(0.5),5dlu",
				//1          2         3   4  5  6   7  8   9  10  11  12
				"5dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),5dlu");
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.add((but[0] = macheBut("Ok","ok")),cc.xy(3,3));
		but[0].addKeyListener(this);
		pan.add((but[1] = macheBut("abbrechen","abbrechen")),cc.xy(5,3));
		but[1].addKeyListener(this);
		return pan;
	}
	private JButton macheBut(String titel,String cmd){
		JButton but = new JButton(titel);
		but.setName(cmd);
		but.setActionCommand(cmd);
		but.addActionListener(this);
		return but;
	}	
	private void doRgRechnungPrepare(){
		FensterSchliessen("dieses");
		boolean privat = true;
		if(jrb[0].isSelected()){
			doPrivat();
		}else{
			doBGE();
		}
	}
	private void doPrivat(){
		
	}
	private void doBGE(){
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("abbrechen")){
			this.rueckgabe = -1;
			FensterSchliessen("dieses");
		}else{
			this.rueckgabe = 0;
			doRgRechnungPrepare();
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==27){
			this.rueckgabe = -1;
			FensterSchliessen("dieses");
			return;
		}
		if(arg0.getKeyCode()==10){
			if(((JComponent)arg0.getSource()) instanceof JButton){
				if(((JComponent)arg0.getSource()).getName().equals("abbrechen")){
					this.rueckgabe = -1;
					FensterSchliessen("dieses");
					return;
				}else if(((JComponent)arg0.getSource()).getName().equals("ok")){
					this.rueckgabe = 0;
					doRgRechnungPrepare();
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
		
	}

	@Override
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		FensterSchliessen("dieses");
		
	}
	public void FensterSchliessen(String welches){
		this.jtp.removeMouseListener(this.mymouse);
		this.jtp.removeMouseMotionListener(this.mymouse);
		this.content.removeKeyListener(this);
		for(int i = 0; i < 2;i++){
			but[i].removeActionListener(this);
			but[i].removeKeyListener(this);
			but[i] = null;
		}
		this.mymouse = null; 
		if(this.rtp != null){
			this.rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.rtp=null;			
		}
		this.pinPanel = null;
		setVisible(false);
		this.dispose();
	}	

}
