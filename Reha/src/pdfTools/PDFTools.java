package pdfTools;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import systemEinstellungen.SystemConfig;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfTable;

public class PDFTools {

	public static PdfPTable FliessText(float x1,float y1,float x2,float y2,
			PdfContentByte cb,String text,PdfStamper stamper){
		PdfPTable table = new PdfPTable (1);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		//float[] fwiths = {x2-x1};
		try {
			table.setTotalWidth(x2-x1);
			table.setLockedWidth(true);
			cb.saveState();
            

			
			Chunk ch = new Chunk();
			HashMap map = ch.getAttributes();
			if(map==null){
				System.out.println("map == null");
			}
			if(map.isEmpty()){
				System.out.println("map isEmpty");
			}
			Set entries = map.entrySet();
		    Iterator it = entries.iterator();
		    while (it.hasNext()) {
		    	 Map.Entry entry = (Map.Entry) it.next();
		    	 System.out.println("Key:"+entry.getKey()+" - Wert:"+entry.getValue());
		    }

			PdfPCell cell1 = new PdfPCell();
			cell1.setPadding(0.f);
			cell1.setFixedHeight(y2-y1);
			cell1.setNoWrap(false);
			cell1.setBorder(0);
			cell1.addElement(ch);

			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

			cell1.setSpaceCharRatio(0.5f);

			//cell1.addElement((Element) pg);
			
			table.addCell(text);
			
			table.writeSelectedRows(0, -1, x1, y2, cb);

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;

	}
}
