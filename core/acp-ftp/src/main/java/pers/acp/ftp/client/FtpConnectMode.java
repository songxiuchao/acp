package pers.acp.ftp.client;

import org.apache.commons.net.ftp.FTPClient;
import pers.acp.core.exceptions.EnumValueUndefinedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by zhangbin on 2017-11-3 15:16
 */
public enum FtpConnectMode {

    ACTIVE_LOCAL("ACTIVE_LOCAL", FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE),

    ACTIVE_REMOTE("ACTIVE_REMOTE", FTPClient.ACTIVE_REMOTE_DATA_CONNECTION_MODE),

    PASSIVE_LOCAL("PASSIVE_LOCAL", FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE),

    PASSIVE_REMOTE("PASSIVE_REMOTE", FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE);

    private String name;

    private Integer value;

    private static Map<Integer, FtpConnectMode> map;

    static {
        map = new HashMap<>();
        for (FtpConnectMode type : values()) {
            map.put(type.getValue(), type);
        }
    }

    FtpConnectMode(String name, Integer value) {
        this.name = name.toLowerCase();
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static FtpConnectMode getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(FtpConnectMode.class, value + "");
    }

}
