package org.thera_pi.nebraska.gui.utils;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;



public class JCompTools {

	public static JScrollPane getTransparentScrollPane(JPanel jpan){
		JScrollPane jscr = new JScrollPane();
		jscr.setOpaque(false);
		jscr.getViewport().setOpaque(false);
		jscr.setBorder(null);
		jscr.setViewportBorder(null);
		jscr.setViewportView(jpan);
		jscr.validate();
		return jscr;
	}
	public static JScrollPane getTransparentScrollPane(Component jpan){
		JScrollPane jscr = new JScrollPane();
		jscr.setOpaque(false);
		jscr.getViewport().setOpaque(false);
		jscr.setBorder(null);
		jscr.setViewportBorder(null);
		jscr.setViewportView(jpan);
		jscr.validate();
		return jscr;
	}
	
	public static JScrollPane getTransparent2ScrollPane(JScrollPane jpan){
		JScrollPane jscr = new JScrollPane();
		jscr.setOpaque(false);
		jscr.getViewport().setOpaque(false);
		jscr.setBorder(null);
		jscr.setViewportBorder(null);
		jscr.setViewportView(jpan);
		jscr.validate();
		return jscr;
	}
	public static JPanel getEmptyJXPanel(LayoutManager lay){
		JPanel jpan = new JPanel();
		jpan.setBorder(null);
		jpan.setLayout(lay);
		return jpan;
	}
	public static JPanel getEmptyJPanel(){
		JPanel jpan = new JPanel();
		jpan.setBorder(null);
		return jpan;
	}

}
