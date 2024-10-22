package back;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/info", new DataHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("server: " + server.getAddress());
    }

    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Request-Method", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "*");
            String method = exchange.getRequestMethod();
            if ("POST".equals(method)) {
                try (InputStream requestBody = exchange.getRequestBody();
                     ByteArrayOutputStream output = new ByteArrayOutputStream()) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = requestBody.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    String text = output.toString();

                    Files.write(Paths.get("info.txt"), text.getBytes());

                    exchange.sendResponseHeaders(200, -1);
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.close();
                }
            } else if ("GET".equals(method)) {
                String data = new String(Files.readAllBytes(Paths.get("info.txt")));
                exchange.sendResponseHeaders(200, data.getBytes().length);
                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(data.getBytes());
                }

            }
        }
    }
}
