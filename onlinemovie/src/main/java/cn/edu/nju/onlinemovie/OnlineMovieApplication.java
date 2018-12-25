package cn.edu.nju.onlinemovie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.nju.onlinemovie.mapper")
public class OnlineMovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineMovieApplication.class, args);
	}
}
