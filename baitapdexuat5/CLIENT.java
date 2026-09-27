package baitapdexuat5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CLIENT {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 6000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Luồng đọc tin nhắn từ Server về Console
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (Exception e) {
                    System.out.println("Đã ngắt kết nối với Server.");
                }
            });
            receiveThread.start();

            // Luồng chính: Đọc dữ liệu người dùng nhập từ Console gửi lên Server
            while (true) {
                String userInput = scanner.nextLine();
                out.println(userInput);
                if ("QUIT".equalsIgnoreCase(userInput.trim())) {
                    break;
                }
            }

            socket.close();
            scanner.close();

        } catch (Exception e) {
            System.err.println("Không thể kết nối tới Server: " + e.getMessage());
        }
    }
}