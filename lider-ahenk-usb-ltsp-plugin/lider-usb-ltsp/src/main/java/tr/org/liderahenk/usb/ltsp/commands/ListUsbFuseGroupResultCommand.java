package tr.org.liderahenk.usb.ltsp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.usb.ltsp.entities.UsbFuseGroupResult;
import tr.org.liderahenk.usb.ltsp.plugininfo.PluginInfoImpl;

/**
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ListUsbFuseGroupResultCommand implements ICommand {

	private static final Logger logger = LoggerFactory.getLogger(ListUsbFuseGroupResultCommand.class);
	
	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	private EntityManager entityManager;
	
	private static final String FIND_RESULTS = 
			"SELECT r.USB_FUSE_GROUP_RESULT_ID, r.USERNAME, r.UID, r.STATE_CODE, r.CREATE_DATE, r.END_DATE "
			+ "FROM P_USB_FUSE_GROUP_RESULT r "
			+ "INNER JOIN (SELECT USERNAME, UID, MAX(CREATE_DATE) AS MAX_DATE, CREATE_DATE FROM P_USB_FUSE_GROUP_RESULT GROUP BY USERNAME, UID) t "
			+ "ON (r.USERNAME = t.USERNAME AND r.UID = t.UID AND r.CREATE_DATE = t.MAX_DATE) "
			+ "WHERE 1=1 #CONDITION# "
			+ "ORDER BY r.CREATE_DATE DESC";
	
	private static final String UID_CONDITION = "AND r.uid = ?1";

	@SuppressWarnings("unchecked")
	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		Map<String, Object> parameterMap = context.getRequest().getParameterMap();
		String uid = (String) parameterMap.get("uid");

		String sql = FIND_RESULTS.replace("#CONDITION#", uid != null && !uid.isEmpty() ? UID_CONDITION : "");
		Query query = entityManager.createNativeQuery(sql, UsbFuseGroupResult.class);
		if (uid != null && !uid.isEmpty()) {
			query.setParameter(1, uid);
		}
		
		List<UsbFuseGroupResult> result = query.getResultList();
		logger.info("Number of fuse group results found: {0}", new Object[] {result.size()});
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fuse-group-results", new ObjectMapper().writeValueAsString(result));

		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	@Override
	public String getCommandId() {
		return "LIST_USB_FUSE_GROUP_STATUS";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setPluginInfo(PluginInfoImpl pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
