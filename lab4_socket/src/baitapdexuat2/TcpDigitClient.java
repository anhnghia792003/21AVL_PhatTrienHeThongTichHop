package baitapdexuat2;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TcpDigitClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("            TCP DIGIT TO WORD CLIENT              ");
        System.out.println("==================================================");

        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("[CLIENT] Kết nối thành công tới Server " + SERVER_HOST + ":" + SERVER_PORT);
            System.out.println("[HƯỚNG DẪN] Nhập 1 chữ số (0-9) để xem cách đọc, hoặc nhập 'QUIT' để thoát.\n");

            while (true) {
                System.out.print("Client > ");
                String input = scanner.nextLine();

                // Gửi dữ liệu sang Server (luôn thêm ký tự xuống dòng '\n')
                writer.write(input);
                writer.newLine();
                writer.flush();

                // Nếu người dùng nhập QUIT thì thoát chương trình
                if ("QUIT".equalsIgnoreCase(input.trim())) {
                    System.out.println("[CLIENT] Đã gửi lệnh QUIT. Đóng kết nối...");
                    break;
                }

                // Nhận phản hồi từ Server
                String response = reader.readLine();
                System.out.println("Server > " + response);
            }

        } catch (IOException e) {
            System.err.println("[CLIENT LỖI] Không thể kết nối tới Server: " + e.getMessage());
        }
    }
}