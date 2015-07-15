package com.gop.lfg.data.config;

import com.mongodb.Mongo;
import org.mongeez.MongeezRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.inject.Inject;

/**
 * Created by VMPX4526 on 15/07/2015.
 */
@Configuration
public class MongoConfig {

    @Inject
    private Mongo mongo;

    @Inject
    private MongoTemplate mongoTemplate;

    @Bean
    public MongeezRunner mongeezRunner() {
        MongeezRunner runner = new MongeezRunner();
        runner.setExecuteEnabled(true);
        runner.setMongo(mongo);
        runner.setDbName(mongoTemplate.getDb().getName());
        runner.setFile(new ClassPathResource("mongeez/mongeez.xml"));
        return runner;
    }

}
