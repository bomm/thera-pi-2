package rtaWissen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;

import chrriis.common.Utils;

public class SourceFenster extends JXPanel {
   
		  
		  private static final String LS = Utils.LINE_SEPARATOR;   
		  
		  public SourceFenster(StringBuffer buf) {   
		    super(new BorderLayout());   

		    TextArea tx = new TextArea();
		    tx.setFont(new Font("Courier",Font.PLAIN,12));
		    tx.setForeground(Color.RED);
		    tx.setText(buf.toString());
		    JScrollPane sorce = new JScrollPane(tx);
		    add(sorce,BorderLayout.CENTER);
		    /*
		    final JSyntaxHighlighter syntaxHighlighter = new JSyntaxHighlighter();   
		    syntaxHighlighter.setContent(buf.toString() , JSyntaxHighlighter.ContentLanguage.HTML);   
		    add(syntaxHighlighter, BorderLayout.CENTER);
		    */   

		  }   
		     
		     
}  


