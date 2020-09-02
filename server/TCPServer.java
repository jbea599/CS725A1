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
import java.util.concurrent.TimeUnit;

class TCPServer { 
	static boolean loggedin = false;
	static boolean userFound = false;
	static boolean accFound = false;
	static boolean passFound = false;
	static String parts[];
	static String nameFile = "";
	static int transferType = 1; // 1 corresponds to ASCII, 2 coressponds to binary and 3 to Continuous 
	private static File defaultDIR = FileSystems.getDefault().getPath("").toFile().getAbsoluteFile();
	private static String dirString = defaultDIR.getPath(); 
	static private DataOutputStream dataOutToClient;


	public static void main(String argv[]) throws Exception {
		String clientSentence;

		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			TCPServer.dataOutToClient = new DataOutputStream(connectionSocket.getOutputStream()); // create byte-based stream output
			String response = "testds";
			outToClient.writeBytes("+ Localhost SFTP service" + '\n');
			while (true) {
				System.out.println(dirString);
				System.out.println("Working Directory = " + System.getProperty("user.dir"));
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
						response = TCPServer.CDIR(clientSentence.substring(5));
					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("KILL")) {
					if(TCPServer.loggedin == true){
						response = TCPServer.KILL(clientSentence.substring(5));
					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("NAME")) {
					if(TCPServer.loggedin == true){
						response = TCPServer.NAME(clientSentence.substring(5));
					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if(clientSentence.contains("TOBE")){
					if(TCPServer.loggedin == true){
						response = TCPServer.TOBE(clientSentence.substring(5));
					}else{
						response = "! Cannot use this function until logged-in";
					}

				} else if (clientSentence.contains("RETR")) {
					if(TCPServer.loggedin == true){
						response = TCPServer.RETR(clientSentence.substring(5), outToClient, inFromClient);
					}else{
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("STOR")) {
					if(TCPServer.loggedin == true){
						response = TCPServer.STOR(clientSentence.substring(5));
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

		File file=new File(dirString + "\\users.txt");    //creates a new file instance  
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
			filepath = new File(dirString);
		}else{ 	
			filepath = new File(dirString + "\\" + dir); 
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

	public static String CDIR(String input){
		
		if(input.contains("~")){
			TCPServer.dirString = defaultDIR.getPath();
			return("! Changed working dir to default");
			
		}else{
			String newDir = TCPServer.dirString + "\\" + input;
			System.out.println(newDir);
			File newDirFILE = new File(newDir);
			if(newDirFILE.isDirectory()){
				TCPServer.dirString = newDir;
				return("! Changed working dir to " + input);
			}else{
				return("- Can't connect to directory because: directory does not exist");
			}
		}
	}

	public static String KILL(String input){ 
		File f = new File(dirString + "\\" + input);
		boolean deleted;
		try {
			deleted = f.delete();
		} catch (Exception e) {
			return("Error");
		}
		
		if(deleted){
			return("+ File deleted");
		}else{
			return("- Invalid file");
		}
	}

	public static String NAME(String input){

		String files[] = defaultDIR.list();
		for(String s: files){
			if(s.equals(input)){
				System.out.println(s + " " + input);
				TCPServer.nameFile = input;
				return ("+ File exists");
			}
		}
		return ("- Can't find " + input);
	}

	public static String TOBE(String input){
		File file = new File(TCPServer.nameFile);

		File file2 = new File(input);

		if (file2.exists()){
			return("- File  already exists");
		}
		boolean success = file.renameTo(file2);
		if(success){
			return(TCPServer.nameFile + " renamed to " + input);
		}else{
			return("- An unknown error occured");
		}
	}
	
	public static String RETR(String input, DataOutputStream outToClient, BufferedReader inFromClient) throws IOException,
			InterruptedException {
		File file1 = new File(input);
		System.out.println("Inside RETR");
		if(file1.exists()){
			long filesize = file1.length();
			outToClient.writeBytes(String.valueOf(filesize) + '\0');
			System.out.println("Waiting for response");
			String cResponse = inFromClient.readLine();
			if(cResponse.equals("SEND")){
				System.out.println("Received send response, sending data");
				byte[] byteBuffer = new byte[(int) file1.length()];
				int bytesCounter; 
				FileInputStream fs = new FileInputStream(file1);
				BufferedInputStream binaryStream = new BufferedInputStream(fs);
				while ((bytesCounter = binaryStream.read(byteBuffer)) >= 0) {
					dataOutToClient.write(byteBuffer, 0, bytesCounter);
				}
				binaryStream.close();
				fs.close();
				dataOutToClient.flush();
				TimeUnit.SECONDS.sleep(2);
				System.out.println("Finished sending");

			}else{ 
				System.out.println("received a stop response, finsihing");
				return("+ ok, RETR aborted");
			}


		}else{
			return("- File doesn't exist");
		}

		return("Finished sending");
	}
	public static String STOR(String input){
		return("");
		
	}

	public static void RESET(){
		TCPServer.userFound = false;
		TCPServer.accFound = false;
		TCPServer.passFound = false;
		TCPServer.loggedin = false;
	}
		
} 