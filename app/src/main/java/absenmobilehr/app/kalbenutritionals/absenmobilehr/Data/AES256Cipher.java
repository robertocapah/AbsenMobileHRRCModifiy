package absenmobilehr.app.kalbenutritionals.absenmobilehr.Data;

/**
 * Created by Robert on 25/07/2017.
 */
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Cipher {

    /*Documentation

 String key = "e8ffc7e56311679f12b6fc91aa77a5eb";
byte[] keyBytes = key.getBytes("UTF-8");
byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

String plainText;
byte[] cipherData;
String base64Text;

//############## Request(crypt) ##############
plainText  = "crypt text!!";
cipherData = AES256Cipher.encrypt(ivBytes, keyBytes, plainText.getBytes("UTF-8"));
base64Text = Base64.encodeToString(cipherData, Base64.DEFAULT);
Log.d("encrypt", base64Text);   //BhSJd4mRRJo+fGzpxIOUNg==

//############## Response(decrypt) ##############
base64Text = "72XrlydqnUzVrDfDE7ncnQ==";
cipherData = AES256Cipher.decrypt(ivBytes, keyBytes, Base64.decode(base64Text.getBytes("UTF-8"), Base64.DEFAULT));
plainText = new String(cipherData, "UTF-8");
Log.d("dcrypt", plainText);         //I'm hungry

    * */

    public static byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public static byte[] decrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }
}
