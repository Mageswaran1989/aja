from nsetools import Nse

# Utility Functions
def dict_compare(d1, d2):
    d1_keys = set(d1.keys()) #set with unique keys
    d2_keys = set(d2.keys())
    intersect_keys = d1_keys.intersection(d2_keys)
    items_added = d1_keys - d2_keys
    items_removed = d2_keys - d1_keys
    #create dictionary for modyfied items
    items_modified = {key : (d1[key], d2[key]) for key in intersect_keys if d1[key] != d2[key]}
    items_same = set(key for key in intersect_keys if d1[key] == d2[key])
    return items_added, items_removed, items_modified, items_same


added, removed, modified, same = dict_compare(x, y)

nse = Nse()

stock_name = nse.get_quote('infy')

