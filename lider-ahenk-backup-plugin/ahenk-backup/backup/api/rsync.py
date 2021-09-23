#!/usr/bin/python3
# -*- coding: utf-8 -*-

import json
import os
import re
import subprocess
import sys
import threading
import random
import time
from base.model.enum.content_type import ContentType
from base.model.enum.message_code import MessageCode
from base.model.enum.message_type import MessageType
from base.model.response import Response
from base.plugin.abstract_plugin import AbstractPlugin
from base.scope import Scope

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))


class BackupParser(AbstractPlugin):
    def __init__(self, logger, context):
        super(AbstractPlugin, self).__init__()
        scope = Scope.get_instance()
        self.context = context
        self.messenger = scope.get_messenger()
        self.messaging = scope.get_message_manager()

        self.dry_run_status = True
        self.percentage = None
        self.number_of_files = None
        self.number_of_transferred_files = None
        self.number_of_created_files = None
        self.resuming = True
        self.estimated_time = 0
        self.total_file_size = None
        self.total_transferred_file_size = None
        self.estimated_transfer_size = None
        self.transferred_file_size = 0
        self.logger = logger
        self.last_sended_percentage = -1
        self.last_senden_file_size = -1

    def parse_dry(self, line):
        if 'Number of files' in line:
            total_file = re.findall(r'\d+', line)
            if total_file:
                self.number_of_files = str(total_file[1])
                self.update_last_status()
        elif 'Number of created files' in line:
            total_file = re.findall('Number of created files: (\d+)', line)
            if total_file:
                self.number_of_created_files = str(total_file[0])
                self.update_last_status()
        elif 'Number of regular files transferred' in line:
            transferred_file_size = re.findall('Number of regular files transferred: (\d+)', line)
            if transferred_file_size:
                self.number_of_transferred_files = str(transferred_file_size[0])
                self.update_last_status()
        elif 'Total file size' in line:
            file_size = re.findall('Total file size: (\d+)', line)
            if file_size:
                self.total_file_size = str(file_size[0])
                self.update_last_status()
        elif 'Total transferred file size' in line:
            transferred_file_size = re.findall('Total transferred file size: (\d+)', line)
            if transferred_file_size:
                self.total_transferred_file_size = str(transferred_file_size[0])
                self.estimated_transfer_size = self.total_transferred_file_size
                self.update_last_status()

    def parse_sync(self, line):
        #self.logger.debug('Rsync command results are parsing')

        line_as_arr = None
        try:
            line_as_arr = line.split()
        except:
            pass
        if len(line_as_arr) > 1 and b'%' in line:
            self.transferred_file_size = str(line_as_arr[0].decode('utf-8'))
            self.percentage = str(line_as_arr[1].decode('utf-8')).replace('%', '')
            self.estimated_time = str(line_as_arr[3].decode('utf-8'))

            if float(self.total_transferred_file_size) == 0:
                return -1
            elif self.total_file_size != 0:
                
                transfer_range = float(
                    '{0:.2f}'.format(float(self.total_transferred_file_size) / float(self.total_file_size)))
                real_percentage = int(int(self.percentage) / transfer_range)

                if real_percentage == 100:
                    self.estimated_time = '0:00:00'
                if real_percentage % 10 == 0:
                    if self.last_sended_percentage != real_percentage:
                       self.last_sended_percentage = real_percentage
                       self.logger.info('Gönderilen Dosya: %' + str(real_percentage))
                       self.send_processing_message(str(real_percentage), str(self.estimated_time))

                if real_percentage == 100:
                    return -1
            elif self.total_file_size ==0:
                return -1
        return 0

    def send_processing_message(self, percentage, time):
        try:
            data = {
                'percentage': str(percentage), 'estimation': str(time),
                'numberOfCreatedFiles': str(self.number_of_created_files),
                'numberOfFiles': str(self.number_of_files),
                'totalFileSize': str(self.total_file_size),
                'transferredFileSize': str(self.transferred_file_size),
                'estimatedTransferSize': str(self.estimated_transfer_size),
                'numberOfTransferredFiles': str(self.number_of_transferred_files)
            }

            response = Response(type=MessageType.TASK_STATUS.value, id=self.context.get('taskId'),
                                code=MessageCode.TASK_PROCESSING.value,
                                data=json.dumps(data),
                                content_type=ContentType.APPLICATION_JSON.value)

            message = self.messaging.task_status_msg(response)
            Scope.get_instance().get_messenger().send_direct_message(message)
        except Exception as e:
            self.logger.error('A problem occurred while sending message. Error Message: {0}'.format(str(e)))

    def update_last_status(self):
        if not self.dry_run_status:
            self.percentage = str(100)
            self.estimated_time = '0:00:00'
            self.resuming = False


