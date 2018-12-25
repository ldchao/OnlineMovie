package cn.edu.nju.onlinemovie.controller;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by ldchao on 2018/11/5.
 */
@RestController
public class ConnectController {

    private Map<String, String> connections = new HashMap<>();
    private Map<Integer, Integer> partner = new HashMap<>();
    private Map<Integer, List<JSONObject>> messagesFor = new HashMap<>();
    private Map<String, Integer> oneConnections = new HashMap<>();

    @GetMapping(value = "/connect")
    public String connect(String key) {
        String status;
        int[] ids = new int[2];
        Random rand = new Random();
        if (key != null) {
            status = connections.getOrDefault(key, "new");
            JSONObject json = new JSONObject();
            if (status.equals("waiting")) {    // 前半部分就绪
                ids[1] = rand.nextInt(1000000000);
                ids[0] = oneConnections.get(key);
                System.out.println("second:" + ids[1]);
                partner.put(ids[0], ids[1]);
                partner.put(ids[1], ids[0]);
                messagesFor.put(ids[0], new ArrayList<>());
                messagesFor.put(ids[1], new ArrayList<>());
                status = "connected";
                json.put("id", ids[1]);
                json.put("status", status);
            } else {        // 必须为新连接或为‘connected’状态
                if (status.equals("connected")) {
                    // 删除配对和任何存储的信息
                    partner.remove(ids[0]);
                    partner.remove(ids[1]);
                    messagesFor.remove(ids[0]);
                    messagesFor.remove(ids[1]);
                }
                connections.put(key, "waiting");
                status = "waiting";
                ids[0] = rand.nextInt(1000000000);
                oneConnections.put(key, ids[0]);
                System.out.println("first:" + ids[0]);
                json.put("id", ids[0]);
                json.put("status", status);
            }
            return json.toString();
        } else {
            return "No recognizable query key";
        }
    }


    @PostMapping(value = "/get")
    public String get(Integer id) {
        if (id == null) {
            return webRtcError("No id received with message!");
        }
        if (!partner.containsKey(id) || !messagesFor.containsKey(partner.get(id))) {
            return webRtcError("Invalid id " + id);
        }
        JSONObject json = new JSONObject();
        json.put("msgs", messagesFor.get(id));
        messagesFor.put(id, new ArrayList<>());
        return json.toString();
    }

    @PostMapping(value = "/send")
    public String send(Integer id, String message) {


        if (id == null) {
            return webRtcError("No id received with message!");
        }
        if (message == null) {
            return webRtcError("No message received!");
        }

        if (!partner.containsKey(id) || !messagesFor.containsKey(partner.get(id))) {
            return webRtcError("Invalid id " + id);
        }
        messagesFor.get(partner.get(id)).add(new JSONObject(message));
        JSONObject json = new JSONObject();
        json.put("msg", "Saving message *** " + message + " *** for delivery to id " + partner.get(id));
        return json.toString();
    }

    private String webRtcError(String err) {
        JSONObject json = new JSONObject();
        json.put("err", err);
        return json.toString();
    }
}
