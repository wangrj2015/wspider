package com.rjay.wspider.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class SpiderLogUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger("spiderDataLogger");

    /**
     * 打印日志
     * @param taskType
     * @param data
     */
    public static void log(String taskType, Map<String,String> paramMap, String data) {
        JSONObject result = new JSONObject();
        result.put("taskType", taskType);
        result.put("parameter", paramMap);
        result.put("data", data);
        result.put("date", DateUtil.nowDateStr());
        LOGGER.info(result.toJSONString());
    }

}
