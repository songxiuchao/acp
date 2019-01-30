package pers.acp.core.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import pers.acp.core.config.instance.AcpProperties;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 正则表达式匹配字符串
     *
     * @param regex  正则表达式
     * @param srcStr 待匹配字符串
     * @return true|false
     */
    public static boolean regexPattern(String regex, String srcStr) {
        if (isNullStr(regex)) {
            return false;
        }
        return Pattern.compile(regex).matcher(srcStr).matches();
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
            int endIndex = (i + 1) * length + (i * insertString.length());
            result = result.substring(0, endIndex) + insertString + result.substring(endIndex);
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

    /**
     * 字符串转JSON对象
     *
     * @param src 字符串
     * @return json对象
     */
    public static JsonNode getJsonFromStr(String src) {
        JsonNode jsonNode;
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonNode = mapper.readTree(src);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            jsonNode = mapper.createObjectNode();
        }
        return jsonNode;
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj json对象（JsonNode）
     * @param cls     目标类
     * @return 目标对象
     */
    public static <T> T jsonToObject(JsonNode jsonObj, Class<T> cls) {
        return jsonToObject(PropertyNamingStrategy.SNAKE_CASE, jsonObj, cls);
    }

    /**
     * json对象转为java对象
     *
     * @param propertyNamingStrategy 名称处理规则
     * @param jsonObj                json对象（JsonNode）
     * @param cls                    目标类
     * @return 目标对象
     */
    public static <T> T jsonToObject(PropertyNamingStrategy propertyNamingStrategy, JsonNode jsonObj, Class<T> cls) {
        PropertyNamingStrategy propertyNamingStrategyDefault = new PropertyNamingStrategy();
        if (propertyNamingStrategy != null) {
            propertyNamingStrategyDefault = propertyNamingStrategy;
        }
        ObjectMapper mapper = new ObjectMapper();
        T instance = null;
        try {
            mapper.setPropertyNamingStrategy(propertyNamingStrategyDefault);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            instance = mapper.readValue(jsonObj.toString(), cls);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return instance;
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @return json对象
     */
    public static JsonNode objectToJson(Object instance) {
        return objectToJson(instance, null);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @param excludes 排除的实体成员
     * @return json对象
     */
    public static JsonNode objectToJson(Object instance, String[] excludes) {
        return objectToJson(PropertyNamingStrategy.SNAKE_CASE, instance, excludes);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param propertyNamingStrategy 名称处理规则
     * @param instance               实体对象（只持Map对象）
     * @param excludes               排除的实体成员
     * @return json对象
     */
    public static JsonNode objectToJson(PropertyNamingStrategy propertyNamingStrategy, Object instance, String[] excludes) {
        PropertyNamingStrategy propertyNamingStrategyDefault = new PropertyNamingStrategy();
        if (propertyNamingStrategy != null) {
            propertyNamingStrategyDefault = propertyNamingStrategy;
        }
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(true);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setNodeFactory(jsonNodeFactory);
        JsonNode jsonNode;
        try {
            SimpleFilterProvider provider = new SimpleFilterProvider();
            if (excludes != null) {
                for (String exclude : excludes) {
                    provider.addFilter(exclude, SimpleBeanPropertyFilter.serializeAllExcept(exclude));
                }
            }
            mapper.setFilterProvider(provider);
            mapper.setPropertyNamingStrategy(propertyNamingStrategyDefault);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String jsonStr = mapper.writeValueAsString(instance);
            jsonNode = getJsonFromStr(jsonStr);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            jsonNode = mapper.createObjectNode();
        }
        return jsonNode;
    }

    /**
     * 下划线转驼峰
     *
     * @param str 待处理字符串
     * @return 转换后的字符串
     */
    public static StringBuffer toCamel(String str) {
        //利用正则删除下划线，把下划线后一位改成大写
        Pattern pattern = Pattern.compile("_(\\w)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return toCamel(sb.toString());
    }


    /**
     * 驼峰转下划线
     *
     * @param str 待处理字符串
     * @return 转换后字符串
     */
    public static StringBuffer toUnderline(String str) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return toUnderline(sb.toString());
    }

}
