#!/usr/bin/python
# -*- coding: utf-8 -*-
# Author: Cemre ALPSOY <cemre.alpsoy@agem.com.tr>

import json

from base.plugin.abstract_plugin import AbstractPlugin


class PackageManagement(AbstractPlugin):
    def __init__(self, data, context):
        super(AbstractPlugin, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

    def handle_task(self):
        try:
            items = self.data['packageInfoList']
            result_message = ''
            installed_packages = ''
            uninstalled_packages = ''
            failed_packages = ''

            for item in items:

                # Install package
                if item['tag'] == 'i':
                    self.logger.debug("Installing package: {0}".format(item['packageName']))
                    try:
                        result_code, p_result, p_err = self.install_with_apt_get(item['packageName'], item['version'])
                        if result_code == 0:
                            self.logger.debug("Installed package: {0}".format(item['packageName']))
                            installed_packages += ' ' + item['packageName']
                        else:
                            self.logger.debug("Couldnt Installed package: {0}".format(item['packageName']))
                            failed_packages += ' ' + item['packageName']

                    except Exception as e1:
                        self.logger.error(str(e1))
                        failed_packages += ' ' + item['packageName']

                # Uninstall package
                elif item['tag'] == 'u':
                    self.logger.debug("Uninstalling package: {0}".format(item['packageName']))
                    try:
                        result_code, p_result, p_err = self.uninstall_package(item['packageName'], item['version'])
                        if result_code == 0:
                            self.logger.debug("Uninstalled package: {0}".format(item['packageName']))
                            uninstalled_packages += ' ' + item['packageName']
                        else:
                            self.logger.debug(
                                "Couldnt Uninstalled package: {0}".format(item['packageName']))
                            failed_packages += ' ' + item['packageName']
                    except Exception as e2:
                        self.logger.error(str(e2))
                        failed_packages += ' ' + item['packageName']

            # Result message
            if installed_packages:
                result_message += ' Kurulan paketler: (' + installed_packages + ' )'
            if uninstalled_packages:
                result_message += ' Kald??r??lan paketler: (' + uninstalled_packages + ' )'
            if failed_packages:
                result_message += ' Hata alan paketler: (' + failed_packages + ' )'
                self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                             message='Paket i??lemleri s??ras??nda hata olu??tu: ' + result_message,
                                             data=json.dumps({'Result': result_message}),
                                             content_type=self.get_content_type().APPLICATION_JSON.value)
            else:
                self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                             message='Paket i??lemleri ba??ar??yla ger??ekle??tirildi: ' + result_message,
                                             data=json.dumps({'Result': result_message}),
                                             content_type=self.get_content_type().APPLICATION_JSON.value)
                # TODO return package list!

        except Exception as e:
            self.logger.error(str(e))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='Paket kur/kald??r i??lemleri ger??ekle??tirilirken hata olu??tu:' + str(e),
                                         content_type=self.get_content_type().APPLICATION_JSON.value)


def handle_task(task, context):
    plugin = PackageManagement(task, context)
    plugin.handle_task()
