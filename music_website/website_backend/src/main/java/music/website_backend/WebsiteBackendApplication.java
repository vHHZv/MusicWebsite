package music.website_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class WebsiteBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsiteBackendApplication.class, args);
        System.out.println("=============================================================");
        try {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("获取主机地址失败");
        }
        System.out.println("=============================================================");
    }
}
