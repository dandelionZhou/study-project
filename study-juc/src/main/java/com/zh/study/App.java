package com.zh.study;

import com.zh.study.thread.base.ThreadTest01;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Hello world!
 *
 */

public class App 
{

    static class MyClassLoader extends ClassLoader {

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
            InputStream is = getClass().getResourceAsStream(fileName);
            if (is == null) {
                return super.loadClass(name);
            }

            byte[] b = null;
            try {
                b = new byte[is.available()];
                is.read(b);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return defineClass(name, b, 0, b.length);
        }
    }

    static {
        i = 0;
        //System.out.println(i);
    }

    static int i;

    public static void main( String[] args ) throws ClassNotFoundException, InterruptedException {
        MyClassLoader classLoader = new MyClassLoader();
        Object o = classLoader.loadClass("com.zh.study.thread.base.ThreadTest01");
        System.out.println(((Class) o).getClassLoader());
        System.out.println(o instanceof com.zh.study.thread.base.ThreadTest01);
        System.out.println( "Hello World!" );
        System.out.println(1 << 16);
        System.out.println(1 & 65535);
    }
}
