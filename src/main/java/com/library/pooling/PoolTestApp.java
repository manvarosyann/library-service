package com.library.pooling;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CountDownLatch;

// "Bad Pool" Simulation
public class PoolTestApp {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/librarydb";
        String user = "postgres";
        String password = "postgres";

        DataSource dataSource = new SingleConnectionDataSource(url, user, password);

        int threadCount = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                synchronized (dataSource) {
                    Connection connection = null;
                    PreparedStatement preparedStatement = null;
                    try {
                        connection = dataSource.getConnection();
                        System.out.println(Thread.currentThread().getName() + " starting...");

                        preparedStatement = connection.prepareStatement("select pg_sleep(2)");
                        preparedStatement.execute();

                        System.out.println(Thread.currentThread().getName() + " finished");
                    } catch (Exception e) {
                        System.out.println("Thread failed" + e.getMessage());
                    } finally {
                        try {
                            if (preparedStatement != null) {
                                preparedStatement.close();
                            }
                        } catch (Exception e) {}
                        countDownLatch.countDown();
                    }
                }
            });
            thread.start();
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("Total time with SingleConnectionDataSource: " + (endTime - startTime) / 1000.0 + " seconds");
    }
}
