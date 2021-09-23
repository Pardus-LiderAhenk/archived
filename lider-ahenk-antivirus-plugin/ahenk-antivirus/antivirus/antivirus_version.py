#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Cemre ALPSOY <cemre.alpsoy@agem.com.tr>

from base.plugin.abstract_plugin import AbstractPlugin
import json


class AntivirusVersion(AbstractPlugin):
    def __init__(self, data, context):
        super(AntivirusVersion, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.script_file_path = '/usr/share/ahenk/plugins/antivirus/scripts/'
        self.message_code = self.get_message_code()

    def handle_task(self):

        # Get clamav version
        try:
            (result_code, p_out, p_err) = self.execute("clamscan -V")
            antivirus_version = str(p_out).strip()
            data = {'antivirusVersion': antivirus_version}
            self.logger.debug('clamav version: ' + antivirus_version)
            self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                         message='Antivirus Versiyonu başarıyla getirildi.', data=json.dumps(data),
                                         content_type=self.get_content_type().APPLICATION_JSON.value)
        except Exception as e:
            self.logger.debug('Error while reading antivirus version: {}'.format(str(e)))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='Antivirus versiyonuna ulaşılırken beklenmedik bir hata oluştu')


def handle_task(task, context):
    sample = AntivirusVersion(task, context)
    sample.handle_task()
