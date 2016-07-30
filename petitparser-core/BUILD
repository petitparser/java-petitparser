package(default_visibility = ["//visibility:public"])

licenses(["notice"])  # MIT

java_library(
    name = "main",
    srcs = glob(["src/main/**/*.java"]),
)

java_library(
    name = "test",
    srcs = glob(["src/test/**/*.java"]),
    deps = [
        ":main",
        "//external:junit",
        "//external:hamcrest",
    ],
)

[java_test(
    name = FILE[:-5], 
    srcs = [FILE],
    deps = [
        ":main",
        ":test",
    ],
) for FILE in glob(["src/test/**/*Test.java"])]
