package practice1.ReentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PIngPongReentrantLock {
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition pingCondition = lock.newCondition();
        Condition pongCondition = lock.newCondition();
        final boolean[] pingTurn = {true};

        Thread pingThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lock.lock();
                try {
                    while (!pingTurn[0]) {
                        pingCondition.await();
                    }
                    System.out.print("Ping ");
                    pingTurn[0] = false;
                    pongCondition.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread pongThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lock.lock();
                try {
                    while (pingTurn[0]) {
                        pongCondition.await();
                    }
                    System.out.print("Pong ");
                    pingTurn[0] = true;
                    pingCondition.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        pingThread.start();
        pongThread.start();

        try {
            pingThread.join();
            pongThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

