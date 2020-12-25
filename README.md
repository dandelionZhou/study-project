# study-project
# CAS(Compare and Swap)
    JDK提供的非阻塞原子性操作，通过硬件保证比较-更新操作的原子性。

### boolean compareAndSwapLong(Object obj, long offset, long expect, long update);
#### obj：对象内存位置
#### offset：对象中变量的内存偏移量
#### expect：期望中内存的值
#### update：新的值
    如果对象中内存偏移量为offset的变量的值等于期望值expect，那么将该对象的内存偏移量为offset的变量的值更新为新的值update。

### CAS操作的ABA问题
    变量X值为A，在线程1获取变量X的值之前，线程2使用CAS修改变量X的值为B，只有又通过CAS修改变量X的值为A，
    之后线程1通过CAS时X的值是A，但却不是线程1获取时的A。

    JDK中的AtomicStampedReference类给每个变量的状态值都配备了时间戳，避免了ABA问题的产生。

# sun.misc.Unsafe()
### JDK的rt.jar包中的Unsafe类提供了硬件级别的原子性操作

1.getUnsafe() JDK对Unsafe类使用的限制

```java
@CallerSensitive
public static Unsafe getUnsafe() {
     Class var0 = Reflection.getCallerClass();
    //限制Unsfafe类只能在rt.jar下使用
    if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
        throw new SecurityException("Unsafe");
    } else {
        return theUnsafe;
    }
}
```
2.public native long objectFieldOffset(Field var1);获取变量在所属类的内存偏地址
```java
static{
    try{
        //获取AtomicLong类中value的内存偏移地址
        offset = objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
    } catch(Exception ex) { throw new Error(ex);}
}
```
3.public native int arrayBaseOffset(Class<?> var1);

    获取数组中第一个元素的地址

4.public native int arrayIndexScale(Class<?> var1);

    获取数组中一个元素占用的字节

5.public final native boolean compareAndSwapLong(Object obj, long offset, long expect, long newVal);

    比较obj对象中内存偏移量为offset的变量的值是否与expect相等，
    若相等则将变量值更新为newVal，返回true；否则返回false。

6.public native long getLongVolatile(Object obj, long offset);

    获取对象obj中内存偏移量为offset的变量的值，支持volatile。

7.public native void putLongVolatile(Object obj, long offset, long newVal);

    设置对象obj中内存偏移量为offset的变量的值为newVal，支持volatile。

8.public native void putOrderedLong(Object obj, long offset, long newVal);

    这是一个有延迟的putLongVolatile方法，并且不保证值修改时对其他线程立即可见。只有变量使用volatile修饰
    且预计会被意外修改时才使用。

9.public native void park(boolean isAbsolute, long time);

    阻塞当前线程，其中：
    park(false, 0)表示一直阻塞；time大于0，表示线程在等待指定的time后会被唤醒。
    park(false, 2000)表示一直阻塞，直到2000ms后线程会被唤醒
    park(true, 0)表示不阻塞
    park(true, 2000)表示阻塞，直到当前时间+2000ms这段时间内会被唤醒

10.public native void unpark(Object var1);

    唤醒调用park后阻塞的线程。

11.public final long getAndSetLong(Object obj, long offset, long value);
```java
public final long getAndSetLong(Object obj, long offset, long value) {
    long expect;
    do {
        //获取变量在内存中的值
        expect = this.getLongVolatile(obj, offset);
        //循环尝试CAS更新变量值为value，直到更新成功
    } while(!this.compareAndSwapLong(obj, offset, expect, value));
    return var6;
}
```

12.public final long getAndAddLong(Object var1, long var2, long var4);
```java
public final long getAndAddLong(Object obj, long offset, long addVal) {
    long var6;
    do {
        //获取变量在内存中的值
        val = this.getLongVolatile(obj, offset);
        //循环尝试CAS更新变量值为val + addVal，直到更新成功
    } while(!this.compareAndSwapLong(obj, offset, var6, val + addVal));

     return var6;
}
```
    
### 通过反射使用Unsafe类
```java
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
        unsafe.park(false, 2000);
        System.out.println(System.currentTimeMillis() + " after");
    }
}
```

# LockSupport(用于创建锁和其他同步类)
    LockSupport是使用Unsafe类实现的工具类，是创建锁和其他同步类的基础。
    LockSupport类与每个使用的线程都会关联一个许可证。

1.public static void park(Object blocker);
#####  阻塞没有持有许可证的当前线程
 ```java
 /**
  * # 调用park方法的线程如果已经有许可证，则调用LockSupport.park()会立即返回；
  * # 调用park方法的线程没有许可证，那么会被阻塞挂起
  */
public static void park() {
    UNSAFE.park(false, 0L);
}
 ```
##### 如果其他线程调用了因park()而阻塞挂起的线程的interrupt()方法，设置了中断标志或被虚假唤醒，被阻塞的线程也会被唤醒，但是不会抛出InterruptedException异常。
```java
public static void main(String[] args) throws InterruptedException {

    Thread thread = new Thread(() -> {
        System.out.println("start....");
        //调用LockSupport.park()方法阻塞挂起当前线程
        LockSupport.park();
        System.out.println("end......");
    }, "ThreadA");

    thread.start();
    TimeUnit.SECONDS.sleep(2);
    //该线程已被阻塞挂起，调用该线程interrupt()方法设置中断标志,该线程也会被唤醒
    thread.interrupt();
    //没有抛InterruptedException异常
    //LockSupport.unpark(thread);
    TimeUnit.SECONDS.sleep(1);
    //重新park后，线程也需要重新持有许可证才能不被阻塞
    //LockSupport.park(thread);
    System.out.println("main is over......");
}
```
   
2.public static void unpark(Thread thread);

    当线程调用unpark()时，会让线程持有与LockSupport类关联的许可证；若该线程因为之前调用park()方法而阻塞，该线程会被唤醒。

```java
package com.zh.study.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport.unpark(thread);
 * 给thread与LockSupport关联的许可证，如果线程之前被park()阻塞挂起，那么该线程会被唤醒.
 * @date 2020/12/18
 */
public class LockUnparkTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start...");

        Thread thread = new Thread(() -> {
            System.out.println("threadA start...");
            //主线程休眠3秒，因此该线程此时因该没有获得与LockSupport关联的许可证
            //因此会阻塞挂起
            LockSupport.park();
        }, "ThreadA");

        // LockSupport.unpark(thread);
        thread.start();
        //TimeUnit.SECONDS.sleep(3);
        //此时调用
        LockSupport.unpark(thread);
        System.out.println("main over");
        Thread.interrupted();
    }
}
```

# AbstractQueuedSynchronizer(AQS,抽象同步队列)
AbstractQueuedSynchronizer抽象同步队列，是并发包中同步锁实现的基础。它是一个FIFO(先进先出)队列，其内部结构如下：

## state
线程状态值，通过getState,setState,compareAndSetState函数修改其值，Java并发通过定义state不同的含义，实现不同的锁类型，如ReentrantLock中state表示锁可重入的次数、ReentrantReadWriteLock中state高16位表示读锁状态低16位表示写锁状态、Semaphore中state表示当前可用信号的个数、CountDownlatch中state标识当前计数器的值。

对于AQS来说，线程同步的关键是对state进行操作，根据state是否属于一个线程，操作线程的方式分为独占方式和共享方式。

