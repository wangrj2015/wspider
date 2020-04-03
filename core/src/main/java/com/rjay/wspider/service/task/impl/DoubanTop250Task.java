package com.rjay.wspider.service.task.impl;

import com.rjay.wspider.service.spider.Request;
import com.rjay.wspider.service.spider.Spider;
import com.rjay.wspider.service.spider.selector.Selectable;
import com.rjay.wspider.service.task.TaskFactory;
import com.rjay.wspider.util.SpiderLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 豆瓣电影top250
 */
public class DoubanTop250Task extends AbstractTask {

    private Logger logger = LoggerFactory.getLogger(DoubanTop250Task.class);

    private static final String URL = "https://movie.douban.com/top250?start=%s";

    private static final int TIMEOUT = 5000;

    private static final int RETRY_TIMES = 3;

    @Override
    public String getType() {
        return TaskFactory.TASK_DOUBAN_TOP_250;
    }

    @Override
    public void execute() {
        String url = String.format(URL,params.get("start"));
        logger.info("DoubanTop250Task start:{}",url);
        Request request = Request.build(url).setSocketTimeOut(TIMEOUT).setRetryTimes(RETRY_TIMES);
        Spider.create((page) -> {
                          List<Selectable> nodes = page.getHtml().xpath("//div[@class='article']//li").nodes();
                          nodes.stream().forEach((node) -> {
                              String index = node.xpath("//div[@class='pic']/em/text(0)").toString();
                              String title = node.xpath("//div[@class='info']/div[@class='hd']/a/span[@class='title']/text(0)").toString();
                              String rating = node.xpath("//div[@class='info']/div[@class='bd']//span[@class='rating_num']/text(0)").toString();
                              page.putField(index, title + ":" + rating);
                          });
                      }
            ).addPipeline((resultItems) -> {
            resultItems.getAll().entrySet().stream().forEach((entry)->{
                SpiderLogUtil.log(getType(), params, entry.toString());
            });
        }).request(request).run();
    }

}
