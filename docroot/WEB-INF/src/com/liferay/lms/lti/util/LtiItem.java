package com.liferay.lms.lti.util;

public class LtiItem {
	private long ltiItemId = 0;
	private String name = null;
	private String description= null;
	private String url= null;
	private String secret= null;
	private String rol= null;
	private String contenType= null;
	private Boolean iframe= null;
	private Integer note = null;
	private String id=null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getRol() {
		return rol;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}
	public String getContenType() {
		return contenType;
	}
	public void setContenType(String contenType) {
		this.contenType = contenType;
	}
	public Boolean getIframe() {
		return iframe;
	}
	public void setIframe(Boolean iframe) {
		this.iframe = iframe;
	}
	public Integer getNote() {
		return note;
	}
	public void setNote(Integer note) {
		this.note = note;
	}
	public long getLtiItemId() {
		return ltiItemId;
	}
	public void setLtiItemId(long ltiItemId) {
		this.ltiItemId = ltiItemId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
