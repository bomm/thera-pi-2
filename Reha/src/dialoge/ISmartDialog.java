package dialoge;

import hauptFenster.Reha;


import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.SwingXUtilities;

import rehaContainer.RehaTP;
import systemEinstellungen.SystemConfig;
import terminKalender.TerminFenster;


import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ComponentListener;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

public interface ISmartDialog extends RehaTPEventListener{
	public PinPanel pinPanel=null;
	public JXTitledPanel jtp = null;
	
	public JXTitledPanel getSmartTitledPanel();
	

	public JXTitledPanel getTitledPanel();
	
	public void setContentPanel(Container cont);
	public void aktiviereIcon();
	public void deaktiviereIcon();
	public void setPinPanel (PinPanel pinPanel);
	public PinPanel getPinPanel ();
	public void setIgnoreReturn(boolean ignore);
	
	public void ListenerSchliessen();
	public void rehaTPEventOccurred(RehaTPEvent evt);


}  //  @jve:decl-index=0:visual-constraint="387,36"
