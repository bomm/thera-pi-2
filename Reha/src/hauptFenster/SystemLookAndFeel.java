package hauptFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import systemTools.Colors;
import terminKalender.TerminFenster;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


public class SystemLookAndFeel extends JScrollPane  implements ActionListener, ContainerListener, ComponentListener, RehaTPEventListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String laf = "";  //  @jve:decl-index=0:
	private JXPanel jLFPanel = null;
	private JXPanel jFormPanel = null;
	private SystemLookAndFeel thisScroll;
	private int setOben;
	private RehaTPEventClass xEvent;
	public SystemLookAndFeel thisClass;
	public SystemLookAndFeel(int setOben){
		super();
	
		this.setOben = setOben;
		this.setName("LookAndFeelTP");
		
		setBorder(null);
		setViewportBorder(null);
		
		xEvent = new RehaTPEventClass();
		xEvent.addRehaTPEventListener((RehaTPEventListener)this);
		
		this.setOben = setOben;
		
		jLFPanel = new JXPanel();
		jLFPanel.setBorder(null);
		jLFPanel.setLayout(new BorderLayout());
		jLFPanel.getInsets(new Insets(50,50,50,50));
		jLFPanel.setPreferredSize(new Dimension(100,100));
	    jLFPanel.addComponentListener(this);
	    if(setOben==1){
	    	Reha.jEventTargetOben = jLFPanel;
	    }else if(setOben==2){
	    	Reha.jEventTargetUnten = jLFPanel;
	    }
	    	
	    
		
		JButton jB1 = new JButton("Metall");
	    jB1.addActionListener( this);
	    
		JButton jB2 = new JButton("Motif");
	    jB2.addActionListener( this);

		JButton jB3 = new JButton("Windows");
	    jB3.addActionListener( this);
		
		JButton jB4 = new JButton("Plastic");
	    jB4.addActionListener( this);

	    JButton jB5 = new JButton("Leaf");
	    jB5.addActionListener( this);

	    JRadioButton jxRB = new JRadioButton();
	    jxRB.setText("RadioButton");
	    jxRB.setForeground(Color.WHITE);
	    jxRB.setOpaque(false);
	    
	    JCheckBox jxCB = new JCheckBox();
	    jxCB.setText("Ich bin eine Checkbox");
	    jxCB.setForeground(Color.WHITE);	    
	    jxCB.setOpaque(false);
	    
	    JLabel jlbl = new JLabel("So sehen die Schriften aus");
	    jlbl.setForeground(Color.WHITE);	    
	    jlbl.setOpaque(false);	    

	    
	    
	    FormLayout layout = new FormLayout("80dlu,80dlu ,4dlu, 80dlu,4dlu,80dlu,4dlu,80dlu,4dlu",
	    							"30dlu,30dlu,30dlu");
	    this.jFormPanel = new JXPanel(layout);
	    this.jFormPanel.setBorder(null);
	    this.jFormPanel.setOpaque(false);
	    this.jFormPanel.setBackgroundPainter(Reha.RehaPainter[0]);
	    CellConstraints cc = new CellConstraints();
	    this.jFormPanel.add(jB1,cc.xy(2,1));
	    this.jFormPanel.add(jB2,cc.xy(4,1));
	    this.jFormPanel.add(jB3,cc.xy(6,1));
	    this.jFormPanel.add(jB4,cc.xy(8,1));	    
	    this.jFormPanel.add(jB5,cc.xy(2,2));
	    this.jFormPanel.add(jlbl,cc.xy(4,2));
	    this.jFormPanel.add(jxRB,cc.xy(6,2));
	    this.jFormPanel.add(jxCB,cc.xy(8,2));
	    this.jFormPanel.setVisible(true);
	    //System.out.println("StartWeite="+this.jFormPanel.getWidth()+ "Höhe="+this.jFormPanel.getHeight());
//	    this.jFormPanel.setSize(1000,600);
	    this.jFormPanel.setPreferredSize(new Dimension(600,100));

	    jLFPanel.add(jFormPanel,BorderLayout.CENTER);



	    this.jFormPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				jLFPanel.setPreferredSize(new Dimension(e.getComponent().getMinimumSize().width,e.getComponent().getMinimumSize().height));
				//e.getComponent().resetToPreferredSizes();
				//System.out.println("Weite="+e.getComponent().getSize().width+" Höhe="+e.getComponent().getSize().height);
				e.getComponent().setPreferredSize(new Dimension(e.getComponent().getMinimumSize().width,e.getComponent().getMinimumSize().height));
				//e.getComponent().validate();
				jLFPanel.revalidate();
				//thisScroll.repaint();
				//System.out.println("Privater-Listener:"+e);
			}
		});

	    //this.jFormPanel.addComponentListener(this);
		

	    JXPanel x2Panel = new JXPanel(new FlowLayout());
		x2Panel.setBackground(Color.WHITE);

		JButton jB6 = new JButton("Look übernehmen und zurück");
	    jB6.addActionListener( this);
		x2Panel.add(jB6);
		
		JButton jB7 = new JButton("Abbrechen");
	    jB7.addActionListener( this);
		x2Panel.add(jB7);
		
		jLFPanel.add(x2Panel,BorderLayout.SOUTH);
		
		JScrollPane jscr = new JScrollPane();
		jscr.setBorder(null);
		jscr.setViewportBorder(null);
		jscr.setViewportView(jLFPanel);
		jscr.setVisible(true);
		this.setViewportView(jscr);
		
		//this.setViewportView(jLFPanel);
