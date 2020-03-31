import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChattyChatChatClient {
    public static void main(String[] args) {
        String hostname = "ChattyChatChatServer";
        int port = int(args[1]);
        Socket socket = null;
        try {
            socket = new Socket(hostname, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userIn = new BufferedReader(new InputStreamReader( System.in));
            for (int i = 0; i < 2; i++) {
                System.out.println( in.readLine() );
            }
            boolean done = false;
            while ( !done ) {
                String userInput = userIn.readLine();
                out.println( userInput );
                if ( userInput.equals(".") ) { done = true; }
                String response = null;
                try {
                    response = in.readLine();
                    if (response == null || response.equals("") ) { done = true; }
                } catch (IOException e) {
                    System.out.println("Error receiving from server");
                    done = true;
                }
                System.out.println( response );
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Error connecting to server");
        }
    }
}