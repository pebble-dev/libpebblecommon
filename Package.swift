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
            targets: ["libpebblecommon-release"]),
        .library(
            name: "libpebblecommon-debug",
            targets: ["libpebblecommon-debug"])
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
    ],
    targets: [
        .binaryTarget(
            name: "libpebblecommon-debug",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.0/xcframework-debug.zip",
            checksum: "7a689ad04aeef7ea2e2dc9265e288d8b13e441e33afe96ff507069c4a0f10889"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.0/xcframework-release.zip",
            checksum: "d54614278beb9f729d0d866094e6ad91e8dd1b8b22ed5d234cb4dbd4589e04c7"
        )
    ]
)
