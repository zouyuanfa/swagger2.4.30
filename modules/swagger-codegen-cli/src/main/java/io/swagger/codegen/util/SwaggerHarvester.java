package io.swagger.codegen.util;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

public class SwaggerHarvester {
    private final String baseUrl;
    private final Path outputDir;
    private final Set<String> visitedUrls = new HashSet<>();
    private final Queue<String> urlQueue = new LinkedList<>();
    private int fileCount = 0;
    private final List<String> errorUrls = new ArrayList<>();

    // app-token
    private static final String APP_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHQiOnsiYWNjb3VudElkIjoiMTY0MTM2MDk5MzAxMDMyNDI1MCIsImdyb3VwTmFtZSI6Im5zLXQ1MWMiLCJhcHBfa2V5IjoiN0lqTnJ5QUsiLCJ0ZW5hbnRJZCI6Ijc5ODY2OTM5OTU4IiwidXNlck5hbWUiOiJtdWx0aWNvcnBfMTMzODU4OTAyIiwidXNlcklkIjoxMzM4NTg5MDJ9LCJncnAiOiJucy10NTFjIiwiZXhwIjoxNzUyNjM0NzU2LCJhaWQiOiIxNjQxMzYwOTkzMDEwMzI0MjUwIiwiaWF0IjoxNzUyNTQ4MzU2fQ.Lwm5zD6_qk_u44kHvsc6nVRcOoOvbtml9uk-OnlgQXg";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    // 输出 swagger的文件目录
    private static final String BASE_OUTPUT_PATH = "E:\\Java\\Kingdee\\swagger2.4.30\\modules\\swagger-codegen-cli\\src\\main\\java\\io\\swagger\\codegen\\aster";

    public SwaggerHarvester(String baseUrl) throws IOException {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.outputDir = Paths.get(BASE_OUTPUT_PATH);
        Files.createDirectories(this.outputDir);
        System.out.println("Output will be saved to: " + this.outputDir);
    }

    public void startHarvest(){
        String entryPoint = baseUrl + "/docs/jdy-stars-grpc/";
        urlQueue.add(entryPoint);

        while (!urlQueue.isEmpty()) {
            String currentUrl = urlQueue.poll();
            if (visitedUrls.contains(currentUrl)) {
                continue;
            }

            try {
                processUrl(currentUrl);
            } catch (Exception e) {
                System.err.println("Error processing " + currentUrl + ": " + e.getMessage());
                errorUrls.add(currentUrl);
            }
        }

        System.out.println("\nHarvest complete! Downloaded " + fileCount + " files.");
        if (!errorUrls.isEmpty()) {
            System.out.println("Errors occurred in " + errorUrls.size() + " URLs:");
            for (String url : errorUrls) {
                System.out.println(" - " + url);
            }
        }
    }

    private void processUrl(String url) throws IOException, URISyntaxException {
        System.out.println("\nProcessing: " + url);
        visitedUrls.add(url);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("app-token", APP_TOKEN);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP request failed with code: " + responseCode);
        }

        String contentType = connection.getContentType();
        if (contentType == null) {
            contentType = "";
        }

        if (contentType.contains("html")) {
            String html = readResponse(connection);
            processHtmlDirectory(html, url);
        } else if (contentType.contains("json")) {
            saveJsonFile(connection, url);
        } else {
            System.out.println("Unknown content type: " + contentType);
        }
    }

    private void processHtmlDirectory(String html, String parentUrl) throws MalformedURLException {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");

        for (Element link : links) {
            String href = link.attr("href");

            if (href.contains("file=")) {
                processSwaggerLink(href, parentUrl);
                continue;
            }

            String fullUrl = normalizeUrl(href, parentUrl);

            if (!isValidUrl(fullUrl)) {
                continue;
            }

            if (href.endsWith("/")) {
                urlQueue.add(fullUrl);
            } else if (href.toLowerCase().endsWith(".swagger.json")) {
                urlQueue.add(fullUrl);
            } else {
                System.out.println("Skipping non-Swagger link: " + href);
            }
        }
    }

    private void processSwaggerLink(String href, String parentUrl) throws MalformedURLException {
        Pattern pattern = Pattern.compile("file=([^&]+)");
        Matcher matcher = pattern.matcher(href);

        if (matcher.find()) {
            String jsonPath = matcher.group(1);
            String jsonUrl = new URL(new URL(baseUrl), jsonPath).toString();

            if (!jsonUrl.contains("/openapiv2/")) {
                return;
            }

            String relativePath = jsonPath.replaceAll("^/+", "");
            Path savePath = outputDir.resolve(relativePath);

            if (!visitedUrls.contains(jsonUrl)) {
                urlQueue.add(jsonUrl);
            }
        }
    }

    private void saveJsonFile(HttpURLConnection connection, String url) throws IOException, URISyntaxException {
        URI uri = new URI(url);
        String[] pathComponents = uri.getPath().split("/");

        // 过滤掉不需要的路径部分
        List<String> filteredComponents = new ArrayList<>();
        boolean skipNext = false;
        for (String component : pathComponents) {
            if (component.equals("openapiv2") || component.equals("jdy-stars-grpc")) {
                skipNext = true;
                continue;
            }
            if (!component.isEmpty() && !skipNext) {
                filteredComponents.add(component);
            }
            skipNext = false;
        }

        // 构建保存路径
        Path savePath = outputDir;
        for (String component : filteredComponents) {
            savePath = savePath.resolve(component);
        }

        // 确保是.json文件
        if (!savePath.getFileName().toString().toLowerCase().endsWith(".json")) {
            savePath = savePath.resolveSibling(savePath.getFileName() + ".json");
        }

        Files.createDirectories(savePath.getParent());

        try {
            String jsonContent = readResponse(connection);
            objectMapper.readTree(jsonContent);
            Files.write(savePath, jsonContent.getBytes(), StandardOpenOption.CREATE);
            fileCount++;
            System.out.println("Saved: " + savePath);
        } catch (JsonProcessingException e) {
            System.out.println("Invalid JSON at " + url);
            String content = readResponse(connection);
            Files.write(savePath, content.getBytes(), StandardOpenOption.CREATE);
            System.out.println("Saved raw content: " + savePath);
        }
    }

    private String normalizeUrl(String href, String parentUrl) throws MalformedURLException {
        if (href.startsWith("/swagger-ui/")) {
            return new URL(new URL(baseUrl), href).toString();
        }

        if (!href.startsWith("http")) {
            return new URL(new URL(parentUrl), href).toString();
        }

        URL url = new URL(href);
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath()).toString();
    }

    private boolean isValidUrl(String url) {
        if (!url.startsWith(baseUrl)) {
            return false;
        }

        String[] validPaths = {"/docs/jdy-stars-grpc", "/openapiv2/"};
        boolean hasValidPath = false;
        for (String path : validPaths) {
            if (url.contains(path)) {
                hasValidPath = true;
                break;
            }
        }
        if (!hasValidPath) {
            return false;
        }

        return !visitedUrls.contains(url);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    // 下载所有API 的swagger.JSON文件
    public static void main(String[] args) {
        try {
            SwaggerHarvester harvester = new SwaggerHarvester("https://tf.jdy.com");
            harvester.startHarvest();
            System.out.println("所有Swagger文档已下载完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}