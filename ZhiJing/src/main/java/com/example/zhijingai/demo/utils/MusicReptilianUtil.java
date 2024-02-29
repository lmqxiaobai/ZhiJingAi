package com.example.zhijingai.demo.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@Component
public class MusicReptilianUtil {

    public static String wangYi(String msg) throws IOException {

//        HttpClient httpClient = HttpClients.createDefault();
//
//        String songName = "沉溺";
////        https://dataiqs.com/api/netease/music/?
////        https://dataiqs.com/api/kgmusic/?msg=
//        HttpGet get = new HttpGet("https://dataiqs.com/api/netease/music/?msg=" + songName);
//
//        HttpResponse execute = httpClient.execute(get);
//
//        String string = EntityUtils.toString(execute.getEntity());
//
//        System.out.println(string);

        //String msg = "love yourself"; // Need to search for the song name
        int n = 0; // Index of the download link you want to retrieve
        String type = "song"; // Parsing type
        int countLimit = 10; // Number of items in the list (default is 10)
        int pageLimit = 1; // Page number (default is the first page)
        int offsetLimit = (pageLimit - 1) * countLimit; // Offset number
        String id = ""; // Song ID or playlist ID

        String URL = null;

        switch (type) {
            case "song":
                if (msg.isEmpty()) {
                    //System.out.println("{'code':200,'text':'请输入要解析的歌名'}");
                    return null;
                }
                String neteaseSong = getNeteaseSong(msg, offsetLimit, countLimit, n);
                //System.out.println(neteaseSong);
                int index = neteaseSong.indexOf("id") + 5;
                int indexEnd = neteaseSong.indexOf("page");
                String s = neteaseSong.substring(index, indexEnd - 3);
                System.out.println("歌曲id: " + s);
                URL = "http://music.163.com/song/media/outer/url?id=" + s;
                System.out.println("下载链接: " + URL);
                break;
            case "songid":
                if (!id.isEmpty()) {
                    String songUrl = "http://music.163.com/song/media/outer/url?id=" + id;
                    System.out.println(songUrl);
                    songUrl = getRedirectUrl(songUrl, null);
                    //System.out.println("{'code':200,'text':'解析成功','type':'歌曲解析','now':'" + LocalDateTime.now() + "','song_url':'" + songUrl + "'}");
                    //System.out.println(songUrl);
                } else {
                    System.out.println("{'code':200,'text':'解析失败，请检查歌曲id值是否正确','type':'歌曲解析'}");
                }
                break;
            default:
                System.out.println("{'code':200,'text':'请求参数不存在" + type + "'}");
        }


//        String url = "https://www.kugou.com/yy/html/rank.html";
//
//        Document parse = Jsoup.parse(new URL(url), 10000);
////<div class="pc_temp_songlist  pc_rank_songlist_short">
//        Elements select = parse.select("div.pc_temp_wrap pc_temp_2col_critical > div.pc_temp_main > div.pc_temp_content > div.pc_temp_container > div#rankWrap > div.pc_temp_songlist  pc_rank_songlist_short > ul");
//        for(Element element : select) {
//            System.out.println(element);
//        }
        return URL;

    }

    private static String getNeteaseSong(String msg, int offsetLimit, int countLimit, int n) {
        String string = null;
        try {
            String url = "https://s.music.163.com/search/get/?src=lofter&type=1&filterDj=false&limit=" + countLimit + "&offset=" + offsetLimit + "&s=" + URLEncoder.encode(msg, "UTF-8");
            String jsonStr = getCurl(url);
            JSONObject jsonData = new JSONObject(jsonStr);
            JSONArray songList = jsonData.getJSONObject("result").getJSONArray("songs");
            JSONArray dataList = new JSONArray();

            if (n != -1) {
                JSONObject songInfo = songList.getJSONObject(n);
//                JSONObject id = songInfo.getJSONObject("id");
//                String songId = (String) id.get("id");
//                String songUrl = "http://music.163.com/song/media/outer/url?id=" + songId;
                String id = String.valueOf(songInfo.getInt("id"));
                String songUrl = "http://music.163.com/song/media/outer/url?id=" + id;
                songUrl = getRedirectUrl(songUrl, null);
                JSONObject data = new JSONObject();
                data.put("id", String.valueOf(songInfo.getInt("id")));
                //data.put("id", songId);
                data.put("name", songInfo.getString("name"));
                data.put("singername", songInfo.getJSONArray("artists").getJSONObject(0).getString("name"));
                data.put("page", songInfo.getString("page"));
                data.put("song_url", songUrl);
                dataList.put(data);
            } else {
                for (int i = 0; i < songList.length(); i++) {
                    JSONObject song = songList.getJSONObject(i);
                    JSONObject data = new JSONObject();
                    data.put("id", song.getString("id"));
                    data.put("name", song.getString("name"));
                    data.put("singername", song.getJSONArray("artists").getJSONObject(0).getString("name"));
                    dataList.put(data);
                }
            }

            /*System.out.println(new JSONObject()
                    .put("code", 200)
                    .put("text", "解析成功")
                    .put("type", "歌曲解析")
                    .put("now", java.time.LocalDateTime.now().toString())
                    .put("data", dataList).toString());*/
            string = new JSONObject()
                    .put("code", 200)
                    .put("text", "解析成功")
                    .put("type", "歌曲解析")
                    .put("now", LocalDateTime.now().toString())
                    .put("data", dataList).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    private static String getCurl(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/6.0 (Linux; Android 11; SAMSUNG SM-G973U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/14.2 Chrome/87.0.4280.141 Mobile Safari/537.36");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getRedirectUrl(String url, String[] headers) {
        try {
            String[] defaultHeaders = {"User-Agent: Mozilla/6.0 (Linux; Android 11; SAMSUNG SM-G973U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/14.2 Chrome/87.0.4280.141 Mobile Safari/537.36"};
            headers = (headers == null || headers.length == 0) ? defaultHeaders : headers;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            for (String header : headers) {
                String[] headerParts = header.split(":");
                con.setRequestProperty(headerParts[0], headerParts[1]);
            }

            con.setInstanceFollowRedirects(false);
            con.connect();

            String redirectUrl = con.getHeaderField("Location");

            return redirectUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
