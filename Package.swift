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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.0.41/xcframework-debug.zip",
            checksum: "8dabe6f76b32df3fbac97b85586fd0410685df8c525a326b750ff38719a18679"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.0.41/xcframework-release.zip",
            checksum: "b55cbf28297150a2393224870c28dd261a880af462eea73dde783f98e2182f43"
        )
    ]
)
