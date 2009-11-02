package entlassBerichte;

import hauptFenster.Reha;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXPanel;

import patientenFenster.PatGrundPanel;

import systemTools.JCompTools;
import terminKalender.DatFunk;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class EBerichtTab {
	EBerichtPanel eltern = null;
	JTabbedPane tab = null;
	Eb1 seite1 = null;
	Eb2 seite2 = null;
	Eb3 seite3 = null;
	Eb4 seite4 = null;
	public EBerichtTab(EBerichtPanel xeltern){
		eltern = xeltern;
		tab = new JTabbedPane();
		tab.setUI(new WindowsTabbedPaneUI());
		seite1 = new Eb1(eltern);
		tab.addTab("E-Bericht Seite-1", seite1.getSeite());
		seite2 = new Eb2(eltern);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(seite2.getSeite());		
		tab.addTab("E-Bericht Seite-2", jscr);
		
		seite3 = new Eb3(eltern);
		tab.addTab("E-Bericht Freitext", seite3.getSeite());
		
		seite4 = new Eb4(eltern);
		jscr = JCompTools.getTransparentScrollPane(seite4.getSeite());		
		tab.addTab("E-Bericht KTL", jscr);
		/*
		String bisher = eltern.jry.getTitle();
		System.out.println("Bisheriger Titel = "+bisher);
		bisher = bisher.replaceAll("</html>", "");
		bisher = bisher.replaceAll("<html>", "");
		String titel = "<html>";
		titel = titel+ bisher;
		titel = titel+"<b><font color='#ff0000'> [Patient: "+PatGrundPanel.thisClass.patDaten.get(2)+", "+
		PatGrundPanel.thisClass.patDaten.get(3)+" geb. am:"+
		datFunk.sDatInDeutsch(PatGrundPanel.thisClass.patDaten.get(4))+ 
		(eltern.neu ? " (Neuanlage)]</font></b></html>" : " (Bericht-ID:"+eltern.berichtid+")]</font></b></html>");
		eltern.jry.setTitle(titel);
		*/
		String bisher = eltern.jry.getTitle();
		System.out.println("Bisheriger Titel = "+bisher);
		bisher = bisher.replaceAll("</html>", "");
		bisher = bisher.replaceAll("<html>", "");
		String titel = "";
		titel = titel+ bisher;
		titel = titel+"  [Patient: "+Reha.thisClass.patpanel.patDaten.get(2)+", "+
		Reha.thisClass.patpanel.patDaten.get(3)+" geb. am:"+
		DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4))+ 
		(eltern.neu ? "   (Neuanlage)]" : "   (Bericht-ID:"+eltern.berichtid+")]");
		eltern.jry.setTitle(titel);

	}
	public JTabbedPane getTab(){
		tab.validate();
		return tab;
		
	}
	public Eb1 getTab1(){
		return seite1;
	}
	public Eb2 getTab2(){
		return seite2;
	}
	public Eb3 getTab3(){
		return seite3;
	}
	public Eb4 getTab4(){
		return seite4;
	}

}
