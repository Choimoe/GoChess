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
    String name = "Anonymous";

    ClientSend send;
    ClientReceive receive;

    int requestCount = 0;
    private final String[] requestResponse = new String[10000];

    @Override
    public void run() {
        System.out.println("[DEBUG] ----- Client -----");
        try {
            client = new Socket("localhost", 2005);
        } catch (IOException e) {
            System.out.println("[ERROR] Client: Failed to connect the server.");
            throw new RuntimeException(e);
        }

        send = new ClientSend(client);
        receive = new ClientReceive(client, requestResponse);

        new Thread(send).start();
        new Thread(receive).start();
    }

    public int request(String type, Object... params) {
        requestCount++;
        send.request(requestCount, type, params);
        return requestCount;
    }

    public String getResponse(int id) {
        return requestResponse[id];
    }
}
