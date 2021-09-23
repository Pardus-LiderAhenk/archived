This is for lider ahenk developer. This compose consist of ejabber, openldap and mariadb containers. 

## USAGE

### EJABBERD

####Persistence

For storage of the application data, you can mount volumes at

/opt/ejabberd/ssl
/opt/ejabberd/backup
/opt/ejabberd/upload
/opt/ejabberd/database


**NOTE :** If ejabberd not works as expected you can run below commands; 

docker exec -ti liderxmpp ejabberdctl srg-create everyone im.liderahenk.org "everyone" this_is_everyone everyone

docker exec -ti liderxmpp ejabberdctl srg-user-add @all@ im.liderahenk.org everyone im.liderahenk.org

docker exec -ti liderxmpp ejabberdctl register admin im.liderahenk.org secret

docker exec -ti liderxmpp ejabberdctl register lider_sunucu im.liderahenk.org secret


### OPENLDAP

### Persistence

For storage of the application data, you can mount volumes at

/etc/ldap
/var/lib/ldap



### MARIADB

### Persistence

For storage of the application data, you can mount volumes at

/var/lib/mysql







