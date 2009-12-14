package dialoge;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;

import systemEinstellungen.SystemConfig;
import systemTools.ListenerTools;
import events.RehaTPEvent;
import events.RehaTPEventClass;

public class PinPanel extends JXPanel implements ActionListener{
	private PinPanel thisClass = null;
	private JXButton jb1,jb2 = null;
	private ImageIcon img1,img2,img3 = null;
	public String fenstername = "";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7989339213283693863L;

	public PinPanel(){
		super();
	this.setBorder(null);
	FlowLayout lay = new FlowLayout(FlowLayout.RIGHT,5,2);
	//VerticalLayout lay = new VerticalLayout(3)
	this.setLayout(lay);
	this.setOpaque(false);

	
	
	ZweiButtons();
	img3 = SystemConfig.hmSysIcons.get("inaktiv");
	thisClass = this;
	this.setPreferredSize(new Dimension(50,20));
	this.revalidate();
	return;
	}
	
	public void setzeName(String fenstername){
			this.fenstername = fenstername;
	}

	public void setFocusImage(boolean focus){
		if(focus){
			img1.getImage().getGraphics();
		}
	}
	
	private void ZweiButtons(){
		
		jb1 = new JXButton();
		jb1.setBorder(null);
		jb1.setOpaque(false);
		jb1.setPreferredSize(new Dimension(16,16));
		img1 = SystemConfig.hmSysIcons.get("green"); 
		jb1.setIcon(img1);
		jb1.setActionCommand("gruen");
		jb1.addActionListener(this);
		//jb1.setEnabled(false);
		//jb1.disable();
		this.add(jb1);
		
		jb2 = new JXButton();
		jb2.setBorder(null);
		jb2.setOpaque(false);
		jb2.setPreferredSize(new Dimension(16,16));
		img2 = SystemConfig.hmSysIcons.get("rot"); //new ImageIcon(Reha.proghome+"icons/red.png");
		jb2.setIcon(img2);
		jb2.setActionCommand("rot");
		jb2.addActionListener(this);

		//jb2.setEnabled(false);
		//jb2.disable();
		this.add(jb2);
	}
	
	public void SetzeAktivButton(boolean aktiv){
		if(aktiv){
			jb1.setIcon(img1);
			this.repaint();
		}else{
			jb1.setIcon(img3);
			this.repaint();			
		}
	}
	public JXButton getGruen(){
		return jb1;
	}
	public JXButton getRot(){
		return jb2;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		if(cmd.equals("gruen")){
			String sEvent = getName();
			RehaTPEvent rEvt = new RehaTPEvent(thisClass.getParent().getParent());
			rEvt.setRehaEvent("PinPanelEvent");
			rEvt.setDetails(sEvent,"GRUEN") ;
			//System.outprintln("*****************************************");
			//System.outprintln("*****abgefeuert von GR�N****"+sEvent);
			//System.outprintln("*****************************************");
			RehaTPEventClass.fireRehaTPEvent(rEvt);
			return;
		}
		if(cmd.equals("rot")){
			String sEvent = getName();
			RehaTPEvent rEvt = new RehaTPEvent(thisClass.getParent().getParent());
			rEvt.setRehaEvent("PinPanelEvent");
			rEvt.setDetails(sEvent,"ROT") ;
			RehaTPEventClass.fireRehaTPEvent(rEvt);
			int comps = this.getComponentCount();
			Component comp = null;
			for(int i = 0; i < comps;i++){
				comp = this.getComponent(i);
				ListenerTools.removeListeners(comp);
			}
			img1 = null;
			img2 = null;
			img3 = null;
		}
	}

}
