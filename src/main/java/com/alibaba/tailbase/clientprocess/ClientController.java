package com.alibaba.tailbase.clientprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ClientController {
    @RequestMapping("/getWrongTrace")
    public String getWrongTrace(@RequestParam String traceIdList, @RequestParam Integer batchPos) {
        return ClientProcessData.getWrongTrace(traceIdList, batchPos);
    }
}
