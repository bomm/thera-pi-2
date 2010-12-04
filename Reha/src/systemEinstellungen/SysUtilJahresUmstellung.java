package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.thera_pi.nebraska.gui.utils.ButtonTools;

import sqlTools.SqlInfo;
import terminKalender.DatFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilJahresUmstellung extends JXPanel implements KeyListener, ActionListener  {
	

	/**
	 * 
	 */
	JLabel letztesjahr = null;
	JLabel letztesdatum = null;
	JLabel nextyear = null;
	JButton[] buts = {null,null};
	int umstellpatient = 0;
	int umstellrezept = 0;
	JProgressBar progress = null;
	private static final long serialVersionUID = 1L;
	
	SysUtilJahresUmstellung(){
		super(new GridLayout(1,1));
		////System.out.println("Aufruf SysUtilKalenderanlagen");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
		/*
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.setViewportView(getUmstellungsSeite());
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.validate();
	     
	     add(jscr);
	     */
		add(getUmstellungsSeite());
		return;
	}
	
	private JPanel getUmstellungsSeite(){
		FormLayout lay = new FormLayout("8dlu, p:g, 130dlu, 40dlu,20dlu",
			       //1.    2.  3.  4.    5.  6.    7.   8.  9.  10.  11.  12.  13. 14.   15.  16.  17. 18   19  20   21   22   23   24   25  26   27   28   29  30    31  32   33  34    35   36    37  38   39     40    41   42   43  44   45  46   47   48  49   50  51  52   53  54   55  56   57  58    59   60   61    62
					"p, 10dlu, p, 10dlu, p, 20dlu, p,10dlu, p, 10dlu, p, 20dlu, p, 10dlu, p,  5dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 15dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, 80dlu, 2dlu, p, 10dlu, p, 10dlu, p,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu");
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Letzte Jahresumstellung", cc.xyw(1, 1, 4));
		
		builder.addLabel("für Kalenderjahr", cc.xyw(2, 3,2));
		letztesjahr = new JLabel(Integer.toString(Integer.parseInt(SqlInfo.holeEinzelFeld("select altesjahr from jahresabschluss"))+1));
		letztesjahr.setForeground(Color.RED);
		letztesjahr.setFont(new Font("Arial",Font.BOLD,11));		
		builder.add(letztesjahr, cc.xy(4,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		builder.addLabel("umgestellt am", cc.xyw(2, 5,2));
		letztesdatum = new JLabel(DatFunk.sDatInDeutsch(SqlInfo.holeEinzelFeld("select umgestellt from jahresabschluss")));
		letztesdatum.setForeground(Color.RED);
		letztesdatum.setFont(new Font("Arial",Font.BOLD,11));		
		builder.add(letztesdatum, cc.xy(4,5,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		builder.getPanel().validate();
		
		builder.addSeparator("Neue und damit aktuelle Umstellung", cc.xyw(1, 7, 4));
		builder.addLabel("für Kalenderjahr", cc.xyw(2, 9 ,2));
		
		nextyear = new JLabel(Integer.toString(Integer.parseInt(letztesjahr.getText())+1));
		nextyear.setForeground(Color.RED);
		nextyear.setFont(new Font("Arial",Font.BOLD,11));		
		builder.add(nextyear, cc.xy(4,9,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		builder.addLabel("Umstellung testen", cc.xyw(2, 11 ,2));
		buts[0] = ButtonTools.macheBut("testen", "testen", this);
		builder.add(buts[0], cc.xy(4,11,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		builder.addSeparator("Umstellung starten", cc.xyw(1, 13, 4));
		builder.addLabel("Kalenderjahr jetzt umstellen", cc.xyw(2, 15 ,2));
		buts[1] = ButtonTools.macheBut("umstellen", "umstellen", this);
		builder.add(buts[1], cc.xy(4,15,CellConstraints.FILL,CellConstraints.DEFAULT));
		buts[1].setEnabled(false);
		
		progress = new JProgressBar();
		builder.add(progress,cc.xyw(2,17,3));
		
		return builder.getPanel();

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("testen")){
			doTesten();
			return;
		}
		if(cmd.equals("umstellen")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						int frage = JOptionPane.showConfirmDialog(null, "<html>Sie stellen jetzt die Patienten- und Rezeptdaten für das <b>Geschäftsjahr <font color='#ff0000'>"+nextyear.getText()+"</font></b> (unumkehrbar) um.<br>Wollen Sie die Umsetzung jetzt starten</html>", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
						if(frage != JOptionPane.YES_OPTION){
							return null;
						}
						buts[1].setEnabled(false);
						doUmstellen();
						JOptionPane.showMessageDialog(null,"Daten wurden erfolgreich umgesetzt - wir wünschen Ihnen gute Geschäfte im neuen Kalenderjahr!");
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Umstellung auf neues Jahr fehlgeschlagen");
					}
					return null;
				}
			}.execute();
		}
	}
	public void doUmstellen(){
		String reset = Integer.toString(Integer.parseInt(nextyear.getText())-1);
		Vector <Vector<String>> vec = null;
		String cmd = "select t2.id from verordn as t2 inner join pat5 as t1 on (t2.pat_intern = t1.pat_intern) where t2.befr='T' and t2.zzstatus='0' and (not t1.jahrfrei like '"+letztesjahr.getText()+"%')";
		vec = SqlInfo.holeFelder(cmd);
		if(vec.size() != umstellrezept){
			JOptionPane.showMessageDialog(null,"Die gestesteten Werte und die jetzt ermittelten Werte stimmen nicht überein!\nUmstellung wird nicht durchgeführt");
			return;
		}
		progress.setMinimum(0);
		progress.setMaximum(umstellrezept-1);
		progress.setStringPainted(true);
		int atmen = 0;
		for(int i = 0; i < umstellrezept;i++ ){
			progress.setValue(i);
			//cmd = "select * from verordn where id = '"+vec.get(i).get(0)+"' LIMIT 1";
			//SqlInfo.holeFelder(cmd);
			cmd = "update verordn set befr='F',rez_geb='0.00',rez_bez='F',zzstatus='2',jahrfrei='"+reset+"' where id='"+vec.get(i).get(0)+"' LIMIT 1";
			System.out.println(cmd);
			SqlInfo.sqlAusfuehren(cmd);
			atmen++;
			if(atmen > 10){
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				atmen = 0;
			}
		}
		//dann den Patstamm updaten mit einem updatebefehl
		cmd = "update pat5 set befreit='F',bef_ab=null,bef_dat=null,jahrfrei='"+reset+"' where befreit = 'T' and bef_dat like '"+reset+"%'";
		SqlInfo.sqlAusfuehren(cmd);
		//System.out.println(cmd);
		//dann die jahresabschluss Tabelle auf Vordermann bringen
		cmd = "update jahresabschluss set altesjahr='"+reset+"',umgestellt='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"'";
		SqlInfo.sqlAusfuehren(cmd);
		//System.out.println(cmd);
	}
	
	public void doTesten(){
		long tage = DatFunk.TageDifferenz(DatFunk.sHeute(), "01.01."+nextyear.getText());
		if(tage > 7){
			String meldung = "<html>Es sind noch <b>"+Long.toString(tage)+" Tage</b> bis zum Jahreswechsel!<br>"+
			"Die Umstellung <u>sollte</u> möglichst <b>zeitnah zum Jahreswechsel</b> durchgeführt werden!<br><br>"+
			"<b>Der <u>ideale</u> Zeitpunkt der Umstellung ist der letzte oder der erste Arbeitstag im Kalenderjahr!</b><br><br>"+
			"<b><font color='#ff0000'>Wenn Sie aber der Ansicht sind es müßte jetzt partout umgestellt werden - bitteschön!</font></b></html>";
			JOptionPane.showMessageDialog(null,meldung);
			//return;
		}
		
		Vector <Vector<String>> vec = null;
		String cmd = "select count(*) from pat5 where befreit = 'T' and (bef_dat like '"+letztesjahr.getText()+"%')";
		vec = SqlInfo.holeFelder(cmd);
		umstellpatient = Integer.parseInt(vec.get(0).get(0));
		
		String meldung = "Patienten die zurückgesetzt werden müssen: "+Integer.toString(umstellpatient)+"\n";
		
		cmd = "select count(*) from verordn as t2 inner join pat5 as t1 on (t2.pat_intern = t1.pat_intern) where t2.befr='T' and t2.zzstatus='0' and (not t1.jahrfrei like '"+letztesjahr.getText()+"%')";
		vec = SqlInfo.holeFelder(cmd);
		umstellrezept = Integer.parseInt(vec.get(0).get(0));
		
		meldung = meldung+"Rezepte die korrigiert werden müssen: "+Integer.toString(umstellrezept)+"\n";
		JOptionPane.showMessageDialog(null, meldung);
		

		buts[1].setEnabled(true);
		//buts[0].setEnabled(false);
		
	}

}
