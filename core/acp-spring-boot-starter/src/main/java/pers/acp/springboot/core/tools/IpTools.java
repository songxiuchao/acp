package pers.acp.springboot.core.tools;

import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public final class IpTools {

    private static final LogFactory log = LogFactory.getInstance(IpTools.class);// 日志对象

    /**
     * 获取远程客户端IP
     *
     * @param request 请求对象
     * @return IP
     */
    public static String getRemoteIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (CommonTools.isNullStr(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Citrix-Client-IP");
        }
        if (CommonTools.isNullStr(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (CommonTools.isNullStr(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (CommonTools.isNullStr(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = getRemoteRealIP(request);
        }
        if (!CommonTools.isNullStr(ipAddress) && ipAddress.length() > 15) {
            if (ipAddress.contains(",")) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 获取远程客户端真实底层IP
     *
     * @param request 请求对象
     * @return IP
     */
    private static String getRemoteRealIP(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1") || ipAddress.equals("::1")) {
            // 根据网卡取本机配置的IP
            InetAddress inet = null;
            try {
                inet = InetAddress.getLocalHost();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            ipAddress = inet != null ? inet.getHostAddress() : null;
        }
        if (!CommonTools.isNullStr(ipAddress) && ipAddress.length() > 15) {
            if (ipAddress.contains(",")) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 获取服务器所有网卡的IP
     *
     * @return 网卡对应IP
     */
    public static String[] getServerIPs() {
        String[] IPs;
        try {
            ArrayList<String> tmp_ips = new ArrayList<>();
            Enumeration<NetworkInterface> netInterfaces;
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    StringBuilder addressIP = new StringBuilder();
                    byte[] address = ips.nextElement().getAddress();
                    for (int i = 0; i < address.length; i++) {
                        if (i > 0) {
                            addressIP.append(".");
                        }
                        addressIP.append(address[i] & 0xFF);
                    }
                    tmp_ips.add(addressIP.toString());
                }
            }
            if (tmp_ips.size() == 0) {
                IPs = new String[1];
                IPs[0] = "";
            } else {
                IPs = new String[tmp_ips.size()];
                for (int i = 0; i < tmp_ips.size(); i++) {
                    IPs[i] = tmp_ips.get(i);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            IPs = new String[1];
            IPs[0] = "";
        }
        return IPs;
    }

    /**
     * 获取服务器所有网卡的IP“,”分隔
     *
     * @return 所有网卡IP
     */
    public static String getServerIPstr() {
        String IPs = "";
        String[] ips = getServerIPs();
        if (ips != null) {
            for (String ip : ips) {
                if (!CommonTools.isNullStr(IPs)) {
                    IPs += ",";
                }
                IPs += ip;
            }
        }
        return IPs;
    }

    /**
     * 获取WEB应用访问服务器的IP
     *
     * @param request 请求对象
     * @return 服务器IP
     */
    public static String getWebServerIP(HttpServletRequest request) {
        return request.getLocalAddr();
    }

    /**
     * 获取WEB应用访问服务器的物理地址
     *
     * @param request 请求对象
     * @return 服务器物理地址
     */
    public static String getMACAddress(HttpServletRequest request) {
        String ip = getWebServerIP(request);
        String mac = getMACFromIP(ip);
        if (CommonTools.isNullStr(mac)) {
            mac = CommonTools.getUuid();
        }
        return mac;
    }

    /**
     * 通过IP地址获取对应网络接口的物理地址
     *
     * @param IP 服务器IP
     * @return 服务器物理地址
     */
    private static String getMACFromIP(String IP) {
        String result = "";
        try {
            Enumeration<NetworkInterface> netInterfaces;
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                boolean isGet = false;
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    StringBuilder addressIP = new StringBuilder();
                    byte[] address = ips.nextElement().getAddress();
                    for (int i = 0; i < address.length; i++) {
                        if (i > 0) {
                            addressIP.append(".");
                        }
                        addressIP.append(address[i] & 0xFF);
                    }
                    if (addressIP.toString().equals(IP)) {
                        isGet = true;
                        break;
                    }
                }
                if (isGet) {
                    byte[] addres = ni.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    if (addres != null && addres.length > 1) {
                        sb.append(parseByte(addres[0])).append("-")
                                .append(parseByte(addres[1])).append("-")
                                .append(parseByte(addres[2])).append("-")
                                .append(parseByte(addres[3])).append("-")
                                .append(parseByte(addres[4])).append("-")
                                .append(parseByte(addres[5]));
                    }
                    result = sb.toString().toUpperCase();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = "";
        }
        return result;
    }

    /**
     * 格式化二进制
     *
     * @param b 字节
     * @return 字符串
     */
    private static String parseByte(byte b) {
        int intValue;
        if (b >= 0) {
            intValue = b;
        } else {
            intValue = 256 + b;
        }
        return Integer.toHexString(intValue);
    }
}
