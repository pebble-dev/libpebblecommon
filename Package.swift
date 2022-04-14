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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.4/xcframework-debug.zip",
            checksum: "3f758caab9d50e10007f0d3236b69d3088e079ed57c2fa7130f0f9ab6d155fef"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.4/xcframework-release.zip",
            checksum: "bdf0e1776ae040d11ae12b6f64bb0856b323e1a5ebe298f1676af0bb07cda335"
        )
    ]
)
