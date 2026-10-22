cmake_minimum_required(VERSION 3.22.1)
project("calmatrix")

# Same -O3 trick AI Secretary uses — applied to both Debug and Release
set(CMAKE_C_FLAGS_DEBUG   "${CMAKE_C_FLAGS_DEBUG} -O3" CACHE STRING "" FORCE)
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O3" CACHE STRING "" FORCE)
set(CMAKE_C_FLAGS_RELEASE   "-O3 -DNDEBUG" CACHE STRING "" FORCE)
set(CMAKE_CXX_FLAGS_RELEASE "-O3 -DNDEBUG" CACHE STRING "" FORCE)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/whisper.cpp/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/whisper.cpp/ggml/include)

add_library(whisper SHARED IMPORTED)
set_target_properties(whisper PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libwhisper.so)

add_library(whisper_bridge SHARED whisper_bridge.cpp)

target_link_libraries(whisper_bridge whisper log)