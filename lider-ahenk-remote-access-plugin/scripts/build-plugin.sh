#!/bin/bash

###
# This script builds the project and generates Lider (lider-remote-access.jar) & Lider Console (lider-console-remote-access.jar) distribution files as well as Ahenk package (remote-access.deb)
#
# Generated files can be found under /tmp/lider-ahenk-remote-access-plugin/
###
set -e

pushd $(dirname $0) > /dev/null
PRJ_ROOT_PATH=$(dirname $(pwd -P))
popd > /dev/null
echo "Project path: $PRJ_ROOT_PATH"

# Generate third-party dependencies
echo "Generating third-party dependencies..."
cd "$PRJ_ROOT_PATH"/lider-console-remote-access-dependencies
mvn clean p2:site
echo "Generated third-party dependencies."

# Start jetty server for Tycho to use generated dependencies
echo "Starting server for Tycho..."
mvn jetty:run &
J_PID=$!
echo "Started server."

# Build project
echo "Building lider & lider-console modules..."
cd "$PRJ_ROOT_PATH"
mvn clean install -DskipTests
echo "lider & lider-console modules built successfully."

# After exporting products, kill jetty server process
echo "Shutting down server..."
kill $J_PID
echo "Server shut down."

# Generate Ahenk package
echo "Generating Ahenk package..."
cd "$PRJ_ROOT_PATH"/ahenk-remote-access
dpkg-buildpackage -b -uc
echo "Generated Ahenk package"

EXPORT_PATH=/tmp/lider-ahenk-remote-access-plugin
echo "Export path: $EXPORT_PATH"

# Copy resulting files
echo "Copying generated files to $EXPORT_PATH..."
mkdir -p "$EXPORT_PATH"
mv -f "$PRJ_ROOT_PATH"/*.deb "$EXPORT_PATH"
mv -f "$PRJ_ROOT_PATH"/*.changes "$EXPORT_PATH"
cp -rf "$PRJ_ROOT_PATH"/lider-remote-access/target/lider-*.jar "$EXPORT_PATH"
cp -rf "$PRJ_ROOT_PATH"/lider-console-remote-access/target/lider-console-*.jar "$EXPORT_PATH"
echo "Copied generated files."

echo "Built finished successfully!"
echo "Files can be found under: $EXPORT_PATH"
