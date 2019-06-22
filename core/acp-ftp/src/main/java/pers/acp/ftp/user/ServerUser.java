package pers.acp.ftp.user;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP/SFTP 用户
 */
public abstract class ServerUser {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public boolean isEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(boolean enableFlag) {
        this.enableFlag = enableFlag;
    }

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 可访问路径
     */
    private String homeDirectory = "/";

    /**
     * 是否启用
     */
    private boolean enableFlag = true;

}
