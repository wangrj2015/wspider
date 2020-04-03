package com.rjay.wspider.service.spider.downloader;

import com.rjay.wspider.service.spider.Page;
import com.rjay.wspider.service.spider.Request;
import com.rjay.wspider.service.spider.proxy.Proxy;
import com.rjay.wspider.service.spider.proxy.ProxyProvider;
import com.rjay.wspider.service.spider.proxy.SimpleProxyProvider;
import com.rjay.wspider.service.spider.selector.PlainText;
import com.rjay.wspider.util.CharsetUtils;
import com.rjay.wspider.util.HttpHeaderUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * The http downloader based on HttpClient.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class HttpClientDownloader implements Downloader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final static Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    public final static HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    public final static HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();

    private static ProxyProvider proxyProvider;

    private boolean responseHeader = true;

    static {
        //TODO
        proxyProvider = new SimpleProxyProvider(Lists.emptyList());
    }

    private CloseableHttpClient getHttpClient(Request request) {
        if (request == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = request.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(request);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Page download(Request request) {
        if (request == null) {
            throw new NullPointerException("request can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(request);
        Proxy proxy = proxyProvider.getProxy();
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, proxy);
        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            page = handleResponse(request, httpResponse);
            onSuccess(request);
            logger.info("downloading page {} success", request.getUrl());
            return page;
        } catch (IOException e) {
            logger.warn("download page {} error,{}", request.getUrl(), e.getMessage());
            onError(request);
            return page;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            if (proxyProvider != null && proxy != null) {
                proxyProvider.returnProxy(proxy, page);
            }
        }
    }

    protected Page handleResponse(Request request, HttpResponse httpResponse) throws IOException {
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        Page page = new Page();
        page.setBytes(bytes);
        String charset = request.getCharset();
        if (!request.isBinaryContent()){
            if (charset == null) {
                charset = getHtmlCharset(contentType, bytes);
            }
            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        if (responseHeader) {
            page.setHeaders(HttpHeaderUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return page;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Charset.defaultCharset().name();
            logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }
        return charset;
    }

    private void onSuccess(Request request) {

    }

    private void onError(Request request) {

    }

}

