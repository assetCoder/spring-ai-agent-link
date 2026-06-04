package com.agentlink.config;

import com.agentlink.mcp.DatabaseMcpService;
import com.agentlink.mcp.WeatherMcpService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * MCP Server 配置
 * <p>
 * 将所有 @Tool 标注的服务暴露为 MCP 工具
 */
@Configuration
public class McpConfig {

    @Bean
    public List<ToolCallback> weatherTools(WeatherMcpService weatherMcpService) {
        return ToolCallbacks.from(weatherMcpService);
    }

    @Bean
    public List<ToolCallback> databaseTools(DatabaseMcpService databaseMcpService) {
        return ToolCallbacks.from(databaseMcpService);
    }
}
