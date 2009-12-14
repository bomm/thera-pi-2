package dialoge;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXFrame;

import sqlTools.SqlInfo;
import systemTools.JRtaTextField;
import systemTools.ListenerTools;
import terminKalender.DatFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SchluesselDialog extends JDialog implements WindowListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String schluessel;
	private JRtaTextField[] tfs = {null,null,null};
	private JButton[] buts = {null,null};
	private JLabel schluessellab = null;
	private Font fon = new Font("Arial",Font.BOLD,15);
	private boolean ausgabe;
	private ActionListener acli;
	private KeyListener kli;
	JPanel jpan = null;
	public SchluesselDialog(JXFrame owner, String name,String xschluessel) {
		//super(owner, name);
		//this.setName(name);
		super();
		
		schluessel = xschluessel;
		addWindowListener(this);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		kli = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == 10){
					if(e.getSource() instanceof JButton){
						if(((JComponent)e.getSource()).getName().equals("ok")){
							doErledigt();
						}else{
							doAbbrechen();
						}
					}
				}
				if(e.getKeyCode() == 27){
					e.consume();
					doAbbrechen();
				}

			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		 acli = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(arg0.getActionCommand().equals("ok")){
						doErledigt();
					}else{
						doAbbrechen();
					}
				}
		};
		jpan = getSchluessel();
		this.setContentPane(jpan);
		testeSchluessel();

	}
	private JPanel getSchluessel(){
		tfs[0] = new JRtaTextField("GROSS",true);
		tfs[0].addKeyListener(kli);
		tfs[1] = new JRtaTextField("GROSS",true);
		tfs[1].addKeyListener(kli);
		buts[0] = new JButton("Ok");
		buts[0].addKeyListener(kli);
		buts[0].setActionCommand("ok");
		buts[0].setName("ok");
		buts[0].addActionListener(acli);

		buts[1] = new JButton("Abbrechen");
		buts[1].addKeyListener(kli);
		buts[1].setActionCommand("abbrechen");
		buts[1].setName("abbrechen");
		buts[1].addActionListener(acli);

		
		FormLayout lay = new FormLayout("0dlu,right:max(40dlu;p),10dlu,120dlu,50dlu","10dlu,p,10dlu,p,10dlu,p,10dlu,p,10dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		pb.addLabel("Schl�ssel",cc.xy(2, 2));
		schluessellab = new JLabel(schluessel) ;
		schluessellab.setFont(fon);
		pb.add(schluessellab,cc.xy(4, 2));
		pb.addLabel("Name",cc.xy(2, 4));
		pb.add(tfs[0],cc.xy(4, 4));
		pb.addLabel("Pfand",cc.xy(2, 6));
		pb.add(tfs[1],cc.xy(4, 6));
		FormLayout lay2 = new FormLayout("fill:0:grow(0.5),60dlu,20dlu,60dlu,fill:0:grow(0.5)","5dlu,p,5dlu");
		PanelBuilder pb2 = new PanelBuilder(lay2);
		CellConstraints cc2 = new CellConstraints();
		pb2.add(buts[0],cc2.xy(2,2));
		pb2.add(buts[1],cc2.xy(4,2));
		pb2.getPanel().validate();
		pb.add(pb2.getPanel(),cc.xyw(1, 8, 5));
		pb.getPanel().validate();
		return pb.getPanel();
	}
	@SuppressWarnings("unchecked")
	private void testeSchluessel(){
		Vector<String> vec = SqlInfo.holeSatz("anwesend", " * " ,"schrank ='"+schluessel+"'" , Arrays.asList(new String[] {}));
		if(vec.size()<=0){
			ausgabe = true;
		}else{
			ausgabe = false;
			tfs[0].setText(vec.get(1));
			tfs[1].setText(vec.get(10));
		}
		vec.clear();
		vec = null;
	}
	
	private void doErledigt(){
		if(ausgabe){
			if(tfs[0].getText().trim().equals("") || tfs[1].getText().trim().equals("")){
				JOptionPane.showMessageDialog(null,"Name des Ausleihers und Pfand-Betrag darf nicht leer sein");
				tfs[0].requestFocus();
				return;
			}
			//NNAME,DATUM,KOMMEN,GEHEN,SCHRANK,PFAND
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					DateFormat clockFormat = new SimpleDateFormat("HH:mm:ss");
					String cmd = "insert into anwesend set nname='"+tfs[0].getText()+"', datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', "+
					"kommen='"+(String)clockFormat.format(new Date())+"', schrank='"+schluessel+"', pfand='"+tfs[1].getText()+"'";
					//System.out.println(cmd);
					clockFormat = null;
					SqlInfo.sqlAusfuehren(cmd);
					dispose();
					return null;
				}
			}.execute();
		}else{
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					String cmd = "delete from anwesend where schrank='"+schluessel+"' LIMIT 1";
					SqlInfo.sqlAusfuehren(cmd);
					dispose();
					return null;
				}
			}.execute();
		}

	}
	private void doAbbrechen(){
		this.dispose();
	}
	public void windowClosed(WindowEvent arg0) {
		System.out.println("In windowClosed von RehaSmartDialog");
		ListenerTools.removeListeners(tfs[0]);
		ListenerTools.removeListeners(tfs[1]);
		ListenerTools.removeListeners(buts[0]);
		ListenerTools.removeListeners(buts[1]);
		jpan = null;
		ListenerTools.removeListeners(this);
		removeWindowListener(this);
		acli = null;
		kli = null;
		fon = null;
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		System.out.println("In windowClosing.....");
		this.dispose();
		
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
	
}
