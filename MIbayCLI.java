import java.io.*;
import java.net.*;

public class MIbayCLI {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            return;
        }

        String command = args[0];
        Socket socket = new Socket("localhost", 12345);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        switch (command) {
            case "anbieten":
                if (args.length != 4) {
                    return;
                }
                out.println("anbieten " + args[1] + " " + args[2] + " " + args[3]);
                break;

            case "abbrechen":
                if (args.length != 2) {
                    return;
                }
                out.println("abbrechen " + args[1]);
                break;

            case "liste":
                break;

            case "info":
                break;

            case "bieten":
                if (args.length != 4) {
                    return;
                }
                out.println("bieten " + args[1] + " " + args[2] + " " + args[3]);
                break;

            default:
                System.out.println("Unknown command: " + command);
        }

        String response;
        while ((response = in.readLine()) != null) {
            System.out.println(response);
        }

        socket.close();
    }
}

