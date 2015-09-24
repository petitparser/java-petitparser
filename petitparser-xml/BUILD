package(default_visibility = ["//visibility:public"])

licenses(["notice"])  # MIT

java_library(
    name = "xml",
    srcs = glob(["src/main/**/*.java"]),
    deps = [
        "//petitparser-core:core",
    ],
)

java_binary(
    name = "benchmark",
    main_class = "org.petitparser.grammar.xml.XmlBenchmark",
    srcs = [
        "src/test/java/org/petitparser/grammar/xml/XmlBenchmark.java",
    ],
    deps = [
        ":xml",
        "//petitparser-core:core",
    ],
)

java_test(
    name = "test",
    size = "small",
    srcs = glob(["src/test/**/*Test.java"]),
    deps = [
        ":xml",
        "//third_party:junit4",
        "//petitparser-core:core",
    ],
)