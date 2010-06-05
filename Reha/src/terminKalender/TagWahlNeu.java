package terminKalender;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.WindowConstants;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import systemTools.JRtaTextField;
import systemTools.ListenerTools;
import systemTools.WinNum;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


public class TagWahlNeu extends RehaSmartDialog implements  FocusListener, ActionListener, ComponentListener, WindowListener, KeyListener,RehaTPEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6708633280442828667L;
	//private static TagWahlNeu thisClass = null;
	private String eigenName = null;
	private JXPanel jcc = null;
	private JXPanel jpan  = null;
	private RehaTPEventClass rtp = null;
	private MeinPicker datePick = null;
	public  JRtaTextField datum;
	private String akttag; 
	private String starttag;
	private JXLabel wochentag;
	private JXLabel kalwoche;	
	private JXButton okbut;
	private JXButton abbruchbut;
	//private String aktfocus = "";
	public TagWahlNeu(JXFrame owner, String name,String aktday){
		super(owner, "Eltern-TagWahl"+WinNum.NeueNummer());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		akttag = aktday;
		starttag = aktday;
		
		setPreferredSize(new Dimension(240,170));
		
		eigenName = "TagWahl"+WinNum.NeueNummer(); 
		this.setName(eigenName);
		getSmartTitledPanel().setPreferredSize(new Dimension(240,170));
		getSmartTitledPanel().setName("Eltern-"+eigenName);
		this.getParent().setName("Eltern-"+eigenName);
			
		this.setUndecorated(true);
		this.addFocusListener(this);
		this.addWindowListener(this);
		this.addKeyListener(this);
		
		/**jcc ist die Haupt-JXPanel**/

	
		jcc = new JXPanel(new GridLayout(1,1));
		jcc.setDoubleBuffered(true);
		jcc.setName(eigenName);
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
			     jcc.setBackgroundPainter(Reha.thisClass.compoundPainter.get("TagWahlNeu"));
				return null;
			}
			
		}.execute();
		
		//jcc.setBackground(Color.WHITE);
		jcc.setBorder(null);
		jcc.addKeyListener(this);
		jcc.addFocusListener(this);
		

		//okbut.setPreferredSize(new Dimension(abbruchbut.getWidth(),abbruchbut.getHeight() ));		

		
		this.setContentPanel(jcc );

		
		getSmartTitledPanel().setTitle("Anzeigetag auswählen");
		getSmartTitledPanel().getContentContainer().setName(eigenName);
		getSmartTitledPanel().addKeyListener(this);
		getSmartTitledPanel().validate();
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(eigenName);
		pinPanel.setzeName(eigenName);
		pinPanel.addKeyListener(this);

		setPinPanel(pinPanel);
		

		
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		
		jcc.add(getTagWahl(jpan = new JXPanel()));
		jpan.validate();
		jcc.validate();
		
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				datePick.getInputMap().clear();
				datePick.getActionMap().clear();
				datePick.getMonthView().getInputMap().clear();
				datePick.getMonthView().getActionMap().clear();

				KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
				datePick.getMonthView().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
				datePick.getMonthView().getActionMap().put("doSuchen", new kalenderAction());
				
				
			}
		});
		this.setAlwaysOnTop(true);
		this.setModal(true);
		validate();
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				setzeDatumFocus();
				//datum.requestFocus();
	 			  //datum.setCaretPosition(0);
			}
		});
	}

	public void setzeDatumFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		   if(! datum.hasFocus()){
		 			  datum.requestFocus();
		 			  datum.setCaretPosition(0);		 			  
		 		   }
		 	   }
		}); 	   		
	}


	private JXPanel getTagWahl(JXPanel jp){
										// 1  2  3   4
		FormLayout lay = new FormLayout("10px,right:p,5px,p",
			       //1.    2.   3.   4.  5.  6. 7.  8.
					"10dlu, p, 5dlu, p ,2dlu,p,6dlu,p");
					jp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
					jp.setOpaque(false);
					jp.setLayout(lay);
					CellConstraints cc = new CellConstraints();
					/*
					 */
					
					jp.add(new JXLabel("Datumspicker"),cc.xy(2,2));
					datePick = new MeinPicker();
					Calendar cal1 = new GregorianCalendar();
					String[] spl = starttag.split("\\.");
					cal1.set(new Integer(spl[2]),
							new Integer( (spl[1].substring(0,0).equals("0") ? spl[1].substring(1, 1) : spl[1]) )-1,
							
							new Integer( (spl[0].substring(0,0).equals("0") ? spl[0].substring(1, 1) : spl[0]) )
					);
					datePick.setDate(machePickerDatum(starttag));
					datePick.setEditor(new JRtaTextField("DATUM",false));
					datePick.getEditor().setText(starttag);
					datePick.getEditor().setEditable(false);
					datePick.getEditor().setBackground(Color.WHITE);
					datePick.getEditor().addActionListener(this);
					datePick.getEditor().addKeyListener(this);
					datePick.addActionListener(this);
					datePick.setName("datPick");
					datePick.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							////System.out.println("Date-Picker*************"+e);
						}
					});

					datePick.getMonthView().addKeyListener(new KeyAdapter(){
					
						
						public void keyTyped(KeyEvent arg0) { 
							////System.out.println("Woot"); 
						} 
					});
					datePick.getLinkPanel().addKeyListener(new KeyAdapter(){
						public void keyTyped(KeyEvent arg0) { 
							////System.out.println("Woot"); 
						} 
					}); 
					
					//datePick.addNotify();
					jp.add(datePick,cc.xy(4, 2));

					jp.add(new JXLabel("gewähltes Datum"),cc.xy(2,4));
					datum = new  JRtaTextField("DATUM",false);
					datum.setText(starttag);
					datum.setCaretPosition(0);
					datum.addKeyListener(this);
					datum.addFocusListener(this);
					datum.setName("datum");
					jp.add(datum,cc.xy(4,4));
					
					
					kalwoche = new JXLabel("KW-"+DatFunk.KalenderWoche(starttag) );
					kalwoche.setForeground(Color.RED);
					jp.add(kalwoche,cc.xy(2,6));					
					wochentag = new JXLabel(DatFunk.WochenTag(starttag));
					wochentag.setForeground(Color.RED);
					jp.add(wochentag,cc.xy(4,6));
					
					okbut = new JXButton(     "   ok    ");
					okbut.setActionCommand("ok");
					okbut.addActionListener(this);
					abbruchbut = new JXButton("abbrechen");
					abbruchbut.setActionCommand("abbruch");
					abbruchbut.addActionListener(this);

