package pers.acp.management.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.management.entity.*;
import pers.acp.management.repository.*;
import pers.acp.core.security.MD5Utils;

/**
 * @author zhangbin by 2018-1-17 17:54
 * @since JDK1.8
 */
@Service
@Transactional(readOnly = true)
public class InitData {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConfigurationRepository userConfigurationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModuleFuncRepository moduleFuncRepository;

    @Transactional
    public void doInit() {
        Application application1 = new Application();
        application1.setWebroot("admin");
        application1.setAppname("Acp-admin");
        application1.setDbno(0);
        application1.setLanguage("zh-CN");
        application1.setCopyrightOwner("zb");
        application1.setCopyrightBegin("2017");
        application1.setDefaultapp(1);
        application1.setVersion("3.0");
        application1.setType(0);
        application1.setSort(0);
        application1 = applicationRepository.save(application1);

        Application application2 = new Application();
        application2.setWebroot("portal");
        application2.setAppname("Acp");
        application2.setDbno(0);
        application2.setLanguage("zh-CN");
        application2.setCopyrightOwner("zb");
        application2.setCopyrightBegin("2017");
        application2.setDefaultapp(1);
        application2.setVersion("3.0");
        application2.setType(1);
        application2.setSort(1);
        application2 = applicationRepository.save(application2);

        Module module = new Module();
        module.setAppid(application1.getId());
        module.setName("系统管理");
        module.setCode("systemadmin");
        module.setParentid(application1.getId());
        module.setType(0);
        module = moduleRepository.save(module);

        Menu menu1 = new Menu();
        menu1.setAppid(application1.getId());
        menu1.setName("系统配置");
        menu1.setIconClass("fa-cogs");
        menu1.setIconColor("#1b992f");
        menu1.setPageUrl("");
        menu1.setParentid(application1.getId());
        menu1.setSort(0);
        menu1.setStatus(1);
        menu1.setType(0);
        menu1.setModel(0);
        menu1.setOpentype(0);
        menu1.setDialogH(0);
        menu1.setDialogW(0);
        menu1 = menuRepository.save(menu1);
        Menu menu2 = new Menu();
        menu2.setAppid(application1.getId());
        menu2.setName("用户配置");
        menu2.setIconClass("fa-users");
        menu2.setIconColor("#354ab8");
        menu2.setPageUrl("/view/page/user/user");
        menu2.setParentid(menu1.getId());
        menu2.setSort(1);
        menu2.setStatus(1);
        menu2.setType(0);
        menu2.setModel(0);
        menu2.setOpentype(0);
        menu2.setDialogH(0);
        menu2.setDialogW(0);
        menu2 = menuRepository.save(menu2);
        Menu menu3 = new Menu();
        menu3.setAppid(application1.getId());
        menu3.setName("机构配置");
        menu3.setIconClass("fa-deviantart");
        menu3.setIconColor("#354ab8");
        menu3.setPageUrl("/view/page/department/department");
        menu3.setParentid(menu1.getId());
        menu3.setSort(2);
        menu3.setStatus(1);
        menu3.setType(0);
        menu3.setModel(0);
        menu3.setOpentype(0);
        menu3.setDialogH(0);
        menu3.setDialogW(0);
        menu3 = menuRepository.save(menu3);
        Menu menu4 = new Menu();
        menu4.setAppid(application2.getId());
        menu4.setName("demo");
        menu4.setIconClass("fa-cogs");
        menu4.setIconColor("#1b992f");
        menu4.setPageUrl("");
        menu4.setParentid(application2.getId());
        menu4.setSort(0);
        menu4.setStatus(1);
        menu4.setType(1);
        menu4.setModel(0);
        menu4.setOpentype(0);
        menu4.setDialogH(0);
        menu4.setDialogW(0);
        menu4 = menuRepository.save(menu4);
        Menu menu5 = new Menu();
        menu5.setAppid(application2.getId());
        menu5.setName("上传");
        menu5.setIconClass("fa-users");
        menu5.setIconColor("#354ab8");
        menu5.setPageUrl("/view/page/demo/upload?_type=0");
        menu5.setParentid(menu4.getId());
        menu5.setSort(1);
        menu5.setStatus(1);
        menu5.setType(1);
        menu5.setModel(0);
        menu5.setOpentype(0);
        menu5.setDialogH(0);
        menu5.setDialogW(0);
        menu5 = menuRepository.save(menu5);

        ModuleFunc moduleFunc1 = new ModuleFunc();
        moduleFunc1.setAppid(application1.getId());
        moduleFunc1.setModuleid(module.getId());
        moduleFunc1.setName("查看所有系统在线用户统计");
        moduleFunc1.setCode("allonlineuser");
        moduleFunc1.setIslog(1);
        moduleFunc1.setType(0);
        moduleFunc1 = moduleFuncRepository.save(moduleFunc1);
        ModuleFunc moduleFunc2 = new ModuleFunc();
        moduleFunc2.setAppid(application1.getId());
        moduleFunc2.setModuleid(module.getId());
        moduleFunc2.setName("系统参数配置");
        moduleFunc2.setCode("sysparamconfig");
        moduleFunc2.setIslog(1);
        moduleFunc2.setType(0);
        moduleFunc2 = moduleFuncRepository.save(moduleFunc2);
        ModuleFunc moduleFunc3 = new ModuleFunc();
        moduleFunc3.setAppid(application1.getId());
        moduleFunc3.setModuleid(module.getId());
        moduleFunc3.setName("应用配置");
        moduleFunc3.setCode("appconfig");
        moduleFunc3.setIslog(1);
        moduleFunc3.setType(0);
        moduleFunc3 = moduleFuncRepository.save(moduleFunc3);
        ModuleFunc moduleFunc4 = new ModuleFunc();
        moduleFunc4.setAppid(application1.getId());
        moduleFunc4.setModuleid(module.getId());
        moduleFunc4.setName("角色配置");
        moduleFunc4.setCode("roleconfig");
        moduleFunc4.setIslog(1);
        moduleFunc4.setType(0);
        moduleFunc4 = moduleFuncRepository.save(moduleFunc4);
        ModuleFunc moduleFunc5 = new ModuleFunc();
        moduleFunc5.setAppid(application1.getId());
        moduleFunc5.setModuleid(module.getId());
        moduleFunc5.setName("权限配置");
        moduleFunc5.setCode("powerconfig");
        moduleFunc5.setIslog(1);
        moduleFunc5.setType(0);
        moduleFunc5 = moduleFuncRepository.save(moduleFunc5);

        Role role = new Role();
        role.setAppid(application1.getId());
        role.setName("超级管理员");
        role.setLevels(0);
        role.setSort(0);
        role.getMenuSet().add(menu1);
        role.getMenuSet().add(menu2);
        role.getMenuSet().add(menu3);
        role.getMenuSet().add(menu4);
        role.getMenuSet().add(menu5);
        role.getModuleSet().add(module);
        role.getModuleFuncSet().add(moduleFunc1);
        role.getModuleFuncSet().add(moduleFunc2);
        role.getModuleFuncSet().add(moduleFunc3);
        role.getModuleFuncSet().add(moduleFunc4);
        role.getModuleFuncSet().add(moduleFunc5);
        role = roleRepository.save(role);

        User user = new User();
        user.setName("超级管理员");
        user.setLoginno("admin");
        user.setPassword(MD5Utils.encrypt(MD5Utils.encrypt("888888") + "admin"));
        user.setLevels(0);
        user.setStatus(1);
        user.setSort(0);
        user.getRoleSet().add(role);
        user = userRepository.save(user);

        UserConfiguration userConfiguration = new UserConfiguration();
        userConfiguration.setAppid(application1.getId());
        userConfiguration.setUserid(user.getId());
        userConfiguration.setSkinColorpicker("skin-1");
        userConfiguration.setSettingsNavbar(1);
        userConfiguration.setSettingsSidebar(1);
        userConfiguration.setSettingsBreadcrumbs(1);
        userConfiguration.setSettingsAddContainer(0);
        userConfiguration.setSettingsHover(0);
        userConfiguration.setSettingsCompact(0);
        userConfiguration.setSettingsHighlight(0);
        userConfiguration.setSettingsUseTabs(1);
        userConfigurationRepository.save(userConfiguration);
    }

}
