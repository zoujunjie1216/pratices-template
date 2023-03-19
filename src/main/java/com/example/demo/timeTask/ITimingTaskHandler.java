package com.example.demo.timeTask;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@JobHandler(value = "iTimingTaskHandler")
@Component
@Slf4j
public class ITimingTaskHandler extends IJobHandler {

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("hello job");
        return ReturnT.SUCCESS;
    }
}
