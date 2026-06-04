package com.agentlink.config;

import com.agentlink.mcp.DatabaseMcpService;
import com.agentlink.mcp.WeatherMcpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP 工具注册
 * <p>
 * 管理所有 MCP 工具的元数据和函数引用
 */
@Configuration
public class McpConfig {

    @Bean
    public List<McpTool> agentLinkTools(
            WeatherMcpService weatherMcpService,
            DatabaseMcpService databaseMcpService) {
        return List.of(
                // 天气工具
                new McpTool("getCurrentWeather", "获取指定城市的实时天气（温度、湿度、风速、天气状况）",
                        Map.of("city", "string"), weatherMcpService::getCurrentWeather),
                new McpTool("getWeatherForecast", "获取指定城市未来3天的天气预报",
                        Map.of("city", "string"), weatherMcpService::getWeatherForecast),
                // 数据库工具
                new McpTool("listTables", "列出数据库中所有表名",
                        Map.of(), databaseMcpService::listTables),
                new McpTool("getTableSchema", "获取指定表的字段结构（字段名、类型、主键、注释）",
                        Map.of("tableName", "string"), databaseMcpService::getTableSchema),
                new McpTool("queryByNaturalLanguage", "通过自然语言查询数据库，自动转为SQL并执行",
                        Map.of("question", "string"), databaseMcpService::queryByNaturalLanguage)
        );
    }

    /**
     * MCP 工具定义
     */
    public record McpTool(
            String name,
            String description,
            Map<String, String> parameters,
            java.util.function.Function<Map<String, String>, Object> executor
    ) {
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("name", name);
            json.put("description", description);
            json.put("parameters", parameters);
            return json;
        }
    }
}