### protected final int getState
```java
/**
 * Returns the current value of synchronization state.
 * This operation has memory semantics of a {@code volatile} read.
 * 获取同步状态state的值，具有volatile语义
 * @return current state value
 */
protected final int getState() {
	return state;
}
```
### protected final void setState(int newState)
```java
/**
 * Sets the value of synchronization state.
 * This operation has memory semantics of a {@code volatile} write.
 * 更新同步状态state的值，具有volatile语义
 * @param newState the new state value
 */
protected final void setState(int newState) {
	state = newState;
}
```
### protected final boolean compareAndSetState(int expect, int update)
```java
/**
 * Atomically sets synchronization state to the given updated
 * value if the current state value equals the expected value.
 * This operation has memory semantics of a {@code volatile} read
 * and write.
 * 通过Unsafe类提供的硬件级别CAS原子性操作更新同步状态值state
 * # true  CAS尝试更新成功表示当前线程持有锁
 * # false CAS尝试更新失败标识锁由其他线程持有，放入阻塞队列
 * @param expect the expected value 期望值
 * @param update the new value 新值
 * @return {@code true} if successful. False return indicates that the actual
 * value was not equal to the expected value.
 */
protected final boolean compareAndSetState(int expect, int update) {
	// See below for intrinsics setup to support this
    //this：当前AQS抽象队列对象
    //stateOffset：state在当前AQS对象中的内存偏移量
    //expect：期望该变量在内存中的值
    //update：新值
    //如果该对象中内存偏移量的state变量的值与expect期望值相等则将值更新为update新值
	return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
}
```
## static final class Node
    AQS阻塞队列的节点类型
### volatile int waitStatus
阻塞队列中节点的状态，负值表示有效的等待状态，大于0的值表示已被取消
value | desc
----|----
SIGNAL(-1)|节点在等待唤醒，新节点入队时会将前驱结点状态更新为SIGNAL
CANCELLED(1)|节点已取消调度，当timeout或响应被中断的情况下，会触发变更为此状态
CONDITION(-2)|节点放到条件变量的条件队列中时，会变更为此状态
PROPAGATE(-3)|共享模式下，节点会唤醒其他节点
0|节点入队时的默认状态

## public final void acquire(int arg)
    独占锁的实现，无视中断(即时线程被其他线程中断也不会立即返回). 具体锁资源的获取逻辑由子类重写tryAcquire实现。
    tryAcquire成功则表示获取锁资源成功，失败则表示获取锁资源失败，需要放入AQS阻塞队列阻塞挂起。
```java
/**
 * Acquires in exclusive mode, ignoring interrupts.  Implemented
 * by invoking at least once {@link #tryAcquire},
 * returning on success.  Otherwise the thread is queued, possibly
 * repeatedly blocking and unblocking, invoking {@link
 * #tryAcquire} until success.  This method can be used
 * to implement method {@link Lock#lock}.
 * 独占方式实现资源的获取，对中断无视(即时线程被设置中断标志仍会继续)。
 * @param arg the acquire argument.  This value is conveyed to
 * 	{@link #tryAcquire} but is otherwise uninterpreted and
 * 	can represent anything you like.
 *
 */
public final void acquire(int arg) {
	//尝试获取锁(CAS更新state的值)，成功则返回，失败则将当前线程的加入AQS阻塞队列
	if (!tryAcquire(arg) &&
	    //以独占方式将当前线程加入AQS阻塞队列
		acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        //如果线程中断标识interrupted()为true，则调用当前线程的interrupt()中断该线程
		selfInterrupt();
}

/**
 * Creates and enqueues node for current thread and given mode.
 * 将当前线程加入AQS阻塞队列中
 * @param mode Node.EXCLUSIVE for exclusive, Node.SHARED for shared
 * @return the new node
 */
private Node addWaiter(Node mode) {
	//以独占方式创建一个当前线程的Node节点
	Node node = new Node(Thread.currentThread(), mode);
	// Try the fast path of enq; backup to full enq on failure
	//尝试将node节点指向AQS队列的尾部节点
	Node pred = tail;
	if (pred != null) {
		node.prev = pred;
		if (compareAndSetTail(pred, node)) {
			pred.next = node;
			return node;
		}
	}
	//如果尝试失败，则调用enq循环尝试直至成功
	enq(node);
	return node;
}

/**
 * Inserts node into queue, initializing if necessary. See picture above.
 * 将节点加入AQS阻塞队列
 * @param node the node to insert
 * @return node's predecessor 返回节点的前驱节点(即原始的尾节点，当前的倒数第二个节点)
 */
private Node enq(final Node node) {
	for (;;) {
		Node t = tail;
		if (t == null) { // Must initialize
			//初始化时第一个节点是空节点，哨兵节点
			if (compareAndSetHead(new Node()))
				tail = head;
		} else {
			//将新节点放到阻塞队列的尾部
			node.prev = t;
			//CAS更新队列的尾节点为node直至成功
			if (compareAndSetTail(t, node)) {
			    //将哨兵或之前的尾节点的后驱指向新入列的节点
			    t.next = node;
			    return t;
			}
		}
	}
}

/**
 * Acquires in exclusive uninterruptible mode for thread already in
 * queue. Used by condition wait methods as well as acquire.
 * 此时线程已经放入AQS阻塞队列，该方法用于判断新的节点是否能够正常park()
 * @param node the node 新加入AQS阻塞队列的当前线程的尾节点
 * @param arg the acquire argument
 * @return {@code true} if interrupted while waiting 当前线程是否被中断
 */
final boolean acquireQueued(final Node node, int arg) {
    //悲观认为该节点不能正常park
	boolean failed = true;
	try {
        //默认当前线程的中断标志为false
		boolean interrupted = false;
		for (;;) {
            //获取新加入节点的前驱节点
			final Node p = node.predecessor();
            //如果当前节点为第一个入队的节点，那么当前节点的前驱结点为哨兵节点
			if (p == head && tryAcquire(arg)) {
                //CAS尝试获取资源成功后将当前节点设为头节点
				setHead(node);
                //将哨兵节点设为Null，方便GC回收
				p.next = null; // help GC
				failed = false;
				return interrupted;
			}
            //否则判断当前节点是正常park()还是cancel
			if (shouldParkAfterFailedAcquire(p, node) &&
                //调用LockSupport.park(Thread.currentThread());阻塞当前线程并返回当前线程的interrupted()中断标志
				parkAndCheckInterrupt())
                    //如果当前线程Thread.currentThread().interrupted()中断标志为true则返回，否则默认没被中断
					interrupted = true;
		}
	} finally {
		if (failed)
            //如果当前节点不能正常Park，则调用则方法取消该节点
		    cancelAcquire(node);
	}
}

/**
 * Checks and updates status for a node that failed to acquire.
 * Returns true if thread should block. This is the main signal
 * control in all acquire loops.  Requires that pred == node.prev.
 * 检查node节点前驱节点的状态，如果前驱结点pred的waitStatus 等于SIGNAL则表示
 * node节点可以正常park()，否则其他waitStatus都无法正常park()
 * @param pred node's predecessor holding status
 * @param node the node
 * @return {@code true} if thread should block
 */
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
	int ws = pred.waitStatus;
	if (ws == Node.SIGNAL)
		/*
		 * 该节点已设置状态，正在等待资源释放，即为正常节点，node节点可正常park()
		 */
		return true;
    
    //该前驱结点为CANNELED状态
	if (ws > 0) {
		/*
		 * 如果当前节点的前驱结点线程已被取消(CANCELLED),则放弃该节点，继续找该节
		 * 点的前驱结点直至节点状态正常(waitStatus < 0)
		 */
		do {
			node.prev = pred = pred.prev;
		} while (pred.waitStatus > 0);
		
		pred.next = node;
	} else {
		/*
		 * waitStatus must be 0 or PROPAGATE.  Indicate that we
		 * need a signal, but don't park yet.  Caller will need to
		 * retry to make sure it cannot acquire before parking.
		 */
		compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
	}
	return false;
}

```
## ConditionObject
AQS的内部类ConditionObject，用来结合锁实现线程同步。ConditionObject是条件变量，每个条件变量对应一个条件队列(单项链表队列)，其用来存放调用条件变量的await后被阻塞的线程。

