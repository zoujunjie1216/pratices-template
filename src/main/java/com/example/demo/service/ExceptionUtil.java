package com.example.demo.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public final class ExceptionUtil {
    public static void handleException(BlockException e){
        System.out.println("异常兜底,e:{}"+e);
    }
}
