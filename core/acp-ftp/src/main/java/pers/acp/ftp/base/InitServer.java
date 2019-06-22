package pers.acp.ftp.base;

import pers.acp.ftp.user.UserFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public abstract class InitServer {

    private static Map<String, UserFactory> userFactoryMap = new ConcurrentHashMap<>();

    protected static void addUserFactory(UserFactory userFactory) {
        userFactoryMap.put(userFactory.getClass().getCanonicalName(), userFactory);
    }

    protected static UserFactory getUserFactory(String className) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        UserFactory userFactory = userFactoryMap.getOrDefault(className, null);
        if (userFactory == null) {
            userFactory = (UserFactory) Class.forName(className).getDeclaredConstructor().newInstance();
        }
        return userFactory;
    }

}
