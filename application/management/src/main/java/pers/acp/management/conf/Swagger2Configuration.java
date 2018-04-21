package pers.acp.management.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangbin by 2018-1-30 22:18
 * @since JDK1.8
 */
@Configuration
@EnableSwagger2
public class Swagger2Configuration {

    @Bean
    public Docket createRestApi() {
        Set<String> protocols = new HashSet<>();
        protocols.add("https");
        protocols.add("http");
        return new Docket(DocumentationType.SWAGGER_2)
                .protocols(protocols)
                .apiInfo(apiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("pers.acp.management.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建 api文档的详细信息函数
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("API 文档")
                //创建人
                .contact(new Contact("zhangbin", "https://user.qzone.qq.com/391289241", "zhangbin1010@qq.com"))
                //版本号
                .version("3.0")
                //描述
                .description("API 描述")
                .build();
    }

}
