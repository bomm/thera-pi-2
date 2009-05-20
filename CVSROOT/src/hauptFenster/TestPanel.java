package hauptFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.jdesktop.swinghelper.layer.painter.Painter;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.PinstripePainter;

import systemTools.Colors;

public class TestPanel extends JScrollPane implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int setOben; 

	public TestPanel(int setOben) {
		super();
		
		this.setOben = setOben;
		this.setBorder(null);
		this.setViewportBorder(null);

		
		JXPanel jp1 = new JXPanel(new BorderLayout());
		jp1.setBackground(Color.WHITE);
		jp1.setBorder(null);
		JButton jBut1 = new JButton("Schliessen");
		jBut1.setIcon(new ImageIcon(getClass().getResource("/icons/exit.png")));
		jBut1.addActionListener(this);
		jp1.add(jBut1,BorderLayout.NORTH);
		this.setViewportView(jp1);
		JXPanel jp2 = new JXPanel(new BorderLayout());
		jp2.setOpaque(false);
		jp2.setBackgroundPainter(Reha.RehaPainter[0]);
		jp1.add(jp2,BorderLayout.CENTER);
	}
	
/****************************************/
/****************************************/
	 @Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();
		if(cmd.equals("Schliessen")){
			//schliessen();
		}
		
		
	}

}
