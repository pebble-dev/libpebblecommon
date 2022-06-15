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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.5/xcframework.zip",
            checksum: "7293fac736ef8a083c4e74d608301f299d4d91b84b537abbec492c8fb433af69"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "RELEASE-URL",
            checksum: "RELEASE-CHECKSUM"
        )
    ]
)
