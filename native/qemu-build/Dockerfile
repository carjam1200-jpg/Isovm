FROM ubuntu:22.04

# Minimal environment for building QEMU in Docker

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
    autoconf \
    automake \
    libtool \
    pkg-config \
    gcc \
    g++ \
    make \
    git \
    python3 \
    python3-pip \
    libglib2.0-dev \
    libfdt-dev \
    libpixman-1-dev \
    libssl-dev \
    wget \
    ca-certificates \
    unzip \
    cmake \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /work

# Note: this image does not include the Android NDK. Mount your NDK into /ndk when running the container

# Example run:
# docker build -t isovm-qemu-builder:latest .
# docker run --rm -it -v /path/to/android-ndk:/ndk -v $PWD/output:/work/output isovm-qemu-builder:latest /bin/bash