### private transient Node firstWaiter
    条件变量中条件队列(单向链表)的头节点

### private transient Node lastWaiter
    条件变量中条件队列(单向链表)的尾节点

### private Node addConditionWaiter()
    将当前线程加入条件队列
```java
/**
 * Adds a new waiter to wait queue.
 * @return its new wait node
 */
private Node addConditionWaiter() {
    Node t = lastWaiter;
    // If lastWaiter is cancelled, clean out.
    //释放条件队列中不是CONDITION状态的节点
    if (t != null && t.waitStatus != Node.CONDITION) {
         unlinkCancelledWaiters();
        t = lastWaiter;
    }
    //创建一个当前线程的条件队列节点
    Node node = new Node(Thread.currentThread(), Node.CONDITION);
    //如果条件队列为null，则将其设为头节点
    if (t == null)
        firstWaiter = node;
    else
         t.nextWaiter = node;
    //否则将该节点设为尾节点并返回
    lastWaiter = node;
    return node;
}
```

### public final void await() throws InterruptedException
    调用条件变量的await方法，将当前线程放到条件队列中，如果当前线程未持有锁则会抛出IllegalMonitorStateException异常，
    如果当前线程被其他线程调用了interrupt()方法则会抛出InterruptedException
```java
/**
 * Implements interruptible condition wait.
 * <ol>
 * <li> If current thread is interrupted, throw InterruptedException.
 * <li> Save lock state returned by {@link #getState}.
 * <li> Invoke {@link #release} with saved state as argument,
 *      throwing IllegalMonitorStateException if it fails.
 * <li> Block until signalled or interrupted.
 * <li> Reacquire by invoking specialized version of
 *      {@link #acquire} with saved state as argument.
 * <li> If interrupted while blocked in step 4, throw InterruptedException.
 * </ol>
 */
public final void await() throws InterruptedException {
    //如果当前线程被其他线程调用interrupt()方法则抛出中断异常
    if (Thread.interrupted())
		throw new InterruptedException();
    //将当前线程加到条件队列的尾节点
    Node node = addConditionWaiter();
    //将AQS阻塞队列中当前线程的节点释放并获取当前state的值
    int savedState = fullyRelease(node);
    int interruptMode = 0;
    /*
     * 如果该节点是条件队列节点，即node.waitStatus=CONDITION
     * 则调用LockSupport.park(thread)阻塞挂起该线程
     */

    while (!isOnSyncQueue(node)) {
		LockSupport.park(this);
		if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
			break;
	}
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
		interruptMode = REINTERRUPT;
    if (node.nextWaiter != null) // clean up if cancelled
		unlinkCancelledWaiters();
    if (interruptMode != 0)
		reportInterruptAfterWait(interruptMode);
}
```

### public final void signal
    将条件队列的头节点(等待时间最长的一个)移除，将其放入AQS阻塞队列
```java
/**
 * Moves the longest-waiting thread, if one exists, from the
 * wait queue for this condition to the wait queue for the
 * owning lock.
 *
 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
 * returns {@code false}
 */
public final void signal() {
    //如果线程不是独占锁则抛出IllegalMonitorStateException
    if (!isHeldExclusively())
        throw new IllegalMonitorStateException();
    Node first = firstWaiter;
    if (first != null)
        doSignal(first);
}

/**
 * Removes and transfers nodes until hit non-cancelled one or
 * null. Split out from signal in part to encourage compilers
 * to inline the case of no waiters.
 * 移除条件队列的头节点，并将其放入阻塞队列
 * @param first (non-null) the first node on condition queue
 */
private void doSignal(Node first) {
    do {
        if ( (firstWaiter = first.nextWaiter) == null)
            lastWaiter = null;
        first.nextWaiter = null;
    } while (!transferForSignal(first) &&
     (first = firstWaiter) != null);
}

/**
 * Transfers a node from a condition queue onto sync queue.
 * Returns true if successful.
 * @param node the node
 * @return true if successfully transferred (else the node was
 * cancelled before signal)
 */
final boolean transferForSignal(Node node) {
	/*
	 * If cannot change waitStatus, the node has been cancelled.
     * 将CANCELLED状态(节点的线程已经取消)的节点过滤
	 */
	if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
		return false;

	/*
	 * Splice onto queue and try to set waitStatus of predecessor to
	 * indicate that thread is (probably) waiting. If cancelled or
	 * attempt to set waitStatus fails, wake up to resync (in which
	 * case the waitStatus can be transiently and harmlessly wrong).
     * 将该节点加入AQS阻塞队列并尝试唤醒
	 */
	Node p = enq(node);
	int ws = p.waitStatus;
	if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
		LockSupport.unpark(node.thread);
	return true;
}
```
### public final void signalAll
    将条件队列中所有节点移除并将其加入AQS阻塞队列
```java
/**
 * Moves all threads from the wait queue for this condition to
 * the wait queue for the owning lock.
 * 移除条件队列中所有的节点并将其加入锁的AQS阻塞队列
 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
 * returns {@code false}
 */
public final void signalAll() {
    //非独占方式实现的锁调用该方法直接抛异常
    if (!isHeldExclusively())
		throw new IllegalMonitorStateException();
    //获取当前条件队列(单向链表队列)的头节点
    Node first = firstWaiter;
    if (first != null)
        //从头节点开始移除
		doSignalAll(first);
}

/**
 * Removes and transfers all nodes.
 * 从条件队列的头节点开始移除所有节点
 * @param first (non-null) the first node on condition queue
 */
private void doSignalAll(Node first) {
    //将条件变量的条件队列初始化
    lastWaiter = firstWaiter = null;
    do {
        //获取下一节点
		Node next = first.nextWaiter;
        //移除头节点
		first.nextWaiter = null;
        //将移除的节点加入锁的AQS阻塞队列并尝试唤醒
		transferForSignal(first);
        //将下一节点设为头节点继续移除
		first = next;
        //直到所有节点全部移除
    } while (first != null);
}
```
## protected boolean isHeldExclusively
    锁是否已经被持有，具体逻辑由子类实现
```java
/**
 * Returns {@code true} if synchronization is held exclusively with
 * respect to the current (calling) thread.  This method is invoked
 * upon each call to a non-waiting {@link ConditionObject} method.
 * (Waiting methods instead invoke {@link #release}.)
 *
 * <p>The default implementation throws {@link
 * UnsupportedOperationException}. This method is invoked
 * internally only within {@link ConditionObject} methods, so need
 * not be defined if conditions are not used.
 * #true: 锁已经被线程持有
 * #false：锁尚未被持有 
 * @return {@code true} if synchronization is held exclusively;
 * {@code false} otherwise
 * @throws UnsupportedOperationException if conditions are not supported
 */
protected boolean isHeldExclusively() {
	throw new UnsupportedOperationException();
}
```
## protected boolean tryAcquire
    以独占方式尝试获取锁资源(即CAS更新state的值是否成功)，具体逻辑由子类实现
```java
/**
 * Attempts to acquire in exclusive mode. This method should query
 * if the state of the object permits it to be acquired in the
 * exclusive mode, and if so to acquire it.
 *
 * <p>This method is always invoked by the thread performing
 * acquire.  If this method reports failure, the acquire method
 * may queue the thread, if it is not already queued, until it is
 * signalled by a release from some other thread. This can be used
 * to implement method {@link Lock#tryLock()}.
 *
 * <p>The default
 * implementation throws {@link UnsupportedOperationException}.
 *
 * @param arg the acquire argument. This value is always the one
 *passed to an acquire method, or is the value saved on entry
 *to a condition wait.  The value is otherwise uninterpreted
 *and can represent anything you like.
 * @return {@code true} if successful. Upon success, this object has
 * been acquired.
 * @throws IllegalMonitorStateException if acquiring would place this
 * synchronizer in an illegal state. This exception must be
 * thrown in a consistent fashion for synchronization to work
 * correctly.
 * @throws UnsupportedOperationException if exclusive mode is not supported
 */
protected boolean tryAcquire(int arg) {
	throw new UnsupportedOperationException();
}
```
## protected boolean tryRelease
    以独占方式尝试释放锁资源(即CAS更新state的值是否成功)，具体逻辑由子类实现
