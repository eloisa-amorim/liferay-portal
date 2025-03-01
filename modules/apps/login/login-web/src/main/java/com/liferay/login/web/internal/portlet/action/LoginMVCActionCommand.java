/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.login.web.internal.portlet.action;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CookieNotSupportedException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PasswordExpiredException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserIdException;
import com.liferay.portal.kernel.exception.UserLockoutException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManager;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Peter Fellwock
 */
@Component(
	property = {
		"javax.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/login"
	},
	service = MVCActionCommand.class
)
public class LoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (PropsValues.AUTH_LOGIN_DISABLED) {
			actionResponse.sendRedirect(
				themeDisplay.getPathMain() +
					PropsValues.AUTH_LOGIN_DISABLED_PATH);

			return;
		}

		/*if (actionRequest.getRemoteUser() != null) {
			actionResponse.sendRedirect(themeDisplay.getPathMain());

			return;
		}*/

		try {
			login(themeDisplay, actionRequest, actionResponse);

			boolean doActionAfterLogin = ParamUtil.getBoolean(
				actionRequest, "doActionAfterLogin");

			if (doActionAfterLogin) {
				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					PortletURLBuilder.createRenderURL(
						_portal.getLiferayPortletResponse(actionResponse)
					).setMVCRenderCommandName(
						"/login/login_redirect"
					).buildString());
			}
		}
		catch (Exception exception) {
			if (exception instanceof AuthException) {
				Throwable throwable = exception.getCause();

				if (throwable instanceof PasswordExpiredException ||
					throwable instanceof UserLockoutException) {

					SessionErrors.add(
						actionRequest, throwable.getClass(), throwable);
				}
				else {
					if (_log.isInfoEnabled()) {
						_log.info("Authentication failed");
					}

					SessionErrors.add(actionRequest, exception.getClass());
				}
			}
			else if (exception instanceof CompanyMaxUsersException ||
					 exception instanceof CookieNotSupportedException ||
					 exception instanceof NoSuchUserException ||
					 exception instanceof PasswordExpiredException ||
					 exception instanceof UserEmailAddressException ||
					 exception instanceof UserIdException ||
					 exception instanceof UserLockoutException ||
					 exception instanceof UserPasswordException ||
					 exception instanceof UserScreenNameException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else {
				_log.error(exception, exception);

				_portal.sendError(exception, actionRequest, actionResponse);

				return;
			}

			postProcessAuthFailure(actionRequest, actionResponse);

			hideDefaultErrorMessage(actionRequest);
		}
	}

	protected String getCompleteRedirectURL(
		HttpServletRequest httpServletRequest, String redirect) {

		HttpSession session = httpServletRequest.getSession();

		Boolean httpsInitial = (Boolean)session.getAttribute(
			WebKeys.HTTPS_INITIAL);

		String portalURL = null;

		if (PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS &&
			!PropsValues.SESSION_ENABLE_PHISHING_PROTECTION &&
			(httpsInitial != null) && !httpsInitial.booleanValue()) {

			portalURL = _portal.getPortalURL(httpServletRequest, false);
		}
		else {
			portalURL = _portal.getPortalURL(httpServletRequest);
		}

		return portalURL.concat(redirect);
	}

	protected void login(
			ThemeDisplay themeDisplay, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		if (!themeDisplay.isSignedIn()) {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			String login = ParamUtil.getString(actionRequest, "login");
			String password = actionRequest.getParameter("password");
			boolean rememberMe = ParamUtil.getBoolean(
				actionRequest, "rememberMe");

			PortletPreferences portletPreferences =
				PortletPreferencesFactoryUtil.getStrictPortletSetup(
					themeDisplay.getLayout(),
					_portal.getPortletId(actionRequest));

			String authType = portletPreferences.getValue("authType", null);

			_authenticatedSessionManager.login(
				httpServletRequest, httpServletResponse, login, password,
				rememberMe, authType);
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		String mainPath = themeDisplay.getPathMain();

		if (PropsValues.PORTAL_JAAS_ENABLE) {
			if (Validator.isNotNull(redirect)) {
				redirect = StringBundler.concat(
					mainPath, "/portal/protected?redirect=",
					URLCodec.encodeURL(redirect));
			}
			else {
				redirect = mainPath.concat("/portal/protected");
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.sendRedirect(redirect);

			return;
		}

		if (Validator.isNotNull(redirect)) {
			if (!themeDisplay.isSignedIn()) {
				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					_http.addParameter(
						_portal.getPathMain() + "/portal/login", "redirect",
						redirect));

				return;
			}

			redirect = _portal.escapeRedirect(redirect);

			if (Validator.isNotNull(redirect) &&
				!redirect.startsWith(Http.HTTP)) {

				redirect = getCompleteRedirectURL(httpServletRequest, redirect);
			}
		}

		if (Validator.isNotNull(redirect)) {
			actionResponse.sendRedirect(redirect);
		}
		else {
			boolean doActionAfterLogin = ParamUtil.getBoolean(
				actionRequest, "doActionAfterLogin");

			if (doActionAfterLogin) {
				return;
			}

			actionResponse.sendRedirect(mainPath);
		}
	}

	protected void postProcessAuthFailure(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(actionRequest);

		String portletName = liferayPortletRequest.getPortletName();

		Layout layout = (Layout)actionRequest.getAttribute(WebKeys.LAYOUT);

		PortletURL portletURL = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				actionRequest, liferayPortletRequest.getPortlet(), layout,
				PortletRequest.RENDER_PHASE)
		).setParameter(
			"saveLastPath", Boolean.FALSE.toString()
		).build();

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String login = ParamUtil.getString(actionRequest, "login");

		if (Validator.isNotNull(login)) {
			SessionErrors.add(actionRequest, "login", login);
		}

		if (portletName.equals(LoginPortletKeys.LOGIN)) {
			portletURL.setWindowState(WindowState.MAXIMIZED);
		}
		else {
			portletURL.setWindowState(actionRequest.getWindowState());
		}

		actionResponse.sendRedirect(portletURL.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoginMVCActionCommand.class);

	@Reference
	private AuthenticatedSessionManager _authenticatedSessionManager;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

}