package com.zh.study.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * sun.misc.Unsafe:
 *  提供了硬件级别的原子性操作
 * 主要方法：
 * 1.public native long objectFieldOffset(Field var1);获取指定变量在所属类中的内存偏移地址
 * 2.public native long getLongVolatile(Object var1, long var2);获取对象var1中内存偏移量为var2的变量对应的volatile语义的值
 * 3.public native void putLongVolatile(Object var1, long var2, long var4);将对象var1中内存偏移量为var2的变量值设置为var4
 * 4.public native void park(boolean var1, long var2);阻塞当前线程
 *  var1等于false且var2等于0时表示一直阻塞
 *  var1等于true且var2大于0时表示在当前时间累加var2后当前线程会被唤醒
 * 5.public native void unpark(Object var1);唤醒调用park()后阻塞的线程
 *
 * Unsafe做了限制，不能直接调用：
 *  if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
 *      throw new SecurityException("Unsafe");
 *   } else {
 *     return theUnsafe;
 *  }
 * java.lang.ExceptionInInitializerError
 * Caused by: java.lang.SecurityException: Unsafe
 *
 * 万能的反射调用Unsafe
 *
 * @date 2020/12/16
 */
public class UnsafeTest {
    //static final Unsafe unsafe = Unsafe.getUnsafe();
    static Unsafe unsafe = null;
    static long stateOffset = 0L;
    private volatile long state;
    static {
        try {
            //stateOffset = unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("state"));
            Field field = Unsafe.class.getDeclaredField("theUnsafe");

            //设置可存取
            field.setAccessible(true);

            //获取该变量的值
            unsafe = (Unsafe) field.get(null);

            stateOffset = unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("state"));

        } catch (Exception e) {
            System.out.println("...");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UnsafeTest unsafeTest = new UnsafeTest();
        //对象在内存中的中的原始值 0
        System.out.println(unsafe.getLongVolatile(unsafeTest, stateOffset));
        //通过CAS比较-更新内存中指定偏移量（该变量）的值 0 -> 1
        System.out.println(unsafe.compareAndSwapLong(unsafeTest, stateOffset, 0, 1));
        //java.lang.ExceptionInInitializerError
        //CAS更新成功后，再次获取该内存地址变量的值为1
        System.out.println(unsafe.getLongVolatile(unsafeTest, stateOffset));
        //Caused by: java.lang.SecurityException: Unsafe

        //false,0表示一直阻塞
        //unsafe.park(false, 0);
        System.out.println(System.currentTimeMillis() + " before");
        //unsafe.park(false, 2000);
        unsafe.park(true, 10000);
        System.out.println(System.currentTimeMillis() + " after");
    }
}
