package tr.org.liderahenk.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.directory.api.ldap.model.message.SearchScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.SearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

@Controller
public class LoginController {

	@Autowired
	private ILDAPService ldapProcessor;

	@Autowired
	private IResponseFactory responseFactory;
	
	@Autowired
	private IPluginDao pluginDao;
	
	@Autowired
	private IServiceRegistry serviceRegistry;
	
	private List<LdapEntry> treeList;
	
	 private @Autowired ServletContext servletContext;


	@RequestMapping("/")
	public String main(Model model) {

		model.addAttribute("hello_str", "nbr");

		return "index";
	}

	@RequestMapping(value = "/getEntry", method = { RequestMethod.POST })
	public @ResponseBody List<LdapEntry> getEntries(@RequestBody String requestBody, HttpServletRequest request,Model model) {

		List<LdapEntry> entries=null;
		try {
			String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
			requestBodyDecoded = requestBodyDecoded.trim();
			
			String dn=requestBodyDecoded.substring(requestBodyDecoded.indexOf("=")+1, requestBodyDecoded.length());
			
			dn= dn.trim();

			// (!(objectClass=person))
			 entries = ldapProcessor.findSubEntries(dn, "(!(objectclass=organizationalUnit))",
					 new String[] { "*" }, SearchScope.ONELEVEL);
			
			model.addAttribute("ldapTreeList", treeList);
			model.addAttribute("entrylist", entries);

		} catch (Exception e) {
			e.printStackTrace();
		}

		IRestResponse resp = responseFactory.createResponse(RestResponseStatus.ERROR,
				Arrays.asList(new String[] { "NOT_AUTHORIZED" }));

		return entries;
	}
	
