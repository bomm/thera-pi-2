package urlaubBeteiligung;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingworker.SwingWorker;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import rehaInternalFrame.JBeteiligungInternal;
import systemTools.ButtonTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.JRtaCheckBox;
import utils.DatFunk;
import terminKalender.ParameterLaden;

public class Beteiligung  extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5302438379722827660L;
	
	JBeteiligungInternal internal=null;
	JRtaComboBox jcmb = null;
	JLabel lab = null;
	JRtaTextField[] tfs = {null,null};
	JRtaTextField[] tfbeteil = {null,null};
	JRtaCheckBox chbbeteil = null;
	JXPanel content = null;
	JButton[] buts = {null};
	ActionListener al = null;
	KeyListener kl = null;
	boolean initok = false;
	String aktBehandlerNummer = "";

	int prozentBehandlung=0;
	int prozentHb=0;
	
	Vector<Vector<String>> veckolls = new Vector<Vector<String>>();  
	
	
	public Beteiligung(JBeteiligungInternal bti){
		super();
		this.internal = bti;
		makeListeners();
		add(getContent(),BorderLayout.CENTER);
		initok = true;
	}

	private JXPanel getContent(){       //   1            2     3     4    5     6     7     8   9    
		FormLayout lay = new FormLayout("fill:0:grow(0.5),5dlu,70dlu,3dlu,60dlu,3dlu,70dlu,3dlu,60dlu,"+
		//10   11    12     13
		"3dlu,70dlu,3dlu, 60dlu,25dlu,fill:0:grow(0.5)",
		// 1   2  3   4  5   6
		"10dlu,p,5dlu,p,5dlu,p");
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		lab = new JLabel("Mitarbeiter");
		content.add(lab,cc.xy(3,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jcmb = new JRtaComboBox();
		jcmb.setDataVectorVector(doKollegen(), 0, 1);
		jcmb.setActionCommand("kollegen");
		jcmb.addActionListener(al);
		content.add(jcmb,cc.xy(5,2));
		lab = new JLabel("Bet. an Beh.(%)");
		content.add(lab,cc.xy(7,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfbeteil[0]= new JRtaTextField("ZAHLEN",true);
		tfbeteil[0].setText("0");
		content.add(tfbeteil[0],cc.xy(9,2));
		lab = new JLabel("Bet. an HB(%)");
		content.add(lab,cc.xy(11,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfbeteil[1]= new JRtaTextField("ZAHLEN",true);
		tfbeteil[1].setText("0");
		content.add(tfbeteil[1],cc.xy(13,2));
		
		lab = new JLabel("von...");
		content.add(lab,cc.xy(3,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0]= new JRtaTextField("DATUM",true);
		tfs[0].setText(DatFunk.sHeute());
		content.add(tfs[0],cc.xy(5,4));

		lab = new JLabel("bis...");
		content.add(lab,cc.xy(7,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[1]= new JRtaTextField("DATUM",true);
		tfs[1].setText(DatFunk.sHeute());
		content.add(tfs[1],cc.xy(9,4));
		
		content.add((buts[0]=ButtonTools.macheButton("ermitteln", "ermitteln", al)),cc.xy(13,4));
		buts[0].addKeyListener(kl);
		content.validate();
		return content;
	}
	private Vector<Vector<String>> doKollegen(){
		int lang = ParameterLaden.vKollegen.size();
		veckolls.clear();
		Vector<String> vecdummy = new Vector<String>(); 
		for(int i = 0;i<lang;i++){
			vecdummy.clear();
			System.out.println(ParameterLaden.vKollegen.get(i));
			vecdummy.add((String)ParameterLaden.vKollegen.get(i).get(0));
			vecdummy.add((String)ParameterLaden.vKollegen.get(i).get(3));
			veckolls.add((Vector<String>)vecdummy.clone());
		}
		Comparator<Vector<String>> comparator = new Comparator<Vector<String>>() {
			@Override
			public int compare(Vector<String> o1, Vector<String> o2) {
				// TODO Auto-generated method stub
				String s1 = (String)o1.get(0);
				String s2 = (String)o2.get(0);
				return s1.compareTo(s2);
			}
		};
		Collections.sort(veckolls,comparator);
		return veckolls;
	}
	private void doErmitteln(){
		if(jcmb.getValue().toString().trim().equals("00")){
			JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Behandler aus");
			return;
		}
		if(tfbeteil[0].getText().trim().equals("0") && tfbeteil[1].getText().trim().equals("0")){
			JOptionPane.showMessageDialog(null, "Bei jeweils 0% Beteiligung gibt's eigentlich nichts was zu ermitteln wäre\nDeppen gibt's.....");
			return;
		}
		try{
			prozentBehandlung = Integer.parseInt(tfbeteil[0].getText().trim());
			prozentHb = Integer.parseInt(tfbeteil[1].getText().trim());
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Ein oder beide Prozent-Werte sind unzulässig");
			return;
		}
		
	}
	
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("ermitteln")){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							doErmitteln();
							return null;
						}
					}.execute();
				}
				if(cmd.equals("kollegen") && initok){
					aktBehandlerNummer = jcmb.getValue().toString();
					System.out.println("Aktueller Behandler = "+aktBehandlerNummer);
				}
			}
		};
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};
	}
	public void doAufraeumen(){
		
	}

}
