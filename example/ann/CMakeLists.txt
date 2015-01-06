INCLUDE_DIRECTORIES(../../include)

configure_file(weight.dat ${CMAKE_CURRENT_BINARY_DIR}/weight.dat COPYONLY)
configure_file(input.dat ${CMAKE_CURRENT_BINARY_DIR}/input.dat COPYONLY)
ADD_EXECUTABLE(hopfield_network hopfield_network.cpp)
ADD_EXECUTABLE(perceptron_network perceptron_network.cpp)
#TARGET_LINK_LIBRARIES(hopfield_network LINK_PUBLIC aja_ann)
