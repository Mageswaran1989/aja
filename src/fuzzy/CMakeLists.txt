CMAKE_MINIMUM_REQUIRED (VERSION 2.6)
PROJECT (aja_fuzzy)

ADD_LIBRARY(aja_fuzzy SHARED fuzzifier.cpp) 
#target_include_directories (aja_fuzzy PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

