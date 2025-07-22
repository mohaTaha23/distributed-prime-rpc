import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    static PrintWriter out ;
    static BufferedReader in ;
    public static void main(String[] args) {
        try {                                                               // establish connection w directory
            Socket socket = new Socket("localhost",10000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("samo3leeko");                                      // message to tell its a client
            String message = in.readLine();
            if (message.equals("rawe7")){
                System.out.println("no servers available :(");
            }   // if a message was IP and Port:
            else {
                out.close();
                in.close();
                socket.close(); // close connection w directory no longer needed

                String ip = message.split(":")[0];
                int port = Integer.parseInt(message.split(":")[1]);
                socket = new Socket(ip,port);           // make a connection w IP and port of the server

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(in.readLine());
                Scanner soso = new Scanner(System.in);
                int input;
                while (true){       // keeps sending numbers and get results
                    System.out.println("enter a number: ");
                    input = soso.nextInt();
                    out.println(input);
                    System.out.println(in.readLine());
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

}
