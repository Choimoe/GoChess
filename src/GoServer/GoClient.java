package GoServer;

import GoServer.ClientIO.ClientReceive;
import GoServer.ClientIO.ClientSend;
import GoServer.ClientIO.GoRequest;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class GoClient implements Runnable {
    Socket client;
    String name = "Client";

    ClientSend send;
    ClientReceive receive;

    int requestIDCount = 0, waitingResponse = 0;
    int clientID = -1;
    private Map<Integer, String> requestResponse;
    private final int MAX_USER = 10;
    private boolean isLocalGame = false;

    public void setLocalGame(boolean isLocalGame) { this.isLocalGame = isLocalGame; System.out.println("[DEBUG] isLocalGame = " + isLocalGame); }

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
        requestResponse = new HashMap<>();
        receive = new ClientReceive(client, requestResponse, name);

        new Thread(send).start();
        new Thread(receive).start();


        String response = requestVital("getClientID");
        clientID = Integer.parseInt(response.substring(1));
        name += clientID;
        System.out.println("[LOG] " + name + ": get ID = " + clientID);
        requestIDCount = MAX_USER + clientID;

        System.out.println("[DEBUG] isLocalGame = " + isLocalGame);

        if (isLocalGame) {
            requestVital("questLocal");
            System.out.println("[LOG] " + name + ": set LocalGame = " + response);
//            requestVital("gameStart");
        }
    }

    private String requestVital (String request) {
        int reqID = request(request);
        while (getResponse(reqID) == null) {
            synchronized (this) {
                try {
                    this.wait(100);
                } catch (InterruptedException e) {
                    System.out.println("[ERROR] Client?: Failed to wait for " + request);
                }
            }
        }
        return getResponse(reqID);
    }

    public int request(String type, Object... params) {
        requestIDCount += MAX_USER;
        waitingResponse++;
        send.request(requestIDCount, type, params);
        return requestIDCount;
    }

    public String getResponse(int id) {
        waitingResponse--;
        if (!requestResponse.containsKey(id)) return null;
        return requestResponse.get(id);
    }

    public int waitingNumber() {
        return waitingResponse;
    }

    public String[] getAllResponse() {
        String[] responses = new String[requestResponse.size()];
        int i = 0;
        for (String response : requestResponse.values()) {
            responses[i++] = response;
        }
        requestResponse.clear();
        return responses;
    }
}
