import org.eclipse.jetty.server.Server;

import java.io.File;

public class Main {

    public static boolean test = new File("/home/tookuk").exists();

    public static void main(String[] args) throws Exception {

        Server server = new Server(test?8080:80);
        server.setHandler(new HlcHandler(FileLoader.load()));

        server.start();
        server.join();
    }

}
