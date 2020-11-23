package com.jeshy.djkeyren;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTS {


    public static String YTSearch(String searchQ) {
        try {
            Document doc = Jsoup.connect("https://www.youtube.com/results?search_query=" + searchQ).get();
            Elements elements = doc.select("script");
            Pattern p = Pattern.compile("\"url\":\"/watch\\?v=(.+?)\"");
            Matcher m = p.matcher(elements.toString());
            m.find();
            return ("https://www.youtube.com" + m.group().substring(7).replace("\"",""));
        }
        catch (IOException ex){ ex.printStackTrace();}
        return null;
    }

    public static boolean isLink(String isUrl) {
        Pattern p = Pattern.compile("https://www\\.youtube.com/watch\\?v=(.+?)");
        Matcher m = p.matcher(isUrl);
        return m.find();
    }
}
