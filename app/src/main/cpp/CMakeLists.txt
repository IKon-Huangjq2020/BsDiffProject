cmake_minimum_required(VERSION 3.10.2)

# 设置路径
file(GLOB bzip_source ${CMAKE_SOURCE_DIR}/bzip/*.c)
# 加载头文件
include_directories(bzip)


add_library(
        bspatcher

        SHARED

        ${bzip_source}
        bspatch.c
        native-lib.cpp
)


find_library(
        log-lib
        log)


target_link_libraries(
        bspatcher

        ${log-lib})