#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Cemre ALPSOY <cemre.alpsoy@agem.com.tr>
from base.plugin.abstract_plugin import AbstractPlugin


class AntivirusChangeConfiguration(AbstractPlugin):
    def __init__(self, data, context):
        super(AntivirusChangeConfiguration, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.script_file_path = '/usr/share/ahenk/plugins/antivirus/scripts/'
        self.message_code = self.get_message_code()

    def handle_task(self):
        try:
            self.logger.debug('Antivirus change configuration file task is started...')
            result_code, p_out, p_err = self.execute('echo "{0}" > /etc/clamav/freshclam.conf'.format(str(self.data['configurationParameter'])))
            if result_code == 0:
                self.logger.debug("Antivirus configuration file is changed. Clamav service will be restarted")
                result_code, p_out, p_err = self.execute("sudo service clamav-freshclam restart")
                if result_code == 0:
                    self.logger.debug("Clamav is restarted successfully")
                else:
                    self.logger.debug("Clamav service could not be restarted")
                self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                             message="Antivirus Konfigürasyon dosyası başarıyla güncellendi")
            else:
                self.logger.debug('Antivirus Configuration File could not be changed')
                self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                             message="Antivirüs Konfigürasyon Dosyası güncellenemedi")
        except Exception as e:
            self.logger.debug('Error : {}'.format(str(e)))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='Antivirüs konfigürasyon dosyası değiştirilirken beklenmedik hata!')


def handle_task(task, context):
    sample = AntivirusChangeConfiguration(task, context)
    sample.handle_task()
