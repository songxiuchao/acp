package pers.acp.management.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.management.repository.OnlineUserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhangbin by 2018-1-20 21:43
 * @since JDK1.8
 */
@Service
@Transactional(readOnly = true)
public class OnlineUserDomain {

    private final OnlineUserRepository onlineUserRepository;

    @Autowired
    public OnlineUserDomain(OnlineUserRepository onlineUserRepository) {
        this.onlineUserRepository = onlineUserRepository;
    }

    @Transactional
    public void doClean(long timeout) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = simpleDateFormat.format((new Date()).getTime() - timeout);
        onlineUserRepository.deleteByLastActiveTimeLessThan(timeStr);
    }

    @Transactional
    public void doClean(String userId) {
        onlineUserRepository.deleteByUserid(userId);
    }

    @Transactional
    public void doCleanAll() {
        onlineUserRepository.deleteAll();
    }

}
