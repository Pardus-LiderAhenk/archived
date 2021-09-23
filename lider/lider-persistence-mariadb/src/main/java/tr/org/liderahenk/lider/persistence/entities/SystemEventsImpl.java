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
package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.ISystemEvents;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "SystemEvents")
public class SystemEventsImpl implements ISystemEvents {

	private static final long serialVersionUID = -7714502390049469298L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	private Integer systemEventsId;

	@Column(name = "CustomerID", nullable = true)
	private Long customerId;

	@Column(name = "ReceivedAt", nullable = true)
	private Date receivedAt;

	@Column(name = "DeviceReportedTime", nullable = true)
	private Date deviceReportedTime;

	@Column(name = "Facility", nullable = true, length = 6)
	private Integer facility;

	@Column(name = "Priority", nullable = true, length = 6)
	private Integer priority;

	@Column(name = "FromHost", nullable = true, length = 60)
	private String fromHost;

	@Lob
	@Column(name = "Message", nullable = true)
	private String message;

	@Column(name = "NTSeverity", nullable = true, length = 11)
	private Integer ntSeverity;

	@Column(name = "Importance", nullable = true, length = 11)
	private Integer importance;

	@Column(name = "EventSource", nullable = true, length = 60)
	private String eventSource;

	@Column(name = "EventUser", nullable = true, length = 60)
	private String eventUser;

	@Column(name = "EventCategory", nullable = true, length = 11)
	private Integer eventCategory;

	@Column(name = "EventID", nullable = true, length = 11)
	private Integer eventId;

	@Lob
	@Column(name = "EventBinaryData", nullable = true)
	private String eventBinaryData;

	@Column(name = "MaxAvailable", nullable = true, length = 11)
	private Integer maxAvailable;

	@Column(name = "CurrUsage", nullable = true, length = 11)
	private Integer currUsage;

	@Column(name = "MinUsage", nullable = true, length = 11)
	private Integer minUsage;

	@Column(name = "MaxUsage", nullable = true, length = 11)
	private Integer maxUsage;

	@Column(name = "InfoUnitID", nullable = true, length = 11)
	private Integer infoUnitId;

	@Column(name = "SysLogTag", nullable = true, length = 60)
	private String sysLogTag;

	@Column(name = "EventLogType", nullable = true, length = 60)
	private String eventLogType;

	@Column(name = "GenericFileName", nullable = true, length = 60)
	private String genericFileName;

	@Column(name = "SystemID", nullable = true, length = 11)
	private Integer systemId;

	public SystemEventsImpl(Integer systemEventsId, Long customerId, Date receivedAt, Date deviceReportedTime,
			Integer facility, Integer priority, String fromHost, String message, Integer ntSeverity, Integer importance,
			String eventSource, String eventUser, Integer eventCategory, Integer eventId, String eventBinaryData,
			Integer maxAvailable, Integer currUsage, Integer minUsage, Integer maxUsage, Integer infoUnitId,
			String sysLogTag, String eventLogType, String genericFileName, Integer systemId) {
		super();
		this.systemEventsId = systemEventsId;
		this.customerId = customerId;
		this.receivedAt = receivedAt;
		this.deviceReportedTime = deviceReportedTime;
		this.facility = facility;
		this.priority = priority;
		this.fromHost = fromHost;
		this.message = message;
		this.ntSeverity = ntSeverity;
		this.importance = importance;
		this.eventSource = eventSource;
		this.eventUser = eventUser;
		this.eventCategory = eventCategory;
		this.eventId = eventId;
		this.eventBinaryData = eventBinaryData;
		this.maxAvailable = maxAvailable;
		this.currUsage = currUsage;
		this.minUsage = minUsage;
		this.maxUsage = maxUsage;
		this.infoUnitId = infoUnitId;
		this.sysLogTag = sysLogTag;
		this.eventLogType = eventLogType;
		this.genericFileName = genericFileName;
		this.systemId = systemId;
	}

