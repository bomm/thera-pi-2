package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class RehaIOServer{
	public ServerSocket serv = null;
	StringBuffer sb = new StringBuffer();
	InputStream input = null;
	OutputStream output = null;
	private int port = 6000;
	public RehaIOServer() throws IOException{
		
		while(port < 6020){
			try {
				serv = new ServerSocket(port);
				break;
			} catch (IOException e) {
				if(serv != null){
					serv.close();
					serv = null;
				}
				e.printStackTrace();
				port++;
				System.out.println("Fehler bei Port: "+port);
			}
		}
		if(port==620){
			serv = null;
			return;
		}
		System.out.println("IO-SocketServer installiert auf Port: "+port);
		Socket client = null;
		while(true){
			try {
				client = serv.accept();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			sb.setLength(0);
			sb.trimToSize();
			input = client.getInputStream();
			output = client.getOutputStream();
			int byteStream;
			String test = "";
			while( (byteStream =  input.read()) > -1){
				char b = (char)byteStream;
				sb.append(b);
			}
			test = String.valueOf(sb);
			final String xtest = test;
			if(xtest.equals("INITENDE")){
				byte[] schreib = "ok".getBytes();
				output.write(schreib);
				output.flush();
				output.close();
				input.close();
				serv.close();
				serv = null;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}								
				Reha.warten = false;
				break;
			}else{
			}
			byte[] schreib = "ok".getBytes();
			output.write(schreib);
			output.flush();
			output.close();
			input.close();
		}
		if(serv != null){
			serv.close();
			serv = null;
		}else{
			////System.out.println("Socket war bereits geschlossen");
		}
		return;
	}
	public String getPort(){
		return Integer.toString(port);
	}
}