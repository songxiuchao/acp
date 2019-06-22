package pers.acp.spring.boot.init;

import io.netty.handler.codec.ByteToMessageDecoder;
import pers.acp.spring.boot.socket.base.ISocketServerHandle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang by 22/06/2019
 * @since JDK 11
 */
public abstract class BaseInitTask {

    private static Map<String, ISocketServerHandle> socketServerHandleMap = new ConcurrentHashMap<>();

    private static Map<String, ByteToMessageDecoder> byteToMessageDecoderMap = new ConcurrentHashMap<>();

    protected static void addServerHandle(ISocketServerHandle socketServerHandle) {
        socketServerHandleMap.put(socketServerHandle.getClass().getCanonicalName(), socketServerHandle);
    }

    protected static void addMessageDecoder(ByteToMessageDecoder byteToMessageDecoder) {
        byteToMessageDecoderMap.put(byteToMessageDecoder.getClass().getCanonicalName(), byteToMessageDecoder);
    }

    protected static ISocketServerHandle getSocketServerHandle(String className) {
        return socketServerHandleMap.getOrDefault(className, null);
    }

    protected static ByteToMessageDecoder getMessageDecoder(String className) {
        return byteToMessageDecoderMap.getOrDefault(className, null);
    }

}
