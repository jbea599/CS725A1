/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import java.io.*; 
import java.net.*;
import java.util.*;

class TCPServer { 
	static boolean loggedin = false;
	static boolean userFound = false;
	static boolean accFound = false;
	static boolean passFound = false;
	static String parts[];

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
					TCPServer.RESET();
					break;

				} else if (clientSentence.contains("USER")) {
					response = TCPServer.USER(clientSentence.substring(5));

				} else if (clientSentence.contains("ACCT")) {
					response = TCPServer.ACCT(clientSentence.substring(5));

				} else if (clientSentence.contains("PASS")) {
					response = TCPServer.PASS(clientSentence.substring(5));

				} else if (clientSentence.contains("TYPE")) {
					if(TCPServer.loggedin == true){

					}else{
						outToClient.writeBytes("! Cannot use this function until logged-in" + '\n');
					}
				} else if (clientSentence.contains("LIST")) {
					if(TCPServer.loggedin == true){

					}else{
						outToClient.writeBytes("! Cannot use this function until logged-in" + '\n');
					}
				} else if (clientSentence.contains("CDIR")) {
					if(TCPServer.loggedin == true){

					}else{
						outToClient.writeBytes("! Cannot use this function until logged-in" + '\n');
					}
				} else if (clientSentence.contains("KILL")) {
					if(TCPServer.loggedin == true){

					}else{
						outToClient.writeBytes("! Cannot use this function until logged-in" + '\n');
					}
				} else if (clientSentence.contains("NAME")) {
					if(TCPServer.loggedin == true){

					}else{
						outToClient.writeBytes("! Cannot use this function until logged-in" + '\n');
					}
				} else if (clientSentence.contains("RETR")) {
					if(TCPServer.loggedin == true){

					}else{
						outToClient.writeBytes("! Cannot use this function until logged-in" + '\n');
					}
				} else if (clientSentence.contains("STOR")) {
					if(TCPServer.loggedin == true){

					}else{
						outToClient.writeBytes("! Cannot use this function until logged-in" + '\n');
					}
				} else {
					response = "Invalid Command. Please try again";
				}
				System.out.println(response);
				outToClient.writeBytes(response + '\n');

			}
		}
	}

	public static String USER(String input) throws FileNotFoundException {

		if(input.equals("guest")){
			TCPServer.loggedin = true;
			return("! guest logged in");
		}

		File file=new File("users.txt");    //creates a new file instance  
		Scanner scanner = new Scanner(file);
		int lineNum = 0;
    	while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			lineNum++;
			TCPServer.parts = line.split(" ");
			System.out.println(parts[0] + " " + parts[1] + " " + parts[2]);
			if(parts[0].contains(input)) {
				System.out.println("found the username on line: " + lineNum);
				TCPServer.userFound = true;
				break;
			}
		}
		if(TCPServer.userFound == false){
			return("- Invalid user-id, try again");
		}else{
			return("+ User-id valid, send account and password");
		}

	}

	public static String ACCT(String input) throws FileNotFoundException {
		System.out.println(parts[1]);
		System.out.println(input);

		if(parts[1].equals(input)) {
			TCPServer.accFound = true;
		}
		
		if(TCPServer.accFound == false){
			return("- Invalid account, try again");
		}else if(TCPServer.accFound == true && TCPServer.passFound == false){
			return("+ Account valid, send password");
		}else{
			TCPServer.loggedin = true;
			return("! Account valid, logged-in");
		}
	}
	public static String PASS(String input) throws FileNotFoundException {
		System.out.println(parts[2]);
		System.out.println(input);
		
		if(parts[2].equals(input)) {
			TCPServer.passFound = true;
		}
		
		if(TCPServer.passFound == false){
			return("- Wrong password, try again");
		}else if(TCPServer.passFound == true && TCPServer.accFound == false){
			return("+ Send account");
		}else{
			TCPServer.loggedin = true;
			return("! Logged-in");
		}
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

	public static void RESET(){
		TCPServer.userFound = false;
		TCPServer.accFound = false;
		TCPServer.passFound = false;
		TCPServer.loggedin = false;
	}
		
} 