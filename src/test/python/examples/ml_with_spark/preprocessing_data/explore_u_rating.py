#Dataset : /opt/datasets/ml-100k                                                                                        
                                                                                                                        
PATH = "/opt/datasets/ml-100k/u.data"
rating_data_raw = sc.textFile(PATH)
print rating_data_raw.first()
num_ratings = rating_data_raw.count()
print "Ratings: %d" % num_ratings

rating_data = rating_data_raw.map(lambda line: line.split("\t"))
ratings = rating_data.map(lambda fields: int(fields[2]))
max_rating = ratings.reduce(lambda x, y: max(x, y))
min_rating = ratings.reduce(lambda x, y: min(x, y))
mean_rating = ratings.reduce(lambda x, y: x + y) / num_ratings
median_rating = np.median(ratings.collect())
ratings_per_user = num_ratings / num_users
ratings_per_movie = num_ratings / num_movies
print "Min rating: %d" % min_rating
print "Max rating: %d" % max_rating
print "Average rating: %2.2f" % mean_rating
print "Median rating: %d" % median_rating
print "Average # of ratings per user: %2.2f" % ratings_per_user
print "Average # of ratings per movie: %2.2f" % ratings_per_movie

ratings.stats()


count_by_rating = ratings.countByValue()
x_axis = np.array(count_by_rating.keys())
y_axis = np.array([float(c) for c in count_by_rating.values()])
# we normalize the y-axis here to percentages
y_axis_normed = y_axis / y_axis.sum()
pos = np.arange(len(x_axis))
width = 1.0
ax = plt.axes()
ax.set_xticks(pos + (width / 2))
ax.set_xticklabels(x_axis)
plt.bar(pos, y_axis_normed, width, color='lightblue')
plt.xticks(rotation=30)
fig = matplotlib.pyplot.gcf()
fig.set_size_inches(16, 10)


user_ratings_grouped = rating_data.map(lambda fields: (int(fields[0]),int(fields[2]))).groupByKey()

user_ratings_byuser = user_ratings_grouped.map(lambda (k, v): (k, len(v)))
user_ratings_byuser.take(5)

user_ratings_byuser_local = user_ratings_byuser.map(lambda (k, v): v).collect()
hist(user_ratings_byuser_local, bins=200, color='lightblue', normed=True)
fig = matplotlib.pyplot.gcf()
fig.set_size_inches(16,10)

#Transforming timestamps into categorical features

def extract_datetime(ts):
  import datetime
  return datetime.datetime.fromtimestamp(ts)


timestamps = rating_data.map(lambda fields: int(fields[3]))
hour_of_day = timestamps.map(lambda ts: extract_datetime(ts).hour)
hour_of_day.take(5)

def assign_tod(hr):
  times_of_day = {
    'morning' : range(7, 12),
    'lunch' : range(12, 14),
    'afternoon' : range(14, 18),
    'evening' : range(18, 23),
    'night' : range(23, 7)
  }
  for k, v in times_of_day.iteritems():
     if hr in v:
     return k

time_of_day = hour_of_day.map(lambda hr: assign_tod(hr))
time_of_day.take(5)


