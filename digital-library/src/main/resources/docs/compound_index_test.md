# Compound Index Analysis: `borrowing(user_id, return_date, borrow_date)`

## Why do we need a Compound Index?
This query pattern is widespread in our system — users often want to:
1. View their currently borrowed books
2. See the most recent borrowings first

As the borrowing table grows (we have millions of rows), this type of query becomes slow without optimization:
1. Without an index, PostgreSQL performs a sequential scan, filtering millions of rows and sorting them
2. A compound index allows the database to quickly locate matching rows and return them already sorted

By creating a compound index on (user_id, return_date, borrow_date):
1. The database uses the index to filter and sort in a single operation
2. We reduce execution time and resource usage


### Use Case
In our library system, we frequently run queries that:
- Filter by `user_id`
- Filter by `return_date IS NULL`
- Sort results by `borrow_date DESC`

Example query:
```sql
SELECT * FROM borrowing
WHERE user_id = 1
  AND return_date IS NULL
ORDER BY borrow_date DESC;
```

