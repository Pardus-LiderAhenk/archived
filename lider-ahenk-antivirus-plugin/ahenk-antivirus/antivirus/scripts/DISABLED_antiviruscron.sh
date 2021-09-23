#!/bin/bash

line=$(sed '/antiviruscron.sh/d' /var/spool/cron/crontabs/root)
echo "$line" > /var/spool/cron/crontabs/root
