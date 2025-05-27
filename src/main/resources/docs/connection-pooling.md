# Connection Pooling in Java

## What is Connection Pooling?

Connection pooling is a technique used to manage database connections efficiently by reusing a fixed number of
connections rather than creating and closing them for each operation. It improves performance, reduces resource usage,
and avoids connection exhaustion.

## Why is it Needed?

1. Performance: Creating a DB connection is expensive. Reusing them is faster.
2. Concurrency: A single connection cannot handle multiple threads simultaneously.
3. Resource Management: Pooled connections are limited and managed efficiently.
4. Stability: Prevents exhausting DB resources under load.

## Experiment Setup
### Without Pooling
We created a `SingleConnectionDataSource` that returned the same connection to all threads. Each thread ran:
```sql
select pg_sleep(2)
```
Since only one thread could use the connection at a time, all five threads ran sequentially.
Total time: ~10 seconds

### With HikariCP
We configured HikariCP with a pool size of 5, allowing each thread to acquire its own connection and run in parallel.
Total time: ~2.1 seconds

### HikariCP Advantages
1. High performance and low latency
2. Simple configuration
3. Widely adopted in Java ecosystems
4. Actively maintained and lightweight

