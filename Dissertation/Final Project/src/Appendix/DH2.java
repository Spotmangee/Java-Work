package Appendix;

import sun.security.krb5.internal.crypto.Aes256;

import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class DH2 {

    private static int bitLength = 512;
    private static int p = 7120; //prime
    private static int g = 2;   //generator- primitive root modulo of prime

    //message, ciphertext and plaintext
    private static String message;
    private static byte[] ciphertext;
    private static String plaintext;

    private static BigInteger generatorValue, primeValue;      //generator and prime fro both keys
    private static BigInteger publicA, secretA, sharedKeyA;    //keys for personA
    private static  BigInteger publicB, sharedKeyB, secretB;    //keys for personB


    public static void main(String args[]) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {

        //--------------------------------------------------------------------------------------------------------------
        //Keys defined: -
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");

        primeValue = BigInteger.valueOf((long) p);  //findPrime();
        System.out.println("the prime is " + primeValue);

        generatorValue = BigInteger.valueOf((long) g); // findPrimeRoot(primeValue);
        System.out.println("the generator of the prime is " + generatorValue);

        //keys for personA
        secretA = new BigInteger(bitLength - 2, randomSecureRandom);
        publicA = generatorValue.modPow(secretA, primeValue);

        // keys for personB
        secretB = new BigInteger(bitLength - 2, randomSecureRandom);
        publicB = generatorValue.modPow(secretB, primeValue);


        //--------------------------------------------------------------------------------------------------------------
        //Keys made and displayed:

        //should be same for A and B
        sharedKeyA = publicB.modPow(secretA, primeValue);
        sharedKeyB = publicA.modPow(secretB, primeValue);

        System.out.println("the shared key for A is " + sharedKeyA);
        System.out.println("the shared key for B is " + sharedKeyB);

        //--------------------------------------------------------------------------------------------------------------
        //Keys converted from BigInt to secret key: -

        byte[] key1Bytes = sharedKeyA.toByteArray();
        byte[] key2Bytes = sharedKeyA.toByteArray();

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key1Sha = sha.digest(key1Bytes);
        byte[] key2Sha = sha.digest(key2Bytes);

        byte[] key1 = Arrays.copyOf(key1Sha, 16); // use only first 128 bits
        SecretKey secretKey1 = new SecretKeySpec(key1, "AES");

        byte[] key2 = Arrays.copyOf(key2Sha, 16); // use only first 128 bits
        SecretKey secretKey2 = new SecretKeySpec(key1, "AES");


        //--------------------------------------------------------------------------------------------------------------
        //Converting message to Ascii values:

        DataInputStream in = new DataInputStream(System.in);
        System.out.println("Enter the plain text:");    //message input
        message = in.readLine();

        message.trim();     //removes whitespace trails
        byte[] messageByte = message.getBytes("UTF-8");
        System.out.println("Bytecode address is as follows: " + messageByte);



        //source: http://stackoverflow.com/questions/7401941/how-to-convert-string-into-byte-and-back


        //--------------------------------------------------------------------------------------------------------------
        //Encryption    -

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey1);

            ciphertext = cipher.doFinal(messageByte);
            System.out.println("Encrypted Ciphertext: " + new String(ciphertext));

        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }

        //--------------------------------------------------------------------------------------------------------------
        //Decryption

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey2);

            byte[] messageChar = cipher.doFinal(ciphertext);
            plaintext = new String(messageChar, "UTF-8");
            System.out.println("Decrypted Plaintext: " + plaintext);

        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
    }
}

//Help for encrypt and decrypt: http://aesencryption.net/
//Help From: https://github.com/pannous/Diffie-Hellman/blob/master/DH.java