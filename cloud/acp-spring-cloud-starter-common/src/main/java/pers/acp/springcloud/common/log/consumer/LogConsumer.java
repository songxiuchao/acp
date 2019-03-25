package pers.acp.springcloud.common.log.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springcloud.common.log.LogConstant;
import pers.acp.springcloud.common.log.LogInfo;

/**
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
public class LogConsumer {

    private final LogFactory log = LogFactory.getInstance(LogConsumer.class);

    private final ObjectMapper objectMapper;

    private final LogProcess logProcess;

    @Autowired
    public LogConsumer(ObjectMapper objectMapper, LogProcess logProcess) {
        this.objectMapper = objectMapper;
        this.logProcess = logProcess;
    }

    @StreamListener(LogConstant.INPUT)
    public void consumer(String message) {
        try {
            LogInfo logInfo = objectMapper.readValue(message, LogInfo.class);
            String logType = LogConstant.DEFAULT_TYPE;
            if (!CommonTools.isNullStr(logInfo.getLogType())) {
                logType = logInfo.getLogType();
            }
            logInfo.setLogType(logType);
            logProcess.process(logInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
