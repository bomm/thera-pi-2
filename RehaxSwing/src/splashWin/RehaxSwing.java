package splashWin;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;

public class RehaxSwing implements WindowListener,KeyListener{

	/**
	 * @param args
	 */
	static JDialog jDiag = null;
	JXPanel contentPanel = null;
	static JLabel standDerDingelbl = null;
	static boolean socketoffen = false;
	static SockServer sock = null;
	public static String proghome;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RehaxSwing application = new RehaxSwing();
		new Thread(){
			public void run(){
				
				try {
					new SockServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		String prog = java.lang.System.getProperty("user.dir");
		if(System.getProperty("os.name").contains("Linux")){
			proghome = "/opt/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Windows")){
			proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Mac")){
			proghome = "/opt/RehaVerwaltung/";
		}

		System.out.println("ProgHome = "+proghome);
		
	
		//new RxSocketClient().setzeInitStand("Mandant = [510841109]");
		
		//jDiag.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jDiag = application.getDialog();

		jDiag.validate();
		jDiag.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		//jDiag.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		jDiag.setVisible(true);
		

		
		
	}
	private JDialog getDialog() {
		contentPanel = new JXPanel(new BorderLayout());
		contentPanel.setPreferredSize(new Dimension(400,300));
		contentPanel.add(new SplashInhalt(),BorderLayout.CENTER);
		JXPanel textPanel = new JXPanel(new BorderLayout());
		textPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		textPanel.setBackground(Color.WHITE);
		textPanel.setPreferredSize(new Dimension(0,15));
		standDerDingelbl = new JLabel("OpenSource-Projekt Reha-xSwing");
		standDerDingelbl.setFont(new Font("Arial", 8, 10));
		textPanel.add(standDerDingelbl,BorderLayout.WEST);
		contentPanel.add(textPanel,BorderLayout.SOUTH);
		contentPanel.validate();

		JDialog xDiag = new JDialog();
		xDiag.setCursor(new Cursor(Cursor.WAIT_CURSOR));		
		xDiag.setUndecorated(true);
		xDiag.setContentPane(contentPanel);
		xDiag.setSize(450, 200);
		xDiag.setLocationRelativeTo(null);
		xDiag.addWindowListener(this);
		xDiag.addKeyListener(this);
		xDiag.pack();
		return xDiag;
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("In windowClosed");
		if(SockServer.serv != null){
			try {
				SockServer.serv.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("In windowClosing");
		if(SockServer.serv != null){
			try {
				SockServer.serv.close();
				SockServer.serv = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		/*
		if(SockServer.serv != null){
			try {
				SockServer.serv.close();
				SockServer.serv = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode()==KeyEvent.VK_F4 && arg0.isAltDown()){
			System.out.println("ALT+F4 gedrückt");
			if(SockServer.serv != null){
				try {
					SockServer.serv.close();
					SockServer.serv = null;
				} catch (IOException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			jDiag.dispose();
			System.exit(0);
		}
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}

class SockServer{
	static ServerSocket serv = null;
	SockServer() throws IOException{
		try {
			serv = new ServerSocket(1234);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RehaxSwing.jDiag.dispose();
			return;
		}
		
		Socket client = null;
		//RehaxSwing.standDerDingelbl.setText("�ffne Socket");
		int income = 0;
		StringBuffer sb = new StringBuffer();
		while(true){
			try {
				RehaxSwing.socketoffen = true;				
				if(income==0){
					new RxSocketClient().setzeInitStand("INITENDE");
				}
				client = serv.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				break;
			}
			income++;
			sb.setLength(0);
			sb.trimToSize();
			InputStream input = client.getInputStream();
			OutputStream output = client.getOutputStream();
			int byteStream;
			String test = "";
			while( (byteStream =  input.read()) > -1){
				//System.out.println("******byteStream Erhalten******  "+byteStream );
				char b = (char)byteStream;
				sb.append(b);
			}

			test = new String(sb);
			System.out.println("Socket= "+test);			
			final String xtest = new String(test);

			if(xtest.equals("INITENDE")){
						byte[] schreib = "ok".getBytes();
						output.write(schreib);
						output.flush();
						output.close();
						input.close();
						serv.close();
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}								
						System.exit(0);
						break;			}else{
				SetzeLabel slb = new SetzeLabel();
				slb.init(new String(new String(xtest)));
				slb.execute();
			}
			byte[] schreib = "ok".getBytes();
			output.write(schreib);
			output.flush();
			output.close();
			input.close();

			//JOptionPane.showMessageDialog(null, test);
		}
		if(serv != null){
			serv.close();
			serv = null;
			System.out.println("Socket wurde geschlossen");
			RehaxSwing.socketoffen = false;
			RehaxSwing.jDiag.dispose();
		}else{
			System.out.println("Socket wurde geschlossen");
			RehaxSwing.socketoffen = false;
			RehaxSwing.jDiag.dispose();
		}
		SetzeLabel slb = new SetzeLabel();
		slb.init(new String(new String("Abbruch der Socketfunktion")));
		slb.execute();

		return;
	}
}
class RxSocketClient {
	String stand = "";
	Socket server = null;
	public void setzeInitStand(String stand){
		this.stand = new String(stand);
		run();
	}
	public void run() {
		try {
			serverStarten();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String mes = new String(  e.toString());
			JOptionPane.showMessageDialog(null,  mes);
		}
	}
	private void serverStarten() throws IOException{
		this.server = new Socket("localhost",1235);
		OutputStream output = (OutputStream) server.getOutputStream();
		InputStream input = server.getInputStream();

		byte[] bytes = this.stand.getBytes();

		output.write(bytes);
		output.flush();
		int zahl = input.available();
		if (zahl > 0){
			byte[] lesen = new byte[zahl];
			input.read(lesen);
		}
	
		server.close();
		input.close();
		output.close();
	}
}


class SetzeLabel extends SwingWorker<Void, Void>{
	private String labeltext = "";
	public void init(String text){
		this.labeltext = text;
		
	}
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				RehaxSwing.standDerDingelbl.setText(labeltext);
			}
		});

		return null;
	}

}