package dialoge;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;

import events.RehaTPEvent;
import events.RehaTPEventClass;

import CommonTools.ListenerTools;

import reha301.Reha301;


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
		try{
			this.setBorder(null);
			FlowLayout lay = new FlowLayout(FlowLayout.RIGHT,5,2);
			this.setLayout(lay);
			this.setOpaque(false);
			ZweiButtons();
			img3 = new ImageIcon(Reha301.progHome+"icons/inaktiv.png");
			thisClass = this;
			this.setPreferredSize(new Dimension(50,20));
			this.revalidate();
		}catch(Exception ex){
			
		}

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
		try{
			jb1 = new JXButton();
			jb1.setBorder(null);
			jb1.setOpaque(false);
			jb1.setPreferredSize(new Dimension(16,16));
			img1 = new ImageIcon(Reha301.progHome+"icons/green.png"); 
			jb1.setIcon(img1);
			jb1.setActionCommand("gruen");
			jb1.addActionListener(this);
			this.add(jb1);
			
			jb2 = new JXButton();
			jb2.setBorder(null);
			jb2.setOpaque(false);
			jb2.setPreferredSize(new Dimension(16,16));
			img2 = new ImageIcon(Reha301.progHome+"icons/red.png");
			jb2.setIcon(img2);
			jb2.setActionCommand("rot");
			jb2.addActionListener(this);
		this.add(jb2);
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
			try{
				
				String sEvent = getName();
				RehaTPEvent rEvt = new RehaTPEvent(thisClass.getParent().getParent());
				rEvt.setRehaEvent("PinPanelEvent");
				rEvt.setDetails(sEvent,"ROT") ;
				RehaTPEventClass.fireRehaTPEvent(rEvt);
				////System.out.println(rEvt);						

				int comps = this.getComponentCount();
				Component comp = null;
				for(int i = 0; i < comps;i++){
					comp = this.getComponent(i);
					ListenerTools.removeListeners(comp);
				}
				img1 = null;
				img2 = null;
				img3 = null;
				this.setVisible(false);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

}
