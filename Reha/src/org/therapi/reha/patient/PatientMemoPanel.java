package org.therapi.reha.patient;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.StringTools;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class PatientMemoPanel extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1894163619378832811L;
	PatientHauptPanel patientHauptPanel = null;
	MouseListener ml = null;
	public PatientMemoPanel(PatientHauptPanel patHauptPanel){
		super();
		setLayout(new BorderLayout());
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
		add(getMemoPanel(),BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				activateMouseListener();
			}
		});
	}
	public void activateMouseListener(){
		ml = new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3 && ( (patientHauptPanel.pmemo[0].isEditable()) || (patientHauptPanel.pmemo[1].isEditable())) ){
					new Floskeln( (patientHauptPanel.pmemo[0].isEditable() ? 0 : 1), e );
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
		};
		patientHauptPanel.pmemo[0].addMouseListener(ml);
		patientHauptPanel.pmemo[1].addMouseListener(ml);
	}
	public void fireAufraeumen(){
		for(int i = 0; i < patientHauptPanel.memobut.length;i++){
			patientHauptPanel.memobut[i].removeActionListener(patientHauptPanel.memoAction);
		}
		patientHauptPanel.pmemo[0].removeMouseListener(ml);
		patientHauptPanel.pmemo[1].removeMouseListener(ml);
		ml = null;
		patientHauptPanel.memoAction = null;
	}
	
	public void doMemoAction(ActionEvent arg0){
		if(patientHauptPanel.autoPatid == -1){
			return;
		}
		String sc = arg0.getActionCommand();
		if(sc.equals("kedit")){
			patientHauptPanel.inMemo = 0;
			patientHauptPanel.memobut[0].setEnabled(false);
			patientHauptPanel.memobut[1].setEnabled(true);
			patientHauptPanel.memobut[2].setEnabled(true);
			patientHauptPanel.pmemo[0].setForeground(Color.RED);
			patientHauptPanel.pmemo[0].setEditable(true);
			patientHauptPanel.pmemo[0].setCaretPosition(0);
			patientHauptPanel.memobut[3].setEnabled(false);
			return;
		}
		if(sc.equals("kedit2")){
			patientHauptPanel.inMemo = 1;
			patientHauptPanel.memobut[3].setEnabled(false);
			patientHauptPanel.memobut[4].setEnabled(true);
			patientHauptPanel.memobut[5].setEnabled(true);
			patientHauptPanel.pmemo[1].setForeground(Color.RED);
			patientHauptPanel.pmemo[1].setEditable(true);
			patientHauptPanel.pmemo[1].setCaretPosition(0);
			patientHauptPanel.memobut[0].setEnabled(false);
			return;
		}
		if(sc.equals("ksave")){
			patientHauptPanel.memobut[0].setEnabled(true);
			patientHauptPanel.memobut[1].setEnabled(false);
			patientHauptPanel.memobut[2].setEnabled(false);
			patientHauptPanel.pmemo[0].setForeground(Color.BLUE);
			patientHauptPanel.pmemo[0].setEditable(false);
			patientHauptPanel.memobut[3].setEnabled(true);
			String cmd = "update pat5 set anamnese='"+StringTools.Escaped(Reha.thisClass.patpanel.pmemo[0].getText())+"' where id='"+
			Reha.thisClass.patpanel.autoPatid+"'";
			new ExUndHop().setzeStatement(cmd);
			patientHauptPanel.inMemo = -1;
			return;
		}
		if(sc.equals("ksave2")){
			patientHauptPanel.memobut[3].setEnabled(true);
			patientHauptPanel.memobut[4].setEnabled(false);
			patientHauptPanel.memobut[5].setEnabled(false);
			patientHauptPanel.pmemo[1].setForeground(Color.BLUE);
			patientHauptPanel.pmemo[1].setEditable(false);
			patientHauptPanel.memobut[0].setEnabled(true);
			String cmd = "update pat5 set pat_text='"+StringTools.Escaped(Reha.thisClass.patpanel.pmemo[1].getText())+"' where id='"+
			Reha.thisClass.patpanel.autoPatid+"'";
			new ExUndHop().setzeStatement(cmd);
			patientHauptPanel.inMemo = -1;
			return;
		}
		if(sc.equals("kbreak")){
			patientHauptPanel.memobut[0].setEnabled(true);
			patientHauptPanel.memobut[1].setEnabled(false);
			patientHauptPanel.memobut[2].setEnabled(false);
			patientHauptPanel.pmemo[0].setForeground(Color.BLUE);
			patientHauptPanel.pmemo[0].setEditable(false);
			patientHauptPanel.memobut[3].setEnabled(true);
			patientHauptPanel.pmemo[0].setText((String) SqlInfo.holeSatz("pat5", "anamnese", "id='"+patientHauptPanel.autoPatid+"'", Arrays.asList(new String[] {})).get(0) );
			patientHauptPanel.inMemo = -1;
			return;
		}
		if(sc.equals("kbreak2")){
			patientHauptPanel.memobut[3].setEnabled(true);
			patientHauptPanel.memobut[4].setEnabled(false);
			patientHauptPanel.memobut[5].setEnabled(false);
			patientHauptPanel.pmemo[1].setForeground(Color.BLUE);
			patientHauptPanel.pmemo[1].setEditable(false);
			patientHauptPanel.memobut[0].setEnabled(true);		
			patientHauptPanel.pmemo[1].setText((String) SqlInfo.holeSatz("pat5", "pat_text", "id='"+patientHauptPanel.autoPatid+"'", Arrays.asList(new String[] {})).get(0) );
			patientHauptPanel.inMemo = -1;
			return;
		}
		
		
	}
	private JXPanel getMemoPanel(){
		JXPanel mittelinksunten = new JXPanel(new BorderLayout());
		mittelinksunten.setOpaque(false);
		
//		mittelinksunten.addFocusListener(eltern.getFocusListener());
	    

		mittelinksunten.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		JToolBar jtoolb = new JToolBar();
		jtoolb.setOpaque(false);
		jtoolb.setBorder(null);
		jtoolb.setBorderPainted(false);
		jtoolb.setRollover(true);
		patientHauptPanel.memobut[0] = new JButton();
		patientHauptPanel.memobut[0].setIcon(SystemConfig.hmSysIcons.get("edit"));
		patientHauptPanel.memobut[0].setToolTipText("Langtext editieren");		
		patientHauptPanel.memobut[0].setActionCommand("kedit");
		patientHauptPanel.memobut[0].addActionListener(patientHauptPanel.memoAction);
		jtoolb.add(patientHauptPanel.memobut[0]);
		patientHauptPanel.memobut[1] = new JButton();
		patientHauptPanel.memobut[1].setIcon(SystemConfig.hmSysIcons.get("save"));		
		patientHauptPanel.memobut[1].setToolTipText("Langtext speichern");		
		patientHauptPanel.memobut[1].setActionCommand("ksave");
		patientHauptPanel.memobut[1].addActionListener(patientHauptPanel.memoAction);			
		patientHauptPanel.memobut[1].setEnabled(false);
		jtoolb.add(patientHauptPanel.memobut[1]);
		jtoolb.addSeparator(new Dimension(40,0));
		patientHauptPanel.memobut[2] = new JButton();
		patientHauptPanel.memobut[2].setIcon(SystemConfig.hmSysIcons.get("stop"));
		patientHauptPanel.memobut[2].setToolTipText("Langtext bearbeiten abbrechen");		
		patientHauptPanel.memobut[2].setActionCommand("kbreak");
		patientHauptPanel.memobut[2].addActionListener(patientHauptPanel.memoAction);
		patientHauptPanel.memobut[2].setEnabled(false);
		jtoolb.add(patientHauptPanel.memobut[2]);
		
		patientHauptPanel.memotab = new JTabbedPane();
		patientHauptPanel.memotab.setUI(new WindowsTabbedPaneUI());
		patientHauptPanel.memotab.setOpaque(false);
		patientHauptPanel.memotab.setBorder(null);
		

		patientHauptPanel.pmemo[0] = new JTextArea();
		patientHauptPanel.pmemo[0].setFont(new Font("Courier",Font.PLAIN,11));
		patientHauptPanel.pmemo[0].setLineWrap(true);
		patientHauptPanel.pmemo[0].setName("notitzen");
		patientHauptPanel.pmemo[0].setWrapStyleWord(true);
		patientHauptPanel.pmemo[0].setEditable(false);
		patientHauptPanel.pmemo[0].setBackground(Color.WHITE);
		patientHauptPanel.pmemo[0].setForeground(Color.BLUE);
		JScrollPane span = JCompTools.getTransparentScrollPane(patientHauptPanel.pmemo[0]);
		//span.setBackground(Color.WHITE);
		span.validate();
		JXPanel jpan = JCompTools.getEmptyJXPanel(new BorderLayout());
		jpan.setOpaque(true);
		JXPanel jpan2 = JCompTools.getEmptyJXPanel(new BorderLayout());
		/*****************/

		jpan2.setBackgroundPainter(Reha.thisClass.compoundPainter.get("FliessText"));
	     jpan2.add(jtoolb);
		jpan.add(jpan2,BorderLayout.NORTH);
		jpan.add(span,BorderLayout.CENTER);
		patientHauptPanel.memotab.addTab("Notizen", jpan);
		/******************************************/
		JToolBar jtoolb2 = new JToolBar();
		jtoolb2.setOpaque(false);
		jtoolb2.setBorder(null);
		jtoolb2.setBorderPainted(false);
		jtoolb2.setRollover(true);
		patientHauptPanel.memobut[3] = new JButton();
		patientHauptPanel.memobut[3].setIcon(SystemConfig.hmSysIcons.get("edit"));
		patientHauptPanel.memobut[3].setToolTipText("Langtext editieren");		
		patientHauptPanel.memobut[3].setActionCommand("kedit2");
		patientHauptPanel.memobut[3].addActionListener(patientHauptPanel.memoAction);
		jtoolb2.add(patientHauptPanel.memobut[3]);
		patientHauptPanel.memobut[4] = new JButton();
		patientHauptPanel.memobut[4].setIcon(SystemConfig.hmSysIcons.get("save"));

		patientHauptPanel.memobut[4].setToolTipText("Langtext speichern");		
		patientHauptPanel.memobut[4].setActionCommand("ksave2");
		patientHauptPanel.memobut[4].addActionListener(patientHauptPanel.memoAction);
		patientHauptPanel.memobut[4].setEnabled(false);
		jtoolb2.add(patientHauptPanel.memobut[4]);
		jtoolb2.addSeparator(new Dimension(40,0));
		patientHauptPanel.memobut[5] = new JButton();
		patientHauptPanel.memobut[5].setIcon(SystemConfig.hmSysIcons.get("stop"));
		patientHauptPanel.memobut[5].setToolTipText("Langtext bearbeiten abbrechen");		
		patientHauptPanel.memobut[5].setActionCommand("kbreak2");
		patientHauptPanel.memobut[5].addActionListener(patientHauptPanel.memoAction);
		patientHauptPanel.memobut[5].setEnabled(false);
		jtoolb2.add(patientHauptPanel.memobut[5]);
		
		
		patientHauptPanel.pmemo[1] = new JTextArea();
		patientHauptPanel.pmemo[1].setFont(new Font("Courier",Font.PLAIN,11));
		patientHauptPanel.pmemo[1].setLineWrap(true);
		patientHauptPanel.pmemo[1].setName("notitzen");
		patientHauptPanel.pmemo[1].setWrapStyleWord(true);
		patientHauptPanel.pmemo[1].setEditable(false);
		patientHauptPanel.pmemo[1].setBackground(Color.WHITE);
		patientHauptPanel.pmemo[1].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(patientHauptPanel.pmemo[1]);
		span.setBackground(Color.WHITE);
		span.validate();
		jpan = JCompTools.getEmptyJXPanel(new BorderLayout());
		jpan.setOpaque(true);
		jpan2 = JCompTools.getEmptyJXPanel(new BorderLayout());
		jpan2.setBackgroundPainter(Reha.thisClass.compoundPainter.get("FliessText"));
	    jpan2.add(jtoolb2);
		jpan.add(jpan2,BorderLayout.NORTH);
		jpan.add(span,BorderLayout.CENTER);
		patientHauptPanel.memotab.addTab("Fehldaten", jpan);
		

		
		mittelinksunten.add(patientHauptPanel.memotab,BorderLayout.CENTER);
		mittelinksunten.revalidate();

		return mittelinksunten;
		
	}
	/********************************************************************************/
	class Floskeln{
		public Floskeln(int memo, MouseEvent e){
			
		}
	}
	/********************************************************************************/	

}
