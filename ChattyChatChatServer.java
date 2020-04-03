import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChattyChatChatServer {

    static Vector<ChattyChatChatRunnable> users = new Vector<>();

    static int clientNumber = 0;

    public static void main(String[] args) {
        int port = 40000;
        ServerSocket listener = null;
        boolean runServer = true;

        try {
            listener = new ServerSocket(port);

            while(runServer) {
                try {
                    ChattyChatChatRunnable test = new ChattyChatChatRunnable(listener.accept(), clientNumber);
                    users.add(test);
                    new Thread(test).start();
                    clientNumber++;
                } catch (IOException  e) {
                    System.out.println("Error connecting to client " + clientNumber++);
                }
            }
            listener.close();
        } catch (Exception e) {
            System.out.println("Error establishing listener");
        }
    }

    public static class ChattyChatChatRunnable implements Runnable {
        private Socket socket;
        private int clientNumber;
        private String nickname;
        private PrintWriter out;

        public ChattyChatChatRunnable(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            this.nickname = "Anonymous " + clientNumber;
            this.out = null;
        }

        @Override
        public void run() {

            BufferedReader in = null;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Hello, you are " + nickname + "!");
                out.println("Type /quit to quit.");

                for (ChattyChatChatRunnable chatUser : ChattyChatChatServer.users) {
                    if (chatUser.clientNumber != this.clientNumber) {
                        chatUser.out.println(nickname + " has joined the chat!");
                    }
                }

                boolean done = false;

                while(!done) {
                    String input = in.readLine();

                    if ( input == null || input.toLowerCase().equals("/quit")) {
                        done = true;
                        out.println(nickname + " has been disconnected.");
                    }

                    if (input.substring(0, 1).equals("/") && (input.length() > 5)) {
                        if (input.toLowerCase().substring(0, 6).equals("/nick ")) {
                            String old_nickname = nickname;
                            nickname = input.substring(6);
                            for (ChattyChatChatRunnable chatUser : ChattyChatChatServer.users) {
                              chatUser.out.println(old_nickname + " has changed their name to " + nickname + "!");
                            }
                            System.out.println(old_nickname + " changed to " + nickname);
                        }
                    }
                    else if ( input != null && !done ) {
                        // Write a for-loop to iterate through the vector above and send to each
                        for (ChattyChatChatRunnable chatUser : ChattyChatChatServer.users) {
                            chatUser.out.println(nickname + ": " + input);
                            System.out.println("Sent " + nickname + ": " + input + " to " + chatUser.nickname);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while talking to " + nickname);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket with " + nickname);
                } finally {
                    System.out.println("Connection to " + nickname + " closed");
                }
            }
        } // End run()
    }
}