package(default_visibility = ["//visibility:public"])

licenses(["notice"])  # MIT

java_toolchain(
    name = "java_toolchain",
    encoding = "UTF-8",
    source_version = "8",
    target_version = "8",
    misc = [
      "-extra_checks:on",
    ],
)

java_library(
    name="junit",
    exports = [
        "//external:hamcrest-core-jar",
        "//external:hamcrest-library-jar",
        "//external:junit-jar",
    ],
)