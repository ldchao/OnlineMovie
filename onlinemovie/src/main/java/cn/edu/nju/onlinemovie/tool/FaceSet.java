package cn.edu.nju.onlinemovie.tool;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class FaceSet {
    public static String getFaceSet() throws IOException {
        String url = ApiInfo.getUrl("/getdetail");
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", ApiInfo.getApiKey());
        map.put("api_secret", ApiInfo.getApiSecret());

        map.put("faceset_token", ApiInfo.getFaceSetToken());
        String str = null;
        str = ApiInfo.getStr(url, map, byteMap);
        JSONObject json = new JSONObject(str);
        String faceset = json.getString("face_tokens");
        System.out.println("face set---> " + faceset);
        return faceset;
    }

}

