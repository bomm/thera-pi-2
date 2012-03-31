import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CalcKilometer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String copy = "Es ist ein Fehler aufgetreten - Adressen richtig?";
		int kilometer = 0;
		int minuten = 0;
		double meter = 0.;
		int sekunden = 0;

		if(args.length == 2) {
			String mandAdr = args[1];
			String patientAdr = args[0];
			
			String url = "http://maps.google.com/maps/api/directions/xml?origin="+ mandAdr +"&destination="+ patientAdr +"&sensor=false";
			//Google nach Kilometern Fragen!
			try {
			URL google = new URL(url);
			InputStream in = google.openStream();
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in);
			Boolean vaterReached = false;
			while(parser.hasNext()) {
				String name = (parser.hasName()) ? parser.getName().getLocalPart() : "";
				if(name.equals("copyrights")) {
					copy = parser.getElementText();
				}
				if(name.equals("leg") && parser.isStartElement()) {
					vaterReached = true;
				} else if(name.equals("leg") && parser.isEndElement()) {
					vaterReached = false;
				} else if(vaterReached && parser.isStartElement()) {
					if(name.equals("duration")) {
						while(!parser.isEndElement()) {
							if(parser.hasName()) {
								if(parser.getName().getLocalPart().equals("value")) {
									sekunden = Integer.parseInt(parser.getElementText());
								}
							}
							parser.next();
						}
					} else if(name.equals("distance")) {
						while(!parser.isEndElement()) {
							if(parser.hasName()) {
								if(parser.getName().getLocalPart().equals("value")) {
									meter = Double.parseDouble(parser.getElementText()+".0");//Integer.parseInt(parser.getElementText());
								}
							}
							parser.next();
						}
					} else {
						while(!(parser.isEndElement() && ((parser.hasName()) ? parser.getName().getLocalPart().equals(name) : false ))) {
							parser.next();
						}
					}
				}
				parser.next();
			}
			Double dkm = Double.valueOf(meter*2./1000.);
			kilometer = Double.valueOf(Math.rint(dkm)).intValue(); 
			minuten = sekunden / 60;
			} catch (Exception e) {
				e.printStackTrace();
				copy = "Fehler: " + e.getMessage();
			}
			
		}
		String smeter1 = Double.toString(meter);
		String smeter2 = Double.toString(meter*2);
		
		System.out.println(kilometer + ";" + minuten + ";" + 
				"(Einfache Strecke in Meter = "+smeter1.substring(0,smeter1.indexOf("."))+"m)<br>"+
				"(Exakte Strecke hin und zurück in Meter = "+smeter2.substring(0,smeter2.indexOf("."))+"m)<br><br>"+copy+"<br><br>");
		System.exit(0);
	}

}
