package base;

import java.io.StringWriter;
import java.net.URI;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
//import javax.xml.bind.JAXBContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import struct.Config;

public class ApacheBrowser {

    public String cookie = null;
    public String get_last_url = null;

    public String get(String url) {
        this.get_last_url = url;

        UnsafeSSLHelper unsafeSSLHelper = new UnsafeSSLHelper();
        org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClientBuilder.create().setSslcontext(unsafeSSLHelper.createUnsecureSSLContext()).setHostnameVerifier(unsafeSSLHelper.getPassiveX509HostnameVerifier()).build();
        HttpClientContext context = HttpClientContext.create();
        String result = "";
        CloseableHttpResponse response = null;
        try {
            HttpGet request = new HttpGet(url);
            request.addHeader(HttpHeaders.USER_AGENT, Config.uagent);
            if (cookie != null) {
                request.addHeader("Cookie", cookie);
            }
            response = httpClient.execute(request, context);

            List<URI> redirectLocations = context.getRedirectLocations();
            //redirectLocations.get(0);
            if (redirectLocations != null) {
                if (!redirectLocations.isEmpty()) {
                    URI location = URIUtils.resolve(request.getURI(), redirectLocations.get(redirectLocations.size() - 1));
                    this.get_last_url = location.toString();
                }
            }

            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }

            } finally {
                response.close();
            }
        } catch (Exception e) {
        } finally {
            try {
                httpClient.close();

            } catch (Exception e1) {
            }

        }
        return result;
    }

    public String post(String url, HashMap<String, String> post) {

        UnsafeSSLHelper unsafeSSLHelper = new UnsafeSSLHelper();

        String result = "";
        try {
            org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClientBuilder.create().setSslcontext(unsafeSSLHelper.createUnsecureSSLContext()).setHostnameVerifier(unsafeSSLHelper.getPassiveX509HostnameVerifier()).build();
            CloseableHttpResponse response = null;
            HttpClientContext context = HttpClientContext.create();

            StringWriter writer = new StringWriter();

            HttpPost request = new HttpPost(url);

            request.addHeader(HttpHeaders.USER_AGENT, Config.uagent);
            if (cookie != null) {
                request.addHeader("Cookie", cookie);
            }

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : post.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            request.setEntity(new UrlEncodedFormEntity(params));

            response = httpClient.execute(request, context);

            List<URI> redirectLocations = context.getRedirectLocations();
            if (redirectLocations != null) {
                if (!redirectLocations.isEmpty()) {
                    URI location = URIUtils.resolve(request.getURI(), redirectLocations.get(redirectLocations.size() - 1));
                    this.get_last_url = location.toString();
                }
            }
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }

            } finally {
                response.close();
            }

        } catch (Exception e) {
        } finally {

        }

        return result;
    }
}
