# DataSource vs. DriverManager in JDBC

When building Java applications that interact with databases, two key ways to get a connection are:

1. `DriverManager`: The traditional JDBC method.
2. `DataSource`: A modern, flexible, and preferred approach.

## What is DriverManager?
`DriverManager` is a basic JDBC utility that manages a list of database drivers and provides connections via:
<pre> 
Connection conn = DriverManager.getConnection(url, user, password);
</pre> 

### Advantages
1. Simple and straightforward.
2. Useful for small apps and learning projects.
3. No setup needed beyond JDBC driver and credentials.
### Disadvantages
1. Creates a new connection every time, which is costly in production.
2. Lacks connection pooling — a feature critical for performance and resource management.
3. Configuration must be hardcoded or handled manually.
4. No support for advanced connection features (timeouts, monitoring, etc.).

## What is DataSource?
`DataSource` is an interface from javax.sql that represents a source of database connections. It can be implemented by:
1. Connection pool libraries (e.g. HikariCP, Apache DBCP).
2. Application servers or containers via JNDI.
<pre> 
DataSource ds = ...;
Connection conn = ds.getConnection();
</pre> 

### Advantages
1. Supports connection pooling → reuses connections, improves performance.
2. It can be configured externally (e.g., via XML, properties files, Spring).
3. It can be managed by containers (e.g., Tomcat, Spring Boot).
4. Suitable for enterprise apps and microservices.
5. Cleaner and more testable (can be injected/mocked).
### Disadvantages
1. Requires more initial configuration.
2. Slightly more complex to set up if not using a framework.