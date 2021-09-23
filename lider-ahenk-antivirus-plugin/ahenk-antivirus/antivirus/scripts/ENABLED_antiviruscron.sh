#!/bin/bash

line="* */$1 * * * $2antiviruscron.sh"
(crontab -u root -l; echo "$line" ) | crontab -u root -
