package fortschrittDlg;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;

import systemEinstellungen.SystemConfig;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class FortschrittDlg extends JXDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6399738869642861479L;
	/**
	 * 
	 */

	private JLabel textlab;
	private JLabel bildlab;
	Font font = new Font("Arial",Font.PLAIN,12);
	public JProgressBar fortschritt;
	
	public FortschrittDlg() {
		super((JComponent)Reha.thisFrame.getGlassPane());
		this.setUndecorated(true);
		this.setModal(false);
		//this.setAlwaysOnTop(true);
		this.setContentPane(getContent());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.validate();
		this.setVisible(true);
		this.requestFocus();
	}
	public JXPanel getContent(){
	JXPanel jpan = new JXPanel();
	jpan.setPreferredSize(new Dimension(400,100));
	jpan.setBackground(Color.WHITE);
	jpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	@SuppressWarnings("unused")
	FormLayout lay = new FormLayout("fill:0:grow(0.5),p,fill:0:grow(0.5)",
			"fill:0:grow(0.5),p,15dlu,p,3dlu,p,fill:0:grow(0.5)");
	jpan.setLayout(lay);
	CellConstraints cc = new CellConstraints();
	bildlab = new JLabel(" ");
	bildlab.setIcon(SystemConfig.hmSysIcons.get("tporgklein"));
	jpan.add(bildlab,cc.xy(2, 2));
	textlab = new JLabel(" ");
	textlab.setFont(font);
	textlab.setForeground(Color.BLUE);
	jpan.add(textlab,cc.xy(2, 4,CellConstraints.CENTER,CellConstraints.CENTER));
	fortschritt = new JProgressBar();
	jpan.add(fortschritt,cc.xy(2, 6,CellConstraints.FILL,CellConstraints.CENTER));
	jpan.validate();
	return jpan;
	
	}
	public void setzeLabel(String labelText){
		textlab.setText(labelText);
		//textlab.getParent().validate();
	}

}

