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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.17/xcframework.zip",
            checksum: "8b784be101031c8b177e96a29480253a56a95e42efbba9e2279c42f1ded0c61c"
        )
    ]
)
