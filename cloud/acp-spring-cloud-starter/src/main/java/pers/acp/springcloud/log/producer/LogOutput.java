package pers.acp.springcloud.log.producer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import pers.acp.springcloud.log.LogConstant;

/**
 * @author zhangbin by 11/07/2018 14:34
 * @since JDK 11
 */
public interface LogOutput {

    @Output(LogConstant.OUTPUT)
    MessageChannel sendMessage();

}
