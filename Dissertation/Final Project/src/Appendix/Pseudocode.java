package Project;

/**
 * Created by Karstan on 01/05/2017.
 */
public class Pseudocode {
}

/**     Client: -
 *
 * set server name, port number, channel
 * initialise scanner and reader streams
 * connect to server through socket
 * if login complete then
 * print 'logged in'
 * choose nickname
 * choose username
 * write nickname to server
 * write username to server
 * join channel
 * print 'channel joined'
 * while in.hasNext()
 * read from server
 * write messages to server
 * read messages from server
 * if message sent
 * if no print 'message cannot send'
 * if yes print 'message sent'
 * end
 */





/**     Encryption & decryption: -
 *
 * prime chosen (or p) = 23
 * primitive root modulo of prime (or g) = 5
 * alice choses a secret integer a = 6, then sends bob A = ga mod p which equals 8
 * bob choses a secret integer a = 15, then sends bob A = ga mod p which equals 15
 * alice compoutes s = ab mod p which = 2
 * bob computes s = ab mod p which = 2
 * both parties now share a secret: 2
 * key1 is sent to Alice
 * key2 is sent to Bob
 *
 * Alice decides to send a message to Bob
 * Alice's key is converted to bytecode
 * message string input is received
 * message string is converted into bytecode
 * initialisation vector is created
 * message, iv and key bytecode are wrapped
 * message is encrypted to ciphertext
 * ciphertext is sent
 * ciphertext is received
 * ciphertext is stripped of iv and key to create plaintext
 * plaintext is converted from bytecode
 * plaintext is read by Bob
 */

/*
//http://stackoverflow.com/questions/26828649/diffiehellman-key-exchange-to-aes-or-desede-in-java
//http://stackoverflow.com/questions/29267435/generating-iv-for-aes-in-java
//http://stackoverflow.com/questions/7401941/how-to-convert-string-into-byte-and-back
//https://www.youtube.com/watch?v=Us_1xPgwLz0   (irc client)
//
 */