//		this.setViewportView(this.jLFPanel);

		
		this.addContainerListener(this);
		this.setVisible(true);
		this.revalidate();
		this.thisScroll = this;
	    //System.out.println("Zweite Weite="+this.jFormPanel.getWidth()+ "Höhe="+this.jFormPanel.getHeight());
	}

	  public void actionPerformed(ActionEvent event) {

		    String cmd = event.getActionCommand();
		    //System.out.println(cmd);
		    try {

		      // Look and Feel auswählen

		      if (laf.equals("")){
		    	  laf = Reha.aktLookAndFeel;
		    	  //System.out.println("Laf = "+laf);
		      }  

		      if (cmd.equals("Metall")) {
		    	  laf = "javax.swing.plaf.metal.MetalLookAndFeel";

		      } else if (cmd.equals("Motif")) {

		        laf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

		      } else if (cmd.equals("Windows")) {

		        laf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

		      }
		      else if (cmd.equals("Plastic")) {

			        laf = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";

			      }
		      else if (cmd.equals("Leaf")) {
			      }

		      		      
		      else if (cmd.equals("Look übernehmen und zurück")) {
		    	  Reha.thisClass.setzeUi(laf, this);
		    	  JOptionPane.showMessageDialog(Reha.thisFrame, "Sie haben das Look & Feel auf Ihre Bedürfnisse angepaßt.\n"+
		                    "Es wird dringend geraten die Anwendung neu zu starten\n\n"+
		                    "Die Anwendungslogik wird fehlerfrei weiterlaufen, aber das Aussehen wird vermutlich\n"+
		                    "etwas 'strange' erscheinen......");
		    	  FensterSchliessen(((JXTitledPanel) this.getParent()).getContentContainer().getName());
		    	  /*
		    	  if(setOben==1){
		    	  		Reha.jInhaltOben.setVisible(false);
		    	  		Reha.jContainerOben.remove(Reha.jInhaltOben);
		    	  		Reha.jInhaltOben = null;
		    	  		Reha.jEventTargetOben = null;
		    	  		Reha.thisClass.setzeLeerOben();
		    	  		Reha.jContainerOben.validate();
		    	  		SwingUtilities.updateComponentTreeUI(Reha.jLeerOben);
		    	  }else if(setOben==2){
			    	  	Reha.jInhaltUnten.setVisible(false);
			    	  	Reha.jContainerUnten.remove(Reha.jInhaltUnten);
			    	  	Reha.jInhaltUnten = null;
			    	  	Reha.jEventTargetUnten = null;
			    	  	Reha.thisClass.setzeLeerUnten();
			    	  	Reha.jContainerUnten.validate();
		    	  }
		    	  */
			  }
		      else if (cmd.equals("Abbrechen")) {
		    	  	FensterSchliessen(((JXTitledPanel) this.getParent()).getContentContainer().getName());
		    	  	/*
		    	  	xEvent.removeRehaTPEventListener(this);
					Reha.thisClass.TPschliessen(setOben,null);

	    	  		*/
			      }


		      //Look and Feel umschalten

		      UIManager.setLookAndFeel(laf);

		      SwingUtilities.updateComponentTreeUI(this);
		      this.revalidate();
		      Toolkit.getDefaultToolkit().beep();

		    } catch (UnsupportedLookAndFeelException e) {

		      System.err.println(e.toString());

		    } catch (ClassNotFoundException e) {

		      System.err.println(e.toString());

		    } catch (InstantiationException e) {

		      System.err.println(e.toString());

		    } catch (IllegalAccessException e) {

		      System.err.println(e.toString());

		    }

		  }
	  
	    private String getLAFInfo() { 
	        String s = "";

	        // Build string from headings and info from UI Manager

	        s = s.concat("Current look and feel:\n");
	        s = s.concat(((String)UIManager.getLookAndFeel().getName()));
	        
	        s = s.concat("\nInstalled look and feels:\n");
	UIManager.LookAndFeelInfo info[]
	            = UIManager.getInstalledLookAndFeels();
	        for(int i = 0; i < info.length; i++) { 
	            s = s.concat(info[i].getName() + " ");
	        } 

	        s = s.concat("\nAuxiliary look and feels:\n");
	        LookAndFeel auxinfo[] = UIManager.getAuxiliaryLookAndFeels();
	        if (auxinfo != null) { 
	            for(int i = 0; i < auxinfo.length; i++) { 
	                s = s.concat(auxinfo[i].getName());
	            } 
	        } 
	        else { 
	            s = s.concat("None\n");
	        } 

	        s = s.concat("\nCross-platform look and feel class name:\n");
	        s = s.concat(UIManager.getCrossPlatformLookAndFeelClassName());

	        s = s.concat("\nSystem look and feel class name:\n");
	        s = s.concat(UIManager.getSystemLookAndFeelClassName());
	        
	        return(s);
	    }
/**********
 * ContainerEvents
 * *************************/
	    
		@Override
		public void componentAdded(ContainerEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentRemoved(ContainerEvent arg0) {
			// TODO Auto-generated method stub
		} 	  
		public void componentResized(ContainerEvent arg0) {
			// TODO Auto-generated method stub
			//System.out.println("Component L&F: "+arg0);
			arg0.getComponent().validate();
			
		}
/**********
* ComponentEvents
* *************************/

		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
				//jLFPanel.revalidate();
				//System.out.println("jFormPanel - L&F : "+e);
		}

		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void RehaTPEventOccurred(RehaTPEvent evt) {
			if (evt.getDetails()[0] == ((JXTitledPanel) this.getParent()).getContentContainer().getName()){
				FensterSchliessen(evt.getDetails()[0]);
			}	
		}

		public void FensterSchliessen(String fname){
			xEvent.removeRehaTPEventListener((RehaTPEventListener) this);
			//System.out.println(this.getParent().getParent().getParent().getParent().getParent());				
			//Reha.thisClass.TPschliessen(setOben,(Object) this.getParent().getParent().getParent().getParent().getParent(),fname);
	
		}

}
