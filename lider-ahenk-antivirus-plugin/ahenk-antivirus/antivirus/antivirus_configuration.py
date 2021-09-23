#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Cemre ALPSOY <cemre.alpsoy@agem.com.tr>

from base.plugin.abstract_plugin import AbstractPlugin
import json


class AntivirusConfiguration(AbstractPlugin):
    def __init__(self, data, context):
        super(AntivirusConfiguration, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

    def handle_task(self):

        # Get clamav configuration file
        try:
            (result_code, p_out, p_err) = self.execute("cat /etc/clamav/freshclam.conf")
            antivirus_configuration = str(p_out).strip()
            data = {'antivirusConfiguration': antivirus_configuration}
            self.logger.debug('antivirus configuration file is: ' + antivirus_configuration)
            self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                         message='Antivirus Konfigurasyon dosyası başarıyla getirildi.', data=json.dumps(data),
                                         content_type=self.get_content_type().APPLICATION_JSON.value)
        except Exception as e:
            self.logger.debug('Error while reading antivirus configuration file: {}'.format(str(e)))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='Antivirus konfigurasyon dosyasına ulaşılırken beklenmedik bir hata oluştu')


def handle_task(task, context):
    sample = AntivirusConfiguration(task, context)
    sample.handle_task()
