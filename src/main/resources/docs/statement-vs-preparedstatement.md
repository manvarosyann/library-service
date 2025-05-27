# Difference Between Statement and PreparedStatement in Java

In Java JDBC, `Statement` and `PreparedStatement` are both used to execute SQL queries against a database. However, they differ significantly in terms of security, performance, and usability.

## Statement
`Statement` is used to execute simple SQL queries without parameters.

### Example
<pre> 
Statement statement = connection.createStatement();
ResultSet resultSet = statement.executeQuery("select * from users where username = 'admin'");
</pre> 

### Characteristics:
1. Not precompiled, SQL is compiled by the DB on each execution.
2. Prone to SQL injection if user input is directly embedded in the query.
3. Suitable only for static queries without parameters.

## PreparedStatement
`PreparedStatement` is used to execute parameterized SQL queries. The query is precompiled and can be executed multiple times with different parameters.

### Example
<pre> 
PreparedStatement preparedStatement = connection.prepareStatement("select * from users where username = ?");
preparedStatem
ent.setString(1, "admin");
ResultSet resultSet = preparedStatement.executeQuery();
</pre> 

### Characteristics:
1. Prevents SQL injection, parameters are safely bound, not concatenated.
2. Offers better performance for repeated executions (query is precompiled).
3. Easier to read, maintain, and scale for dynamic input.

