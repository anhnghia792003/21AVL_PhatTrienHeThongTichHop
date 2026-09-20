package baitapdexuat1;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostAndUriInspector {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("           HOST AND URI INSPECTOR SYSTEM          ");
        System.out.println("==================================================");

        // 1. Kiểm tra tham số đầu vào (Bắt buộc phải đủ 2 tham số riêng biệt)
        if (args.length < 2) {
            System.err.println("[LỖI KHÔNG HỢP LỆ] Thiếu tham số truyền vào!");
            System.err.println("Cú pháp yêu cầu: java HostAndUriInspector <hostname> <URI>");
            System.err.println("Ví dụ: java HostAndUriInspector google.com https://google.com:443/search?q=java#top");
            System.out.println("==================================================");
            return; // Dừng chương trình ngay lập tức, không chạy dữ liệu mặc định
        }

        String hostnameInput = args[0];
        String uriInput = args[1];

        System.out.println("--> Hostname nhận vào : " + hostnameInput);
        System.out.println("--> URI nhận vào      : " + uriInput);
        System.out.println("--------------------------------------------------");

        // 2. Phân tích URI bằng java.net.URI
        System.out.println("[1] PHÂN TÍCH THÀNH PHẦN URI:");
        try {
            URI uri = new URI(uriInput);
            System.out.println("  • Scheme   : " + (uri.getScheme() != null ? uri.getScheme() : "(Không có)"));
            System.out.println("  • Host     : " + (uri.getHost() != null ? uri.getHost() : "(Không có)"));
            System.out.println("  • Port     : " + (uri.getPort() != -1 ? uri.getPort() : "(Default/Không có)"));
            System.out.println("  • Path     : " + (uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "(Rỗng)"));
            System.out.println("  • Query    : " + (uri.getQuery() != null ? uri.getQuery() : "(Không có)"));
            System.out.println("  • Fragment : " + (uri.getFragment() != null ? uri.getFragment() : "(Không có)"));
        } catch (URISyntaxException e) {
            System.err.println("  [LỖI URI] Cú pháp URI không hợp lệ: " + e.getMessage());
        }

        System.out.println("--------------------------------------------------");

        // 3. Phân tích Hostname và tra cứu địa chỉ IP
        System.out.println("[2] PHÂN TÍCH ĐỊA CHỈ IP THUỘC HOSTNAME:");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostnameInput);
            System.out.println("  Tìm thấy " + addresses.length + " địa chỉ IP:");

            for (int i = 0; i < addresses.length; i++) {
                InetAddress addr = addresses[i];
                String ipType = "Khác";
                if (addr instanceof Inet4Address) {
                    ipType = "IPv4";
                } else if (addr instanceof Inet6Address) {
                    ipType = "IPv6";
                }

                System.out.printf("  [%d] IP: %-39s | Loại: %-4s | Loopback: %-5b | Site Local: %-5b%n",
                        (i + 1),
                        addr.getHostAddress(),
                        ipType,
                        addr.isLoopbackAddress(),
                        addr.isSiteLocalAddress()
                );
            }
        } catch (UnknownHostException e) {
            System.err.println("  [LỖI HOSTNAME] Không thể phân giải hostname: " + hostnameInput);
        }

        System.out.println("==================================================");
    }
}