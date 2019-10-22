<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.model.PortletConstants"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortletURL"%>
<%@page import="com.liferay.lms.lti.util.LtiItem"%>
<%@page import="com.liferay.lms.lti.util.LtiItemLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.exception.SystemException"%>
<%@page import="com.liferay.portal.kernel.exception.PortalException"%>
<%@page import="com.liferay.lms.service.LearningActivityLocalServiceUtil"%>
<%@page import="com.liferay.lms.model.LearningActivity"%>
<%@ include file="/html/init.jsp" %>

<portlet:actionURL name="save" var="save" />


<%
	Long actId = ParamUtil.getLong(renderRequest, "actId", 0);
	if(actId==0){
		actId = ParamUtil.getLong(renderRequest, "resId", 0);
	}
	
	LearningActivity learningActivity = null;
    String url= "";
	String secret= "";
	Boolean iframe= false;
	Integer note = 0;
	String id="";
	
	try {
		learningActivity = LearningActivityLocalServiceUtil.getLearningActivity(actId);
		
		url= LearningActivityLocalServiceUtil.getExtraContentValue(actId,  "url", "");
		secret= LearningActivityLocalServiceUtil.getExtraContentValue(actId,  "secret", "");
		iframe= Boolean.parseBoolean(LearningActivityLocalServiceUtil.getExtraContentValue(actId,  "iframe", "false"));
		note = Integer.parseInt(LearningActivityLocalServiceUtil.getExtraContentValue(actId,  "note", "0"));
		id=LearningActivityLocalServiceUtil.getExtraContentValue(actId,  "id", "");
	} catch (PortalException e) {
	} catch (SystemException e) {
	}
	if(learningActivity!=null){
		renderRequest.setAttribute("learningActivity", learningActivity);
	}
	
	
%>

	<aui:input name="url"  value="<%=url %>" label="learningactivity.lti.url">
			<aui:validator name="required"></aui:validator>
		
	</aui:input>
	<aui:input name="id"  value="<%=id %>" label="learningactivity.lti.key">
		<aui:validator name="required"></aui:validator>
	</aui:input>
	<input type="hidden" name="rol" id="rol" value="Student" >
	<aui:input name="secret"  value="<%=secret %>" label="learningactivity.lti.secret">
		<aui:validator name="required"></aui:validator>
	</aui:input>
	<aui:input name="note"  value="<%=note %>" label="learningactivity.lti.note">
		<aui:validator name="required"></aui:validator>
		<aui:validator name="number"></aui:validator>
	</aui:input>
	<aui:input name="iframe" type="checkbox" label="learningactivity.lti.iframe" value="<%=iframe %>" checked="<%=iframe %>"/>
	

	
