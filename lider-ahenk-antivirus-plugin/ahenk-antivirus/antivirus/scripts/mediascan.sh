#!/bin/bash
if [[ $1 ==  "sd"* ]] || [[ $1 ==  "fd"*  ]] || [[ $1 ==  "sr"*  ]]
then
    echo "Media Tipine Bakiliyor..." >> /var/log/clamavscanlog
    echo $1 >> /var/log/clamavscanlog
    sleep 2
    disktype=$(udevadm info --name=/dev/$1 | grep ID_TYPE | cut -d "=" -f2)
    echo "Media Tipi -> $disktype" >> /var/log/clamavscanlog
    if [[ "$disktype" == "floppy" ]]
    then
        floppyScanState=$(grep -c "floppy" $2/antivirus.policy)
        if  [ $floppyScanState -eq 1 ]
        then
        	echo "Floppy Taramasi Yapiliyor... Bu işlem biraz zaman alacaktir...  ($1)" >> /var/log/clamavscanlog
        	$(mkdir /tmp/$1)
        	$(mount -o rw /dev/$1 /tmp/$1 )
        	result=$(clamscan -riav --bell /tmp/$1/)
        	echo "$result" >> /var/log/clamavscanlog
        	$(umount /dev/$1)
        	$(umount /tmp/$1)
        	$(rm -rf /tmp/$1)
        	echo "Floppy taramasi tamamlandi. Floppy mount ediliyor." >> /var/log/clamavscanlog
        fi
     fi

    if [[ "$disktype" == "cd" ]]
    then
	   cdromScanState=$(grep -c "cd" $2/antivirus.policy)
	    if  [ $cdromScanState -eq 1 ]
	    then
        	echo "CDROM Taramasi Başlatiliyor... Bu işlem biraz zaman alacaktir... ($1)" >> /var/log/clamavscanlog
        	$(mkdir /tmp/$1)
        	#$(mount -o rw /dev/$1 /tmp/$1 )
        	sleep 3
        	$(mount /dev/$1 /tmp/$1 )
        	result=$(clamscan -riav --bell /tmp/$1/)
        	echo "$result" >> /var/log/clamavscanlog
        	#$(umount /dev/$1)
        	$(umount /tmp/$1)
        	$(rm -rf /tmp/$1)
        	$(mkdir -p /media/cdrom/$1)
            $(chmod 777 /media/cdrom/$1)
            $(mount --bind /dev/$1 /media/cdrom/$1)
            echo "CDROM taramasi tamamlandi. CDROM mount ediliyor." >> /var/log/clamavscanlog
    	fi
    fi

    if [[ "$disktype" == "disk" ]]
    then
	    diskScanState=$(grep -c "usb" $2/antivirus.policy)
	    if  [ $diskScanState -eq 1 ]
	    then
        	echo " Disk Taraması Başlatılıyor... Bu işlem biraz zaman alacaktir... ($1)" >> /var/log/clamavscanlog
       		$(mkdir /tmp/$1)
        	$(mount -o rw /dev/$1 /tmp/$1 )
        	result=$(clamscan -riav --bell /tmp/$1/)
        	echo "$result" >> /var/log/clamavscanlog
        	$(umount /dev/$1)
        	$(umount /tmp/$1)
        	$(rm -rf /tmp/$1)
        	echo "Disk taramasi tamamlandi. Disk mount ediliyor." >> /var/log/clamavscanlog
	    fi
    fi
fi