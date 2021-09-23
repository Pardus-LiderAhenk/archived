#!/bin/bash

while read -r line 
do 
	clamscan -irv $line --log=/var/log/clamavscanlog
done < "/usr/share/ahenk/plugins/antivirus/antivirusscanfolder"
