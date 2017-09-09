package Project; /**
 * Created by Karstan on 23/03/2017.
 */

import java.io.*;
import java.net.*;
import java.util.*;


public class Client {

    //port, server, channel
    private static final int port = 6665;
    private static final String server = "secmess.ddns.net";
    private static final String channel = "#hello";

    //scanner in and writer out
    private static Scanner in;
    private static PrintWriter out;

    //nickname, username & message
    private static String nickname;
    private static String username;
    private static String message;

    public static void main(String[] args) throws Exception {
        //setup scanner and socket
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket(server, port);
        System.out.println("You are now connected");

        //initialise reader and writer
        out = new PrintWriter(socket.getOutputStream(), true); //gets output from server
        in = new Scanner(socket.getInputStream());  //gets input via scanner
        System.out.println("Scanner and Reader are established");

        //nickname + username
        System.out.println("Choose your nickname: ");
        nickname = scanner.nextLine();

        System.out.println("Choose a username: ");
        username = scanner.nextLine();

        //write your info to the server
        write("NICK", nickname);
        write("USER", username + " 0 * :" + nickname);
        write("JOIN", channel);

        //while messages print out - read it
        while (in.hasNext()) {
            String serverMessage = in.nextLine();
            System.out.println("<<<< " + serverMessage);

            //ping so user is not kicked
            if (serverMessage.startsWith("PING")) {
                String pingContents = serverMessage.split(" ", 2)[1];
                write("PONG", pingContents);
            }

            //stop scanner prompting you every half second
            if(serverMessage.contains(":End of")) {
                 System.out.println("Enter a message: ");
                 message = scanner.nextLine();

                 //write message to server
                 write("PRIVMSG", channel + " :" + message);
            }
        }
        //close all loose ends
        in.close();
        out.close();
        socket.close();
        System.out.println("Program Finished");
    }

    //write function that allows messages to send to server
    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>>> " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}

//Help from: https://www.youtube.com/watch?v=Us_1xPgwLz0
