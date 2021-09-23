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
echo "Building the project..."
cd "$PRJ_ROOT_PATH"
mvn clean install -DskipTests
echo "The project built successfully."

EXPORT_PATH=/tmp/lider-ahenk-ad-migration-tool
echo "Export path: $EXPORT_PATH"

# Copy resulting files
echo "Copying generated files to $EXPORT_PATH..."
mkdir -p "$EXPORT_PATH"
cp -rf "$PRJ_ROOT_PATH"/target/lider-ahenk*.jar "$EXPORT_PATH"
cp -rf "$PRJ_ROOT_PATH"/src/main/resources/config.properties "$EXPORT_PATH"
echo "Copied files."

echo "Built finished successfully!"
echo "Files can be found under: $EXPORT_PATH"
