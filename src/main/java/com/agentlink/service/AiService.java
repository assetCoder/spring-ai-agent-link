package com.agentlink.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * AI 服务 - 封装 DeepSeek 调用
 * 用于自然语言转 SQL 等场景
 */
@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final ChatLanguageModel model;

    public AiService(@Value("${deepseek.api.key}") String apiKey,
                     @Value("${deepseek.api.base-url}") String baseUrl,
                     @Value("${deepseek.api.model}") String modelName) {
        this.model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxRetries(2)
                .timeout(Duration.ofSeconds(30))
                .logRequests(false)
                .logResponses(false)
                .build();
        log.info("AiService initialized with model={}", modelName);
    }

    /**
     * 自然语言转 SQL
     */
    public String naturalLanguageToSql(String question, String tableSchema) {
        String prompt = """
                你是一个SQL专家。根据用户的问题和数据库表结构，生成正确的MySQL查询语句。
                
                表结构：
                %s
                
                用户问题：%s
                
                要求：
                1. 只返回SQL语句，不要任何解释
                2. SQL必须安全（只读查询，没有DELETE/UPDATE/INSERT/DROP）
                3. 使用标准的MySQL语法
                4. 如果问题无法用SQL回答，返回：-- 无法回答: 原因
                """.formatted(tableSchema, question);

        return model.generate(prompt);
    }
}
