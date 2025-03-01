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

package com.liferay.site.admin.web.internal.portal.settings.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.map.constants.MapProviderWebKeys;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class MapsSiteSettingsConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "third-party-applications";
	}

	@Override
	public String getKey() {
		return "site-configuration-maps";
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(locale, "maps");
	}

	@Override
	public String getScope() {
		return ExtendedObjectClassDefinition.Scope.GROUP.getValue();
	}

	@Override
	public boolean isVisible() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		Group siteGroup = themeDisplay.getSiteGroup();

		if (siteGroup == null) {
			return false;
		}

		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group siteGroup = themeDisplay.getSiteGroup();

			Group liveGroup = null;

			if (siteGroup.isStagingGroup()) {
				liveGroup = siteGroup.getLiveGroup();
			}
			else {
				liveGroup = siteGroup;
			}

			httpServletRequest.setAttribute(
				MapProviderWebKeys.MAP_PROVIDER_KEY,
				MapProviderHelperUtil.getMapProviderKey(
					_groupLocalService, themeDisplay.getCompanyId(),
					liveGroup.getGroupId()));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/edit_maps.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException("Unable to render edit_maps.jsp", exception);
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

}