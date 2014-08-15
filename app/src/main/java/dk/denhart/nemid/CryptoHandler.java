package dk.denhart.nemid;

/**
 * Created by Denhart on 11-08-2014.
 * Based on http://stackoverflow.com/questions/992019/java-256bit-aes-encryption/992413#992413
 */
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import javax.crypto.*;
import javax.crypto.spec.*;

public class CryptoHandler {
    private SecretKey secretKey = null;
    private Cipher cipher = null;

    public static byte[] salt = {
            (byte)0xc8, (byte)0x73, (byte)0x41, (byte)0x8c,
            (byte)0x7e, (byte)0xd8, (byte)0xee, (byte)0x89
    };
    public CryptoHandler(char[] password) throws InvalidKeySpecException,
            NoSuchAlgorithmException,
            NoSuchPaddingException
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
        secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }


    public Crypto encrypt(byte[] cleartext) throws IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException,
            InvalidParameterSpecException
    {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new Crypto(
                cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV()
                ,cipher.doFinal(cleartext)
        );
    }

    public byte[] decrypt(Crypto storage) throws BadPaddingException,
            IllegalBlockSizeException,
            InvalidAlgorithmParameterException,
            InvalidKeyException
    {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(storage.getIv()));
        return cipher.doFinal(storage.getCiphertext());
    }

}
