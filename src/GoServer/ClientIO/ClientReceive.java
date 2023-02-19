package GoServer.ClientIO;

import GoBoard.ChessBoard;
import GoUtil.GoLogger;
import GoUtil.GoUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ClientReceive implements Runnable {
    private final Socket client;
    static DataInputStream input;
    private boolean isRunning;
    private final Map<Integer, String> requestResponse;
    private final String name;
    private ChessBoard notifier;

    public ClientReceive(Socket client, Map<Integer, String> requestResponse, String name) {
        this.client = client;
        this.name = name;
        this.requestResponse = requestResponse;

        isRunning = true;
        try {
            input = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            GoLogger.error(name + " - Failed to create Input stream.");
            release();
        }
    }

    private void release() {
        isRunning = false;
        GoUtil.close(client, input);
    }

    private String receive() {
        try {
//            GoLogger.log(name, "Waiting for response...");
            return input.readUTF();
        } catch (IOException e) {
            GoLogger.error(name + " - Failed to receive message.");
            release();
        }
        return null;
    }

    protected void processMessage(String message) {
        if (message.charAt(0) != '#') return;
        String[] messageSplit = message.split("\\|");
        int requestID = Integer.parseInt(messageSplit[0].substring(1));
        if (requestID < 0) return;
        GoLogger.log(name, "Received message: [" + requestID / 10 + "] " + messageSplit[1]);
        requestResponse.put(requestID, messageSplit[1]);
        notifier.processResponse(messageSplit[1]);
    }

    public void setNotifyOnNewRequests(ChessBoard board) {
        this.notifier = board;
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