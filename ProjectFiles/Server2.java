import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {
    public boolean available = true;
    static int port = 10101;
    static BufferedReader in;
    static PrintWriter out;

    public static void main(String[] args) {
        try {
            Socket directorySocket = new Socket("localhost", 10000);
            PrintWriter printWriter = new PrintWriter(directorySocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(directorySocket.getInputStream()));
            printWriter.println("server");

            int directoryPort = Integer.parseInt(in.readLine());
            System.out.println("Port received: " + directoryPort);
            directorySocket = new Socket("localhost", directoryPort);

            out = new PrintWriter(directorySocket.getOutputStream(), true);
            out.println("localhost:" + port);
            System.out.println("Sent IP + port");

            HandleDirectory handleDirectory = new HandleDirectory(directorySocket);
            Thread thread = new Thread(handleDirectory);
            thread.start();

            ServerSocket serverSocket = null;
            renewPort();

            while (true) {
                try {
                    serverSocket = new ServerSocket(port);
                    System.out.println("Server is listening on port: " + port);
                    Socket clientSocket = serverSocket.accept();
                    renewPort();
                    Thread thread2 = new Thread(new HandleClient(clientSocket));
                    thread2.start();
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static class HandleClient implements Runnable {
        private Socket socket;
        BufferedReader in;
        PrintWriter out;

        public HandleClient(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Serv2 is talking, send an integer");
                while (true) {
                    int input = Integer.parseInt(in.readLine());
                    System.out.println("Received: " + input);
                    if (isPrime(input)) {
                        out.println("Yes, " + input + " is prime");
                    } else {
                        int nextPrime = nextPrime(input);
                        out.println("No, " + input + " is not a prime, the next prime is: " + nextPrime);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }
        }
    }

    private static class HandleDirectory implements Runnable {
        Socket directorySocket;
        PrintWriter printWriter;
        BufferedReader in;

        public HandleDirectory(Socket directorySocket) {
            this.directorySocket = directorySocket;
        }

        @Override
        public void run() {
            try {
                printWriter = new PrintWriter(directorySocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(directorySocket.getInputStream()));
                while (true) {
                    String message = in.readLine();
                    if ("alive?".equals(message)) {
                        printWriter.println(port);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    if (printWriter != null) printWriter.close();
                    if (in != null) in.close();
                    if (directorySocket != null) directorySocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }
        }
    }

    private static boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    private static int nextPrime(int number) {
        number++;
        while (!isPrime(number)) {
            number++;
        }
        return number;
    }

    private static void renewPort() {
        int portNumber =0;
        while (true){
            portNumber = (int)Math.floor(Math.random()*6000) + 10000;
            try {
                ServerSocket socket = new ServerSocket(portNumber);
                socket.close();
                port = portNumber;
                break;
            }
            catch (Exception e) {
            }
        }
    }
}
