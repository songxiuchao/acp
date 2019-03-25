package pers.acp.springcloud.common.log.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
import pers.acp.springcloud.common.log.LogConstant;

/**
 * @author zhangbin by 11/07/2018 14:34
 * @since JDK 11
 */
public interface LogInput {

    @Input(LogConstant.INPUT)
    SubscribableChannel input();

}
