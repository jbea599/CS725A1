/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import java.io.*;
import java.net.*;
import java.nio.file.FileSystems;

class TCPClient { 

    private static File defaultDIR = FileSystems.getDefault().getPath("").toFile().getAbsoluteFile();
    private static String dirString = defaultDIR.getPath(); 
    
    public static void main(String argv[]) throws Exception 
    { 
        String sentence; 
        String response = "";
	
        BufferedReader inFromUser = 
	    new BufferedReader(new InputStreamReader(System.in)); 
	
        Socket clientSocket = new Socket("localhost", 6789); 
	
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
	
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        BufferedInputStream dataInFromServer = new BufferedInputStream(clientSocket.getInputStream());
        


        int character = 0;
        sentence = inFromServer.readLine();
        System.out.println(sentence);

        while(true){
            System.out.println(dirString);
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            sentence = inFromUser.readLine(); 
            outToServer.writeBytes(sentence + '\n'); 

            System.out.println("Waiting for response");
            response = "";
            while(true){
                character = inFromServer.read();
                if(character == 0){
                    break;
                }
                response = response.concat(Character.toString((char)character));
            }

            System.out.println("FROM SERVER: " + response);

            if(sentence.contains("RETR") & (!response.contains("-"))){
                String requestedFile = sentence.substring(5);
                System.out.println(requestedFile);
                boolean validSend = false;
                int sizefile = Integer.parseInt(response);
                while(!validSend){
                    System.out.println("inside valid send while loop, waiting to read from client ");
                    sentence = inFromUser.readLine();
                    if(sentence.equals("SEND") || (sentence.equals("STOP"))){
                        validSend = true;
                        System.out.println("valid send is true");
                    }else{
                        System.out.println("Not a valid message, send either SEND or STOP");
                    }
                }
                outToServer.writeBytes(sentence + '\n');

                if(sentence.equals("SEND")){ // receive aborted message then will return to normal operation
                    File file = new File(dirString + "\\" + requestedFile);
                    System.out.println(file.getPath());
                    // by default, files overwrite existing ones
                    FileOutputStream fs = new FileOutputStream(file);
                    BufferedOutputStream binaryOutputStream = new BufferedOutputStream(fs);
                    for (int i = 0; i < sizefile; i++) {
                        binaryOutputStream.write(dataInFromServer.read());
                    }
                    binaryOutputStream.close();
                    fs.close();
                    System.out.println("+A new file was received: " + requestedFile);
                }
                response = "";
                while(true){
                    character = inFromServer.read();
                    if(character == 0){
                        break;
                    }
                    response = response.concat(Character.toString((char)character));
                }
                System.out.println(response);
            }

            if(response.equals("+localhost closing connection")){
                break;
            }
        }
        clientSocket.close(); 
	
    } 
} 
