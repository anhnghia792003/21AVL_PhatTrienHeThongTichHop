package baitapdexuat3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TcpDateTimeClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("            TCP DATE TIME CLIENT                 ");
        System.out.println("==================================================");

        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("[TCP CLIENT] Đã kết nối tới Server. Các lệnh khả dụng: DATE, TIME, DATETIME, QUIT\n");

            while (true) {
                System.out.print("TCP Client > ");
                String command = scanner.nextLine();

                writer.write(command);
                writer.newLine();
                writer.flush();

                if ("QUIT".equalsIgnoreCase(command.trim())) {
                    System.out.println("[TCP CLIENT] Ngắt kết nối.");
                    break;
                }

                String response = reader.readLine();
                if (response == null) {
                    System.err.println("[TCP CLIENT] Server đã ngắt kết nối đột ngột!");
                    break;
                }
                System.out.println("Server > " + response);
            }
        } catch (IOException e) {
            System.err.println("[TCP CLIENT LỖI] Lỗi kết nối: " + e.getMessage());
        }
    }
}