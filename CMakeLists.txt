cmake_minimum_required(VERSION 2.8)

project(iVizonAI)

set(CMAKE_MODULE_PATH ${iVizonAI_SOURCE_DIR}/cmake)

# find OpenCL
find_package(OpenCL REQUIRED)
include_directories(SYSTEM ${OPENCL_INCLUDE_DIRS})

# find Boost
find_package(Boost 1.48 REQUIRED)
include_directories(SYSTEM ${Boost_INCLUDE_DIRS})

# optional support for c++11
option(BOOST_COMPUTE_USE_CPP11 "Use C++11 features" OFF)
if(${BOOST_COMPUTE_USE_CPP11})
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
endif()

# thread-safety options
option(BOOST_COMPUTE_THREAD_SAFE "Compile with BOOST_COMPUTE_THREAD_SAFE defined" OFF)
if(${BOOST_COMPUTE_THREAD_SAFE})
  add_definitions(-DBOOST_COMPUTE_THREAD_SAFE)
  if(${BOOST_COMPUTE_USE_CPP11})
    add_definitions(-DBOOST_COMPUTE_HAVE_THREAD_LOCAL)
  endif()
endif()

# optional third-party libraries
option(BOOST_COMPUTE_HAVE_EIGEN "Have Eigen" OFF)
option(BOOST_COMPUTE_HAVE_OPENCV "Have OpenCV" OFF)
option(BOOST_COMPUTE_HAVE_QT "Have Qt" OFF)
option(BOOST_COMPUTE_HAVE_VTK "Have VTK" OFF)
option(BOOST_COMPUTE_HAVE_CUDA "Have CUDA" OFF)
option(BOOST_COMPUTE_HAVE_TBB "Have TBB" OFF)

include_directories(include)
