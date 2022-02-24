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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.0.39/xcframework-debug.zip",
            checksum: "aa89e119260def14f092d987bfc29425a5f7f0a9dfa03e6e126583f260bb21fa"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.0.39/xcframework-release.zip",
            checksum: "d68a58802c38c08f576d238ebf2e23fe9611afc44bb7402a18d2226a0ecb124a"
        )
    ]
)
