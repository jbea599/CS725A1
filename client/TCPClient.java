package client;

/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import java.io.*; 
import java.net.*; 
class TCPClient { 
    
    public static void main(String argv[]) throws Exception 
    { 
        String sentence; 
        String response = "";
	
        BufferedReader inFromUser = 
	    new BufferedReader(new InputStreamReader(System.in)); 
	
        Socket clientSocket = new Socket("localhost", 6789); 
	
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
	
        
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        int character = 0;

        while(true){
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
            if(response.equals("+localhost closing connection")){
                break;
            }
        }
        clientSocket.close(); 
	
    } 
} 