```java
/**
 * Attempts to set the state to reflect a release in exclusive
 * mode.
 *
 * <p>This method is always invoked by the thread performing release.
 *
 * <p>The default implementation throws
 * {@link UnsupportedOperationException}.
 *
 * @param arg the release argument. This value is always the one
 * passed to a release method, or the current state value upon
 * entry to a condition wait.  The value is otherwise
 * uninterpreted and can represent anything you like.
 * @return {@code true} if this object is now in a fully released
 * state, so that any waiting threads may attempt to acquire;
 * and {@code false} otherwise.
 * @throws IllegalMonitorStateException if releasing would place this
 * synchronizer in an illegal state. This exception must be
 * thrown in a consistent fashion for synchronization to work
 * correctly.
 * @throws UnsupportedOperationException if exclusive mode is not supported
 */
protected boolean tryRelease(int arg) {
	throw new UnsupportedOperationException();
}
```
## protected int tryAcquireShared
    以共享方式尝试获取锁资源(即CAS更新state的值是否成功)，具体逻辑由子类实现
```java
/**
 * Attempts to acquire in shared mode. This method should query if
 * the state of the object permits it to be acquired in the shared
 * mode, and if so to acquire it.
 *
 * <p>This method is always invoked by the thread performing
 * acquire.  If this method reports failure, the acquire method
 * may queue the thread, if it is not already queued, until it is
 * signalled by a release from some other thread.
 *
 * <p>The default implementation throws {@link
 * UnsupportedOperationException}.
 *
 * @param arg the acquire argument. This value is always the one
 * passed to an acquire method, or is the value saved on entry
 * to a condition wait.  The value is otherwise uninterpreted
 * and can represent anything you like.
 * @return a negative value on failure; zero if acquisition in shared
 * mode succeeded but no subsequent shared-mode acquire can
 * succeed; and a positive value if acquisition in shared
 * mode succeeded and subsequent shared-mode acquires might
 * also succeed, in which case a subsequent waiting thread
 * must check availability. (Support for three different
 * return values enables this method to be used in contexts
 * where acquires only sometimes act exclusively.)  Upon
 * success, this object has been acquired.
 * @throws IllegalMonitorStateException if acquiring would place this
 * synchronizer in an illegal state. This exception must be
 * thrown in a consistent fashion for synchronization to work
 * correctly.
 * @throws UnsupportedOperationException if shared mode is not supported
 */
protected int tryAcquireShared(int arg) {
	throw new UnsupportedOperationException();
}
```
## protected int tryReleaseShared
    以共享方式尝试释放锁资源(即CAS更新state的值是否成功)，具体逻辑由子类实现
```java
/**
 * Attempts to set the state to reflect a release in shared mode.
 *
 * <p>This method is always invoked by the thread performing release.
 *
 * <p>The default implementation throws
 * {@link UnsupportedOperationException}.
 *
 * @param arg the release argument. This value is always the one
 * passed to a release method, or the current state value upon
 * entry to a condition wait.  The value is otherwise
 * uninterpreted and can represent anything you like.
 * @return {@code true} if this release of shared mode may permit a
 * waiting acquire (shared or exclusive) to succeed; and
 * {@code false} otherwise
 * @throws IllegalMonitorStateException if releasing would place this
 * synchronizer in an illegal state. This exception must be
 * thrown in a consistent fashion for synchronization to work
 * correctly.
 * @throws UnsupportedOperationException if shared mode is not supported
 */
protected boolean tryReleaseShared(int arg) {
	throw new UnsupportedOperationException();
}
```
## public final boolean hasQueuedPredecessors
    当前线程所在节点是否为头节点，若是头节点则返回false，否则返回true，用做公平锁的策略。
```java
/*
 * 判断当前线程锁在节点是否AQS队列的头节点
 * @return true 表示不是头节点 false 表示是头节点
 * 公平锁的公平体现在此，头节点等待时间最长，若当前线程所在节点非头节点则直接阻塞，
 * 若为头节点则尝试唤醒
 */
public final boolean hasQueuedPredecessors() {
	// The correctness of this depends on head being initialized
	// before tail and on head.next being accurate if the current
	// thread is first in queue.
	Node t = tail; // Read fields in reverse initialization order
	Node h = head;
	Node s;
	return h != t &&
	((s = h.next) == null || s.thread != Thread.currentThread());
}
```

## 基于AQS实现不可重入的独占锁
```java
package com.zh.study.aqs;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于AQS抽象同步队列实现的不可重入锁，锁只能被一个线程持有，且每次都需要尝试获取资源
 * 其中state的含义：1表示锁被持有，0表示锁未被持有
 * @date 2020/12/22
 */
public class NonReentrantLock implements Lock, Serializable {

    /**
     * 内部帮助类，用于提供不可重入锁的获取与释放逻辑
     * state: 1 表示锁已被持有 0 表示锁未被持有
     */
    private static class Sync extends AbstractQueuedSynchronizer {

        /**
         * state == 1时表示锁被持有
         * @return 锁是否被持有
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        /**
         * 以独占方式实现锁的获取
         * @param acquires
         * @return
         */
        @Override
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            //state=0表示锁未被线程持有
            //CAS尝试将state更新为1，若成功则获取锁资源
            if (compareAndSetState(0, 1)) {
                //设置锁的持有者为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 以独占方式实现锁的释放
         * @param releases
         * @return
         */
        @Override
        protected boolean tryRelease(int releases) {
            assert releases == 1;
            //只有持有锁的线程才能调用释放锁的方法
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            //设置锁的当前持有者为null
            setExclusiveOwnerThread(null);
            //设置锁的状态为未持有
            setState(0);
            return true;
        }

        /**
         * 提供ConditionObjec实例用于实现锁的条件变量Condition
         * @return
         */
        Condition newCondition() {
            return new ConditionObject();
        }
    }

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    private static final NonReentrantLock lock = new NonReentrantLock();
    public static void main(String[] args) {
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                lock.lock();
                System.out.println(Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }, "T"+i).start();
        }

    }
}

```
# ReentrantLock 可重入锁
ReentrantLock也是基于AQS实现的同步锁，其state代表可重入的次数，不同的是ReentrantLock不仅是独占锁，还实现了公平锁和非公平锁，默认为非公平。
```java
/**
 * Creates an instance of {@code ReentrantLock}.
 * This is equivalent to using {@code ReentrantLock(false)}.
 */
public ReentrantLock() {
	sync = new NonfairSync();
}

/**
 * Creates an instance of {@code ReentrantLock} with the
 * given fairness policy.
 * ReentrantLock(true)则为公平锁
 * @param fair {@code true} if this lock should use a fair ordering policy
 */
public ReentrantLock(boolean fair) {
	sync = fair ? new FairSync() : new NonfairSync();
}
```
## 非公平锁
非公平体现在先到不一定先得，即线程A先在锁的AQS队列中阻塞等待，线程B之后尝试获取锁，此时刚好锁被释放，此时线程B通过CAS尝试成功立即就获取到了锁。
```java
/**
 * Performs non-fair tryLock.  tryAcquire is implemented in
 * subclasses, but both need nonfair try for trylock method.
 */
final boolean nonfairTryAcquire(int acquires) {
    //获取当前线程
    final Thread current = Thread.currentThread();
    //获取state状态值
    int c = getState();
    //state=0表示当前锁没有被线程持有
    //此时可能刚好锁被持有的线程释放，而当前线程获取到了
    //在AQS阻塞队列中的线程只能继续等待当前线程释放锁
    if (c == 0) {
		//CAS更新状态值state
		if (compareAndSetState(0, acquires)) {
			//设置所持有者为当前线程
			setExclusiveOwnerThread(current);
			return true;
		}
    }
    //可重入的逻辑，如果当前线程为锁的持有者
    else if (current == getExclusiveOwnerThread()) {
		//state更新为当前值加1
		int nextc = c + acquires;
		//锁可重入次数为Integer.MAX_VALUE
		if (nextc < 0) // overflow
			throw new Error("Maximum lock count exceeded");
		//设置当前state值加1
		setState(nextc);
		return true;
    }
    return false;
}
```
## 公平锁
公平锁的体现在如果当前线程所在节点不是AQS阻塞队列的头节点，那么就不让CAS尝试获取锁。
```java
/**
 * Fair version of tryAcquire.  Don't grant access unless
 * recursive call or no waiters or is first.
 */
protected final boolean tryAcquire(int acquires) {
    //获取当前线程
    final Thread current = Thread.currentThread();
    //获取当前state值
    int c = getState();
    //如果线程没有被持有，公平锁并没有让当前线程CAS尝试获取锁
    if (c == 0) {
		//而是通过hasQueuedPredecessors()限制只有当当前线程所在节点为头节点时才能获取锁
		if (!hasQueuedPredecessors() &&
			compareAndSetState(0, acquires)) {
			setExclusiveOwnerThread(current);
			return true;
		}
    }
    else if (current == getExclusiveOwnerThread()) {
		int nextc = c + acquires;
		if (nextc < 0)
			throw new Error("Maximum lock count exceeded");
		setState(nextc);
		return true;
    }
    return false;
}
```

