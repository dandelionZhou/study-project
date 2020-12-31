# 进程和线程
### 进程
### 线程
# 并发和并行
### 并发
并发是单位时间内，多个线程执行不同任务
### 并行
并行是同一时间段内，多个线程执行不同任务
# 用户线程和守护线程

# 线程的上下文切换
### 用户态和内核态

# Java线程的创建方式
+ 继承Thread
+ 实现Runnable
+ 使用FutureTask和Callable
```java
package com.zh.study.thread.base.create;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * java线程创建的三种基本方式
 * @date 2020/12/28
 */
public class CreateThreadTest {

    /**
     * 继承Thread，无返回值
     */
    static class CreateThreadByExtThread extends Thread {
        @Override
        public void run() {
            System.out.println("CreateThreadByExtThread print...not return result");
        }
    }

    /**
     * 实现Runnable
     */
    static class CreateThreadByImplRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("CreateThreadByImplRunnable print...not return result");
        }
    }

    /**
     * FutureTask + Callable创建带返回值的线程
     */
    static class CreateThreadByImplCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("CreateThreadByImplCallable print...return result");
            return "result";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new CreateThreadByExtThread().start();
        new Thread(new CreateThreadByImplRunnable()).start();
        FutureTask<String> futureTask = new FutureTask<>(new CreateThreadByImplCallable());
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
    }

}
```
# 多线程的基本API
### wait()
    java.lang.Object中的方法，阻塞当前线程。

##### 为什么设计在Object类中而不是Thread类？
    在JVM中对象的内存布局包括对象头，实例数据和填充补齐，其中对象头又包括对象运行时数据(Mark Word)和类型指针(如果对象为数组对象，则还包括数组长度),其中Mark Word中包括对象的哈希码，GC分代年龄，锁位标记，偏向线程ID等；所以每个对象都可以进行锁操作，而每个对象都继承自Object，因此设计在Object中较合理。

### notify()
     java.lang.Object中的方法，唤醒调用wait()方法而被阻塞的线程

### join()
    java.lang.Thread中的方法，阻塞主线程，直到调用此方法的线程执行完毕。

### sleep()
    java.lang.Thread中的方法，将当前线程休眠指定时间，不释放监视器锁资源。

### yield()
    
### interrupt()
    设置调用此方法线程的中断标记，并不会主动中断线程
```java
public void interrupt() {
    //安全检测
	if (this != Thread.currentThread())
		checkAccess();
    //设置中断标记
	synchronized (blockerLock) {
		Interruptible b = blocker;
		if (b != null) {
			interrupt0();   // Just to set the interrupt flag
			b.interrupt(this);
			return;
		}
	}
	interrupt0();
}
```
### interrupted()
    返回当前线程的中断状态并清除中断标记
```java
public static boolean interrupted() {
    return currentThread().isInterrupted(true);
}
```
### isInterrupted()
    返回当前线程的中断状态不清除中断标记
```java
public boolean isInterrupted() {
    return isInterrupted(false);
}
```

# 线程的状态
### NEW
    初始化，此时线程还未start
### RUNNABLE
    运行中，Thread.start()
### BLOCKED
    线程进入Synchonized同步代码块或同步方法时，获取锁失败RUNNABLE->BLOCKED；
    获取锁时，BLOCKED->RUNNABLE
### WAITING
    RUNNABLE->WAITING:
        Object.wait()
        Thread.join()
        LockSupport.park()

    WAITING->RUNNABLE:
        Object.notify()
        Object.notifyAll()
        LockSupport.unpark(thread)
    
### TIMED_WAITING
    RUNNABLE->TIME_WAITING:
        Thread.sleep(time)
        Object.wait(time)
        Thread.join(time)
        LockSupport.parkNanos(time)
        LockSupport.parkUntil(time)

    TIME_WAITING->RUNNIABLE:
        Object.notify()
        Object.notifyAll()
        LockSupport.unpark(thread)
    
### TERMINATED
    线程终止状态，线程执行完毕
### 什么是线程的虚假唤醒？
    线程在没有被其他线程调用notify(),notifyAll(),等待超时或被中断，就由挂起状态变为可运行状态，这就是虚假唤醒。

# 死锁
### 什么是死锁？
    在请求独占资源时导致环路等待而无法继续运行。
### 死锁产生的必要条件
+ 互斥条件：资源在某一时刻只能由一个线程获取
+ 请求并持有条件：线程1在拥有资源A的情况下，继续请求获取资源B
+ 不可剥夺条件：资源A由线程1获取后，其他线程无法获取，除非线程1主动释放
+ 环路等待条件：线程1拥有A后继续请求B，线程2拥有B后继续请求A，这就构成了资源的环路链。
### 如何避免死锁？
破坏至少一个构成死锁的必要条件，其中只有请求并持有和环路等待条件时可以被破坏的，即资源分配的有序性。
# Java共享变量的内存可见性问题
### JMM内存模型
    Java Memory Modal(Java内存模型)
+ 主内存
+ 工作内存

### 工作内存和主内存的交互
+ lock
+ unlock
+ read
+ load
+ use
+ assign
+ store
+ write

