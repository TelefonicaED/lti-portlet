package com.liferay.lms.lti.portlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.lms.lti.util.LtiItem;
import com.liferay.lms.lti.util.LtiItemLocalServiceUtil;
import com.liferay.lms.model.LearningActivity;
import com.liferay.lms.model.LearningActivityResult;
import com.liferay.lms.service.LearningActivityLocalServiceUtil;
import com.liferay.lms.service.LearningActivityResultLocalServiceUtil;
import com.liferay.lms.service.LearningActivityTryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.tls.basiclti.BasicLTIConstants;
import com.tls.basiclti.BasicLTIUtil;


public class LtiGeneralPortlet extends MVCPortlet {
	private static Log log = LogFactoryUtil.getLog(LtiGeneralPortlet.class);
	protected String editJSP;
	protected String viewJSP;

	public void init() throws PortletException {
		editJSP = getInitParameter("edit-template");
		viewJSP = getInitParameter("view-template");
	}
	
	public void doView(RenderRequest renderRequest,RenderResponse renderResponse) throws IOException, PortletException {
		String mode = (String) renderRequest.getParameter("mode");
		if(log.isDebugEnabled()){
			log.debug("mode:"+mode);
			log.debug("actId:"+renderRequest.getParameter("actId"));
			log.debug("moduleId:"+renderRequest.getParameter("moduleId"));
			log.debug("actionEditing:"+renderRequest.getParameter("actionEditing"));
		}
		if(mode!=null&&mode.equals("edit")){
			if(log.isDebugEnabled())log.debug("go:"+editJSP);
			Long actId = ParamUtil.getLong(renderRequest, "actId", 0);
			if(actId==0){
				actId = ParamUtil.getLong(renderRequest, "resId", 0);
			}
			renderRequest.setAttribute("actId", actId);
			LearningActivity learningActivity = null;
			try {
				learningActivity = LearningActivityLocalServiceUtil.getLearningActivity(actId);
			} catch (PortalException e) {
				if(log.isDebugEnabled())e.printStackTrace();
				if(log.isErrorEnabled())log.error(e.getMessage());
			} catch (SystemException e) {
				if(log.isDebugEnabled())e.printStackTrace();
				if(log.isErrorEnabled())log.error(e.getMessage());
			}
			if(learningActivity!=null){
				if(log.isDebugEnabled())log.debug("learningActivity::"+learningActivity);
				renderRequest.setAttribute("learningActivity", learningActivity);
				LtiItem ltiItem = LtiItemLocalServiceUtil.fetchByactId(actId);
				renderRequest.setAttribute("ltiItem", ltiItem);
			}
			renderRequest.setAttribute("mode", "view");
			include(editJSP, renderRequest, renderResponse);
		}else{
			Long actId = ParamUtil.getLong(renderRequest, "actId", 0);
			if(actId>0){
				LtiItem ltiItem = LtiItemLocalServiceUtil.fetchByactId(actId);
				if(ltiItem!=null){
					LearningActivity learningActivity = null;
					try {
						learningActivity = LearningActivityLocalServiceUtil.getLearningActivity(actId);
					} catch (PortalException e) {
						if(log.isDebugEnabled())e.printStackTrace();
						if(log.isErrorEnabled())log.error(e.getMessage());
					} catch (SystemException e) {
						if(log.isDebugEnabled())e.printStackTrace();
						if(log.isErrorEnabled())log.error(e.getMessage());
					}
					renderRequest.setAttribute("learningActivity", learningActivity);
					renderRequest.setAttribute("ltiItem", ltiItem);
					Map<String,String> postProp = new HashMap<String, String>();
					ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
					
					if(log.isDebugEnabled()){
						log.debug("link_id:"+PortalUtil.getCurrentURL(PortalUtil.getHttpServletRequest(renderRequest)));
						log.debug("url:"+ltiItem.getUrl());
						log.debug("secret:"+ltiItem.getSecret());
					}
					
					StringBuffer st = new StringBuffer();
					st.append(learningActivity.getActId());
					st.append("-");
					st.append(themeDisplay.getUser().getUserId());
					
					
					postProp.put(BasicLTIConstants.RESOURCE_LINK_ID, PortalUtil.getCurrentCompleteURL(PortalUtil.getHttpServletRequest(renderRequest))); 					 
					postProp.put(BasicLTIConstants.USER_ID,themeDisplay.getUser().getUuid());
					//CONTEXT_ID is optional, but recomended, this is a unique value
					postProp.put(BasicLTIConstants.CONTEXT_ID,st.toString());
					postProp.put(BasicLTIConstants.CONTEXT_TITLE,learningActivity.getTitle(LocaleUtil.getDefault().toString(), true));
					postProp.put(BasicLTIConstants.CONTEXT_LABEL,learningActivity.getTitle(LocaleUtil.getDefault().toString(), true));
					//postProp.put(BasicLTIConstants.CONTEXT_TYPE, ltiItem.getContenType());
					//Roles ar defines in standard: http://www.imsglobal.org/LTI/v1p1/ltiIMGv1p1.html#_Toc319560471
					boolean isTeacher=themeDisplay.getPermissionChecker().hasPermission(themeDisplay.getScopeGroupId(), "com.liferay.lms.model",themeDisplay.getScopeGroupId(), "VIEW_RESULTS");	
					if(isTeacher)
					{
						postProp.put(BasicLTIConstants.ROLES,"Instructor");
					}
					else
					{
						postProp.put(BasicLTIConstants.ROLES,"Student");
					}
					postProp.put(BasicLTIConstants.LIS_PERSON_NAME_GIVEN, themeDisplay.getUser().getFirstName());
					postProp.put(BasicLTIConstants.LIS_PERSON_NAME_FAMILY, themeDisplay.getUser().getLastName());
					postProp.put(BasicLTIConstants.LIS_PERSON_NAME_FULL, themeDisplay.getUser().getFullName());
					postProp.put(BasicLTIConstants.LIS_PERSON_CONTACT_EMAIL_PRIMARY, themeDisplay.getUser().getEmailAddress());
					postProp.put(BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE, themeDisplay.getLocale().toString());
					//At the moment only window
					postProp.put(BasicLTIConstants.LAUNCH_PRESENTATION_DOCUMENT_TARGET,"window");
					//Url notification
					postProp.put(BasicLTIConstants.LIS_OUTCOME_SERVICE_URL, PortalUtil.getPortalURL(PortalUtil.getHttpServletRequest(renderRequest))+"/lti-portlet/ltiservice");
					//This identificator from notification
					postProp.put(BasicLTIConstants.LIS_RESULT_SOURCEDID, st.toString());
					postProp.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_GUID, themeDisplay.getCompany().getWebId());
					postProp.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_NAME, themeDisplay.getCompany().getWebId());
					
					
					//TODO change id
					/*Map<String,String> props =  BasicLTIUtil.signProperties(postProp, ltiItem.getUrl(), "POST", String.valueOf(ltiItem.getLtiItemId()), ltiItem.getSecret(), 
							null, null, null,null,null);*/
					Map<String,String> props =  BasicLTIUtil.signProperties(postProp, ltiItem.getUrl(), "POST", ltiItem.getId(), ltiItem.getSecret(), 
							null, String.valueOf(ltiItem.getLtiItemId()), null,null,null);
					
					String postLaunch = BasicLTIUtil.postLaunchHTML(props, ltiItem.getUrl(), false,themeDisplay.getLocale(),ltiItem.getIframe());
					
					int times = 0;
					LearningActivityResult result = null;
					try {
						times = LearningActivityTryLocalServiceUtil.getTriesCountByActivityAndUser(actId, themeDisplay.getUserId());
						result = LearningActivityResultLocalServiceUtil.getByActIdAndUserId(actId, themeDisplay.getUserId());
					} catch (PortalException e) {
					} catch (SystemException e) {
					}

					if(log.isDebugEnabled())log.debug("PostLaunch\n"+postLaunch);
					renderRequest.setAttribute("postLaunch", postLaunch);
					renderRequest.setAttribute("times", times);
					renderRequest.setAttribute("result", result);
					include(viewJSP, renderRequest, renderResponse);
					
				}else{
					renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.FALSE);
				}
			}else{
				renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.FALSE);
			}
		}
	}

	@ProcessAction(name = "edit")
	public void edit(ActionRequest actionRequest,ActionResponse actionResponse) {

		actionResponse.setRenderParameters(actionRequest.getParameterMap());
		
		Enumeration<String> names = actionRequest.getAttributeNames();
    	
		
		Long actId = ParamUtil.getLong(actionRequest, "resId", 0);
		
		if(actId<=0)
			actId = ParamUtil.getLong(actionRequest, "actId", 0);
		
		LearningActivity learningActivity = null;
		try {
			learningActivity = LearningActivityLocalServiceUtil.getLearningActivity(actId);
		} catch (PortalException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}
		
		if(learningActivity!=null){
			if(log.isDebugEnabled())log.debug("learningActivity::"+learningActivity);
			actionRequest.setAttribute("learningActivity", learningActivity);
			LtiItem ltiItem = LtiItemLocalServiceUtil.fetchByactId(actId);
			actionRequest.setAttribute("ltiItem", ltiItem);
		}
		
	}

	@ProcessAction(name = "save")
	public void save(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

		Long actId = ParamUtil.getLong(actionRequest, "actId", 0);
		if(log.isDebugEnabled())log.debug("windowState:"+actionRequest.getWindowState());
		
		LtiItem ltiItem = LtiItemLocalServiceUtil.fetchByactId(actId);

		String url = ParamUtil.getString(actionRequest, "url", "");
		String id = ParamUtil.getString(actionRequest, "id", "");
		String secret = ParamUtil.getString(actionRequest, "secret", "");
		String rol = ParamUtil.getString(actionRequest, "rol", "");
		Boolean iframe = ParamUtil.getBoolean(actionRequest, "iframe", false);
		Integer note = ParamUtil.getInteger(actionRequest, "note", 0);
		
		if(ltiItem!=null){
			ltiItem.setUrl(url);
			ltiItem.setSecret(secret);
			ltiItem.setRol(rol);
			ltiItem.setIframe(iframe);
			ltiItem.setNote(note);
			ltiItem.setId(id);
			ltiItem = LtiItemLocalServiceUtil.updateLtiItem(ltiItem);
		}else{
			ltiItem = LtiItemLocalServiceUtil.create(actId, url, secret,id);
			ltiItem.setRol(rol);
			ltiItem.setIframe(iframe);
			ltiItem.setNote(note);
			ltiItem = LtiItemLocalServiceUtil.updateLtiItem(ltiItem);	
		}

		actionResponse.setRenderParameter("mode", "edit");
		actionResponse.setRenderParameter("actId", String.valueOf(actId));
	}
	
	protected void include(String path, RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		PortletRequestDispatcher portletRequestDispatcher = getPortletContext()
				.getRequestDispatcher(path);

		if (portletRequestDispatcher == null) {
			// do nothing
			// _log.error(path + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
}
