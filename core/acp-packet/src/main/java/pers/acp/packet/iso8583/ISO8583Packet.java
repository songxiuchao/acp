package pers.acp.packet.iso8583;

import pers.acp.core.log.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 简单介绍下ISO8583。
 * 这个东西说白了就是一种数据结构。我们定义一种规则把一堆东西放进去，再按照规则
 * 把数据正确拿出来。这就是报文的实质。
 * <p>
 * ISO8583报文的结构是：前面有16字节（128位）位图数据，后面就是数据。
 * 报文最多有128个域（字段）。具体的一个报文不会有这么多，一般是几个域。
 * 有哪几个就记录在位图中。而且域有定长和变长之分。
 * 这些都是事先定义好的，具体可以看我写的properties定义文件.
 * <p>
 * 位图转化成01字符串就是128个，如果某一位是1，代表这个域有值，然后按照properties定义的规则取值。
 * 如果是0，则这个域没有值。
 * <p>
 * 再说定长和变长。
 * 定长域(定长比较好理解，一个字段规定是N位，那么字段值绝对不能超过N位，不足N位就在后面补空格)
 * 变长域(变长域最后组装成的效果：例如变长3位，定义var3，这里的3是指长度值占3位，字段值是123456，最后结果就是006123456)
 * 注意（变长的长度按照域值得字节长度计算，而不是按照域值字符串长度算！）
 * <p>
 * 从网上不难找到ISO8583报文的介绍，这里就不多说了。
 * 但是具体解析和组装的代码还真不好找，所以本人就写了一个让刚接触ISO8583报文的人更好入门。
 * <p>
 * <p>
 * <p>
 * 解析的容器，我使用了Map，具体到工作中，还是要换成其他的容器的。
 * 报文定义说明：iso8583.properties
 * 例如
 * FIELD031 = string,10
 * FIELD032 = string,VAR2
 * <p>
 * FIELD031是定长，长度是10
 * FIELD032是变长，长度值占2位，也就是说长度值最大99，也就是域值最大长度99.
 *
 * @author zhangbin
 */
public class ISO8583Packet {

    private static final LogFactory log = LogFactory.getInstance(ISO8583Packet.class);

    private static String packetEncoding = "UTF-8";//报文编码 UTF-8 GBK

    private static Map<String, String> map8583Definition = null;// 8583报文128域定义器

