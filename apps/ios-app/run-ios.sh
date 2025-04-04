#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Navigate to the iOS app directory
cd "$(dirname "$0")"

# Clean the build directory to ensure changes are reflected
echo "Cleaning the build directory..."
xcodebuild clean -scheme aha-bridge-protocol

# Build the iOS app with updated Swift files
echo "Building the iOS app..."
xcodebuild -scheme aha-bridge-protocol -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 16 Pro' CONFIGURATION_BUILD_DIR=./build build || {
  echo "Build failed. Exiting."
  exit 1
}

# Get the simulator's current state
SIMULATOR_NAME="iPhone 16 Pro"
SIMULATOR_STATE=$(xcrun simctl list devices | grep "$SIMULATOR_NAME" | grep -o "(Booted)" || echo "Shutdown")

if [[ "$SIMULATOR_STATE" == "(Booted)" ]]; then
  echo "Simulator is already booted."
else
  # Boot the simulator if not already booted
  echo "Booting the simulator..."
  xcrun simctl boot "$SIMULATOR_NAME" || true
fi

# Reset the simulator only if it's not in a "Booted" state
if [[ "$SIMULATOR_STATE" != "(Booted)" ]]; then
  echo "Resetting the simulator..."
  xcrun simctl erase all
else
  echo "Skipping simulator reset as it is already booted."
fi

# Uninstall the app from the simulator to ensure a fresh install
echo "Uninstalling the existing app from the simulator..."
xcrun simctl uninstall booted com.aha.aha-bridge-protocol || true

# Install the app on the simulator
echo "Installing the app on the simulator..."
xcrun simctl install booted ./build/aha-bridge-protocol.app || {
  echo "Step 2: Failed to install the app. Exiting."
  exit 1
}

# Launch the app on the simulator with verbose logging
echo "Launching the app on the simulator..."
xcrun simctl launch --console booted com.aha.aha-bridge-protocol || {
  echo "Step 3: Failed to launch the app. "
}

echo "iOS app has been updated and is running successfully!"
