package pdftest2;

public class PDFTools {
	public static String sechserDatum(String datum){
		String retdatum = "";
		if(datum.contains("-")){
			retdatum = datum.substring(8,10)+datum.substring(5,7)+datum.substring(2,4);
		}else{
			retdatum = datum.substring(0,2)+datum.substring(3,5)+datum.substring(8,10);
		}
		return retdatum;
	}
	
}
