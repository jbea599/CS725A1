
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
	static boolean canSize = false;

	static String parts[];
	static String nameFile = "";
	static int transferType = 2; // 1 corresponds to ASCII, 2 coressponds to binary and 3 to Continuous
	private static File defaultDIR = FileSystems.getDefault().getPath("").toFile().getAbsoluteFile();
	private static String dirString = defaultDIR.getPath();

	static File fileToStor;
	static String fileToStorSTRING;

	static Socket connectionSocket;
	static ServerSocket welcomeSocket;

	static String clientSentence;
	static String response;

	static BufferedReader inFromClient;
	static DataOutputStream outToClient;
	static DataOutputStream dataOutToClient;

	static BufferedInputStream dataInFromClient;

	public static void main(String argv[]) throws Exception {

		welcomeSocket = new ServerSocket(6789);
		System.out.println("Server started");

		while (true) {
			connectionSocket = welcomeSocket.accept();

			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			dataOutToClient = new DataOutputStream(connectionSocket.getOutputStream()); // Used for RETR
			dataInFromClient = new BufferedInputStream(connectionSocket.getInputStream());

			outToClient.writeBytes("+ Localhost SFTP service" + '\n');
			while (true) {
				// System.out.println(dirString);
				// System.out.println("Working Directory = " + System.getProperty("user.dir"));
				clientSentence = inFromClient.readLine();

				if (clientSentence.equals("DONE")) {
					outToClient.writeBytes("+localhost closing connection");
					RESET();
					break;

				} else if (clientSentence.contains("USER")) {
					response = USER(clientSentence.substring(5));
					System.out.println(defaultDIR);

				} else if (clientSentence.contains("ACCT")) {
					response = ACCT(clientSentence.substring(5));

				} else if (clientSentence.contains("PASS")) {
					response = PASS(clientSentence.substring(5));

				} else if (clientSentence.contains("TYPE")) {
					if (loggedin == true) {
						response = TYPE(clientSentence.substring(5));
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("LIST")) {
					if (loggedin == true) {
						ArrayList<String> ar = new ArrayList<String>();
						String test[] = clientSentence.split(" ");

						ar.add(test[0]);
						ar.add(test[1]);
						if (test.length == 2) {
							ar.add("\0");
						} else {
							ar.add(test[2]);
						}
						response = LIST(ar.get(1).toString(), ar.get(2).toString());
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("CDIR")) {
					if (loggedin == true) {
						response = CDIR(clientSentence.substring(5));
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("KILL")) {
					if (loggedin == true) {
						response = KILL(clientSentence.substring(5));
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("NAME")) {
					if (loggedin == true) {
						response = NAME(clientSentence.substring(5));
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("TOBE")) {
					if (loggedin == true) {
						response = TOBE(clientSentence.substring(5));
					} else {
						response = "! Cannot use this function until logged-in";
					}

				} else if (clientSentence.contains("RETR")) {
					if (loggedin == true) {
						response = RETR(clientSentence.substring(5));
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("STOR")) {
					if (loggedin == true) {
						String test[] = clientSentence.split(" ");
						response = STOR(test[1], test[2]);
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else if (clientSentence.contains("SIZE")) {
					if (loggedin == true) {
						response = SIZE(clientSentence.substring(5));
					} else {
						response = "! Cannot use this function until logged-in";
					}
				} else {
					response = "Invalid Command. Please try again";
				}
				if (!(response.equals("NO RESPONSE"))) {
					System.out.println(response);
					outToClient.writeBytes(response + '\0');
				} else {
					System.out.println("response already sent in function");
				}
			}
		}
	}

	public static String USER(String input) throws FileNotFoundException {

		if (input.equals("guest")) {
			TCPServer.loggedin = true;
			return ("! guest logged in");
		}

		File file = new File(dirString + "\\users.txt"); // creates a new file instance
		Scanner scanner = new Scanner(file);
		int lineNum = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			lineNum++;
			TCPServer.parts = line.split(" ");
			System.out.println(parts[0] + " " + parts[1] + " " + parts[2]);
			if (parts[0].contains(input)) {
				System.out.println("found the username on line: " + lineNum);
				TCPServer.userFound = true;
				break;
			}
		}
		if (TCPServer.userFound == false) {
			return ("- Invalid user-id, try again");
		} else {
			return ("+ User-id valid, send account and password");
		}

	}

	public static String ACCT(String input) throws FileNotFoundException {
		System.out.println(parts[1]);
		System.out.println(input);

		if (parts[1].equals(input)) {
			TCPServer.accFound = true;
		}

		if (TCPServer.accFound == false) {
			return ("- Invalid account, try again");
		} else if (TCPServer.accFound == true && TCPServer.passFound == false) {
			return ("+ Account valid, send password");
		} else {
			TCPServer.loggedin = true;
			return ("! Account valid, logged-in");
		}
	}

	public static String PASS(String input) throws FileNotFoundException {
		System.out.println(parts[2]);
		System.out.println(input);

		if (parts[2].equals(input)) {
			TCPServer.passFound = true;
		}

		if (TCPServer.passFound == false) {
			return ("- Wrong password, try again");
		} else if (TCPServer.passFound == true && TCPServer.accFound == false) {
			return ("+ Send account");
		} else {
			TCPServer.loggedin = true;
			return ("! Logged-in");
		}
	}

	public static String TYPE(String input) {
		if (input.equals("a")) {
			TCPServer.transferType = 1;
			return ("+ Using Ascii mode");
		} else if (input.equals("b")) {
			TCPServer.transferType = 2;
			return ("+ Using Binary mode");
		} else if (input.equals("c")) {
			TCPServer.transferType = 3;
			return ("+ Using Continuous mode");
		} else {
			return ("- Type not valid");
		}

	}

	public static String LIST(String input, String dir) throws IOException {

		File files[];
		String response = "";
		File filepath;

		if (dir.equals("\0")) {
			filepath = new File(dirString);
		} else {
			filepath = new File(dirString + "\\" + dir);
		}

		files = filepath.listFiles();

		response = filepath.getAbsolutePath();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm");

		if (input.equals("F") || input.equals("V")) {
			for (File file : files) {
				response = response + '\n' + file.getName();
				if (input.equals("V")) {
					long modifiedTime = file.lastModified();
					String modifiedDate = dateFormat.format(new Date(modifiedTime));
					response += "    " + modifiedDate + "    SIZE: " + file.length();

				}

			}
		} else {
			response = "- Invalid listing type, choose F or V";
		}
		return (response);
	}

	public static String CDIR(String input) {

		if (input.contains("~")) {
			TCPServer.dirString = defaultDIR.getPath();
			return ("! Changed working dir to default");

		} else {
			String newDir = TCPServer.dirString + "\\" + input;
			System.out.println(newDir);
			File newDirFILE = new File(newDir);
			if (newDirFILE.isDirectory()) {
				TCPServer.dirString = newDir;
				return ("! Changed working dir to " + input);
			} else {
				return ("- Can't connect to directory because: directory does not exist");
			}
		}
	}

	public static String KILL(String input) {
		File f = new File(dirString + "\\" + input);
		boolean deleted;
		try {
			deleted = f.delete();
		} catch (Exception e) {
			return ("Error");
		}

		if (deleted) {
			return ("+ File deleted");
		} else {
			return ("- Invalid file");
		}
	}

	public static String NAME(String input) {

		String files[] = defaultDIR.list();
		for (String s : files) {
			if (s.equals(input)) {
				System.out.println(s + " " + input);
				TCPServer.nameFile = input;
				return ("+ File exists");
			}
		}
		return ("- Can't find " + input);
	}

	public static String TOBE(String input) {
		File file = new File(TCPServer.nameFile);

		File file2 = new File(input);

		if (file2.exists()) {
			return ("- File  already exists");
		}
		boolean success = file.renameTo(file2);
		if (success) {
			return (TCPServer.nameFile + " renamed to " + input);
		} else {
			return ("- An unknown error occured");
		}
	}

	public static String RETR(String input) throws IOException, InterruptedException {
		File file1 = new File(input);
		System.out.println("Inside RETR");
		if (file1.exists()) {
			long filesize = file1.length();
			outToClient.writeBytes(String.valueOf(filesize) + '\0');
			System.out.println("Waiting for response");
			String cResponse = inFromClient.readLine();
			if (cResponse.equals("SEND")) {
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

			} else {
				System.out.println("received a stop response, finsihing");
				return ("+ ok, RETR aborted");
			}

		} else {
			return ("- File doesn't exist");
		}

		return ("Finished sending");
	}

	public static String STOR(String input, String dir) {
		System.out.println("Inside STOR");
		fileToStor = new File(dir);

		fileToStorSTRING = dir;
		if (input.equals("NEW")) {
			if (fileToStor.exists()) {
				return ("- File exists, but system doesn't support generations");
			} else {
				canSize = true;
				return ("+ File does not exist, will create a new file");
			}

		} else if (input.equals("OLD")) {
			if (fileToStor.exists()) {
				canSize = true;
				return ("+ Will write over old file");
			} else {
				canSize = true;
				return ("+ Will create a new file");
			}

		} else if (input.equals("APP")) {
			return ("Havent done APP yet lol");
		} else {
			return ("Invalid command, send either NEW, OLD, or APP");
		}

	}

	public static String SIZE(String input) throws IOException {
		if (!(canSize)) {
			return ("- Need to use STOR command before size command");
		}
		long filesize = Long.valueOf(input);
		long drivesize = Files.getFileStore(defaultDIR.toPath().toRealPath()).getUsableSpace();
		if (filesize > drivesize) {
			return ("- Not enough room, dont send it");
		} else {
			outToClient.writeBytes("+ ok, waiting for file" + '\0');
			File file = new File(dirString + "\\" + fileToStorSTRING);
			System.out.println(file.getPath());
			FileOutputStream fs = new FileOutputStream(file);
			BufferedOutputStream binaryOutputStream = new BufferedOutputStream(fs);
			System.out.println(filesize);
			for (int i = 0; i < filesize; i++) {
				System.out.println("test");
				binaryOutputStream.write(dataInFromClient.read());
			}
			binaryOutputStream.close();
			fs.close();
			System.out.println("+A new file was received: " + fileToStorSTRING);
			canSize = false;
			return ("+ Saved " + fileToStorSTRING);

		}

	}

	public static void RESET() {
		TCPServer.userFound = false;
		TCPServer.accFound = false;
		TCPServer.passFound = false;
		TCPServer.loggedin = false;
		TCPServer.transferType = 1;
		TCPServer.parts[0] = "";
		TCPServer.parts[1] = "";
		TCPServer.parts[2] = "";
		canSize = false;
	}

}