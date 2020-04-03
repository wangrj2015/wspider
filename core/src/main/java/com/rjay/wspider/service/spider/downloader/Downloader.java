package com.rjay.wspider.service.spider.downloader;

import com.rjay.wspider.service.spider.Page;
import com.rjay.wspider.service.spider.Request;

public interface Downloader {

    /**
     * Downloads web pages and store in Page object.
     *
     * @param request request
     * @return page
     */
    Page download(Request request);

}
