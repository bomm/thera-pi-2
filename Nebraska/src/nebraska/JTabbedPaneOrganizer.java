package nebraska;

import java.awt.BorderLayout;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;


public class JTabbedPaneOrganizer extends JXPanel implements ChangeListener{
	public JXTitledPanel jxTitel;
	public JTabbedPane jtb;
	public JXHeader jxh;
	private Vector<String> vectitel = new Vector<String>();
	private Vector<String> vecdescript = new Vector<String>();
	private Vector<ImageIcon> vecimg = new Vector<ImageIcon>();
	public JTabbedPaneOrganizer(){
		super();
		setOpaque(false);
		setLayout(new BorderLayout());
		jtb = new JTabbedPane();
		doHeader();
		jtb.addTab("Zert-Antrag stellen", new ZertAntrag());
		jtb.addTab("Zertifikate auswerten", new JXPanel());
		jtb.addTab("Manuell verschlüsseln", new JXPanel());
		jtb.addTab("Test- und Experimentierpanel", new NebraskaTestPanel());
		jtb.addChangeListener(this);
        jxh = new JXHeader();
        add(jxh, BorderLayout.NORTH);
        add(jtb, BorderLayout.CENTER);
        
        jxh.validate();
        jtb.validate();
		validate();



	}
	public void setHeader(int header){
        jxh.setTitle(vectitel.get(header));
        jxh.setDescription(vecdescript.get(header));
        jxh.setIcon(vecimg.get(header));
        jxh.validate();
	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		JTabbedPane pane = (JTabbedPane)arg0.getSource();
        int sel = pane.getSelectedIndex();
        jxh.setTitle(vectitel.get(sel));
        jxh.setDescription(vecdescript.get(sel));
        jxh.setIcon(vecimg.get(sel));        
	}
	private void doHeader(){
		ImageIcon ico;
        String ss = Constants.KEYSTORE_DIR+File.separator+"icons"+File.separator+"nebraska_scale.jpg";
		ico = new ImageIcon(ss);
		vectitel.add("Antrag auf Zertifizierung bei der ITSG stellen");
		vecdescript.add("....Geben Sie hier bitte Ihre Stammdaten ein\n" +
                "Wenn die Stammdaten komplett sind können Sie den Antrag ausdrucken, unterzeichnen und anschließend per FAX\n" +
                "an die ITSG senden. (FAX-Nr. der ITSG finden Sie auf dem Antrag).\n\n"+
                "Wenn Sie die Schaltfläche 'Request-erzeugen' wird für Sie ein Schlüsselpaar erzeugt und automatisiert an die ITSG per Mail versandt.");
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
                "Hier werden die Funktionen die später Nebraska zu dem machen was Nebraske ist\n"+
                "entwickelt und getestet");
		vecimg.add(ico);
	}

}
