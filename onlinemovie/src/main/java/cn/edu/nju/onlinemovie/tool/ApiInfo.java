package cn.edu.nju.onlinemovie.tool;

import java.util.HashMap;

public class ApiInfo {
    private static String url = "https://api-cn.faceplusplus.com/facepp/v3";
    private final static String apiKey = "n_AMLORnBrsJygSawMIPb46QbMMssn61";
    private final static String apiSecret = "vXgyB0GqsJJ1oDviL84G1vKco56pXEp0";
    private final static String faceSetToken = "3c7ca305149cf68326cceec62a5c8da8";

    public static String getUrl(String specific) {
        return url + specific;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getApiSecret() {
        return apiSecret;
    }

    public static String getFaceSetToken() {
        return faceSetToken;
    }

    public static String getStr(String url, HashMap<String,String> map, HashMap<String, byte[]> byteMap) {
        String str=null;
        try {
            byte[] bacd = PostTool.post(url, map, byteMap);
            str = new String(bacd);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

}
