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
        int port = Integer.parseInt(args[0]); // Use something 40000 to run on your computer
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
                    String[] messageWords = input.split("\\s+");

                    if (input == null || messageWords[0] == "/quit") {
                        done = true;
                        for (ChattyChatChatRunnable chatUser : ChattyChatChatServer.users) {
                            chatUser.out.println(nickname + " has left the chat.");
                        }
                    }

                    // Checking if the message is a command
                    if (messageWords[0].contains("/dm")) {
                        String send_minus_commands = "";
                        for (int i = 2; i < messageWords.length; i++) {
                            send_minus_commands += messageWords[i] + " ";
                        }
                        for (ChattyChatChatRunnable chatUser : ChattyChatChatServer.users) {
                            if ((chatUser.nickname.equals(messageWords[1])) || (chatUser.nickname == this.nickname)) {
                                chatUser.out.println(nickname + " [DM]: " + send_minus_commands);
                            }
                        }
                    } else if (messageWords[0].contains("/nick")) {
                        if (messageWords.length > 1) {
                            String old_nickname = nickname;
                            nickname = messageWords[1];
                            for (ChattyChatChatRunnable chatUser : ChattyChatChatServer.users) {
                                chatUser.out.println(old_nickname + " has changed their name to " + nickname + "!");
                            }
                            System.out.println(old_nickname + " changed to " + nickname);
                        } else {
                            out.println("Nickname could not be changed. The nickname you entered is incorrect.");
                        }
                    } else if (input != null && !done) {
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