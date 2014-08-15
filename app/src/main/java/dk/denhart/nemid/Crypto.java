package dk.denhart.nemid;

/**
 * Created by Denhart on 11-08-2014.
 */


public class Crypto {
    private byte[] iv;
    private byte[] ciphertext;


    public Crypto(byte[] iv, byte[] ciphertext) {
        this.iv = iv;
        this.ciphertext = ciphertext;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    public byte[] getIv() {
        return iv;
    }


}
