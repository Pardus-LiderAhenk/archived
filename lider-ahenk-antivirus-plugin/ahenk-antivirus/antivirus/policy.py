#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Cemre ALPSOY <cemre.alpsoy@agem.com.tr>
from base.plugin.abstract_plugin import AbstractPlugin
import json
import threading
import subprocess
import os.path


class Sample(AbstractPlugin):
    def __init__(self, profile_data, context):
        super(Sample, self).__init__()
        self.profile_data = profile_data
        self.context = context
        self.logger = self.get_logger()
        self.parameters = json.loads(self.profile_data)
        self.plugin_path = os.path.abspath(os.path.join(os.path.dirname(__file__)))
        self.scan_media_file_path = self.plugin_path + '/antivirus.policy'
        self.clamav_conf_file_path = self.plugin_path + '/freshclam.conf'
        self.message_code = self.get_message_code()
        self.script_file_path = self.plugin_path + '/scripts/'
        self.result_message = ''

    def enable_usb_scan(self):
        self.logger.debug("Enable Usb Scan Method beginning")
        (result_code1, p_out1, p_err1) = self.execute('grep -c "mediascan" /var/spool/incron/root')
        if str(p_out1.strip()) == '0':
            (result_code, p_out, p_err) = self.execute(
                'echo /dev IN_ATTRIB,IN_NO_LOOP {0}mediascan.sh \$\#  {1} >> /var/spool/incron/root'.format(
                    self.script_file_path, self.plugin_path))
            if result_code > 0:
                self.logger.debug('Couldn\'t create USB scan job'.format(p_err))
            else:
                self.logger.debug('Successfully created USB scan job')

    def append_to_policy_file(self, media_type):
        if (not self.is_exist(self.scan_media_file_path)) or (
                    media_type not in open(self.scan_media_file_path, 'r+').read()):
            f = open(self.scan_media_file_path, 'a+')
            f.write(media_type + '\n')
            f.close()
        self.logger.debug('Successfully appended ' + media_type + ' to ' + self.scan_media_file_path)

    def remove_from_policy_file(self, media_type):
        if self.is_exist(self.scan_media_file_path):
            self.execute('rm {0}'.format(self.scan_media_file_path))
        self.logger.debug('Successfully removed ' + media_type + ' from ' + self.scan_media_file_path)

    def remove_cron_definition(self, text):
        command = "sed --in-place '" + text.replace('/', '\/') + "/d' /var/spool/incron/root"
        self.logger.debug(
            "Removing cron definition which includes '" + text + "' text if exist in /var/spool/incron/root")
        self.execute(command)
        self.logger.debug("varsa sildim")

    def scan_folder(self, folder):
        result_code, folder_name, p_err = self.execute('echo {}'.format(folder))
        self.logger.debug(result_code)
        if result_code == 0:
            self.execute('echo ' + folder + ' >> {0}/antivirusscanfolder'.format(self.plugin_path))
            tcommand = 'clamscan -r ' + folder + ' --log=/var/log/clamavscanlog'
            tcommand2 = None
            tcommand3 = None
            try:
                ter_command = ThreadCommand(tcommand, tcommand2, tcommand3)
                ter_command.run()
            except:
                print("rerun")
        else:
            self.logger.debug(' ! Not Scaned ! Path not exists ' + str(folder))
            self.result_message += '! Not Scaned ! Path not exists ' + str(folder) + '\r\n'

    def watch_folder(self, folder):
        if self.is_exist(folder):
            self.execute('echo ' + folder + ' >> {0}/antiviruswatchfolder'.format(self.plugin_path))
            self.execute(
                'echo ' + folder + ' IN_CREATE,IN_NO_LOOP {}downloadscan.sh \$\@ Download >> /var/spool/incron/root'.format(
                    self.script_file_path))
        else:
            self.logger.debug("Path does not exist {0}".format(folder))
            self.result_message += "Path does not exist {0}".format(folder) + '\r\n'

    def handle_policy(self):
        try:
            result_code, p_result, p_err = self.execute('rm /etc/incron.allow')
            if self.is_exist('/var/log/clamavscanlog') is False:
                self.create_file('/var/log/clamavscanlog')
            if self.is_exist('/var/spool/incron/root') is False:
                self.create_file('/var/spool/incron/root')
            if 'isRunning' in self.parameters and (
                            self.parameters['isRunning'] == 'Kapalı' or self.parameters['isRunning'] == 'Off'):
                self.logger.debug('Trying to stop clamav service')
                result_code, p_result, p_err = self.execute('service clamav-freshclam stop')
                if result_code != 0:
                    self.logger.debug("ERROR AT ANTIVIRUS SERVICE STATUS CHANGE " + p_err)
                    self.result_message += 'ERROR AT ANTIVIRUS SERVICE STATUS CHANGE ' + p_err + '\r\n'
            # Start clamav
            if 'isRunning' in self.parameters and (
                            self.parameters['isRunning'] == 'Açık' or self.parameters['isRunning'] == 'On'):
                self.logger.debug('Trying to start clamav service')
                result_code, p_result, p_err = self.execute('service clamav-freshclam start')
                if result_code != 0:
                    self.logger.debug("ERROR AT ANTIVIRUS SERVICE STATUS CHANGE " + p_err)
                    self.result_message += 'ERROR AT ANTIVIRUS SERVICE STATUS CHANGE ' + p_err + '\r\n'

            # Enable USB scanning
            if 'usbScanning' in self.parameters and (
                            self.parameters['usbScanning'] == 'Açık' or self.parameters['usbScanning'] == 'On'):
                self.logger.debug('Trying to enable USB scan')
                self.enable_usb_scan()
                self.append_to_policy_file('usb')
            # Disable USB scanning
            if 'usbScanning' in self.parameters and (
                            self.parameters['usbScanning'] == 'Kapalı' or self.parameters['usbScanning'] == 'Off'):
                self.logger.debug('Trying to disable USB scan')
                self.remove_cron_definition('/dev ')
                self.remove_from_policy_file('usb')

                # Change scan frequency
            if 'executionFrequency' in self.parameters:
                self.logger.debug('Trying to change scan frequency')
                try:
                    self.logger.debug('Successfully Antivirus Cron frequency > ')
                    run_frequency = self.parameters['executionFrequency']
                    (result_code, p_out, p_err) = self.execute(self.script_file_path + 'DISABLED_antiviruscron.sh')
                    if result_code == 0:
                        bash_script = self.script_file_path + 'ENABLED_antiviruscron.sh ' + str(
                            run_frequency) + ' ' + self.script_file_path
                        (result_code, p_out, p_err) = self.execute(bash_script)
                        if result_code > 0:
                            self.logger.debug("ERROR ANTIVIRUS CRON FREQUENCY CHANGES " + p_err)
                            self.result_message += "ERROR ANTIVIRUS CRON FREQUENCY CHANGES " + p_err + '\r\n'
                        else:
                            self.logger.debug('Successfully Antivirus Cron frequency changes')
                    else:
                        self.logger.debug('Error Antivirus plugin in execution frequency option')
                        self.result_message += 'Error Antivirus plugin in execution frequency option\r\n'
                except Exception as e:
                    self.logger.debug('Error Antivirus plugin '.format(str(e)))
                    self.result_message += 'Error Antivirus plugin '.format(str(e)) + '\r\n'

            # Change update frequency
            if 'updatingInterval' in self.parameters:
                self.logger.debug('Trying to change update frequency')
                try:
                    update_frequency = self.parameters['updatingInterval']
                    (result_code, p_out, p_err) = self.execute(
                        self.script_file_path + 'DISABLED_antivirusupdatecron.sh')
                    if result_code == 0:
                        bash_script = self.script_file_path + 'ENABLED_antivirusupdatecron.sh ' + str(
                            update_frequency) + ' ' + self.script_file_path
                        (result_code, p_out, p_err) = self.execute(bash_script)
                        if result_code > 0:
                            self.logger.debug(
                                "ERROR ANTIVIRUS CRON UPDATE FREQUENCY CHANGES ".format(p_err))
                            self.result_message += "ERROR ANTIVIRUS CRON UPDATE FREQUENCY CHANGES ".format(
                                p_err) + '\r\n'
                        else:
                            self.logger.debug('Successfully Antivirus Update frequency Cron changes')
                    else:
                        self.logger.debug('Error Antivirus plugin in updating interval option')
                        self.result_message += 'Error Antivirus plugin in updating interval option'
                except Exception as e:
                    self.logger.debug('Error Antivirus plugin '.format(str(e)))
                    self.result_message += 'Error Antivirus plugin '.format(str(e)) + '\r\n'

            # Scan folder
            if 'scannedFolders' in self.parameters:
                self.logger.debug('Trying to configure scan folder')
                self.execute('echo -n "" > {0}/antivirusscanfolder'.format(self.plugin_path))
                scan_folder = self.parameters['scannedFolders']
                folder_split = scan_folder.split(",")
                for folder in folder_split:
                    self.logger.debug('echo {}'.format(folder))
                    self.logger.debug(folder)
                    if "$USER" in folder:
                        for user in self.Sessions.user_name():
                            folder = folder.replace("$USER", user)
                            self.logger.debug(folder)
                            self.scan_folder(folder)
                    else:
                        self.scan_folder(folder)
            else:
                if self.is_exist("{0}/antivirusscanfolder".format(self.plugin_path)):
                    self.execute('echo -n "" > {0}/antivirusscanfolder'.format(self.plugin_path))

            # Enable download scanning
            if 'scanDownloadedFiles' in self.parameters and (
                            self.parameters['scanDownloadedFiles'] == 'Açık' or self.parameters[
                        'scanDownloadedFiles'] == 'On'):
                self.logger.debug('Trying to enable download scan')
                if self.is_exist('{0}/antivirus.configuration'.format(self.plugin_path)):
                    self.execute(
                        "sed -i '/scandownload:False/c\scandownload:True' {0}/antivirus.configuration".format(
                            self.plugin_path))
                else:
                    self.execute('echo "scandownload:True" > {0}/antivirus.configuration'.format(self.plugin_path))
            # Disable download scanning
            if 'scanDownloadedFiles' not in self.parameters:
                self.logger.debug('Trying to disable download scan')
                self.remove_cron_definition('/Downloads')
                if self.is_exist('{0}/antivirus.configuration'.format(self.plugin_path)):
                    self.execute(
                        "sed -i '/scandownload:True/c\scandownload:False' {0}/antivirus.configuration".format(
                            self.plugin_path))
                else:
                    self.execute('echo "scandownload:False" > {0}/antivirus.configuration'.format(self.plugin_path))

            # Watch folder
            if 'folderForDownloadedFiles' in self.parameters:
                self.logger.debug('Trying to configure watch folder')
                if self.is_exist('{0}/antiviruswatchfolder'.format(self.plugin_path)):
                    for line in open('{0}/antiviruswatchfolder'.format(self.plugin_path), 'r'):
                        self.logger.debug(line)
                        self.logger.debug("stripped line : " + line)
                        self.remove_cron_definition(line.strip())
                self.execute('echo -n "" > {0}/antiviruswatchfolder'.format(self.plugin_path))
                watchfolder = self.parameters['folderForDownloadedFiles']
                foldersplit = watchfolder.split(",")
                for folder in foldersplit:
                    if "$USER" in folder:
                        for user in self.Sessions.user_name():
                            folder = folder.replace("$USER", user)
                            self.logger.debug(folder)
                            self.watch_folder(folder)
                    else:
                        self.logger.debug(folder)
                        self.watch_folder(folder)
            else:
                self.logger.debug('Trying to configure watch folder')
                if self.is_exist('{0}/antiviruswatchfolder'.format(self.plugin_path)):
                    for line in open('{0}/antiviruswatchfolder'.format(self.plugin_path), 'r'):
                        self.logger.debug(line)
                        self.logger.debug("stripped line : " + line.strip())
                        self.remove_cron_definition(line.strip())
                    self.delete_file('{0}/antiviruswatchfolder'.format(self.plugin_path))
            result_mes = 'Antivirus profili başarıyla uygulandı\r\n' + self.result_message
            # Get clamav configuration '/etc/clamav/freshclam.conf'
            self.context.create_response(code=self.message_code.POLICY_PROCESSED.value,
                                         message=result_mes)
            self.logger.info('Antivirus policy is handled successfully')

        except Exception as e:
            self.logger.error(
                'A problem occured while handling Antivirus policy: {0}'.format(str(e)))
            result_mes = 'Antivirus profili uygulanırken bir hata oluştu.\r\n' + self.result_message
            self.context.create_response(code=self.message_code.POLICY_ERROR.value,
                                         message=result_mes)


def handle_policy(profile_data, context):
    sample = Sample(profile_data, context)
    sample.handle_policy()


class ThreadCommand(object):
    def __init__(self, cmd, cmd1, cmd2):
        self.cmd = cmd
        self.cmd1 = cmd1
        self.cmd2 = cmd2
        self.process = None

    def run(self):
        def target():
            self.process = subprocess.Popen(self.cmd, shell=True)
            self.process.communicate()

            if self.cmd1:
                self.process = subprocess.Popen(self.cmd1, shell=True)
                self.process.communicate()
            if self.cmd2:
                self.process = subprocess.Popen(self.cmd2, shell=True)
                self.process.communicate()

        thread = threading.Thread(target=target)
        thread.start()
