#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: 

from base.plugin.abstract_plugin import AbstractPlugin
from base.model.task_bean import TaskBean
from base.model.plugin_bean import PluginBean
import os
from base.scope import Scope

class Init(AbstractPlugin):
	def __init__(self, context):
		super(Init, self).__init__()
		self.context = context
		self.logger = self.get_logger()
		self.scope = Scope.get_instance()
		self.pluginManager = self.scope.get_plugin_manager()
		self.db_service = self.scope.get_db_service()
		self.task_manager = self.scope.get_task_manager()

		self.logger.debug('Parameters were initialized.')

	def handle_init_mode(self):
		self.logger.debug('BACKUP INIT MODE RUNNING...')
		if not os.path.exists('/etc/ahenk/lastbackuptask'):
			open('/etc/ahenk/lastbackuptask','w+').close()

		bakcup_file = open('/etc/ahenk/lastbackuptask','r')
		task_id = bakcup_file.readline()
		if task_id != None and task_id != '':
			self.logger.debug('Tamamlanmamis backup process bulundu... Backup tekrar baslatiliyor.')
			self.logger.debug(task_id)
			task = self.get_task_by_id(int(task_id))
			self.logger.debug(task)
			self.scope.get_plugin_manager().process_task(task)

	def get_task_by_id(self, task_id):
		self.logger.debug('Getting task from db.')
		try:
			db_task = self.db_service.select('task', criteria='id={0}'.format(task_id))[0]
			return TaskBean(db_task[0], db_task[1], db_task[2], db_task[3], db_task[4], db_task[5],
							self.get_plugin_by_id(db_task[6]), db_task[7], db_task[8])
		except Exception as e:
			self.logger.debug('A problem occurred while getting task by id. Error Message: {0}'.format(str(e)))

	def get_plugin_by_id(self, plugin_id):
		self.logger.debug('Getting plugin from db.')
		db_plugin = self.db_service.select('plugin', criteria='id={0}'.format(plugin_id))[0]
		return PluginBean(db_plugin[0], db_plugin[1], db_plugin[2], db_plugin[3], db_plugin[4], db_plugin[5],
						  db_plugin[6], db_plugin[7], db_plugin[8], db_plugin[11], db_plugin[9], db_plugin[10],
						  db_plugin[12])

		


def handle_mode(context):
	init = Init(context)
	init.handle_init_mode()
