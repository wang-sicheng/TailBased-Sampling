package com.alibaba.tailbase;

import com.alibaba.tailbase.clientprocess.ClientProcessData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CommonController {

  private static Integer DATA_SOURCE_PORT = 0;

  public static Integer getDataSourcePort() {
    return DATA_SOURCE_PORT;
  }

  @RequestMapping("/ready")
  public String ready() {
    return "suc";
  }

  @RequestMapping("/setParameter")
  public String setParamter(@RequestParam Integer port) {
    DATA_SOURCE_PORT = port;
    if (Utils.isClientProcess()) {
      ClientProcessData.start();
    }
    return "suc";
  }

  @RequestMapping("/start")
  public String start() {
    return "suc";
  }



}
