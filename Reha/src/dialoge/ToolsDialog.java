package dialoge;

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import systemEinstellungen.SystemConfig;

import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class ToolsDialog extends JXDialog implements FocusListener, ActionListener, MouseListener, WindowListener, KeyListener,RehaTPEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5894214872643895071L;
	private JXTitledPanel jtp = null;
	private MouseAdapter mymouse = null;
	private PinPanel pinPanel = null;
	private JXPanel content = null;
	private JList jList = null;
	private RehaTPEventClass rtp = null;
	
	private JButton abfeuern = null;
	private MouseListener toolsMl = null;

	
	public ToolsDialog(JXFrame owner,String titel,JList list){
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		this.setUndecorated(true);
		this.setName("ToolsDlg");	
		this.jList = list;
		this.jList.addKeyListener(this);
		//this.jList.addMouseListener(this);
	
		this.jtp = new JXTitledPanel();
		this.jtp.setName("ToolsDlg");
		this.mymouse = new DragWin(this);
		this.jtp.addMouseListener(mymouse);
		this.jtp.addMouseMotionListener(mymouse);
		this.jtp.addMouseListener(this);
		this.jtp.setContentContainer(getContent(list));
		this.jtp.setTitleForeground(Color.WHITE);
		this.jtp.setTitle(titel);
		this.pinPanel = new PinPanel();
		this.pinPanel.getGruen().setVisible(false);
		this.pinPanel.setName("ToolsDlg");
		this.jtp.setRightDecoration(this.pinPanel);
		this.setContentPane(jtp);
		//this.setModal(true);
		this.setResizable(false);
		this.rtp = new RehaTPEventClass();
		this.rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				jList.requestFocus();
				jList.setSelectedIndex(0);
			}
		});
	}
	
	private JXPanel getContent(JList list){
		content = new JXPanel(new BorderLayout());
		content.add(new JScrollPane(list), BorderLayout.CENTER);
		if((Boolean)SystemConfig.hmOtherDefaults.get("ToolsDlgShowButton")){
			abfeuern = new JButton("ausführen....");
			abfeuern.setActionCommand("abfeuern");
			abfeuern.addActionListener(this);
			content.add(abfeuern,BorderLayout.SOUTH);
		}
		return content;
	}
	
	public void activateListener(){
		toolsMl = new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()== (Integer) SystemConfig.hmOtherDefaults.get("ToolsDlgClickCount")){
					if( ((JComponent)arg0.getSource()) instanceof JList){
						Reha.toolsDlgRueckgabe = Integer.valueOf(jList.getSelectedIndex());
						FensterSchliessen("dieses");
					}
				}
				
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		};
		this.jList.addMouseListener(toolsMl);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				jList.requestFocus();		
			}
		});
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
		if(arg0.getActionCommand().equals("abfeuern")){
			Reha.toolsDlgRueckgabe = Integer.valueOf(jList.getSelectedIndex());
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
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getClickCount()== (Integer) SystemConfig.hmOtherDefaults.get("ToolsDlgClickCount")){
			if( ((JComponent)arg0.getSource()) instanceof JList){
				Reha.toolsDlgRueckgabe = Integer.valueOf(jList.getSelectedIndex());
				FensterSchliessen("dieses");
			}
		}
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
		/*
		if(arg0.getClickCount()== (Integer) SystemConfig.hmOtherDefaults.get("ToolsDlgClickCount")){
			if( ((JComponent)arg0.getSource()) instanceof JList){
				this.rueckgabe = jList.getSelectedIndex();
				FensterSchliessen("dieses");
			}
		}
		*/
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}	

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==10){
			if( ((JComponent)arg0.getSource()) instanceof JList){
				Reha.toolsDlgRueckgabe = Integer.valueOf(jList.getSelectedIndex());
				FensterSchliessen("dieses");
			}
		}
		if(arg0.getKeyCode()==27){
			Reha.toolsDlgRueckgabe = Integer.valueOf(-1);
			FensterSchliessen("dieses");			
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
		this.jList.removeKeyListener(this);
		this.jList.removeMouseListener(this);
		if(toolsMl != null){
			this.jList.removeMouseListener(toolsMl);
			toolsMl = null;
		}
		if(abfeuern != null){
			this.abfeuern.removeActionListener(this);			
		}
		this.mymouse = null; 
		if(this.rtp != null){
			this.rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.rtp=null;			
		}
		setVisible(false);
		this.jList = null;
		this.dispose();
	}



}
