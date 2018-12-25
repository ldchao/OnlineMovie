package cn.edu.nju.onlinemovie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by ldchao on 2018/5/7.
 */
@Controller
public class UrlController {


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/movie")
    public String movie(Model model, String roomId) {
        model.addAttribute("roomId", roomId );
        return "OnlineMovie";
    }

    @GetMapping("/room")
    public String room() {
        return "Room";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/p2p")
    public String p2p() {
        return "p2p";
    }


}
