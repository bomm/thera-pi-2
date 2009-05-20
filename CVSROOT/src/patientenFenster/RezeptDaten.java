package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdesktop.swingx.JXPanel;

import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RezeptDaten extends JXPanel{
	//public JLabel[] rezlabs = {null,null,null,null,null,null,null,null,null};
	public JRtaTextField reznum = null;
	public RezeptDaten(){
		super();
		this.setOpaque(false);
		setBorder(null);
		setLayout(new BorderLayout());
		add(getDatenPanel(),BorderLayout.CENTER);
	}
	
	public JScrollPane getDatenPanel(){
		JScrollPane jscr = null;
		FormLayout lay = new FormLayout("p,fill:0:grow(0.50),p,fill:0:grow(0.50),p",
				//      1.Sep                2.Sep                              3.Sep
				//1 2   3  4   5  6   7  8   9 10   11 12  13 14  15 16  17  18 19  20 21 22  23 24       25
				"p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p,2dlu,p,2dlu,p, 2dlu,p,5dlu,p,5dlu,p,2dlu,p,20dlu:g,22px" );
		CellConstraints cc = new CellConstraints();
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.getPanel().setOpaque(false);
		Font fontreznr = new Font("Tahoma",Font.BOLD,16);
		Font fontbehandlung = new Font("Tahoma",Font.BOLD,11);		
		reznum = new JRtaTextField("GROSS",true);
		reznum.setText("KG57606");
		reznum.setFont(fontreznr);
		reznum.setForeground(Color.BLUE);
		reznum.setOpaque(false);
		reznum.setEditable(false);
		reznum.setBorder(null);
		reznum.setDragEnabled(true);
		/*
		PatGrundPanel.thisClass.rezlabs[0] = new JLabel();
		PatGrundPanel.thisClass.rezlabs[0].setFont(fontreznr);
		PatGrundPanel.thisClass.rezlabs[0].setName("rezeptnummer");
		PatGrundPanel.thisClass.rezlabs[0].setForeground(Color.BLUE);
		PatGrundPanel.thisClass.rezlabs[0].setText("KG57606");
		*/

		
		PatGrundPanel.thisClass.rezlabs[1] = new JLabel("");
		PatGrundPanel.thisClass.rezlabs[1].setName("hausbesuch");
		PatGrundPanel.thisClass.rezlabs[1].setIcon(new ImageIcon(Reha.proghome+"icons/home.gif"));

		PatGrundPanel.thisClass.rezlabs[2] = new JLabel("angelegt von: /st.");
		PatGrundPanel.thisClass.rezlabs[2].setName("angelegt");
		
		PatGrundPanel.thisClass.rezlabs[3] = new JLabel("AOK Reutlingen");
		PatGrundPanel.thisClass.rezlabs[3].setName("kostentraeger");

		PatGrundPanel.thisClass.rezlabs[4] = new JLabel("Gundel");
		PatGrundPanel.thisClass.rezlabs[4].setName("arzt");
		
		PatGrundPanel.thisClass.rezlabs[5] = new JLabel("Folgev. außerhalb d.R.");
		PatGrundPanel.thisClass.rezlabs[5].setName("verornungsart");
		
		PatGrundPanel.thisClass.rezlabs[6] = new JLabel("Begründung");
		PatGrundPanel.thisClass.rezlabs[6].setName("begruendung");
		PatGrundPanel.thisClass.rezlabs[6].setForeground(Color.RED);
		
		PatGrundPanel.thisClass.rezlabs[7] = new JLabel("Arztbericht");
		PatGrundPanel.thisClass.rezlabs[7].setName("arztbericht");

		PatGrundPanel.thisClass.rezlabs[8] = new JLabel("15 x KG");
		PatGrundPanel.thisClass.rezlabs[8].setName("behandlung1");
		PatGrundPanel.thisClass.rezlabs[8].setFont(fontbehandlung);
		PatGrundPanel.thisClass.rezlabs[9] = new JLabel("1 - 2 x");
		PatGrundPanel.thisClass.rezlabs[9].setName("frequenz");
		PatGrundPanel.thisClass.rezlabs[9].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezlabs[10] = new JLabel("15 x ET");
		PatGrundPanel.thisClass.rezlabs[10].setName("behandlung2");
		PatGrundPanel.thisClass.rezlabs[10].setFont(fontbehandlung);
		PatGrundPanel.thisClass.rezlabs[11] = new JLabel("----");
		PatGrundPanel.thisClass.rezlabs[11].setName("behandlung3");
		PatGrundPanel.thisClass.rezlabs[11].setFont(fontbehandlung);
		PatGrundPanel.thisClass.rezlabs[12] = new JLabel("----");
		PatGrundPanel.thisClass.rezlabs[12].setName("behandlung4");
		PatGrundPanel.thisClass.rezlabs[12].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezlabs[13] = new JLabel("WS 2b");
		PatGrundPanel.thisClass.rezlabs[13].setName("indikation");
		PatGrundPanel.thisClass.rezlabs[13].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezlabs[14] = new JLabel("20 Min.");
		PatGrundPanel.thisClass.rezlabs[14].setName("Dauer");
		PatGrundPanel.thisClass.rezlabs[14].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezdiag = new JTextArea("Das Kreuz tut weh....\nund der Hacksen auch\nZ.n. Blinddarm - Totaloperation...\n"+
				"Chron. Hypochondrie");
		PatGrundPanel.thisClass.rezdiag.setOpaque(false);
		PatGrundPanel.thisClass.rezdiag.setFont(new Font("Courier",Font.PLAIN,11));
		PatGrundPanel.thisClass.rezdiag.setForeground(Color.BLUE);
		PatGrundPanel.thisClass.rezdiag.setLineWrap(true);
		PatGrundPanel.thisClass.rezdiag.setName("notitzen");
		PatGrundPanel.thisClass.rezdiag.setWrapStyleWord(true);
		PatGrundPanel.thisClass.rezdiag.setEditable(false);


		jpan.add(reznum,cc.xy(1, 1));
		//jpan.add(PatGrundPanel.thisClass.rezlabs[0],cc.xy(1, 1));
		jpan.add(PatGrundPanel.thisClass.rezlabs[1],cc.xy(3, 1));
		jpan.add(PatGrundPanel.thisClass.rezlabs[2],cc.xy(5, 1));

		jpan.addSeparator("", cc.xyw(1,3,5));

		jpan.add(PatGrundPanel.thisClass.rezlabs[3],cc.xy(1, 5));
		jpan.add(PatGrundPanel.thisClass.rezlabs[4],cc.xy(5, 5));

		jpan.add(PatGrundPanel.thisClass.rezlabs[5],cc.xy(1, 7));
		jpan.add(PatGrundPanel.thisClass.rezlabs[6],cc.xy(3, 7));
		jpan.add(PatGrundPanel.thisClass.rezlabs[7],cc.xy(5, 7));

		jpan.addSeparator("", cc.xyw(1,9,5));
		
		jpan.add(PatGrundPanel.thisClass.rezlabs[8],cc.xy(1, 11));
		jpan.add(PatGrundPanel.thisClass.rezlabs[9],cc.xy(3, 11));
		jpan.add(PatGrundPanel.thisClass.rezlabs[14],cc.xy(5, 11));
		jpan.add(PatGrundPanel.thisClass.rezlabs[10],cc.xy(1, 13));
		jpan.add(PatGrundPanel.thisClass.rezlabs[11],cc.xy(1, 15));
		jpan.add(PatGrundPanel.thisClass.rezlabs[12],cc.xy(1, 17));
		
		
		jpan.addSeparator("", cc.xyw(1,19,5));
		
		jpan.add(PatGrundPanel.thisClass.rezlabs[13],cc.xy(1, 21));
		//jpan.add(PatGrundPanel.thisClass.rezlabs[14],cc.xy(1, 23));

		jpan.add(JCompTools.getTransparentScrollPane(PatGrundPanel.thisClass.rezdiag),cc.xywh(3, 21,3,4));
		
		jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}

}
