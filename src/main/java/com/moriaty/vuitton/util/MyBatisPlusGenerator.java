package com.moriaty.vuitton.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

/**
 * <p>
 * Mybatis Plus 生成器
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午12:10
 */
public class MyBatisPlusGenerator {

    private MyBatisPlusGenerator() {

    }

    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.50.112:3306/vuitton_plus?serverTimezone=GMT%2B8&useUnicode=true"
                     + "&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false"
                     + "&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root";
        FastAutoGenerator.create(url, username, password)
                .dataSourceConfig(builder -> builder
                        .schema("mybatis-plus")
                        .keyWordsHandler(new MySqlKeyWordsHandler()))
                .globalConfig(builder -> builder
                        .outputDir("src/main/java/")
                        .disableOpenDir()
                        .author("Moriaty")
                        .commentDate("yyyy-MM-dd HH:mm:ss"))
                .packageConfig(builder -> builder
                        .parent("com.moriaty.vuitton.dao")
                        .entity("model"))
                .templateConfig(builder -> builder
                        .disable(TemplateType.XML, TemplateType.MAPPER, TemplateType.SERVICE,
                                TemplateType.SERVICE_IMPL, TemplateType.CONTROLLER))
                .strategyConfig(builder -> builder.addInclude("video_play_history")
                        .entityBuilder().enableLombok().enableChainModel()
                        .enableFileOverride().disableSerialVersionUID())
                .execute();
    }
}
