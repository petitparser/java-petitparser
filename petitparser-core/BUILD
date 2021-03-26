package(default_visibility = ["//visibility:public"])

licenses(["notice"])  # MIT

java_library(
    name = "main",
    srcs = glob(["src/main/**/*.java"]),
    deps = [
        "@maven//:com_google_code_findbugs_jsr305",
    ],
)

java_library(
    name = "test",
    srcs = glob(["src/test/**/*.java"]),
    deps = [
        ":main",
        "@maven//:junit_junit",
        "@maven//:org_hamcrest_hamcrest_core",
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
