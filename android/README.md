# Android app skeleton for Isovm

This directory contains a minimal Android Studio app skeleton (Kotlin) that demonstrates:
- A simple MainActivity with controls to pick an ISO and start/stop a VM service
- A foreground VMService that launches an external QEMU binary (assumed to be present in the app's files dir)
- A placeholder VncSurfaceView where a VNC client/rendering implementation can be integrated

Notes
- This is a proof-of-concept skeleton. It does not include QEMU binaries. Place a suitable qemu-system-x86_64 (aarch64 build) binary at runtime in the app's files directory (context.filesDir) named `qemu-system-x86_64` and mark it executable.
- ABI: app is configured to include arm64-v8a in the native ABIs.
- Build settings use modern Android defaults; adjust compileSdk/targetSdk/AGP/Kotlin versions to the latest stable in your environment.

How this works (user flow)
1. The user taps "Import ISO" and selects an ISO file via Android's Storage Access Framework. The app copies that ISO into its private storage.
2. The user taps "Start VM" which starts a foreground VMService. VMService attempts to execute the bundled QEMU binary with the selected ISO as a virtual CD drive and starts QEMU with a VNC server listening on a loopback port.
3. The app currently provides a placeholder VncSurfaceView. Integrate a VNC client library (e.g. libvncclient via JNI or a Java VNC client) and connect to the VM's VNC port (127.0.0.1:5901) to display the guest.

Next steps for a user-friendly release
- Integrate a robust VNC client UI (keyboard, touch->mouse mapping, scaling)
- Add UI for VM settings (memory, CPU, disk images) and easy ISO import (batch, downloads)
- Implement secure sandboxing and permission guidance
- Provide one-click prebuilt QEMU binaries for supported ABIs and a failover to download them on first run
- Add snapshot/save-restore UI and storage management

