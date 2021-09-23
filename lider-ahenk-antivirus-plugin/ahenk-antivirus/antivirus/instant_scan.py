#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Cemre ALPSOY <cemre.alpsoy@agem.com.tr>
from base.plugin.abstract_plugin import AbstractPlugin


class InstantScan(AbstractPlugin):
    def __init__(self, data, context):
        super(InstantScan, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.script_file_path = '/usr/share/ahenk/plugins/antivirus/scripts/'
        self.message_code = self.get_message_code()

    def scan_folder(self, folder, result_message):
        result_code, folder_name, p_err = self.execute('cd {}'.format(folder))
        if result_code == 0:
            result_code, result, error = self.execute(
                '{0}downloadscan.sh {1} Instant'.format(self.script_file_path, folder))
            if result_code > 0:
                self.logger.debug('{} directory could not be scanned'.format(folder))
                result_message += '{} dizini taranamadı\r\n'.format(folder)
            else:
                result_message += '{} dizini başarılı bir şekilde tarandı\r\n'.format(folder)
                self.logger.debug('{} directory is scanned succesfully'.format(folder))
        else:
            result_message += '{} dizini bulunmamakta; bu sebeple taranamadı'
            self.logger.debug('{} directory does not exist'.format(folder))

    def handle_task(self):
        try:
            self.logger.debug('Antivirus instant scan task is started...')
            result_message = 'Dn : {}'.format(self.Ahenk.dn())
            watch_folder = str(self.data['folderPath'])
            folder_split = watch_folder.split(",")
            self.logger.debug('Folder Path/(s) is parsed')
            if self.is_exist(self.script_file_path):
                for folder in folder_split:
                    if "$USER" in folder:
                        for user in self.Sessions.user_name():
                            folder = folder.replace("$USER", user)
                            self.logger.debug(folder)
                            self.scan_folder(folder, result_message)
                    else:
                        self.scan_folder(folder, result_message)
                self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                             message=result_message)
            else:
                result_message += 'Tarama işlemlerini gerçekleştiren dizin bulunamadı. Tarama işlemi gerçekleştirilemedi'
                self.logger.debug('Scanning process could not be started because of unfound scanner controller files')
                self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                             message=result_message)
        except Exception as e:
            self.logger.debug('Error : {}'.format(str(e)))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='Anlık tarama işlemi yapılırken beklenmedik hata!')


def handle_task(task, context):
    sample = InstantScan(task, context)
    sample.handle_task()
