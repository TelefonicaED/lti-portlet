package com.liferay.lms.lti.asset;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import com.liferay.lms.asset.LearningActivityBaseAssetRenderer;
import com.liferay.lms.lti.LTILearningActivityType;
import com.liferay.lms.model.LearningActivity;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.PortletURLFactoryUtil;

public class LTIAssetRenderer extends LearningActivityBaseAssetRenderer {

	public LTIAssetRenderer(LearningActivity learningactivity,
			LTILearningActivityType connectLearningActivityType)
			throws SystemException, PortalException {
		super(learningactivity,connectLearningActivityType,false);
	}
	
	@Override
	protected PortletURL getURLEditDetails(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) throws Exception {
		PortletURL portletURL = PortletURLFactoryUtil.create(liferayPortletRequest,getPortletId(),getLayout().getPlid(),PortletRequest.RENDER_PHASE);	
		portletURL.setParameter("mvcPath", "/html/lti/admin/edit.jsp");
		portletURL.setParameter("actionEditingDetails", StringPool.TRUE);
	    portletURL.setParameter("resId",Long.toString( getLearningactivity().getActId()));
	    return portletURL;
	}
	
	
	@Override
	 protected String getMvcPathView(long userId,
	   LiferayPortletResponse liferayPortletResponse,
	   WindowState windowState) throws Exception {
	    return "/html/lti/view.jsp";
	  
	 }
}