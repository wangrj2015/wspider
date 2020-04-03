package com.rjay.wspider.service.spider.pipeline;

import com.rjay.wspider.service.spider.ResultItems;

public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems resultItems
     */
    void process(ResultItems resultItems);
}
