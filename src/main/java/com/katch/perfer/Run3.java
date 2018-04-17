package com.katch.perfer;

public class Run3 {
	public static void main(String[] args) {
		try {
			MyThread thread = new MyThread();
			thread.start();
			Thread.sleep(1000);
			thread.interrupt();
			System.out.println("是否停止1？=" + thread.isInterrupted());// true
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}