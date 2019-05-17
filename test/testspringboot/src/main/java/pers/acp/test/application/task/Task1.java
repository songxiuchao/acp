package pers.acp.test.application.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.base.BaseSpringBootScheduledTask;
import pers.acp.test.application.repo.pg.TableTwoRepo;
import pers.acp.test.application.repo.primary.TableRepo;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

@Component("task1")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Task1 extends BaseSpringBootScheduledTask {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private final TableRepo tableRepo;

    private final TableTwoRepo tableTwoRepo;

    @Autowired
    public Task1(TableRepo tableRepo, TableTwoRepo tableTwoRepo) {
        this.tableRepo = tableRepo;
        this.tableTwoRepo = tableTwoRepo;
    }

    @Override
    public boolean beforeExecuteFun() {
        return true;
    }

    @Override
    public Object executeFun() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>我是定时任务1》》》》》》》》》》");
        log.info("table 1 >>>>> " + CommonTools.objectToJson(tableRepo.findAll()));
        log.info("table 2 >>>>> " + CommonTools.objectToJson(tableTwoRepo.findAll()));
        return true;
    }

    @Override
    public void afterExecuteFun(Object result) {

    }
}
