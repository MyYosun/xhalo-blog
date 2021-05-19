package cn.xhalo.blog.auth.client.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Xhalo on 2019/7/24.
 */
@Slf4j
public class RequestUtil {
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        //没有附带X-Forwarded-For头信息，就直接getRemoteAddr
        if (ip == null || ip.trim().length() == 0) {
            String clientIp = request.getRemoteAddr();

            if (!clientIp.equals("0:0:0:0:0:0:0:1") && !clientIp.equals("127.0.0.1")) {
                return clientIp;
            }
            //获取到的是ipv6地址或127地址，说明是浏览器和服务器在一台机器上的情况，就转为使用获取本机真实网卡被分配到的ip
            //添加该方式只是为了方便在浏览器和服务器处于同一台机器上的时候，auth服务端获取到的客户端ip能保持一致，如果是在实际的生产环境，用户的浏览器不可能会和服务器在一台机器上，所以也就不会走到该方法。
            return getHostIp();
        }
        return getRemoteIpFromForward(ip);
    }

    public static String getHostIp() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddressEnumeration;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaceEnumeration.hasMoreElements()) {
                networkInterface = networkInterfaceEnumeration.nextElement();
                inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    inetAddress = inetAddressEnumeration.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address && !inetAddress.getHostAddress().equals("127.0.0.1")) {
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
            if (ipList.size() == 0) {
                log.error("未能获取到本机ip！");
                return null;
            }
            if (ipList.size() > 1) {
//                LOGGER.error("获取到多个本机ipV4类型的ip！系统无法确定您想要使用的ip！");
                log.warn("获取到多个本机ipV4类型的ip！系统将返回第一个ip！");
                return ipList.get(0);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList.get(0);
    }

    private static String getRemoteIpFromForward(String ip) {
        int commaOffset = ip.indexOf(',');
        if (commaOffset == -1) {
            return ip;
        }
        return ip.substring(0, commaOffset);
    }
}
