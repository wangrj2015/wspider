package com.rjay.wspider.service.balance;

import com.rjay.wspider.service.worker.Worker;

import java.util.Map;

/**
 * worker选择策略
 */
public interface WorkerSelectStrategy {

    Worker selectWorker(Map<String,Worker> workers);

}
