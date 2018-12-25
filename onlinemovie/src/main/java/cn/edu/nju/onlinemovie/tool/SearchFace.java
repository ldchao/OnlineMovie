package cn.edu.nju.onlinemovie.tool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class SearchFace {


    public static String search(String faceToken) {
        String url = ApiInfo.getUrl("/search");
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", ApiInfo.getApiKey());
        map.put("api_secret", ApiInfo.getApiSecret());
        map.put("faceset_token", ApiInfo.getFaceSetToken());
        map.put("face_token", faceToken);
        map.put("return_result_count", "1");

        String str;
        str = ApiInfo.getStr(url, map, byteMap);
        System.out.println(str);
        JSONObject json = new JSONObject(str);
        JSONArray reArray = json.getJSONArray("results");

        JSONObject temp = reArray.getJSONObject(0);
        String result = temp.getDouble("confidence")+"";
        System.out.println(result);
        return result;
    }
}
