package baitapdexuat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpDateTimeServer {

    private static final int PORT = 9999;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("            UDP DATE TIME SERVER                 ");
        System.out.println("==================================================");

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("[UDP SERVER] Đang lắng nghe tại cổng " + PORT + "...");
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String command = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim().toUpperCase();
                System.out.println("[UDP SERVER] Nhận lệnh '" + command + "' từ " + receivePacket.getSocketAddress());

                // Xử lý lệnh (UDP không xử lý QUIT)
                String response = TcpDateTimeServer.processCommand(command);
                byte[] sendData = response.getBytes(StandardCharsets.UTF_8);

                DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort()
                );
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.err.println("[UDP SERVER LỖI] " + e.getMessage());
        }
    }
}