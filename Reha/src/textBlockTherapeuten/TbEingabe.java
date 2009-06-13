package textBlockTherapeuten;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXFrame;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

import systemTools.JRtaTextField;

import dialoge.RehaSmartDialog;
import events.RehaTPEventListener;

public class TbEingabe extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ThTextBlock thb = null;
	private JRtaTextField rueck = null;
	Vector<String> tbvec = null;
	int tbaktid;

	public TbEingabe(JXFrame owner, String name,ThTextBlock thb,Vector vtbs,int akttbid,JRtaTextField rueck) {
		super(owner, name);
		this.thb = thb;
		this.tbvec = vtbs;
		this.tbaktid = akttbid;
		this.rueck = rueck;
		
		super.getSmartTitledPanel().setName(name);
		super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
		String xtitel = "<html>Text für Platzhalter eingeben";
	    super.getSmartTitledPanel().setTitle(xtitel);
		setName(name);
		super.getSmartTitledPanel().setContentContainer(getEingabe());
		setLocationRelativeTo(this.thb);
		setModal(true);
		pack();


	}
	private JPanel getEingabe(){
		FormLayout lay = new FormLayout("","");
		PanelBuilder pb = new PanelBuilder(lay);
		return pb.getPanel();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
