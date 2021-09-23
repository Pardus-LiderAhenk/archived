#!/bin/bash
stuckclam=`lsof -t /var/log/clamav/freshclam.log`
#echo $stuckclam
if [[ $stuckclam ]]
then
kill -9 $stuckclam
logger freshclam killed
fi
(freshclam --quiet)
