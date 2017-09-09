/**
 * Created by Matthew Sellman on 10/10/2016.
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Timestamp;
import java.util.Random;
import java.util.Scanner;

public class Cipher     //main class
{
    private static BigInteger p, q, phi, n, e, d;       //variables for RSA
    private static int bitlen = 512;        //bit length for string conversion
    private static Random rand; //random integer
    private static String message;  //user input string for encryption

    public Cipher() {   //constructor for main class
        rand = new Random();    //implementation of random
        p = BigInteger.probablePrime(bitlen, rand); //first prime number
        q = BigInteger.probablePrime(bitlen, rand); //second prime number
        n = p.multiply(q);  //multiply together for one way function
        e = BigInteger.probablePrime(bitlen / 2, rand); //value for use in key
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));  //phi, or totient for key usage
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)    //while the greatest common multiple is greater than zero & less than phi
        {
            e.add(BigInteger.ONE);  //add one to integer
        }
        d = e.modInverse(phi);  //inverse mod e in comparison to phi
    }

    public static void main(String[] args) throws IOException {     //main function

        //// TODO: 22/11/2016 : Beginning of task 1

        Scanner sc = new Scanner(System.in);
        System.out.println("I would recommend choosing 37 and 47 as prime numbers");    //two prime numbers for secure usage
        System.out.println("Choose an insecure case, by entering 5,7 & a string of repeating characters\n");    // an insecure case for the program

        System.out.print("Enter first prime as int: "); //user input for first prime
        while (!sc.hasNextInt()) sc.next();
        p = BigInteger.valueOf(sc.nextInt());

        System.out.print("Enter second prime as int: "); //second user input for first prime
        while (!sc.hasNextInt()) sc.next();
        q = BigInteger.valueOf(sc.nextInt());

        Cipher rsa = new Cipher();  //implementation of cipher within main
        DataInputStream in = new DataInputStream(System.in);
        System.out.println("Enter the plain text:");
        message = in.readLine();

        // encrypt and send
        byte[] encrypted = rsa.encryption(message.getBytes());  //encryption from bytes to rsa
        System.out.println("Encrypting Message: " + message);   //lets user know message is being encrypted
        System.out.println("Encrypted Message: " + encrypted);  //let user see the encrypted message
        System.out.println("Message sent!");    //lets user know their message has been sent
        System.out.println("Bob has received your message");    //Letting the user know that their message has been recieved

        //decrypt and send
        byte[] decrypted = rsa.decryption(encrypted);    // decryption from rsa into bytes
        System.out.println("Decrypting Your Message: " + decrypted);    // shows message having been encrypted
        System.out.println("Decrypted Message: " + new String(decrypted));  // shows message having been decrypted

        //// TODO: 22/11/2016 : End of task 1






        //// TODO: 22/11/2016 : Beginning of task 2

        //first do an interaction as above where you contact server
        System.out.println("\n");   //new line
        System.out.println("Task 2");   //title
        System.out.println("\n");   //newline
        Scanner scan = new Scanner(System.in);  //scanner for user input
        System.out.println("Would you like Bobs public key? Yes or No (case sensitive): "); // user input here
        while (!scan.hasNext()) {
            scan.next();
        }
            if (scan.next().equals("Yes")) {              //if user chooses yes
                //server gives you bobs key
                System.out.println(" Dear User, Here is B’s public key signed by me. Yours sincerely, S. ");
            }

        DataInputStream into = new DataInputStream(System.in);  //data input stream for plaintext
        System.out.println("Enter the plain text message to send to bob through server: "); //prompt
        message = into.readLine();  //read line
        System.out.println("Encrypting Message: " + message);   // message to be encrypted
        encrypted = rsa.encryption(message.getBytes()); //encryption
        System.out.println("Encrypted Message: " + encrypted);  // message to be encrypted
        //you write a message to bob using that key
        System.out.println("Message sent!");    // message sent prompt

        //bob decrypts message and reads it (optional?)

        //bob contacts server and ask for key
        System.out.println( "Dear S, This is B and I would like to get A’s public key. Yours sincerely, B. ");
        //server gives bob a key
        System.out.println("Dear B, Here is A’s public key signed by me. Yours sincerely, S.\n");
        //bob sends message to you

        message = "Dear User, Here is my nonce and yours, proving I decrypted it. Yours sincerely, B."; //bobs message
        System.out.println("Encrypting Bobs Message: ");    //lets user know somethings going on

        encrypted = rsa.encryption(message.getBytes());  //encryption
        System.out.println("Encrypted Message: " + encrypted);  //encryoted message sent to user


        decrypted = rsa.decryption(encrypted);    // decryption
        System.out.println("Decrypting Bob's Message: " + decrypted);   //de-crypting bobs message from server
        System.out.println("Decrypted Message: " + new String(decrypted));  //decrypted message sent to user
        //you decrypt and read it
    }

    // Encrypt message
    public byte[] encryption(byte[] message) {  //message bytecodeDef
        return (new BigInteger(message)).modPow(e, n).toByteArray();    //encryption: m'= (m^e mod n)
    }

    // Decrypt message
    public byte[] decryption(byte[] message) {  //message bytecode
        return (new BigInteger(message)).modPow(d, n).toByteArray();    //inverse of e mod phi:- d % phi = 1
    }
}
