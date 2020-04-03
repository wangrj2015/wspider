package com.rjay.wspider.service.spider;

import com.rjay.wspider.util.UrlUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Request implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    private String domain;

    private String url;

    private String userAgent;

    private String method;

    private boolean useGzip = true;

    private int retryTimes = 0;

    private int socketTimeOut = 5000;

    private int connectTimeout = 3000;

    private int connectionRequestTimeout = 500;

    private HttpRequestBody requestBody;

    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras;

    private Map<String, String> cookies = new HashMap<String, String>();

    private Map<String, String> headers = new HashMap<String, String>();

    private boolean disableCookieManagement = false;

    /**
     * When it is set to TRUE, the downloader will not try to parse response body to text.
     *
     */
    private boolean binaryContent = false;

    private String charset;

    public Request() {
    }

    public Request(String url) {
        this.url = url;
        this.domain = UrlUtils.getDomain(url);
    }

    public static Request build(String url){
        return new Request(url);
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Request putExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public Request setExtras(Map<String, Object> extras) {
        this.extras = extras;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Request setUrl(String url) {
        this.url = url;
        this.domain = UrlUtils.getDomain(url);
        return this;
    }

    /**
     * The http method of the request. Get for default.
     * @return httpMethod
     * @since 0.5.0
     */
    public String getMethod() {
        return method;
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public Request setMethod(String method) {
        this.method = method;
        return this;
    }

    public boolean isDisableCookieManagement() {
        return disableCookieManagement;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (url != null ? !url.equals(request.url) : request.url != null) return false;
        return method != null ? method.equals(request.method) : request.method == null;
    }

    public Request addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    public Request addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpRequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(HttpRequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public boolean isBinaryContent() {
        return binaryContent;
    }

    public Request setBinaryContent(boolean binaryContent) {
        this.binaryContent = binaryContent;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public Request setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public Request setUseGzip(boolean useGzip) {
        this.userAgent = userAgent;
        return this;
    }

    public Request setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public Request setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
        return this;
    }

    public Request setConnectTimeout(int connectTimeout){
        this.connectTimeout = connectTimeout;
        return this;
    }

    public Request setConnectionRequestTimeout(int connectionRequestTimeout){
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }


    public Request setDisableCookieManagement(boolean disableCookieManagement) {
        this.disableCookieManagement = disableCookieManagement;
        return this;
    }

    @Override
    public String toString() {
        return "Request{" + "domain='" + domain + '\'' + ", url='" + url + '\'' + ", userAgent='" + userAgent + '\''
               + ", method='" + method + '\'' + ", useGzip=" + useGzip + ", retryTimes=" + retryTimes
               + ", socketTimeOut=" + socketTimeOut + ", connectTimeout=" + connectTimeout + ", requestBody="
               + requestBody + ", extras=" + extras + ", cookies=" + cookies + ", headers=" + headers
               + ", disableCookieManagement=" + disableCookieManagement + ", charset='" + charset + '\'' + '}';
    }
}
