package chzzk.grassdiary;

import chzzk.grassdiary.global.config.AwsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(value = {AwsProperties.class})
public class GrassdiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrassdiaryApplication.class, args);
    }

}
