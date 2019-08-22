package com.zed.androidexercise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView responseText;

    private String address = "http://10.0.2.2/get_url.xml";
    private String id = "1002";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendRequest = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_request) {

            // 发送请求，接收返回数据进行处理，然后打开浏览器访问网页
            HttpUtil.sendHttpRequest(address, new okhttp3.Callback() {

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    String url = parse(id, responseData);
                    redirect(url);
                }

                @Override
                public void onFailure(Call call, IOException e) {

                }
            });

        }
    }

    // 判断是json还是xml格式，并进行相应的处理
    // TODO:文件不为xml或json时的处理
    private String parse(String id, String responseData) {
        if (address.endsWith(".json")) {
            return parseJSONWithGSON(id, responseData);
        } else if (address.endsWith("xml")) {
            return parseXMLWithPull(id, responseData);
        }
        return "error:wrong format";
    }

    // 解析json，根据id返回相应网址
    // TODO:未找到对应网址时处理
    private String parseJSONWithGSON(String id, String jsonData) {
        Gson gson = new Gson();
        List<Website> websites = gson.fromJson(jsonData, new TypeToken<List<Website>>(){}.getType());
        for (Website website : websites) {
            if (id.equals(website.getId())) {
                return website.getUrl();
            }
        }
        return "error:not found";
    }

    // 解析xml，根据id返回相应网址
    // TODO:未找到对应网址时处理与异常处理
    private String parseXMLWithPull(String id, String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String websiteId = "";
            String url = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("id".equals(nodeName)) {
                            websiteId = xmlPullParser.nextText();
                        } else if ("url".equals(nodeName)) {
                            url = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("website".equals(nodeName) && websiteId.equals(id)) {
                            return url;
                        }
                        break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
        // 未找到对应网址
        return "error:not found";
    }

    // 打开浏览器访问指定网址
    private void redirect(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
