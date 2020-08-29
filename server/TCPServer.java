package server;

/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;

class TCPServer { 
	static boolean loggedin = false;
	static boolean userFound = false;
	static boolean accFound = false;
	static boolean passFound = false;
	static String parts[];
	static int transferType = 1; // 1 corresponds to ASCII, 2 coressponds to binary and 3 to Continuous 
	private static final File defaultDIR = FileSystems.getDefault().getPath("").toFile().getAbsoluteFile();

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
					outToClient.writeBytes("+localhost closing connection");
					TCPServer.RESET();
					break;

				} else if (clientSentence.contains("USER")) {
					response = TCPServer.USER(clientSentence.substring(5));
					System.out.println(defaultDIR);

				} else if (clientSentence.contains("ACCT")) {
					response = TCPServer.ACCT(clientSentence.substring(5));

				} else if (clientSentence.contains("PASS")) {
					response = TCPServer.PASS(clientSentence.substring(5));

				} else if (clientSentence.contains("TYPE")) {
					if(TCPServer.loggedin == true){
						response = TCPServer.TYPE(clientSentence.substring(5));
					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("LIST")) {
					if(TCPServer.loggedin == true){
						ArrayList<String> ar = new ArrayList<String>();
						String test[] = clientSentence.split(" ");

						ar.add(test[0]);
						ar.add(test[1]);
						if(test.length == 2){
							ar.add("\0");
						}else{
							ar.add(test[2]);
						}
						response = TCPServer.LIST(ar.get(1).toString(), ar.get(2).toString(), outToClient);
					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("CDIR")) {
					if(TCPServer.loggedin == true){

					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("KILL")) {
					if(TCPServer.loggedin == true){

					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("NAME")) {
					if(TCPServer.loggedin == true){

					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("RETR")) {
					if(TCPServer.loggedin == true){

					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("STOR")) {
					if(TCPServer.loggedin == true){

					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else {
					response = "Invalid Command. Please try again";
				}
				if(!(response.equals("NO RESPONSE"))){
					System.out.println(response);
					outToClient.writeBytes(response + '\0');
				}else{
					System.out.println("response already sent in function");
				}
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

	public static String TYPE(String input){
		if(input.equals("a")){
			TCPServer.transferType = 1;
			return("+ Using Ascii mode");
		}else if(input.equals("b")){
			TCPServer.transferType = 2;
			return("+ Using Binary mode");
		}else if(input.equals("c")){
			TCPServer.transferType = 3;
			return("+ Using Continuous mode");
		}else{
			return("- Type not valid");
		}

	}
	public static String LIST(String input, String dir, DataOutputStream outToClient) throws IOException {
		
		File files[];
		String response = "";
		File filepath;
		
		if(dir.equals("\0")){
			filepath = defaultDIR;
		}else{ 	
			filepath = FileSystems.getDefault().getPath(dir).toFile().getAbsoluteFile(); 
		}

		files = filepath.listFiles();

		response = filepath.getAbsolutePath();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm");

		if(input.equals("F") || input.equals("V")){
			for(File file : files){
				response = response + '\n' + file.getName();
				if(input.equals("V")){
					long modifiedTime = file.lastModified();
					String modifiedDate = dateFormat.format(new Date(modifiedTime));
					response += "    " + modifiedDate + "    SIZE: " + file.length();

				}

			}
		}else{
			response = "- Invalid listing type, choose F or V";
		}
		return(response);
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