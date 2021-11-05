package app.model;

import java.io.*;
import java.nio.CharBuffer;
import java.util.Hashtable;
import java.util.Objects;

public class HttpRequestParser {
    private String rawRequest;
    private String _requestLine;
    private Hashtable<String,String> cookieStore;
    private Hashtable<String, String> _requestHeaders;
    private StringBuffer _messagetBody;
    private String method;
    private String url;
    private String version;


    public String getMethod(){
        return method;
    }

    public HttpRequestParser(BufferedReader in) throws IOException {
        _requestHeaders = new Hashtable<String, String>();
        _messagetBody = new StringBuffer();
        cookieStore = new Hashtable<>();
        parseRequest(in);
    }

    public String getURL(){
        return url;
    }

    public String getVersion(){
        return version;
    }

    /**
     * Parse and HTTP request.
     *

     *            String holding http request.
     * @throws IOException
     *             If an I/O error occurs reading the input stream.
     *             If HTTP Request is malformed
     */
    public void parseRequest(BufferedReader reader) throws IOException{

        setRequestLine(reader.readLine()); // Request-Line ; Section 5.1

        String header = reader.readLine();
        while (header.length() > 0) {
            appendHeaderParameter(header);
            header = reader.readLine();
        }

        String cookie = getHeaderParam("Cookie");
        if(Objects.nonNull(cookie)){
            for(String c : cookie.trim().split("\\s+")){
                String [] kv = c.split("=");
                cookieStore.put(kv[0],kv[1]);
            }
        }
        String body = "";

        int contentLength = Integer.parseInt(_requestHeaders.getOrDefault("Content-Length","0").trim());
        if(contentLength > 0) {
            char buffer[] = new char[contentLength];
            for (int i = 0; i < contentLength; i++) {
                int c = reader.read();
                if (c == -1)
                    break;
                buffer[i] = (char) c;
            }
            body = new String(buffer);
            for(String bodyLine : body.split("\r\n")){
                if(bodyLine.equals(""))
                    break;
                appendMessageBody(bodyLine);
            }
        }
    }

    /**
     *
     * 5.1 Request-Line The Request-Line begins with a method token, followed by
     * the Request-URI and the protocol version, and ending with CRLF. The
     * elements are separated by SP characters. No CR or LF is allowed except in
     * the final CRLF sequence.
     *
     * @return String with Request-Line
     */
    public String getRequestLine() {
        return _requestLine;
    }

    public String getCookie(String key){
        return cookieStore.get(key);
    }

    private void setRequestLine(String requestLine) {
        if (requestLine == null || requestLine.length() == 0) {
            throw new IllegalArgumentException("Invalid Request-Line: " + requestLine);
        }
        _requestLine = requestLine;
        String get = _requestLine;
        String[] tokens = get.split("\\s+");
        this.method = tokens[0];
        this.url = tokens[1];
        this.version = tokens[2];
    }

    private void appendHeaderParameter(String header)  {
        int idx = header.indexOf(":");
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid Header Parameter: " + header);
        }
        _requestHeaders.put(header.substring(0, idx), header.substring(idx + 1, header.length()));
    }

    /**
     * The message-body (if any) of an HTTP message is used to carry the
     * entity-body associated with the request or response. The message-body
     * differs from the entity-body only when a transfer-coding has been
     * applied, as indicated by the Transfer-Encoding header field (section
     * 14.41).
     * @return String with message-body
     */
    public String getMessageBody() {
        return _messagetBody.toString();
    }

    private void appendMessageBody(String bodyLine) {
        _messagetBody.append(bodyLine).append("\r\n");
    }

    /**
     * For list of available headers refer to sections: 4.5, 5.3, 7.1 of RFC 2616
     * @param headerName Name of header
     * @return String with the value of the header or null if not found.
     */
    public String getHeaderParam(String headerName){
        return _requestHeaders.get(headerName);
    }
}
