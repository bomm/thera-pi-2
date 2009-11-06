package entlassBerichte;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import patientenFenster.ArztAuswahl;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class BerichtArztAuswahl extends JXPanel implements ActionListener, KeyListener,FocusListener  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2476140315046322974L;
	ImageIcon hgicon;	
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;	
	JRtaCheckBox[] checks =	{null,null,null,null,null,null};
	JButton drucken = null;
	Font fon = new Font("Tahoma",Font.BOLD,11);
	EBerichtPanel eltern;
	public JXTable arzttbl = null;
	public MyArztTableModel atblm;
	JButton[] buts = {null,null};

	public BerichtArztAuswahl(EBerichtPanel xeltern){
	super();

	setLayout(new BorderLayout());
	
	new SwingWorker<Void,Void>(){

		@Override
		protected Void doInBackground() throws Exception {
		    setBackgroundPainter(Reha.thisClass.compoundPainter.get("GutachtenWahl"));
			return null;
		}
		
	}.execute();


	add(getForm(),BorderLayout.CENTER);
	addKeyListener(this);

	eltern = xeltern;
	eltern.aerzte = new String[] {};
	}
	
	private JPanel getForm(){
		try{

		//                                1     2    3    4    5      1       2    3   4  5
		FormLayout lay = new FormLayout("0dlu,10dlu,fill:0:grow(1.0),10dlu,0dlu","10dlu,100dlu,5dlu,p,5dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		atblm = new MyArztTableModel();
		atblm.setColumnIdentifiers(new String[] {"Drucken","Name","Vorname","Strasse","Ort","LANR","BSNR",""});
		arzttbl = new JXTable(atblm);
		arzttbl.getColumn(0).setMaxWidth(30);
		arzttbl.getColumn(7).setMinWidth(0);
		arzttbl.getColumn(7).setMaxWidth(0);
		arzttbl.validate();
		JScrollPane jscr = JCompTools.getTransparentScrollPane(arzttbl);
		jscr.validate();
		pb.add(jscr,cc.xy(3,2,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		FormLayout lay2 = new FormLayout("fill:0:grow(0.33),150dlu,fill:0:grow(0.33),150dlu,fill:0:grow(0.33)","5dlu,p,5dlu");
		PanelBuilder pb2 = new PanelBuilder(lay2);
		pb2.getPanel().setOpaque(false);
		CellConstraints cc2 = new CellConstraints();
		buts[0] = new JButton("Auswahl übernehmen");
		buts[0].setActionCommand("uebernehmen");
		buts[0].addActionListener(this);
		pb2.add(buts[0],cc2.xy(2,2));
		
		buts[1] = new JButton("neuen Arzt aufnehmen");
		buts[1].setActionCommand("zusatz");
		buts[1].addActionListener(this);
		pb2.add(buts[1],cc2.xy(4,2));

		pb2.getPanel().validate();
		
		pb.add(pb2.getPanel(),cc.xy(3,4));
		
		pb.getPanel().validate();
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				ladeTabelle();
				Reha.thisClass.progressStarten(false);
				return null;
			}
			
		}.execute();
		
		return pb.getPanel();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
		
	}
	private void ladeTabelle(){
		String test = Reha.thisClass.patpanel.patDaten.get(63);
		if(test.trim().equals("")){
			JOptionPane.showMessageDialog(null, "Mit der Arztliste dieses Patienten läuft etwas schief....");
		}else{
			String[] arztid = test.split("\n");

			for(int i = 0; i < arztid.length;i++){
				String[] arzt = arztid[i].split("@");
				Vector vec = SqlInfo.holeFelder("select nachname,vorname,strasse,ort,arztnum,bsnr,id from arzt where id = '"+arzt[1]+"'");
				Vector vec2 = (Vector) ((Vector)vec.get(0)).clone();
				vec2.insertElementAt(Boolean.valueOf(false), 0);
				atblm.addRow( (Vector)vec2.clone());
			}
			if(atblm.getRowCount()> 0){
				arzttbl.setRowSelectionInterval(0, 0);
			}
			arzttbl.revalidate();
		}
	}
	private void doUebernahme(){
		Vector<String> vec = new Vector<String>();
		String[] aerzte;
		int rows = atblm.getRowCount();
		for(int i = 0; i < rows;i++){
			if(atblm.getValueAt(i, 0) == Boolean.TRUE){
				vec.add((String)atblm.getValueAt(i, 7));
			}
		}
		if(vec.size() > 0){
			aerzte = new String[vec.size()];
			for(int i = 0; i < vec.size();i++){
				aerzte[i] = vec.get(i);
			}
			eltern.aerzte = aerzte.clone();
		}else{
			JOptionPane.showMessageDialog(null,"Na ja, keine Auswahl ist ja auch eine Auswahl....");
			eltern.aerzte = new String[] {};
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("uebernehmen")){
			final BerichtArztAuswahl xthis = this;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doUebernahme();
					((JXDialog)xthis.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
					((JXDialog)xthis.getParent().getParent().getParent().getParent().getParent()).dispose();
					return null;
				}
			}.execute();
		}
		if(cmd.equals("zusatz")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doArztAufnehmen();
					return null;
				}
			}.execute();
		}
		
	}

	private void doArztAufnehmen(){
		JRtaTextField[] tf = {null,null,null};
		tf[0] = new JRtaTextField("nix",false);
		tf[1] = new JRtaTextField("nix",false);
		tf[2] = new JRtaTextField("nix",false);
		//ArztAuswahl(JXFrame owner, String name,String[] suchegleichnach,JRtaTextField[] elterntf,String arzt) {
		ArztAuswahl awahl = new ArztAuswahl(null,"ArztAuswahl",new String[] {"",""},new JRtaTextField[] {tf[0],tf[1],tf[2]},"");
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		System.out.println(tf[0].getText()+" - "+tf[1].getText()+" - "+tf[2].getText());
		if(!tf[2].getText().trim().equals("")){
			Vector vec = SqlInfo.holeFelder("select nachname,vorname,strasse,ort,arztnum,bsnr,id from arzt where id = '"+tf[2].getText()+"'");
			if(vec.size() > 0){
				String test = Reha.thisClass.patpanel.patDaten.get(63);
				if(! test.contains("@"+tf[2].getText().trim()+"@")){
					Vector vec2 = (Vector) ((Vector)vec.get(0)).clone();
					vec2.insertElementAt(true, 0);
					atblm.addRow( (Vector)vec2.clone());
					arzttbl.validate();
					String msg = "Dieser Arzt ist bislang nicht in der Arztliste dieses Patienten.\n"+
					"Soll dieser Arzt der Ärzteliste des Patienten zugeordnet werden?";
					int frage = JOptionPane.showConfirmDialog(null,msg,"Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
					if(frage == JOptionPane.YES_OPTION){
						test = test + "@"+tf[2].getText().trim()+"@\n";
						Reha.thisClass.patpanel.patDaten.set(63,test);
						String cmd = "update pat5 set aerzte='"+test+"' where pat_intern='"+Reha.thisClass.patpanel.aktPatID+"'";
						new ExUndHop().setzeStatement(cmd);
					}
				}else{
					JOptionPane.showMessageDialog(null,"Dieser Arzt ist bereits in der Ärzteliste enthalten...");
				}
			}
		}
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
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
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

class MyArztTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){return Boolean.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
//return (columnIndex == 0) ? Boolean.class : String.class;
}

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	    	return true;
	      }
	    
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object theData = null;
			if(columnIndex > 0){
				theData = (String) ((Vector)getDataVector().get(rowIndex)).get(columnIndex);				
			}else{
				theData = (Boolean) ((Vector)getDataVector().get(rowIndex)).get(columnIndex);
			}
			Object result = null;
			result = theData;
			return result;
		}
	    
	   
}

