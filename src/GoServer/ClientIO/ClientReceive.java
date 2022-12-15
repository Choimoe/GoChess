package GoServer.ClientIO;

import GoUtil.GoUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientReceive implements Runnable {
    private final Socket client;
    static DataInputStream input;
    private boolean isRunning;
    private final String[] requestResponse;
    private final String name;

    public ClientReceive(Socket client, String[] requestResponse, String name) {
        this.client = client;
        this.name = name;
        this.requestResponse = requestResponse;

        isRunning = true;
        try {
            input = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("[ERROR] " + name + ": Failed to create Input stream.");
            release();
        }
    }

    private void release() {
        isRunning = false;
        GoUtil.close(client, input);
    }

    private String receive() {
        try {
//            System.out.println("[LOG] " + name + ": Waiting for response...");
            return input.readUTF();
        } catch (IOException e) {
            System.out.println("[ERROR] " + name + ": Failed to receive message.");
            release();
        }
        return null;
    }

    protected void processMessage(String message) {
        if (message.charAt(0) != '#') return;
        String[] messageSplit = message.split("\\|");
        int requestID = Integer.parseInt(messageSplit[0].substring(1));
        if (requestID < 0 || requestID >= requestResponse.length) return;
//        System.out.println("[DEBUG] " + name + ": Received message: [" + requestID / 10 + "] " + messageSplit[1]);
        requestResponse[requestID] = messageSplit[1];
    }

    @Override
    public void run() {
        while (isRunning) {
            String message = receive();
            if (message == null) continue;
            processMessage(message);
        }
    }
}