package pers.acp.test.application.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.socket.base.BaseSocketHandle;
import pers.acp.test.application.repo.primary.TableRepo;
import pers.acp.core.CommonTools;

/**
 * @author zhangbin by 2018-1-31 11:59
 * @since JDK1.8
 */
@Component("TestTcpHandle")
public class TestTcpHandle extends BaseSocketHandle {

    private final TableRepo tableRepo;

    @Autowired
    public TestTcpHandle(TableRepo tableRepo) {
        this.tableRepo = tableRepo;
    }

    @Override
    public String doResponse(String recvStr) {
        return CommonTools.objectToJson(tableRepo.findAll()).toString();
    }
}
