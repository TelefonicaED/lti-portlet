package com.liferay.lms.lti.util;

import java.util.HashMap;

import com.liferay.lms.model.LearningActivity;
import com.liferay.lms.service.LearningActivityLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

public class LtiItemLocalServiceUtil {
	private static Log log = LogFactoryUtil.getLog(LtiItemLocalServiceUtil.class);
	
	public static LtiItem create(long actId, String url, String secret,String id){
		LtiItem ltiItem = new LtiItem();
		ltiItem.setUrl(url);
		ltiItem.setSecret(secret);
		ltiItem.setLtiItemId(actId);
		ltiItem.setId(id);
		return ltiItem;
	}
	
	public static LtiItem updateLtiItem(LtiItem ltiItem){
		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "contenType", ltiItem.getContenType());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "description", ltiItem.getDescription());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "iframe", String.valueOf(ltiItem.getIframe()));
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "name", ltiItem.getName());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "note", String.valueOf(ltiItem.getNote()));
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "rol", ltiItem.getRol());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "id", ltiItem.getId());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "secret", ltiItem.getSecret());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}

		try {
			LearningActivityLocalServiceUtil.setExtraContentValue(ltiItem.getLtiItemId(), "url", ltiItem.getUrl());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}
		
		return ltiItem;
	}
	
	public static LtiItem fetchByactId(long actId){
		LearningActivity learningActivity = null;
		try {
			learningActivity = LearningActivityLocalServiceUtil.getLearningActivity(actId);
			if(learningActivity.getTypeId()!=104){
				learningActivity = null;
			}
		} catch (PortalException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		} catch (SystemException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}
		
		return fetchByactId(learningActivity);
	}

	public static LtiItem fetchByactId(LearningActivity learningActivity){
		
		if(learningActivity==null){
			return null;
		}else{
			LtiItem ltiItem = new LtiItem();
			ltiItem.setLtiItemId(learningActivity.getActId());
			String extra = learningActivity.getExtracontent();
			if(extra!=null&&!"".equals(extra)){
				ltiItem.setContenType( getExtraContentValue(extra, "contenType"));
				ltiItem.setDescription( getExtraContentValue(extra, "description"));
				try{
					ltiItem.setIframe(Boolean.valueOf(getExtraContentValue(extra, "iframe")));
				}catch(Exception e){
					ltiItem.setIframe(false);
				}
				ltiItem.setName( getExtraContentValue(extra, "name"));
				try{
					ltiItem.setNote(Integer.valueOf(getExtraContentValue(extra, "note")));
				}catch(Exception e){
					ltiItem.setIframe(false);
				}
				ltiItem.setRol(getExtraContentValue(extra, "rol"));
				ltiItem.setSecret(getExtraContentValue(extra, "secret"));
				ltiItem.setId(getExtraContentValue(extra, "id"));
				ltiItem.setUrl(getExtraContentValue(extra, "url"));
			}
			return ltiItem;
		}
	}
	
	private static String getExtraContentValue(String extra, String key){

		try {
			HashMap<String, String> hashMap = convertXMLExtraContentToHashMap(extra);
			if(hashMap.containsKey(key)){
				return hashMap.get(key);
			}
			else{
				return "";
			}
		} catch (Exception e) {
		}

		return "";
	}
	
	private static HashMap<String, String> convertXMLExtraContentToHashMap(String extra){
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		Document document;

		try {		
			document = SAXReaderUtil.read(extra);
			Element rootElement = document.getRootElement();
	
			for(Element key:rootElement.elements()){
	
				if(key.getName().equals("document")){
					hashMap.put(key.getName(), key.attributeValue("id") );
				}else{
					hashMap.put(key.getName(), key.getText());
				}
	
			}
		}catch (DocumentException e) {
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}
		
		return hashMap;
	}
}
