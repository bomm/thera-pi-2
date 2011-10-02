package lvaEntlassmitteilung;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLabelUI;

import org.jdesktop.swingx.JXPanel;



import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.JDBFException;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class LVAoberflaeche extends JXPanel implements ListSelectionListener, ActionListener {
	JRtaComboBox combobox1;
	JComboBox combobox2;
	JButton bnr1;	
	JButton bnr2;
	JButton bnr3;
	JButton bnr4;
	JButton bnr5;
	JButton bnr6;
	public boolean abbruch;
	public String returnString;
	JRtaTextField[] tf1 = {null,null,null,null,null,null};
	Vector<String> vec = new Vector<String>();
	Vector<String> dummyvec = new Vector<String>();	
	
	JLabel tagelab = null;
	
	public LVAoberflaeche(String rez_nr){
		super();
		
			setOpaque(false);	
	

		FormLayout layob1 = new FormLayout("1500dlu",
				// 1  2     3    4  5           6           7          8            9     10    11   
				"p,5dlu,p,5dlu,p");
						CellConstraints cc = new CellConstraints();
						setLayout(layob1);
						
						add(getAbschnitt1(rez_nr),cc.xy(1, 1));
						add(getAbschnitt2(),cc.xy(1, 3));
						add(getAbschnitt3(),cc.xy(1, 5));
	
	
	}
				
		
	private JPanel getAbschnitt1(String rez_nr){
			FormLayout abschnitt1Pan = new FormLayout("10dlu,p,5dlu,40dlu,10dlu,p,p,40dlu,20dlu,2dlu,20dlu,2dlu,40dlu,10dlu",
					"10dlu,p,p,p,3dlu,p,10dlu");
			PanelBuilder pabschnitt1 = new PanelBuilder(abschnitt1Pan);
			pabschnitt1.getPanel().setOpaque(false);
			CellConstraints ca1 = new CellConstraints();
		
			JLabel lbl1 = new JLabel("<html><u>Rehadaten:</u>");
			pabschnitt1.add(lbl1, ca1.xy(2,2));
			JLabel lbl2 = new JLabel("Rehanummer:");
			pabschnitt1.add(lbl2, ca1.xy(2,4));
			tf1 [0] = new JRtaTextField("ZAHLEN",true);
			if(rez_nr != null){
				tf1[0].setText(rez_nr.replace("RH", ""));				
			}
			pabschnitt1.add(tf1[0],ca1.xy(2,6));
			bnr1 = new JButton("Connect");
			bnr1.setActionCommand("connect");
			bnr1.addActionListener(this);
			pabschnitt1.add(bnr1,ca1.xy(4,6));
			JLabel lbl3 = new JLabel("Rehatage: ");
			pabschnitt1.add(lbl3, ca1.xy(6,6));
			tagelab = new JLabel("0");
			pabschnitt1.add(tagelab, ca1.xy(7,6));
			
			bnr4 = new JButton("+");
			bnr4.setActionCommand("plus");
			bnr4.addActionListener(this);
			pabschnitt1.add(bnr4,ca1.xy(9,4));
			bnr5 = new JButton("-");
			bnr5.setActionCommand("minus");
			bnr5.addActionListener(this);
			pabschnitt1.add(bnr5,ca1.xy(11,4));
			bnr6 = new JButton("sort.");
			bnr6.setActionCommand("sortieren");
			bnr6.addActionListener(this);
			bnr6.setEnabled(false);
			pabschnitt1.add(bnr6,ca1.xy(13,4));
			
			JLabel lbl4 = new JLabel("Anwesenheitsliste:");
			pabschnitt1.add(lbl4, ca1.xyw(9,3,5));
	     	combobox1 = new JRtaComboBox(new Vector<Vector<String>>(),0,1);
			pabschnitt1.add(combobox1,ca1.xyw(9,6,5));

		
			pabschnitt1.getPanel().validate();
			return pabschnitt1.getPanel();
		}
	class CbEditor extends JRtaTextField implements ComboBoxEditor{

		public CbEditor(String type, boolean selectWhenFocus) {
			super(type, selectWhenFocus);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void addActionListener(ActionListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Component getEditorComponent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getItem() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeActionListener(ActionListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void selectAll() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setItem(Object arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private JPanel getAbschnitt2(){
		FormLayout abschnitt2Pan = new FormLayout("10dlu,70dlu,5dlu,p,10dlu,70dlu,5dlu,p,10dlu,70dlu,5dlu,p,10dlu",
				"10dlu,p,5dlu,p,5dlu,p,10dlu,p,5dlu,p,10dlu,p,5dlu,p,10dlu");
		PanelBuilder pabschnitt2 = new PanelBuilder(abschnitt2Pan);
		pabschnitt2.getPanel().setOpaque(false);
		CellConstraints ca2 = new CellConstraints();
	
		
		JLabel lbl1 = new JLabel("<html><u>Fomulardaten:</u>");
		pabschnitt2.add(lbl1, ca2.xy(2,2));
		JLabel lbl2 = new JLabel("Fomulartyp:");
		pabschnitt2.add(lbl2, ca2.xy(2,4));
		
		combobox2 = new JComboBox(new String[] {"4-Zeilig mit Fahrtkosten","10-Zeilig o. Fahrtkosten"});
		combobox2.setActionCommand("zeilig");
		combobox2.addActionListener(this);
		pabschnitt2.add(combobox2,ca2.xyw(2,6,5));
		
		//JLabel lbl3 = new JLabel();
		JLabel lbl3 = new JLabel( "Abstand in Millimeter\nvom oberen Blattrand\nbis zur ersten\nDatumszeile:", null, SwingConstants.LEFT );
		lbl3.setHorizontalTextPosition( SwingConstants.LEFT );
		lbl3.setUI( new MultiLineLabelUI() );
		pabschnitt2.add(lbl3, ca2.xy(2,8));
		JLabel lbl4 = new JLabel("Abstand in Millimeter\nvom linken Blattrand\nbiszum Beginn der\n1.Datumslinie:",null, SwingConstants.LEFT);
		lbl4.setHorizontalTextPosition( SwingConstants.LEFT );
		lbl4.setUI( new MultiLineLabelUI() );
		pabschnitt2.add(lbl4, ca2.xy(6,8));
		JLabel lbl5 = new JLabel("Abstand in Millimeter\nzwischen den\nDatumszeilen\n(Y-Richtung):",null, SwingConstants.LEFT);
		lbl5.setHorizontalTextPosition( SwingConstants.LEFT );
		lbl5.setUI( new MultiLineLabelUI() );
		pabschnitt2.add(lbl5, ca2.xy(10,8));
		
		tf1 [1] = new JRtaTextField("D",true,"6.2","");
		tf1[1].setText("174,00");
		pabschnitt2.add(tf1[1],ca2.xy(2,10));
		tf1 [2] = new JRtaTextField("D",true,"6.2","");
		tf1[2].setText("23,00");
		pabschnitt2.add(tf1[2],ca2.xy(6,10));
		tf1 [3] = new JRtaTextField("D",true,"6.2","");
		tf1[3].setText("8,00");
		pabschnitt2.add(tf1[3],ca2.xy(10,10));
		
		JLabel lbl6 = new JLabel("mm");
		pabschnitt2.add(lbl6, ca2.xy(4,10));
		JLabel lbl7 = new JLabel("mm");
		pabschnitt2.add(lbl7, ca2.xy(8,10));
		JLabel lbl8 = new JLabel("mm");
		pabschnitt2.add(lbl8, ca2.xy(12,10));
		
		JLabel lbl9 = new JLabel("Länge der einzelnen\nDatumslinien:",null, SwingConstants.LEFT);
		lbl9.setHorizontalTextPosition( SwingConstants.LEFT );
		lbl9.setUI( new MultiLineLabelUI() );
		pabschnitt2.add(lbl9, ca2.xy(2,12));
		JLabel lbl10 = new JLabel("Abstand in Millimeter\nzwischen den\nDatumsfeldern\n(X-Richtung):",null, SwingConstants.LEFT);
		lbl10.setHorizontalTextPosition( SwingConstants.LEFT );
		lbl10.setUI( new MultiLineLabelUI() );
		pabschnitt2.add(lbl10, ca2.xy(6,12));
		
		tf1 [4] = new JRtaTextField("D",true,"6.2","");
		tf1[4].setText("25,00");
		pabschnitt2.add(tf1[4],ca2.xy(2,14));
		tf1 [5] = new JRtaTextField("D",true,"6.2","");
		tf1[5].setText("5,50");
		pabschnitt2.add(tf1[5],ca2.xy(6,14));
		
		JLabel lbl11 = new JLabel("mm");
		pabschnitt2.add(lbl11, ca2.xy(4,14));
		JLabel lbl12 = new JLabel("mm");
		pabschnitt2.add(lbl12, ca2.xy(8,14));
		
		
		
		pabschnitt2.getPanel().validate();
		return pabschnitt2.getPanel();
	}
	
	
	private JPanel getAbschnitt3(){
		FormLayout abschnitt3Pan = new FormLayout("100dlu,60dlu,50dlu,60dlu,10dlu",
				"10dlu,p,10dlu");
		PanelBuilder pabschnitt3 = new PanelBuilder(abschnitt3Pan);
		pabschnitt3.getPanel().setOpaque(false);
		CellConstraints ca3 = new CellConstraints();
		
		bnr2 = new JButton("Drucken");
		bnr2.setActionCommand("drucken");
		bnr2.addActionListener(this);
		pabschnitt3.add(bnr2,ca3.xy(2,2));
		
		bnr3 = new JButton("Schliessen");
		bnr3.setActionCommand("schliessen");
		bnr3.addActionListener(this);
		pabschnitt3.add(bnr3,ca3.xy(4,2));
		
		
		
		pabschnitt3.getPanel().validate();
		return pabschnitt3.getPanel();
	}
		
		
		
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("connect")){
			doHoleTermine();
			//doDBFEinlesen();
			return;
		}
		if(cmd.equals("plus")){
			doPlus( ((JButton)arg0.getSource()).getLocationOnScreen() );
	        tagelab.setText(Integer.toString(combobox1.getItemCount()));
			return;
		}
		if(cmd.equals("minus")){
			doMinus();
	        tagelab.setText(Integer.toString(combobox1.getItemCount()));
			return;
		}
		if(cmd.equals("sortieren")){
			doSortieren();
			return;
		}		
		if(cmd.equals("zeilig")){
			doZeilig(combobox2.getSelectedIndex());
			return;
		}
		if(cmd.equals("drucken")){
			doPDFStarten();
			return;
		}
		if(cmd.equals("schliessen")){
			System.exit(0);
			return;
		}
		  
		
		
	}
	private void doHoleTermine(){
		String termine = holeRezFeld("termine", "rez_nr='"+"RH"+tf1[0].getText().trim()+"'");
		Vector<String>vec = new Vector<String>();
		if(termine.length() > 0){
			String[] terms = termine.split("\n");
			for(int i = 0; i < terms.length;i++){
				String[] tag = terms[i].split("@");
				vec.add(tag[0].toString());
			}
			combobox1.setDataVector((Vector<String>)vec.clone());
	        combobox1.setMaximumRowCount(25);
	        tagelab.setText(Integer.toString(combobox1.getItemCount()));
	        combobox1.validate();
		}
	}
	public static String holeRezFeld(String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		
			
		try {
			stmt =  LVArahmen.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "select "+feld+" from verordn where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				ret = (rs.getString(feld)==null  ? "" :  rs.getString(feld));
			}
			
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return ret;
	}
	
	
	private void doSortieren(){
		
	}
	
	private void doPDFStarten(){
		String[] werte = {tf1[1].getText().trim().replace(",","."),
				tf1[2].getText().trim().replace(",","."),
				tf1[3].getText().trim().replace(",","."),
				tf1[4].getText().trim().replace(",","."),
				tf1[5].getText().trim().replace(",",".")
				};
		new PDFDummy(combobox1.getDataVector(),werte);
	}
	private void doZeilig(int wert){
				
		if(wert==0){
			tf1[1].setText("174,00");
			tf1[2].setText("23,00");
			tf1[3].setText("8,00");
			tf1[4].setText("25,00");
			tf1[5].setText("5,50");
		return;
		}
		
		
		
		if (wert==1);{
			tf1[1].setText("166,00");
			tf1[2].setText("24,00");
			tf1[3].setText("8,00");
			tf1[4].setText("25,00");
			tf1[5].setText("5,50");
		return;
		}
		
		
	}
	
	
	private void doPlus(Point p){
		JRtaTextField datum = new JRtaTextField("DATUM",true);
		datum.setText(DatFunk.sHeute());
		plusdatum pldat = new plusdatum(this);
		pldat.pack();
		pldat.setLocation(p.x,p.y+20);
		pldat.setModal(true);
		pldat.setVisible(true);

		//System.out.println(abbruch);
		if(!abbruch && (returnString != null)){
			String retDatum = returnString;
			//System.out.println("In Elternklasse "+retDatum);
			Vector vec = combobox1.getDataVector();
			vec.add(returnString);
			Comparator<String> comparator = new Comparator<String>() {
		
				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					String s1 = DatFunk.sDatInSQL((String)o1);
					String s2 = DatFunk.sDatInSQL((String)o2);
					return s1.compareTo(s2);
				}
			};
			Collections.sort(vec,comparator);
			combobox1.setDataVector((Vector) vec.clone());
			combobox1.setSelectedItem(returnString);
		}
	}
	private void doMinus(){
		if(combobox1.getItemCount()>0){
			int position = combobox1.getSelectedIndex();
			Vector vec = combobox1.getDataVector();
			vec.remove(position);
			combobox1.setDataVector(vec);
		}
	}

	private void doDBFEinlesen(){
		String datei;
		String nummer = tf1[0].getText().trim();
		
		if (nummer.equals("")){
			JOptionPane.showMessageDialog(null, "Bitte Reha-Nummer eingeben");
			return;
		}
		datei = ("L:/projekte/rta/dbf/eterm/"+"RH" + nummer +".dbf").toLowerCase();	

		File f = new File(datei);

		if(!f.exists()){
			JOptionPane.showMessageDialog(null, "Rehanummer --> RH"+nummer+" <-- existiert nicht");
			return;
		}
		
		DBFReader dbfreader;
		try {
			dbfreader = new DBFReader(datei);
		////System.out.println("Öffne Datei "+datei);
		String ganzerString = "";
		String datum;
		DateFormat df = DateFormat.getDateInstance();
		vec.clear();
		combobox1.setDataVector((Vector)vec.clone());
        for(int i = 0; dbfreader.hasNextRecord(); i++){	
        	dummyvec.clear();
            Object aobj[] = dbfreader.nextRecord();
            for (int j=0; j<aobj.length; j++){
            	if(j==0){
            		try{
            			datum = df.format(aobj[j]);
            		}catch(Exception ex){
            			datum = DatFunk.sHeute();            			
            		}
            		//dummyvec.add(datum);
            		//dummyvec.add(DatFunk.sDatInSQL(datum));
            		vec.add((String)datum);
            		////System.out.println("Feld "+j+" "+(aobj[j]));
            	}
            }	
              
            ////System.out.print("\n");
        }
        dbfreader.close();		
        combobox1.setDataVector((Vector<String>)vec.clone());
        combobox1.setMaximumRowCount(25);
        combobox1.validate();
		//System.out.println("Vectorlänge = "+vec.size());
		} catch (JDBFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
/****************************************************************************************/
class MultiLineLabelUI extends BasicLabelUI
{
	static {
		labelUI = new MultiLineLabelUI();
	}
	
    protected String layoutCL(
        JLabel label,                  
        FontMetrics fontMetrics, 
        String text, 
        Icon icon, 
        Rectangle viewR, 
        Rectangle iconR, 
        Rectangle textR)
    {
        String s = layoutCompoundLabel(
            (JComponent) label,
            fontMetrics,
            splitStringByLines(text),
            icon,
            label.getVerticalAlignment(),
            label.getHorizontalAlignment(),
            label.getVerticalTextPosition(),
            label.getHorizontalTextPosition(),
            viewR,
            iconR,
            textR,
            label.getIconTextGap());
    	
    	if( s.equals("") )
    		return text;
    	return s;
    }
	
	
	static final int LEADING = SwingConstants.LEADING;
	static final int TRAILING = SwingConstants.TRAILING;
	static final int LEFT = SwingConstants.LEFT;
	static final int RIGHT = SwingConstants.RIGHT;
	static final int TOP = SwingConstants.TOP;
	static final int CENTER = SwingConstants.CENTER;

	/**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * The JComponents orientation (LEADING/TRAILING) will also be taken
     * into account and translated into LEFT/RIGHT values accordingly.
     */
    public static String layoutCompoundLabel(JComponent c,
                                             FontMetrics fm,
                                             String[] text,
                                             Icon icon,
                                             int verticalAlignment,
                                             int horizontalAlignment,
                                             int verticalTextPosition,
                                             int horizontalTextPosition,
                                             Rectangle viewR,
                                             Rectangle iconR,
                                             Rectangle textR,
                                             int textIconGap)
    {
        boolean orientationIsLeftToRight = true;
        int     hAlign = horizontalAlignment;
        int     hTextPos = horizontalTextPosition;

        
        if (c != null) {
            if (!(c.getComponentOrientation().isLeftToRight())) {
                orientationIsLeftToRight = false;
            }
        }
        

        // Translate LEADING/TRAILING values in horizontalAlignment
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalAlignment) {
        case LEADING: 
            hAlign = (orientationIsLeftToRight) ? LEFT : RIGHT;
            break;
        case TRAILING: 
            hAlign = (orientationIsLeftToRight) ? RIGHT : LEFT;
            break;
        }

        // Translate LEADING/TRAILING values in horizontalTextPosition
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalTextPosition) {
        case LEADING: 
            hTextPos = (orientationIsLeftToRight) ? LEFT : RIGHT;
            break;
        case TRAILING: 
            hTextPos = (orientationIsLeftToRight) ? RIGHT : LEFT;
            break;
        }

        return layoutCompoundLabel(fm,
                                   text,
                                   icon,
                                   verticalAlignment,
                                   hAlign,
                                   verticalTextPosition,
                                   hTextPos,
                                   viewR,
                                   iconR,
                                   textR,
                                   textIconGap);
    }


    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * This layoutCompoundLabel() does not know how to handle LEADING/TRAILING
     * values in horizontalTextPosition (they will default to RIGHT) and in
     * horizontalAlignment (they will default to CENTER).
     * Use the other version of layoutCompoundLabel() instead.
     */
    public static String layoutCompoundLabel(
        FontMetrics fm,
        String[] text,
        Icon icon,
        int verticalAlignment,
        int horizontalAlignment,
        int verticalTextPosition,
        int horizontalTextPosition,
        Rectangle viewR,
        Rectangle iconR,
        Rectangle textR,
        int textIconGap)
    {
        /* Initialize the icon bounds rectangle iconR.
         */

        if (icon != null) {
            iconR.width = icon.getIconWidth();
            iconR.height = icon.getIconHeight();
        }
        else {
            iconR.width = iconR.height = 0;
        }

        /* Initialize the text bounds rectangle textR.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for textR.
         */

        // Fix for textIsEmpty sent by Paulo Santos
        boolean textIsEmpty = (text == null) || (text.length == 0)
			|| (text.length == 1 && ( (text[0]==null) || text[0].equals("") ));

    	String rettext = "";
        if (textIsEmpty) {
            textR.width = textR.height = 0;
        }
        else {
        	Dimension dim = computeMultiLineDimension( fm, text );
            textR.width = dim.width;
            textR.height = dim.height;
        }

        /* Unless both text and icon are non-null, we effectively ignore
         * the value of textIconGap.  The code that follows uses the
         * value of gap instead of textIconGap.
         */

        int gap = (textIsEmpty || (icon == null)) ? 0 : textIconGap;

        if (!textIsEmpty) {

            /* If the label text string is too wide to fit within the available
             * space "..." and as many characters as will fit will be
             * displayed instead.
             */

            int availTextWidth;

            if (horizontalTextPosition == CENTER) {
                availTextWidth = viewR.width;
            }
            else {
                availTextWidth = viewR.width - (iconR.width + gap);
            }


            if (textR.width > availTextWidth && text.length == 1) {
                String clipString = "...";
                int totalWidth = SwingUtilities.computeStringWidth(fm,clipString);
                int nChars;
                for(nChars = 0; nChars < text[0].length(); nChars++) {
                    totalWidth += fm.charWidth(text[0].charAt(nChars));
                    if (totalWidth > availTextWidth) {
                        break;
                    }
                }
                rettext = text[0].substring(0, nChars) + clipString;
                textR.width = SwingUtilities.computeStringWidth(fm,rettext);
            }
        }


        /* Compute textR.x,y given the verticalTextPosition and
         * horizontalTextPosition properties
         */

        if (verticalTextPosition == TOP) {
            if (horizontalTextPosition != CENTER) {
                textR.y = 0;
            }
            else {
                textR.y = -(textR.height + gap);
            }
        }
        else if (verticalTextPosition == CENTER) {
            textR.y = (iconR.height / 2) - (textR.height / 2);
        }
        else { // (verticalTextPosition == BOTTOM)
            if (horizontalTextPosition != CENTER) {
                textR.y = iconR.height - textR.height;
            }
            else {
                textR.y = (iconR.height + gap);
            }
        }

        if (horizontalTextPosition == LEFT) {
            textR.x = -(textR.width + gap);
        }
        else if (horizontalTextPosition == CENTER) {
            textR.x = (iconR.width / 2) - (textR.width / 2);
        }
        else { // (horizontalTextPosition == RIGHT)
            textR.x = (iconR.width + gap);
        }

        /* labelR is the rectangle that contains iconR and textR.
         * Move it to its proper position given the labelAlignment
         * properties.
         *
         * To avoid actually allocating a Rectangle, Rectangle.union
         * has been inlined below.
         */
        int labelR_x = Math.min(iconR.x, textR.x);
        int labelR_width = Math.max(iconR.x + iconR.width,
                                    textR.x + textR.width) - labelR_x;
        int labelR_y = Math.min(iconR.y, textR.y);
        int labelR_height = Math.max(iconR.y + iconR.height,
                                     textR.y + textR.height) - labelR_y;

        int dx, dy;

        if (verticalAlignment == TOP) {
            dy = viewR.y - labelR_y;
        }
        else if (verticalAlignment == CENTER) {
            dy = (viewR.y + (viewR.height / 2)) - (labelR_y + (labelR_height / 2));
        }
        else { // (verticalAlignment == BOTTOM)
            dy = (viewR.y + viewR.height) - (labelR_y + labelR_height);
        }

        if (horizontalAlignment == LEFT) {
            dx = viewR.x - labelR_x;
        }
        else if (horizontalAlignment == RIGHT) {
            dx = (viewR.x + viewR.width) - (labelR_x + labelR_width);
        }
        else { // (horizontalAlignment == CENTER)
            dx = (viewR.x + (viewR.width / 2)) -
                 (labelR_x + (labelR_width / 2));
        }

        /* Translate textR and glypyR by dx,dy.
         */

        textR.x += dx;
        textR.y += dy;

        iconR.x += dx;
        iconR.y += dy;

        return rettext;
    }
	
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY)
    {
        int accChar = l.getDisplayedMnemonic();
        g.setColor(l.getForeground());
        drawString(g, s, accChar, textX, textY);
    }


    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY)
    {
		int accChar = l.getDisplayedMnemonic();
		g.setColor(l.getBackground());
    	drawString(g, s, accChar, textX, textY);
    }
	
	protected void drawString( Graphics g, String s, int accChar, int textX, int textY )
	{
    	if( s.indexOf('\n') == -1 )
			BasicGraphicsUtils.drawString(g, s, accChar, textX, textY);
    	else
    	{
    		String[] strs = splitStringByLines( s );
    		int height = g.getFontMetrics().getHeight();
    		// Only the first line can have the accel char
    		BasicGraphicsUtils.drawString(g, strs[0], accChar, textX, textY);
    		for( int i = 1; i < strs.length; i++ )
    			g.drawString( strs[i], textX, textY + (height*i) );
    	}
	}
	
	public static Dimension computeMultiLineDimension( FontMetrics fm, String[] strs )
	{
		int i, c, width = 0;
        for(i=0, c=strs.length ; i < c ; i++)
        	width = Math.max( width, SwingUtilities.computeStringWidth(fm,strs[i]) );
		return new Dimension( width, fm.getHeight() * strs.length );
	}
	
	
	protected String str;
	protected String[] strs;
	
	public String[] splitStringByLines( String str )
	{
		if( str.equals(this.str) )
			return strs;
		
		this.str = str;
		
		int lines = 1;
		int i, c;
        for(i=0, c=str.length() ; i < c ; i++) {
            if( str.charAt(i) == '\n' )
            	lines++;
        }
		strs = new String[lines];
		StringTokenizer st = new StringTokenizer( str, "\n" );
		
		int line = 0;
		while( st.hasMoreTokens() )
			strs[line++] = st.nextToken();
			
		return strs;
	}
}
