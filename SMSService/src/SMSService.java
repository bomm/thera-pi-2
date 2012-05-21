import java.io.IOException;

import javax.swing.JOptionPane;

import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

import Tools.INIFile;




public class SMSService {

	private String comport, hersteller, model;
	private int baudrate;
	
	/**
	 * @param args[0] IK args[1...n-1] number args[n] text
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws SMSLibException 
	 * @throws TimeoutException 
	 */
	public static void main(String[] args) {
		String homedir = System.getProperty("user.dir");
		if(args.length > 0) {
			String IK = args[0];
			String msg = args[args.length -1];
			INIFile sms = new INIFile(homedir + "\\ini\\" + IK + "\\sms.INI");
			SMSService smsService = new SMSService(sms.getStringProperty("Modem", "Com"), sms.getStringProperty("Modem", "Hersteller"), sms.getStringProperty("Modem", "Typ"), sms.getIntegerProperty("Modem", "Baudrate"));
			for(int i = 1; i < args.length -1; i++) {
				System.out.println("Sende: " + args[i] + " " + msg);
				smsService.sendSMS(args[i], msg);
			}
			close();
		} else {
			INIFile mand = new INIFile(homedir + "\\ini\\mandanten.ini");
			Object[] possibilities = new Object[mand.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten")];
			for(int n = 1; n <= possibilities.length; n++) {
				possibilities[n-1] = mand.getStringProperty("TheraPiMandanten", "MAND-IK"+n);
			}
			String IK = (String)JOptionPane.showInputDialog(
                    null,
                    "Bitte IK wählen:",
                    "IK wählen",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    null);
			INIFile sms = new INIFile(homedir + "\\ini\\" + IK + "\\sms.INI");
			SMSService smsService = new SMSService(sms.getStringProperty("Modem", "Com"), sms.getStringProperty("Modem", "Hersteller"), sms.getStringProperty("Modem", "Typ"), sms.getIntegerProperty("Modem", "Baudrate"));
			new SMSFrame(smsService);
			
		}
		
	}
	
	public static void close() {
		try {
			Service.getInstance().stopService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public SMSService(String comport, String hersteller, String model, int baudrate) {
		this.comport = comport;
		this.hersteller = hersteller;
		this.model = model;
		this.baudrate = baudrate;
		SerialModemGateway gateway = new SerialModemGateway("modem.com1", this.comport, this.baudrate, this.hersteller, this.model);
		gateway.setOutbound(true);
		try {
			Service.getInstance().addGateway(gateway);
			Service.getInstance().startService();
		} catch (Exception e) {
			e.printStackTrace();
			SMSService.close();
		}
	}
	
	public void sendSMS(String number, String text) {
		number = number.replace("+", "00");
		number = number.replaceAll("\\s", "");
		number = number.replaceAll("[/-]",	"");
		OutboundMessage msg = new OutboundMessage(number, text);
		try {
			Service.getInstance().sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
			SMSService.close();
		}
		System.out.println(msg);
	}

}
