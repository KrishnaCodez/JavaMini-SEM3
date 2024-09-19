    import java.io.*;
    import java.net.*;
    import java.util.Scanner;

    public class ChatClient {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ChatClient(String serverAddress) throws IOException {
            socket = new Socket(serverAddress, 12346); // Port must match the server's port
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            new ReadThread().start();  // Thread for reading messages from server
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the server IP address: ");
            String serverAddress = scanner.nextLine();

            try {
                ChatClient client = new ChatClient(serverAddress);
                System.out.println("Connected to the chat server. Type 'exit' to quit.");

                while (true) {
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("exit")) {
                        client.sendMessage("exit");
                        break;
                    }
                    client.sendMessage(message);
                }

                client.socket.close();
                scanner.close();
            } catch (IOException e) {
                System.out.println("Error connecting to the server: " + e.getMessage());
            }
        }

        // Thread to read incoming messages from server
        class ReadThread extends Thread {
            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                }
            }
        }
    }
