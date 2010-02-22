package utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import nebraska.Constants;

public class DESCryptoTest {
    
    public static void main(String[] args) {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
            KeyGenerator kg = KeyGenerator.getInstance(Constants.SECRET_KEY_ALGORITHM_DES);
            Key key = kg.generateKey();
            Cipher cipher = Cipher.getInstance(Constants.SECRET_KEY_ALGORITHM_DES);
            byte[] data = "Hello World!".getBytes();
            System.out.println("Original data : " + new String(data));
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(data);
            System.out.println("Encrypted data: " + new String(result));
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] original = cipher.doFinal(result);
            System.out.println("Decrypted data: " + new String(original));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }
}