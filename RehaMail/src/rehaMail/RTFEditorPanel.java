package rehaMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import org.jdesktop.swingx.JXPanel;

import CommonTools.ButtonTools;
import CommonTools.JCompTools;

public class RTFEditorPanel extends JXPanel implements FocusListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7519372791799588835L;
	RTFEditorKit rtfEditor = null;
	JTextPane editorArea = null;
	JButton[] buts = {null,null,null,null,null};
	ActionListener al = null;
	JComboBox box1 = null;
	JComboBox box2 = null;
	
	public int caretposition = -1;
	public int selstart = -1;
	public int selend = -1;
	
	public RTFEditorPanel(boolean showtoolbar,boolean editable,boolean mitsavebutton){
		super(new BorderLayout());
		setOpaque(false);
		activateListener();
		if(showtoolbar){
			add(getToolBar(mitsavebutton),BorderLayout.NORTH);			
		}
		add(getEditorPane(),BorderLayout.CENTER);
		caretposition = 0;
		editorArea.requestFocus();
		editorArea.setEditable(editable);
		validate();
	}
	
	private JToolBar getToolBar(boolean mitsavebutton){
		JXPanel pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		JToolBar bar = new JToolBar();
		bar.setOpaque(false);
		bar.setRollover(true);
		//bar.setBorder(null);
		bar.addSeparator(new Dimension(0,30));
		
		//bar.add((buts[0]=ButtonTools.macheButton("<html><div style='width: 25px; height: 25px;'><b>B</b></div></html>", "fett", al)) );

		bar.add((buts[0]=ButtonTools.macheButton("", "fett", al)) );
		buts[0].setIcon(new ImageIcon(RehaMail.progHome+"icons/BoldHK.png"));
		buts[0].addActionListener(new StyledEditorKit.BoldAction());
		bar.addSeparator(new Dimension(20,0));
		
		bar.add((buts[1]=ButtonTools.macheButton("", "kursiv", al)) );
		buts[1].setIcon(new ImageIcon(RehaMail.progHome+"icons/ItalicHK.png"));
		buts[1].addActionListener(new StyledEditorKit.ItalicAction());
		bar.addSeparator(new Dimension(20,0));
		
		bar.add((buts[2]=ButtonTools.macheButton(" U ", "unterstrichen", al)) );
		buts[2].addActionListener(new StyledEditorKit.UnderlineAction());
		bar.addSeparator(new Dimension(20,0));
		
		bar.add((buts[3]=ButtonTools.macheButton("", "farbe", al)) );
		buts[3].setIcon(new ImageIcon(RehaMail.progHome+"icons/RotHK.png"));
		bar.addSeparator(new Dimension(20,0));
		
		//GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//String[] alleFonts = ge.getAvailableFontFamilyNames();
		box1 = new JComboBox(new String[] {"Arial", "Courier New"});
		box1.setActionCommand("schriftart");
		box1.addActionListener(al);
		box1.setMaximumSize(new Dimension(120,25));
		box1.setSelectedItem("Arial");
		bar.add(box1);
		
		bar.addSeparator(new Dimension(10,0));
		
		box2 = new JComboBox(new String[] {"8","10","12","14","16","18","20"});
		box2.setActionCommand("groesse");
		box2.addActionListener(al);
		box2.setMaximumSize(new Dimension(40,25));
		box2.setSelectedItem("16");
		bar.add(box2);
		
		if(mitsavebutton){
			bar.addSeparator(new Dimension(20,0));
			bar.add( (buts[4]=ButtonTools.macheButton("", "speichern", al)));
			buts[4].setIcon(new ImageIcon(RehaMail.progHome+"icons/SaveHK.png"));
		}

		return bar;
	}
	
	
	
	private JScrollPane getEditorPane(){
		rtfEditor = new RTFEditorKit();
		editorArea = new JTextPane();
		editorArea.addFocusListener(this);
		
		JScrollPane pan = JCompTools.getTransparentScrollPane(editorArea);
		
		editorArea.setEditorKit(rtfEditor);
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attr,"Arial");
		editorArea.setCharacterAttributes(attr, false);
		StyleConstants.setFontSize(attr, 16);
		editorArea.setCharacterAttributes(attr, false);
		pan.validate();
		return pan;
	}
	
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("fett")){
					setCaret();
					return;
				}else if(cmd.equals("kursiv")){
					setCaret();
					return;
				}else if(cmd.equals("farbe")){
					doFarbe();
				}else if(cmd.equals("groesse")){
					setCaret();
					doSchriftGroesse();
					
				}else if(cmd.equals("schriftart")){
					setCaret();
					doSchriftArt();
				}else if(cmd.equals("unterstrichen")){
					setCaret();
					return;
				}else if(cmd.equals("speichern")){
						RehaMail.thisClass.getMTab().getToDoPanel().doSpeichern();
				}
				
			}
			
		};
	}
	private RTFEditorPanel getInstance(){
		return this;
	}
	private void doFarbe(){
		Color color = JColorChooser.showDialog(this, "Color Chooser", Color.cyan);
		if (color != null) {
			
			if(selend > 0 && selend != selstart){
				final Color xcolor = color;
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						editorArea.removeFocusListener(getInstance());
						//editorArea.select(selstart, selend);
						editorArea.setSelectionStart(selstart);
						editorArea.setSelectionEnd(selend);
						MutableAttributeSet attr = new SimpleAttributeSet();
						StyleConstants.setForeground(attr, xcolor);
						editorArea.setCharacterAttributes(attr, false);
						
						editorArea.select(selend, editorArea.getDocument().getLength());
						StyleConstants.setForeground(attr, Color.BLACK);
						
						editorArea.setCharacterAttributes(attr, false);
						
						editorArea.setCaretPosition(selend);

						editorArea.requestFocus();
						
						editorArea.addFocusListener(getInstance());

					}
				});

			}else{
				editorArea.setCaretPosition(caretposition);
				MutableAttributeSet attr = new SimpleAttributeSet();
				StyleConstants.setForeground(attr, color);
				editorArea.setCharacterAttributes(attr, false);

			}
			
            
		}
	}
	
	public void doSchriftArt(){
		if(caretposition < 0){return;}
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attr,box1.getSelectedItem().toString());
		editorArea.setCharacterAttributes(attr, false);	
	}
	public void doSchriftGroesse(){
		if(caretposition < 0){return;}
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr,Integer.parseInt(box2.getSelectedItem().toString()));
		editorArea.setCharacterAttributes(attr, false);	
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		//System.out.println("Focus bekommen");
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		caretposition = editorArea.getCaretPosition();
		selstart = editorArea.getSelectionStart();
		selend = editorArea.getSelectionEnd();
		//System.out.println("Focus verloren / CaretPosition = "+caretposition);
		//System.out.println("Focus verloren / Selection = "+selstart+" - "+selend);
 
	}
	private void setCaret(){
		if(caretposition < 0){return;}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				editorArea.requestFocus();
				if(selend > 0){
					//editorArea.setSelectionStart(selstart);
					//editorArea.setSelectionEnd(selend);
					editorArea.select(selstart, selend);
				}else{
					editorArea.setCaretPosition(caretposition);	
				}
						
			}
		});
	}
	public void allesEinschalten(){
		for(int i = 0; i < 4; i++){buts[i].setEnabled(true);}
		box1.setEnabled(true);
		box2.setEnabled(true);
		editorArea.setEditable(true);
		editorArea.requestFocus();
		editorArea.setCaretPosition(0);
	}
	public void allesAusschalten(){
		for(int i = 0; i < 4; i++){buts[i].setEnabled(false);}
		box1.setEnabled(false);
		box2.setEnabled(false);
		editorArea.setEditable(false);
	}
	

}
