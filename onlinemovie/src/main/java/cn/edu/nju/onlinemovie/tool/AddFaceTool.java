package cn.edu.nju.onlinemovie.tool;

import java.util.HashMap;

public class AddFaceTool {
    public static void addface(String facetoken) {
        String url = ApiInfo.getUrl("/addface");
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", ApiInfo.getApiKey());
        map.put("api_secret", ApiInfo.getApiSecret());
        map.put("faceset_token", ApiInfo.getFaceSetToken());
        map.put("face_tokens", facetoken);

        try {
            PostTool.post(url, map, byteMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