	@RequestMapping(value = "/getHtml", method = { RequestMethod.POST })
	public @ResponseBody String getHtml(@RequestBody String name, HttpServletRequest request,Model model) {
		
		try {
			name = name.trim();
			model.addAttribute("htmlPageLink", "/getPluginHtmlPage/conky");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IRestResponse resp = responseFactory.createResponse(RestResponseStatus.ERROR,
				Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
		
		return "main";
	}

	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public String login(@RequestParam(value = "lg_username", required = false) String lg_username,
			@RequestParam(value = "lg_password", required = false) String lg_password, HttpServletRequest request,
			Model model) {

		try {

			List<LdapSearchFilterAttribute> filterAtt = new ArrayList<>();
			filterAtt.add(new LdapSearchFilterAttribute("cn", lg_username, SearchFilterEnum.EQ));
			filterAtt.add(new LdapSearchFilterAttribute("userPassword", lg_password, SearchFilterEnum.EQ));

			List<LdapEntry> user = ldapProcessor.search(filterAtt, new String[] { "*" });

			if (user == null || user.size() == 0) {
				return "index";

			} else {

				HttpSession sessionInf = request.getSession();

				sessionInf.setAttribute("sessionMember", user.get(0));
				sessionInf.setAttribute("userName", lg_username);
				sessionInf.setAttribute("userNameJid", lg_username+"@im.mys.pardus.org.tr"); // service name getting from configuration service IConfigurationService configurationService;
				sessionInf.setAttribute("userPassword", lg_password);

				// get ldap tree

				LdapEntry domainBaseEntry = ldapProcessor.getDomainEntry();
				
				List<? extends IPlugin> plugins = pluginDao.findAll(IPlugin.class, 200);
				
				sessionInf.setAttribute("pluginList", plugins);
				
				HashMap<String, ICommand> commands= serviceRegistry.getCommands();
				
				
				ArrayList<String> commandList=new ArrayList<>();
				
				for (IPlugin iPlugin : plugins) {

				//	System.out.println("PLUGIN : "+ iPlugin.getName() + "    "+iPlugin.getVersion());
				//	System.out.println("LOOKING for commands");
					
					for (Iterator iterator = commands.keySet().iterator(); iterator.hasNext();) {
						
						String commandsPluginFullName= (String) iterator.next();
						
					//	System.out.println(" ... > commands full name : " + commandsPluginFullName );
						
						String[] commandFullNameArr=commandsPluginFullName.split(":");
						String commandName="";
						if(commandFullNameArr.length>1){
							commandName=commandFullNameArr[0];
						}
						
						if(iPlugin.getName().toUpperCase().equals(commandName)){
							
						//	System.out.println(" >>>>>>>>>>>>>>>>>> eslesme bulundu.. plugin name :  " + iPlugin.getName() + " command name : " + commandName  );
							ICommand iCommand= commands.get(commandsPluginFullName);
							commandList.add(iCommand.getCommandId());
							
						}
						
					}
					
				}
				
				// List<LdapEntry>
				// entries=ldapProcessor.findSubEntries(domainBaseEntry.getDistinguishedName(),"(objectclass=organizationalUnit)",
				// new String[]{"cn","entryUUID","hasSubordinates"},
				// SearchScope.ONELEVEL);

				// domainBaseEntry.setChildEntries(entries);

				ldapProcessor.getLdapTree(domainBaseEntry);

				treeList = new ArrayList<>();
				createList(domainBaseEntry);

				// for (LdapEntry ldapEntry : entries) {
				//
				// ldapEntry.setParent(domainBaseEntry.getEntryUUID());
				//
				// }
				// entryTree.addAll(entries);

				//model.addAttribute("ldapTreeList", treeList);
				sessionInf.setAttribute("ldapTreeList", treeList);
				sessionInf.setAttribute("deneme", "deneme");
				model.addAttribute("ldapBaseDn", domainBaseEntry);

			}

		} catch (LdapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "main";
	}
	
	
//	@RequestMapping(value = "/addSelectEntry", method = { RequestMethod.POST })
//	public @ResponseBody List<LdapEntry> addSelectEntry (@RequestBody String requestBody, HttpServletRequest request,Model model) {
//		
//		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
//		requestBodyDecoded = requestBodyDecoded.trim();
//		
//
//		try {
//
//			List<LdapEntry> selectedEntryDetail= ldapProcessor.findSubEntries( selectedEntry ,"(objectclass=*)",
//					new String[]{"*"}, SearchScope.OBJECT);
//			
//			if (!selectedEntryDetail.isEmpty() && selectedEntryDetail.size() > 0) {
//
//				HttpSession sessionInf = request.getSession();
//
//				List<LdapEntry> selectedEntryList = (ArrayList<LdapEntry>) sessionInf.getAttribute("selectedEntryList");
//
//				if (selectedEntryList == null)
//					selectedEntryList = new ArrayList<>();
//
//				selectedEntryList.add(selectedEntryDetail.get(0));
//
//				sessionInf.setAttribute("selectedEntryList", selectedEntryList);
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "main";
//	}
	
	@RequestMapping(value = "/xmppconnection", method = { RequestMethod.POST })
	public String xmppConnection(@RequestBody String requestBody, HttpServletRequest request) {
		
		System.out.println(requestBody);
		
		try {
			
			HttpSession sessionInf = request.getSession();
			
			String status= "xmppConnected";


			sessionInf.setAttribute("xmppconnectedStatus", status);
			
			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "main";
	}
	
	
//	@RequestMapping(value="/getPluginHtmlPage/{dn}", produces = MediaType.TEXT_HTML_VALUE,  method = { RequestMethod.GET })
	@RequestMapping(value="/getPluginHtmlPage/{dn}", method = { RequestMethod.GET })
//	@ResponseBody
	public String getData(@PathVariable("dn") String dn, Model model) {
		
		
		InputStream inputStream = null;
		 String html="";
		
		try {
            inputStream = servletContext.getResourceAsStream("/WEB-INF/views/conky/conky.jsp");
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            
            String line;
            
            while ((line = bufferedReader.readLine()) != null) {
				html = html + line;
			}
             
         	System.out.println("");
			model.addAttribute("deneme", "deneme");
			model.addAttribute("html", html);
             
             
        }
		catch(Exception e){
			e.printStackTrace();
		}
		
		finally {
            if (inputStream != null) {
               try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        }
		
		IRestResponse resp = responseFactory.createResponse(RestResponseStatus.OK,html);
		return "conky/conky";
	}

	public void createList(LdapEntry entry) {

		if (entry.getChildEntries().size() == 0) {
			treeList.add(entry);

		} else {
			treeList.add(entry);

			for (LdapEntry ldapEntry : entry.getChildEntries()) {

				createList(ldapEntry);
			}
		}
	}

	public IPluginDao getPluginDao() {
		return pluginDao;
	}

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	public IServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(IServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
