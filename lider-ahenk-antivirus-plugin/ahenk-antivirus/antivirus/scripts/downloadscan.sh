#!/bin/bash

echo "$2 Scan Started" >> /var/log/clamavscanlog
clamscan -riav --bell $1 --log=/var/log/clamavscanlog
echo "$1 Scan Finished" >> /var/log/clamavscanlog
