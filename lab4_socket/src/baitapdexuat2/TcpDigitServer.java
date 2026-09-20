package baitapdexuat2;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpDigitServer {

    private static final int PORT = 8888;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("            TCP DIGIT TO WORD SERVER              ");
        System.out.println("==================================================");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[SERVER] Đang lắng nghe tại cổng " + PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("\n[SERVER] Đã kết nối với Client: " + socket.getRemoteSocketAddress());

                // Xử lý từng Client trong luồng riêng biệt
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVER LỖI] " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                // Kiểm tra lệnh thoát QUIT từ Client (không phân biệt chữ hoa/thường)
                if ("QUIT".equalsIgnoreCase(inputLine.trim())) {
                    System.out.println("[SERVER] Client yêu cầu ngắt kết nối (QUIT).");
                    break;
                }

                // Xử lý logic đọc chữ số
                String response = processDigitInput(inputLine);

                // Gửi phản hồi lại Client (mỗi thông điệp một dòng)
                writer.write(response);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Mối kết nối với Client bị ngắt.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    /**
     * Kiểm tra dữ liệu đầu vào và chuyển đổi thành chữ tiếng Việt hoặc báo lỗi ERR INVALID_DIGIT
     */
    private static String processDigitInput(String input) {
        // Kiểm tra đúng một ký tự từ '0' đến '9' (Không chấp nhận khoảng trắng hay chuỗi dài > 1)
        if (input == null || input.length() != 1 || !Character.isDigit(input.charAt(0))) {
            return "ERR INVALID_DIGIT";
        }

        switch (input.charAt(0)) {
            case '0': return "Không";
            case '1': return "Một";
            case '2': return "Hai";
            case '3': return "Ba";
            case '4': return "Bốn";
            case '5': return "Năm";
            case '6': return "Sáu";
            case '7': return "Bảy";
            case '8': return "Tám";
            case '9': return "Chín";
            default: return "ERR INVALID_DIGIT";
        }
    }
}
