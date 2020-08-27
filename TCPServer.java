/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import java.io.*; 
import java.net.*; 

class TCPServer { 
	static boolean loggedin = false;

	public static void main(String argv[]) throws Exception {
		String clientSentence;

		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			String response = "testds";
			while (true) {

				clientSentence = inFromClient.readLine();

				if (clientSentence.equals("DONE")) {
					outToClient.writeBytes("+localhost closing connection" + '\n');
					break;

				} else if (clientSentence.contains("USER")) {
					response = TCPServer.USER(clientSentence.substring(5));

				} else if (clientSentence.contains("ACCT")) {

				} else if (clientSentence.contains("PASS")) {

				} else if (clientSentence.contains("TYPE")) {

				} else if (clientSentence.contains("LIST")) {

				} else if (clientSentence.contains("CDIR")) {

				} else if (clientSentence.contains("KILL")) {

				} else if (clientSentence.contains("NAME")) {

				} else if (clientSentence.contains("RETR")) {

				} else if (clientSentence.contains("STOR")) {

				} else {
					response = "Invalid Command. Please try again";
				}
				System.out.println(response);
				outToClient.writeBytes(response + '\n');

			}
		}
	}

	public static String USER(String input) {
		return(input);
	}

	public static void ACCT(String input){
		
	}
	public static void PASS(String input){
		
	}
	public static void TYPE(String input){
		
	}
	public static void LIST(String input){
		
	}
	public static void CDIR(String input){
		
	}
	public static void KILL(String input){
		
	}
	public static void NAME(String input){
		
	}
	public static void RETR(String input){
		
	}
	public static void STOR(String input){
		
	}
		
} 