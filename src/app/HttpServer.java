package app;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final int NUM_THREADS = 50;
    private static final String INDEX_FILE = "index.html";
    private static final String DOCUMENT_ROOT = "/home/user/Desktop/workshop/IV1212_task2/web";
    private final File rootDirectory;
    private final int port;

    public HttpServer(File rootDirectory, int port) throws IOException {
        if (!rootDirectory.isDirectory()) {
            throw new IOException(rootDirectory
                    + " does not exist as a directory");
        }
        this.rootDirectory = rootDirectory;
        this.port = port;
    }

    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket request = server.accept();

                    Runnable r = new RequestProcessor(
                            rootDirectory, INDEX_FILE, request);
                    pool.submit(r);
                } catch (IOException ex) {

                }
            }
        }
    }

    public static void main(String[] args) {
        // get the Document root
        File docroot;
        try {
            docroot = new File(DOCUMENT_ROOT);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Usage: java HTTP docroot port");
            return;
        }
        // set the port to listen on
        int port = 8080;

        try {
            HttpServer webserver = new HttpServer(docroot, port);
            webserver.start();
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }
}
