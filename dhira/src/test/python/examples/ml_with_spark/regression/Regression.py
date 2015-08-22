##day.csv and hour.csv

########        - instant: record index
########        - dteday : date
#        - season : season (1:springer, 2:summer, 3:fall, 4:winter)
#        - yr : year (0: 2011, 1:2012)
#        - mnth : month ( 1 to 12)
#        - hr : hour (0 to 23)
#        - holiday : weather day is holiday or not (extracted from http://dchr.dc.gov/page/holiday-schedule)
#        - weekday : day of the week
#        - workingday : if day is neither weekend nor holiday is 1, otherwise is 0.
#        + weathersit : 
#                - 1: Clear, Few clouds, Partly cloudy, Partly cloudy
#                - 2: Mist + Cloudy, Mist + Broken clouds, Mist + Few clouds, Mist
#                - 3: Light Snow, Light Rain + Thunderstorm + Scattered clouds, Light Rain + Scattered clouds
#                - 4: Heavy Rain + Ice Pallets + Thunderstorm + Mist, Snow + Fog
#        - temp : Normalized temperature in Celsius. The values are divided to 41 (max)
#        - atemp: Normalized feeling temperature in Celsius. The values are divided to 50 (max)
#        - hum: Normalized humidity. The values are divided to 100 (max)
#        - windspeed: Normalized wind speed. The values are divided to 67 (max)
########        - casual: count of casual users
########        - registered: count of registered users
#        - cnt: count of total rental bikes including both casual and registered


PATH = "/opt/datasets/Bike-Sharing-Dataset/"
raw_data = sc.textFile(PATH + "hour_noheader.csv")

num_data = raw_data.count()
records = raw_data.map(lambda x: x.split(","))
first = records.first()
print first
print num_data

records.cache()

def get_mapping(rdd, idx):
  return rdd.map(lambda fields: fields[idx]).distinct().zipWithIndex().collectAsMap()

print "Mapping of first categorical feasture column: %s" % get_mapping(records, 2)

mappings = [get_mapping(records, i) for i in range(2,10)]
cat_len = sum(map(len, mappings))
num_len = len(records.first()[11:15])
total_len = num_len + cat_len

print "Feature vector length for categorical features: %d" % cat_len
print "Feature vector length for numerical features: %d" % num_len
print "Total feature vector length: %d" % total_len

from pyspark.mllib.regression import LabeledPoint
import numpy as np

def extract_features(record):
  cat_vec = np.zeros(cat_len)
  i = 0
  step = 0
  for field in record[2:9]:
    m = mappings[i]
    idx = m[field]
    cat_vec[idx + step] = 1
    i = i + 1
    step = step + len(m)
  num_vec = np.array([float(field) for field in record[10:14]])
  return np.concatenate((cat_vec, num_vec))

def extract_label(record):
  return float(record[-1])

data = records.map(lambda r: LabeledPoint(extract_label(r), extract_features(r)))

first_point = data.first()
print "Raw data: " + str(first[2:])
print "Label: " + str(first_point.label)
print "Linear Model feature vector:\n" + str(first_point.features)
print "Linear Model feature vector length: " + str(len(first_point.features))


