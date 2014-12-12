include_directories(../../include)

ADD_EXECUTABLE(example_fuzzifier example_fuzzifier.cpp)
target_link_libraries (example_fuzzifier  ${EXTRA_LIBS})
