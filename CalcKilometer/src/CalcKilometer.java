import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CalcKilometer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String copy = "Es ist ein Fehler aufgetreten - Adressen richtig?";
		int kilometer = 1;
		int minuten = 1;
		if(args.length == 2) {
			String mandAdr = args[1];
			String patientAdr = args[0];
			
			String url = "http://maps.google.com/maps/api/directions/xml?origin="+ mandAdr +"&destination="+ patientAdr +"&sensor=false";
			//Google nach Kilometern Fragen!
			int meter = 1;
			int sekunden = 1;
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
									meter = Integer.parseInt(parser.getElementText());
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
			} catch (Exception e) {
				copy = "Fehler: " + e.getMessage();
			}
			kilometer = Math.round((meter*2) / 1000);
			minuten = sekunden / 60;
		}
		System.out.println(kilometer + ";" + minuten + ";" + copy);
	}

}
