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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.18/xcframework.zip",
            checksum: "7871efd46b72338dce650547608c8f07877c12bc2a9a76c1f66d3882769ae6f6"
        )
    ]
)
