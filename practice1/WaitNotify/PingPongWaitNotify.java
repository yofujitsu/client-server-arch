package practice1.WaitNotify;

public class PingPongWaitNotify {
    public static void main(String[] args) {
        final Object lock = new Object();
        final boolean[] pingTurn = {true};

        Thread pingThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    try {
                        while (pingTurn[0]) {
                            lock.wait();
                        }
                        System.out.print("Ping ");
                        pingTurn[0] = false;
                        lock.notify();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        Thread pongThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    try {
                        while (pingTurn[0]) {
                            lock.wait();
                        }
                        System.out.print("Pong ");
                        pingTurn[0] = true;
                        lock.notify();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
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