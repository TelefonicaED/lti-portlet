<%@ include file="/html/init.jsp" %>

<c:if test="${not empty learningActivity}">
	<h2 id="h2" class="description-title">${learningActivity.getTitle(themeDisplay.locale)}</h2>
	<div id="resourcedescription">${learningActivity.getDescription(themeDisplay.locale)}</div>
	<c:choose>
		<c:when test="${times >= learningActivity.tries  || learningActivity.tries==0}">
			<div class="ltistate"><liferay-ui:message key="learningactivity.lti.blocked" /></div>
			<c:if test="${not empty result}">
				<c:choose>				
					<c:when test="${result.passed}">
						<div class="ltistatepass"><liferay-ui:message key="learningactivity.lti.passed" /> ${result.result}/100</div>
					</c:when>
					<c:otherwise>
						<div class="ltistatenopass"><liferay-ui:message key="learningactivity.lti.notpassed" /> ${result.result}/100</div>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:choose>				
				<c:when test="${not empty result}">
					<c:choose>
						<c:when test="${result.passed}">
							<div class="ltistatepass"><liferay-ui:message key="learningactivity.lti.passed" /> ${result.result}/100</div>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${ltiItem.iframe}">
									${postLaunch}</br>
									<div id="fcontetn" style="height: 670px; width: 99%;">
										<a name="content"></a>
							          	<iframe id="output_frame" style="display:none;height:100%; width:100%" name="output_frame"><liferay-ui:message key="learningactivity.lti.noframes" /></iframe>
							    	</div>
								</c:when>
								<c:otherwise>
									${postLaunch}
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${ltiItem.iframe}">
							${postLaunch}</br>
							<div id="fcontetn" style="height: 670px; width: 99%;">
								<a name="content"></a>
					          	<iframe id="output_frame" style="display:none;height:100%; width:100%" name="output_frame"><liferay-ui:message key="learningactivity.lti.noframes" /></iframe>
					    	</div>
						</c:when>
						<c:otherwise>
							${postLaunch}
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</c:if>