package cn.edu.nju.onlinemovie.util;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by ldchao on 2019/1/6.
 */
@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketServer>> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid，表示房间号
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        CopyOnWriteArraySet<WebSocketServer> webSocketServers = webSocketMap.get(sid);

        JSONObject json = new JSONObject();

        String status;

        if (webSocketServers == null) {
            CopyOnWriteArraySet<WebSocketServer> list = new CopyOnWriteArraySet<WebSocketServer>();
            list.add(this);
            webSocketMap.put(sid, list);
            status = "waiting";

        } else {
            webSocketServers.add(this);
            status = "connected";
        }

        addOnlineCount(); //在线数加1
        System.out.println("有新窗口开始监听:" + sid + ",当前在线人数为" + getOnlineCount());
        this.sid = sid;

        json.put("status", status);
        if (status.equals("connected")) {
            connect(json.toString());
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketMap.get(sid).remove(this); //从set中删除
        if (webSocketMap.get(sid).isEmpty()) {
            webSocketMap.remove(sid);
        }
        subOnlineCount(); //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到来自窗口" + sid + "的信息:" + message);

        //群发消息
        for (WebSocketServer item : webSocketMap.get(sid)) {
            try {
                if (item != this.session) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(String message) throws IOException {
        synchronized (this.session) {
            this.session.getBasicRemote().sendText(message);
        }
    }


    private void connect(String message) {
        System.out.println("推送消息到窗口" + sid + "，推送内容:" + message);
        for (WebSocketServer item : webSocketMap.get(sid)) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}