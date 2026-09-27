package baitapdexuat6;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerBenchmark {
    public static final int TCP_PORT = 7000;
    public static final int UDP_PORT = 7001;
    public static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        System.out.println("Dang khoi chạy Server Echo TCP & UDP...");

        // Thread chạy TCP Server
        new Thread(() -> startTcpServer()).start();

        // Thread chạy UDP Server
        new Thread(() -> startUdpServer()).start();
    }

    private static void startTcpServer() {
        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try (InputStream in = socket.getInputStream();
                         OutputStream out = socket.getOutputStream()) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                            out.flush();
                        }
                    } catch (Exception e) {
                        // Connection closed
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startUdpServer() {
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                // Phản hồi lại gói tin vừa nhận
                DatagramPacket reply = new DatagramPacket(
                        packet.getData(), packet.getLength(),
                        packet.getAddress(), packet.getPort());
                socket.send(reply);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}