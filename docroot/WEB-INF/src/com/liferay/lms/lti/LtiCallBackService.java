package com.liferay.lms.lti;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.liferay.lms.lti.util.LtiItem;
import com.liferay.lms.lti.util.LtiItemLocalServiceUtil;
import com.liferay.lms.model.LearningActivity;
import com.liferay.lms.model.LearningActivityResult;
import com.liferay.lms.model.LearningActivityTry;
import com.liferay.lms.service.LearningActivityLocalServiceUtil;
import com.liferay.lms.service.LearningActivityResultLocalServiceUtil;
import com.liferay.lms.service.LearningActivityTryLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.ServiceContext;
import com.tls.pox.IMSPOXRequest;

public class LtiCallBackService extends HttpServlet{
	private static final long serialVersionUID = 1231789235690235235L;
	Log log = LogFactoryUtil.getLog(LtiCallBackService.class);
	
	private static final String SUCCESS = "success";
	private static final String FAILURE = "failure";
	private static final String P1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<imsx_POXEnvelopeResponse xmlns = \"http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0\">"+
		"<imsx_POXHeader>"+
		"<imsx_POXResponseHeaderInfo>"+
		"<imsx_version>V1.0</imsx_version>"+
		"<imsx_messageIdentifier>";
	private static final String P2 = "</imsx_messageIdentifier>"+
		"<imsx_statusInfo>"+
		"<imsx_codeMajor>";//success - failure 
		private static final String P3 = "</imsx_codeMajor>"+
		"<imsx_severity>status</imsx_severity>"+
		"<imsx_description>TLS Response</imsx_description>"+
		"<imsx_messageRefIdentifier>";
	private static final String P4 = "</imsx_messageRefIdentifier>"+
		"<imsx_operationRefIdentifier>";
			