# ReentrantReadWriteLock 读写锁
基于AQS实现的可重入的读写锁，其中写锁是独占锁，读锁是共享锁，公平和非公平的体现和ReentrantLock类似，默认是非公平锁。其中state的高16位表示读状态，也就是获取读锁的次数，低16位表示写锁的线程的可重入次数。
```java
/*
 * Read vs write count extraction constants and functions.
 * Lock state is logically divided into two unsigned shorts:
 * The lower one representing the exclusive (writer) lock hold count,
 * and the upper the shared (reader) hold count.
 */

static final int SHARED_SHIFT   = 16;
//1<<16 表示1左移16位，也就是2^16 = 65536 
static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
//读锁的最大个数位65535 = 65536 - 1
static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
//写锁的可重入最大次数65535
static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

/** Returns the number of shared holds represented in count  */
static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
/** Returns the number of exclusive holds represented in count  */
static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
```

### 写锁 WriteLock
###### 写锁为独占锁，在写锁和读锁都没有被其他线程持有的情况下，当前线程可获取写锁；如果当前已有线程获取读锁或写锁，则当前线程会被阻塞挂起，如果当前读锁的数量达到最大值或写锁的可重入次数达到最大值65535，则抛出异常。
```java
/**
 * 尝试获取锁
 */
protected final boolean tryAcquire(int acquires) {
    /*
     * 1. 如果读锁的数量不为0或者写锁的的数量不为0且锁的持有者非当前线程则返回false;
     * 2. 如果读锁的共享数量达到最大值或写锁的可重入次数达到最大值则返回false；
     * 3. Otherwise, this thread is eligible for lock if
     *    it is either a reentrant acquire or
     *    queue policy allows it. If so, update state
     *    and set owner.
     */
    Thread current = Thread.currentThread();
    int c = getState();
    //写锁的可重入次数
    int w = exclusiveCount(c);
    //如果state的低16为不等0则表示锁已经被线程持有
    if (c != 0) {
		// (Note: if c != 0 and w == 0 then shared count != 0)
		//如果写锁的状态值为0则表示写锁未被线程持有，但是state不等于，那就是读锁被其他线程持有了
		if (w == 0 || current != getExclusiveOwnerThread())
			//如果读锁被其他线程持有或者当前线程非锁的持有者，返回false尝试获取资源失败
			return false;
		//如果锁的可重入次数超过最大值则抛出异常
		if (w + exclusiveCount(acquires) > MAX_COUNT)
			throw new Error("Maximum lock count exceeded");
		// Reentrant acquire
		//当前线程为锁的持有者，更新state为原值加1
		setState(c + acquires);
		return true;
    }
    //如果锁未被持有，则判断是否公平锁
    //公平锁则不让当前线程持有锁
    //非公平锁则让当前线程尝试获取锁
    if (writerShouldBlock() ||
		!compareAndSetState(c, c + acquires))
			return false;
    //非公平锁则让当前线程持有锁
    setExclusiveOwnerThread(current);
    return true;
}

/**
 * 尝试释放锁
 */
protected final boolean tryRelease(int releases) {
    //若未持有锁的线程调用此方法则会抛出IllegalMonitorStateException
    if (!isHeldExclusively())
		throw new IllegalMonitorStateException();
    //将state状态值减1
    int nextc = getState() - releases;
    //判断state是否等于0
    boolean free = exclusiveCount(nextc) == 0;
    //若等于0则释放锁
    if (free)
setExclusiveOwnerThread(null);
    //否则啥都不干，将state更新为原值减1
    setState(nextc);
    return free;
}
```

