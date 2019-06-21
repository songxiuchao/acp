package pers.acp.springboot.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pers.acp.core.CommonTools;
import pers.acp.springboot.enums.ResponseCode;
import pers.acp.springboot.vo.ErrorVO;

/**
 * Created by Shepherd on 2016-08-05.
 * 报文工具类
 */
public class PackageTools {

    /**
     * 根据环境配置，构建 mapper
     *
     * @param environment 环境配置
     * @return mapper
     */
    public static ObjectMapper buildJacksonObjectMapper(Environment environment) {
        JacksonProperties jacksonProperties = new JacksonProperties();
        jacksonProperties.setPropertyNamingStrategy(environment.getProperty("spring.jackson.property-naming-strategy", ""));
        if ("non_null".equals(environment.getProperty("spring.jackson.default-property-inclusion", "").toLowerCase())) {
            jacksonProperties.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        }
        return buildJacksonObjectMapper(jacksonProperties);
    }

    /**
     * 根据配置文件，构建 mapper
     *
     * @param jacksonProperties 配置信息
     * @return mapper
     */
    public static ObjectMapper buildJacksonObjectMapper(JacksonProperties jacksonProperties) {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        PropertyNamingStrategy propertyNamingStrategyDefault = new PropertyNamingStrategy();
        if ("SNAKE_CASE".equals(jacksonProperties.getPropertyNamingStrategy().toUpperCase())) {
            propertyNamingStrategyDefault = new PropertyNamingStrategy.SnakeCaseStrategy();
        }
        builder.propertyNamingStrategy(propertyNamingStrategyDefault);
        if (jacksonProperties.getDefaultPropertyInclusion() != null) {
            builder.serializationInclusion(jacksonProperties.getDefaultPropertyInclusion());
        }
        return builder.propertyNamingStrategy(propertyNamingStrategyDefault).build();
    }

    /**
     * 构建响应报文
     *
     * @param responseCode 响应代码
     * @param msg          响应信息
     * @return 响应报文JSON对象
     */
    public static ErrorVO buildErrorResponsePackage(ResponseCode responseCode, String msg) {
        if (CommonTools.isNullStr(msg)) {
            msg = responseCode.getName();
        }
        return buildErrorResponsePackage(responseCode.getValue(), msg);
    }

    /**
     * 构建响应报文
     *
     * @param code 响应代码
     * @param msg  响应信息
     * @return 响应报文JSON对象
     */
    public static ErrorVO buildErrorResponsePackage(Integer code, String msg) {
        ErrorVO errorVO = new ErrorVO();
        errorVO.setCode(code);
        errorVO.setError(msg);
        errorVO.setErrorDescription(msg);
        return errorVO;
    }

}
