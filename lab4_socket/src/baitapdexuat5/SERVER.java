package baitapdexuat5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SERVER {
    private static final int PORT = 6000;
    // Cấu trúc Thread-Safe lưu danh sách Nickname -> PrintWriter của Client
    private static final Map<String, PrintWriter> clientsMap = new ConcurrentHashMap<>();
    // Thread Pool quản lý tối đa 50 kết nối đồng thời
    private static final ExecutorService pool = Executors.newFixedThreadPool(50);

    public static void main(String[] args) {
        System.out.println("Server Chat TCP đang khởi chạy tại cổng " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private String nickname;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 1. Xử lý chọn Nickname duy nhất
                while (true) {
                    out.println("NICK_REQ: Nhập nickname duy nhất của bạn:");
                    nickname = in.readLine();
                    if (nickname == null) return;
                    
                    nickname = nickname.trim();
                    if (nickname.isEmpty()) {
                        out.println("ERR: Nickname không được để trống!");
                        continue;
                    }

                    synchronized (clientsMap) {
                        if (!clientsMap.containsKey(nickname)) {
                            clientsMap.put(nickname, out);
                            break; // Đăng ký thành công
                        } else {
                            out.println("ERR: Nickname đã tồn tại. Vui lòng chọn tên khác!");
                        }
                    }
                }

                out.println("OK: Chào mừng " + nickname + " tham gia phòng chat!");
                broadcast("[Hệ thống]: " + nickname + " đã tham gia phòng chat.", nickname);

                // 2. Vòng lặp nhận và xử lý lệnh từ Client
                String message;
                while ((message = in.readLine()) != null) {
                    message = message.trim();

                    if (message.equalsIgnoreCase("QUIT")) {
                        out.println("OK: Tạm biệt!");
                        break;
                    } else if (message.equalsIgnoreCase("USERS")) {
                        out.println("[Danh sách Online]: " + String.join(", ", clientsMap.keySet()));
                    } else if (message.toUpperCase().startsWith("MSG ")) {
                        String content = message.substring(4).trim();
                        if (!content.isEmpty()) {
                            broadcast("[" + nickname + "]: " + content, nickname);
                        }
                    } else {
                        out.println("ERR: Lệnh không hợp lệ! Dùng MSG <nội dung>, USERS, hoặc QUIT.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Client " + (nickname != null ? nickname : "") + " ngắt kết nối đột ngột.");
            } finally {
                // 3. Loại bỏ Client khỏi danh sách khi thoát hoặc gặp lỗi
                if (nickname != null && clientsMap.containsKey(nickname)) {
                    clientsMap.remove(nickname);
                    broadcast("[Hệ thống]: " + nickname + " đã rời phòng chat.", nickname);
                    System.out.println("Đã xóa " + nickname + " khỏi hệ thống.");
                }
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Gửi tin nhắn broadcast đến tất cả các Client khác
        private void broadcast(String msg, String excludeUser) {
            for (Map.Entry<String, PrintWriter> entry : clientsMap.entrySet()) {
                if (!entry.getKey().equals(excludeUser)) {
                    entry.getValue().println(msg);
                }
            }
        }
    }
}