//					okbut.setPreferredSize(abbruchbut.getPreferredSize());
					jp.add(okbut,cc.xyw(1, 8,2));
					jp.add(abbruchbut,cc.xy(4, 8));

					//jp.add(abbruchbut,cc.xy(4, 8));
					
					jp.addKeyListener(this);
					jp.validate();
					//jp.requestFocus();
					//datum.requestFocusInWindow();
			

		return jp;
	}
	private Date machePickerDatum(String sdatum){
		Calendar cal1 = new GregorianCalendar();
		String[] spl = sdatum.split("\\.");
		cal1.set(new Integer(spl[2]),
				new Integer( (spl[1].substring(0,0).equals("0") ? spl[1].substring(1, 1) : spl[1]) )-1,
				
				new Integer( (spl[0].substring(0,0).equals("0") ? spl[0].substring(1, 1) : spl[0]) )
		);
		return cal1.getTime();
	}


public void setzeFocus(){
	jpan.requestFocus(true);
	datum.requestFocus();
	datum.setCaretPosition(0);
}
@Override
public void focusGained(FocusEvent arg0) {
	if( ((JComponent)arg0.getSource()).getName().equals("datum")){
		////System.out.println("focus erhalten "+arg0);
	}
	// TODO Auto-generated method stub
	////System.out.println("focus erhalten "+arg0);
	
}

@Override
public void focusLost(FocusEvent arg0) {
	if( ((JComponent)arg0.getSource()).getName() != null){
		if( ((JComponent)arg0.getSource()).getName().equals("datum")){
			////System.out.println("focus verloren "+arg0);
			try{
				String opposite = ((JComponent)arg0.getOppositeComponent()).getName();
				if( opposite.contains("TagWahl") || 
						opposite.contains("Spalte") ||
						opposite.contains("Combo")){
					datum.requestFocus();	
				}
			}catch(java.lang.NullPointerException ex){
				//dispose();
			}
		}
	}

	// TODO Auto-generated method stub
	//
	
}

