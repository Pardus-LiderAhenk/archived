#!/usr/bin/python3
# -*- coding: utf-8 -*-


import os.path
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

from api.rsync import BackupRsync


def handle_task(task, context):
    task['type'] = 'r'
    backup = BackupRsync(task, context)
    backup.backup()
