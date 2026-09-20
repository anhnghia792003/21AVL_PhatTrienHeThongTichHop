package network;


import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostInspector {

    public static void main(String[] args) {
        // Kiểm tra thiếu tham số
        if (args.length != 1) {
            System.out.println("Usage: java network.HostInspector <hostname>");
            return;
        }

        String hostname = args[0];

        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            System.out.println("Host: " + hostname);

            for (InetAddress address : addresses) {
                System.out.println("- IP: " + address.getHostAddress());
                
                // Bổ sung thông báo kiểm tra IPv4 hay IPv6
                if (address instanceof Inet4Address) {
                    System.out.println("  IP Type: IPv4");
                } else if (address instanceof Inet6Address) {
                    System.out.println("  IP Type: IPv6");
                } else {
                    System.out.println("  IP Type: Unknown");
                }

                System.out.println("  Canonical : " + address.getCanonicalHostName());
                System.out.println("  Loopback  : " + address.isLoopbackAddress());
                System.out.println("  Site local: " + address.isSiteLocalAddress());
            }

        } catch (UnknownHostException e) {
            // Thông báo lỗi rõ ràng, không in stack trace dài
            System.err.println("Không phân giải được host: " + hostname);
        }
    }
}