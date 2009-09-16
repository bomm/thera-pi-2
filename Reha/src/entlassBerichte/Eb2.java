package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;

import systemTools.JCompTools;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Eb2 {
	EBerichtPanel eltern = null;
	JXPanel pan = null;
	Font fontgross = null;
	Font fontklein = null;
	Font fontnormal = null;
	Font fontcourier = null;
	Font fontarialfett = null;
	Font fontarialnormal = null;
	
	public Eb2(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		eltern = xeltern;
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,10);
		fontarialfett =new Font("Arial",Font.BOLD,12);
		fontarialnormal =new Font("Arial",Font.PLAIN,12);		
		fontcourier =new Font("Courier New",Font.PLAIN,12);
		pan.add(constructSeite(),BorderLayout.CENTER);
		pan.validate();
		pan.setVisible(true);
	}
	
		
		
	public JXPanel getSeite(){
		return pan;
	}
	public JScrollPane constructSeite(){
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.25),p,fill:0:grow(0.25),10dlu",
				
				//   2=titel   4=block1 6=block2 8=block3
				// 1    2   3    4   5    6      8   9   10  11   12  13 14
				"20dlu, p ,2dlu, p, 10dlu,p,5dlu,p ,5dlu,p,  5dlu,p, 0dlu,p, 4dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		//pb.addLabel("Das ist ein Label",cc.xy(2,2));
		pb.add(getTitel(),cc.xy(3,2));

		
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}
	private JXPanel getTitel(){
		JXPanel tit = new JXPanel();
		tit.setOpaque(false);
		FormLayout laytit = new FormLayout("p,33dlu,p","2dlu,p");
		CellConstraints cctit = new CellConstraints(); 
		tit.setLayout(laytit);
		return tit;
	}	
	
	

}
