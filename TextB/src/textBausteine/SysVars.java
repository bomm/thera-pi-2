package textBausteine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

public class SysVars extends JDialog implements MouseListener, KeyListener{
	
	List<String> sysVars1;
	List<String> sysTexte;
	JList lb; 
	DefaultListModel model = new DefaultListModel();
	testbauoberflaeche eltern;
	public SysVars(testbauoberflaeche xeltern){
		super();
		this.setContentPane(getPane());
		this.setAlwaysOnTop(true);
		//this.getRootPane().add(getPane(),BorderLayout.CENTER);
		setPreferredSize(new Dimension(200,420));
		setTitle("System-Variable");
		eltern = xeltern;
		setModal(false);
		validate();
	}
	private JScrollPane getPane(){
		
		lb = new JList(model);
		lb.addMouseListener(this);
		lb.addKeyListener(this);
		JScrollPane scr = new JScrollPane();
		scr.setViewportView(lb);
		scr.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				macheSysVars1();
				macheTexte();
				for(int i = 0;i<sysTexte.size();i++){
					model.add(i,sysTexte.get(i));
				}
				lb.setSelectedIndex(0);
				return null;
			}
			
		}.execute();
		return scr;
	}
	
	private void macheTexte(){
		sysTexte = Arrays.asList(new String[] {"erstes Behandlungsdatum","letztes Behandlungsdatum",
				"Rezeptdatum","Anrede","Pat. Nachname","Pat. Vorname","Heutiges Datum",
				"Tabulator",/*"Zeilenumbruch",*/"...Der Patient / Die Patientin",
				"...der Patient / die Patientin","...Dem Patient / Der Patientin","...dem Patient / der Patientin",
				"...Den Patient / Die Patientin","...den Patient / die Patientin",
				"...des Patient / der Patientin","...ihm / ihr","...Sein / Ihr","...sein / ihr","...Seine / Ihre",
				"...seine / ihre","...Er / Sie","...er / sie"
		});

	}
	private void macheSysVars1(){
		sysVars1 = Arrays.asList(new String[] {"^ErstDatum^","^LetztDatum^","^RezDatum^","^Anrede^","^PatName^","^PatVName^","^Heute^",
				"^Tab^",/*"^CRLF^",*/"^DerPat/DiePat^","^derPat/diePat^",
				"^DemPat/DerPat^","^demPat/derPat^","^DenPat/DiePat^","^denPat/diePat^",
				"^desPat/derPat^","^ihm/ihr^","^Sein/Ihr^","^sein/ihr^","^Seine/Ihre^","^seine/ihre^",
				"^Er/Sie^","^er/sie^"
	
		});
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getClickCount()==2){
			eltern.setSysVar(sysVars1.get(lb.getSelectedIndex()));
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==10){
			eltern.setSysVar(sysVars1.get(lb.getSelectedIndex()));
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
}