	private static final String P5 = "</imsx_operationRefIdentifier>"+
		"</imsx_statusInfo>"+
		"</imsx_POXResponseHeaderInfo>"+
		"</imsx_POXHeader>"+
		"<imsx_POXBody>"+
		"<replaceResultResponse/>"+
		"</imsx_POXBody>"+
		"</imsx_POXEnvelopeResponse>";
	private static final String P6 = "</imsx_operationRefIdentifier>"+
			"</imsx_statusInfo>"+
			"</imsx_POXResponseHeaderInfo>"+
			"</imsx_POXHeader>"+
			"<imsx_POXBody>"+
			"<readResultResponse><result>"+
			"<resultScore>"+
            "<language>en</language>"+
            "<textString>";
	private static final String P7 = "</textString>"+
       " </resultScore>"+
       "</result>"+
       "</readResultResponse>"+
			"</imsx_POXBody>"+
			"</imsx_POXEnvelopeResponse>";
	private static final String P8 = "</imsx_operationRefIdentifier>"+
			"</imsx_statusInfo>"+
			"</imsx_POXResponseHeaderInfo>"+
			"</imsx_POXHeader>"+
			"<imsx_POXBody>"+
			"<replaceResultResponse/>"+
			"</imsx_POXBody>"+
			"</imsx_POXEnvelopeResponse>";

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doGet(request, response);
	}
		
	//TODO OAuth is not verified 
	public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException {	
		try{
			log.debug("-------------SERVLET DO GET--------------------------------   ");
			ServletInputStream input = request.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(input, writer, "UTF-8");
			String xml = writer.toString();
			if(log.isDebugEnabled())log.debug(xml);
	
			IMSPOXRequest imspoxRequest = new IMSPOXRequest(xml);
			
			String msg = request.getParameter("lti_msg");

			log.debug("-- MESSAGE RECIEVED: "+msg);
			
			String error = request.getParameter("lti_errormsg");

			log.debug("-- ERROR RECIEVED: "+error);
			
			imspoxRequest.parsePostBody();
			Map<String,String> results = imspoxRequest.getBodyMap();
			String grade = results.get("/resultRecord/result/resultScore/textString");
			String id = results.get("/resultRecord/sourcedGUID/sourcedId");
			String identifier = imspoxRequest.getHeaderMessageIdentifier();

			String[] ids = id.split("-");
			
			Long latId = null;
			Long userId = null;
			
			if(ids.length>1){
				latId = Long.parseLong(ids[0]);
				userId = Long.parseLong(ids[1]);
			}
			
			LearningActivityTry lat = LearningActivityTryLocalServiceUtil.fetchLearningActivityTry(latId);
			
			if(log.isDebugEnabled()){
				for(String key:results.keySet()){
					log.debug("-->"+key+": "+results.get(key));
				}
			}
			if(imspoxRequest.bodyElement.getNodeName().equals("readResultRequest"))
			{
				StringBuffer out = new StringBuffer(P1);
				out.append(identifier);
				out.append(P2);
				if(lat!=null)
				{
					grade="0.0";
					if(LearningActivityResultLocalServiceUtil.existsLearningActivityResult(lat.getActId(), userId))
					{
						LearningActivityResult learningActivityResult = LearningActivityResultLocalServiceUtil.getByActIdAndUserId(lat.getActId(), userId);
						 grade=new Double((new Double(learningActivityResult.getResult())/100.0)).toString();
						 
					}
					out.append(SUCCESS);
					out.append(P3);
					out.append(identifier);
					out.append(P4);
					out.append(new Date().getTime());
					out.append(P6);
					out.append(grade);
					out.append(P7);
					
				}
				else
				{
					out.append(FAILURE);
					out.append(P3);
					out.append(identifier);
					out.append(P4);
					out.append(new Date().getTime());
					out.append(P5);
				}
				response.setContentType("application/x-www-form-urlencoded");
				PrintWriter res = response.getWriter();
				res.print(out.toString());
				res.flush();
				res.close();
				
			}
			if(imspoxRequest.bodyElement.getNodeName().equals("deleteResultRequest"))
			{
				StringBuffer out = new StringBuffer(P1);
				out.append(identifier);
				out.append(P2);
				out.append(FAILURE);
				out.append(P3);
				out.append(identifier);
				out.append(P4);
				out.append(new Date().getTime());
				out.append(P8);
				response.setContentType("application/x-www-form-urlencoded");
				PrintWriter res = response.getWriter();
				res.print(out.toString());
				res.flush();
				res.close();
			}
			if(imspoxRequest.bodyElement.getNodeName().equals("replaceResultRequest")){
				Long lGrade = new Double((Double.valueOf(grade)*100)).longValue();
				if(lGrade>100){
					lGrade=100L;
				}
				log.debug("LGRADE:  "+lGrade);
				StringBuffer out = new StringBuffer(P1);
				out.append(identifier);
				out.append(P2);
	
				if(lat!=null){
					LearningActivity la = LearningActivityLocalServiceUtil.getLearningActivity(lat.getActId());
					try{
						ServiceContext serviceContext =  new ServiceContext();
						serviceContext.setUserId(userId);
						serviceContext.setCompanyId(la.getCompanyId());
						serviceContext.setScopeGroupId(la.getGroupId());
						lat.setEndDate(new Date());
						lat.setResult(lGrade);
						if(log.isDebugEnabled())log.debug("Add!learningTry");
						LearningActivityTryLocalServiceUtil.updateLearningActivityTry(lat);
							
						out.append(SUCCESS);
					}catch(Exception e){
						if(log.isDebugEnabled())e.printStackTrace();
						if(log.isErrorEnabled())log.error(e.getMessage());
						out.append(FAILURE);
					}
				}else{
					out.append(FAILURE);
				}	
				
				out.append(P3);
				out.append(identifier);
				out.append(P4);
				out.append(new Date().getTime());
				out.append(P5);
	
				if(log.isDebugEnabled())log.debug(out.toString());
				
				response.setContentType("application/x-www-form-urlencoded");
				PrintWriter res = response.getWriter();
				res.print(out.toString());
				res.flush();
				res.close();
			}
			
		}catch(Exception e){
			if(log.isDebugEnabled())e.printStackTrace();
			if(log.isErrorEnabled())log.error(e.getMessage());
		}
	}

}
