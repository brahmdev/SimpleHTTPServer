import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import java.util.*;

public class HttpServerTest {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.createContext("/repository", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "{\n" +
                    "\t\"payload\": {\n" +
                    "\t\t\"google\": {\n" +
                    "\t\t\t\"expectUserResponse\": true,\n" +
                    "\t\t\t\"richResponse\": {\n" +
                    "\t\t\t\t\"items\": [{\n" +
                    "\t\t\t\t\t\t\"simpleResponse\": {\n" +
                    "\t\t\t\t\t\t\t\"textToSpeech\": \"I felt very nice to create your collection.\"\n" +
                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"userStorage\": \"{\\\"data\\\":{}}\"\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "}";
            Headers headers = he.getRequestHeaders();
            Headers responseHeaders = he.getResponseHeaders();
            for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                responseHeaders.put(header.getKey(), header.getValue());
            }
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
            System.out.println("*****************************************\n*******************************");
            for (Map.Entry<String, List<String>> entry : entries) {
                System.out.println(entry.toString());
            }

            InputStreamReader isr =  new InputStreamReader(he.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);

// From now on, the right way of moving from bytes to utf-8 characters:

            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }

            br.close();
            isr.close();
            System.out.println("Request Body is: "+ buf.toString());
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}