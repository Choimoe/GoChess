package GoServer;

import GoGame.GoMain;
import GoUtil.GoUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class GoServerMain implements Runnable {
    GoMain goGame;

    int userCount;
    private final CopyOnWriteArrayList<Channel> users= new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Thread> pool = new CopyOnWriteArrayList<>();

    private void debugPrintGoMap() {
        System.out.println("[DEBUG] Server: Map:");
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                System.out.print(goGame.getPosStep(i, j) + " ");
            }
            System.out.println();
        }
    }

    @Override
    public void run() {
        System.out.println("[LOG] ----- Server -----");
        ServerSocket server = null;

        userCount = 0;
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
            Socket client;
            try {
                client = server.accept();
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to accept client.");
                throw new RuntimeException();
            }
            if (userCount > 9) {
                System.out.println("[LOG] Server: Too many users.");
                continue;
            }
            Channel userChannel = new Channel(client, userCount);
            Thread userThread = new Thread(userChannel);
            System.out.println("[LOG] A client connected");
            userCount++;

            users.add(userChannel);
            pool.add(userThread);

            userThread.start();
        }
    }

    class Channel implements Runnable {
        private DataInputStream input;
        private DataOutputStream output;
        private boolean isRunning;
        private final int id;

        public Channel(Socket client, int id) {
            isRunning = true;
            this.id = id;
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
            String content = messageSplit.length > 2 ? messageSplit[2] : "";

            StringBuilder response = new StringBuilder("#");
            response.append(requestID).append("|");

            switch (type) {
                case "0x0" -> getMapHashCode(response);
                case "0x1" -> putPiece(content, response);
                case "0x2" -> skipTurn(content, response);
                case "0x3" -> {
                    // lose
                }
                case "0x4" -> loadSave(content, response);

                case "1x1" -> response.append("cT");

                case "9x0" -> response.append("i").append(id);
                case "9x1" -> response.append("o").append(Math.max(userCount - 2, 0));
                case "9x2" -> response.append(userCount >= 2 ? "gT" : "gF");
                case "9x8" -> gameStart(response);
                case "9x9" -> { return null; }
            }

            return response.toString();
        }

        private void loadSave(String content, StringBuilder response) {
            if (userCount != 1) response.append("rF");
            else {
                response.append("rT");
                goGame.recover(content);
                response.append(content);
            }
        }

        private void getMapHashCode(StringBuilder response) {
            int hashCode = goGame.toString().hashCode();
            response.append("h").append(hashCode);
        }

        private void gameStart(StringBuilder response) {
            if (userCount >= 2) {
                response.append("aF");
            } else {
                response.append("gT");
                goGame.clear();
                goGame.beginGame();
            }
        }

        private void skipTurn(String content, StringBuilder response) {
            if (Integer.parseInt(content) != goGame.getCurrentPlayer()) {
                response.append("sF");
            } else {
                goGame.skipTurn();
                response.append("sT");
            }
        }

        private void putPiece(String content, StringBuilder response) {
            String[] contentSplit = content.split(",");
            int x       = Integer.parseInt(contentSplit[0]),
                y       = Integer.parseInt(contentSplit[1]),
                player  = Integer.parseInt(contentSplit[2]);

            if (player != goGame.getCurrentPlayer()) {
                response.append("pF");
                return;
            }

            if (goGame.putPiece(x, y)) {
                response.append("pT").append(x).append(",").append(y);
                goGame.removePiece(goGame.getRemovePieces(x, y));
            } else response.append("pF");
//                    debugPrintGoMap();
        }

        private void clientExit() {
            release();
            goGame.clear();
            System.out.println("[LOG] Server: Game cleared.");
        }

        @Override
        public void run() {
            while (isRunning) {
                String message = receive();
                if (message.equals("")) continue;
                String response = processMessage(message);
                System.out.println("[LOG] " + message + " -> " + response);
                if (response == null) continue;
                sendAll(response);
            }

            clientExit();
        }
    }
}
