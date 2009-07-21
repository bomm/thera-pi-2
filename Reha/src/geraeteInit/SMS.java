package geraeteInit;
import java.net.*; 
import java.io.*; 
import javax.swing.*; 
import javax.net.ssl.HttpsURLConnection; 


class SMS 
{ 

	public static void main(String args[]) 
	{ 
		boolean debug=true; 

		String phone	="00491792925261"; 
		String message	="SMS-Test"; 
		String ppgHost	="http://t-online.t-online.de/t-on/dien/sms/star/CP/start.html"; 
		String username ="1970223872";
		String password ="81867590";
		
		try
		{ 
			//phone=URLEncoder.encode("http://t-online.t-online.de/t-on/dien/sms/star/CP/start.html", "UTF-8"); 

			if(debug) 
				System.out.println("phone------>"+phone); 
			message=URLEncoder.encode("SendMsg via Now.SMS", "UTF-8"); 

			if(debug) 
				System.out.println("message---->"+message); 
		} 
		catch (UnsupportedEncodingException e) 
		{ 
			System.out.println("Encoding not supported"); 
		} 

		//String url_str=ppgHost+"?PhoneNumber="+phone+"&Text="+message; 
		String url_str=ppgHost+"?user="+username+"&password="+password+"&PhoneNumber="+phone+"&Text="+message; 

		if(debug)                   
			System.out.println("url string->"+url_str); 

		
		try
		{		
			URL url2=new URL(url_str); 

			HttpURLConnection connection = (HttpURLConnection) url2.openConnection(); 
			connection.setDoOutput(false); 
			connection.setDoInput(true); 

			if(debug)                  
				System.out.println("Opened Con->"+connection); 
	
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
			System.out.println("unable to create new url"+e.getMessage());
		}

	

	} // main
} // class 