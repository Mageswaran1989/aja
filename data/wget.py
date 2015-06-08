#import wget
url = "http://files.grouplens.org/datasets/movielens/ml-100k.zip"
filename = wget.download(url)
filename
