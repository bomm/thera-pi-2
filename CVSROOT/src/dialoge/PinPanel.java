package dialoge;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.VerticalLayout;

import events.RehaEvent;
import events.RehaEventClass;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class PinPanel extends JXPanel{
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
	img3 = new ImageIcon(Reha.proghome+"icons/inaktiv.png");
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
		img1 = new ImageIcon(Reha.proghome+"icons/green.png");
		jb1.setIcon(img1);
		
		jb1.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			String sEvent = getName();
			RehaTPEvent rEvt = new RehaTPEvent(thisClass.getParent().getParent());
			rEvt.setRehaEvent("PinPanelEvent");
			rEvt.setDetails(sEvent,"GRUEN") ;
			//System.outprintln("*****************************************");
			//System.outprintln("*****abgefeuert von GRÜN****"+sEvent);
			//System.outprintln("*****************************************");
			RehaTPEventClass.fireRehaTPEvent(rEvt);
		}
		});
		jb1.disable();
		this.add(jb1);
		
		jb2 = new JXButton();
		jb2.setBorder(null);
		jb2.setOpaque(false);
		jb2.setPreferredSize(new Dimension(16,16));
		img2 = new ImageIcon(Reha.proghome+"icons/red.png");
		jb2.setIcon(img2);
		
		jb2.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			//System.out.println("Von PinPanel Fenstername = "+fenstername);
			String sEvent = getName();
			RehaTPEvent rEvt = new RehaTPEvent(thisClass.getParent().getParent());
			rEvt.setRehaEvent("PinPanelEvent");
			rEvt.setDetails(sEvent,"ROT") ;
			RehaTPEventClass.fireRehaTPEvent(rEvt);
		}
		});
		jb2.disable();
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

}