	public SystemEventsImpl(ISystemEvents systemEvent) {
		this.systemEventsId = systemEvent.getSystemEventsId();
		this.customerId = systemEvent.getCustomerId();
		this.receivedAt = systemEvent.getReceivedAt();
		this.deviceReportedTime = systemEvent.getDeviceReportedTime();
		this.facility = systemEvent.getFacility();
		this.priority = systemEvent.getPriority();
		this.fromHost = systemEvent.getFromHost();
		this.message = systemEvent.getMessage();
		this.ntSeverity = systemEvent.getNtSeverity();
		this.importance = systemEvent.getImportance();
		this.eventSource = systemEvent.getEventSource();
		this.eventUser = systemEvent.getEventUser();
		this.eventCategory = systemEvent.getEventCategory();
		this.eventId = systemEvent.getEventId();
		this.eventBinaryData = systemEvent.getEventBinaryData();
		this.maxAvailable = systemEvent.getMaxAvailable();
		this.currUsage = systemEvent.getCurrUsage();
		this.minUsage = systemEvent.getMinUsage();
		this.maxUsage = systemEvent.getMaxUsage();
		this.infoUnitId = systemEvent.getInfoUnitId();
		this.sysLogTag = systemEvent.getSysLogTag();
		this.eventLogType = systemEvent.getEventLogType();
		this.genericFileName = systemEvent.getGenericFileName();
		this.systemId = systemEvent.getSystemId();
	}

	public SystemEventsImpl() {
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Date getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(Date receivedAt) {
		this.receivedAt = receivedAt;
	}

	public Date getDeviceReportedTime() {
		return deviceReportedTime;
	}

	public void setDeviceReportedTime(Date deviceReportedTime) {
		this.deviceReportedTime = deviceReportedTime;
	}

	public Integer getFacility() {
		return facility;
	}

	public void setFacility(Integer facility) {
		this.facility = facility;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getFromHost() {
		return fromHost;
	}

	public void setFromHost(String fromHost) {
		this.fromHost = fromHost;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getNtSeverity() {
		return ntSeverity;
	}

	public void setNtSeverity(Integer ntSeverity) {
		this.ntSeverity = ntSeverity;
	}

	public Integer getImportance() {
		return importance;
	}

	public void setImportance(Integer importance) {
		this.importance = importance;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getEventUser() {
		return eventUser;
	}

	public void setEventUser(String eventUser) {
		this.eventUser = eventUser;
	}

	public Integer getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(Integer eventCategory) {
		this.eventCategory = eventCategory;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public String getEventBinaryData() {
		return eventBinaryData;
	}

	public void setEventBinaryData(String eventBinaryData) {
		this.eventBinaryData = eventBinaryData;
	}

	public Integer getMaxAvailable() {
		return maxAvailable;
	}

	public void setMaxAvailable(Integer maxAvailable) {
		this.maxAvailable = maxAvailable;
	}

	public Integer getCurrUsage() {
		return currUsage;
	}

	public void setCurrUsage(Integer currUsage) {
		this.currUsage = currUsage;
	}

	public Integer getMinUsage() {
		return minUsage;
	}

	public void setMinUsage(Integer minUsage) {
		this.minUsage = minUsage;
	}

	public Integer getMaxUsage() {
		return maxUsage;
	}

	public void setMaxUsage(Integer maxUsage) {
		this.maxUsage = maxUsage;
	}

	public Integer getInfoUnitId() {
		return infoUnitId;
	}

	public void setInfoUnitId(Integer infoUnitId) {
		this.infoUnitId = infoUnitId;
	}

	public String getSysLogTag() {
		return sysLogTag;
	}

	public void setSysLogTag(String sysLogTag) {
		this.sysLogTag = sysLogTag;
	}

	public String getEventLogType() {
		return eventLogType;
	}

	public void setEventLogType(String eventLogType) {
		this.eventLogType = eventLogType;
	}

	public String getGenericFileName() {
		return genericFileName;
	}

	public void setGenericFileName(String genericFileName) {
		this.genericFileName = genericFileName;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Integer getSystemEventsId() {
		return systemEventsId;
	}

	public void setSystemEventsId(Integer systemEventsId) {
		this.systemEventsId = systemEventsId;
	}

	@Override
	public String toString() {
		return "SystemEventsImpl [systemEventsId=" + systemEventsId + ", customerId=" + customerId + ", receivedAt="
				+ receivedAt + ", deviceReportedTime=" + deviceReportedTime + ", facility=" + facility + ", importance="
				+ importance + ", facility=" + facility + ", priority=" + priority + ", fromHost=" + fromHost
				+ ", message=" + message + ", ntSeverity=" + ntSeverity + ", eventSource=" + eventSource
				+ ", eventBinaryData=" + eventBinaryData + ", eventLogType=" + eventLogType + ", eventUser=" + eventUser
				+ ", eventCategory=" + eventCategory + ", eventId=" + eventId + ", maxAvailable=" + maxAvailable
				+ ", currUsage=" + currUsage + ", minUsage=" + minUsage + ", maxUsage=" + maxUsage + ", infoUnitId="
				+ infoUnitId + ", sysLogTag=" + sysLogTag + ", eventLogType=" + eventLogType + ", genericFileName="
				+ genericFileName + ", systemId=" + systemId + "]";
	}
}
