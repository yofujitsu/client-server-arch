package practice3;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static final AtomicInteger numberOfMessagesInBuffer = new AtomicInteger(0);
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);
    private static final Queue<Callable<Integer>> mailingTasks = new ConcurrentLinkedQueue<>(); //not synchronized on clear() & size()
    private static final StringBuffer globalMessageStringBuffer = new StringBuffer();

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(5001)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Callable<Integer> mailingTask = Server.registerClientMailingTask(clientSocket);

                scheduledExecutorService.execute(new ClientSession(clientSocket, mailingTask));
            }
        }
    }

    public static Callable<Integer> registerClientMailingTask(Socket socket) {

        Callable<Integer> mailingTask = () -> {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(globalMessageStringBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        };

        if (Server.mailingTasks.add(mailingTask)) {
            return mailingTask;
        } else {
            throw new RuntimeException("Failed to create mailing task for the client: " + socket);
        }
    }

    public static void makeMailing() {
        //Пока я делаю рассылку мне нужна синхронизация
        //Чтобы другой клиент не отправил сообщение в этот момент и оно после рассылки не стёрлось

        synchronized (globalMessageStringBuffer) {
            scheduledExecutorService.schedule(() -> {
                try {
                    scheduledExecutorService.invokeAll(mailingTasks);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    globalMessageStringBuffer.setLength(0);
                    Server.numberOfMessagesInBuffer.getAndSet(0);
                    // в случае ошибки всё равно чистим буфер
                    // лучше потерять сообщения, чем упасть с переполнением стека
                }
            }, 5, TimeUnit.SECONDS);
        }
    }

    private static class ClientSession implements Runnable {

        private final Socket socket;
        private final Callable<Integer> mailingTask;

        public ClientSession(Socket clientSocket, Callable<Integer> clientMailingTask) {
            this.socket = clientSocket;
            this.mailingTask = clientMailingTask;
        }

        @Override
        public void run() {
            try {
                Scanner in = new Scanner(socket.getInputStream());
                while (in.hasNextLine()) {
                    //using just one append for sync purpose
                    globalMessageStringBuffer.append(socket + ": " + in.nextLine() + " " + LocalDateTime.now() + "\n");
                    System.out.println("Buffer updated: \n" + globalMessageStringBuffer);
                    if (Server.numberOfMessagesInBuffer.incrementAndGet() == 1) {
                        Server.makeMailing();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    mailingTasks.remove(mailingTask);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}