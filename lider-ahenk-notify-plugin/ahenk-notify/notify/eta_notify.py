#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Tuncay ÇOLAK <tuncay.colak@tubitak.gov.tr>

from base.plugin.abstract_plugin import AbstractPlugin
from base.model.enum.content_type import ContentType

class Sample(AbstractPlugin):
    def __init__(self, task, context):
        super(Sample, self).__init__()
        self.task = task
        self.message_code = self.get_message_code()
        self.context = context
        self.logger = self.get_logger()
        self.eta_notify_command_fullscreen = "DISPLAY=:0 eta-notify -m  \"{message_content}\" -d {duration}"
        self.eta_notify_command_small = "DISPLAY=:0 eta-notify -m  \"{message_content}\" -d {duration} --{size}"

    def handle_task(self):
        try:
            message_content = self.task['notify_content']
            message_duration = self.task['duration']
            message_size = self.task['size']
            if self.task['size'] == 'small':
                self.execute(self.eta_notify_command_small.format(message_content=message_content, duration=message_duration, size=message_size))
                self.logger.info("Successfully executed ETAP Notify Message ")
            else:
                self.execute(self.eta_notify_command_fullscreen.format(message_content=message_content, duration=message_duration))
                self.logger.info("Successfully executed ETAP Notify Message ")

            self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                         message='Bilgilendirme Mesajı görevi başarıyla çalıştırıldı',
                                         content_type=ContentType.APPLICATION_JSON.value)
        except Exception as e:
            self.context.create_response(code=self.message_code.TASK_PROCESSED.TASK_ERROR.value,
                                         message='Bilgilendirme Mesajı görevi çalıştırılırken hata oluştu '+str(e),
                                         content_type=ContentType.APPLICATION_JSON.value)

def handle_task(task, context):
    print('Sample Plugin Task')
    sample = Sample(task, context)
    sample.handle_task()
