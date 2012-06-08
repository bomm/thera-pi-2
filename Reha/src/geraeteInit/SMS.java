package geraeteInit;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class SMS 
{ 

	public static void main(String args[]) 
	{ 
		boolean debug=true; 

		String phone	="00491792925261"; 
		String message	="SMS-Test"; 
		String ppgHost	="http://datasync.t-online.de/syncml.osp"; 
		String username ="Steinhilber.J@t-online.de";
		String password ="81867590";
		
		try
		{ 
			//phone=URLEncoder.encode("http://t-online.t-online.de/t-on/dien/sms/star/CP/start.html", "UTF-8"); 

			if(debug) 
				System.out.println("phone------>"+phone); 
			message=URLEncoder.encode("Nachricht per SMS", "UTF-8"); 

			if(debug) 
				System.out.println("message---->"+message); 
		} 
		catch (UnsupportedEncodingException e) 
		{ 
			System.out.println("Encoding not supported\n"+e.getStackTrace()); 
		} 

		//String url_str=ppgHost+"?PhoneNumber="+phone+"&Text="+message; 
		String url_str=ppgHost+"?user="+username+"&password="+password+"&PhoneNumber="+phone+"&text="+message; 

		if(debug)                   
			System.out.println("url string->"+url_str); 

		
		try
		{		
			URL url2=new URL(url_str); 

			HttpURLConnection connection = (HttpURLConnection) url2.openConnection(); 
			connection.setDoOutput(true); 
			connection.setDoInput(true); 

			if(debug)                  
				System.out.println("Opened Con->"+connection.getResponseMessage()); 
	
				String res=connection.getResponseMessage(); 
		
			if(debug) 
				System.out.println("Get Resp  ->"+res); 
	
			int code = connection.getResponseCode () ; 
			
			if ( code == HttpURLConnection.HTTP_OK ) 
			{ 
				connection.disconnect() ; 
			}
			
		}
		catch(IOException e)
		{
			System.out.println("Keine Möglichkeit die URL zu kreieren\n"+e.getMessage());
		}
		System.exit(0);
	

	} // main
} // class 