package com.example.demo.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ganmao
 * @date 2021/5/24 11:51
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jdy.config")
public class JDYConfiguration {

    private String headquartersFeedbackAppId;

    private String headquartersFeedbackEntryId;


}
