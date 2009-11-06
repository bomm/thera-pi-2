package hilfsFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import rehaContainer.RehaTP;
import systemEinstellungen.SysUtilDruckvorlage;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.RehaSmartDialog;

public class EmailText implements KeyListener, ActionListener, FocusListener{

	JRadioButton [] jrb = {null,null,null,null};
	JXButton [] jb = {null,null};
	JTextPane ta = null;
	ButtonGroup jrbg = new ButtonGroup();
	RehaSmartDialog rSmart = null;
	int iAktion = 1;
	public EmailText(){
		RehaTP jtp = new RehaTP(0); 
		jtp.setBorder(null);
		jtp.setTitle("EmailText");
		jtp.setContentContainer(getForm());
	    jtp.setVisible(true);


		rSmart = new RehaSmartDialog(null,"EmailText");
		rSmart.setModal(true);
		rSmart.setResizable(false);
		rSmart.setSize(new Dimension(650,400));
		rSmart.getTitledPanel().setTitle("Dieser Text wird im -->Emailtext<-- angezeigt wenn Sie einen Terminplan per Email versenden");
		rSmart.setContentPanel(jtp.getContentContainer());

		//int x = (screenSize.width - rSmart.getWidth()) / 2;
		//int y = (screenSize.height - rSmart.getHeight()) / 2;
		/****************************************************************/
		/****************************************************************/
		rSmart.setLocationRelativeTo(null); 
		rSmart.setVisible(true);
		SysUtilDruckvorlage.thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

				
	}

	private JXPanel getForm(){
 
		FormLayout layout = 
			new FormLayout("10dlu,p:g,10dlu",
			"10dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,5dlu");
			//new FormLayout("10dlu,p,4dlu,p,50dlu,p",
			//		"10dlu,p,3dlu,p,3dlu,p,3dlu,p");
		
		JXPanel xbuilder = new JXPanel();
		xbuilder.setBorder(null);
		xbuilder.setLayout(new BorderLayout());
		xbuilder.setVisible(true);
		//xbuilder.addFocusListener(this);
		xbuilder.addKeyListener(this);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.getPanel().setBackground(Color.WHITE);
		//builder.getPanel().setPreferredSize(new Dimension(400,150));
		builder.getPanel().setOpaque(true);
		CellConstraints cc = new CellConstraints();
		builder = new PanelBuilder(layout);
		ta = new JTextPane();
		ta.setFont(new Font("Courier New",Font.PLAIN,12));
		ta.setPreferredSize(new Dimension(600,280));

		String text = "";
		/*********/
		 File file = new File(Reha.proghome+"vorlagen/"+Reha.aktIK+"/EmailTerminliste.txt");
	      try {
	         // FileReader zum Lesen aus Datei
	         FileReader fr = new FileReader(file);
	         // Der String, der am Ende ausgegeben wird
	         String gelesen;
	         // char-Array als Puffer fuer das Lesen. Die
	         // Laenge ergibt sich aus der Groesse der Datei
	         char[] temp = new char[(int) file.length()];
	         // Lesevorgang
	         fr.read(temp);
	         // Umwandlung des char-Arrays in einen String
	         gelesen = new String(temp);
	         text = gelesen;
	         //Ausgabe des Strings
	         //System.out.println(gelesen);
	         // Ressourcen freigeben
	         fr.close();
	      } catch (FileNotFoundException e1) {
	         // die Datei existiert nicht
	         System.err.println("Datei nicht gefunden: ");
	      } catch (IOException e2) {
	         // andere IOExceptions abfangen.
	         e2.printStackTrace();
	      }
		/*********/
	      if (text.equals("")){
	    	  text = "Sehr geehrte Damen und Herren,\n"+
					"im Dateianhang finden Sie die von Ihnen gewünschten Behandlungstermine.\n\n"+
					"Termine die Sie nicht einhalten bzw. wahrnehmen können, müßen 24 Stunden vorher\n"+
					"abgesagt werden.\n\nIhr Planungs-Team vom RTA";
	      }
		ta.setText(text);
		
		JPanel tapan = new JPanel(new BorderLayout());
		tapan.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
				tapan.add(new JScrollPane(ta),BorderLayout.CENTER);

		builder.add(tapan,cc.xy(2,2));
		xbuilder.add(builder.getPanel(),BorderLayout.NORTH);
		
		FormLayout lay = 
			new FormLayout("10dlu,p,25dlu,p,50dlu,p",
					"0dlu,p,10dlu,p,3dlu,p,3dlu,p,15dlu,p");
		
		PanelBuilder build = new PanelBuilder(layout);
		build.getPanel().setLayout(lay);
		build.getPanel().setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		CellConstraints ccx = new CellConstraints();
		jb[0] = new JXButton("Speichern");
		jb[0].setActionCommand("Speichern"); 
		jb[0].setMnemonic(KeyEvent.VK_S);
		jb[0].addKeyListener(this);
		jb[0].addActionListener(this);
		jb[0].setPreferredSize(new Dimension (75, jb[0].getPreferredSize().height));
		build.add(jb[0],ccx.xy(2,2));		

		jb[1] = new JXButton("Abbruch");
		jb[1].setActionCommand("Abbruch");		
		jb[1].addKeyListener(this);
		jb[1].addActionListener(this);
		jb[1].setPreferredSize(new Dimension (75, jb[0].getPreferredSize().height));
		build.add(jb[1],cc.xy(4,2));		
		
		build.add(new JXLabel(""),cc.xy(4,3));

		xbuilder.add(build.getPanel(),BorderLayout.CENTER);
		return xbuilder;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==10){
			//String [] sret = {null,null};
			rSmart.dispose();
		}
		if(e.getKeyCode()==27){
			//String [] sret = {null,null};
			rSmart.dispose();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println(arg0.getSource());
		//String sAktion = ((AbstractButton) arg0.getSource()).getText();
		//System.out.println("In Text abspeichern");
		//System.out.println("ActionCommand = "+arg0.getActionCommand());
		for (int i = 0 ; i < 1 ; i++){

			if(arg0.getActionCommand().equals("Speichern")){
		
				FileWriter w = null;

				 try {
				        w = new FileWriter(Reha.proghome+"vorlagen/"+Reha.aktIK+"/EmailTerminliste.txt");
				        w.write(ta.getText());
				 
				    } catch (IOException e) {
				        e.printStackTrace();
				 
				    } finally {
				        if (w != null) {
				            try {
				                w.close();
				            } catch (IOException e) {
				                // TODO Auto-generated catch block
				                e.printStackTrace();
				            }
				        }
				    }
				rSmart.dispose();				    
				break;
			}
			if(arg0.getActionCommand().equals("Abbruch")){
				rSmart.dispose();
				break;
			}
		}
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		//System.out.println(arg0);
		if(arg0.getSource() instanceof JRadioButton){
			((AbstractButton) arg0.getSource()).setSelected(true);
			String sAktion = ((AbstractButton) arg0.getSource()).getText(); 
			for (int i = 0 ; i < 1 ; i++){
				if( (sAktion =="Termin auf verfügbare Dauer kürzen")){
					iAktion = 1;
					break;
				}
				if( (sAktion== "Nachfolgenden Termin kürzen")){
					iAktion = 2;
					break;
				}
			}
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
