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
            url: "https://github.com/pebble-dev/libpebblecommon/releases/download/0.1.15/xcframework.zip",
            checksum: "dd2f5dd764956daf00cf0b6dcbc9af88da104fcdfa7813d9f344b97af77ec4a1"
        )
    ]
)
