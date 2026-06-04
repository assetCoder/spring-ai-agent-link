package com.agentlink.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * 天气 MCP Server
 * <p>
 * 使用 wttr.in 免费天气 API（无需 Key）
 */
@Service
public class WeatherMcpService {

    private static final Logger log = LoggerFactory.getLogger(WeatherMcpService.class);

    private final String baseUrl;
    private final HttpClient client;

    public WeatherMcpService(@Value("${weather.api.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String getCurrentWeather(Map<String, String> args) {
        String city = args.getOrDefault("city", "北京");
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            // 注意：wttr.in 的 format 参数包含 %，需要用 URI 构造避免转义问题
            String urlStr = baseUrl + "/" + encodedCity + "?format=%25C+%25t+%25h+%25w";
            var uri = new URI(urlStr);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            log.info("Weather for {}: {}", city, resp.statusCode());
            return city + " 当前天气: " + resp.body();
        } catch (Exception e) {
            log.error("Failed to get weather for {}", city, e);
            return "查询 " + city + " 天气失败: " + e.getMessage();
        }
    }

    public String getWeatherForecast(Map<String, String> args) {
        String city = args.getOrDefault("city", "北京");
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String urlStr = baseUrl + "/" + encodedCity + "?format=%25C+%25t&days=3";
            var uri = new URI(urlStr);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            log.info("Forecast for {}: {}", city, resp.statusCode());
            return city + " 天气预报:\n" + resp.body();
        } catch (Exception e) {
            log.error("Failed to get forecast for {}", city, e);
            return "查询 " + city + " 天气预报失败: " + e.getMessage();
        }
    }
}
