CMAKE_MINIMUM_REQUIRED(VERSION 2.8)

PROJECT(Aja)

#The version Number
SET(Aja_MAJOR_NUMBER 0)
SET(Aja_MINOR_NUMBER 1)

# configure a header file to pass some of the CMake settings
# to the source code
CONFIGURE_FILE(
  "${PROJECT_SOURCE_DIR}/cmake/aja_config.h.in"
  "${PROJECT_BINARY_DIR}/include/aja/config/aja_config.h"
  )

# make sure the directory exiats before copying config header
FILE(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/include/aja/config/)
INCLUDE_DIRECTORIES("${PROJECT_BINARY_DIR}/include/aja/config/")

SET(CMAKE_MODULE_PATH ${Aja_SOURCE_DIR}/cmake)

# find OpenCL
#find_package(OpenCL REQUIRED)
#include_directories(SYSTEM ${OPENCL_INCLUDE_DIRS})

# find Boost
#find_package(Boost 1.48 REQUIRED)
#include_directories(SYSTEM ${Boost_INCLUDE_DIRS})

# optional support for c++11
#option(BOOST_COMPUTE_USE_CPP11 "Use C++11 features" OFF)
#if(${BOOST_COMPUTE_USE_CPP11})
#  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
#endif()

# options
OPTION(AJA_MAKE_VERBOSE "Verbose mode in make" ON)
OPTION(AJA_HAVE_OPENCV "Have OpenCV" OFF)
OPTION(AJA_HAVE_QT "Have Qt" OFF)
OPTION(AJA_HAVE_VTK "Have VTK" OFF)
OPTION(AJA_HAVE_CUDA "Have CUDA" OFF)
OPTION(AJA_HAVE_TBB "Have TBB" OFF)
OPTION(AJA_BUILD_DEBUG "Build with debug symbols" ON)

IF(${AJA_MAKE_VERBOSE})
   SET(CMAKE_VERBOSE_MAKEFILE on)
ENDIF()

IF(${AJA_BUILD_DEBUG})
   SET(CMAKE_BUILD_TYPE Debug)
ELSE(!${AJA_BUILD_DEBUG})
   SET(CMAKE_BUILD_TYPE Release)
ENDIF()

INCLUDE_DIRECTORIES(include)

ADD_SUBDIRECTORY(src)
ADD_SUBDIRECTORY(test)
ADD_SUBDIRECTORY(example)


