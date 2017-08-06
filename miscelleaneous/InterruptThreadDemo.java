package miscelleaneous;

/*
 * 线程被阻塞时的终止方法.比如当一个线程由于需要等候键盘输入而被阻塞，或者调用Thread.join()方法，或者Thread.sleep()方法，
 * DataInputStream.read(),线程处于处于不可运行状态时，即使主程序中将该线程的共享变量设置为true，但该线程此时根本无法检查循环标志，当然也就无法立即中断。
 * 用Thread提供的interrupt()方法，因为该方法虽然不会中断一个正在运行的线程，但它可以使被阻塞的线程抛出一个中断异常，从而使线程提前结束阻塞状态，退出堵塞代码。
 * 调用interrupt（）方法，线程并没有实际被中断，会继续往下执行。只是负责发出要求，具体执行由其他语句实现。
 * 首先看一下该方法的实现：
public static boolean interrupted () {
     return currentThread().isInterrupted(true);
}
该方法就是直接调用当前线程的isInterrupted(true)方法。
然后再来看一下 isInterrupted的实现：
public boolean isInterrupted () {
     return isInterrupted( false);
}
interrupted 是作用于当前线程，isInterrupted 是作用于调用该方法的线程对象所对应的线程。（线程对象对应的线程不一定是当前运行的线程。
例如我们可以在A线程中去调用B线程对象的isInterrupted方法。）
后面的参数( boolean ClearInterrupted),清状态位. 很好区分，只有当前线程才能清除自己的中断位（对应interrupted（）方法）
 */
class A extends Thread {
	volatile boolean stop = false;

	public void run() {
		while (!stop) {
			System.out.println(getName() + " is running " + System.currentTimeMillis());
			try {
				sleep(30 * 1000);
			} catch (InterruptedException e) {
				System.out.println("week up from blcok...");
				stop = true; // 在异常处理代码中修改共享变量的状态
			}
		}
		System.out.println(getName() + " is exiting...");
	}
}

public class InterruptThreadDemo {
	public static void main(String[] args) throws InterruptedException {
		A m1 = new A();
		System.out.println("Starting thread...");
		m1.start(); // 从这里进入run()方法后,不会一直停在while里,因为m1是程序起的一个线程,还有一个叫 main
					// 的主线程,即下面的Thread. 此时两个线程在跑.
		// 多线程时,实现Runnable没有start()方法,只有继承Thread才有.如果A类是实现Runnable,则在A
		// m1 = new A()之后再加一句
		// Thread t1 = new Thread(m1);然后t1就能用start(). 最终还是通过Thread建了一个线程.

		Thread.sleep(1000);
		// System.out.println(Thread.currentThread().getName()+"
		// "+System.currentTimeMillis());
		System.out.println("Interrupt thread...: " + m1.getName());
		// m1.stop = true; // 设置共享变量为true
		m1.interrupt(); // 阻塞时退出阻塞状态
		System.out.println(m1.isInterrupted()); // 实例方法,查调用此方法的线程的状态
		System.out.println(m1.interrupted()); // 静态方法,查'当前'线程的状态
		// If you use interrupted(), what you're asking is "Have I been
		// interrupted since the last time I asked?"
		Thread.sleep(1000); // 主线程休眠3秒以便观察线程m1的中断情况
		System.out.println("Stopping application....................");
		System.out.println(m1.isAlive() + " " + m1.isInterrupted() + " " + m1.isDaemon());
	}
}