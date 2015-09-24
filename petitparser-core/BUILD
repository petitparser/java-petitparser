package(default_visibility = ["//visibility:public"])

licenses(["notice"])  # MIT

java_library(
    name = "core",
    srcs = glob(["src/main/**/*.java"]),
)

java_library(
    name = "test-support",
    srcs = [
        "src/test/java/org/petitparser/Assertions.java",
    ],
    deps = [
        ":core",
        "//:junit",
    ],
)

java_test(
    name = "test",
    size = "small",
    srcs = glob(["src/test/**/*Test.java"]),
    deps = [
        ":core",
        ":test-support",
        "//:junit",
    ],
)