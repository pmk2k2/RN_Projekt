import java.io.*;
import java.net.*;
import java.util.*;

public class MIbayAgent {
    private static final Map<String, Auction> auctions = new HashMap<>();
    private static final Map<String, Integer> accounts = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            return;
        }

        int balance = Integer.parseInt(args[0]);
        String username = System.getenv("USER");
        accounts.put(username, balance);

        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Waiting for commands...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String command = in.readLine();
                String[] tokens = command.split(" ");
                String action = tokens[0];

                switch (action) {
                    case "anbieten":
                        String fileName = tokens[3];
                        int minPrice = Integer.parseInt(tokens[1]);
                        int duration = Integer.parseInt(tokens[2]);
                        auctions.put(fileName, new Auction(fileName, minPrice, duration, System.getenv("USER")));
                        out.println("File " + fileName + " listed for sale.");
                        break;

                    case "abbrechen":
                        fileName = tokens[1];
                        auctions.remove(fileName);
                        break;

                    case "liste":
                        for (Auction auction : auctions.values()) {
                            out.println(auction);
                        }
                        break;

                    case "info":
                        String user = System.getenv("USER");
                        int balance = accounts.getOrDefault(user, 0);
                        out.println("Balance: " + balance);
                        break;

                    case "bieten":
                        int bid = Integer.parseInt(tokens[1]);
                        String seller = tokens[2];
                        fileName = tokens[3];

                        Auction auction = auctions.get(fileName);
                        if (auction != null && auction.getMinPrice() <= bid) {
                            auction.setHighestBid(bid, seller);
                            out.println("Bid accepted for file " + fileName);
                        } else {
                            out.println("Bid rejected.");
                        }
                        break;

                    default:
                        out.println("Unknown command.");
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Auction {
        private final String fileName;
        private final int minPrice;
        private final int duration;
        private final String seller;
        private int highestBid;
        private String highestBidder;

        public Auction(String fileName, int minPrice, int duration, String seller) {
            this.fileName = fileName;
            this.minPrice = minPrice;
            this.duration = duration;
            this.seller = seller;
            this.highestBid = 0;
            this.highestBidder = null;
        }

        public int getMinPrice() {
            return minPrice;
        }

        public void setHighestBid(int bid, String bidder) {
            this.highestBid = bid;
            this.highestBidder = bidder;
        }

        @Override
        public String toString() {
            return fileName + " | Min: " + minPrice + " | Highest: " + highestBid + " | Seller: " + seller;
        }
    }
}
