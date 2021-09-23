package test.liderahenk.ldap.server;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.noggit.JSONParser;
import org.noggit.ObjectBuilder;
import org.slf4j.LoggerFactory;

public class PartitionHandler {

        private DirectoryService directoryService;

        PartitionHandler(DirectoryService directoryService) {
                this.directoryService = directoryService;
        }

        public void init(String name) throws Exception {
                FileReader r = new FileReader(new File(this.getClass()
                                .getResource(name).getFile()));
                JSONParser parser = new JSONParser(r);
                HashMap top = (HashMap) ObjectBuilder.getVal(parser);
                HashMap partitions = (HashMap) top.get("partitions");
                Iterator iter = partitions.keySet().iterator();
                while (iter.hasNext()) {
                        String partitionName = (String) iter.next();
                        HashMap partition = (HashMap) partitions.get(partitionName);
                        String dn = (String) partition.get("dn");
                        HashMap attributes = (HashMap) partition.get("attributes");
                        checkPartition(partitionName, dn, attributes);
                }
                
        }


        private void checkPartition(String partitionName, String dn,
                        HashMap attributes) throws Exception {
                LoggerFactory.getLogger(this.getClass()).debug("Adding partition:"+partitionName+dn+attributes.toString());

                Partition apachePartition = addPartition(partitionName, dn);
               
                try {
                        directoryService.getAdminSession().lookup(
                                        apachePartition.getSuffixDn());
                } catch (LdapException lnnfe) {
                        Dn dnApache = new Dn(dn);
                        Entry entryApache = directoryService.newEntry(dnApache);
                        Iterator<String> iter = attributes.keySet().iterator();
                        while (iter.hasNext()) {
                                String attributeName = (String) iter.next();
                                Object value = attributes.get(attributeName);
                                if (value instanceof String) {
                                        entryApache.add(attributeName, (String) value);
                                } else {
                                        ArrayList<String> val = (ArrayList<String>) value;
                                        for (String v : val) {
                                                entryApache.add(attributeName, v);
                                        }
                                }
                        }
                        directoryService.getAdminSession().add(entryApache);
                }
        }

        private Partition addPartition(String partitionId, String partitionDn)
                        throws Exception {
                Partition partition = new JdbmPartition(directoryService.getSchemaManager());
                partition.setId(partitionId);
                partition.setSuffixDn(new Dn(partitionDn));
                directoryService.addPartition(partition);
                return partition;
        }

}