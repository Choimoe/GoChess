package GoServer;

import GoGame.GoMain;
import GoUtil.GoUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class GoServerMain implements Runnable {
    GoMain goGame;

    private CopyOnWriteArrayList<Channel> users= new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Thread> pool = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        System.out.println("[DEBUG] ----- Server -----");
        ServerSocket server = null;

        goGame = new GoMain();
        goGame.beginGame();

        try {
            server = new ServerSocket(2005);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to create server.");
//            throw new RuntimeException();
        }
        if (server == null) return;
        //noinspection InfiniteLoopStatement
        while (true) {
            Socket client = null;
            try {
                client = server.accept();
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to accept client.");
                throw new RuntimeException();
            }
            Channel userChannel = new Channel(client);
            Thread userThread = new Thread(userChannel);
            System.out.println("[LOG] A client connected");

            users.add(userChannel);
            pool.add(userThread);

            userThread.start();
        }
    }

    class Channel implements Runnable {
        private DataInputStream input;
        private DataOutputStream output;
        private boolean isRunning;

        public Channel(Socket client) {
            isRunning = true;
            try {
                this.input = new DataInputStream(client.getInputStream());
                this.output = new DataOutputStream(client.getOutputStream());
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to create IO stream.");
                release();
            }
        }

        private void send(String message) {
            try {
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to send message.");
                release();
            }
        }

        private void sendAll(String message) {
            for (Channel channel : users) {
                channel.send(message);
            }
        }

        private String receive() {
            String message = "";
            try {
                message = input.readUTF();
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to receive message.");
                release();
            }
            return message;
        }

        private void release() {
            this.isRunning = false;
            GoUtil.close(input, output);
        }

        // #2|0x1|18,11
        private String processMessage(String message) {
            String[] messageSplit = message.split("\\|");
            int requestID = Integer.parseInt(messageSplit[0].substring(1));
            String type = messageSplit[1];
            String content = messageSplit[2];

            StringBuilder response = new StringBuilder("#");
            response.append(requestID).append("|");

            switch (type) {
                case "0x0" -> {}
                case "0x1" -> {
                    String[] contentSplit = content.split(",");
                    int x = Integer.parseInt(contentSplit[0]),
                        y = Integer.parseInt(contentSplit[1]),
                        user = Integer.parseInt(contentSplit[2]);

                    if (user != goGame.getCurrentPlayer()) {
                        response.append("pF");
                        break;
                    }

                    if (goGame.putPiece(x, y)) {
                        response.append("pT").append(x).append(",").append(y);
                    } else response.append("pF");
                }
                case "0x2" -> {
                    int player = Integer.parseInt(content);
                    if (player != goGame.getCurrentPlayer()) {
                        response.append("sF");
                    } else {
                        goGame.skipTurn();
                        response.append("sT");
                    }
                }
                case "0x3" -> {
                    // lose
                }
                case "1x1" -> {
                    response.append("cT");
                }
            }

            return response.toString();
        }

        @Override
        public void run() {
            while (isRunning) {
                String message = receive();
                if (message.equals("")) continue;
                String response = processMessage(message);
                System.out.println("[LOG] " + message + " -> " + response);
                sendAll(response);
            }
        }
    }
}
