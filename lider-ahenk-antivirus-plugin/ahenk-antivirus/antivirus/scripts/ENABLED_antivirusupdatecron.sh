#!/bin/bash

line="* */$1 * * * $2antivirusupdatecron.sh"
(crontab -u root -l; echo "$line" ) | crontab -u root -
