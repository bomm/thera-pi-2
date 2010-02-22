package utils;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import nebraska.Constants;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DESKeyGenerator {
    
    public static void main(String[] args) {
        //
        // Dynamically load a provider at runtime
        //Security.addProvider(new com.sun.crypto.provider.SunJCE());
    	Security.addProvider(new BouncyCastleProvider());
        try {
        	//KeyPairGenerator keyp;
    		KeyPairGenerator keyp;
			try {
				
			keyp = KeyPairGenerator.getInstance(Constants.PUBLIC_KEY_ALGORITHM,Constants.SECURITY_PROVIDER);
    		keyp.initialize(1024, new SecureRandom());
    		KeyPair keyPair = keyp.generateKeyPair();
    		Key keyPr = keyPair.getPrivate();
    		Key keyPu = keyPair.getPublic();
            //
            // Get a DES private key
            //System.out.println( "\nStart generating DES key" );         
            //KeyGenerator kg = KeyGenerator.getInstance(Constants.SECRET_KEY_ALGORITHM_DES);
            //Key key = kg.generateKey();
            System.out.println("Private-Key format: " + keyPr.getFormat());
            System.out.println("Private-Key algorithm: " + keyPr.getAlgorithm());
            System.out.println("Private-Key toString(): " + keyPr.toString());
            System.out.println("Public-Key format: " + keyPu.getFormat());
            System.out.println("Public-Key algorithm: " + keyPu.getAlgorithm());
            System.out.println("Public-Key toString(): " + keyPu.toString());

        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        } catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}    