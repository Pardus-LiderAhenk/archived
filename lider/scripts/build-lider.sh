#!/bin/bash

###
# This script builds the project and generates Lider distribution (Lider.tar.gz)
#
# Generated file can be found under /tmp/lider
###
set -e

pushd $(dirname $0) > /dev/null
PRJ_ROOT_PATH=$(dirname $(pwd -P))
popd > /dev/null
echo "Project path: $PRJ_ROOT_PATH"

# Build project
echo "Building lider project..."
cd "$PRJ_ROOT_PATH"
mvn clean install -DskipTests
echo "lider project built successfully."

# Generate Lider packages
echo "Generating Lider packages"
cd "$PRJ_ROOT_PATH"/lider-distro
mvn clean install -DskipTests
# Rename resulting files
cd "$PRJ_ROOT_PATH"/lider-distro/target
mv lider-distro-*.tar.gz lider.tar.gz
mv lider-distro-*.zip lider.zip
echo "Generated Lider packages"

EXPORT_PATH=/tmp/lider
echo "Export path: $EXPORT_PATH"

# Copy resulting files
echo "Copying generated lider packages to $EXPORT_PATH..."
mkdir -p "$EXPORT_PATH"
cp -rf "$PRJ_ROOT_PATH"/lider-distro/target/lider* "$EXPORT_PATH"
echo "Copied exported lider-console products."

echo "Built finished successfully!"
echo "Files can be found under: $EXPORT_PATH"
