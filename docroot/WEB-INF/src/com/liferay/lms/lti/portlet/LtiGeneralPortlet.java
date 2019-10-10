package com.liferay.lms.lti.portlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.lms.lti.util.LtiItem;
import com.liferay.lms.lti.util.LtiItemLocalServiceUtil;
import com.liferay.lms.model.LearningActivity;
import com.liferay.lms.model.LearningActivityResult;
import com.liferay.lms.model.LearningActivityTry;
import com.liferay.lms.service.LearningActivityLocalServiceUtil;
import com.liferay.lms.service.LearningActivityResultLocalServiceUtil;
import com.liferay.lms.service.LearningActivityTryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
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



	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {
		log.debug("---- RENDER");
		long actId=0;
		boolean actionEditingDetails = ParamUtil.getBoolean(renderRequest, "actionEditingDetails", false);

		if(actionEditingDetails){
			actId=ParamUtil.getLong(renderRequest, "resId", 0);
			
		}
		else{
			actId=ParamUtil.getLong(renderRequest, "actId", 0);
			renderResponse.setProperty("clear-request-parameters",Boolean.TRUE.toString());

		}
		//renderResponse.setProperty("clear-request-parameters",Boolean.TRUE.toString());

		if(actId==0){
			renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.FALSE);
		}
		super.render(renderRequest, renderResponse);
	}

	public void doDispatch(RenderRequest renderRequest,RenderResponse renderResponse) throws IOException, PortletException {

		log.debug("---- DO DISPATCH");
		super.doDispatch(renderRequest, renderResponse);
	}

	public void doView(RenderRequest renderRequest,RenderResponse renderResponse) throws IOException, PortletException {
		String mode = (String) renderRequest.getParameter("mode");
		log.debug("---- DO VIEW");
		if(log.isDebugEnabled()){
			log.debug("mode:"+mode);
			log.debug("actId:"+renderRequest.getParameter("actId"));
			log.debug("moduleId:"+renderRequest.getParameter("moduleId"));
			log.debug("actionEditing:"+renderRequest.getParameter("actionEditing"));
		}
		
		String lti_msg = ParamUtil.getString(PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)), "lti_msg","");
		String lti_log = ParamUtil.getString(PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)), "lti_log","");
		String lti_errorlog = ParamUtil.getString(PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)), "lti_errorlog","");
		String lti_errormsg = ParamUtil.getString(PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)), "lti_errormsg","");
		if(Validator.isNotNull(lti_msg)){
			SessionMessages.add(renderRequest, "lti_msg");
			renderRequest.setAttribute("lti_msg", lti_msg);
		}
		if(Validator.isNotNull(lti_log)){
			log.info("--Log from lti provider: "+lti_log);
		}
		
		if(Validator.isNotNull(lti_errormsg)){
			SessionErrors.add(renderRequest, "lti_errormsg");
			renderRequest.setAttribute("lti_errormsg", lti_errormsg);
		}
		if(Validator.isNotNull(lti_errorlog)){
			log.error("--Log error from lti provider: "+lti_errorlog);
		}
		Long actId = ParamUtil.getLong(renderRequest, "actId", 0);
		if(actId>0){
			try{
				LtiItem ltiItem = LtiItemLocalServiceUtil.fetchByactId(actId);
				if(ltiItem!=null && ltiItem.getUrl()!=null){
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
	
					
					int times = 0;
					LearningActivityResult result = null;
					try {
						times = LearningActivityTryLocalServiceUtil.getTriesCountByActivityAndUser(actId, themeDisplay.getUserId());
						result = LearningActivityResultLocalServiceUtil.getByActIdAndUserId(actId, themeDisplay.getUserId());
					} catch (PortalException e) {
					} catch (SystemException e) {
					}
					
					
					renderRequest.setAttribute("times", times);
					renderRequest.setAttribute("result", result);
					
					if(times<learningActivity.getTries()||learningActivity.getTries()==0){
						if(log.isDebugEnabled()){
							log.debug("link_id:"+PortalUtil.getCurrentURL(PortalUtil.getHttpServletRequest(renderRequest)));
							log.debug("url:"+ltiItem.getUrl());
							log.debug("secret:"+ltiItem.getSecret());
						}
		
						ServiceContext serviceContext;
						
							serviceContext = ServiceContextFactory.getInstance(LearningActivityTry.class.getName(), renderRequest);
						
							LearningActivityTry lat =LearningActivityTryLocalServiceUtil.createOrDuplicateLast(actId,serviceContext);
							
							StringBuffer st = new StringBuffer();
							st.append(lat.getLatId());
							st.append("-");
							st.append(themeDisplay.getUserId());
			
							//Identificador para comunicarse con el servicio
							postProp.put(BasicLTIConstants.LIS_RESULT_SOURCEDID, st.toString());
							postProp.put(BasicLTIConstants.LIS_OUTCOME_SERVICE_URL, PortalUtil.getPortalURL(PortalUtil.getHttpServletRequest(renderRequest))+"/lti-portlet/ltiservice");
							
							//Identificador del recurso
							postProp.put(BasicLTIConstants.RESOURCE_LINK_ID, st.toString()); 	
							postProp.put(BasicLTIConstants.RESOURCE_LINK_TITLE, learningActivity.getTitle(themeDisplay.getLocale())); 
							postProp.put(BasicLTIConstants.RESOURCE_LINK_DESCRIPTION, learningActivity.getDescription(themeDisplay.getLocale()));
							
							//URL de vuelta para la capa de presentacion
							postProp.put(BasicLTIConstants.LAUNCH_PRESENTATION_RETURN_URL, PortalUtil.getPortalURL(PortalUtil.getHttpServletRequest(renderRequest))+themeDisplay.getURLCurrent());
							
							
							//CONTEXT_ID is optional, but recomended, this is a unique value
						    postProp.put(BasicLTIConstants.CONTEXT_ID,st.toString());
							postProp.put(BasicLTIConstants.CONTEXT_TITLE,learningActivity.getTitle(LocaleUtil.getDefault().toString(), true));
							postProp.put(BasicLTIConstants.CONTEXT_LABEL,learningActivity.getTitle(LocaleUtil.getDefault().toString(), true));
							postProp.put(BasicLTIConstants.CONTEXT_TYPE, ltiItem.getContenType());
			
							//Rol del usuario.
							boolean isTeacher=themeDisplay.getPermissionChecker().hasPermission(themeDisplay.getScopeGroupId(), "com.liferay.lms.model",themeDisplay.getScopeGroupId(), "VIEW_RESULTS");	
							if(isTeacher)
							{
								postProp.put(BasicLTIConstants.ROLES,"Instructor");
							}
							else
							{
								postProp.put(BasicLTIConstants.ROLES,"Learner");
							}
							
							//Identificacion del usuario
							postProp.put(BasicLTIConstants.LIS_PERSON_NAME_GIVEN, themeDisplay.getUser().getFirstName());
							postProp.put(BasicLTIConstants.LIS_PERSON_NAME_FAMILY, themeDisplay.getUser().getLastName());
							postProp.put(BasicLTIConstants.LIS_PERSON_NAME_FULL, themeDisplay.getUser().getFullName());
							postProp.put(BasicLTIConstants.LIS_PERSON_CONTACT_EMAIL_PRIMARY, themeDisplay.getUser().getEmailAddress());
							postProp.put(BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE, themeDisplay.getLocale().toString());
							postProp.put(BasicLTIConstants.USER_ID,String.valueOf(themeDisplay.getUserId()));
							
							
							postProp.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_GUID, themeDisplay.getCompany().getWebId());
							postProp.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_NAME, themeDisplay.getCompany().getWebId());
							try {
								postProp.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_DESCRIPTION, themeDisplay.getCompany().getName());
							//postProp.put(BasicLTIConstants.TOOL_CONSUMER_INFO_VERSION, )
							}catch(Exception e){
								log.debug(e);
							}
					
							Map<String,String> props =  BasicLTIUtil.signProperties(postProp, ltiItem.getUrl(), "POST", ltiItem.getId(), ltiItem.getSecret(), 
									null, String.valueOf(ltiItem.getLtiItemId()), null,null,null);
			
							String postLaunch = BasicLTIUtil.postLaunchHTML(props, ltiItem.getUrl(), false,themeDisplay.getLocale(),ltiItem.getIframe());
			
							
			
							if(log.isDebugEnabled())log.debug("PostLaunch\n"+postLaunch);
							renderRequest.setAttribute("postLaunch", postLaunch);
						}
				
				
				}else{
					SessionErrors.add(renderRequest,"lti-not-configured");
				}
			}catch (Exception e) {
				e.printStackTrace();
				SessionErrors.add(renderRequest,"lti-not-configured");
			}
		}else{
			renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.FALSE);
		}
		
		include(viewJSP, renderRequest, renderResponse);

	}

	@ProcessAction(name = "edit")
	public void edit(ActionRequest actionRequest,ActionResponse actionResponse) {
		log.debug("---- EDIT");
		actionResponse.setRenderParameters(actionRequest.getParameterMap());

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
