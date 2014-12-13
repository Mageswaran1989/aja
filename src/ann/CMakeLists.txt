CMAKE_MINIMUM_REQUIRED (VERSION 2.6)
PROJECT (aja_ann)

#Adds all the *.cpp file in the current directory
FILE(GLOB aja_ann_src
    "*.cpp"
)

ADD_LIBRARY(aja_ann SHARED ${aja_ann_src})
target_include_directories (aja_ann PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
