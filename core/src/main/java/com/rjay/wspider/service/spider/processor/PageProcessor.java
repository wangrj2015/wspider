package com.rjay.wspider.service.spider.processor;

import com.rjay.wspider.service.spider.Page;

public interface PageProcessor {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page page
     */
    void process(Page page);

}
