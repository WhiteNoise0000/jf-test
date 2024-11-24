package jp.whitenoise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JfTestApplication {

    public static final String APP_NAME = "漁獲予定量集計";

    public static void main(String[] args) {
        SpringApplication.run(JfTestApplication.class, args);
    }

}
