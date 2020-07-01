package com.alibaba.tailbase.clientprocess;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {
    @RequestMapping("/getWrongTrace")
    public String getWrongTrace(@RequestParam String traceIdList, @RequestParam Integer batchPos) {
        return ClientProcessData.getWrongTrace(traceIdList, batchPos);
    }
}