class BackupRsync(AbstractPlugin):
    def __init__(self, backup_data, context):
        super(BackupRsync, self).__init__()
        self.backup_data = backup_data
        self.context = context
        self.backup_result = {}
        self.parser = BackupParser(self.logger, context)

    def prepare_command(self):

        if self.backup_data['type'] == 'b':
            path = self.backup_data['sourcePath'] + ' ' + self.backup_data['username'] + "@" + self.backup_data[
                'destHost'] + ':' + self.backup_data['destPath']

        else:
            path = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data[
                'sourcePath'] + ' ' + self.backup_data['destPath']

        options = ' -a --no-i-r --info=progress2 --stats --no-h -M--fake-super '

        backup_command = 'rsync ' + options + ' ' + path
        self.logger.info(str(backup_command))

        return backup_command

    def dry_run(self):

        if self.backup_data['type'] == 'b':

            path = self.backup_data['sourcePath'] + ' ' + self.backup_data['username'] + "@" + self.backup_data[
                'destHost'] + ':' + self.backup_data['destPath']

        else:
            path = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data[
                'sourcePath'] + ' ' + self.backup_data['destPath']

        options = ' -azn --stats --no-h '
        dry_run_backup_command = 'rsync ' + options + ' ' + path
        return dry_run_backup_command

    def execute_command(self, cmd):
        # executing result a göre task fail ver
        self.logger.debug('Backup command is executing. Command : {0}'.format(cmd))
        try:
            self.parser.resuming = True
            command_process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                               stderr=subprocess.PIPE)
            self.logger.debug("BACKUP COMMAND -------")
            err_line = command_process.stderr.readline()
            if err_line and b'closed' in err_line:
               time.sleep(random.randrange(0,10,2))
               self.execute_command(cmd) 
            self.logger.debug('Err Line')
            self.logger.debug(err_line)
            self.logger.debug('Err Line End')
            #self.logger.debug(command_process.stdout.readline())
            #self.logger.debug(command_process)
            self.parser.set_process = command_process
            while self.parser.resuming:
                output = command_process.stdout.readline()
                self.logger.debug(output) 
                if output == '' or command_process.poll() is not None:
                    try:
                          if int(str(self.parser.estimated_transfer_size))-2000 < int(str(self.parser.transferred_file_size)) < int(str(self.parser.estimated_transfer_size))+2000 :
                             self.parser.resuming = False
                             self.logger.info('#processed')
                          else:
                             #if int(str(self.parser.last_senden_file_size)) != int(str(self.parser.transferred_file_size)):
                             #   self.parser.last_senden_file_size = self.parser.transferred_file_size
                             #   self.parser.transferred_file_size = 0
                             #   self.parser.estimated_transfer_size = str(int(str(self.parser.estimated_transfer_size)) - int(str(self.parser.transferred_file_size)))
                             
                             #self.parser.estimated_transfer_size = str(int(str(self.parser.estimated_transfer_size)) - int(str(self.parser.transferred_file_size)))
                             #self.parser.transferred_file_size = 0
                             #self.logger.debug(self.parser.estimated_transfer_size)
                             #self.logger.debug(self.parser.transferred_file_size)
                             #self.logger.debug('EXECUTE COMMAND TEKRAR CALISIYOR')
                             #self.execute_command(cmd)
                             self.backup()
                    except:
                          self.parser.resuming = False
                          self.logger.debug('RSYNC ERROR - YEDEKLEME ISLEMI TEKRARLANIYOR')
                          self.backup()
                if output:
                    if self.parser.parse_sync(output) == -1:
                        pass
        except Exception as e:
            self.logger.error('A problem occurred while executing rsync command. Error Message: {0}'.format(str(e)))
            #return False
            self.backup()

    def execute_dry_run(self, cmd):

        process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE)

        p_out = process.stdout.read().decode("unicode_escape")

        for line in p_out.strip().split('\n'):
            self.parser.parse_dry(line)

    def get_resource_total_size(self, source_path):
        return os.stat(source_path).st_size

    def is_file(self, source_path):
        return os.path.isfile(source_path)

    def append_command_execution_type(self, cmd):
        return 'sshpass -p ' + self.backup_data['password'] + ' ' + cmd + ' | stdbuf -oL tr "\\r" "\\n"'

    def destination_confirm(self):
        if self.backup_data['type'] == 'b':
            return self.execute(
                'sshpass -p {0} ssh -o StrictHostKeyChecking=no {1}@{2} mkdir -p {3}'.format(
                    self.backup_data['password'],
                    self.backup_data['username'],
                    self.backup_data['destHost'],
                    os.path.dirname(self.backup_data['destPath'])))[0]
        else:
            return 0

    def backup(self):
        # Change status of parser and run dry run command for backup informations
        # self.parser.dry_run_status = True
        retry_count = 10;
        is_destination_ok = False
        for count in range(retry_count):
            if self.destination_confirm() != 0:
                continue
            else:
                is_destination_ok = True
                break

        if not is_destination_ok:
            self.context.create_response(code=MessageCode.TASK_ERROR.value,
                                             message='Uzak dizin oluşturulamadı.',
                                             content_type=self.get_content_type().APPLICATION_JSON.value)

        dry_run_cmd = self.append_command_execution_type(self.dry_run())
        self.logger.debug('Dry run command:{0}'.format(dry_run_cmd))
        test_dry_run = True
        test_dry_run_count = 1
        while test_dry_run:
            self.execute_dry_run(dry_run_cmd)
            if self.parser.number_of_files == None or self.parser.number_of_created_files == None or self.parser.number_of_transferred_files == None or self.parser.total_file_size == None:
                time.sleep(test_dry_run_count * random.randrange(0, 10, 2))
                test_dry_run_count = test_dry_run_count + 1
                continue
            else:
                test_dry_run = False
        self.parser.dry_run_status = False
        self.logger.info('Dry run executed.')
        self.prepare_backup()

        self.logger.info('Backup completed')

        result = dict()
        if self.context.is_mail_send():
            mail_content = self.context.get_mail_content()
            if mail_content.__contains__('{path}'):
                mail_content = str(mail_content).replace('{path}', str(self.backup_data['sourcePath']))
            if mail_content.__contains__('{ahenk}'):
                mail_content = str(mail_content).replace('{ahenk}', str(self.Ahenk.dn()))
            if mail_content.__contains__('{totalFileSize}'):
                mail_content = str(mail_content).replace('{totalFileSize}', str(self.parser.total_file_size))
            if mail_content.__contains__('{transferredFileSize}'):
                mail_content = str(mail_content).replace('{transferredFileSize}', str(self.parser.transferred_file_size))
            if mail_content.__contains__('{numberOfFiles}'):
                mail_content = str(mail_content).replace('{numberOfFiles}', str(self.parser.transferred_file_size))
            if mail_content.__contains__('{numberOfTransferredFiles}'):
                mail_content = str(mail_content).replace('{numberOfTransferredFiles}', str(self.parser.transferred_file_size))
            self.context.set_mail_content(mail_content)
            result['mail_content'] = str(self.context.get_mail_content())
            result['mail_subject'] = str(self.context.get_mail_subject())
            result['mail_send'] = self.context.is_mail_send()

        result['numberOfCreatedFiles'] = str(self.parser.number_of_created_files)
        result['numberOfFiles'] = str(self.parser.number_of_files)
        result['totalFileSize'] = str(self.parser.total_file_size)
        result['transferredFileSize'] = str(self.parser.transferred_file_size)
        result['estimatedTransferSize'] = str(self.parser.estimated_transfer_size)
        result['numberOfTransferredFiles'] = str(self.parser.number_of_transferred_files)

        self.context.create_response(code=MessageCode.TASK_PROCESSED.value, data=json.dumps(result),
                                     message='Dosya transferi bitti.',
                                     content_type=self.get_content_type().APPLICATION_JSON.value)

    def prepare_backup(self):
        cmd = self.append_command_execution_type(self.prepare_command())
        self.logger.info(cmd)
        self.execute_command(cmd)

    def start_backup(self):
        try:
            t = threading.Thread(target=self.prepare_backup, args=())
            t.start()
        except Exception as e:
            Scope.get_instance().get_logger().info(e)