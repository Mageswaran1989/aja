INCLUDE_DIRECTORIES(../../include)

ADD_EXECUTABLE(hopfield_network hopfield_network.cpp)
TARGET_LINK_LIBRARIES(hopfield_network LINK_PUBLIC aja_ann)
