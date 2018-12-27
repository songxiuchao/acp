package pers.acp.core.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import pers.acp.core.config.instance.AcpProperties;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangbin on 2016/9/9.
 * 工具类
 */
public class CommonUtils {

    private static Properties pps;

    private static String DEFAULT_CHARSET = "utf-8";

    private static final LogFactory log = LogFactory.getInstance(CommonUtils.class);

    /**
     * 获取系统默认字符集
     *
     * @return 字符集
     */
    public static String getDefaultCharset() {
        return DEFAULT_CHARSET;
    }

    /**
     * 获取资源文件的输入流
     *
     * @param fileName 文件路径
     * @return 输入流
     */
    public static InputStream getResourceInputStream(String fileName) {
        String name = fileName.replace("\\", "/");
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        return CommonUtils.class.getClassLoader().getResourceAsStream(name);
    }

    /**
     * 初始化系统配置文件
     */
    private static void initSystemProperties() {
        try {
            pps = AcpProperties.getInstance();
        } catch (Exception e) {
            log.error("load acp.properties failed!");
        }
    }

    /**
     * 获取配置信息
     *
     * @param key 键
     * @return 值
     */
    public static String getProperties(String key) {
        if (pps == null) {
            initSystemProperties();
        }
        if (pps == null) {
            return null;
        } else {
            return pps.getProperty(key);
        }
    }

    /**
     * 获取配置信息
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getProperties(String key, String defaultValue) {
        if (pps == null) {
            initSystemProperties();
        }
        if (pps == null) {
            return defaultValue;
        } else {
            return pps.getProperty(key, defaultValue);
        }
    }

    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    public static boolean isAbsPath(String path) {
        String prefix = getProperties("abspath.prefix", "abs:");
        String prefixu = getProperties("userpath.prefix", "user:");
        return path.startsWith(prefix) || !(path.startsWith(prefixu) || path.startsWith("/") || path.startsWith("\\") || path.startsWith(File.separator));
    }

    /**
     * 获取绝对路径
     *
     * @param srcPath 路径
     * @return 绝对路径
     */
    public static String getAbsPath(String srcPath) {
        String path = srcPath.replace("\\", File.separator).replace("/", File.separator);
        if (isAbsPath(path)) {
            String prefix = getProperties("abspath.prefix", "abs:");
            if (path.startsWith(prefix)) {
                return path.substring(prefix.length());
            } else {
                return path;
            }
        } else {
            String prefix = getProperties("userpath.prefix", "user:");
            if (path.startsWith(prefix)) {
                return System.getProperty("user.home") + path.substring(prefix.length());
            } else {
                return getProjectAbsPath() + path;
            }
        }
    }

    /**
     * 获取webroot绝对路径
     *
     * @return webroot绝对路径
     */
    public static String getProjectAbsPath() {
        try {
            String classPath = URLDecoder.decode(CommonUtils.class.getResource("/").getPath(), DEFAULT_CHARSET);
            int indexWEB_INF = classPath.indexOf("WEB-INF");
            if (indexWEB_INF == -1) {
                indexWEB_INF = classPath.indexOf("bin");
            }
            String webrootpath = classPath;
            if (indexWEB_INF != -1) {
                webrootpath = webrootpath.substring(0, indexWEB_INF);
            }
            if (webrootpath.startsWith("jar")) {
                webrootpath = webrootpath.substring(10);
            } else if (webrootpath.startsWith("file")) {
                webrootpath = webrootpath.substring(6);
            } else {
                ClassLoader classLoader = CommonUtils.class.getClassLoader();
                URL url = classLoader.getResource("/");
                if (url != null) {
                    classPath = url.getPath();
                    indexWEB_INF = classPath.indexOf("WEB-INF");
                    if (indexWEB_INF == -1) {
                        indexWEB_INF = classPath.indexOf("bin");
                    }
                    if (indexWEB_INF != -1) {
                        webrootpath = classPath.substring(0, indexWEB_INF);
                    } else {
                        webrootpath = classPath;
                    }
                }
            }
            if (webrootpath.endsWith("/")) {
                webrootpath = webrootpath.substring(0, webrootpath.length() - 1);
            }
            webrootpath = webrootpath.replace("/", File.separator);
            if (webrootpath.startsWith("\\")) {
                webrootpath = webrootpath.substring(1);
            }
            if (webrootpath.contains("!")) {
                webrootpath = webrootpath.substring(0, webrootpath.indexOf("!"));
                webrootpath = webrootpath.substring(0, webrootpath.lastIndexOf(File.separator));
            }
            if (CommonUtils.isNullStr(webrootpath)) {
                webrootpath = File.separator;
            } else {
                if (File.separator.equalsIgnoreCase("/")) {
                    if (!webrootpath.startsWith("/")) {
                        webrootpath = "/" + webrootpath;
                    }
                }
            }
            return webrootpath;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("webRootAbsPath=\"\"");
            return "";
        }
    }

    /**
     * 表达式变量替换
     *
     * @param varFormula 变量表达式:格式“${变量名}” 或带有变量格式的字符串
     * @param data       数据集
     * @return 目标字符串
     */
    public static String replaceVar(String varFormula, Map<String, String> data) {
        String tmpvar = varFormula;
        if (tmpvar.contains("${")) {
            if (tmpvar.contains("}")) {
                int begin = tmpvar.indexOf("${");
                while (begin != -1) {
                    tmpvar = tmpvar.substring(begin + 2);
                    if (tmpvar.contains("}")) {
                        String var = tmpvar.substring(0, tmpvar.indexOf("}"));
                        if (data.containsKey(var)) {
                            varFormula = varFormula.replace("${" + var + "}", data.get(var));
                        }
                        tmpvar = tmpvar.substring(tmpvar.indexOf("}") + 1);
                        begin = tmpvar.indexOf("${");
                    } else {
                        begin = -1;
                    }
                }
            }
            return varFormula;
        } else {
            return varFormula;
        }
    }

