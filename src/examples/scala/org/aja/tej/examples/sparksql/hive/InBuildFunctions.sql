SHOW TABLES;

SHOW TABLES 'name.*' //Java REGEX

SHOW PARTITIONS table_name;

DESCRIBE table_name;

DESCRIBE EXTENDED table_name;

DESCRIBE EXTENDED table_name PARTITION (col_name='partition_name_here');

ALTER TABLE old_table_name RENAME TO new_table_name;

ALTER TABLE old_table_name RENAME REPLACE COLUMNS (col_name TYPE, ...);

ALTER TABLE table_name ADD COLUMNS (col_name TYPE COMMENT "...", col_name TYPE DEFAULT deafult_value_here)

DROP TABLE table_name;

ALTER TABLE table_name DROP PARTITION (col_name=value)