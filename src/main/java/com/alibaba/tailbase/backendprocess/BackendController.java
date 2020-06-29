package com.alibaba.tailbase.backendprocess;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController {
    @RequestMapping("/setWrongTraceId")
    public String setWrongTraceId(@RequestParam String traceIdListJson, @RequestParam int batchPos) {
        return BackendProcessData.setWrongTraceId(traceIdListJson, batchPos);
    }

    @RequestMapping("/finish")
    public String finish() {
        return BackendProcessData.finish();
    }
}
