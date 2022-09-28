package com.dorohedoro;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MybatisPlusGenerator {
    
    @Test
    public void generate() {
        FastAutoGenerator.create(
                "jdbc:mysql://localhost:3306/security?autoReconnect=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC",
                "root",
                "12345")
                .globalConfig(builder -> {
                    builder.author("liuziye")
                            .outputDir("C://mybatis-plus");
                })
                .packageConfig(builder -> {
                    builder.parent("com.dorohedoro")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "C:/mybatis-plus//com//dorohedoro"));
                })
                .strategyConfig(builder -> builder.addInclude("roles"))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
