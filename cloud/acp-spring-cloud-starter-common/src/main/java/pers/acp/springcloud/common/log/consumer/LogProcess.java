package pers.acp.springcloud.common.log.consumer;

import pers.acp.springcloud.common.log.LogInfo;

/**
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
public interface LogProcess {

    void process(LogInfo logInfo);

}
