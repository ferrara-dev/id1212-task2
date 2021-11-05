package app;

import app.controller.Controller;
import app.model.Game;
import app.model.HttpRequestParser;
import app.template.Template;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;

public class RequestProcessor implements Runnable {
    private static Controller controller = new Controller();
    private File rootDirectory;
    private String indexFileName = "index.html";
    private Socket connection;

    public RequestProcessor(File rootDirectory, String indexFileName, Socket connection) {
        if (rootDirectory.isFile()) {
            throw new IllegalArgumentException(
                    "rootDirectory must be a directory, not a file");
        }
        try {
            rootDirectory = rootDirectory.getCanonicalFile();
        } catch (IOException ex) {
        }
        this.rootDirectory = rootDirectory;
        if (indexFileName != null) this.indexFileName = indexFileName;
        this.connection = connection;
    }

    @Override
    public void run() {
        // for security checks
        String root = rootDirectory.getPath();
        try {
            OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
            Writer out = new OutputStreamWriter(raw);
            BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            HttpRequestParser request = new HttpRequestParser(in);

            List<String> cookies = new ArrayList<>();

            String method = request.getMethod();
            URI uri = new URI(request.getURL());
            String version = request.getVersion();
            String idCookie = request.getCookie("id");

            if(Objects.isNull(idCookie)){
                Game g = controller.startGame();
                idCookie = g.getId();
            }

            cookies.add("id=" + idCookie);
            if (method.equals("GET")) {
                String fileName = uri.getPath();
                if (fileName.equals("/"))
                    fileName += indexFileName;
                String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
                File theFile = new File(rootDirectory, fileName.substring(1, fileName.length()));
                // Don't let clients outside the document root
                if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
                    byte[] theData = Files.readAllBytes(theFile.toPath());
                    if (version.startsWith("HTTP/")) { // send a MIME header
                        sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length, cookies);
                    }
                    // send the file; it may be an image or other binary data
                    // so use the underlying output stream
                    // instead of the writer
                    raw.write(theData);
                    raw.flush();
                }
                else { // can't find the file
                    String body = new StringBuilder("<HTML>\r\n")
                            .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
                            .append("</HEAD>\r\n")
                            .append("<BODY>")
                            .append("<H1>HTTP Error 404: Not Found</H1>\r\n")
                            .append("</BODY></HTML>\r\n").toString();
                    if (version.startsWith("HTTP/")) { // send a MIME header
                        sendHeader(out, "HTTP/1.0 404 File Not Found",
                                "text/html; charset=utf-8", body.length(),cookies);
                    }
                    out.write(body);
                    out.flush();
                }
            } else if(method.equals("POST")){
                String body = request.getMessageBody();
                if(uri.getPath().equals("/guess")) {
                    int guess = Integer.parseInt(body.split("\r\n")[0].split("=")[1]);
                    String id = request.getCookie("id");
                    String page = controller.guess(id,guess);
                    //
                    sendHeader(out, "HTTP/1.0 200 OK", "text/html; charset=utf-8", body.length(),cookies);
                    raw.write(page.getBytes());
                    raw.flush();
                }
            }
            else { // method does not equal "GET" or "POST"
                String body = new StringBuilder("<HTML>\r\n")
                        .append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
                        .append("</HEAD>\r\n")
                        .append("<BODY>")
                        .append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
                        .append("</BODY></HTML>\r\n").toString();
                if (version.startsWith("HTTP/")) { // send a MIME header
                    sendHeader(out, "HTTP/1.0 501 Not Implemented", "text/html; charset=utf-8", body.length(),cookies);
                }
                out.write(body);
                out.flush();
            }
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();

        }
    }

    private void sendHeader(Writer out, String responseCode, String contentType, int length, List<String> cookies) throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Content-length: " + length + "\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: HTTPServer 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        if(!cookies.isEmpty()){
            StringBuilder s = new StringBuilder();
            s.append("Set-Cookie: ");
            for(String c : cookies){
                s.append(c + " ");
            }
            s.append("\r\n");
            out.write(s.toString());
        }
        out.write("Content-type: " + contentType + "\r\n\r\n");

        out.flush();

    }

}
