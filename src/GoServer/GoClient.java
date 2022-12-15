package GoServer;

import GoServer.ClientIO.ClientReceive;
import GoServer.ClientIO.ClientSend;
import GoServer.ClientIO.GoRequest;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class GoClient implements Runnable {
    Socket client;
    String name = "Client";

    ClientSend send;
    ClientReceive receive;

    int requestCount = 0;
    int clientID = -1;
    private final String[] requestResponse = new String[100000];
    private final int MAX_USER = 10;

    @Override
    public void run() {
        System.out.println("[LOG] ----- Client -----");
        try {
            client = new Socket("localhost", 2005);
        } catch (IOException e) {
            System.out.println("[ERROR] Client?: Failed to connect the server.");
            throw new RuntimeException(e);
        }
        send = new ClientSend(client, name);
        receive = new ClientReceive(client, requestResponse, name);

        new Thread(send).start();
        new Thread(receive).start();

        int reqID = request("getClientID");
        while (getResponse(reqID) == null) {
            synchronized (this) {
                try {
                    this.wait(100);
                } catch (InterruptedException e) {
                    System.out.println("[ERROR] Client?: Failed to wait for getting name.");
                }
            }
        }
        String response = getResponse(reqID);
        clientID = Integer.parseInt(response.substring(1));
        name += clientID;
        System.out.println("[LOG] " + name + ": get ID = " + clientID);
        requestCount = MAX_USER + clientID;
    }

    public int request(String type, Object... params) {
        requestCount += MAX_USER;
        send.request(requestCount, type, params);
        return requestCount;
    }

    public String getResponse(int id) {
        return requestResponse[id];
    }
}
