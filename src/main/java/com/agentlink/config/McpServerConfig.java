package com.agentlink.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP REST API 端点
 * <p>
 * 提供 SSE (Server-Sent Events) 端点供 MCP 客户端连接
 * 端点：/mcp/tools - 获取工具列表
 * 端点：/mcp/execute - 执行工具
 */
@Configuration
public class McpServerConfig {

    private static final Logger log = LoggerFactory.getLogger(McpServerConfig.class);

    @Bean
    public RouterFunction<ServerResponse> mcpRoutes(List<McpConfig.McpTool> tools, ObjectMapper mapper) {
        return RouterFunctions.route()
                // 健康检查
                .GET("/health", req -> ServerResponse.ok().body(Map.of(
                        "status", "UP",
                        "service", "AgentLink",
                        "version", "1.0.0",
                        "tools", tools.size())))
                // 工具列表
                .GET("/mcp/tools", req ->
                        ServerResponse.ok().body(Map.of("tools",
                                tools.stream().map(McpConfig.McpTool::toJson).toList())))
                // 执行工具
                .POST("/mcp/execute", req -> {
                    byte[] bytes = req.servletRequest().getInputStream().readAllBytes();
                    Map<String, Object> params = mapper.readValue(bytes, Map.class);

                    String toolName = (String) params.get("tool");
                    Map<String, String> args = (Map<String, String>) params.get("arguments");

                    log.info("Execute tool: {} args={}", toolName, args);

                    var tool = tools.stream()
                            .filter(t -> t.name().equals(toolName))
                            .findFirst()
                            .orElse(null);

                    if (tool == null) {
                        return ServerResponse.badRequest().body(Map.of("error", "Tool not found: " + toolName));
                    }

                    try {
                        Object result = tool.executor().apply(args == null ? Map.of() : args);
                        return ServerResponse.ok().body(Map.of(
                                "tool", toolName,
                                "result", result
                        ));
                    } catch (Exception e) {
                        log.error("Tool execution failed: {}", toolName, e);
                        return ServerResponse.ok().body(Map.of(
                                "tool", toolName,
                                "error", e.getMessage()
                        ));
                    }
                })
                .build();
    }
}
