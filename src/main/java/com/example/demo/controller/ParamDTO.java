package com.example.demo.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamDTO implements Serializable {

    private String appId;
    /**
     * 用户id
     */
    private String userId;

    private String queryParam;
}
