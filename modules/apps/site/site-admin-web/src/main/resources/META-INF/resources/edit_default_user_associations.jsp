<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
Group siteGroup = themeDisplay.getSiteGroup();

Group liveGroup = null;

if (siteGroup.isStagingGroup()) {
	liveGroup = siteGroup.getLiveGroup();
}
else {
	liveGroup = siteGroup;
}

long groupId = scopeGroupId;

UnicodeProperties groupTypeSettings = null;

if (liveGroup != null) {
	groupId = liveGroup.getGroupId();

	groupTypeSettings = liveGroup.getTypeSettingsProperties();
}
else {
	groupTypeSettings = new UnicodeProperties();
}

List<Role> defaultSiteRoles = new ArrayList<>();

long[] defaultSiteRoleIds = StringUtil.split(groupTypeSettings.getProperty("defaultSiteRoleIds"), 0L);

for (long defaultSiteRoleId : defaultSiteRoleIds) {
	defaultSiteRoles.add(RoleLocalServiceUtil.getRole(defaultSiteRoleId));
}

List<Team> defaultTeams = new ArrayList<>();

long[] defaultTeamIds = StringUtil.split(groupTypeSettings.getProperty("defaultTeamIds"), 0L);

for (long defaultTeamId : defaultTeamIds) {
	defaultTeams.add(TeamLocalServiceUtil.getTeam(defaultTeamId));
}
%>

<liferay-util:buffer
	var="removeRoleIcon"
>
	<liferay-ui:icon
		icon="times-circle"
		markupView="lexicon"
		message="remove"
	/>
</liferay-util:buffer>