# ThreadLocal使用
### ThreadLocal原理
ThreadLocal内部使用当前线程的ThreadLocalMap存储当前ThreadLocal变量和值的键值对，因此每次取值都是从当前线程的工作内存中的ThreadLoclMap变量中取的，而每个线程都有自己的工作内存，因此互不影响。
### 子线程访问父线程设置的本地变量
使用InheritableThreadLocal创建本地变量，会在当前线程初始化时将父类的ThreadLocalMap复制到当前线程工作内存的ThreadLocalMap中。
### ThreadLocal内存泄漏问题？
#### Java的引用类型
+ 强引用
强引用就是我们常见的类型，如Object o = new Object(); o就是强引用，除非手动将o设为null，否则计算内存溢出了，虚拟机也不会回收强引用
+ 软引用
软引用相对于强引用来说，只有当内存空间不够时才会回收，如果回收后仍然内存不足，则会报内存溢出
+ 弱引用
无论内存够不够，GC都会回收弱引用
+ 虚引用
最弱的引用类型，随时会被回收
#### 内存泄漏的原因
```java
static class ThreadLocalMap {

	/**
	 * The entries in this hash map extend WeakReference, using
	 * its main ref field as the key (which is always a
	 * ThreadLocal object).  Note that null keys (i.e. entry.get()
	 * == null) mean that the key is no longer referenced, so the
	 * entry can be expunged from table.  Such entries are referred to
	 * as "stale entries" in the code that follows.
	 */
	static class Entry extends WeakReference<ThreadLocal<?>> {
	/** The value associated with this ThreadLocal. */
	Object value;

		Entry(ThreadLocal<?> k, Object v) {
			super(k);
			value = v;
		}
	}
}
```  
由上面代码可知，ThreadLocalMap的Entry中key是弱引用类型，也就是存放在ThreadLocalMap中的键值对，key很容易被GC回收了，但是value是强引用不会被回收，这就导致内存泄漏。如果线程的生命周期足够长，如线程池这样，那么就有可能导致内存溢出。
#### 怎么避免使用ThreadLocal内存泄漏 
调用ThreadLocal.remove()方法及时清理

# synchronized和volatile
### java指令重排
java编译器和处理器对没有数据关联的代码进行重排序
### 伪共享
CPU读取缓冲行，缓冲行可能存在多个共享变量，导致伪共享
### synchronized
synchronized是java提供的一种重量级的同步机制，其内部锁是悲观锁,排他锁,独占锁,可重入锁
+ 原子性
+ 可见性
+ 有序性
### volatile
+ 可见性
+ 有序性
### JDK1.5后对synchronized的优化
#### 偏向锁
#### 轻量级锁
#### 锁自旋(自适应自旋)
#### 锁消除
#### 锁粗化

### 锁升级(锁膨胀)
偏向锁->轻量级锁->锁自适应自旋->重量级锁

# Java中的CAS操作
### sun.misc.Unsafe
Unsafe提供了硬件级别的原子性操作
### java.util.concurrent.locks.LockSupport
内部使用Unsafe类，是实现锁和同步类的基础

# Java中锁的类型
### 悲观锁和乐观锁
### 独占锁和共享锁
### 公平锁和非公平锁
### 可重入锁
### 自旋锁

# Java中锁实现原理
### AQS(AbstractQueuedSynchronizer)抽象同步队列
##### state
##### node
##### ConditionObject
##### 独占方式实现锁
+ acquire()
通过对state进行原子性操作，判定当前线程是否获取锁资源，具体逻辑由子类实现tryAcquire();如果判定该线程获取锁失败则会将当前线程以独占方式放入AQS阻塞队列
+ acquireInterruptibly()
同acquire，只是该方法会对当前线程的中断做出响应，具体逻辑由子类实现tryAcquire()
+ release
通过对state进行原子性操作，判定当前线程是否释放锁资源，具体逻辑由子类实现tryRelease()

##### 共享方式实现锁
+ acquireShared()
+ acquireSharedInterruptibly()
+ releaseShared()

# JUC(Java.util.concurrent)中锁
### ReentrantLock
基于AQS实现的可重入的，独占锁，其中state表示可重入次数，其内部实现了公平和非公平锁
##### 公平锁和非公平锁
公平锁的体现：线程A获取锁失败进入AQS阻塞队列，线程B获取锁时，如果锁的持有者刚好释放锁，那么锁会被AQS队列中线程A获取。
非公平的体现：线程A获取锁失败进入AQS阻塞队列，线程B获取锁时，如果锁的持有者刚好释放锁，那么锁会被线程B获取，而非AQS阻塞队列中的线程A。
##### 可重入锁
当前线程如果是锁的持有者，那么不用在进行CAS获取锁的操作，直接state的值加1
##### 独占锁
即通过tryAcquire,tryAcquireInterruptibly,tryRelease来实现锁的获取和释放

### ReentrantReadWriteLock
基于AQS实现的可重入的读写锁，其中state高16表示读锁的共享个数，低16表示写锁的可重入次数
##### 读锁readLock
读锁为共享锁，其中AQS中ThreadLocalHoldCounter用于统计读锁的重入次数
##### 写锁writeLock
写锁为独占锁
##### 读锁和写锁
+ 同一个线程可以在持有写锁的前提下，继续持有读锁
+ 在写锁未被其他线程持有的情况下，读锁可以由多个线程共同持有

### StampedLock
JDK1.8提供的锁，共三种模式：独占写锁，悲观读锁，乐观读锁
##### 独占写锁
写锁只能由一个线程持有

##### 悲观读锁
认为写比读多，在操作资源前就将资源锁定，会阻塞其他线程

##### 乐观读锁
认为读比写多，仅通过

### ConcurrentLinkedQueue非阻塞队列
### 阻塞队列
##### LinkedBlockingQueue 
##### 
