package com.katch.perfer;

public class Run {
	public static void main(String[] args) {
		try {
			MyThread thread = new MyThread();
			thread.start();
			Thread.sleep(1000);
			thread.stop();
			System.out.println("是否停止1？=" + thread.interrupted());// false
			System.out.println("是否停止2？=" + thread.interrupted());// false main线程没有被中断!!!
			thread.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}