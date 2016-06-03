#Dataset : /opt/datasets/ml-100k                                                                                        
                                                                                                                        
PATH = "/opt/datasets/ml-100k/u.item"
movie_data = sc.textFile(PATH)
movie_data.first()
movie_data.take(10)

def convert_year(x):
    try:
        return int(x[-4:])
    except:
        return 1900 # there is a 'bad' data point with a blank year,which we set to 1900 and will filter out later

movie_fields  = movie_data.map(lambda lines: lines.split("|"))
years = movie_fields.map(lambda fields: fields[2]).map(lambda x: convert_year(x))

years_filtered = years.filter(lambda x: x != 1900)
movie_ages = years_filtered.map(lambda yr: 1998 - yr).countByValue()
values = movie_ages.values()
bins = movie_ages.keys()
hist(values, bins=bins, color='lightblue', normed=True)
fig = matplotlib.pyplot.gcf()
fig.set_size_inches(16,10)

# Filling in bad/missing data

years_pre_processed = movie_fields.map(lambda fields: fields[2]).map(lambda x: convert_year(x)).collect()
years_pre_processed_array = np.array(years_pre_processed)

mean_year = np.mean(years_pre_processed_array[years_pre_processed_array!=1900])
median_year = np.median(years_pre_processed_array[years_pre_processed_array!=1900])
index_bad_data = np.where(years_pre_processed_array==1900)[0][0]
years_pre_processed_array[index_bad_data] = median_year
print "Mean year of release: %d" % mean_year
print "Median year of release: %d" % median_year
print "Index of '1900' after assigning median: %s" % np.where(years_pre_processed_array == 1900)[0]

#Text feature extraction

def extract_title(raw):
  import re
  # this regular expression finds the non-word (numbers) between parentheses
  #print("1.>>>" + raw)
  grps = re.search("\((\w+)\)", raw)
  #print(grps.start())
  if grps:
    # we take only the title part, and strip the trailing whitespace from the remaining text, below
    #print("3.>>>" + raw[:grps.start()].strip())
    return raw[:grps.start()].strip()
  else:
    return raw

raw_titles = movie_fields.map(lambda fields: fields[1])

for raw_title in raw_titles.take(5):
  print extract_title(raw_title)

movie_titles = raw_titles.map(lambda m: extract_title(m))
# next we tokenize the titles into terms. We'll use simple whitespace tokenization
title_terms = movie_titles.map(lambda t: t.split(" "))
print title_terms.take(5)

# next we would like to collect all the possible terms, in order to build out dictionary of term <-> index mappings
# unique terms using distinct()
all_terms = title_terms.flatMap(lambda x: x).distinct().collect()
# create a new dictionary to hold the terms, and assign the "1-of-k" indexes
idx = 0
all_terms_dict = {}
for term in all_terms:
  all_terms_dict[term] = idx
  idx +=1

print "Total number of terms: %d" % len(all_terms_dict)
print "Index of term 'Dead': %d" % all_terms_dict['Dead']
print "Index of term 'Rooms': %d" % all_terms_dict['Rooms']


all_terms_dict2 = title_terms.flatMap(lambda x: x).distinct().zipWithIndex().collectAsMap()
print "Index of term 'Dead': %d" % all_terms_dict2['Dead']
print "Index of term 'Rooms': %d" % all_terms_dict2['Rooms']

# this function takes a list of terms and encodes it as a scipy sparse vector using an approach
# similar to the 1-of-k encoding
def create_vector(terms, term_dict):
  from scipy import sparse as sp
  num_terms = len(term_dict)
  x = sp.csc_matrix((1, num_terms))
  for t in terms:
    if t in term_dict:
    idx = term_dict[t]
    x[0, idx] = 1
return x


all_terms_bcast = sc.broadcast(all_terms_dict)
term_vectors = title_terms.map(lambda terms: create_vector(terms, all_terms_bcast.value))
term_vectors.take(5)


np.random.seed(42)
x = np.random.randn(10)
norm_x_2 = np.linalg.norm(x)
normalized_x = x / norm_x_2
print "x:\n%s" % x
print "2-Norm of x: %2.4f" % norm_x_2
print "Normalized x:\n%s" % normalized_x
print "2-Norm of normalized_x: %2.4f" %np.linalg.norm(normalized_x)


# Using MLlib for feature normalization

from pyspark.mllib.feature import Normalizer
normalizer = Normalizer()
vector = sc.parallelize([x])

normalized_x_mllib = normalizer.transform(vector).first().toArray()

print "x:\n%s" % x
print "2-Norm of x: %2.4f" % norm_x_2
print "Normalized x MLlib:\n%s" % normalized_x_mllib
print "2-Norm of normalized_x_mllib: %2.4f" % np.linalg.norm(normalized_x_mllib)



