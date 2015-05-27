INCLUDE_DIRECTORIES(../../include)

ADD_EXECUTABLE(fuzzifier fuzzifier.cpp)
TARGET_LINK_LIBRARIES(fuzzifier LINK_PUBLIC aja_fuzzy)
