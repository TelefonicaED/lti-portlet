package com.liferay.lms.lti;

import com.liferay.lms.learningactivity.BaseLearningActivityType;
import com.liferay.lms.lti.asset.LTIAssetRenderer;
import com.liferay.lms.model.LearningActivity;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
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
}
