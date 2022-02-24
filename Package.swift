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
            url: "",
            checksum: "412db0f681e36b25591b94b5ccd1088838416f2ee6d4e435d9dcefc6144f2e09"
        ),
        .binaryTarget(
            name: "libpebblecommon-release",
            url: "",
            checksum: "de7947ecf5d6514847dfa72b488809db4f917138c5c608e428c51ea4259df610"
        )
    ]
)
