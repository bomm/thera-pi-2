package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ReverseSocket {
	String nachricht = "";
	Socket client = null;
	int port = -1;
	public void setzeRehaNachricht(int xport,String xnachricht){
		this.nachricht = xnachricht;
		this.port = xport;
		run();
	}
	public void run() {
		try {
			serverStarten();
		} catch (IOException e) {
		}
	}
	private void serverStarten() throws IOException{
		try{
			this.client = new Socket("localhost",this.port);
			OutputStream output = (OutputStream) client.getOutputStream();
			InputStream input = client.getInputStream();

			byte[] bytes = this.nachricht.getBytes();

			output.write(bytes);
			output.flush();
			int zahl = input.available();
			if (zahl > 0){
				byte[] lesen = new byte[zahl];
				input.read(lesen);
			}
		
			client.close();
			input.close();
			output.close();
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
