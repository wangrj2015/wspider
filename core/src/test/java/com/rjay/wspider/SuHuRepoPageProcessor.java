package com.rjay.wspider;

import com.rjay.wspider.service.spider.Page;
import com.rjay.wspider.service.spider.Request;
import com.rjay.wspider.service.spider.Spider;
import com.rjay.wspider.service.spider.processor.PageProcessor;


public class SuHuRepoPageProcessor implements PageProcessor {

    public void process(Page page) {
        page.putField("title", page.getHtml().xpath("//*[@id='article-container']/div[2]/div[1]/div[1]/div[1]/h1").toString());
        page.putField("article", page.getHtml().xpath("//*[@id='mp-editor']").toString());

    }

    public static void main(String[] args) {
        Request request = new Request();
        request.setUrl("https://www.sohu.com/a/333053286_260616?spm=smpc.home.top-news2.1.15655762282334950000&_f=index_news_0");
        request.setRetryTimes(3);
        Spider.create(new SuHuRepoPageProcessor()).request(request).run();
    }

}
