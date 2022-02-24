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
            targets: ["libpebblecommon-debug", "libpebblecommon-release"])
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
    ],
    targets: [
        .binaryTarget(
            name: "libpebblecommon-debug",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.0.40/xcframework-debug.zip",
            checksum: "25a282b2f1b637c5cbc710c812376a39a0f4d59b0d0e375f3fdc2d5af0c8a4ad"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.0.40/xcframework-release.zip",
            checksum: "2b954b64a57258f8dfc5b23ff21cc213081efd695b331eda094aa614b64064b3"
        )
    ]
)
