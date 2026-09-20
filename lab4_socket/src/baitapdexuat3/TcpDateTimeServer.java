package baitapdexuat3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TcpDateTimeServer {

    private static final int PORT = 9999;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH mm ss");

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("            TCP DATE TIME SERVER                 ");
        System.out.println("==================================================");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[TCP SERVER] Đang lắng nghe tại cổng " + PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[TCP SERVER] Khách kết nối từ: " + socket.getRemoteSocketAddress());
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[TCP SERVER LỖI] " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                String command = inputLine.trim().toUpperCase();
                
                if ("QUIT".equals(command)) {
                    System.out.println("[TCP SERVER] Client ngắt kết nối (QUIT).");
                    break;
                }

                String response = processCommand(command);
                writer.write(response);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("[TCP SERVER] Mối kết nối bị đứt đột ngột.");
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public static String processCommand(String command) {
        LocalDateTime now = LocalDateTime.now();
        switch (command) {
            case "DATE":
                return now.format(DATE_FMT);
            case "TIME":
                return now.format(TIME_FMT);
            case "DATETIME":
                return now.format(DATE_FMT) + " " + now.format(TIME_FMT);
            default:
                return "ERR UNKNOWN_COMMAND";
        }
    }
}