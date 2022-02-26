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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.1/xcframework-debug.zip",
            checksum: "f5d544b0928ea9ba4ded420a98dd58975c0c90bf478994e72d468fdb50d07fa6"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.1/xcframework-release.zip",
            checksum: "5849ea01c94de5c9399b919ce5380fd205d1993ec9fd4786fdb99749a5a4be37"
        )
    ]
)