@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	//System.out.println(arg0);
	if(arg0.getActionCommand().equals("datePickerCommit")){
		////System.out.println("Gew�hlt wurde das Datum: "+datePick.getEditor().getText().trim());
		akttag = datePick.getEditor().getText().trim();
		datum.setText(akttag );
		wochentag.setText(DatFunk.WochenTag(akttag) );
		kalwoche.setText("KW-"+DatFunk.KalenderWoche(akttag) );
		setzeFocus();
		//datum.setCaretPosition(0);
		//datum.requestFocus();
		zurueck();
		this.dispose();
		
	}
	if(arg0.getActionCommand().equals("ok")){
		new Thread(){
			public void run(){
				zurueck();				
			}
		}.start();
		this.setVisible(false);

		
	}
	if(arg0.getActionCommand().equals("abbruch")){
		this.dispose();
		setVisible(false);

	}
	
}
private void zurueck(){
	SwingUtilities.invokeLater(new Runnable(){
		public  void run(){
			setVisible(false);
			akttag = datum.getText().trim();
			
        	if(DatFunk.DatumsWert(akttag) > DatFunk.DatumsWert(Reha.kalMax)){
        		JOptionPane.showMessageDialog(null,"Sie versuchen hinter das Ende des Kalenders zu springen ("+akttag+")"+
        				"\nKalenderspanne aktuell = von "+Reha.kalMin+" bis "+Reha.kalMax);
        		
        	}else if(DatFunk.DatumsWert(akttag) < DatFunk.DatumsWert(Reha.kalMin)){
        		JOptionPane.showMessageDialog(null,"Sie versuchen vor den Beginn des Kalenders zu springen ("+akttag+")"+
        				"\nKalenderspanne aktuell = von "+Reha.kalMin+" bis "+Reha.kalMax);

        		
        	}else{
    			Reha.thisClass.terminpanel.datGewaehlt = akttag;
    			Reha.thisClass.terminpanel.suchSchonMal();
        	}

			
		}
	});
	this.dispose();	
	//FensterSchliessen(this.getName());
}
@Override
public void componentHidden(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void componentMoved(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void componentResized(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void componentShown(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyPressed(KeyEvent arg0) {
	////System.out.println(arg0.getKeyCode()+" - "+arg0.getSource()+"Key Event");	
	for(int i = 0;i<1;i++){
		if(arg0.getKeyCode() == 27){
			arg0.consume();
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.dispose();

			break;
			//FensterSchliessen(null);
		}
		if(arg0.getKeyCode() == 10){
			//if(arg0.getSource() instanceof JRtaTextField){
			arg0.consume();
				((JComponent) arg0.getSource()).requestFocus();
				////System.out.println(arg0.getKeyCode()+" - "+arg0.getSource()+"Soll Focus behalten");
			zurueck();
			//}	
			break;
		}
		if(arg0.getKeyCode() == 33){
			////System.out.println("tag + tag");
			akttag = DatFunk.sDatPlusTage(akttag,1);
			datePick.setDate(machePickerDatum(akttag));
			datum.setText(akttag);
			//wochentag.setText(datFunk.WochenTag(akttag));
			wochentag.setText(DatFunk.WochenTag(akttag) );
			kalwoche.setText("KW-"+DatFunk.KalenderWoche(akttag) );
			datum.setCaretPosition(0);
			datum.requestFocus();
			//arg0.consume();
			break;
		}
		if(arg0.getKeyCode() == 34){
			////System.out.println("tag - tag");
			akttag = DatFunk.sDatPlusTage(akttag,-1);
			datePick.setDate(machePickerDatum(akttag));
			datum.setText(akttag);
			//wochentag.setText(datFunk.WochenTag(akttag));
			wochentag.setText(DatFunk.WochenTag(akttag) );
			kalwoche.setText("KW-"+DatFunk.KalenderWoche(akttag) );
			datum.setCaretPosition(0);
			datum.requestFocus();
			//arg0.consume();
			break;
		}
	}

}	
@Override
public void keyReleased(KeyEvent e) {
	////System.out.println("Key event Released ");
	
	if(e.getKeyCode() == 10){
		e.consume();
			((JComponent) e.getSource()).requestFocus();
			zurueck();
	}	
	if(e.getKeyCode() == 27){
		e.consume();
		this.dispose();
	}	
}

@Override
public void keyTyped(KeyEvent e) {
	////System.out.println("Key event Released ");
	if(e.getKeyCode() == 10){
		e.consume();
		((JComponent) e.getSource()).requestFocus();

		zurueck();
	}	
	if(e.getKeyCode() == 27){
		e.consume();
		this.dispose();
	}	
}

public void rehaTPEventOccurred(RehaTPEvent evt) {
	String ss =  this.getName();
	try{
		if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
			FensterSchliessen(evt.getDetails()[0]);
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp = null;
		}	
	}catch(NullPointerException ne){

	}
}
@Override
public void windowClosed(WindowEvent arg0) {
	if(rtp != null){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		rtp = null;
	}
	if(datum != null){
		////System.out.println("Listeners von Tagwahl entfernen");
		ListenerTools.removeListeners(datum);
		datum = null;
		ListenerTools.removeListeners(okbut);
		okbut = null;
		ListenerTools.removeListeners(abbruchbut);
		abbruchbut = null;
		ListenerTools.removeListeners(this);
	}
	if(jcc != null){
		ListenerTools.removeListeners(jcc);
		jcc = null;
	}
}

@Override
public void windowClosing(WindowEvent arg0) {
	if(rtp != null){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		rtp = null;
	}	
}

public void FensterSchliessen(String welches){
	this.dispose();		
}	



}
class kalenderAction extends AbstractAction {
	
	


/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	////System.out.println("in Abstract Action" +arg0);	
}
}

class MeinPicker extends JXDatePicker implements KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	MeinPicker(){
		super();
		this.addKeyListener(this);
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("pressed");
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("released");
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("typed");
	}
	
}


