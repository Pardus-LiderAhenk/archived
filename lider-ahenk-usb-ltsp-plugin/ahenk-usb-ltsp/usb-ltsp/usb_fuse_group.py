#!/usr/bin/python3
# -*- coding: utf-8 -*-

import json

from base.plugin.abstract_plugin import AbstractPlugin


class Fusegroup(AbstractPlugin):
    def __init__(self, task, context):
        super(AbstractPlugin, self).__init__()
        self.task = task
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

    def does_user_exist(self, username):
        username = username.replace(' ', '')
        result_code, p_out, p_err = self.execute('getent passwd | grep -c \'^{0}:\''.format(username))

        if not p_err and p_out.strip() == '1':
            return True
        else:
            return False

    def does_group_exist(self, groupname):
        groupname = groupname.replace(' ', '')
        result_code, p_out, p_err = self.execute('getent group \'{0}\' /etc/passwd'.format(groupname))
        if p_out:
            return True
        else:
            return False

    def handle_task(self):

        try:
            data_list = []
            users = str(self.task['usernames']).replace(']', '').replace('[', '').split(',')
            state = str(self.task['statusCode'])
            err = 0
            self.logger.debug('Configuration of Usb privileges started.')
            for user in users:
                user = user.replace('\'', '')
                self.logger.debug('User {0} is handling'.format(user))
                if not self.does_user_exist(user):
                    self.logger.debug('User {0} does not exist'.format(user))
                    data = dict()
                    data['username'] = user
                    data['statusCode'] = "3"
                    data_list.append(data)
                    err = 1
                    continue
                if not self.does_group_exist('fuse'):
                    self.logger.debug('Fuse group does not exist')
                    data = dict()
                    data['username'] = user
                    data['statusCode'] = "2"
                    data_list.append(data)
                    err = 1
                    continue

                data = dict()
                data['username'] = user
                if state == '1':
                    result_code, p_out, p_err = self.execute('adduser {0} fuse'.format(user))
                    if result_code == 0:
                        self.logger.debug('User {0} added to fuse group'.format(user))
                        data['statusCode'] = "1"
                    else:
                        self.logger.error('A problem occurred while adding user {0} to fuse group'.format(user))
                        data['statusCode'] = "4"
                        err = 1

                    if self.task['endDate']:
                        self.setup_cron(user)
                else:
                    self.logger.debug('')
                    result_code, p_out, p_err = self.execute('deluser {0} fuse'.format(user))
                    if result_code == 0:
                        self.logger.debug('User {0} removed from fuse group'.format(user))
                        data['statusCode'] = "0"
                    else:
                        self.logger.error('A problem occurred while removing user {0} from fuse group'.format(user))
                        data['statusCode'] = "4"
                        err = 1
                data_list.append(data)
                continue

            result = dict()
            result['fuse-group-results'] = json.dumps(data_list)
            if self.context.is_mail_send():
                mail_content = self.context.get_mail_content()
                if mail_content.__contains__('{usernames}'):
                    mail_content = str(mail_content).replace('{usernames}', str(self.task['usernames']))
                if mail_content.__contains__('{ahenk}'):
                    mail_content = str(mail_content).replace('{ahenk}', str(self.Ahenk.dn()))
                self.context.set_mail_content(mail_content)
                result['mail_content'] = str(self.context.get_mail_content())
                result['mail_subject'] = str(self.context.get_mail_subject())
                result['mail_send'] = self.context.is_mail_send()

            if err == 1:
                self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                             message='USB hakları düzenlenemedi.',
                                             data=json.dumps(result),
                                             content_type=self.get_content_type().APPLICATION_JSON.value)
            else:
                self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                             message='USB hakları düzenlendi.',
                                             data=json.dumps(result),
                                             content_type=self.get_content_type().APPLICATION_JSON.value)
        except Exception as e:
            self.logger.error('A problem occurred while editing usb privilege. Erro Message: {0}'.format(str(e)))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='USB hakları düzenlenirken hata oluştu',
                                         content_type=self.get_content_type().APPLICATION_JSON.value)

    def setup_cron(self, username):
        end_cron = self.task['endDate']

        self.logger.debug('End date cron will be setup as {0}'.format(end_cron))

        cron_command = '{0} root deluser {1} fuse\n'.format(end_cron, username)
        cron_file = open('/etc/cron.d/fuse_remover', 'w+')
        cron_file.write('# /etc/cron.d/fuse_remover: Removes fuse group from specified user.\n\n')
        cron_file.write('SHELL=/bin/sh\n')
        cron_file.write('PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin\n\n')
        cron_file.write(cron_command)
        cron_file.close()
        change_cron_mod = 'chmod 600 /etc/cron.d/fuse_remover'
        self.execute(change_cron_mod)
        self.logger.debug('Changed mod of fuse-remover cron file. Restarting cron service')
        self.execute("service cron restart")
        self.logger.debug('Restarted cron service')


def handle_task(task, context):
    fg = Fusegroup(task, context)
    fg.handle_task()
