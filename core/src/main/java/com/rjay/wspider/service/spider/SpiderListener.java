package com.rjay.wspider.service.spider;

public interface SpiderListener {

    void onSuccess(Request request);

    void onError(Request request);

}
