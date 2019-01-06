package cn.edu.nju.onlinemovie.controller;

import cn.edu.nju.onlinemovie.bean.User;
import cn.edu.nju.onlinemovie.mapper.UserMapper;
import cn.edu.nju.onlinemovie.tool.AddFaceTool;
import cn.edu.nju.onlinemovie.tool.Detection;
import cn.edu.nju.onlinemovie.tool.SearchFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @RequestMapping("/all")
    @ResponseBody
    public List<User> getUsers() {
        return userMapper.getUsers();
    }

    @RequestMapping("/add")
    @ResponseBody
    public String addUser() {
        User user = new User();
        int id = userMapper.maxUserId() + 1;
        user.setUserId(id);
        user.setUserName("dog");
        user.setFaceToken("faijdaiofjdadja");
        userMapper.addUser(user);
        return "success";
    }

    @RequestMapping("/face")
    @ResponseBody
    public String getFaceToken(String imgString) throws IOException {

        JSONObject json = new JSONObject();
        String result = null;
        System.out.println("Face");
        String faceToken = Detection.getFace(imgString);
        System.out.println(faceToken);
        if (faceToken.equals("error")) {
            json.put("info","no face");
            result = json.toString();
            return result;
        } else {
            String conf_str = SearchFace.search(faceToken);
            double confidence = Double.parseDouble(conf_str);
            if (confidence >= 75) {
                json.put("info","exist");
                result = json.toString();
                return result;
            } else {
                AddFaceTool.addface(faceToken);
                json.put("info","create");
                result = json.toString();
                return result;
            }
        }
    }


}
