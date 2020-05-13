package com.alibaba.tailbase.clientprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProcessData.class.getName());

    @RequestMapping("/getWrongTrace")
    public String getWrongTrace(@RequestParam String traceIdList, @RequestParam Integer batchPos) {
        String json = ClientProcessData.getWrongTracing(traceIdList, batchPos);
        LOGGER.info("suc to getWrongTrace, batchPos:" + batchPos);
        return json;
    }
}
