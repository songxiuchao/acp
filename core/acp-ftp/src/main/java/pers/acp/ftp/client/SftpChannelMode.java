package pers.acp.ftp.client;

import pers.acp.core.exceptions.EnumValueUndefinedException;
import com.jcraft.jsch.ChannelSftp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangbin on 2016/12/20.
 * sftp 传输模式
 */
public enum SftpChannelMode {

    OVERWRITE("OVERWRITE", ChannelSftp.OVERWRITE),

    RESUME("RESUME", ChannelSftp.RESUME),

    APPEND("APPEND", ChannelSftp.APPEND);

    private String name;

    private Integer value;

    private static Map<Integer, SftpChannelMode> map;

    static {
        map = new HashMap<>();
        for (SftpChannelMode type : values()) {
            map.put(type.getValue(), type);
        }
    }

    SftpChannelMode(String name, Integer value) {
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

    public static SftpChannelMode getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(SftpChannelMode.class, value + "");
    }

}
