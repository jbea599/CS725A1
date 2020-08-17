/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import java.io.*; 
import java.net.*; 

class TCPServer { 
    
    public static void main(String argv[]) throws Exception 
    { 
		String clientSentence; 
		String capitalizedSentence; 
		ServerSocket welcomeSocket = new ServerSocket(6789); 
		while(true){
			Socket connectionSocket = welcomeSocket.accept(); 


			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
			
			DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 

			while(true) { 

				clientSentence = inFromClient.readLine(); 
				
				if(clientSentence.equals("DONE")){
					outToClient.writeBytes("+localhost closing connection" + '\n');
					break;

				}else if(clientSentence.contains("USER")){

				}else if(clientSentence.contains("ACCT")){

				}else if(clientSentence.contains("PASS")){

				}else if(clientSentence.contains("TYPE")){

				}else if(clientSentence.contains("LIST")){

				}else if(clientSentence.contains("CDIR")){

				}else if(clientSentence.contains("KILL")){

				}else if(clientSentence.contains("NAME")){

				}else if(clientSentence.contains("RETR")){

				}else if(clientSentence.contains("STOR")){

				}else{
					outToClient.writeBytes("Invalid Command. Please try again" + '\n'); 
				}
				
				
			} 
		}
	}


	public void USER(String input){

	}
	public void ACCT(String input){
		
	}
	public void PASS(String input){
		
	}
	public void TYPE(String input){
		
	}
	public void LIST(String input){
		
	}
	public void CDIR(String input){
		
	}
	public void KILL(String input){
		
	}
	public void NAME(String input){
		
	}
	public void RETR(String input){
		
	}
	public void STOR(String input){
		
	}
	

	
	
} 

