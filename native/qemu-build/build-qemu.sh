#!/usr/bin/env bash
# build-qemu.sh - Opinionated skeleton to fetch and configure QEMU for Android/aarch64
# Usage: ANDROID_NDK_ROOT=/path/to/ndk ./build-qemu.sh

set -euo pipefail

# Configuration
QEMU_REPO=${QEMU_REPO:-https://git.qemu.org/git/qemu.git}
QEMU_TAG=${QEMU_TAG:-stable-8}   # change to desired release/tag
ANDROID_NDK_ROOT=${ANDROID_NDK_ROOT:-""}
OUTPUT_DIR=${OUTPUT_DIR:-$(pwd)/output}
BUILD_DIR=${BUILD_DIR:-$(pwd)/qemu-src}
ANDROID_API=${ANDROID_API:-21}
ABI=${ABI:-arm64-v8a}

if [ -z "$ANDROID_NDK_ROOT" ]; then
  echo "ERROR: ANDROID_NDK_ROOT must point to an Android NDK installation."
  echo "You can download the NDK from https://developer.android.com/ndk/downloads"
  exit 1
fi

mkdir -p "$OUTPUT_DIR" "$BUILD_DIR"

if [ ! -d "$BUILD_DIR/.git" ]; then
  git clone "$QEMU_REPO" "$BUILD_DIR"
  cd "$BUILD_DIR"
  git checkout "$QEMU_TAG" || true
else
  cd "$BUILD_DIR"
  git fetch --all --tags
  git checkout "$QEMU_TAG" || true
fi

# Create standalone toolchain (NDK r19+ uses clang and unified toolchain). We use clang by setting CC/AARCH64-specific flags.
# This is a simple approach and may require adjustments for newer NDKs.

export SYSROOT="$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/sysroot"
export AARCH64_TOOLCHAIN_PREFIX="$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android"
export CC="${AARCH64_TOOLCHAIN_PREFIX}-${ANDROID_API}-clang"
export CXX="${AARCH64_TOOLCHAIN_PREFIX}-${ANDROID_API}-clang++"
export AR="${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar"
export RANLIB="${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ranlib"

# Configure flags - we build a user-mode/system emulator without GUI dependencies where possible.
# Adjust --target-list as needed. We want qemu-system-x86_64 built to run as an aarch64 binary.

cd "$BUILD_DIR"
mkdir -p build-android && cd build-android

PKG_CONFIG_PATH="" \
CPPFLAGS="--sysroot=${SYSROOT}" \
LDFLAGS="--sysroot=${SYSROOT}" \
CC="$CC" CXX="$CXX" AR="$AR" RANLIB="$RANLIB" \
../configure \
  --prefix="$OUTPUT_DIR" \
  --disable-docs \
  --disable-tests \
  --enable-gtk=no \
  --enable-sdl=no \
  --disable-vnc \
  --disable-system \
  --enable-system \
  --target-list="x86_64-softmmu" \
  --host-list="aarch64-linux-gnu" || true

# Note: configure may fail depending on missing host dependencies and qemu version. Inspect the output and add/adjust flags.

make -j$(nproc) || true
make install || true

echo "Build finished. Artifacts (if any) are installed under: $OUTPUT_DIR"
