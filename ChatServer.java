import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12346)) {
            System.out.println("Server started. Listening on port 12346...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, ClientHandler excludeUser) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeUser) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Client " + clientHandler.getUsername() + " removed.");
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return this.username;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask for the username
            out.println("Enter your username: ");
            username = in.readLine();
            System.out.println(username + " has joined the chat.");

            // Notify everyone that the new client has joined
            ChatServer.broadcastMessage(username + " has joined the chat.", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                ChatServer.broadcastMessage(username + ": " + message, this);
            }

            // When the user leaves the chat
            System.out.println(username + " has left the chat.");
            ChatServer.broadcastMessage(username + " has left the chat.", this);
            ChatServer.removeClient(this);
            socket.close();
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
		
        out.println(message);
    }
}
