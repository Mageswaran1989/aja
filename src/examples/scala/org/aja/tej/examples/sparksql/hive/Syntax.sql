CREATE TABLE table_name(col_name1 TYPE COMMENT "...",
                        col_name2 TYPE,
                        col_name3 ARRAY<TYPE>,
                        col_name4 MAP<TYPE, TYPE>
                        ...)
COMMENT "..."
PARTITIONED BY(col_name1 TYPE,
               new_col_name TYPE,
               ...)
CLUSTERED BY(col_name) SORTED BY(col_name2) INTO n BUCKETS #each bucket sorted internally
ROW FORMAT DELIMITED
       FIELDS TERMINATED BY '?'
       COLLECTIONS ITEMS TERMINATED BY '?'
       MAP KEYS TERMINATED BY '?'
STORED AS SEQUENCEFILE;
#Default delimiter is ASCII 001 (ctrl-A)