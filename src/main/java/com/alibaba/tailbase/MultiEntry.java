package com.alibaba.tailbase;

import com.alibaba.tailbase.backendprocess.BackendController;
import com.alibaba.tailbase.backendprocess.BackendProcessData;
import com.alibaba.tailbase.clientprocess.ClientProcessData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.alibaba.tailbase")
public class MultiEntry {
    public static void main(String[] args) {
        if (Utils.isBackendProcess()) {
            BackendProcessData.init();
            BackendProcessData.start();
        }
        if (Utils.isClientProcess()) {
            ClientProcessData.init();
        }
        SpringApplication.run(MultiEntry.class, args);
    }
}
