package com.yk.Motivation.base.initData;

import com.yk.Motivation.base.app.AppConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class All {
    @Bean
    public ApplicationRunner initAll(
    ) {
        return args -> {
            new File(AppConfig.getTempDirPath()).mkdirs();
        };
    }
}
