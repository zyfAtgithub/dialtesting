package com.yf.dialtesting.model;

import java.util.List;

/**
 * 拨测信息封装
 */
public class DialTestingInfo {

    /** 拨测url */
    private String url;

    /** 拨测数 */
    private int dialCount;

    /** 每次访问间隔 */
    private int interval;

    /** 并发数 */
    private int concurrentNum;

    /** 连接超时 */
    private int conTimeout;

    /** 数据传输超时 */
    private int soTimeout;

    /** 是否启动代理 */
    private boolean proxyEnabled;

    /** 代理IP列表，英文逗号分割 */
    private List<String> proxyList;

    public DialTestingInfo() {}

    public DialTestingInfo(String url, int dialCount, int interval, int concurrentNum, int conTimeout, int soTimeout, boolean proxyEnabled, List<String> proxyList) {
        this.url = url;
        this.dialCount = dialCount;
        this.interval = interval;
        this.concurrentNum = concurrentNum;
        this.conTimeout = conTimeout;
        this.soTimeout = soTimeout;
        this.proxyEnabled = proxyEnabled;
        this.proxyList = proxyList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDialCount() {
        return dialCount;
    }

    public void setDialCount(int dialCount) {
        this.dialCount = dialCount;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getConcurrentNum() {
        return concurrentNum;
    }

    public void setConcurrentNum(int concurrentNum) {
        this.concurrentNum = concurrentNum;
    }

    public int getConTimeout() {
        return conTimeout;
    }

    public void setConTimeout(int conTimeout) {
        this.conTimeout = conTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public List<String> getProxyList() {
        return proxyList;
    }

    public void setProxyList(List<String> proxyList) {
        this.proxyList = proxyList;
    }
}
