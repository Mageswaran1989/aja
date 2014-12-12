INCLUDE_DIRECTORIES(../../include)

ADD_EXECUTABLE(example_fuzzifier example_fuzzifier.cpp)
TARGET_LINK_LIBRARIES(example_fuzzifier LINK_PUBLIC aja_fuzzy)