    /**
     * 判断是否空字符串
     *
     * @param src 源字符串
     * @return 是否为空
     */
    public static boolean isNullStr(String src) {
        return StringUtils.isBlank(src);
    }

    /**
     * 获取36位全球唯一的字符串（带4个分隔符）
     *
     * @return 结果
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * 获取32位全球唯一的字符串
     *
     * @return 结果
     */
    public static String getUuid32() {
        return getUuid().replace("-", "");
    }

    /**
     * 获取24位短uuid
     *
     * @return 结果
     */
    public static String getUuid24() {
        return getUuid(24);
    }

    /**
     * 获取16位短uuid
     *
     * @return 结果
     */
    public static String getUuid16() {
        return getUuid(16);
    }

    /**
     * 获取8位短uuid
     *
     * @return 结果
     */
    public static String getUuid8() {
        return getUuid(8);
    }

    /**
     * 获取uuid
     *
     * @param length 字符串长度
     * @return 结果
     */
    private static String getUuid(int length) {
        return new RandomStringGenerator.Builder()
                .withinRange(33, 126)
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build().generate(length);
    }

    /**
     * 源字符串中每到指定长度时就插入子字符串
     *
     * @param src          源字符串
     * @param length       分隔长度
     * @param insertString 插入的子字符串
     * @return 目标字符串
     */
    public static String autoInsertString(String src, int length, String insertString) {
        String result = src;
        int maxlength = src.length();
        for (int i = 0; i < maxlength / length; i++) {
            result = result.substring(0, (i + 1) * length + (i * insertString.length())) + insertString + result.substring((i + 1) * length + (i * insertString.length()));
        }
        if (result.lastIndexOf(insertString) == result.length() - insertString.length()) {
            result = result.substring(0, result.length() - insertString.length());
        }
        return result;
    }

    /**
     * 字符串填充函数
     *
     * @param str    待填充的字符串
     * @param number 填充后的字节长度
     * @param flag   0-向左填充，1-向右填充
     * @param c      填充字符
     * @return 填充后的字符串
     */
    public static String strFillIn(String str, int number, int flag, char c) {
        return strFillIn(str, number, flag, c + "");
    }

    /**
     * 字符串填充函数
     *
     * @param str    待填充的字符串
     * @param number 填充后的字节长度
     * @param flag   0-向左填充，1-向右填充
     * @param string 填充字符串
     * @return 填充后的字符串
     */
    public static String strFillIn(String str, int number, int flag, String string) {
        int byteLen = str.getBytes().length;
        int strLen = str.length();
        if (flag == 0) {
            return StringUtils.leftPad(str, number - (byteLen - strLen), string);
        } else if (flag == 1) {
            return StringUtils.rightPad(str, number - (byteLen - strLen), string);
        } else {
            return "";
        }
    }

    /**
     * 判断字符串是否在数组中
     *
     * @param str        源字符串
     * @param array      字符串数组
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    public static boolean strInArray(String str, String[] array, boolean ignoreCase) {
        if (array != null) {
            for (String anArray : array) {
                if (ignoreCase) {
                    if (str.equalsIgnoreCase(anArray)) {
                        return true;
                    }
                } else {
                    if (str.equals(anArray)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断字符串是否在列表中
     *
     * @param str        源字符串
     * @param arrayList  字符串列表
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    public static boolean strInList(String str, List<String> arrayList, boolean ignoreCase) {
        if (arrayList != null) {
            long count;
            if (ignoreCase) {
                count = arrayList.stream().filter(anArrayList -> anArrayList.equalsIgnoreCase(str)).count();
            } else {
                count = arrayList.stream().filter(anArrayList -> anArrayList.equals(str)).count();
            }
            return count > 0;
        }
        return false;
    }

    /**
     * 获取指定格式的时间字符串
     *
     * @param date       Date实例
     * @param dateFormat 格式
     * @return 格式化的时间格式
     */
    public static String getDateTimeString(Date date, String dateFormat) {
        if (date == null) {
            date = new Date(); // 当前时间
        }
        if (isNullStr(dateFormat)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss"; // 默认时间格式化模式
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前日期
     *
     * @return 日期字符串
     */
    public static String getNowString() {
        return getDateTimeString(null, "yyyy-MM-dd");
    }

    /**
     * 获取当前时间
     *
     * @return 日期时间字符串
     */
    public static String getNowTimeString() {
        return getDateTimeString(null, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 在线程池中执行任务
     *
     * @param threadPool 线程池实例
     * @param task       线程池任务
     * @return 执行结果
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static Object excuteTaskInThreadPool(ThreadPoolService threadPool, BaseThreadTask task) {
        if (task != null && threadPool != null) {
            threadPool.addTask(task);
            try {
                synchronized (task) {
                    task.wait();
                    if (task.getTaskResult() != null) {
                        log.debug("excute task int threadpool [" + threadPool.getPoolName() + "] success:" + task.getTaskResult());
                        return task.getTaskResult();
                    } else {
                        log.debug("excute task int threadpool [" + threadPool.getPoolName() + "] success");
                        return null;
                    }
                }
            } catch (Exception e) {
                log.error("excute task int threadpool [" + threadPool.getPoolName() + "] failed:" + e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }
    }

}
