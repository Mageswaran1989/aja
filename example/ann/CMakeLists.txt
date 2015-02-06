INCLUDE_DIRECTORIES(../../include)

#configure_file(data/weight.dat ${CMAKE_CURRENT_BINARY_DIR}/weight.dat COPYONLY)
#configure_file(data/input.dat ${CMAKE_CURRENT_BINARY_DIR}/input.dat COPYONLY)
FILE(COPY data/ DESTINATION ${CMAKE_CURRENT_BINARY_DIR}/)
ADD_EXECUTABLE(backpropagation_feedforward_network backpropagation_feedforward_network.cpp)
ADD_EXECUTABLE(hopfield_network hopfield_network.cpp)
ADD_EXECUTABLE(perceptron_network perceptron_network.cpp)
#TARGET_LINK_LIBRARIES(hopfield_network LINK_PUBLIC aja_ann)
