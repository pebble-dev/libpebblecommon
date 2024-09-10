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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.22/xcframework.zip",
            checksum: "4467603fe0aaae1499032242f7d74eba2cae1e8f406546587848bfbba69677e6"
        )
    ]
)
