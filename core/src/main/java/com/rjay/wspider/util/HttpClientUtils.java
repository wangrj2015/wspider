package com.rjay.wspider.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * http工具类
 */
public class HttpClientUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static final int DEF_TIME_OUT = 3000;

    private static final int CONNECT_TIMEOUT = 500;

    private static final int CONNECT_REQUEST_TIMEOUT = 500;

    private static final int MAX_CONN = 100;

    private static final int MAX_PER_ROUTE = 20;

    private static CloseableHttpClient httpClient;

    static {
        PoolingHttpClientConnectionManager pm = new PoolingHttpClientConnectionManager();
        pm.setMaxTotal(MAX_CONN);
        pm.setDefaultMaxPerRoute(MAX_PER_ROUTE);

        //socketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(DEF_TIME_OUT).build();
        pm.setDefaultSocketConfig(socketConfig);

        //requestConfig
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(DEF_TIME_OUT).build();
        httpClient = HttpClients.custom().setConnectionManager(pm).setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * Post By JSON
     *
     * @param url
     * @param params
     * @return
     */
    public static String sendPostWithJSON(String url, String params) {

        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;
        String result = null;

        try {
            httpPost.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));

            httpResponse = httpClient.execute(httpPost);

            result = EntityUtils.toString(httpResponse.getEntity());
        } catch (ConnectTimeoutException e) {
            logger.warn("sendPostWithJSON ConnectTimeoutException", e);
            httpPost.abort();
        } catch (SocketTimeoutException e) {
            logger.warn("sendPostWithJSON SocketTimeoutException", e);
            httpPost.abort();
        } catch (Exception e) {
            logger.warn("sendPostWithJSON error", e);
            httpPost.abort();
        } finally {
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return result;
    }

    /**
     * Post By Pair
     *
     * @param url
     * @param params
     * @return
     */
    public static String sendPostByPair(String url, List<NameValuePair> params) {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;
        String result = null;

        try {

            if (params != null && !params.isEmpty()) {
                httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            }

            httpResponse = httpClient.execute(httpPost);

            result = EntityUtils.toString(httpResponse.getEntity());
        } catch (ConnectTimeoutException e) {
            logger.warn("sendByPair ConnectTimeoutException", e);
            httpPost.abort();
        } catch (SocketTimeoutException e) {
            logger.warn("sendByPair SocketTimeoutException", e);
            httpPost.abort();
        } catch (Exception e) {
            logger.warn("sendByPair error", e);
            httpPost.abort();
        } finally {
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return result;
    }

}
