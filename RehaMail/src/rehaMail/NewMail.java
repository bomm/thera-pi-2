package rehaMail;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;

import Tools.ButtonTools;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.internal.frame.LayoutManager;

public class NewMail extends JFrame  implements WindowListener  {
	
	boolean neu;
	
	private IFrame             officeFrame       = null;
	public	ITextDocument      document          = null;
	private JPanel             noaPanel          = null;
	private JXPanel			noaDummy = null;
	NativeView nativeView = null;
	
	ActionListener al = null;
	
	JButton[] buts = {null,null,null,null,null};
	
	public NewMail(String title,boolean neu,Point pt ){
		super();
		this.neu = neu;
		addWindowListener(this);
		setSize(750,400);
		setPreferredSize(new Dimension(750,400));
		activateListener();
		getContentPane().add (getContent());
		setLocation(pt);
		setTitle(title);
		pack();
		setVisible(true);

	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("senden")){
					
				}
			}
			
		};
	}
	private JXPanel getContent(){
		JXPanel pan = new JXPanel();
		pan.validate();
		String xwerte = "fill:0:grow(1.0)";
		String ywerte = "p,5dlu,fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.add(getToolbar(),cc.xy(1,1));
		pan.add(getnoaDummy(),cc.xy(1, 3));
		noaDummy.setVisible(true);
		noaDummy.add(getOOorgPanel(),BorderLayout.CENTER);

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				while(RehaMail.officeapplication==null){
				}

				while(!RehaMail.officeapplication.isActive()){
					Thread.sleep(75);
				}
				fillNOAPanel();
				validate();
				nativeView.requestFocus();
				document.getTextService().getCursorService().getTextCursor().gotoStart(true);
				setVisible(true);
				return null;
			}
			
		}.execute();

		pan.validate();
		return pan;
	}
	private JToolBar getToolbar(){
		JToolBar bar = new JToolBar();
		bar.addSeparator(new Dimension(50,30));
		bar.add(buts[0]=ButtonTools.macheButton("senden", "senden", al));
		return bar;
	}
	private JPanel getOOorgPanel(){
		noaPanel = new JPanel(new GridLayout());
		noaPanel.setPreferredSize(new Dimension(1024,800));
		noaPanel.validate();
		return noaPanel;
	}
	private JXPanel getnoaDummy(){
		noaDummy = new JXPanel(new GridLayout(1,1));
		return noaDummy;
	}
 

	private void fillNOAPanel() {
	    if (noaPanel != null) {
		      try {
		        officeFrame = constructOOOFrame(RehaMail.officeapplication, noaPanel);

		        document = (ITextDocument) RehaMail.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		            IDocument.WRITER,
		            DocumentDescriptor.DEFAULT);

	        	Tools.OOTools.setzePapierFormat(document, new Integer(25199), new Integer(19299));
	        	Tools.OOTools.setzeRaender(document, new Integer(10), new Integer(10),new Integer(10),new Integer(10));
		        //hideElements(LayoutManager.URL_MENUBAR);
		        //hideElements(LayoutManager.URL_STATUSBAR);
	        	nativeView.validate();
		        try {
					document.zoom(DocumentZoomType.BY_VALUE, (short)90);
				} catch (DocumentException e) {
					e.printStackTrace();
				}
		        noaPanel.setVisible(true);		      }
		      catch (Throwable throwable) {
		        noaPanel.add(new JLabel("Ein Fehler ist aufgetreten: " + throwable.getMessage()));
		      }
		    }
		  }

	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
	    nativeView = new NativeView(RehaMail.officeNativePfad);
	    parent.add(nativeView);
	    parent.addComponentListener(new ComponentAdapter() {
	      public void componentResized(ComponentEvent e) {
	    	refreshSize();
	        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	        parent.getLayout().layoutContainer(parent);
	      }
	    });

	    nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	    parent.getLayout().layoutContainer(parent);
	    officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    return officeFrame;
	}
	private void hideElements(String url ) throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException, NOAException{
	    ILayoutManager layoutManager = officeFrame.getLayoutManager();
	    XLayoutManager xLayoutManager = layoutManager.getXLayoutManager();
	    XUIElement element = xLayoutManager.getElement(url);
	    if (element != null) {
	        XPropertySet xps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, element);
	        xps.setPropertyValue("Persistent", new Boolean(false));
	        xLayoutManager.hideElement(url);
	    }
	}

	public final void refreshSize() {
		noaPanel.setPreferredSize(new Dimension(noaPanel.getWidth() , noaPanel.getHeight()- 5));
		final Container parent = noaPanel.getParent();
		if (parent instanceof JComponent) {
			((JComponent) parent).revalidate();
		}
		final Window window1 = SwingUtilities.getWindowAncestor(nativeView.getParent().getParent());
		if (window1 != null) {
			window1.validate();
		}
		noaPanel.getLayout().layoutContainer(noaPanel);

	}




	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(document != null){
			document.close();
			System.out.println("Dokument wurde geschlossen");
			document = null;
		}
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
