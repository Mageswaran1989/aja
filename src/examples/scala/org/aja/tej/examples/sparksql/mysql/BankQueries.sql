create database aja;

--Run BankDatabase.sql

show tables;

desc customer;

-----------------------------------------------------
SELECT emp_id, fname, lname
FROM employee
WHERE lname = 'Bkadfl';

SELECT fname, lname FROM employee;

-----------------------------------------------------
SELECT * FROM department;
SELECT dept_id, name FROM department;

SELECT name FROM department;

-----------------------------------------------------
SELECT emp_id, 'ACTIVE', emp_id * 3.14159, UPPER(lname) FROM employee;
--with custom column names

SELECT emp_id, 'ACTIVE' status, emp_id * 3.14159 empid_x_pi, UPPER(lname) last_name_upper FROM employee;

SELECT emp_id, 'ACTIVE' AS status, emp_id * 3.14159 AS empid_x_pi, UPPER(lname) AS last_name_upper FROM employee;

-----------------------------------------------------
SELECT VERSION(), USER(), DATABASE();
-----------------------------------------------------
SELECT cust_id FROM account;
SELECT DISTINCT cust_id FROM account;

