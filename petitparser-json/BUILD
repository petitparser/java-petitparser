package(default_visibility = ["//visibility:public"])

licenses(["notice"])  # MIT

java_library(
    name = "main",
    srcs = glob(["src/main/**/*.java"]),
    deps = ["//petitparser-core:main"],
)

java_library(
    name = "test",
    srcs = glob(["src/test/**/*.java"]),
    deps = [
        ":main",
        "//external:junit",
        "//external:hamcrest",
        "//petitparser-core:main",
        "//petitparser-core:test"
    ],
)

[java_test(
    name = FILE[:-5], 
    srcs = [FILE],
    deps = [
        ":main",
        ":test",
        "//petitparser-core:main",
    ],
) for FILE in glob(["src/test/**/*Test.java"])]