### 读锁 ReadLock
###### 读锁为共享锁，在写锁没有被其他线程持有的情况下，读锁可由多个线程共同持有；如果写锁被其他线程持有，则当前线程会被阻塞挂起。
```java
protected final int tryAcquireShared(int unused) {
    Thread current = Thread.currentThread();
    int c = getState();
    //如果写锁已被其他线程持有则直接返回
    if (exclusiveCount(c) != 0 &&
		getExclusiveOwnerThread() != current)
		return -1;
    //获取读锁的数量
    int r = sharedCount(c);
    //读锁公平非公平的体现，是否应该阻塞当前线程
    //公平锁：非头节点就阻塞
    //非公平锁：头节点如果不是共享节点就阻塞
    if (!readerShouldBlock() &&
		r < MAX_COUNT &&
		compareAndSetState(c, c + SHARED_UNIT)) {
		//如果当前读锁的数量为0
		if (r == 0) {
			//将当前线程设置第一个读锁线程
			firstReader = current;
			//第一个读锁线程的重入次数为12
			firstReaderHoldCount = 1;
		} else if (firstReader == current) {
			//如果当前线程为持有第一个读锁线程，则将重入次数加1
			firstReaderHoldCount++;
		} else {
			//否则获取缓存中该线程信息(线程id和读锁的重入次数)
			HoldCounter rh = cachedHoldCounter;
			//如果该线程是第一次获取读锁(即缓存的holdCount等于null)
			//或是缓存的上一次获取读锁的线程不是当前线程
			if (rh == null || rh.tid != getThreadId(current))
			//将当前线程的HoldCount放到缓存HoldCount对象
			cachedHoldCounter = rh = readHolds.get();
			else if (rh.count == 0)
			//如果当前前程为上一次获取读锁的线程
			readHolds.set(rh);
			//则将重入次数加1并更新ThreadLocal中的值
			rh.count++;
		}
		return 1;
    }
    //尝试获取锁时，多个线程只有一个成功，不成功的进入以下方法重试
    return fullTryAcquireShared(current);
}

/**
 * Full version of acquire for reads, that handles CAS misses
 * and reentrant reads not dealt with in tryAcquireShared.
 */
final int fullTryAcquireShared(Thread current) {
    /*
     * This code is in part redundant with that in
     * tryAcquireShared but is simpler overall by not
     * complicating tryAcquireShared with interactions between
     * retries and lazily reading hold counts.
     */
    HoldCounter rh = null;
    for (;;) {
		int c = getState();
		if (exclusiveCount(c) != 0) {
			//如果写锁被其他线程持有则直接返回
			if (getExclusiveOwnerThread() != current)
		return -1;
			// else we hold the exclusive lock; blocking here
			// would cause deadlock.
		} else if (readerShouldBlock()) {
			//如果该线程应该阻塞
			// Make sure we're not acquiring read lock reentrantly
			if (firstReader == current) {
		// assert firstReaderHoldCount > 0;
			} else {
		if (rh == null) {
			rh = cachedHoldCounter;
			//当前线程不是上一个读锁线程，即当前线程从未获取过读锁
			//那就将该线程从ThreadLocal中remove
			if (rh == null || rh.tid != getThreadId(current)) {
		rh = readHolds.get();
		if (rh.count == 0)
			readHolds.remove();
			}
		}
		if (rh.count == 0)
			return -1;
			}
		}
		if (sharedCount(c) == MAX_COUNT)
			throw new Error("Maximum lock count exceeded");
		//如果线程不该阻塞且读锁的数量不为最大值，则将当前线程设为读锁持有者
		if (compareAndSetState(c, c + SHARED_UNIT)) {
			if (sharedCount(c) == 0) {
				firstReader = current;
				firstReaderHoldCount = 1;
			} else if (firstReader == current) {
				firstReaderHoldCount++;
			} else {
				if (rh == null)
					rh = cachedHoldCounter;
				if (rh == null || rh.tid != getThreadId(current))
					rh = readHolds.get();
				else if (rh.count == 0)
					readHolds.set(rh);
			rh.count++;
			cachedHoldCounter = rh; // cache for release
			}
			return 1;
		}
    }
}

protected final boolean tryReleaseShared(int unused) {
    Thread current = Thread.currentThread();
    //如果当前线程就是第一个读锁的持有者，即当前读锁持有者只有一个线程时
    if (firstReader == current) {
		// assert firstReaderHoldCount > 0;
		//如果当前线程读锁的重入次数为1
		if (firstReaderHoldCount == 1)
			//将读锁的第一个持有者设为null
			firstReader = null;
		else
			//如果该线程重入读锁的次数不为1，则将其减1
			firstReaderHoldCount--;
    } else {
			//获取缓存中的当前线程的HoldCounter
			HoldCounter rh = cachedHoldCounter;
			if (rh == null || rh.tid != getThreadId(current))
				rh = readHolds.get();
			//获取当前线程在读锁的重入次数
			int count = rh.count;
			if (count <= 1) {
				//重入次数为1时，移除ThreadLocal中当前线程
				readHolds.remove();
				if (count <= 0)
					//当前线程未持有读锁
					throw unmatchedUnlockException();
			}
			//如果可重入次数不等于1，则减1
			--rh.count;
    }
	
    //CAS更新state中读锁的数量
    for (;;) {
		int c = getState();
		int nextc = c - SHARED_UNIT;
		if (compareAndSetState(c, nextc))
			// Releasing the read lock has no effect on readers,
			// but it may allow waiting writers to proceed if
			// both read and write locks are now free.
			return nextc == 0;
    }
}

private IllegalMonitorStateException unmatchedUnlockException() {
    return new IllegalMonitorStateException(
	"attempt to unlock read lock, not locked by current thread");
}
```
# StampedLock
StampedLock是JDK8新增的一个锁，该锁提供了三种模式的读写控制，当调用获取锁的函数时，会返回一个long型的变量，我们称之为戳记(stamp)，这个戳记代表锁的状态。

## 写锁writeLock(不可重入锁)
排他锁或独占锁，某时只有一个线程可以获取该锁，当一个线程获取该所后，其他请求读锁和写锁的线程必须等待。 

## 悲观读锁readLock(共享锁)
在没有其他线程获取写锁的情况下，多个线程可以同时获取读锁。悲观体现在操作数据前会悲观的认为其他线程可能已经对要操作的数据修改，所以先对数据加锁。

## 乐观读锁tryOptimisticRead
相对于悲观读锁，乐观读锁在操作数据前没有通过CAS设置锁的状态，而是通过位运算操作测试当前有没有线程持有写锁，如果没有则返回一个非0的stamp版本信息。在具体操作数据前还需要调用validate方法验证该stamp版本是否可用，即在获取位运算这段期间有没有线程获取写锁。

# 基于CAS的非阻塞队列
## ConcurrentLinkedQueue
线程安全的无界非阻塞队列(单向链表方式)，对于入队和出队操作使用CAS操作来实现线程安全。
Method|Desc
----|----
offer|在队列尾部添加一个元素
poll|在队列头部获取并移除一个元素
peek|获取队列头部一个元素(不移除)
remove|如果队列中存在该元素则删除该元素，如果存在多个则删除第一个

# 阻塞队列
## LinkedBlockingQueue
使用独占锁实现的有界阻塞队列(单向链表)，内部有两个ReentrantLock的实例，takeLock用来控制只有一个线程可以从队列获取元素，putLock用来控制只有一个线程可以从队列尾部添加元素；内部有两个条件变量，其中notEmpty对应takeLock，notFull对应putLock。
Method|Desc
----|----
offer|向队列尾部添加一个元素(putLock)
put|向队列尾部添加一个元素(putLock)
poll|从队列头部获取并移除一个元素(takeLock)
peek|获取头部元素(takeLock)
take|获取当前头部元素并移除(takeLock),如果在阻塞时被其他线程设置了中断标志，则会抛出中断异常并返回
remove|删除队列里面指定的元素(putLock,takeLock双重锁)

## ArrayBlockingQueue
使用独占锁实现的有界阻塞对立(数组)

## PriorityBlockingQueue
带优先级的无界阻塞队列(平衡二叉树)，每次出队都返回优先级最高或最低的元素。

## DelayQueue
无界阻塞延迟队列，队列中每个元素都有过期时间，当从队列获取元素时，只有过期元素才会出列，队列头部元素是最快要过期的元素。

# 线程池ThreadPoolExecutor
### private final AtomicInteger ctl
相当于AQS中的state变量，用来记录线程池状态和线程池线程个数。其中高3位表示线程池的状态，后面的位数表示线程数。
```java
/**
 * ctl相当于AQS中的state，高3位表示线程的状态，其他位表示线程的个数
 */
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
//int类型的位数减去高3位后剩余的位数
private static final int COUNT_BITS = Integer.SIZE - 3;
//线程的最大个数 = 2^COUNT_BITS - 1
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
```
其中线程池的线程状态主要包括：
线程状态|描述
-----|-----
RUNNING|接受新任务并且处理阻塞队列里的任务
SHUTDOWN|不接受新任务但是会处理阻塞队列里的任务
STOP|不接受新任务，也不会处理阻塞队列里的任务且中断正在处理的任务
TIDYING|所有的任务都执行完(包括阻塞队列里的任务),当前线程池中活动线程的数量为0，将调用terminated方法
TERMINATED|终止状态，terminated方法执行完毕后的状态

其中线程的状态转变主要包括：
情景|线程状态转变
-----|-----
显式调用shutdown()或隐式地调用finalize()里面的shutdown()方法|RUNNING -> SHUTDOWN
显式调用shutdownNow()|(RUNNING or SHUTDOWN) -> STOP
当阻塞队列和线程池都为空|SHUTDOWN -> TIDYING
当线程池为空|STOP -> TIDYING
当terminated方法执行完成后|TIDYING -> TERMINATED

