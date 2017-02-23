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
	try {
		learningActivity = LearningActivityLocalServiceUtil.getLearningActivity(actId);
	} catch (PortalException e) {
	} catch (SystemException e) {
	}
	if(learningActivity!=null){
		renderRequest.setAttribute("learningActivity", learningActivity);
		LtiItem ltiItem = LtiItemLocalServiceUtil.fetchByactId(actId);
		renderRequest.setAttribute("ltiItem", ltiItem);
	}
	
	
	
	LiferayPortletURL backUrl = PortletURLFactoryUtil.create(request, PortalUtil.getJsSafePortletId("editactivity"+
			PortletConstants.WAR_SEPARATOR+"liferaylmsportlet"), themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);
	backUrl.setWindowState(LiferayWindowState.NORMAL);
	backUrl.setParameter("actId", String.valueOf(learningActivity.getActId()));
	backUrl.setParameter("actionEditingActivity", StringPool.TRUE);
	backUrl.setParameter("resId", String.valueOf(learningActivity.getActId()));	
	backUrl.setParameter("resModuleId", String.valueOf(learningActivity.getModuleId()));
	backUrl.setParameter("jspPage", "/html/editactivity/editactivity.jsp");
	request.setAttribute("backUrl", backUrl.toString());
%>

<liferay-ui:header title="<%=learningActivity.getTitle(themeDisplay.getLocale())%>" backURL="<%= (request.getAttribute(\"backUrl\")!=null)?(String)request.getAttribute(\"backUrl\"):\"\" %>"/>

<script type="text/javascript">
	function check(){
		if(document.getElementById('url').value==null||document.getElementById('url').value==""){
			alert("<liferay-ui:message key='learningactivity.lti.error.url' />");
			return;
		}
		if(document.getElementById('id').value==null||document.getElementById('id').value==""){
			alert("<liferay-ui:message key='learningactivity.lti.error.id' />");
			return;
		}
		if(document.getElementById('secret').value==null||document.getElementById('secret').value==""){
			alert("<liferay-ui:message key='learningactivity.lti.error.secret' />");
			return;
		}
		if(document.getElementById('note').value==null||document.getElementById('note').value==""){
			alert("<liferay-ui:message key='learningactivity.lti.error.note' />");
			return;
		}
		if(!isInt(document.getElementById('note').value)){
			alert("<liferay-ui:message key='learningactivity.lti.error.note.nan' />");
			return;
		}
		document.getElementById("saveform").submit();
	}
	
	function isInt(x) {
		  var y=parseInt(x);
		  if (isNaN(y)) return false;
		  return x==y && x.toString()==y.toString();
	}
</script>
<c:if test="${not empty learningActivity}">
	<liferay-ui:message key="learningactivity.lti.propertiesfor" /> ${learningActivity.getTitle(themeDisplay.locale)}
	<form action="${save}" name="saveform" id="saveform" method="POST" >
		<input type="hidden" name="actId" id="actId" value="${learningActivity.actId}" >
		<c:choose>
			<c:when test="${not empty ltiItem}">
				<liferay-ui:message key="learningactivity.lti.url" />:<br/><input type="text" name="url" id="url" value="${ltiItem.url}" ><br/>
				<liferay-ui:message key="learningactivity.lti.key" />:<br/><input type="text" name="id" id="id" value="${ltiItem.id}" ><br/>
				<liferay-ui:message key="learningactivity.lti.secret" />:<br/><input type="text" name="secret" id="secret" value="${ltiItem.secret}" ><br/>
				<input type="hidden" name="rol" id="rol" value="Student" >
				<liferay-ui:message key="learningactivity.lti.note" />:<br/><input type="text" name="note" id="note" value="${ltiItem.note}" ><br/>
				<liferay-ui:message key="learningactivity.lti.iframe" />:<input type="checkbox" name="iframe" id="iframe" <c:if test="${ltiItem.iframe}">checked="checked"</c:if> ><br/>
			</c:when>
			<c:otherwise>
				<liferay-ui:message key="learningactivity.lti.url" />:<br/><input type="text" name="url" id="url" value="" ><br/>
				<liferay-ui:message key="learningactivity.lti.key" />:<br/><input type="text" name="id" id="id" value="" ><br/>
				<liferay-ui:message key="learningactivity.lti.secret" />:<br/><input type="text" name="secret" id="secret" value="" ><br/>
				<input type="hidden" name="rol" id="rol" value="Student" >
				<liferay-ui:message key="learningactivity.lti.note" />:<br/><input type="text" name="note" id="note" value="0" ><br/>
				<liferay-ui:message key="learningactivity.lti.iframe" />:<input type="checkbox" name="iframe" id="iframe" ><br/>
			</c:otherwise>
		</c:choose>
		<aui:button-row>
			<aui:button type="button" value="save" onclick="javascript:check()" />
			<aui:button onClick="<%= (request.getAttribute(\"backUrl\")!=null)?(String)request.getAttribute(\"backUrl\"):\"\" %>" type="cancel" value="canceledition"/>
		</aui:button-row>
		
	</form>
</c:if>