package GoServer;

import GoGame.GoMain;
import GoUtil.GoLogger;
import GoUtil.GoUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class GoServerMain implements Runnable {
    GoMain goGame;

    int userCountHis, userCount;
    private final CopyOnWriteArrayList<Channel> users= new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Thread> pool = new CopyOnWriteArrayList<>();

    private final int OBSERVER = -1, BLACK_PLAYER = 1, WHITE_PLAYER = 2;
    private boolean isLocalGame = false;

    private void debugPrintGoMap() {
        GoLogger.debug("Map:");
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                System.out.print(goGame.getPosStep(i, j) + " ");
            }
            System.out.println();
        }
    }

    private int allocationUser() {
        return switch(userCount) {
            case 1 -> BLACK_PLAYER;
            case 2 -> WHITE_PLAYER;
            default -> OBSERVER;
        };
    }

    @Override
    public void run() {
        GoLogger.log("Server", "start");
        ServerSocket server = null;

        userCountHis = 0;
        goGame = new GoMain();
        goGame.beginGame();

        try {
            server = new ServerSocket(2005);
        } catch (IOException e) {
            GoLogger.error("Failed to create server.");
//            throw new RuntimeException();
        }
        if (server == null) return;
        //noinspection InfiniteLoopStatement
        while (true) {
            Socket client;
            try {
                client = server.accept();
            } catch (IOException e) {
                GoLogger.error("Failed to accept client.");
                throw new RuntimeException();
            }

            if (userCountHis > 9) {
                GoLogger.log("Server", "rejected : too many users.");
                continue;
            }

            userCount++;
            Channel userChannel = new Channel(client, userCountHis, allocationUser());
            Thread userThread = new Thread(userChannel);
            GoLogger.log("Server", "A client connected");
            userCountHis++;

            users.add(userChannel);
            pool.add(userThread);

            userThread.start();
        }
    }

    class Channel implements Runnable {
        private DataInputStream input;
        private DataOutputStream output;
        private boolean isRunning, isReady;
        private final int id;
        private int userProp;

        private void setReady() { isReady = true; }

        public Channel(Socket client, int id, int userProp) {
            isRunning = true;
            this.id = id;
            this.userProp = userProp;

            try {
                this.input = new DataInputStream(client.getInputStream());
                this.output = new DataOutputStream(client.getOutputStream());
            } catch (IOException e) {
                GoLogger.error("Failed to create IO stream.");
                release();
            }
        }

        private void send(String message) {
            try {
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                GoLogger.error("Failed to send message.");
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
                GoLogger.error("Failed to receive message.");
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

                case "9x0" -> getClientID(response);
                case "9x1" -> response.append("o").append(Math.max(userCount - 2, 0));
                case "9x2" -> response.append(userCount >= 2 ? "gT" : "gF");
                case "9x7" -> questLocal(response);
                case "9x8" -> gameStart(response);
                case "9x9" -> { return null; }
            }

            return response.toString();
        }

        private void getClientID(StringBuilder response) {
            response.append("i").append(id);
            setReady();
        }

        private void questLocal(StringBuilder response) {
            if (userCount != 1) {
                response.append("vF");
                return;
            }
            response.append("vT");
            isLocalGame = true;
        }

        private void loadSave(String content, StringBuilder response) {
            if (!isLocalGame &&userCountHis != 1) response.append("rF");
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
            if (userCountHis >= 2) {
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

            GoLogger.debug("isLocalGame = " + String.valueOf(isLocalGame));
            if (!isLocalGame && ((player != goGame.getCurrentPlayer()) || (player != userProp))) {
                response.append("pF");
                return;
            }

            if (goGame.putPiece(x, y)) {
                response.append("pT").append(x).append(",").append(y);
                goGame.removePiece(goGame.getRemovePieces(x, y));
            } else response.append("pF");
//            debugPrintGoMap();
        }

        private void clientExit() {
            userCount--;
            release();
            goGame.clear();
            GoLogger.log("Server", "Game cleared.");
        }

        @Override
        public void run() {
            while (isRunning) {
                String message = receive();
                if (message.equals("")) continue;
                String response = processMessage(message);
                GoLogger.log("Server", message + " -> " + response);
                if (response == null) continue;
                if (isLocalGame) send(response);
                else sendAll(response);
            }

            clientExit();
        }
    }
}
