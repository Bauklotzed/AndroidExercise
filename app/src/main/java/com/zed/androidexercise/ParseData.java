package com.zed.androidexercise;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.List;

public class ParseData {

    // 解析json，根据id返回相应网址
    // TODO:未找到对应网址时处理
    public static String parseJSONWithGSON(String id, String jsonData) {
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
    public static String parseXMLWithPull(String id, String xmlData) {
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

}
