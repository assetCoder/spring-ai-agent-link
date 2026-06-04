# AgentLink — 多 MCP Server 集成平台

> **Spring Boot + Spring AI MCP 协议** | 🌤️ 天气查询 / 🗄️ 数据库自然语言查询 / ➕ 更多服务

AgentLink 是一个 **MCP (Model Context Protocol) Server** 集成平台，让 AI Agent 通过标准协议连接真实世界的数据和服务。

## 🏗️ 系统架构

```
AI Agent (Dify / Claude Desktop / 自定义Agent)
        │
        │ MCP 协议 (SSE/Stdio)
        ▼
┌───────────────────────┐
│     AgentLink         │  ← 端口 8081
│  MCP Server Hub       │
├───────────────────────┤
│                       │
│  🌤️ WeatherMcpService │  ← 天气查询 (wttr.in)
│  🗄️ DatabaseMcpService│  ← 数据库 NL2SQL (DeepSeek)
│                       │
│  ➕ 更多 MCP 接入中...  │
└───────────────────────┘
```

## ✨ 核心特性

### 🌤️ 天气查询 MCP
- 实时天气：温度、湿度、风速、天气状况
- 3天天气预报
- 全球城市支持（wttr.in，无需 API Key）

### 🗄️ 数据库自然语言查询 MCP
- `listTables()` - 列出所有表
- `getTableSchema()` - 查看表结构
- `queryByNaturalLanguage()` - 直接说人话查数据
- 自动 NL2SQL（DeepSeek），安全校验（只读 SELECT）
- 结果限 20 行，防止刷屏

### 🔌 标准 MCP 协议
- 基于 Spring AI MCP Server WebMVC
- SSE (Server-Sent Events) 传输
- 兼容 Claude Desktop / Dify / Cursor 等 MCP 客户端
- Actuator 健康检查

## 🚀 快速开始

### 前置条件
- **Java 17+**
- **Maven 3.8+**
- **MySQL**（如需使用数据库 MCP）
- **DeepSeek API Key**（自然语言转 SQL）

### 1️⃣ 配置

```bash
# DeepSeek API Key
export DEEPSEEK_API_KEY=sk-your-deepseek-api-key

# application.yml 中修改 MySQL 连接
spring.datasource.url=jdbc:mysql://你的数据库地址:3306/你的库名
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

### 2️⃣ 编译运行

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-agent-link-1.0.0.jar
```

### 3️⃣ 验证服务

```bash
# 健康检查
curl http://localhost:8081/actuator/health

# MCP 端点
curl http://localhost:8081/mcp
```

## 📡 MCP 工具文档

### 🌤️ WeatherMcpService

| 工具 | 描述 | 参数 |
|------|------|------|
| `getCurrentWeather` | 获取指定城市实时天气 | city: String |
| `getWeatherForecast` | 获取未来3天天气预报 | city: String |

**示例：**
```
AI: "北京今天天气怎么样？"
→ getCurrentWeather("北京")
→ "北京 当前天气: ☀️ +22°C 湿度:45% 风速:10km/h"
```

### 🗄️ DatabaseMcpService

| 工具 | 描述 | 参数 |
|------|------|------|
| `listTables` | 列出数据库中所有表 | 无 |
| `getTableSchema` | 查看指定表的字段结构 | tableName: String |
| `queryByNaturalLanguage` | 用自然语言查询数据库 | question: String |

**示例：**
```
AI: "上个月销售额最高的产品有哪些？"
→ queryByNaturalLanguage("上个月销售额最高的产品有哪些？")
→ "SQL: SELECT p.name, SUM(o.amount) as total FROM ..."
   "1. name=iPhone 15, total=¥128000, ..."
   "2. name=MacBook Air, total=¥96000, ..."
```

## 🔗 集成方式

### 集成到 Dify
1. Dify → 工具 → MCP 工具源 → 添加 SSE 地址
2. 输入 `http://localhost:8081/mcp`
3. 在 Workflow 中直接拖拽使用

### 集成到 spring-ai-rag-demo
通过共享 JVM 或 REST 桥接，将 MCP 工具注入到现有 Bot 的 SearchTools 中。

## 🧱 技术栈

| 组件 | 选型 |
|------|------|
| 框架 | Spring Boot 3.2.5 |
| MCP 协议 | Spring AI 1.0.0-M5 (WebMVC/SSE) |
| 天气 | wttr.in（免费，无需 Key） |
| 数据库 | MySQL + JDBC |
| NL2SQL | DeepSeek Chat (LangChain4j 0.35.0) |
| 健康检查 | Spring Boot Actuator |
| 构建 | Maven |

## 🗺️ 演进路线

- [x] v1.0 - 项目脚手架 + Spring AI MCP 协议集成
- [x] v1.1 - 天气 MCP（实时天气 + 预报）
- [x] v1.2 - 数据库 MCP（NL2SQL + 安全校验）
- [ ] v1.3 - 搜索引擎 MCP
- [ ] v1.4 - Dify 集成指南 + 示例
- [ ] v1.5 - 对接 spring-ai-rag-demo Telegram Bot

## 📄 License

MIT
