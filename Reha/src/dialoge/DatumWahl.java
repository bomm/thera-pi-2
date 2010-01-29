package dialoge;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.FormLayout;

import rehaContainer.RehaTP;
import terminKalender.TerminFenster;

public class DatumWahl implements KeyListener, ActionListener, FocusListener{

	JRadioButton [] jrb = {null,null,null,null};
	JXButton [] jb = {null,null};
	ActionListener al;
	ButtonGroup jrbg = new ButtonGroup();
	RehaSmartDialog rSmart = null;
	int iAktion = 1;
	JXPanel tv;
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	String aktTag = "x";
	String wahlTag = "y";
	public DatumWahl(int x,int y){
		RehaTP jtp = new RehaTP(0); 
		jtp.setBorder(null);
		jtp.setTitle("Wohin mit dem Termin???");
		jtp.setContentContainer(getForm());
	    jtp.setVisible(true);


		rSmart = new RehaSmartDialog(null,"WohinmitTermin");
		
		rSmart.setModal(false);
		rSmart.setAlwaysOnTop(true);
		rSmart.setResizable(false);

		rSmart.setSize(new Dimension(225,200));
		rSmart.setPreferredSize(new Dimension(225,200));
		rSmart.getTitledPanel().setTitle("Monatsübersicht");
		rSmart.setContentPanel(jtp.getContentContainer());
		/****************************************************************/
		/*
		tv = TerminFenster.getThisClass().getViewPanel();
		int xvp = tv.getLocationOnScreen().x+tv.getWidth();
		if((x+225+10) > xvp){
			x=x-225;
		}
		int yvp = tv.getLocationOnScreen().y+tv.getHeight();
		if(y+145 > yvp){
			y=y-145;
		}
		*/
		x=20;
		y=500;
		/****************************************************************/
		rSmart.setLocation(x, y); 
		rSmart.pack();
		rSmart.setVisible(true);

		//jrb[0].requestFocus();		
	}
	
	private JXPanel getForm(){
		 
		FormLayout layout = 
			new FormLayout("10dlu,p,10dlu,p,2dlu,p,100dlu,40dlu",
			"10dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,5dlu");
			//new FormLayout("10dlu,p,4dlu,p,50dlu,p",
			//		"10dlu,p,3dlu,p,3dlu,p,3dlu,p");
		JXPanel xbuilder = new JXPanel();
		xbuilder.setBorder(null);
		xbuilder.setLayout(new BorderLayout());
		xbuilder.setVisible(true);
		final JXMonthView monthView = new JXMonthView ();
		  monthView.setPreferredColumnCount (1);
		  monthView.setPreferredRowCount (1);
		  monthView.setTraversable(true);
		  monthView.setShowingWeekNumber(true);
		  al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(TerminFenster.getThisClass()!= null){
					Date dat = monthView.getSelectionDate();
					if(dat==null){
						return;
					}
					wahlTag = sdf.format(monthView.getSelectionDate());
					if(wahlTag.equals(aktTag)){
						return;
					}
					aktTag = wahlTag;
					Reha.thisClass.progLoader.ProgTerminFenster(1, 0);
					TerminFenster.getThisClass().springeAufDatum(aktTag);
				}else{
					Date dat = monthView.getSelectionDate();
					if(dat==null){
						return;
					}
					wahlTag = sdf.format(monthView.getSelectionDate());
					if(wahlTag.equals(aktTag)){
						return;
					}
					aktTag = wahlTag;
					Reha.thisClass.progLoader.ProgTerminFenster(1, 0);
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							TerminFenster.getThisClass().springeAufDatum(aktTag);
							
						}
					});
				}
				
			}
		  };
		  monthView.addActionListener(al);
		  xbuilder.add(monthView,BorderLayout.CENTER);
		return xbuilder;
	}	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}	
