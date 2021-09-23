#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Seren Piri <seren.piri@agem.com.tr>

import os.path
import sys
from base.system.system import System

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

from api.rsync import BackupRsync
from base.scope import Scope

def handle_task(task, context):
	Scope.get_instance().get_logger().debug(context.get('task_id'))
	backup_task_file = open('/etc/ahenk/lastbackuptask','w')
	backup_task_file.write(str(context.get('task_id')))
	backup_task_file.close()
	
	if '{MAC}' in task['destPath']:
		task['destPath'] = task['destPath'].replace('{MAC}', str(System.Hardware.Network.mac_addresses()[0]))
	if '{IP_ADDRESS}' in task['destPath']:
		task['destPath'] = task['destPath'].replace('{IP_ADDRESS}', str(System.Hardware.ip_addresses()[0]))

	task['destPath'] = task['destPath']+'/'+task['sourcePath']
	task['type']='b'
	
	task['sourcePath'] = task['sourcePath'] + '/'

	backup = BackupRsync(task, context)
	backup.backup()

	backup_task_file = open('/etc/ahenk/lastbackuptask','w')
	backup_task_file.write('')
	backup_task_file.close()