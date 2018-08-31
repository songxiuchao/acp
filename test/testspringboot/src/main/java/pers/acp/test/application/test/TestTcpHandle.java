package pers.acp.test.application.test;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.socket.base.ISocketServerHandle;
import pers.acp.test.application.repo.primary.TableRepo;
import pers.acp.core.CommonTools;

/**
 * @author zhangbin by 2018-1-31 11:59
 * @since JDK1.8
 */
@Component("TestTcpHandle")
public class TestTcpHandle implements ISocketServerHandle {

    private final TableRepo tableRepo;

    @Autowired
    public TestTcpHandle(TableRepo tableRepo) {
        this.tableRepo = tableRepo;
    }

    @Override
    public String doResponse(String recvStr) {
        return CommonTools.objectToJson(tableRepo.findAll()).toString();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {

    }

    @Override
    public void sessionClosed(IoSession session) {

    }

    @Override
    public void sessionCreated(IoSession session) {

    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idlestatus) {
        session.closeNow();
    }

    @Override
    public void sessionOpened(IoSession session) {

    }
}
