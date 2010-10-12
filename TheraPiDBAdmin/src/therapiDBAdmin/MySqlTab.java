package therapiDBAdmin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;


public class MySqlTab  extends JXPanel implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTabbedPane sqlTab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	JXPanel tab1 = null;
	JXPanel tab2 = null;
	JXPanel tab3 = null;
	JXPanel tab4 = null;
	
	public static String iPAdresse = null;
	public static String portAdresse = null;
	public static String neuerUser = null;
	public static String neuesPasswort = null;
	public static String neuerDBName = null;
	
	private Vector<String> vectitel = new Vector<String>();
	private Vector<String> vecdescript = new Vector<String>();
	private Vector<ImageIcon> vecimg = new Vector<ImageIcon>();
	
	MySqlTab(){
		super();
		 
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createRaisedBevelBorder());
		sqlTab = new JTabbedPane();
		sqlTab.setUI(new WindowsTabbedPaneUI());
		tab1 = new Seite1(this);
		tab1.setName("Seite1");
		sqlTab.add("<html><font size='5' >&nbsp;1 - MySql-Zugang testen&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>",tab1);
		tab2 = new Seite2(this);;
		tab2.setName("Seite2");
		sqlTab.add("<html><font size='5' >&nbsp;2 - <font color='e77817'>Thera-Pi</font> Datenbank erstellen&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>",tab2);
		tab3 = new Seite3(this);
		tab3.setName("Seite3");
		sqlTab.add("<html><font size='5' >&nbsp;3 -  <font color='e77817'>Thera-Pi</font> Tabellen importieren&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>",tab3);
		tab4 = new MyErstInstall(this);
		tab4.setName("Seite4");
		sqlTab.add("<html><font size='5' >&nbsp;4 -  <font color='e77817'>Thera-Pi</font> Eigene Firma (IK) einrichten&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>",tab4);
		Component[] comp = sqlTab.getComponents();
		for (int i = 0; i < comp.length;i++){
			System.out.println(comp[i]);
		}
		
		
		

		
		sqlTab.addChangeListener(this);
		doHeader();
        jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(sqlTab, BorderLayout.CENTER);
        setHeader(0);

		validate();
	}
	
	public void setSeite1Ok(boolean ok){
		if(ok){
			sqlTab.setTitleAt(0, "<html><font size='5' >&nbsp;1 - MySql-Zugang testen&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/clean.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite1Ok = true;
		}else{
			sqlTab.setTitleAt(0, "<html><font size='5' >&nbsp;1 - MySql-Zugang testen&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite1Ok = false;
		}
	}
	public void setSeite2Ok(boolean ok){
		if(ok){
			sqlTab.setTitleAt(1, "<html><font size='5' >&nbsp;2 - <font color='e77817'>Thera-Pi</font> Datenbank erstellen&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/clean.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite2Ok = true;
		}else{
			sqlTab.setTitleAt(1, "<html><font size='5' >&nbsp;2 - <font color='e77817'>Thera-Pi</font> Datenbank erstellen&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite2Ok = false;
		}
	}
	public void setSeite3Ok(boolean ok){
		if(ok){
			sqlTab.setTitleAt(2, "<html><font size='5' >&nbsp;3 -  <font color='e77817'>Thera-Pi</font> Tabellen importieren&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/clean.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite3Ok = true;
		}else{
			sqlTab.setTitleAt(2, "<html><font size='5' >&nbsp;3 -  <font color='e77817'>Thera-Pi</font> Tabellen importieren&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite3Ok = false;
		}
	}
	public void setSeite4Ok(boolean ok){
		if(ok){
			sqlTab.setTitleAt(3, "<html><font size='5' >&nbsp;4 -  <font color='e77817'>Thera-Pi</font> Eigene Firma (IK) einrichten&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/clean.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite3Ok = true;
		}else{
			sqlTab.setTitleAt(3, "<html><font size='5' >&nbsp;4 -  <font color='e77817'>Thera-Pi</font> Eigene Firma (IK) einrichten&nbsp;<img src='file:///"+TheraPiDbAdmin.proghome+"icons/application-exit.png' width='32' height='32'></font></html>");
			TheraPiDbAdmin.seite3Ok = false;
		}
	}
	public void setParams(String db,String user,String pw){
		neuerDBName = db;
		neuerUser = user;
		neuesPasswort = pw;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		JTabbedPane pane = (JTabbedPane)arg0.getSource();
        int sel = pane.getSelectedIndex();
        if(sel > 0 && ! TheraPiDbAdmin.seite1Ok){
        	JOptionPane.showMessageDialog(null, "Testen Sie zuerst die Verbindung zur MySql-Datenbank\nOhne erfolgreichen Test können keine Tabellen importiert werden!");
//        	pane.setSelectedIndex(0);
        	return;
        }
        if(sel == 2 && ! TheraPiDbAdmin.seite2Ok){
        	JOptionPane.showMessageDialog(null, "Legen Sie zuerst die Datenbank und einen neuen Benutzer an\nTabellen können ansonsten nicht importiert werden");
//        	pane.setSelectedIndex(1);
        	return;
        }
        jxh.setTitle(vectitel.get(sel));
        jxh.setDescription(vecdescript.get(sel));
        jxh.setIcon(vecimg.get(sel));   

		
	}
	
	private void doHeader(){
		ImageIcon ico;
        //String ss = System.getProperty("user.dir")+File.separator+"icons"+File.separator+"TPorg.png";
        String ss = TheraPiDbAdmin.proghome+"icons"+File.separator+"TPorg.png";
        ico = new ImageIcon(ss);
		vectitel.add("<html><font size='5'>Datenbank für <font color='e77817'>Thera-Pi</font> einrichten</font></html>");
		vecdescript.add("<html>Gehen Sie bitte Schritt für Schritt vor.<br>" +
                "MySql muß bereits etweder lokal auf Ihrem Rechner oder aber auf dem zukünftigen Datenbankserver <b>vollständig und lauffähig</b> installiert sein. Der Benutzer 'root' muß für jeden Host zugelassen sein ('root'@'%')<br>" +
                "Ist dies nicht der Fall, beenden Sie den Assistenten und installieren Sie zunächst MySql und weitere Server-Programme.<br>"+
                "Die einfachste Möglichkeit bietet das XAMPP-Paket ( Google -> Suchbegriff: 'xampp +download' )</html>");
		vecimg.add(ico);
		
		
		vectitel.add("<html><font size='5'>Datenbank für <font color='e77817'>Thera-Pi</font> einrichten</font></html>");
		vecdescript.add("<html>Jetzt legen Sie die eigentliche Datenbank, sowie einen Datenbankbenutzer und dessen Passwort an.<br>Gehen Sie hierfür wie folgt vor:<br>" +
                "1. Geben Sie einen Namen für Ihre neue Thera-Pi Datenbank an. Verwenden Sie <b>keine</b> Umlaute, Leerzeichen oder Sonderzeichen<br>" +
                "2. Geben Sie einen (neuen) gemeinsamen Benutzernamen und ein Passwort für die Thera-Pi-Datenbank an.</html>");
		vecimg.add(ico);
		
		vectitel.add("<html><font size='5'>Datenbank für <font color='e77817'>Thera-Pi</font> einrichten</font></html>");
		vecdescript.add("....Hier können Sie Dateien manuell verschlüsseln\n" +
                "Weshalb auch immer....");
		vecimg.add(ico);
		vectitel.add("<html><font size='5'><font color='e77817'>Thera-Pi</font> für den erstmaligen Start einrichten</font></html>");
		vecdescript.add("<html>überprüfen Sie bitte ob alle u.g. Voraussetzungen auf Ihr System zutreffen.<br>" +
                "1. <b>MySql</b> muß bereits lokal auf Ihrem Rechner, oder aber auf dem zukünftigen Datenbankserver <b>vollständig und lauffähig</b> installiert sein. <br>" +
                "2. Sie müssen bereits eine <b>neue Datenbank</b> erstellt haben<br>" +
                "3. Für diese Datenbank muß ein <b>Datenbankbenutzer</b> erstellt worden sein der von überall erreichbar sein sollte (Host=<b>'%'</b>)<br>"+
                "4. Für den Datenbankbenutzer muß ein <b>Paßwort</b> erstellt worden sein<br>"+
                "5. Sie haben die <b>Thera-Pi-Tabellen</b> bereits in Ihre neue Datenbank <b>importiert</b>.<br>"+
                "<font color='aa0000'><b>Nur wenn Sie alle oben genannten Voraussetzungen erfüllen sollten Sie mit der Anpassung für den erstmaligen Start von Thera-Pi fortfahren.<br>Andernfalls brechen "+
                "Sie an dieser Stelle ab, holen die versäumten Installationsschritte nach und starten Sie dann dieses Tool erneut.</b></font><br><br>"+"</html>");
		vecimg.add(ico);
		

	}	
	
	public void setHeader(int header){
        jxh.setTitle(vectitel.get(header));
        jxh.setDescription(vecdescript.get(header));
        jxh.setIcon(vecimg.get(header));
        //jxh.validate();
	}


}
