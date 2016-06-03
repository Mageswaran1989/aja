#Text Processing:
Common Crawl dataset of billions of websites contains over 840 billion individual words


#Text features
1. Bag-of-words
2. Term frequency-inverse document frequency (TF-IDF)
3. Feature hashing (1-of-K features encoding)

#Stemming
A common step in text processing and tokenization is stemming. This is the conversion
of whole words to a base form (called a word stem). For example, plurals might be
converted to singular (dogs becomes dog), and forms such as walking and walker might
become walk. Stemming can become quite complex and is typically handled with
specialized NLP or search engine software (such as NLTK, OpenNLP, and Lucene,
for example).

##TF-IDF
tf-idf(t,d) = tf(t,d) x idf(t)
tf - term frequency  (number of occurrences) of term t in document d
idf - inverse document frequency of term t in the corpus log(N/d)
N - is the total number of documents, and 
d - is the number of documents in which the term t occurs

##Datasets:
http://kdd.ics.uci.edu/databases/20newsgroups/20newsgroups.data.html.
http://qwone.com/~jason/20Newsgroups
