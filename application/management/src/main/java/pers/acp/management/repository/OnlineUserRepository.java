package pers.acp.management.repository;

import pers.acp.management.base.BaseRepository;
import pers.acp.management.entity.OnlineUser;

/**
 * @author zhangbin by 2018-1-17 17:47
 * @since JDK1.8
 */
public interface OnlineUserRepository extends BaseRepository<OnlineUser, String> {

    void deleteByUserid(String userid);

    void deleteByLastActiveTimeLessThan(String timeStr);

}
