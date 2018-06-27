package com.rminfo.httpclient;

import com.rminfo.shiro.ShiroConfig;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @项目名称：wyait-manage
 * @包名：com.wyait.manage.config
 * @类描述：
 * @创建人：wyait
 * @创建时间：2018-01-11 9:13
 * @version：V1.0
 */
@Configuration
public class HttpClientConfig {
    private static final Logger logger = LoggerFactory
            .getLogger(ShiroConfig.class);
    /**
     * 连接池最大连接数
     */
    @Value("${httpclient.config.connMaxTotal}")
    private int connMaxTotal = 20;

    /**
     *
     */
    @Value("${httpclient.config.maxPerRoute}")
    private int maxPerRoute = 20;

    /**
     * 连接存活时间，单位为s
     */
    @Value("${httpclient.config.timeToLive}")
    private int timeToLive = 10;

    /**
     * 配置连接池
     *
     * @return
     */
    @Bean(name = "poolingClientConnectionManager")
    public PoolingHttpClientConnectionManager poolingClientConnectionManager() {
        PoolingHttpClientConnectionManager poolHttpcConnManager = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
        // 最大连接数
        poolHttpcConnManager.setMaxTotal(this.connMaxTotal);
        // 路由基数
        poolHttpcConnManager.setDefaultMaxPerRoute(this.maxPerRoute);
        return poolHttpcConnManager;
    }

    @Value("${httpclient.config.connectTimeout}")
    private int connectTimeout = 3000;

    @Value("${httpclient.config.connectRequestTimeout}")
    private int connectRequestTimeout = 2000;

    @Value("${httpclient.config.socketTimeout}")
    private int socketTimeout = 3000;

    /**
     * 设置请求配置
     *
     * @return
     */
    @Bean
    public RequestConfig config() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(this.connectRequestTimeout)
                .setConnectTimeout(this.connectTimeout)
                .setSocketTimeout(this.socketTimeout)
                .build();
    }

    @Value("${httpclient.config.retryTime}")// 此处建议采用@ConfigurationProperties(prefix="httpclient.config")方式，方便复用
    private int retryTime;

    /**
     * 重试策略
     *
     * @return
     */
    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
        // 请求重试
        final int retryTime = this.retryTime;
        return new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                // Do not retry if over max retry count,如果重试次数超过了retryTime,则不再重试请求
                if (executionCount >= retryTime) {
                    return false;
                }
                // 服务端断掉客户端的连接异常
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                // time out 超时重试
                if (exception instanceof InterruptedIOException) {
                    return true;
                }
                // Unknown host
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                // Connection refused
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                // SSL handshake exception
                if (exception instanceof SSLException) {
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 创建httpClientBuilder对象
     *
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("poolingClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {

        return HttpClients.custom().setConnectionManager(httpClientConnectionManager)
                .setRetryHandler(this.httpRequestRetryHandler())
                //.setKeepAliveStrategy(connectionKeepAliveStrategy())
                //.setRoutePlanner(defaultProxyRoutePlanner())
                .setDefaultRequestConfig(this.config());

    }

    /**
     * 自动释放连接
     *
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }
}