<portlet:actionURL name="/site_admin/edit_default_user_associations" var="editDefaultUserAssociationsURL">
	<portlet:param name="mvcRenderCommandName" value="/configuration_admin/view_configuration_screen" />
	<portlet:param name="configurationScreenKey" value="site-configuration-default-user-associations" />
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= editDefaultUserAssociationsURL %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="liveGroupId" type="hidden" value="<%= liveGroup.getGroupId() %>" />

	<liferay-frontend:edit-form-body>
		<p class="text-muted">
			<liferay-ui:message key="select-the-default-roles-and-teams-for-new-members" />
		</p>

		<h4 class="text-default"><liferay-ui:message key="site-roles" /> <liferay-ui:icon-help message="default-site-roles-assignment-help" /></h4>

		<liferay-ui:search-container
			compactEmptyResultsMessage="<%= true %>"
			emptyResultsMessage="none"
			headerNames="title,null"
			id="siteRolesSearchContainer"
			total="<%= defaultSiteRoles.size() %>"
		>
			<liferay-ui:search-container-results
				results="<%= defaultSiteRoles %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.Role"
				keyProperty="roleId"
				modelVar="role"
			>
				<liferay-ui:search-container-column-text
					name="title"
					truncate="<%= true %>"
					value="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
				/>

				<liferay-ui:search-container-column-text>
					<a class="modify-link" data-rowId="<%= role.getRoleId() %>" href="javascript:;"><%= removeRoleIcon %></a>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				paginate="<%= false %>"
			/>
		</liferay-ui:search-container>

		<div class="button-holder form-group">
			<aui:button cssClass="modify-link" id="selectSiteRoleLink" value="select" />
		</div>

		<h4 class="text-default"><liferay-ui:message key="teams" /> <liferay-ui:icon-help message="default-teams-assignment-help" /></h4>

		<liferay-ui:search-container
			compactEmptyResultsMessage="<%= true %>"
			emptyResultsMessage="none"
			headerNames="title,null"
			id="teamsSearchContainer"
			total="<%= defaultTeams.size() %>"
		>
			<liferay-ui:search-container-results
				results="<%= defaultTeams %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.Team"
				keyProperty="teamId"
				modelVar="team"
			>
				<liferay-ui:search-container-column-text
					name="title"
					truncate="<%= true %>"
					value="<%= HtmlUtil.escape(team.getName()) %>"
				/>

				<liferay-ui:search-container-column-text>
					<a class="modify-link" data-rowId="<%= team.getTeamId() %>" href="javascript:;"><%= removeRoleIcon %></a>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				paginate="<%= false %>"
			/>
		</liferay-ui:search-container>

		<div class="button-holder">
			<aui:button cssClass="modify-link" id="selectTeamLink" value="select" />
		</div>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button href='<%= ParamUtil.getString(request, "redirect") %>' type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script use="escape,liferay-search-container">
	var bindModifyLink = function (config) {
		var searchContainer = config.searchContainer;

		searchContainer.get('contentBox').delegate(
			'click',
			(event) => {
				var link = event.currentTarget;

				searchContainer.deleteRow(
					link.ancestor('tr'),
					link.getAttribute('data-rowId')
				);
			},
			'.modify-link'
		);
	};

	var bindSelectLink = function (config) {
		var searchContainer = config.searchContainer;

		A.one(config.linkId).on('click', (event) => {
			var searchContainerData = searchContainer.getData();

			if (!searchContainerData.length) {
				searchContainerData = [];
			}
			else {
				searchContainerData = searchContainerData.split(',');
			}

			var ids = A.one(config.inputId).val();

			var uri = Liferay.Util.addParams(
				config.urlParam + '=' + ids,
				config.uri
			);

			Liferay.Util.openSelectionModal({
				onSelect: function (event) {
					var entityId = event.entityid;

					var rowColumns = [
						A.Escape.html(event.entityname),
						'<a class="modify-link" data-rowId="' +
							entityId +
							'" href="javascript:;"><%= UnicodeFormatter.toString(removeRoleIcon) %></a>',
					];

					searchContainer.addRow(rowColumns, entityId);

					searchContainer.updateDataStore();
				},
				selectEventName: config.id,
				selectedData: searchContainerData,
				title: config.title,
				url: uri,
			});
		});
	};

	<%
	PortletURL selectSiteRoleURL = PortletURLBuilder.create(
		PortletProviderUtil.getPortletURL(request, Role.class.getName(), PortletProvider.Action.BROWSE)
	).setParameter(
		"eventName", liferayPortletResponse.getNamespace() + "selectSiteRole"
	).setParameter(
		"groupId", groupId
	).setParameter(
		"roleType", RoleConstants.TYPE_SITE
	).setParameter(
		"step", "2"
	).setWindowState(
		LiferayWindowState.POP_UP
	).build();

	String selectSiteRolePortletId = PortletProviderUtil.getPortletId(Role.class.getName(), PortletProvider.Action.BROWSE);
	%>

	var siteRolesConfig = {
		id: '<portlet:namespace />selectSiteRole',
		idAttr: 'roleid',
		inputId: '#<portlet:namespace />siteRolesSearchContainerPrimaryKeys',
		linkId: '#<portlet:namespace />selectSiteRoleLink',
		searchContainer: Liferay.SearchContainer.get(
			'<portlet:namespace />siteRolesSearchContainer'
		),
		title: '<liferay-ui:message arguments="site-role" key="select-x" />',
		titleAttr: 'roletitle',
		uri: '<%= selectSiteRoleURL.toString() %>',
		urlParam:
			'<%= PortalUtil.getPortletNamespace(selectSiteRolePortletId) %>roleIds',
	};

	bindModifyLink(siteRolesConfig);
	bindSelectLink(siteRolesConfig);

	<%
	PortletURL selectTeamURL = PortletURLBuilder.create(
		PortletProviderUtil.getPortletURL(request, Team.class.getName(), PortletProvider.Action.BROWSE)
	).setParameter(
		"eventName", liferayPortletResponse.getNamespace() + "selectTeam"
	).setParameter(
		"groupId", groupId
	).setWindowState(
		LiferayWindowState.POP_UP
	).build();

	String selectTeamPortletId = PortletProviderUtil.getPortletId(Team.class.getName(), PortletProvider.Action.BROWSE);
	%>

	var teamsConfig = {
		id: '<portlet:namespace />selectTeam',
		idAttr: 'teamid',
		inputId: '#<portlet:namespace />teamsSearchContainerPrimaryKeys',
		linkId: '#<portlet:namespace />selectTeamLink',
		searchContainer: Liferay.SearchContainer.get(
			'<portlet:namespace />teamsSearchContainer'
		),
		title: '<liferay-ui:message arguments="team" key="select-x" />',
		titleAttr: 'teamname',
		uri: '<%= selectTeamURL.toString() %>',
		urlParam:
			'<%= PortalUtil.getPortletNamespace(selectTeamPortletId) %>teamIds',
	};

	bindModifyLink(teamsConfig);
	bindSelectLink(teamsConfig);
</aui:script>