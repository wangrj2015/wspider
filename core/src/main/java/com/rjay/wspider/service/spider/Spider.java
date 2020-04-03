package com.rjay.wspider.service.spider;

import com.rjay.wspider.service.spider.downloader.Downloader;
import com.rjay.wspider.service.spider.downloader.HttpClientDownloader;
import com.rjay.wspider.service.spider.pipeline.ConsolePipeline;
import com.rjay.wspider.service.spider.pipeline.Pipeline;
import com.rjay.wspider.service.spider.processor.PageProcessor;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 爬虫
 */
public class Spider {

    private Logger logger = LoggerFactory.getLogger(Spider.class);

    protected Downloader downloader;

    protected PageProcessor pageProcessor;

    protected List<Pipeline> pipelines = Lists.newArrayList();

    protected List<SpiderListener> listeners = Lists.newArrayList();

    protected Request request;

    public static Spider create(PageProcessor pageProcessor) {
        return new Spider(pageProcessor);
    }

    public Spider(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    public Spider addUrl(String... urls) {
        for (String url : urls) {
            request = new Request(url);
        }
        return this;
    }

    public Spider request(String url){
        this.request = new Request(url);
        return this;
    }

    public Spider request(Request request){
        this.request = request;
        return this;
    }

    public Spider setDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public Spider setPageProcessor(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
        return this;
    }

    public Spider addPipeline(Pipeline pipeline) {
        this.pipelines.add(pipeline);
        return this;
    }

    public Spider registerListener(SpiderListener listener){
        this.listeners.add(listener);
        return this;
    }

    public void init(){
        if (downloader == null) {
            this.downloader = new HttpClientDownloader();
        }
        if (pipelines.isEmpty()) {
            pipelines.add(new ConsolePipeline());
        }
    }

    public void run(){
        init();
        logger.info("Spider start , request:{}", request);
        try{
            Page page = downloader.download(request);
            if (page.isDownloadSuccess()){
                onDownloadSuccess(page);
                logger.info("Spider success , request:{}", request);
            } else {
                logger.error("Spider failed , request:{}", request);
                onDownloaderFail();
            }
        }catch (Exception e){
            logger.error("Spider error , request:{}", request, e);
            onDownloaderFail();
        }
    }

    private void onDownloadSuccess(Page page) {
        //解析页面
        pageProcessor.process(page);
        //TODO add request
        //数据持久化
        if (!page.getResultItems().isSkip()) {
            for (Pipeline pipeline : pipelines) {
                pipeline.process(page.getResultItems());
            }
        }
        //触发成功监听器
        if(CollectionUtils.isNotEmpty(listeners)){
            for(SpiderListener listener : listeners){
                listener.onSuccess(request);
            }
        }
    }

    private void onDownloaderFail() {
        //重试
        for(int retry = 1; retry <= request.getRetryTimes(); retry++){
            //等待1s再重试
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                //do nothing
            }
            logger.info("Spider retry [{}] start , request:{}", retry, request);
            try{
                Page page = downloader.download(request);
                if (page.isDownloadSuccess()){
                    onDownloadSuccess(page);
                    logger.info("Spider retry [{}] success , request:{}", retry, request);
                    return;
                } else {
                    logger.error("Spider retry [{}] failed , request:{}", retry, request);
                }
            }catch (Exception e){
                logger.error("Spider retry [{}] error , request:{}", retry, request, e);
            }
        }
        //触发失败监听器
        if(CollectionUtils.isNotEmpty(listeners)){
            for(SpiderListener listener : listeners){
                listener.onError(request);
            }
        }
    }
}
