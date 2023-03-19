package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.example.demo.asyndownload.TestExportExcel;
import com.example.demo.config.JDYConfiguration;
import com.example.demo.service.TestService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/test")
@Slf4j
public class TesController {


    @Autowired
    TestService testService;

    @Autowired
    JDYConfiguration jdyConfiguration;

    @Value("${jdy.config.headquartersFeedbackAppId}")
    private String headquartersFeedbackAppId1;

    @GetMapping("test")
    public String test(){
        String headquartersFeedbackAppId = jdyConfiguration.getHeadquartersFeedbackAppId();
        log.info(headquartersFeedbackAppId1);
        return headquartersFeedbackAppId;
    }

    @GetMapping("test1")
    public void tes1t(){
        testService.test();
    }

    @GetMapping("test2")
    public String tes131t(@RequestParam("info") Long info){
        return testService.hello(info);
    }

    @GetMapping("test3")
    public String tes13t(@RequestParam("info") String info){
        return testService.helloAnother(info);
    }

    @PostMapping("test2")
    public ParamDTO test2(@RequestParam("info") String info){
        System.out.println(info);
        //分页查询逻辑处理
        return ParamDTO.builder().build();
    }

    @PostMapping("test3")
    public List<Map> test(@RequestBody ParamDTO dto){
        TestExportExcel testExportExcel = TestExportExcel.builder().partnerName("test1").partnerMobile("110").meetingDate(new Date())
                .managerName("sdfasd").area("南昌").ticketResult("sdfasdfs").build();
        TestExportExcel testExportExcel1 = TestExportExcel.builder().partnerName("test2").partnerMobile("112").meetingDate(new Date())
                .managerName("sdfasd").area("南昌").ticketResult("sdfasdfs").build();
        TestExportExcel testExportExcel2 = TestExportExcel.builder().partnerName("test3").partnerMobile("119").meetingDate(new Date())
                .managerName("sdfasd").area("南昌").ticketResult("sdfasdfs").build();

        List<TestExportExcel> fin = Lists.newArrayList(testExportExcel,testExportExcel1,testExportExcel2);

        List<Map> mapFin = new ArrayList<>();
        fin.forEach(e->{
            Map map = JSON.parseObject(JSONObject.toJSONString(e), Map.class);
            mapFin.add(map);
        });
        return mapFin;
    }

}
