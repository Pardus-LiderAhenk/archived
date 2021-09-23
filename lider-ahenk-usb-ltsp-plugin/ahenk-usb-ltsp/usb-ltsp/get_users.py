import json
import os
from base.plugin.abstract_plugin import AbstractPlugin

class GetUser(AbstractPlugin):
    def __init__(self, task, context):
        super(AbstractPlugin, self).__init__()
        self.task = task
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

    def handle_task(self):
        try:
            # for single selected user
            selectedUser= None

            if 'selectedUser' in self.task :
                selectedUser = str(self.task['selectedUser'])
                self.logger.debug('Selected User : '+ str(selectedUser))

                if selectedUser :

                    users = []

                    if os.path.isdir('/home/' + str(selectedUser)):
                        result = os.popen("getent passwd | grep " + selectedUser).read()
                        if result:
                            user = dict()
                            user['username'] = str(selectedUser)
                            result_code, p_out, p_err = self.execute("groups " + str(selectedUser))

                            if 'fuse' in p_out:
                                user["statusCode"] = 1
                            else:
                                user["statusCode"] = 0

                            users.append(user)

                    self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                                 message='Login olan kullanıcılar başarı ile getirildi.',
                                                 data=json.dumps(
                                                     {
                                                         'Result': 'İşlem Başarı ile gercekleştirildi',
                                                         'users': users
                                                     }
                                                 ),
                                                 content_type=self.get_content_type().APPLICATION_JSON.value)

            else:
                users=self.get_home_users()
                self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                                 message='Login olan kullanıcılar başarı ile getirildi.',
                                                 data=json.dumps(
                                                     {
                                                         'Result': 'İşlem Başarı ile gercekleştirildi',
                                                         'users': users
                                                     }
                                                 ),
                                                 content_type=self.get_content_type().APPLICATION_JSON.value)
        except Exception as e:
            self.logger.error('A problem occurred while editing usb privilege. Error Message: {0}'.format(str(e)))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='Kullanıcılar listelenirken hata oluştu',
                                         content_type=self.get_content_type().APPLICATION_JSON.value)

    def get_home_users(self):
        users = []
        directories = os.listdir('/home')
        for directory in directories:
            if os.path.isdir('/home/' + directory):
                result = os.popen("getent passwd | grep " + directory).read()
                if result:
                    user = dict()
                    user['username'] = str(directory)
                    result_code, p_out, p_err = self.execute("groups " + str(directory))

                    if 'fuse' in p_out:
                        user["statusCode"] = 1
                    else :
                        user["statusCode"] = 0

                    users.append(user)
        return users


def handle_task(task, context):
    classs = GetUser(task, context)
    classs.handle_task()
