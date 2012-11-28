package rehaMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class MailTab extends JXPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4803270370485221090L;
	JTabbedPane mailTab = null;
	public MailPanel mailPanel = null;
	public SendMailPanel sendPanel = null;
	public ToDoPanel todoPanel = null;
	public RTFEditorPanel editorPanel = null;
	public static RehaMail eltern;
	public MailTab(RehaMail xeltern){
		super(new BorderLayout());
		eltern = xeltern;
		CompoundPainter<Object> cp = null;
		MattePainter mp = null;
		LinearGradientPaint p = null;
		/*****************/
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(960,100);		
		start = new Point2D.Float(0, 0);
		end = new Point2D.Float(0,400);
		float[] dist =null;
		Color[] colors = null;
	    dist = new float[] {0.0f, 0.75f};
	    colors = new Color[] {Color.WHITE,CommonTools.Colors.Green.alpha(0.45f)};
	    p = new LinearGradientPaint(start, end, dist, colors);
	    mp = new MattePainter(p);
	    cp = new CompoundPainter<Object>(mp);
	    this.setBackgroundPainter(cp);

		setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		mailTab = new JTabbedPane();
		mailTab.setUI(new WindowsTabbedPaneUI());
		mailTab.setOpaque(false);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					
					mailPanel = new MailPanel();
					mailPanel.setOpaque(false);
					mailTab.setPreferredSize(RehaMail.thisFrame.getPreferredSize());
					//mailTab.addTab("RTF-Editor",RehaMail.attachmentIco[5],editorPanel=new RTFEditorPanel()); 

					mailTab.addTab("eingegangene Nachrichten", RehaMail.attachmentIco[5],mailPanel );
					mailTab.addTab("gesendete Nachrichten",RehaMail.attachmentIco[6],(sendPanel = new SendMailPanel()));
					mailTab.addTab(ToDoPanel.setTabTitel(),RehaMail.symbole.get("todo"),(todoPanel = new ToDoPanel()));
					/*
					DropTarget dndt = new DropTarget();
					DropTargetListener dropTargetListener =
						 new DropTargetListener() {
						  public void dragEnter(DropTargetDragEvent e) {}
						  public void dragExit(DropTargetEvent e) {}
						  public void dragOver(DropTargetDragEvent e) {}
						  public void drop(DropTargetDropEvent e) {
							  
						  }
						@Override
						public void dropActionChanged(DropTargetDragEvent dtde) {
							// TODO Auto-generated method stub
							
						}
					};
					
					dndt.addDropTargetListener(dropTargetListener);
					todoPanel.setDropTarget(dndt);
					*/
					
					mailTab.revalidate();
					
					mailTab.setSelectedIndex(0);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
			
		add(mailTab,BorderLayout.CENTER);
	}
	public MailPanel getMailPanel(){
		return mailPanel;
	}
	public SendMailPanel getSendPanel(){
		return sendPanel;
	}
	public ToDoPanel getToDoPanel(){
		return todoPanel;
	}
	public void setzeTitel(int tab,String titel){
		mailTab.setTitleAt(tab, titel);
	}

	
}
