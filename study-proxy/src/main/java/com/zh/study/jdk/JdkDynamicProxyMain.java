package com.zh.study.jdk;

import sun.misc.ProxyGenerator;
import sun.reflect.misc.ReflectUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Modifier;


/**
 * JDK动态代理的底层实现
 * com.sun.proxy.$Proxy6.class为jdk动态代理生成的代理类
 */
public class JdkDynamicProxyMain {
    private static final String proxyClassNamePrefix = "$Proxy";
    public static void main(String[] args) throws Exception {
        Class<?>[] interfaces = GamePlay.class.getInterfaces();
        int accessFlags = Modifier.PUBLIC | Modifier.FINAL;
        String proxyPkg = ReflectUtil.PROXY_PACKAGE + ".";

        String proxyName = proxyPkg + proxyClassNamePrefix + "6";
        byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                proxyName, interfaces);
        String filePath = "F:\\StudyProjectWorkspace\\study-proxy\\src\\main\\java\\com\\zh\\study\\" + proxyName + ".class";
        FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
        fileOutputStream.write(proxyClassFile);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
