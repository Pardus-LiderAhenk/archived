#!/usr/bin/python3
# -*- coding: utf-8 -*-

from base.plugin.abstract_plugin import AbstractPlugin
from base.model.enum.content_type import ContentType
import json
import datetime

class RootPassword(AbstractPlugin):
    def __init__(self, task, context):
        super(RootPassword, self).__init__()
        self.task = task
        self.context = context
        self.message_code = self.get_message_code()
        self.logger = self.get_logger()
        self.create_shadow_password = 'mkpasswd {}'
        self.change_password = 'usermod -p {0} {1}'
        self.username= 'root'


    def save_mail(self, status):
        cols = ['command', 'mailstatus', 'timestamp'];
        values = ['set_root_password', status, self.timestamp()]
        self.db_service.update('mail', cols, values)

    def set_mail(self,mail_content):
        if mail_content.__contains__('{date}'):
            mail_content = str(mail_content).replace('{date}', str(datetime.date.today()));
        if mail_content.__contains__('{ahenk}'):
            mail_content = str(mail_content).replace('{ahenk}', str(self.Ahenk.dn()));

        self.context.set_mail_content(mail_content)

    def handle_task(self):
        password = self.task['RootPassword'];
        rootEntity = self.task['rootEntity'];

        self.logger.debug('[Root Pass] password:  ' + str("**********"));

        mail_send = False
        mail_subject = ''
        mail_content = ''

        if 'mailSend' in self.task:
            mail_send = self.task['mailSend'];
        if 'mailSubject' in self.task:
            mail_subject = self.task['mailSubject'];
        if 'mailContent' in self.task:
            mail_content = self.task['mailContent'];
        try:

            if str(password).strip() != '':
                result_code, p_out, p_err = self.execute(self.create_shadow_password.format(password))
                shadow_password = p_out.strip()
                self.execute(self.change_password.format('\'{}\''.format(shadow_password), self.username))
                self.set_mail(mail_content)
                self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                             message='Parola Başarı ile değiştirildi.',
                                             data=json.dumps({
                                                 'Result': 'Parola Başarı ile değiştirildi.',
                                                 'mail_content': str(self.context.get_mail_content()),
                                                 'mail_subject': str(self.context.get_mail_subject()),
                                                 'mail_send': self.context.is_mail_send(),
                                                 'rootEntity': rootEntity
                                             }),
                                             content_type=ContentType.APPLICATION_JSON.value)
                self.logger.debug('Changed password.')

        except Exception as e:
            self.logger.error('Error: {0}'.format(str(e)))
            mail_content = 'Root Parolası değiştirlirken hata oluştu.'
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='Parola değiştirilirken hata oluştu.',
                                         data=json.dumps({
                                             'Result': 'Parola değiştirilirken hata oluştu.',
                                             'mail_content': str(self.context.get_mail_content()),
                                             'mail_subject': str(self.context.get_mail_subject()),
                                             'mail_send': self.context.is_mail_send(),
                                             'rootEntity': rootEntity
                                         }),
                                         content_type=ContentType.APPLICATION_JSON.value)




def handle_task(task, context):
    clz = RootPassword(task, context)
    clz.handle_task()
