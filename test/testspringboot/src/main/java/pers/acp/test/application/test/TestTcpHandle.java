package pers.acp.test.application.test;

import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.test.application.repo.primary.TableRepo;

/**
 * @author zhangbin by 2018-1-31 11:59
 * @since JDK 11
 */
@Component("TestTcpHandle")
public class TestTcpHandle implements ISocketServerHandle {

    private final TableRepo tableRepo;

    public TestTcpHandle(TableRepo tableRepo) {
        this.tableRepo = tableRepo;
    }

    @Override
    public String doResponse(String recvStr) {
        return CommonTools.objectToJson(tableRepo.findAll()).toString();
    }

    @Override
    public String userEventTriggered(IdleStateEvent evt) throws Exception {
        return null;
    }
}
