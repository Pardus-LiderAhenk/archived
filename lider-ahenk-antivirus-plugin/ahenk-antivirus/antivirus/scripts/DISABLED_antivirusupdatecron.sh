#!/bin/bash

line=$(sed '/antivirusupdatecron.sh/d' /var/spool/cron/crontabs/root)
echo "$line" > /var/spool/cron/crontabs/root
