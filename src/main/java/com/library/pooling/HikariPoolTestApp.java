package com.library.pooling;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CountDownLatch;

public class HikariPoolTestApp {
    public static void main(String[] args) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/librarydb");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setMaximumPoolSize(5);

        DataSource dataSource = new HikariDataSource(config);

        int threadCount = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try (Connection connection = dataSource.getConnection();
                     PreparedStatement stmt = connection.prepareStatement("select pg_sleep(2)")) {

                    System.out.println(Thread.currentThread().getName() + " starting...");
                    stmt.execute();
                    System.out.println(Thread.currentThread().getName() + " finished");

                } catch (Exception e) {
                    System.out.println("Thread failed: " + e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
            thread.start();
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("Total time with HikariCP: " + (endTime - startTime) / 1000.0 + " seconds");

        if (dataSource instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }
}
