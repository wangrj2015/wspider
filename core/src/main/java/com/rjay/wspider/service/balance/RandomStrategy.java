package com.rjay.wspider.service.balance;

import com.rjay.wspider.service.worker.Worker;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

/**
 * 随机策略
 */
public class RandomStrategy implements WorkerSelectStrategy {

    private Random random = new Random();

    @Override
    public Worker selectWorker(Map<String,Worker> workers) {
        Collection<Worker> workerList = workers.values();
        return workerList.toArray(new Worker[]{})[random.nextInt(workerList.size())];
    }
}
