# QEMU build scaffolding for Isovm

This directory contains base files to build or fetch QEMU binaries for use in the Android APK VM project.

Goals
- Provide an opinionated starting point for building qemu-system-x86_64 that can be integrated into an Android app.
- Target ABI: arm64-v8a (hosted on Android devices). Note: this means building QEMU as a native binary for Android (aarch64) that emulates x86_64 guests.
- Prefer using a reproducible Docker environment for Linux-based cross-builds.

Notes and caveats
- Building QEMU for Android is non-trivial. Many projects either cross-compile with the Android NDK or bundle prebuilt QEMU binaries.
- Performance: emulating x86_64 guests on an ARM host requires full CPU emulation (no KVM) and will be slow. Devices with KVM exposed can use kernel acceleration but require different packaging and permissions.
- This scaffold does not include binary blobs. Use the build script or the download script to obtain binaries.

See the scripts below for usage examples.
