package com.agentlink.mcp;

import com.agentlink.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 数据库查询 MCP Server
 * <p>
 * 通过自然语言查询 MySQL 数据库
 * 暴露工具：
 * - listTables: 列出所有表
 * - getTableSchema: 获取表结构
 * - queryByNaturalLanguage: 自然语言转SQL并查询
 */
@Service
public class DatabaseMcpService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMcpService.class);

    private final JdbcTemplate jdbcTemplate;
    private final AiService aiService;

    public DatabaseMcpService(JdbcTemplate jdbcTemplate, AiService aiService) {
        this.jdbcTemplate = jdbcTemplate;
        this.aiService = aiService;
    }

    @Tool(description = "列出数据库中所有表名")
    public List<String> listTables() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE()",
                String.class);
        log.info("Found {} tables", tables.size());
        return tables;
    }

    @Tool(description = "获取指定表的字段结构（字段名、类型、是否主键、注释）")
    public List<Map<String, Object>> getTableSchema(String tableName) {
        String sql = """
                SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_COMMENT
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?
                ORDER BY ORDINAL_POSITION
                """;
        return jdbcTemplate.queryForList(sql, tableName);
    }

    @Tool(description = "通过自然语言查询数据库，自动将用户问题转为SQL并执行返回结果")
    public String queryByNaturalLanguage(String question) {
        try {
            // 1. 获取所有表结构
            StringBuilder schemaBuilder = new StringBuilder();
            List<String> tables = listTables();
            for (String table : tables) {
                schemaBuilder.append("表名: ").append(table).append("\n");
                List<Map<String, Object>> columns = getTableSchema(table);
                for (Map<String, Object> col : columns) {
                    schemaBuilder.append("  - ")
                            .append(col.get("COLUMN_NAME")).append(" ")
                            .append(col.get("COLUMN_TYPE"))
                            .append(" [").append(col.get("COLUMN_KEY")).append("]")
                            .append(" ").append(col.get("COLUMN_COMMENT") != null ? col.get("COLUMN_COMMENT") : "")
                            .append("\n");
                }
            }

            // 2. AI 转 SQL
            String sql = aiService.naturalLanguageToSql(question, schemaBuilder.toString());
            log.info("NL2SQL: {} -> {}", question, sql);

            if (sql.trim().startsWith("--")) {
                return sql.trim().substring(2).trim();
            }

            // 3. 安全检查：只允许 SELECT
            if (!sql.trim().toUpperCase().startsWith("SELECT")) {
                return "只支持查询操作（SELECT），已拒绝不安全SQL";
            }

            // 4. 执行查询
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            return "SQL: " + sql + "\n\n查询结果（共" + results.size() + "条）:\n" + formatResults(results);

        } catch (Exception e) {
            log.error("Database query error", e);
            return "查询失败: " + e.getMessage();
        }
    }

    private String formatResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) return "无数据";
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(results.size(), 20);
        for (int i = 0; i < limit; i++) {
            sb.append(i + 1).append(". ");
            Map<String, Object> row = results.get(i);
            row.forEach((key, value) -> sb.append(key).append("=").append(value).append(", "));
            sb.setLength(sb.length() - 2);
            sb.append("\n");
        }
        if (results.size() > 20) {
            sb.append("... 还有 ").append(results.size() - 20).append(" 条");
        }
        return sb.toString();
    }
}
