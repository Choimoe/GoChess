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

    public ClientReceive(Socket client, String[] requestResponse) {
        this.client = client;
        this.requestResponse = requestResponse;

        isRunning = true;
        try {
            input = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to create Input stream.");
            release();
        }
    }

    private void release() {
        isRunning = false;
        GoUtil.close(client, input);
    }

    private String receive() {
        try {
            System.out.println("[LOG] Client: Waiting for response...");
            return input.readUTF();
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to receive message.");
            release();
        }
        return null;
    }

    protected void processMessage(String message) {
        System.out.println("[DEBUG] Client : Received message: " + message);
        if (message.charAt(0) != '#') return;
        String[] messageSplit = message.split("\\|");
        int requestID = Integer.parseInt(messageSplit[0].substring(1));
        if (requestID < 0 || requestID >= requestResponse.length) return;
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