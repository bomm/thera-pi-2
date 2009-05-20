package hauptFenster;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTitledPanel;

//import rtaWissen.BrowserFenster;

import dialoge.RehaSmartDialog;

public class ContainerConfig {

	private int compAnzahl = 0;
	public static Vector compVec = new Vector();
	private ArrayList compArr = new ArrayList();

	
	public ContainerConfig(){
		compAnzahl = compAnzahl+1;		
	}

	public void addContainer(String simage,String sname,Component jcomp,RehaSmartDialog smd){
		JXButton jb1 = new JXButton();
		jb1.setBorder(null);
		jb1.setOpaque(false);
		jb1.setPreferredSize(new Dimension(16,16));
		ImageIcon img1 = new ImageIcon(Reha.proghome+"/icons/"+simage);
		jb1.setIcon(img1);
		jb1.setName("icon-"+sname);
		jb1.setToolTipText(sname);
		compArr.add(sname);
		compArr.add(jcomp);
		compArr.add(jb1);
		compArr.add(smd);
		//compArr.add()
		
		compVec.add(compArr);
		jb1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				removeContainer(((Component) e.getSource()).getName());
			}
		});

		jb1.disable();
		Reha.thisClass.jxPinContainer.add(jb1);
		Reha.thisClass.jxPinContainer.revalidate();
		jcomp.hide();
		//((Component)((ArrayList)compVec.get(compAnzahl-1)).get(1)).hide();
		//System.out.println("Vektorinhalt "+ compVec);
		System.out.println("********Name = *********************");
		System.out.println(sname);		
		System.out.println("********Componente = *********************");
		System.out.println(jcomp);
	}	
	/*
	public void addBrowserContainer(String simage,String sname,Component jcomp,BrowserFenster smd){
		JXButton jb1 = new JXButton();
		jb1.setBorder(null);
		jb1.setOpaque(false);
		jb1.setPreferredSize(new Dimension(16,16));
		ImageIcon img1 = new ImageIcon(Reha.proghome+"/icons/"+simage);
		jb1.setIcon(img1);
		jb1.setName("icon-"+sname);
		jb1.setToolTipText(sname);
		compArr.add(sname);
		compArr.add(jcomp);
		compArr.add(jb1);
		compArr.add(smd);
		//compArr.add()
		
		compVec.add(compArr);
		jb1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				removeContainer(((Component) e.getSource()).getName());
			}
		});

		jb1.disable();
		Reha.thisClass.jxPinContainer.add(jb1);
		Reha.thisClass.jxPinContainer.revalidate();
		jcomp.hide();
		//((Component)((ArrayList)compVec.get(compAnzahl-1)).get(1)).hide();
		//System.out.println("Vektorinhalt "+ compVec);
		System.out.println("********Name = *********************");
		System.out.println(sname);		
		System.out.println("********Componente = *********************");
		System.out.println(jcomp);
	}		
	*/
	
		public void removeContainer(String cname){
			int i,size;
			size = compVec.size();
			String name = "";
			 
			for(i=0;i<size;i++){
				name = (String) ((ArrayList)compVec.get(i)).get(0);
				if (cname.equals("icon-"+name)){

					((Component)((ArrayList)compVec.get(i)).get(1)).show();
					if(((ArrayList)compVec.get(i)).get(3) != null){
						if(((ArrayList)compVec.get(i)).get(3) instanceof RehaSmartDialog){
							((RehaSmartDialog)((ArrayList)compVec.get(i)).get(3)).setVisible(true);
							((RehaSmartDialog)((ArrayList)compVec.get(i)).get(3)).requestFocus();
						}else if(((ArrayList)compVec.get(i)).get(3) instanceof JDialog){
							((JDialog)((ArrayList)compVec.get(i)).get(3)).setVisible(true);
							((JDialog)((ArrayList)compVec.get(i)).get(3)).requestFocus();
						}
					}else{
						((RehaSmartDialog)((ArrayList)compVec.get(i)).get(1)).requestFocus();						
					}

					((Component)((ArrayList)compVec.get(i)).get(2)).setVisible(false);

					Reha.thisClass.jxPinContainer.remove(
							((Component)((ArrayList)compVec.get(i)).get(2)));
					Component com = ((Component)((ArrayList)compVec.get(i)).get(2));
					com = null;
					compVec.remove(i);
					Reha.thisClass.jxPinContainer.revalidate();
					
					break;
					//System.out.println( compVec);
				}	
			}
			

	}
	
}
