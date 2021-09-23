#!/usr/bin/python3
# -*- coding: utf-8 -*-

from base.plugin.abstract_plugin import AbstractPlugin


class Init(AbstractPlugin):
    def __init__(self, context):
        super(Init, self).__init__()
        self.context = context
        self.logger = self.get_logger()

    def handle_mode(self):
        try:
            if self.is_installed('clamav') is False:
                result_code, result, error = self.install_with_apt_get('clamav')
                if result_code != 0:
                    self.logger.error('Package clamav can not be installed')
                else:
                    self.logger.debug('Package clamav installed successfully')
        except Exception as e:
            self.logger.error(
                'Error while installing clamav package. Error message : {0}'.format(
                    str(e)))
        try:
            result_code, result, error = self.execute('/etc/init.d/clamav start')
            if result_code == 0:
                self.logger.debug(' clamav service started successfully')
            else:
                self.logger.error(
                    'clamav service could not be started - Error while executing /etc/init.d/clamav start command')
        except Exception as e:
            self.logger.error(
                'Error while starting clamav service. Error message : {0}'.format(str(e)))


def handle_mode(context):
    init = Init(context)
    init.handle_mode()