### 线程池参数
参数|描述
-----|-----
corePoolSize|线程池中核心线程的个数
workQueue|用于保存等待执行的任务的阻塞队列
maximumPoolSize|线程池最大线程数量
threadFactory|创建线程的工厂
handler|饱和策略，当阻塞队列达到最大且线程的个数达到maximumPoolSize最大线程数量后采取的策略
keepAliveTime|当线程池中的线程数量比核心线程数量多时，闲置状态的线程存活的最大时间
```java
/**
 * Core pool size is the minimum number of workers to keep alive
 * (and not allow to time out etc) unless allowCoreThreadTimeOut
 * is set, in which case the minimum is zero.
 * 线程池中核心线程个数
 */
private volatile int corePoolSize;
	
/**
 * The queue used for holding tasks and handing off to worker
 * threads.  We do not require that workQueue.poll() returning
 * null necessarily means that workQueue.isEmpty(), so rely
 * solely on isEmpty to see if the queue is empty (which we must
 * do for example when deciding whether to transition from
 * SHUTDOWN to TIDYING).  This accommodates special-purpose
 * queues such as DelayQueues for which poll() is allowed to
 * return null even if it may later return non-null when delays
 * expire.
	 * 用于保存等待执行的任务的阻塞队列，比如:
	 * 		# 基于数组的有界ArrayBlockingQueue
	 *  # 基于链表的无界LinkedBlockingQueue
	 *  # 优先级队列PriorityBlockingQueue
 */
private final BlockingQueue<Runnable> workQueue;
	
/**
 * Maximum pool size. Note that the actual maximum is internally
 * bounded by CAPACITY.
	 * 线程池的最大线程数量
 */
private volatile int maximumPoolSize;	
	
/**
 * Factory for new threads. All threads are created using this
 * factory (via method addWorker).  All callers must be prepared
 * for addWorker to fail, which may reflect a system or user's
 * policy limiting the number of threads.  Even though it is not
 * treated as an error, failure to create threads may result in
 * new tasks being rejected or existing ones remaining stuck in
 * the queue.
 *
 * We go further and preserve pool invariants even in the face of
 * errors such as OutOfMemoryError, that might be thrown while
 * trying to create threads.  Such errors are rather common due to
 * the need to allocate a native stack in Thread.start, and users
 * will want to perform clean pool shutdown to clean up.  There
 * will likely be enough memory available for the cleanup code to
 * complete without encountering yet another OutOfMemoryError.
 * 创建线程的工厂
 */
private volatile ThreadFactory threadFactory;
	
/**
 * Handler called when saturated or shutdown in execute.
 * 饱和策略，当阻塞队列达到最大且线程的个数达到maximumPoolSize最大线程数量后
 * 采取的策略，如
 *  # AbortPolicy(抛出异常)
 *  # CallerRunsPolicy(使用调用者所在的线程来运行任务)
 *  # DiscardOldestPolicy(调用poll丢弃一个任务，执行当前任务)
 *  # DiscardPolicy(直接丢弃，不抛出异常)
 */
	 
private volatile RejectedExecutionHandler handler;

/**
 * Timeout in nanoseconds for idle threads waiting for work.
 * Threads use this timeout when there are more than corePoolSize
 * present or if allowCoreThreadTimeOut. Otherwise they wait
 * forever for new work.
 * 当线程池中的线程数量比核心线程数量多时，闲置状态的线程存活的最大时间
 */
private volatile long keepAliveTime;	
```

### 线程池类型
类型|描述
-----|-----
newFixedThreadPool|创建一个核心线程和最大线程数都为n的线程池
newSingleThreadExecutor|创建一个核心线程和最大线程数都为1的线程池
newCachedThreadPool|创建一个按需创建的线程池，初始线程个数为0，最大线程数为Integer.MAX_VALUE
```java
/**
 * Creates a thread pool that reuses a fixed number of threads
 * operating off a shared unbounded queue.  At any point, at most
 * {@code nThreads} threads will be active processing tasks.
 * If additional tasks are submitted when all threads are active,
 * they will wait in the queue until a thread is available.
 * If any thread terminates due to a failure during execution
 * prior to shutdown, a new one will take its place if needed to
 * execute subsequent tasks.  The threads in the pool will exist
 * until it is explicitly {@link ExecutorService#shutdown shutdown}.
 *
 * @param nThreads the number of threads in the pool
 * @return the newly created thread pool
 * @throws IllegalArgumentException if {@code nThreads <= 0}
 */
public static ExecutorService newFixedThreadPool(int nThreads) {
	//核心线程数和最大线程数为nThreads，只要线程数超过核心线程数则回收空闲线程
	return new ThreadPoolExecutor(nThreads, nThreads,
		0L, TimeUnit.MILLISECONDS,
		new LinkedBlockingQueue<Runnable>());
}

/**
 * Creates an Executor that uses a single worker thread operating
 * off an unbounded queue. (Note however that if this single
 * thread terminates due to a failure during execution prior to
 * shutdown, a new one will take its place if needed to execute
 * subsequent tasks.)  Tasks are guaranteed to execute
 * sequentially, and no more than one task will be active at any
 * given time. Unlike the otherwise equivalent
 * {@code newFixedThreadPool(1)} the returned executor is
 * guaranteed not to be reconfigurable to use additional threads.
 * 
 * @return the newly created single-threaded Executor
 */
public static ExecutorService newSingleThreadExecutor() {
	//核心线程数和最大线程数为1，只要线程数超过1个则回收空闲线程
	return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1,
		0L, TimeUnit.MILLISECONDS,
		new LinkedBlockingQueue<Runnable>()));
}

/**
 * Creates a thread pool that creates new threads as needed, but
 * will reuse previously constructed threads when they are
 * available.  These pools will typically improve the performance
 * of programs that execute many short-lived asynchronous tasks.
 * Calls to {@code execute} will reuse previously constructed
 * threads if available. If no existing thread is available, a new
 * thread will be created and added to the pool. Threads that have
 * not been used for sixty seconds are terminated and removed from
 * the cache. Thus, a pool that remains idle for long enough will
 * not consume any resources. Note that pools with similar
 * properties but different details (for example, timeout parameters)   

 * may be created using {@link ThreadPoolExecutor} constructors.
 *
 * @return the newly created thread pool
 */
public static ExecutorService newCachedThreadPool() {
	/*
	 * 核心线程数corePoolSize: 0
	 * 最大线程数maximumPoolSize：Integer.MAX_VALUE
	 * keepAliveTime：60 只要当前线程在60s内空闲则回收
	 * workQueue为同步队列，加入同步队列的任务会被马上执行，同步队列里最多只有一个任务
	 */
	return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
		60L, TimeUnit.SECONDS,
		new SynchronousQueue<Runnable>());
}
```
###### 为什么不建议用Exectors创建线程池
从以上源码可知，在使用Executors类的newCachedThreadPool创建线程池时，线程的最大数量为Integer.MAX_VALUE，很容易造成OOM(Out Of Memory, 内存溢出)。

### 定时任务调度的线程池ScheduledThreadPoolExecutor
其内部使用DelayQueue延迟队列来存放具体的任务，任务的类型由变量period表示。
+ period=0，说明任务是一次性的，执行完毕后就退出了
+ period<0，说明当前任务为fixed-delay任务，是固定延迟的定时可重复执行任务
+ period>0，说明当前任务为fixed-rate任务，是固定频率的定时可重复执行任务 

# 线程同步器
### CountDownLatch
基于AQS实现的一次性`(使用完后state的值就变成了0)`线程同步器，适用于主线程需要等待多个子线程执行完毕后再进行汇总的场景。`共享方式`实现锁，其中`state表示子线程的个数`。

