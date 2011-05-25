package dialoge;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.thera_pi.nebraska.gui.utils.ButtonTools;

import systemEinstellungen.SysUtilKuerzel;
import systemTools.JRtaCheckBox;
import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class KuerzelNeu extends JXDialog implements  WindowListener, KeyListener,RehaTPEventListener{

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private JXTitledPanel jtp = null;
	private MouseAdapter mymouse = null;
	private PinPanel pinPanel = null;
	private JXPanel content = null;
	private RehaTPEventClass rtp = null;
	private JRtaTextField[] tfs = {null,null};
	private JRtaCheckBox[] jbox = {null,null};
	private JButton[] buts = {null,null};
	boolean neu;
	JLabel labKurz = null;
	JLabel labLang = null;
	ActionListener al = null;
	SysUtilKuerzel eltern = null;
	
	public KuerzelNeu(JXFrame owner,String titel,boolean xneu,SysUtilKuerzel xeltern){
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		installListener();
		this.setUndecorated(true);
		this.setName("KuerzelDlg");	
		this.eltern = xeltern;
		this.neu = xneu;
		this.jtp = new JXTitledPanel();
		this.jtp.setName("KuerzelDlg");
		this.mymouse = new DragWin(this);
		this.jtp.addMouseListener(mymouse);
		this.jtp.addMouseMotionListener(mymouse);
		this.jtp.addKeyListener(this);
		this.jtp.setContentContainer(getContent());
		this.jtp.setTitleForeground(Color.WHITE);
		this.jtp.setTitle(titel);
		this.pinPanel = new PinPanel();
		this.pinPanel.getGruen().setVisible(false);
		this.pinPanel.setName("KuerzelDlg");
		this.jtp.setRightDecoration(this.pinPanel);
		this.setContentPane(jtp);
		this.setModal(true);
		this.setResizable(false);
		this.rtp = new RehaTPEventClass();
		this.rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(!neu){
					Object[] kdaten = eltern.getKuerzelDaten();
					tfs[0].setText(String.valueOf(kdaten[0]));
					tfs[1].setText(String.valueOf(kdaten[1]));
					jbox[0].setSelected((Boolean)kdaten[2]);
					jbox[1].setSelected((Boolean)kdaten[3]);
				}
				setzeFocus((JComponent)tfs[0]);
			}
		});
	}
	private void setzeFocus(JComponent comp){
		final JComponent xcomp = comp;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				xcomp.requestFocus();
			}
		});
	}
	private void installListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("speichern")){
					doSpeichern();
					return;
				}
				if(cmd.equals("abbrechen")){
					FensterSchliessen("dieses");
					return;
				}
				
			}
			
		};
	}
	private JXPanel getContent(){
		String xwerte = "fill:0:grow(0.5),125dlu,5dlu,120dlu,fill:0:grow(0.5)";
		//                    1           2  3   4  5  6   7   8     9           10
		String ywerte = "fill:0:grow(0.5),p,5dlu,p,5dlu,p,5dlu,p,fill:0:grow(0.5),p";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setBackground(Color.WHITE);
		content.setLayout(lay);
		content.addKeyListener(this);
		
		labKurz = new JLabel("Kürzel (max. 10 Zeichen)");
		content.add(labKurz,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JRtaTextField("nix",true);
		tfs[0].setName("kuerzel");
		tfs[0].addKeyListener(this);
		content.add(tfs[0],cc.xy(4,2));
		
		labLang = new JLabel("Langtext (max. 35 Zeichen)");
		content.add(labLang,cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[1] = new JRtaTextField("nix",true);
		tfs[1].setName("langtext");
		tfs[1].addKeyListener(this);
		content.add(tfs[1],cc.xy(4,4));

		labLang = new JLabel("vorrangiges Heilmittel");
		content.add(labLang,cc.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jbox[0] = new JRtaCheckBox();
		jbox[0].setOpaque(false);
		jbox[0].setName("vorrangig");
		jbox[0].addKeyListener(this);
		content.add(jbox[0],cc.xy(4,6));

		labLang = new JLabel("kann allein verordnet werden (nur ergänz. HM)");
		content.add(labLang,cc.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jbox[1] = new JRtaCheckBox();
		jbox[1].setOpaque(false);
		jbox[1].setName("separat");
		jbox[1].addKeyListener(this);
		content.add(jbox[1],cc.xy(4,8));


		FormLayout lay2 = new FormLayout("fill:0:grow(0.50),60dlu,20dlu,60dlu,fill:0:grow(0.50)",
				"10dlu,p,10dlu");
		CellConstraints cc2 = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay2);
		buts[0] = ButtonTools.macheBut("speichern", "speichern",al );
		jpan.add(buts[0],cc2.xy(2, 2));
		
		buts[1] = ButtonTools.macheBut("abbrechen", "abbrechen",al );
		jpan.add(buts[1],cc2.xy(4, 2));
		
		content.add(jpan,cc.xyw(1,10,5));
		
		content.validate();
		return content;
	}
	private void doSpeichern(){
		if(tfs[0].getText().trim().equals("") || tfs[1].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Kürzel und Langtext dürfen nicht leer sein");
			return;
		}
		if(!neu){
			eltern.updateKuerzel(tfs[0].getText(),tfs[1].getText(),jbox[0].isSelected(),jbox[1].isSelected());
			FensterSchliessen("dieses");
		}else{
			eltern.insertKuerzel(tfs[0].getText(),tfs[1].getText(),jbox[0].isSelected(),jbox[1].isSelected());
			FensterSchliessen("dieses");
		}
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==10){
		}
		if(arg0.getKeyCode()==27){
			//this.rueckgabe = -1;
			FensterSchliessen("dieses");
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if( ((JComponent)arg0.getSource()) instanceof JRtaTextField){
			if ( ((JComponent)arg0.getSource()).getName().equals("kuerzel")){
				if(tfs[0].getText().length() > 10){
					labKurz.setForeground(Color.RED);
				}else{
					labKurz.setForeground(Color.BLACK);
				}
			}else if ( ((JComponent)arg0.getSource()).getName().equals("langtext")){
				if(tfs[1].getText().length() > 35){
					labLang.setForeground(Color.RED);
				}else{
					labLang.setForeground(Color.BLACK);
				}
			}
		}
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
		this.mymouse = null; 
		if(this.rtp != null){
			this.rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.rtp=null;			
		}
		setVisible(false);
		this.dispose();
	}

}
