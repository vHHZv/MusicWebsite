package music.website_backend;

import music.website_backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootTest
class WebsiteBackendApplicationTests {
    @Autowired
    private UserServiceImpl userService;

    @Test
    void test() {
        /*String path = System.getProperty("user.dir") + File.separator +
                ".." + File.separator + "files" + File.separator + "img" +
                File.separator + "name";*/

        /*System.out.println(FileOpUtil.BASE_PATH + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "files" + File.separator);*/

        // userService.getCollectSongIds(1L);
        try {
            String IP = InetAddress.getLocalHost().getHostAddress();
            System.out.println(IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
