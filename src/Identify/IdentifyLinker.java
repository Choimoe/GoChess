package Identify;

import GoBoard.ChessBoard;
import GoUtil.GoLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class IdentifyLinker {
    String pyPath = "src/Identify/main.py";
    String[] args = new String[] {"python", pyPath};
    Socket client;

    public boolean isRunning = true;

    public void linker(ChessBoard target) throws IOException, InterruptedException {
        GoLogger.log("Linker Server", "start");

        ServerSocket server = null;
        try {
            server = new ServerSocket(1111);
        } catch (IOException e) {
            GoLogger.error("Failed to create linker server.");
        }

        new Thread(() -> {
            try {
                Runtime.getRuntime().exec(args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        try {
            assert server != null;
            client = server.accept();
        } catch (IOException e) {
            GoLogger.error("Failed to accept client.");
            throw new RuntimeException();
        }

        Channel userChannel = new Channel(client, target);
        Thread userThread = new Thread(userChannel);
        GoLogger.log("Linker Server", "A client connected");
        userThread.start();
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class Channel implements Runnable {
        private DataInputStream input;
        private BufferedReader br;
        private boolean isRunning;
        ChessBoard target;

        int lastX = 0, lastY = 0, lastPlayer = -1;
        
        private String receive() {
            String message = null;
            try {
                while (message == null)
                    message = br.readLine();
            } catch (IOException ignored) {}
            return message;
        }

        public Channel(Socket client, ChessBoard target) {
            isRunning = true;
            this.target = target;
            try {
                this.input = new DataInputStream(client.getInputStream());
                InputStreamReader reader = new InputStreamReader(this.input);
                br = new BufferedReader(reader);
            } catch (IOException e) {
                GoLogger.error("Failed to create IO stream.");
            }
        }

        public void setRunning(boolean running) {
            this.isRunning = running;
        }

        @Override
        public void run() {
            while (isRunning) {
                processMessage(receive());
            }
        }

        @SuppressWarnings("SuspiciousNameCombination")
        private void processMessage(String message) {
            GoLogger.debug("receive from python: " + message);
            String[] result = message.split(" ");
            if (lastPlayer != -1) {
                int currentPlayer = result[2].equals("B") ? 1 : 2;
                if (currentPlayer != lastPlayer) {
                    target.forceSetPieceDisplay(lastY, lastX);
                }
            }

            lastPlayer  = result[2].equals("B") ? 1 : 2;
            lastX       = Integer.parseInt(result[0]);
            lastY       = Integer.parseInt(result[1]);
        }
    }
}
