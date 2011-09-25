package Suchen;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;

public class JCompTools {

	public static JScrollPane getTransparentScrollPane(JXPanel jpan){
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
	public static JXPanel getEmptyJXPanel(LayoutManager lay){
		JXPanel jpan = new JXPanel();
		jpan.setBorder(null);
		jpan.setLayout(lay);
		return jpan;
	}
	public static JXPanel getEmptyJXPanel(){
		JXPanel jpan = new JXPanel();
		jpan.setBorder(null);
		return jpan;
	}

}
