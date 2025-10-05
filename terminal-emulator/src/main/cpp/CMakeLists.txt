cmake_minimum_required(VERSION 3.22.1)
project(termux_terminal_native LANGUAGES C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_EXTENSIONS OFF)

add_library(termux SHARED
    termux.c
)

target_include_directories(termux PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
)

find_library(log-lib log)

target_link_libraries(termux PRIVATE ${log-lib})
