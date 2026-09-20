package baitapdexuat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UdpDateTimeClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("            UDP DATE TIME CLIENT                 ");
        System.out.println("==================================================");

        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            // Cấu hình Timeout 3 giây để tránh treo vô tận khi UDP Server tắt
            socket.setSoTimeout(3000);
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);

            System.out.println("[UDP CLIENT] Sẵn sàng gửi gói tin. Các lệnh: DATE, TIME, DATETIME, EXIT\n");

            while (true) {
                System.out.print("UDP Client > ");
                String command = scanner.nextLine().trim();

                if ("EXIT".equalsIgnoreCase(command)) {
                    System.out.println("[UDP CLIENT] Kết thúc chương trình.");
                    break;
                }

                byte[] sendData = command.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);

                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                try {
                    socket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server > " + response);
                } catch (SocketTimeoutException e) {
                    System.err.println("Server > [LỖI TIMEOUT] Không nhận được phản hồi từ UDP Server (Có thể Server đã bị tắt).");
                }
            }
        } catch (IOException e) {
            System.err.println("[UDP CLIENT LỖI] " + e.getMessage());
        }
    }
}