package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import sqlTools.SqlInfo;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import terminKalender.datFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Eb4 {
	EBerichtPanel eltern = null;
	JXPanel pan = null;
	Font fontgross = null;
	Font fontklein = null;
	Font fontnormal = null;
	Font fontcourier = null;
	Font fontarialfett = null;
	Font fontarialnormal = null;
	Font fontarialfettgross = null;
	JScrollPane jscr = null;

	public Eb4(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		eltern = xeltern;
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,11);
		fontarialfett =new Font("Arial",Font.BOLD,11);
		fontarialnormal =new Font("Arial",Font.PLAIN,11);		
		fontcourier =new Font("Courier New",Font.PLAIN,12);
		fontarialfettgross = new Font("Arial",Font.BOLD,12);

		pan.add(constructSeite());

		new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() throws Exception {
				try{
					laden(eltern.neu);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			return null;
			}
		}.execute();	

	}
	public JXPanel getSeite(){
		return pan;
	}
	public JPanel constructSeite(){
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.25),p,fill:0:grow(0.25),10dlu",
				// 1    2   3     4   5    
				"20dlu, p ,10dlu, p,20dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		return pb.getPanel();
	}
	
	
	private void laden(boolean lneu){
		Vector<Vector<String>> vec = null;
		if(lneu){
			holeKTL(true);
		}else{
			vec = SqlInfo.holeFelder("select entdat3 from bericht2 where berichtid='"+
					new Integer(eltern.berichtid).toString()+"' LIMIT 1" );
			if(vec.size() <= 0){
				JOptionPane.showMessageDialog(null,"Achtung - kann KTL-Leistungen nicht laden");
				return;
			}
			String entdat = datFunk.sDatInDeutsch(vec.get(0).get(0));
			if(datFunk.DatumsWert(entdat) < datFunk.DatumsWert("01.01.2008")){
				holeKTL(false);
			}else{
				holeKTL(true);
			}
		}
	}
	private void holeKTL(boolean ktlneu){
		Vector<Vector<String>> vec = null;
		String ktltabelle = "";
		if(ktlneu){
			ktltabelle = "masntext2";
		}else{
			ktltabelle = "masntext";
		}
		vec = SqlInfo.holeFelder("select * from "+ktltabelle );
		System.out.println("Dit ktltabelle = "+ktltabelle);
		Vector<String> vec2 = new Vector<String>();
		vec2.add("./.");
		vec2.add("");
		vec2.add("");
		vec2.add("0");
		vec.insertElementAt((Vector)((Vector<String>)vec2).clone(), 0);
		for(int i = 0;i < 50;i++){
			eltern.ktlcmb[i] = new JRtaComboBox((Vector<Vector<String>>)vec,0,3);
			eltern.ktltfc[i] = new JRtaTextField("GROSS",false);
			eltern.ktltfd[i] = new JRtaTextField("GROSS",false);
			eltern.ktltfa[i] = new JRtaTextField("ZAHLEN",false);
		}
		eltern.ktlcmb[3].setSelectedIndex(5);
		System.out.println(" Der Rückgabewert der Combobox = "+eltern.ktlcmb[3].getValue());
	}
	
}
