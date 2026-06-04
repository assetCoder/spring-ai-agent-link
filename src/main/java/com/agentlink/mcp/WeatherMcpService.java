package com.agentlink.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 天气 MCP Server
 * <p>
 * 使用 wttr.in 免费天气 API（无需 Key）
 * 暴露工具：
 * - getCurrentWeather: 获取城市实时天气
 * - getWeatherForecast: 获取城市天气预报
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

    @Tool(description = "获取指定城市的实时天气情况，返回温度、天气状况等信息")
    public String getCurrentWeather(String city) {
        try {
            String url = baseUrl + "/" + city + "?format=%C+%t+%h+%w";
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            log.info("Weather API response for {}: {}", city, resp.statusCode());
            return city + " 当前天气: " + resp.body();
        } catch (Exception e) {
            log.error("Failed to get weather for {}", city, e);
            return "查询 " + city + " 天气失败: " + e.getMessage();
        }
    }

    @Tool(description = "获取指定城市未来3天的天气预报")
    public String getWeatherForecast(String city) {
        try {
            String url = baseUrl + "/" + city + "?format=%C+%t&days=3";
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            log.info("Weather forecast response for {}: {}", city, resp.statusCode());
            return city + " 天气预报:\n" + resp.body();
        } catch (Exception e) {
            log.error("Failed to get forecast for {}", city, e);
            return "查询 " + city + " 天气预报失败: " + e.getMessage();
        }
    }
}
