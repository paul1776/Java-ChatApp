import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

public class ChattyChatChatClient {
    static boolean done = false;

    public static synchronized void printToScreen(boolean eraseLine, String to_print) {
        if (eraseLine) {
            System.out.print(String.format("\033[%dA", 1)); // Move up
            System.out.print("\033[2K"); // Erase line content
        } else {
            System.out.println(to_print);
        }
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 40000;
        Socket socket = null;

        try {
            socket = new Socket(hostname, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

            // To print the two lines of the welcome message
            for (int i = 0; i < 2; i++) {
                printToScreen(false, in.readLine());
            }

            // Thread to check for user input and send to Server
            Thread checkForInput = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!done) {
                            String userInput = userIn.readLine();

                            // Removes the past line to format the message with username at the server level!
                            printToScreen(true, "");

                            // Send message to server
                            out.println(userInput);

                            if (userInput.toLowerCase().equals("/quit")) { done = true; }
                        }
                    } catch(IOException e){
                        System.out.println("Error receiving from server");
                        done = true;
                    }
                }
            });

            // Thread to check for inbound messages and print to command line
            Thread checkForMessages = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!done) {
                        try {
                            // read the message sent to this client
                            String response = in.readLine();
                            printToScreen(false, response);
                        } catch (Exception e) { System.out.println("Error connecting to server"); }
                    }
                }
            });

            checkForMessages.start();
            checkForInput.start();

            if (done) { socket.close(); }
        } catch (IOException e) { System.out.println("Error connecting to server"); }
    }
}