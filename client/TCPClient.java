
/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import java.io.*;
import java.net.*;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;

class TCPClient {

    private static File defaultDIR = FileSystems.getDefault().getPath("").toFile().getAbsoluteFile();
    private static String dirString = defaultDIR.getPath();
    private static Socket clientSocket;
    private static DataOutputStream outToServer;
    private static DataOutputStream dataOutToServer;
    private static BufferedReader inFromUser;
    private static BufferedReader inFromServer;
    private static BufferedInputStream dataInFromServer;

    private static int character = 0;
    private static String sentence;
    private static String response = "";
    private static String fileToSend;

    public static void main(String argv[]) throws Exception {

        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        clientSocket = new Socket("localhost", 6789);
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        dataInFromServer = new BufferedInputStream(clientSocket.getInputStream());
        dataOutToServer = new DataOutputStream(clientSocket.getOutputStream()); // Used for Stor

        sentence = inFromServer.readLine();
        System.out.println(sentence);

        while (true) {
            // System.out.println(dirString);
            // System.out.println("Working Directory = " + System.getProperty("user.dir"));
            sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + '\n');

            getMessage();

            if (sentence.contains("RETR") & (!response.contains("-"))) {
                RETRlogic();
            }

            if (sentence.contains("STOR")) {
                fileToSend = sentence.substring(9);
                if (response.contains("+")) {
                    File filets = new File(fileToSend);
                    long filesize = filets.length();
                    System.out.println(filesize);
                    outToServer.writeBytes("SIZE " + filesize + '\n');
                    getMessage();
                    if (response.contains("+")) {
                        System.out.println("Received send response, sending data");
                        byte[] byteBuffer = new byte[(int) fileToSend.length()];
                        int bytesCounter;
                        FileInputStream fs = new FileInputStream(fileToSend);
                        BufferedInputStream binaryStream = new BufferedInputStream(fs);

                        while ((bytesCounter = binaryStream.read(byteBuffer)) >= 0) {
                            dataOutToServer.write(byteBuffer, 0, bytesCounter);
                        }
                        binaryStream.close();
                        fs.close();
                        dataOutToServer.flush();
                        TimeUnit.SECONDS.sleep(2);
                        System.out.println("Finished sending");
                        getMessage();
                    }
                }
            }

            if (response.equals("+localhost closing connection")) {
                break;
            }
        }
        clientSocket.close();

    }

    public static void getMessage() throws IOException {
        response = "";
        while (true) {
            character = inFromServer.read();
            if (character == 0) {
                break;
            }
            response = response.concat(Character.toString((char) character));
        }

        System.out.println("FROM SERVER: " + response);
    }

    public static void RETRlogic() throws IOException {
        String requestedFile = sentence.substring(5);
        System.out.println(requestedFile);
        boolean validSend = false;
        int sizefile = Integer.parseInt(response);
        while (!validSend) {
            System.out.println("inside valid send while loop, waiting to read from client ");
            sentence = inFromUser.readLine();
            if (sentence.equals("SEND") || (sentence.equals("STOP"))) {
                validSend = true;
                System.out.println("valid send is true");
            } else {
                System.out.println("Not a valid message, send either SEND or STOP");
            }
        }
        outToServer.writeBytes(sentence + '\n');

        if (sentence.equals("SEND")) {
            RETRfile(requestedFile, sizefile);
        }
        getMessage();

    }

    public static void RETRfile(String requestedFile, int sizefile) throws IOException {
        File file = new File(dirString + "\\" + requestedFile);
        System.out.println(file.getPath());
        FileOutputStream fs = new FileOutputStream(file);
        BufferedOutputStream binaryOutputStream = new BufferedOutputStream(fs);
        for (int i = 0; i < sizefile; i++) {
            binaryOutputStream.write(dataInFromServer.read());
        }
        binaryOutputStream.close();
        fs.close();
        System.out.println("+A new file was received: " + requestedFile);
    }
}
