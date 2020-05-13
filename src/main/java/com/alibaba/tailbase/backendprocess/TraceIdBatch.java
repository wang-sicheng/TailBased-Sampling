package com.alibaba.tailbase.backendprocess;

import com.alibaba.tailbase.Constants;

import java.util.ArrayList;
import java.util.List;

public class TraceIdBatch {
    private int batchPos = 0;
    private int processCount = 0;
    private List<String> traceIdList = new ArrayList<>(Constants.BATCH_SIZE / 10);

    public int getBatchPos() {
        return batchPos;
    }

    public void setBatchPos(int batchPos) {
        this.batchPos = batchPos;
    }

    public int getProcessCount() {
        return processCount;
    }

    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    public List<String> getTraceIdList() {
        return traceIdList;
    }

}
