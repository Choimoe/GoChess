package GoServer.ClientIO;

import GoUtil.GoUtil;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

public class ClientSend implements Runnable {
    private final Socket client;
    private final BufferedReader console;
    private DataOutputStream output;
    private boolean isRunning;
    private final Queue<GoRequest> requestQueue;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private void release() {
        isRunning = false;
        GoUtil.close(console, output, client);
    }

    public ClientSend(Socket client) {
        isRunning = true;
        requestQueue = new LinkedList<>();
        this.client = client;

        console = new BufferedReader(new InputStreamReader(System.in));

        try {
            output = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            System.out.println("[ERROR] Client: Failed to create client Output stream.");
            release();
        }
    }

    public void request(int requestCount, String type, Object... params) {
        String formatType = switch (type) {
            case "getMap" -> "0x0";
            case "putPiece" -> "0x1";
            case "skipTurn" -> "0x2";
            case "putLose" -> "0x3";
            default -> "1x1";
        };
        String content = switch (type) {
            case "getMap", "putLose" -> "";
            case "skipTurn" -> params[0].toString();
            case "putPiece" -> params[0] + "," + params[1] + "," + params[2];
            default -> (String) params[0];
        };
        GoRequest request = new GoRequest(requestCount, formatType, content);
        lock.writeLock().lock();
        try{
            requestQueue.add(request);
            System.out.println("[DEBUG] Client: add request: " + request);
        } finally {
            lock.writeLock().unlock();
        }
        synchronized (this) {
            this.notify();
        }
    }

    private void send(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            System.out.println("[ERROR] Client: Failed to send message.");
            release();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            System.out.print("");
            if (requestQueue.isEmpty()) continue;
            lock.writeLock().lock();
            try {
                GoRequest request = requestQueue.poll();
                if (request == null) continue;
                System.out.println("[DEBUG] Client: send request: " + request);
                send(request.toString());
            } finally {
                lock.writeLock().unlock();
            }
            synchronized (this) {
                try {
                    this.wait(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
