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

# Generate Lider packages
echo "Generating Javadoc"
cd "$PRJ_ROOT_PATH"
mvn clean javadoc:aggregate
cd "$PRJ_ROOT_PATH"/target/site
tar -zcf lider-console-javadoc.tar.gz apidocs
cd "$PRJ_ROOT_PATH"
echo "Generated Javadoc"

EXPORT_PATH=/tmp/lider-console
echo "Export path: $EXPORT_PATH"

# Copy resulting files
echo "Copying generated Javadoc to $EXPORT_PATH..."
mkdir -p "$EXPORT_PATH"
mv -f "$PRJ_ROOT_PATH"/target/site/lider-console-javadoc.tar.gz "$EXPORT_PATH"
echo "Copied generated Javadoc."

echo "Operation finished successfully!"
echo "Files can be found under: $EXPORT_PATH"
