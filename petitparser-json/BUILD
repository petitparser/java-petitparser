package(default_visibility = ["//visibility:public"])

licenses(["notice"])  # MIT

java_library(
    name = "json",
    srcs = glob(["src/main/**/*.java"]),
    deps = [
        "//petitparser-core:core",
    ],
)

java_test(
    name = "test",
    size = "small",
    srcs = glob(["src/test/**/*Test.java"]),
    deps = [
        ":json",
        "//:junit",
        "//petitparser-core:core",
    ],
)