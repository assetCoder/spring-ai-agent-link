package com.agentlink.config;

import org.springframework.ai.mcp.server.McpServerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MCP Server 自动配置导入
 * <p>
 * Spring AI MCP Server WebMVC 自动配置
 * 暴露 SSE (Server-Sent Events) 端点供 MCP 客户端连接
 */
@Configuration
@Import(McpServerAutoConfiguration.class)
public class McpServerConfig {
}