##### public void countDown
当子线程执行完毕后，将`CountDownLatch`中AQS的`state`值减1
```java
public void countDown() {
    //调用tryReleaseShared更新state值
    sync.releaseShared(1);
}

protected boolean tryReleaseShared(int releases) {
    // Decrement count; signal when transition to zero
    //CAS将state的值减1
    //state的值等于0时直接返回
    for (;;) {
		int c = getState();
		if (c == 0)
			return false;
		int nextc = c-1;
		if (compareAndSetState(c, nextc))
			return nextc == 0;
    }
}
```
##### public void await()
阻塞当前线程，直到`CountDownLatch`中`state`的值等于0，或者当前线程被中断
```java
public void await() throws InterruptedException {
    //共享方式实现锁，调用AQS的acquireSharedInterruptibly
    sync.acquireSharedInterruptibly(1);
}

public final void acquireSharedInterruptibly(int arg)
throws InterruptedException {
	//如果当前线程被中断，则抛出InterruptedException
	if (Thread.interrupted())
		throw new InterruptedException();

	//如果stated的值不等于0，则将当前线程阻塞挂起
    //直到最后一个子线程执行完任务后调用releaseSharded()将其唤醒
    //或线程被中断
	if (tryAcquireShared(arg) < 0)	
    	doAcquireSharedInterruptibly(arg);
}

//AQS中共享方式将当前线程放入阻塞队列
private void doAcquireSharedInterruptibly(int arg)
throws InterruptedException {
    //新增SHARED状态的Node节点
	final Node node = addWaiter(Node.SHARED);
	boolean failed = true;
	try {
		for (;;) {
            //获取该节点的前驱节点
			final Node p = node.predecessor();
            //如果该节点的前驱节点为头部节点
			if (p == head) {
                //尝试获取资源
				int r = tryAcquireShared(arg);
                //如果尝试成功，则将当前的节点设置为头节点
				if (r >= 0) {
					setHeadAndPropagate(node, r);
                    //将获取资源的头部节点出队
					p.next = null; // help GC
					failed = false;
					return;
				}
			}
            //否则判断该节点在获取资源失败后能否正常阻塞
			if (shouldParkAfterFailedAcquire(p, node) &&
            //如果能正常阻塞则调用LockSupport.park()方法阻塞当前线程
            //并检测当前线程是否被中断，如是则抛出InterruptedException
			parkAndCheckInterrupt())
				throw new InterruptedException();
		}
	} finally {
        //否则，将AQS队列中该节点设为CANCELLED取消状态
		if (failed)
			cancelAcquire(node);
	}
}
```

### CyclicBarrier(回环屏障)
基于ReentrantLock独占锁实现的可重用的线程同步器，它可以让一组线程全部达到一个状态后再全部同时执行。之所以称为回环时因为当所有线程执行完毕并重置CyclicBarrier的状态后又可重用，之所以称为屏障时是因为线程调用await方法阻塞，这个阻塞点称为屏障，等所有线程都调用await方法后，就会打破屏障，继续执行。
##### await()
```java
public int await() throws InterruptedException, BrokenBarrierException {
	try {
		return dowait(false, 0L);
	} catch (TimeoutException toe) {
		throw new Error(toe); // cannot happen
	}
}

private int dowait(boolean timed, long nanos)
	throws InterruptedException, BrokenBarrierException,TimeoutException 
{
	//获取独占锁
	final ReentrantLock lock = this.lock;
	lock.lock();
	try {
		//是否屏障被打破
		final Generation g = generation;
		//屏障被打破则抛出BrokenBarrierException
		if (g.broken)
			throw new BrokenBarrierException();

		//是否被中断,中断则唤醒阻塞的所有线程并抛出InterruptedException
		if (Thread.interrupted()) {
			breakBarrier();
			throw new InterruptedException();
		}

		//线程数减1
		int index = --count;

		//如果所有线程都执行完毕，即最后一个线程调用await，即将打破屏障点
		if (index == 0) {  // tripped
			boolean ranAction = false;
			try {
				//查看初始化时是否指定的汇总逻辑
				final Runnable command = barrierCommand;
				//若有，则执行汇总逻辑
				if (command != null)
					command.run();
				ranAction = true;
				//将线程数重新赋值parties
				//唤醒阻塞在条件变量条件队列里的所有线程
				//重新设置屏障的状态broken
				nextGeneration();
				return 0;
			} finally {
				//否则，唤醒所有阻塞线程
				if (!ranAction)
					breakBarrier();
			}
		}

		// loop until tripped, broken, interrupted, or timed out
		//否则，继续阻塞
		for (;;) {
			try {
				if (!timed)
					trip.await();
				else if (nanos > 0L)
					nanos = trip.awaitNanos(nanos);
			} catch (InterruptedException ie) {
				if (g == generation && ! g.broken) {
					breakBarrier();
					throw ie;
				} else {
					// We're about to finish waiting even if we had not
					// been interrupted, so this interrupt is deemed to
					// "belong" to subsequent execution.
					Thread.currentThread().interrupt();
				}
			}

			if (g.broken)
				throw new BrokenBarrierException();

			if (g != generation)
				return index;

			if (timed && nanos <= 0L) {
				breakBarrier();
				throw new TimeoutException();
			}
		}
	} finally {
		lock.unlock();
	}
}

//重置，已达到可重复使用
private void nextGeneration() {
	// signal completion of last generation
    //唤醒条件变量中阻塞的所有子线程
	trip.signalAll();
	// set up next generation
    //重新设置线程数为初始化时指定的数量
	count = parties;
    //重置屏障的打破状态为false
	generation = new Generation();
}
```
##### 实践
```java
package com.zh.study.threadsync;

import com.zh.study.threadpool.MyThreadPool;

import java.util.concurrent.*;

/**
 * 回环屏障CyclicBarrier可以让一组线程全部达到一个状态后再全部同时执行，
 * 相比于CountDownLatch，CyclicBarrier可以重用
 *
 *
 * @date 2020/12/25
 */
public class CyclicBarrierTest {

    static final ExecutorService pool = MyThreadPool.createMyThreadPool();

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, new Runnable() {
            @Override
            public void run() {
                System.out.println("all over...");
            }
        });

        //让每个子线程阶段性的完成任务并汇总，可重复使用
        pool.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println(Thread.currentThread().getName() + "step1 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step2 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step3 start...");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

        pool.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println(Thread.currentThread().getName() + "step1 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step2 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step3 start...");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

        pool.shutdown();
        //singleTest(cyclicBarrier);
    }

    private static void singleTest(CyclicBarrier cyclicBarrier) throws InterruptedException, BrokenBarrierException {
        pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread().getName() + "CyclicBarrier in...");
                cyclicBarrier.await();
                System.out.println(Thread.currentThread().getName() + "CyclicBarrier out...");
                return Thread.currentThread().getName();
            }
        });

        pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread().getName() + "CyclicBarrier in...");
                cyclicBarrier.await();
                System.out.println(Thread.currentThread().getName() +"CyclicBarrier out...");
                return Thread.currentThread().getName();
            }
        });

        pool.shutdown();
    }
}
```
### Semaphore 信号量
基于AQS实现的线程同步器，state表示信号量个数，与CountDownLatch,CyclicBarrier不同的是，它内部的计数器是递增的，并且在一开始时初始化可以指定一个初始值，但是不需要知道需要同步的线程个数，而是在需要同步的地方调用acquire方法时指定需要同步的线程个数。
```java
package com.zh.study.threadsync;

import com.zh.study.threadpool.MyThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

/**
 * 信号量
 *  基于AQS实现的线程同步器，state表示信号量个数.
 *  与CountDownLatch,CyclicBarrier不同的是，它内部的计数器是递增的，并且在一开始时初始化
 *  Semaphore时可以指定一个初始值，但是不需要知道需要同步的线程个数，而是在需要同步的地方调用
 *  acquire方法时指定需要同步的线程个数
 * @date 2020/12/25
 */
public class SemaphoreTest {

    static ExecutorService pool = MyThreadPool.createMyThreadPool();
    static Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                semaphore.release();
            }
        });

        pool.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                semaphore.release();
            }
        });

        semaphore.acquire(2);
        System.out.println("all child thread over!");

        pool.shutdown();
    }
}

```
