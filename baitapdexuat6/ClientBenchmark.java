package baitapdexuat6;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class ClientBenchmark {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int TOTAL_MESSAGES = 1000;
    private static final int MESSAGE_SIZE = 1024; // 1 KB
    private static final int TIMEOUT_MS = 1000;  // 1 giay timeout cho UDP
    private static final int NUM_RUNS = 5;       // Lap lai 5 lan

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   THỰC NGHIỆM SO SÁNH HIỆU NĂNG TCP VÀ UDP");
        System.out.println("==================================================");
        System.out.println("Môi trường: Localhost (" + SERVER_IP + ")");
        System.out.println("Số lượng thông điệp / lần chạy: " + TOTAL_MESSAGES);
        System.out.println("Kích thước thông điệp: " + MESSAGE_SIZE + " bytes");
        System.out.println("Timeout nhận gói (UDP): " + TIMEOUT_MS + " ms");
        System.out.println("Số lần lặp thử nghiệm: " + NUM_RUNS + " lần");
        System.out.println("--------------------------------------------------\n");

        byte[] sampleData = new byte[MESSAGE_SIZE];
        Arrays.fill(sampleData, (byte) 'A');

        long totalTcpTime = 0;
        long totalUdpTime = 0;

        for (int run = 1; run <= NUM_RUNS; run++) {
            System.out.println(">>> LẦN CHẠY THỨ " + run + " <<<");

            // --- ĐO TCP ---
            long tcpStart = System.currentTimeMillis();
            int tcpResponses = testTcp(sampleData);
            long tcpTime = System.currentTimeMillis() - tcpStart;
            totalTcpTime += tcpTime;
            System.out.printf("TCP: Phản hồi %d/%d | Thời gian: %d ms\n", 
                    tcpResponses, TOTAL_MESSAGES, tcpTime);

            // Nghỉ 100ms giữa 2 thử nghiệm
            try { Thread.sleep(100); } catch (Exception e) {}

            // --- ĐO UDP ---
            long udpStart = System.currentTimeMillis();
            int udpResponses = testUdp(sampleData);
            long udpTime = System.currentTimeMillis() - udpStart;
            totalUdpTime += udpTime;
            System.out.printf("UDP: Phản hồi %d/%d | Thời gian: %d ms\n", 
                    udpResponses, TOTAL_MESSAGES, udpTime);

            System.out.println("--------------------------------------------------");
        }

        System.out.println("\n================ KẾT QUẢ TRUNG BÌNH ================");
        System.out.printf("Thời gian trung bình TCP: %.2f ms\n", (double) totalTcpTime / NUM_RUNS);
        System.out.printf("Thời gian trung bình UDP: %.2f ms\n", (double) totalUdpTime / NUM_RUNS);
        System.out.println("====================================================");
    }

    private static int testTcp(byte[] data) {
        int count = 0;
        try (Socket socket = new Socket(SERVER_IP, ServerBenchmark.TCP_PORT);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            byte[] recvBuffer = new byte[MESSAGE_SIZE];
            for (int i = 0; i < TOTAL_MESSAGES; i++) {
                out.write(data);
                out.flush();

                int totalRead = 0;
                while (totalRead < MESSAGE_SIZE) {
                    int read = in.read(recvBuffer, totalRead, MESSAGE_SIZE - totalRead);
                    if (read == -1) break;
                    totalRead += read;
                }
                if (totalRead == MESSAGE_SIZE) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private static int testUdp(byte[] data) {
        int count = 0;
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress address = InetAddress.getByName(SERVER_IP);
            byte[] recvBuffer = new byte[MESSAGE_SIZE];

            for (int i = 0; i < TOTAL_MESSAGES; i++) {
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, ServerBenchmark.UDP_PORT);
                socket.send(sendPacket);

                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                try {
                    socket.receive(recvPacket);
                    count++;
                } catch (SocketTimeoutException e) {
                    // Mất gói tin hoặc không nhận được phản hồi
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}