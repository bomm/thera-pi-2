package rehaKassenbuch;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;



import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class RehaKassenbuchTab extends JXPanel implements ChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7580783032048353314L;
	
	private Vector<String> vectitel = new Vector<String>();
	private Vector<String> vecdescript = new Vector<String>();
	private Vector<ImageIcon> vecimg = new Vector<ImageIcon>();

	JTabbedPane kassenbuchTab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	
	RehaKassenbuchPanel kbPanel = null;
	
	RehaKassenbuchEdit kbEdit = null;
	
	public RehaKassenbuchTab(){
		super();
		setLayout(new BorderLayout());
		kassenbuchTab = new JTabbedPane();
		kassenbuchTab.setUI(new WindowsTabbedPaneUI());
		
		kbPanel = new RehaKassenbuchPanel(this);
		kassenbuchTab.add("Kassenbuch erstellen",kbPanel);
		
		kbEdit = new RehaKassenbuchEdit(this);
		kassenbuchTab.add("Kassenbuch bearbeiten",kbEdit);

		kassenbuchTab.addChangeListener(this);
		doHeader();

		jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(kassenbuchTab, BorderLayout.CENTER);

        jxh.validate();
        kassenbuchTab.validate();
		validate();

		
	}
	public void setHeader(int header){
        jxh.setTitle(vectitel.get(header));
        jxh.setDescription(vecdescript.get(header));
        jxh.setIcon(vecimg.get(header));
        jxh.validate();
	}
	
	public void tabellenAktualisieren(){
		kbEdit.doKBErmitteln();
	}
	private void doHeader(){
		ImageIcon ico;
		
		String ss = System.getProperty("user.dir")+File.separator+"icons"+File.separator+"nebraska_scale.jpg";
		System.out.println(ss);
        ico = new ImageIcon(ss);
		vectitel.add("Kassenbuch erstellen");
		vecdescript.add("<html>Hier haben Sie die Möglichkeit ein neues Kassenbuch für den Zeitraum 'von - bis' zu erstellen.<br>" +
                "Verwenden Sie für die Namensgebung des neuen Kassenbuches <b>keine Umlaute</b> und <b>kein scharfes ß</b>, sowie <b>keine Leerzeichen.</b><br>" +
                "Das einzige erlaubte Sonderzeichen im Namen des Kassenbuches ist der <b>Unterstrich ( _ )</b><br><br>"+
                "Wenn Sie ein neues kassenbuch erzeugt haben können Sie dieses auf dem Karteireiter 'Kassenbuch bearbeiten' erweitern, löschen, wie's beliebt.</html>");
		vecimg.add(ico);
		
		
		vectitel.add("Zertifikate auswerten");
		vecdescript.add("....Hier können Sie die Zertifikatskette einsehen\n" +
                "Neue Zertifikate einlesen (neue Datenannahmestellen)\n" +
                "und schlußendlich überpüfen wie lange welches Zertifikat gültig ist");
		vecimg.add(ico);
		
		vectitel.add("Manuell verschlüsseln");
		vecdescript.add("....Hier können Sie Dateien manuell verschlüsseln\n" +
                "Weshalb auch immer....");
		vecimg.add(ico);

		vectitel.add("Test- und Experimentierpanel");
		vecdescript.add("....Diese Seite ist bislang noch Bodo und Jürgen vorbehalten (leider).\n" +
                "Hier werden die Funktionen die sp�ter Nebraska zu dem machen was Nebraske ist\n"+
                "entwickelt und getestet");
		vecimg.add(ico);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		JTabbedPane pane = (JTabbedPane)arg0.getSource();
        int sel = pane.getSelectedIndex();
        jxh.setTitle(vectitel.get(sel));
        jxh.setDescription(vecdescript.get(sel));
        jxh.setIcon(vecimg.get(sel));   
	}
	

}
