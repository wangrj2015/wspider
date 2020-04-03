package com.rjay.wspider.service.spider.proxy;

import com.rjay.wspider.service.spider.Page;

/**
 * Proxy provider. <br>
 *
 * @since 0.7.0
 */
public interface ProxyProvider {

    /**
     *
     * Return proxy to Provider when complete a download.
     * @param proxy the proxy config contains host,port and identify info
     * @param page the download result
     */
    void returnProxy(Proxy proxy, Page page);

    /**
     * Get a proxy for task by some strategy.
     * @return proxy
     */
    Proxy getProxy();

}
