// swift-tools-version:5.3
import PackageDescription
let package = Package(
    name: "libpebblecommon",
    platforms: [
        .iOS(.v9)
    ],
    products: [
        // Products define the executables and libraries a package produces, and make them visible to other packages.
        .library(
            name: "libpebblecommon",
            targets: ["libpebblecommon"]
        )
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
    ],
    targets: [
        .binaryTarget(
            name: "libpebblecommon",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.24/xcframework.zip",
            checksum: "1e9a8137d3cbf07d35b4d0a725b26d8327e8daca2016246acacfc00632ba2ffe"
        )
    ]
)
