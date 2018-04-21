package pers.acp.management.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.management.entity.Application;
import pers.acp.management.repository.ApplicationRepository;

import java.util.List;

/**
 * @author zhangbin by 2018-1-20 22:43
 * @since JDK1.8
 */
@Service
@Transactional(readOnly = true)
public class ApplicationDomain {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationDomain(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

}
