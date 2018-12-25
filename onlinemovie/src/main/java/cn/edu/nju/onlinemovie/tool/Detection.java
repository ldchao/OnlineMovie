package cn.edu.nju.onlinemovie.tool;

import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.HashMap;

public class Detection {


    public static String getFace(String imgString) throws IOException {
        byte[] buff = getStringImage(imgString.substring(imgString.indexOf(",") + 1));
        String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "n_AMLORnBrsJygSawMIPb46QbMMssn61");
        map.put("api_secret", "vXgyB0GqsJJ1oDviL84G1vKco56pXEp0");
        byteMap.put("image_file", buff);
        String str = null;
        String faceToken = null;
        try {
            byte[] bacd = PostTool.post(url, map, byteMap);
            str = new String(bacd);
            System.out.println("Image--->" + str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject(str);
        JSONArray faces = json.getJSONArray("faces");
        if (faces.isEmpty()) {
            faceToken = "error";
        } else {
            JSONObject jsonToken = faces.getJSONObject(0);
            faceToken = jsonToken.getString("face_token");
            System.out.println("face token---> " + faceToken);
        }
        return faceToken;
    }

    /**
     * Base64字符串转 二进制流
     *
     * @param base64String Base64
     * @return base64String
     * @throws IOException 异常
     */
    public static byte[] getStringImage(String base64String) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return base64String != null ? decoder.decodeBuffer(base64String) : null;
    }


}