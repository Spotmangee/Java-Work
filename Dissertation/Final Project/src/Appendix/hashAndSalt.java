package Appendix;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class hashAndSalt {

    public final static int SALT_LEN = 16;
    public static byte[] Salt, Hash;
    private static char[] chars;

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        //turn text into bytecode
        String text = "Hello World";        //text to convert into hash
        chars = text.toCharArray(); //text converted into chars

        //create hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

        //create salt and store for later usage with secure random
        Salt = new byte[SALT_LEN];
        SecureRandom randomSecureRandom = new SecureRandom();
        randomSecureRandom.nextBytes(Salt);

        //print hash and salt
        System.out.println("Salt: " + new String(Salt));
        System.out.println("Hash: " + new String(Hash));

    }
}

//Help from: http://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java