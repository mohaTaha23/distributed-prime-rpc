import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Directory {
    private static final int port = 10000; // initial port for clients to connect on
    private static Socket clientSocket;
    static List<ServersInfo> servers = new ArrayList<>();
    static int counter = 0; // for round robin

    public static void main(String[] args) {
        try {
            // Create a server socket that listens on the specified port
            ServerSocket socket = new ServerSocket(port);
            System.out.println("Directory is running on port 10000");

            // accept each connection and handle it in Handling thread
            while (true) {
                clientSocket = socket.accept();
                Thread thread = new Thread(new HandleClient(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Inner class to handle client connections
    private static class HandleClient implements Runnable {
        Socket socket; // the incoming socket
        private BufferedReader in; // Input stream to read data from the client
        private PrintWriter out; // Output stream to send data to the client

        public HandleClient(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = in.readLine(); //  the client's message
                out = new PrintWriter(socket.getOutputStream(), true);

                // Check the message received from the client

                if (message.equals("samo3leeko")) { // then its a client
                    String serversInfo = getAvailable();
                    out.println(serversInfo); // Send the information back to the client and thats it
                }
                else if (message.equals("server")) {    // server need registration

                    int availablePort = getAvailablePort(); // Get an available port for the server to connect to it
                    ServerSocket serverSocket = new ServerSocket(availablePort);        //  create a socket for that port
                    out.println(availablePort); // Send the available port number to the server so it makes the connection
                    System.out.println("Available port sent to connect on: " + availablePort);
                    out.close(); // Close the output stream

                    Socket socket1 = null; // Socket for the new server
                    ServersInfo serversInfo = null;

                    try {
                        // Accept a connection from the server
                        socket1 = serverSocket.accept();
                        System.out.println("Server connected!");

                        // Read the server's IP and port information to register
                        in = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                        message = in.readLine();
                        String ip = message.split(":")[0];
                        int port = Integer.parseInt(message.split(":")[1]);
                        System.out.println(ip + " " + port + " " + socket1);

                        // Create a ServersInfo object to store the server's details
                        serversInfo = new ServersInfo(ip, port, socket1);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    // Create and start a thread to track the server
                    TrackServer trackServer = new TrackServer(serversInfo);
                    Thread thread = new Thread(trackServer);
                    thread.start();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        // Inner class to track the server
        private static class TrackServer implements Runnable {
            Socket socketT; // Socket to communicate with the server
            ServersInfo serversInfo;

            // Constructor to initialize the tracker with server information
            public TrackServer(ServersInfo serversInfo) {
                this.serversInfo = serversInfo;
                socketT = serversInfo.socket;
            }

            @Override
            public void run() {
                servers.add(serversInfo); // Add the new server to the list

                PrintWriter out = null;
                BufferedReader in = null;

                try {
                    // Initialize communication streams with the server
                    out = new PrintWriter(socketT.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socketT.getInputStream()));
                } catch (IOException e) {
                    System.out.println(e.getMessage() + " no connection");
                }

                int port;
                while (true) {
                    // heartBeat RPC ,, alive? sent to server, server responds with the available port for the client
                    String message = null;
                    out.println("alive?"); // Send a message to check if the server is alive

                    try {
                        // Read the server's response
                        message = in.readLine();
                    } catch (IOException e) {
                        System.out.println("Error with " + serversInfo.ip + ", removed");
                        servers.remove(serversInfo); // Remove the server if there is no response
                        break;
                    }

                    if (message != null) {      // then server sent an available port
                        try {

                            port = Integer.parseInt(message);
                            if (port != serversInfo.port) serversInfo.setPort(port);    // update if changed
                            Thread.sleep(1000); // Wait for 1 second before the next check
                        } catch (InterruptedException e) {
                            System.out.println("Server " + serversInfo.ip + " is not responding, removed");
                            servers.remove(serversInfo); // Remove the server if it is not responding
                            break;
                        }
                    } else {
                        System.out.println("Server " + serversInfo.ip + " is not responding, removed");
                        servers.remove(serversInfo); // Remove the server if there is no response
                        break;
                    }
                }
            }
        }

        // Method to get information of an available server
        private String getAvailable() {
            if (servers.size() == 0) return "rawe7";        // if there is no servers mn7keeluh yrawwi7
            ServersInfo s = servers.get(counter++ % servers.size()); // Get a server using round-robin selection
            return s.ip + ":" + s.port; // Return the selected server's IP and port
        }
    }

    // Method to get an available port for a new server
    public static int getAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            // Use a new ServerSocket to find an available port
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available ports", e);
        }
    }
}

