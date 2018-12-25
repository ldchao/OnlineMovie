package cn.edu.nju.onlinemovie.mapper;

import cn.edu.nju.onlinemovie.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {

    @Select("select MAX(user_id) from user")
    int maxUserId();

    // 获取所有用户信息
    @Select("SELECT * FROM user")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "faceToken", column = "face_token")
    })
    List<User> getUsers();


    // 获取用户的face token
    @Select("SELECT face_token FROM user")
    List<String> getFaceTokens();

    // 添加用户信息
    @Insert("INSERT INTO user(user_id,user_name,face_token) VALUES(#{userId},#{userName},#{faceToken})")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "faceToken", column = "face_token")
    })
    int addUser(User user);

}
