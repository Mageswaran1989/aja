**Dataset:**  http://www-etud.iro.umontreal.ca/~bergstrj/audioscrobbler_data.html

 Download the archive, and find within it several files. The main data set is in
the file user_artist_data.txt. It contains about 141,000 unique users, and 1.6 million
unique artists. About 24.2 million users’ plays of artists’ are recorded, along with their
count.

Files
-----

user_artist_data.txt
    3 columns: userid artistid playcount

artist_data.txt
    2 columns: artistid artist_name

artist_alias.txt
    2 columns: badid, goodid
    known incorrectly spelt artists and the correct artist id. 
    you can correct errors in user_artist_data as you read it in using this file
    (we're not yet finished merging this data)
    

Refer ALS in docs for more info!