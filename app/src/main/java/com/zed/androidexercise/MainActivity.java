package com.zed.androidexercise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView responseText;

    private String address = "http://10.0.2.2/get_url.json";
    private String id = "1000";

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
    // TODO:xml解析
    private String parse(String id, String responseData) {
        if (address.endsWith(".json")) {
            return parseJSONWithGSON(id, responseData);
        }
        return "error";
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
        return "not find";
    }

    // 打开浏览器访问指定网址
    private void redirect(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
