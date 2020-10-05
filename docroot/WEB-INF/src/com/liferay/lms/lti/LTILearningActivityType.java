package com.liferay.lms.lti;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletResponse;

import com.liferay.lms.learningactivity.BaseLearningActivityType;
import com.liferay.lms.lti.asset.LTIAssetRenderer;
import com.liferay.lms.model.LearningActivity;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.upload.UploadRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetRenderer;

public class LTILearningActivityType extends BaseLearningActivityType {
	public static String PORTLET_ID = 
			PortalUtil.getJsSafePortletId(
					"lti" + PortletConstants.WAR_SEPARATOR + "ltiportlet");

	

	@Override
	public String getName() {
		return "learningactivity.lti";
	}
	
	@Override 
	public String getClassName(){
		return getClass().getName();
	}

	@Override
	public String getDescription() {
		return "learningactivity.lti.helpmessage";
	}

	@Override
	public String getPortletId() {
		return PORTLET_ID;
	}

	@Override
	public long getTypeId() {
		return 104;
	}

	@Override
	public boolean isTriesConfigurable() {
		return true;
	}

	@Override
	public AssetRenderer getAssetRenderer(LearningActivity larn) throws PortalException,SystemException{
		return new LTIAssetRenderer(larn,this);
	}
	
	@Override
	public boolean hasDeleteTries() {
		return true;
	}
	
	@Override
	public String setExtraContent(UploadRequest uploadRequest,PortletResponse portletResponse,LearningActivity learningActivity) throws PortalException,SystemException,DocumentException,IOException, NumberFormatException, Exception {
		
		
		
		String team = ParamUtil.getString(uploadRequest, "team","0");
		long teamId = 0;
		if(!team.equalsIgnoreCase("0")){
			teamId = Long.parseLong(team);
		}
		
		Document document = null;
		Element rootElement = null;
		if ((learningActivity.getExtracontent() == null)
				|| (learningActivity.getExtracontent().trim().length() == 0)) {
			document = SAXReaderUtil.createDocument();
			rootElement = document.addElement("other");
		} else {
			document = SAXReaderUtil.read(learningActivity.getExtracontent());
			rootElement = document.getRootElement();
		}

		Element url = rootElement.element("url");
		if (url != null) {
			url.detach();
			rootElement.remove(url);
		}
		url = SAXReaderUtil.createElement("url");
		url.setText(ParamUtil.getString(uploadRequest, "url", ""));
		rootElement.add(url);

		
		List<Element> ids = 	rootElement.elements("id");
	
		for(Element id: ids){
			if (id != null) {
				id.detach();
				rootElement.remove(id);
			}
		}
		
		Element id = SAXReaderUtil.createElement("id");
		id.setText(ParamUtil.getString(uploadRequest, "id", ""));
		rootElement.add(id);
		
		
		
		Element secret = rootElement.element("secret");
		if (secret != null) {
			secret.detach();
			rootElement.remove(secret);
		}
		secret = SAXReaderUtil.createElement("secret");
		secret.setText(ParamUtil.getString(uploadRequest, "secret", ""));
		rootElement.add(secret);
		
		
		Element rol = rootElement.element("rol");
		if (rol != null) {
			rol.detach();
			rootElement.remove(rol);
		}
		rol = SAXReaderUtil.createElement("rol");
		rol.setText(ParamUtil.getString(uploadRequest, "rol", ""));
		rootElement.add(rol);
		
		
		
		Element iframe = rootElement.element("iframe");
		if (iframe != null) {
			iframe.detach();
			rootElement.remove(iframe);
		}
		iframe = SAXReaderUtil.createElement("iframe");
		iframe.setText(ParamUtil.getString(uploadRequest, "iframe","false"));
		rootElement.add(iframe);
		
		
		Element note = rootElement.element("note");
		if (note != null) {
			note.detach();
			rootElement.remove(note);
		}
		note = SAXReaderUtil.createElement("note");
		note.setText(ParamUtil.getString(uploadRequest, "note", "0"));
		rootElement.add(note);
		
		
		if(!StringPool.BLANK.equals(team)){
			Element teamElement=rootElement.element("team");
			if(teamElement!=null)
			{
				teamElement.detach();
				rootElement.remove(teamElement);
			}
			if(teamId!=0){
				teamElement = SAXReaderUtil.createElement("team");
				teamElement.setText(Long.toString(teamId));
				rootElement.add(teamElement);
			}
		}
		
		
		
		learningActivity.setExtracontent(document.formattedString());
		
		
		
		return null;
	}
	
	@Override
	public String getExpecificContentPage() {
		return PortalUtil.getPathContext()+"/html/lti/admin/edit.jsp";
	}
	
	@Override
	public boolean hasEditDetails() {
		return false;
	}
}
