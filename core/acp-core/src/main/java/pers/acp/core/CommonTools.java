package pers.acp.core;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import pers.acp.core.exceptions.OperateException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.match.MoneyToCN;
import pers.acp.core.match.Operate;
import pers.acp.core.security.key.KeyManagement;
import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;
import pers.acp.core.tools.CommonUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public final class CommonTools {

    private static final LogFactory log = LogFactory.getInstance(CommonTools.class);

    public static void initTools() {
        log.info("default charset is : " + getDefaultCharset());
    }

    /**
     * 获取资源文件的输入流
     *
     * @param fileName 文件路径
     * @return 输入流
     */
    public InputStream getResourceInputStream(String fileName) {
        return CommonUtils.getResourceInputStream(fileName);
    }

    /**
     * 表达式变量替换
     *
     * @param varFormula 变量表达式:格式“${变量名}” 或带有变量格式的字符串
     * @param data       数据集
     * @return 目标字符串
     */
    public static String replaceVar(String varFormula, Map<String, String> data) {
        return CommonUtils.replaceVar(varFormula, data);
    }

    /**
     * 获取系统默认字符集
     *
     * @return 字符集
     */
    public static String getDefaultCharset() {
        return CommonUtils.getDefaultCharset();
    }

    /**
     * 获取配置信息
     *
     * @param key 键
     * @return 值
     */
    public static String getProperties(String key) {
        return CommonUtils.getProperties(key);
    }

    /**
     * 获取配置信息
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getProperties(String key, String defaultValue) {
        return CommonUtils.getProperties(key, defaultValue);
    }

    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    public static boolean isAbsPath(String path) {
        return CommonUtils.isAbsPath(path);
    }

    /**
     * 获取绝对路径
     *
     * @param srcPath 路径
     * @return 绝对路径
     */
    public static String getAbsPath(String srcPath) {
        return CommonUtils.getAbsPath(srcPath);
    }

    /**
     * 将相对路径格式化为绝对路径，相对于 java path
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String formatAbsPath(String path) {
        return CommonUtils.formatAbsPath(path);
    }

    /**
     * 获取webroot绝对路径
     *
     * @return webroot绝对路径
     */
    public static String getWebRootAbsPath() {
        return CommonUtils.getWebRootAbsPath();
    }

    /**
     * 获取36位全球唯一的字符串（带4个分隔符）
     *
     * @return 结果
     */
    public static String getUuid() {
        return CommonUtils.getUuid();
    }

    /**
     * 获取32位全球唯一的字符串
     *
     * @return 结果
     */
    public static String getUuid32() {
        return CommonUtils.getUuid32();
    }

    /**
     * 获取16位短uuid
     *
     * @return 结果
     */
    public static String getUuid16() {
        return CommonUtils.getUuid16();
    }

    /**
     * 获取8位短uuid
     *
     * @return 结果
     */
    public static String getUuid8() {
        return CommonUtils.getUuid8();
    }

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        return KeyManagement.getRandomString(KeyManagement.RANDOM_STR, length);
    }

    /**
     * 正则表达式匹配字符串
     *
     * @param regex  正则表达式
     * @param srcStr 待匹配字符串
     * @return true|false
     */
    public static boolean regexPattern(String regex, String srcStr) {
        return CommonUtils.regexPattern(regex, srcStr);
    }

    /**
     * 判断是否空字符串
     *
     * @param src 源字符串
     * @return 是否为空
     */
    public static boolean isNullStr(String src) {
        return CommonUtils.isNullStr(src);
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
        return CommonUtils.autoInsertString(src, length, insertString);
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
        return CommonUtils.strFillIn(str, number, flag, c);
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
        return CommonUtils.strFillIn(str, number, flag, string);
    }

    /**
     * 判断字符串是否在数组中
     *
     * @param str   源字符串
     * @param array 字符串数组
     * @return 是否存在
     */
    public static boolean strInArray(String str, String[] array) {
        return strInArray(str, array, false);
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
        return CommonUtils.strInArray(str, array, ignoreCase);
    }

    /**
     * 判断字符串是否在列表中
     *
     * @param str       源字符串
     * @param arrayList 字符串列表
     * @return 是否存在
     */
    public static boolean strInList(String str, List<String> arrayList) {
        return strInList(str, arrayList, false);
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
        return CommonUtils.strInList(str, arrayList, ignoreCase);
    }

    /**
     * 获取指定格式的时间字符串
     *
     * @param date       Date实例
     * @param dateFormat 格式
     * @return 格式化的时间格式
     */
    public static String getDateTimeString(Date date, String dateFormat) {
        return CommonUtils.getDateTimeString(date, dateFormat);
    }

    /**
     * 获取当前日期
     *
     * @return 日期字符串
     */
    public static String getNowString() {
        return CommonUtils.getNowString();
    }

    /**
     * 获取当前时间
     *
     * @return 日期时间字符串
     */
    public static String getNowTimeString() {
        return CommonUtils.getNowTimeString();
    }

    /**
     * 计算四则运算表达式结果
     *
     * @param caculateStr 表达式字符串
     * @return 结果
     */
    public static double doCaculate(String caculateStr) throws OperateException {
        Operate operate = new Operate();
        return operate.caculate(caculateStr);
    }

    /**
     * 金额转换为汉语中人民币的大写
     *
     * @param money 金额
     * @return 大写字符串
     */
    public static String getMoneyForCN(double money) {
        return MoneyToCN.moneyToCNMontrayUnit(money);
    }

    /**
     * 在线程池中执行任务
     *
     * @param threadPool 线程池实例
     * @param task       线程池任务
     * @return 执行结果
     */
    public static Object excuteTaskInThreadPool(ThreadPoolService threadPool, BaseThreadTask task) {
        return CommonUtils.excuteTaskInThreadPool(threadPool, task);
    }

    /**
     * 字符串转JSON对象
     *
     * @param src 字符串
     * @return json对象
     */
    public static JsonNode getJsonFromStr(String src) {
        return CommonUtils.getJsonFromStr(src);
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj json对象（JsonNode）
     * @param cls     目标类
     * @return 目标对象
     */
    public static <T> T jsonToObject(JsonNode jsonObj, Class<T> cls) {
        return CommonUtils.jsonToObject(jsonObj, cls);
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
        return CommonUtils.jsonToObject(propertyNamingStrategy, jsonObj, cls);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @return json对象
     */
    public static JsonNode objectToJson(Object instance) {
        return CommonUtils.objectToJson(instance);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @param excludes 排除的实体成员
     * @return json对象
     */
    public static JsonNode objectToJson(Object instance, String[] excludes) {
        return CommonUtils.objectToJson(instance, excludes);
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
        return CommonUtils.objectToJson(propertyNamingStrategy, instance, excludes);
    }

    /**
     * 下划线转驼峰
     *
     * @param str 待处理字符串
     * @return 转换后的字符串
     */
    public static String toCamel(String str) {
        return CommonUtils.toCamel(str).toString();
    }


    /**
     * 驼峰转下划线
     *
     * @param str 待处理字符串
     * @return 转换后字符串
     */
    public static String toUnderline(String str) {
        return CommonUtils.toUnderline(str).toString();
    }

    /**
     * 删除文件
     *
     * @param file   待删除文件
     * @param isSync 是否异步删除
     */
    public static void doDeleteFile(final File file, boolean isSync) {
        String waitTimeStr = getProperties("deletefile.waittime");
        if (isNullStr(waitTimeStr)) {
            waitTimeStr = "1200000";
        }
        if (isSync && Long.valueOf(waitTimeStr) >= 0) {
            long waittime = Long.valueOf(waitTimeStr);
            doDeleteFile(file, true, waittime);
        } else {
            doDeleteFile(file, false, 0);
        }
    }

    /**
     * 删除文件
     *
     * @param file     待删除文件
     * @param isSync   是否异步删除
     * @param waitTime 异步删除等待时间
     */
    public static void doDeleteFile(final File file, boolean isSync, long waitTime) {
        FileDelete.doDeleteFile(file, isSync, waitTime);
    }

    /**
     * 删除文件夹
     *
     * @param dir 将要删除的文件目录
     */
    public static void doDeleteDir(File dir) {
        boolean result = FileDelete.doDeleteDir(dir);
        if (result) {
            log.info("delete fold [" + dir.getAbsolutePath() + "] success!");
        } else {
            log.info("delete fold [" + dir.getAbsolutePath() + "] failed!");
        }
    }

    private static class FileDelete extends Thread {

        private static final LogFactory log = LogFactory.getInstance(FileDelete.class);

        private final File file;

        /**
         * 删除文件等待时间，单位:毫秒，默认1200000毫秒（20分钟）
         */
        private long waitTime = 1200000;

        private FileDelete(final File file) {
            this.file = file;
            this.setDaemon(true);
        }

        private FileDelete(final File file, long waitTime) {
            this(file);
            this.waitTime = waitTime;
        }

        /**
         * 删除文件
         *
         * @param file   待删除的文件
         * @param isSync 是否异步删除
         */
        static void doDeleteFile(File file, boolean isSync) {
            doDeleteFile(file, isSync, 0);
        }

        /**
         * 删除文件
         *
         * @param file     待删除的文件
         * @param isSync   是否异步删除
         * @param waitTime 异步删除等待时间
         */
        static void doDeleteFile(File file, boolean isSync, long waitTime) {
            if (isSync) {
                if (waitTime == 0) {
                    new FileDelete(file).start();
                } else {
                    new FileDelete(file, waitTime).start();
                }
            } else {
                try {
                    if (doDeleteDir(file)) {
                        log.info("delete file [" + file.getAbsolutePath() + "] success!");
                    } else {
                        log.info("delete file [" + file.getAbsolutePath() + "] failed!");
                    }
                } catch (Exception e) {
                    log.error("delete file Exception:" + e.getMessage(), e);
                }
            }
        }

        /**
         * 删除文件夹
         *
         * @param dir 将要删除的文件目录
         * @return true|false
         */
        static boolean doDeleteDir(File dir) {
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    if (children != null) {
                        for (String aChildren : children) {
                            boolean success = doDeleteDir(new File(dir, aChildren));
                            if (!success) {
                                return false;
                            }
                        }
                    }
                }
                return dir.delete();
            } else {
                return true;
            }
        }

        @Override
        public void run() {
            try {
                log.info("ready delete file [" + file.getAbsolutePath() + "],waitting " + (waitTime) / 1000 + " seconds");
                FileDelete.sleep(waitTime);
                if (doDeleteDir(file)) {
                    log.info("delete file [" + file.getAbsolutePath() + "] success!");
                } else {
                    log.info("delete file [" + file.getAbsolutePath() + "] failed!");
                }
            } catch (Exception e) {
                log.error("delete file Exception:" + e.getMessage(), e);
            }
        }
    }

}