    static {
        try {
            ISO8583FieldProperties iso8583FieldProperties = ISO8583FieldProperties.getInstance();
            if (iso8583FieldProperties != null) {
                map8583Definition = new HashMap<>();
                iso8583FieldProperties.forEach((key, value) -> map8583Definition.put(String.valueOf(key), String.valueOf(value)));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 8583报文初始位图:128位01字符串
     *
     * @return 位图
     */
    private static String getInitBitMap() {
        return "10000000" + "00000000" + "00000000" + "00000000"
                + "00000000" + "00000000" + "00000000" + "00000000"
                + "00000000" + "00000000" + "00000000" + "00000000"
                + "00000000" + "00000000" + "00000000" + "00000000";
    }

    /**
     * 组装8583报文
     *
     * @param filedMap 域值
     */
    public static byte[] make8583(TreeMap<String, String> filedMap) {
        byte[] whoe8583;
        if (filedMap == null) {
            return null;
        }
        try {
            String bitMap128 = getInitBitMap();//获取初始化的128位图
            //按照8583定义器格式化各个域的内容
            Map<String, Object> all = formatValueTo8583(filedMap, bitMap128);
            // 获取上送报文内容
            whoe8583 = getWhole8583Packet(all);
            return whoe8583;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取完整的8583报文体（128域）
     *
     * @param all 内容
     * @return 字节数组
     */
    private static byte[] getWhole8583Packet(Map<String, Object> all) {
        if (all == null || all.get("formatedFiledMap") == null || all.get("bitMap128") == null) {
            return null;
        }
        try {
            String bitMap128 = (String) all.get("bitMap128");
            // 128域位图二进制字符串转16位16进制
            byte[] bitmaps = get16BitByteFromStr(bitMap128);

            TreeMap pacBody = (TreeMap) all.get("formatedFiledMap");
            StringBuilder last128 = new StringBuilder();
            Iterator it = pacBody.keySet().iterator();
            for (; it.hasNext(); ) {
                String key = (String) it.next();
                String value = (String) pacBody.get(key);
                last128.append(value);
            }
            byte[] bitContent = last128.toString().getBytes(packetEncoding);//域值

            //组装
            byte[] package8583;
            package8583 = ISO8583Packet.arrayApend(null, bitmaps);
            package8583 = ISO8583Packet.arrayApend(package8583, bitContent);

            return package8583;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static Map<String, Object> formatValueTo8583(TreeMap<String, String> filedMap, String bitMap128) {
        Map<String, Object> all = new HashMap<>();
        TreeMap<String, String> formatedFiledMap = new TreeMap<>();//格式化结果
        if (filedMap != null) {
            Iterator it = filedMap.keySet().iterator();
            for (; it.hasNext(); ) {
                String fieldName = (String) it.next();//例如FIELD005
                String fieldValue = filedMap.get(fieldName);//字段值
                try {
                    if (fieldValue == null) {
                        log.error("报文域 {" + fieldName + "}为空值");
                        return null;
                    }
                    //将域值编码转换，保证报文编码统一
                    fieldValue = new String(fieldValue.getBytes(packetEncoding), packetEncoding);
                    // 数据域名称FIELD开头的为128域
                    if (fieldName.startsWith("FIELD")) {
                        String fieldNo = fieldName.substring(5, 8);//例如005
                        // 组二进制位图串
                        bitMap128 = change16bitMapFlag(fieldNo, bitMap128);

                        // 获取域定义信息
                        String[] fieldDef = map8583Definition.get("FIELD" + fieldNo).split(",");
                        String defLen = fieldDef[1];//长度定义,例20
                        boolean isFixLen = true;//是否定长判断

                        if (defLen.startsWith("VAR")) {//变长域
                            isFixLen = false;
                            defLen = defLen.substring(3);//获取VAR2后面的2
                        }
                        int fieldLen = fieldValue.getBytes(packetEncoding).length;//字段值得实际长度

                        // 判断是否为变长域
                        if (!isFixLen) {// 变长域(变长域最后组装成的效果：例如变长3位，定义var3，这里的3是指长度值占3位，字段值是123456，最后结果就是006123456)
                            int defLen1 = Integer.valueOf(defLen);
                            if (String.valueOf(fieldLen).length() > (10 * defLen1)) {
                                log.error("字段" + fieldName + "的数据定义长度的长度为" + defLen + "位,长度不能超过" + (10 * defLen1));
                                return null;
                            } else {
                                //将长度值组装入字段
                                fieldValue = getVaryLengthValue(fieldValue, defLen1) + fieldValue;
                            }
                        } else {//定长域(定长比较好理解，一个字段规定是N位，那么字段值绝对不能超过N位，不足N位就在后面补空格)
                            int defLen2 = Integer.valueOf(defLen);
                            if (fieldLen > defLen2) {
                                log.error("字段" + fieldName + "的数据定义长度为" + defLen + "位,长度不能超过" + defLen);
                                return null;
                            } else {
                                fieldValue = getFixFieldValue(fieldValue, defLen2);//定长处理
                            }
                        }
                        log.info("组装后报文域 {" + fieldName + "}==" + fieldValue + "==,域长度:" + fieldValue.getBytes(packetEncoding).length);
                    }

                    // 返回结果赋值
                    if (filedMap.containsKey(fieldName)) {
                        if (formatedFiledMap.containsKey(fieldName)) {
                            formatedFiledMap.remove(fieldName);
                        }
                        formatedFiledMap.put(fieldName, fieldValue);
                    } else {
                        log.error(fieldName + "配置文件中不存在!");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }//end for
        }
        all.put("formatedFiledMap", formatedFiledMap);
        all.put("bitMap128", bitMap128);
        return all;
    }


    /**
     * 解析8583报文
     *
     * @param content8583 报文内容
     */
    public static Map<String, String> analyze8583(byte[] content8583) {
        TreeMap<String, String> filedMap = new TreeMap<>();
        try {
            // 取位图
            byte[] bitMap16byte = new byte[16];
            //记录当前位置,从位图后开始遍历取值
            int pos = 16;
            String bitMap128Str;
            if (Integer.toBinaryString(content8583[0] >>> 7).substring(24).equalsIgnoreCase("1")) {
                System.arraycopy(content8583, 0, bitMap16byte, 0, 16);
            } else {
                bitMap16byte = new byte[8];
                pos = 8;
                System.arraycopy(content8583, 0, bitMap16byte, 0, 8);
            }
            // 16位图转2进制位图128位字符串
            bitMap128Str = get16BitMapStr(bitMap16byte);

            // 遍历128位图，取值。注意从FIELD002开始
            for (int i = 1; i < bitMap128Str.length(); i++) {
                String filedValue;//字段值
                String filedName = "FIELD" + getNumThree((i + 1));//FIELD005

                if (bitMap128Str.charAt(i) == '1') {
                    // 获取域定义信息
                    String[] fieldDef = map8583Definition.get(filedName).split(",");
                    String defLen = fieldDef[1];//长度定义,例20
                    boolean isFixLen = true;//是否定长判断

                    if (defLen.startsWith("VAR")) {//变长域
                        isFixLen = false;
                        defLen = defLen.substring(3);//获取VAR2后面的2
                    }
                    // 截取该域信息
                    if (!isFixLen) {//变长域
                        int defLen1 = Integer.valueOf(defLen);//VAR2后面的2
                        String realLen1 = new String(content8583, pos, defLen1, packetEncoding);//报文中实际记录域长,例如16,023
                        int realAllLen = defLen1 + Integer.valueOf(realLen1);//该字段总长度（包括长度值占的长度）
//						filedValue = new String(content8583, pos+defLen1, Integer.valueOf(realLen1), packetEncoding);
                        byte[] filedValueByte = new byte[Integer.valueOf(realLen1)];
                        System.arraycopy(content8583, pos + defLen1, filedValueByte, 0, filedValueByte.length);
                        filedValue = new String(filedValueByte, packetEncoding);
                        pos += realAllLen;//记录当前位置
                    } else {//定长域
                        int defLen2 = Integer.valueOf(defLen);//长度值占的位数
                        filedValue = new String(content8583, pos, defLen2, packetEncoding);
                        pos += defLen2;//记录当前位置
                    }
                    filedMap.put(filedName, filedValue);
                }
            }//end for
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return filedMap;
    }

    /**
     * 复制字符
     *
     * @param str   源字符串
     * @param count 数量
     * @return 目标字符串
     */
    private static String strCopy(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 将setContent放入set（考虑到数组越界）
     *
     * @param set        字节数组
     * @param setContent 字节数组
     * @return 字节数组
     */
    private static byte[] setToByte(byte[] set, byte[] setContent) {
        byte[] res = new byte[set.length];
        if (setContent != null) {
            if (set.length >= setContent.length) {
                System.arraycopy(setContent, 0, res, 0, setContent.length);
            }
        }
        return res;
    }

    public static byte[] setToByte(byte[] set, String setContentStr) {
        byte[] res = new byte[set.length];
        byte[] setContent;
        try {
            setContent = setContentStr.getBytes(packetEncoding);
            res = setToByte(res, setContent);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

    private static String getPacketLen(int len) {
        String res;
        String lenStr = String.valueOf(len);
        int lenC = 4 - lenStr.length();
        res = strCopy("0", lenC) + lenStr;
        return res;
    }

    public static String getPacketLen(String lenStr) {
        String res = "";
        if (lenStr != null) {
            res = getPacketLen(Integer.valueOf(lenStr));
        }
        return res;
    }


    /**
     * 返回a和b的组合,实现累加功能
     *
     * @param a 字节数组
     * @param b 字节数组
     * @return 字节数组
     */
    private static byte[] arrayApend(byte[] a, byte[] b) {
        int a_len = (a == null ? 0 : a.length);
        int b_len = (b == null ? 0 : b.length);
        byte[] c = new byte[a_len + b_len];
        if (a_len == 0 && b_len == 0) {
            return null;
        } else if (a_len == 0) {
            System.arraycopy(b, 0, c, 0, b.length);
        } else if (b_len == 0) {
            System.arraycopy(a, 0, c, 0, a.length);
        } else {
            System.arraycopy(a, 0, c, 0, a.length);
            System.arraycopy(b, 0, c, a.length, b.length);
        }
        return c;
    }


    /**
     * 改变128位图中的标志为1
     *
     * @param fieldNo 域编号
     * @param res     res
     * @return result
     */
    private static String change16bitMapFlag(String fieldNo, String res) {
        int indexNo = Integer.parseInt(fieldNo);
        res = res.substring(0, indexNo - 1) + "1" + res.substring(indexNo);
        return res;
    }


    /**
     * 位图操作
     * <p>
     * 把16位图的字节数组转化成128位01字符串
     *
     * @param bitMap16 位图
     * @return 字符串
     */
    private static String get16BitMapStr(byte[] bitMap16) {
        StringBuilder bitMap128 = new StringBuilder();
        // 16位图转2进制位图128位字符串
        for (byte aBitMap16 : bitMap16) {
            int bc = aBitMap16;
            bc = (bc < 0) ? (bc + 256) : bc;
            String bitnaryStr = Integer.toBinaryString(bc);//二进制字符串
            // 左补零，保证是8位
            String rightBitnaryStr = strCopy("0", Math.abs(8 - bitnaryStr.length())) + bitnaryStr;//位图二进制字符串
            // 先去除多余的零，然后组装128域二进制字符串
            bitMap128.append(rightBitnaryStr);
        }
        return bitMap128.toString();
    }

    /**
     * 位图操作
     * <p>
     * 把128位01字符串转化成16位图的字节数组
     *
     * @param str_128 字符串
     * @return 字节数组
     */
    private static byte[] get16BitByteFromStr(String str_128) {
        byte[] bit16 = new byte[16];
        try {
            if (str_128 == null || str_128.length() != 128) {
                return null;
            }
            // 128域位图二进制字符串转16位16进制
            byte[] tmp = str_128.getBytes(packetEncoding);
            int weight;//权重
            byte[] strout = new byte[128];
            int i, j, w = 0;
            for (i = 0; i < 16; i++) {
                weight = 0x0080;
                for (j = 0; j < 8; j++) {
                    strout[i] += ((tmp[w]) - '0') * weight;
                    weight /= 2;
                    w++;
                }
                bit16[i] = strout[i];
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return bit16;
    }


    /**
     * 从完整的8583报文中获取位图（16字节数组）
     *
     * @param packet 字节数组
     * @return 字节数组
     */
    private static byte[] getPacketHeaderMap(byte[] packet) {
        byte[] packet_header_map = new byte[16];
        if (packet == null || packet.length < 16) {
            return null;
        }
        System.arraycopy(packet, 0, packet_header_map, 0, 16);
        return packet_header_map;
    }

    /**
     * 从完整的8583报文中获取16位图，转化成128位的01字符串
     *
     * @param content8583 字节数组
     * @return 字符串
     */
    public static String get16BitMapFrom8583Byte(byte[] content8583) {
        // 取位图
        byte[] bitMap16 = getPacketHeaderMap(content8583);
        if (bitMap16 != null) {
            // 16位图转2进制位图128位字符串
            return get16BitMapStr(bitMap16);
        } else {
            return null;
        }
    }


    //返回字段号码，例如005
    private static String getNumThree(int i) {
        String len;
        String iStr = String.valueOf(i);
        len = strCopy("0", 3 - iStr.length()) + iStr;
        return len;
    }

    /**
     * 获取字符串变长值
     *
     * @param valueStr 值
     * @param defLen   例如getFixLengthValue("12345678", 2)返回08
     *                 例如getFixLengthValue("12345678", 3)返回008
     *                 <p>
     *                 注意变长长度的计算：
     *                 长度的判断使用转化后的字节数组长度，因为中文在不同的编码方式下，长度是不同的，GBK是2，UTF-8是3，按字符创长度算就是1.
     *                 解析报文是按照字节来解析的，所以长度以字节长度为准，防止中文带来乱码。
     *                 <p>
     *                 比如一个变长域:aa索隆bb，如果按照字符串计算长度那么就是6，最后是06aa索隆bb。
     *                 这样在解析时按照字节解析长度就乱了，因为按照GBK字节解析，一个汉字占2，按照UTF-8解析，一个汉字占3.
     *                 所以在计算时必须按照字节长度为准！按照我们设置的UTF-8编码结果就是10aa索隆bb.
     *                 这样在解析时长度正好是10，也就不会乱了。
     * @return 结果
     */
    private static String getVaryLengthValue(String valueStr, int defLen) {
        return getVaryLengthValue(valueStr, defLen, packetEncoding);
    }

    private static String getVaryLengthValue(String valueStr, int defLen, String encoding) {
        String fixLen = "";
        try {
            if (valueStr == null) {
                return strCopy("0", defLen);
            } else {
                byte[] valueStrByte;
                //这里最好指定编码，不使用平台默认编码
                if (encoding == null || encoding.trim().equals("")) {
                    valueStrByte = valueStr.getBytes();
                } else {
                    valueStrByte = valueStr.getBytes(encoding);
                }
                //长度的判断使用转化后的字节数组长度，因为中文在不同的编码方式下，长度是不同的，GBK是2，UTF-8是3，按字符创长度算就是1.
                //解析报文是按照字节来解析的，所以长度以字节长度为准，防止中文带来乱码
                if (valueStrByte.length > (10 * defLen)) {
                    return null;
                } else {
                    int len = valueStrByte.length;//字段实际长度
                    String len1 = String.valueOf(len);
                    fixLen = strCopy("0", (defLen - len1.length())) + len1;
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return fixLen;
    }

    /**
     * 将字段值做定长处理，不足定长则在后面补空格
     *
     * @param valueStr 值
     * @param defLen   定义长度
     * @return 结果
     */
    private static String getFixFieldValue(String valueStr, int defLen) {
        return getFixFieldValue(valueStr, defLen, packetEncoding);
    }

    private static String getFixFieldValue(String valueStr, int defLen, String encoding) {
        String fixLen = "";
        try {
            if (valueStr == null) {
                return strCopy(" ", defLen);
            } else {
                byte[] valueStrByte;
                //这里最好指定编码，不使用平台默认编码
                if (encoding == null || encoding.trim().equals("")) {
                    valueStrByte = valueStr.getBytes();
                } else {
                    valueStrByte = valueStr.getBytes(encoding);
                }
                //长度的判断使用转化后的字节数组长度，因为中文在不同的编码方式下，长度是不同的，GBK是2，UTF-8是3，按字符创长度算就是1.
                //解析报文是按照字节来解析的，所以长度以字节长度为准，防止中文带来乱码
                if (valueStrByte.length > defLen) {
                    return null;
                } else {
                    fixLen = valueStr + strCopy(" ", defLen - valueStrByte.length);
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }

        return fixLen;
    }


    public static String getPacketEncoding() {
        return packetEncoding;
    }

    public static void setPacketEncoding(String packetEncoding) {
        ISO8583Packet.packetEncoding = packetEncoding;
    }

    public static Map getMap8583Definition() {
        return map8583Definition;
    }

    public static void setMap8583Definition(Map<String, String> map8583Definition) {
        ISO8583Packet.map8583Definition = map8583Definition;
    }

}