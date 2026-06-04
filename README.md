# AgentLink — 多 MCP Server 集成平台

> **Spring Boot + MCP 协议** | 天气查询 / 数据库自然语言查询 / 更多服务

## 🏗️ 项目结构

```
AgentLink (Spring Boot 3.2.5, 端口 8081)
│
├── WeatherMcpService      # 🌤️ 天气查询 MCP
│   └── getCurrentWeather()  - 实时天气（wttr.in）
│   └── getWeatherForecast() - 天气预报
│
├── DatabaseMcpService     # 🗄️ 数据库 NL2SQL MCP
│   └── listTables()         - 列出所有表
│   └── getTableSchema()     - 获取表结构
│   └── queryByNaturalLanguage() - 自然语言查数据库
│
└── (更多 MCP Server 持续添加中...)
```

## 🚀 快速开始

### 前置条件
- **Java 17+**
- **Maven 3.8+**
- **MySQL**（如需数据库MCP）
- **DeepSeek API Key**（自然语言转SQL使用）

### 配置

```bash
# 数据库连接（在 application.yml 中配置）
spring.datasource.url=jdbc:mysql://localhost:3306/agentlink
spring.datasource.username=root
spring.datasource.password=root

# DeepSeek API Key
export DEEPSEEK_API_KEY=sk-your-deepseek-api-key
```

### 运行

```bash
mvn package -DskipTests
java -jar target/spring-ai-agent-link-1.0.0.jar
```

## 📡 MCP 工具列表

### 🌤️ 天气 MCP
| 工具 | 描述 | 参数 |
|------|------|------|
| `getCurrentWeather` | 获取城市实时天气 | city (String) |
| `getWeatherForecast` | 获取3天天气预报 | city (String) |

### 🗄️ 数据库查询 MCP
| 工具 | 描述 | 参数 |
|------|------|------|
| `listTables` | 列出所有表 | 无 |
| `getTableSchema` | 获取表结构 | tableName (String) |
| `queryByNaturalLanguage` | 自然语言查数据库 | question (String) |

## 🔗 与 Dify / AI Agent 集成

AgentLink 通过 Spring AI MCP 协议暴露工具，支持：

### 集成到 Dify
1. 在 Dify 中添加 MCP 工具源 → 输入 AgentLink 地址
2. 在 Workflow 中直接拖拽使用

### 集成到 spring-ai-rag-demo
通过 REST API 调用或共享 JVM 上下文。

## 🗺️ 演进路线

- [x] v1.0 - 基础框架：Spring Boot + MCP Server 脚手架
- [x] v1.1 - 天气 MCP：实时天气/预报
- [x] v1.2 - 数据库 MCP：自然语言查询 MySQL
- [ ] v1.3 - 搜索引擎 MCP
- [ ] v1.4 - 对接 Dify Workflow
- [ ] v1.5 - 对接 Telegram Bot

## 🧱 技术栈

| 组件 | 选型 |
|------|------|
| 框架 | Spring Boot 3.2.5 |
| MCP 协议 | Spring AI MCP (WebMVC) |
| 天气 | wttr.in (免费，无需 Key) |
| 数据库 | MySQL + JDBC |
| NL2SQL | DeepSeek Chat (LangChain4j) |
| 构建 | Maven |
