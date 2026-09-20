package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpCommandClient {

    public static void main(String[] args) {
        // Nhận host và port từ tham số dòng lệnh, mặc định là localhost:5000
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        System.out.println("Đang kết nối tới Server " + host + ":" + port + "...");

        try (
            // 1. Mở kết nối TCP tới Server
            Socket socket = new Socket(host, port);

            // 2. Tạo luồng đọc dữ liệu nhập từ Console bàn phím
            BufferedReader console = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));

            // 3. Tạo luồng nhận dữ liệu từ Server về
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            // 4. Tạo luồng gửi dữ liệu sang Server (autoFlush = true)
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            System.out.println("Kết nối thành công!");
            System.out.println("Nhập các lệnh (PING, TIME, UPPER <văn bản>, QUIT):");

            String request;
            // Đọc từng dòng người dùng nhập từ bàn phím
            while ((request = console.readLine()) != null) {
                
                // Gửi lệnh sang Server (kèm ký tự xuống dòng \n)
                out.println(request);

                // Chờ và đọc 1 dòng phản hồi trả về từ Server
                String response = in.readLine();
                
                // Kiểm tra nếu Server ngắt kết nối đột ngột
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }

                // In phản hồi từ Server ra màn hình
                System.out.println("Server > " + response);

                // Nếu gửi lệnh QUIT thì thoát vòng lặp và đóng Client
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    System.out.println("Đã ngắt kết nối theo yêu cầu.");
                    break;
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Cổng (Port) phải là một số nguyên hợp lệ.");
        } catch (IOException e) {
            System.err.println("Lỗi kết nối Server: " + e.getMessage());
        }
    }
}