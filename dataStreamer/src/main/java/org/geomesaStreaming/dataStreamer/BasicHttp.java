package org.geomesaStreaming.dataStreamer;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class BasicHttp {
    public static String getResponseAsString(String url) {
        String ret = "";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = client.execute(request)) {
                //System.out.println(response.getCode() + " " + response.getReasonPhrase());
                HttpEntity entity = response.getEntity();
                ret = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            catch (ParseException ex) {
                ex.printStackTrace();
            }
            finally {
                client.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return ret;
    }
}
