package com.yf.dialtesting.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLHandshakeException;

import com.yf.dialtesting.util.common.DateUtils;
import com.yf.dialtesting.util.common.StringUtils;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpclientUtil {
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String CHARSET_GBK = "GBK";
    private static final String SSL_DEFAULT_SCHEME = "https";
    private static final int SSL_DEFAULT_PORT = 443;
    private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount >= 3) {
                return false;
            } else if (exception instanceof NoHttpResponseException) {
                return true;
            } else if (exception instanceof SSLHandshakeException) {
                return false;
            } else {
                HttpRequest request = (HttpRequest)context.getAttribute("http.request");
                boolean idempotent = request instanceof HttpEntityEnclosingRequest;
                return !idempotent;
            }
        }
    };
    private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String charset = EntityUtils.getContentCharSet(entity) == null ? "GBK" : EntityUtils.getContentCharSet(entity);
                return new String(EntityUtils.toByteArray(entity), charset);
            } else {
                return null;
            }
        }
    };

    public HttpclientUtil() {
    }

    public static DefaultHttpClient getDefaultHttpClient(String charset) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        httpclient.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        httpclient.getParams().setParameter("http.protocol.expect-continue", Boolean.FALSE);
        httpclient.getParams().setParameter("http.protocol.content-charset", charset == null ? "GBK" : charset);
        httpclient.setHttpRequestRetryHandler(requestRetryHandler);
        return httpclient;
    }

    private static void abortConnection(HttpRequestBase hrb, HttpClient httpclient) {
        if (hrb != null) {
            hrb.abort();
        }

        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
        }

    }

    private static KeyStore createKeyStore(URL url, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("Keystore url may not be null");
        } else {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream is = null;

            try {
                is = url.openStream();
                keystore.load(is, password != null ? password.toCharArray() : null);
            } finally {
                if (is != null) {
                    is.close();
                    is = null;
                }

            }

            return keystore;
        }
    }

    private static List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
        if (paramsMap != null && paramsMap.size() != 0) {
            List<NameValuePair> params = new ArrayList();
            Iterator var2 = paramsMap.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<String, String> map = (Entry)var2.next();
                params.add(new BasicNameValuePair((String)map.getKey(), (String)map.getValue()));
            }

            return params;
        } else {
            return null;
        }
    }

    public static JSONObject get(String url, int contimeout, int sotimeout) {
        return get(url, (HttpHost)null, contimeout, sotimeout);
    }

    public static JSONObject get(String url, HttpHost proxy, int contimeout, int sotimeout) {
        if (url != null && !StringUtils.isEmpty(url)) {
            JSONObject jsonResult = new JSONObject();
            HttpClient httpclient = getDefaultHttpClient(contimeout, sotimeout);
            if (null != proxy) {
                httpclient.getParams().setParameter("http.route.default-proxy", proxy);
            }

            HttpGet hg = new HttpGet(url);
            long begin = System.currentTimeMillis();
            jsonResult.put("begin", DateUtils.date2String(new Date(begin), "HH:mm:ss.SSS"));

            try {
                long end;
                try {
                    HttpResponse response = httpclient.execute(hg);
                    end = System.currentTimeMillis();
                    jsonResult.put("end", DateUtils.date2String(new Date(end), "HH:mm:ss.SSS"));
                    jsonResult.put("statusCode", response.getStatusLine().getStatusCode());
                    jsonResult.put("content", EntityUtils.toString(response.getEntity()));
                    jsonResult.put("cost", end - begin);
                } catch (Exception var15) {
                    end = System.currentTimeMillis();
                    jsonResult.put("end", DateUtils.date2String(new Date(end), "HH:mm:ss.SSS"));
                    jsonResult.put("statusCode", "999");
                    jsonResult.put("cost", end - begin);
                    jsonResult.put("excep", var15.getMessage());
                }
            } finally {
                if (hg != null) {
                    hg.abort();
                }

                if (httpclient != null) {
                    httpclient.getConnectionManager().shutdown();
                }
            }
            return jsonResult;
        } else {
            return null;
        }
    }

    public static HttpClient getDefaultHttpClient(int contimeout, int sotimeout) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        httpclient.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        httpclient.getParams().setParameter("http.protocol.expect-continue", Boolean.FALSE);
        httpclient.getParams().setParameter("http.protocol.content-charset", "GBK");
        httpclient.getParams().setParameter("http.connection.timeout", contimeout);
        httpclient.getParams().setParameter("http.socket.timeout", sotimeout);
//        httpclient.setHttpRequestRetryHandler(requestRetryHandler);
        return httpclient;
    }

    public static void main(String[] args) {
        HttpHost poxy = new HttpHost("58.53.219.5", 80);
        System.out.println(poxy.getHostName());
        JSONObject res = get("http://cdntest.ctdns.net/", poxy, 200, 400);
        System.out.println(res.toString());
    }
}
