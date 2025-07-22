import java.net.ServerSocket;
import java.net.Socket;

public class ServersInfo {
    String ip ;
    int port;
    Socket socket;

    public ServersInfo(String ip, int port, Socket socket) {
        this.ip = ip;
        this.port = port;
        this.socket = socket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }
    public String toString(){
        return "ip: "+ ip + " port: "+ port ;
    }
}
