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

package com.liferay.site.teams.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.sitesadmin.search.UserTeamChecker;
import com.liferay.portlet.usersadmin.search.UserSearch;
import com.liferay.portlet.usersadmin.search.UserSearchTerms;
import com.liferay.site.teams.web.internal.constants.SiteTeamsPortletKeys;
import com.liferay.users.admin.kernel.util.UsersAdminUtil;

import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SelectUsersDisplayContext {

	public SelectUsersDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, SiteTeamsPortletKeys.SITE_TEAMS,
			"users-display-style", "icon");

		return _displayStyle;
	}

	public String getEventName() {
		if (_eventName != null) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_httpServletRequest, "eventName",
			_renderResponse.getNamespace() + "selectUser");

		return _eventName;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "first-name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/select_users.jsp"
		).setRedirect(
			getRedirect()
		).setParameter(
			"eventName", getEventName()
		).setParameter(
			"teamId", getTeamId()
		).build();

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public Team getTeam() {
		if (_team != null) {
			return _team;
		}

		_team = TeamLocalServiceUtil.fetchTeam(getTeamId());

		return _team;
	}

	public long getTeamId() {
		if (_teamId != null) {
			return _teamId;
		}

		_teamId = ParamUtil.getLong(_httpServletRequest, "teamId");

		return _teamId;
	}

	public SearchContainer<User> getUserSearchContainer() {
		if (_userSearchContainer != null) {
			return _userSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<User> userSearchContainer = new UserSearch(
			_renderRequest, getPortletURL());

		OrderByComparator<User> orderByComparator =
			UsersAdminUtil.getUserOrderByComparator(
				getOrderByCol(), getOrderByType());

		userSearchContainer.setOrderByCol(getOrderByCol());
		userSearchContainer.setOrderByComparator(orderByComparator);
		userSearchContainer.setOrderByType(getOrderByType());

		Team team = getTeam();

		userSearchContainer.setRowChecker(
			new UserTeamChecker(_renderResponse, team));

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearchContainer.getSearchTerms();

		LinkedHashMap<String, Object> userParams =
			LinkedHashMapBuilder.<String, Object>put(
				"inherit", Boolean.TRUE
			).put(
				"usersGroups",
				() -> {
					Group group = GroupLocalServiceUtil.fetchGroup(
						team.getGroupId());

					if (group != null) {
						group = StagingUtil.getLiveGroup(group.getGroupId());
					}

					return group.getGroupId();
				}
			).build();

		int usersCount = UserLocalServiceUtil.searchCount(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			searchTerms.getStatus(), userParams);

		userSearchContainer.setTotal(usersCount);

		List<User> users = UserLocalServiceUtil.search(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			searchTerms.getStatus(), userParams, userSearchContainer.getStart(),
			userSearchContainer.getEnd(),
			userSearchContainer.getOrderByComparator());

		userSearchContainer.setResults(users);

		_userSearchContainer = userSearchContainer;

		return _userSearchContainer;
	}

	private String _displayStyle;
	private String _eventName;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Team _team;
	private Long _teamId;
	private SearchContainer<User> _userSearchContainer;

}