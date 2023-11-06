package practice3;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        try (Socket socket = new Socket("localhost", 5001)) {
            Scanner scanner = new Scanner(System.in);

            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine());
                while (in.hasNextLine()) {
                    System.out.println(in.nextLine());
                }
            }
        }
    }
}