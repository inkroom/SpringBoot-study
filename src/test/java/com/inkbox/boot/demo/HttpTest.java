package com.inkbox.boot.demo;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class HttpTest {
    public static void main(String[] args) {
        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet("https://api.github.com/repos/inkroom/image/contents/1600X800");
            HttpEntity entity = client.execute(get).getEntity();
            String s = EntityUtils.toString(entity);
            JSONArray array = new JSONArray(s);

            for (int i = 0; i < array.length(); i++) {

                JSONObject item = array.getJSONObject(i);
                new Download(item.getString("download_url"), item.getString("name")).start();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void download() throws Exception {


    }
}

class Download extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String url;

    private String dir = "E:/image/inkbox/";

    private String name;

    public Download(String url, String name) {
        this.url = url;
        this.name = name;
    }

    @Override
    public void run() {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);

        HttpEntity entity = null;
        try {
            entity = client.execute(get).getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(dir, name);
        if (entity == null) return;
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            InputStream inputStream = entity.getContent();
            byte[] bytes = new byte[1024 * 10];
            int count = -1;
            while ((count = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, count);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            logger.error("{}下载失败", name);
        }


    }
}