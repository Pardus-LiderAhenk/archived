/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.deployer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.deployer.model.PluginArchiveFileInfo;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.deployer.ILiderHotDeployListener;
import tr.org.liderahenk.lider.core.api.persistence.dao.IManagedPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;


/**
 * Default implementation for {@link ILiderHotDeployListener}
 * 
 * @author <a href="mailto:basaran.ismaill@gmail.com">İsmail BAŞARAN</a>
 * 
 */
public class LiderHotDeployListener implements ILiderHotDeployListener,Runnable{
	
	
	private final static Logger logger = LoggerFactory.getLogger(LiderHotDeployListener.class);
	
	private IConfigurationService configurationService;
	private IManagedPluginDao managedPluginDao;
	private IEntityFactory entityFactory;
	
	
	private static final int BUFFER = 8*1024;

	private WatchService watcher;
	private Map<WatchKey,Path> keys;
	
	
	@SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	
	
	@Override
	public void register(Path dir) {
		try {
			WatchKey key = dir.register(watcher, ENTRY_CREATE);
			keys.put(key, dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init(){
		try {
			this.watcher = FileSystems.getDefault().newWatchService();
			this.keys = new HashMap<WatchKey,Path>();
			File hotDeploymentPath = new File("configurationService.getHotDeploymentPath()");
			if(! hotDeploymentPath.exists()){
				hotDeploymentPath.mkdirs();
			}
			Path dir = Paths.get(configurationService.getHotDeploymentPath()); 
			register(dir);
			new Thread(this).start(); //TODO do something better
		} catch (IOException e) {
			logger.error("[LiderHotDeployListener] Exeption occured when initializing hot deployment listener...");
			e.printStackTrace();
		} 
	}
	
	public void destroy(){
		try {
			if(this.watcher != null){
				this.watcher.close();
			}
		} catch (IOException e) {
			logger.error("[LiderHotDeployListener] Exeption occured when destroying hot deployment listener...");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		processEvents();
	}
	
	@Override
	public void processEvents() {
		for (;;) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
            	logger.error("Watcher key not found...");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                logger.info("%s: %s\n", event.kind().name(), child);
                System.out.println(event.kind().name());
                System.out.println(child);
                //TODO untar plugin and dispatch plugins
                try {
					System.out.println(Files.probeContentType(child));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                // Check archive file and install plugins.
                if (checkFileMimeType(child)){
                	try {
                		// get untar destination path from configuration service
                		File destionationFile = new File("/tmp/" + child.getFileName());
                		destionationFile.mkdirs();
                		PluginArchiveFileInfo unTarPluginInfo = unTarPlugin(child.toString(), destionationFile.toString());
						if (unTarPluginInfo.getStatus() != -1){
							Properties pluginPropeties = readPluginProperties(destionationFile.toString());
							installLiderPlugin(destionationFile.toString()+"/" + unTarPluginInfo.getLiderPluginName());
							installAhenkPlugin(destionationFile.toString() + "/" + unTarPluginInfo.getAhenkPluginName());
							installLiderConsolePlugin(destionationFile.toString()+"/site");
						}else {
							System.out.println("Eklenti arşiv dosyası lider için uygun değildir.");
						}
					} catch (IOException e) {
						System.out.println("Eklenti yukleme başarısız");
						e.printStackTrace();
					}
                }else {
                	System.out.println("Bu dosya lider plugini icin uygun degildir.");
                }
            }
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
	}
	
	
	public Boolean installLiderConsolePlugin(String pluginDirectoryPath){
		System.out.println("Lider Console yüklüyorum plugin pathh -> " + pluginDirectoryPath);
		return true;
	}
	
	public Boolean installAhenkPlugin(String debFilePath){
		System.out.println("Ahenk yüklüyorum plugin pathh  -> " + debFilePath);
		return true;
	}
	
	public Boolean installLiderPlugin(String pluginJarPath){
		System.out.println("Lider yüklüyorum plugin pathh -> " + pluginJarPath);
		return true;
	}
	
	
	public Properties readPluginProperties(String pluginPath) throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File(pluginPath+"/lider.properties")));
		return prop;
	}
	
	public boolean checkFileMimeType(Path filePath){
		try {
			//FIXME get from configuration manager
			if (Files.probeContentType(filePath).equalsIgnoreCase("application/gzip" ) 
				|| Files.probeContentType(filePath).equalsIgnoreCase("application/x-tar" )
				|| Files.probeContentType(filePath).equalsIgnoreCase("application/x-compressed-tar")){
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}   
	
	 //FIXME return file names of archived files
	 public PluginArchiveFileInfo unTarPlugin(String tarGzipFilePath, String destPath) throws IOException {
	        TarArchiveInputStream tarIn = null;
	        PluginArchiveFileInfo info = new PluginArchiveFileInfo();
	        try {
	        	GzipCompressorInputStream gzIn = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tarGzipFilePath), BUFFER));
	            tarIn = new TarArchiveInputStream(gzIn);
	            
	            TarArchiveEntry entry;
	            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
	                if (entry.isDirectory()) {
	                	System.out.println(entry.getName());
	                    File f = new File(destPath + File.separator + entry.getName());
	                    f.mkdirs();
	                } else {
	                	
	                	System.out.println(entry.getName());
	                	if(entry.getName().contains("/")){
	                		String[] split = entry.getName().split("/");
	                		String path = destPath + File.separator;
	                		
	                		for (int i = 0; i < split.length - 1; i++) {
								path = path + split[i] + File.separator;
							}
	                		
	                		File pf = new File(path);
	                		pf.mkdirs();
	                	}else if(entry.getName().endsWith("deb")){
	                		info.setAhenkPluginName(entry.getName());
	                	}else if(entry.getName().endsWith("jar")){
	                		info.setLiderPluginName(entry.getName());
	                	}
//	                	System.out.println(entry.getFile().getParent().toString());
	                    int count;
	                    byte data[] = new byte[BUFFER];
	                    FileOutputStream fos = new FileOutputStream(destPath + File.separator + entry.getName());
	                    BufferedOutputStream destOut = new BufferedOutputStream(fos, BUFFER);
	                    while ((count = tarIn.read(data, 0, BUFFER)) != -1) {
	                        destOut.write(data, 0, count);
	                    }
	                    destOut.close();
	                }
	            }
			} catch (Exception e) {
				e.printStackTrace();
				info.setStatus(-1);
			}finally {
	            if (tarIn != null) {
	                tarIn.close();
	            }
	        }
	     
	        
	        return info;
	    }
	
	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public void setManagedPluginDao(IManagedPluginDao managedPluginDao) {
		this.managedPluginDao = managedPluginDao;
	}

	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}
	
	
	public static void main(String...strings ){
		try {
//			unTarPlugin("/home/ismail/devzone/workspace/lider-ahenk/test/remoteplg/remote.tar.gz", "/opt/lider-server/deploy");
//			unZipIt("/home/ismail/devzone/workspace/lider-ahenk/test/remoteplg/test.zip", "/opt/lider-server/deploy");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
