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

AUI.add(
	'liferay-contacts-center',
	(A) => {
		var AArray = A.Array;

		var Lang = A.Lang;

		var Node = A.Node;

		var ParseContent = A.Plugin.ParseContent;

		var CSS_NOT_SELECTED_CHECKBOX =
			'input[type="checkbox"]:not(:disabled):not(:checked)';

		var CSS_SELECTED_CHECKBOX =
			'input[type="checkbox"]:not(:disabled):checked';

		var STR_UNDEFINED = 'undefined';

		var TPL_BLOCK_IMG =
			'<span>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('blocked') +
			'</span>' +
			'</span>';

		var TPL_CONNECTED_IMG =
			'<span>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('connected') +
			'</span>' +
			'</span>';

		var TPL_CONNECTION_REQUESTED_IMG =
			'<span>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('connection-requested') +
			'</span>' +
			'</span>';

		var TPL_ENTRY_DATA =
			'{lastNameAnchor}' +
			'<div class="lfr-contact">' +
			'<div class="lfr-contact-checkbox">' +
			'<input type="checkbox" value="{entryId}" disabled="true" name="contact-ids-{entryId}" class="contact-ids" label="" />' +
			'</div>' +
			'<div class="lfr-contact-grid-item" data-viewSummaryURL="{viewSummaryURL}" data-contactId="{entryId}">' +
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>' +
			'<div class="lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'<a>{fullName}</a>' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'{emailAddress}' +
			'</div>' +
			'</div>' +
			'<div class="clear"></div>' +
			'</div>' +
			'</div>';

		var TPL_ENTRY_DETAIL_DATA =
			'<div class="contacts-profile external-contact">' +
			'<div class="lfr-detail-info">' +
			'{icon}' +
			'<div class="{cssClass} lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'{fullName}' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'<a href="mailto:{emailAddress}">{emailAddress}</a>' +
			'</div>' +
			'</div>' +
			'</div>' +
			'<div class="lfr-detail-info">' +
			'<div class="comments">' +
			'{comments}' +
			'</div>' +
			'</div>' +
			'</div>';

		var TPL_FOLLOWING_IMG =
			'<span>' +
			'<i class="icon-user"></i>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('following') +
			'</span>' +
			'</span>';

		var TPL_ICON =
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>';

		var TPL_NO_RESULTS =
			'<div class="empty">' +
			Liferay.Language.get('there-are-no-results') +
			'</div>';

		var TPL_USER_DATA =
			'{lastNameAnchor}' +
			'<div class="lfr-contact">' +
			'<div class="lfr-contact-checkbox">' +
			'<input type="checkbox" value="{userId}" {disabled} {checked} name="contact-ids-{userId}" class="contact-ids" label="" />' +
			'</div>' +
			'<div class="lfr-contact-grid-item" data-viewSummaryURL="{viewSummaryURL}" data-userId="{userId}">' +
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>' +
			'<div class="lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'<a>{lastName} {firstName}</a>' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'{emailAddress}' +
			'</div>' +
			'</div>' +
			'<div class="clear"></div>' +
			'</div>' +
			'</div>';

		var TPL_USER_DETAIL_DATA =
			'<div class="lfr-contact-grid-item lfr-selected-user {cssClass}" data-viewSummaryURL="{viewSummaryURL}" id="user-{userId}">' +
			'<input type="hidden" value="{userId}" name="selected-user-{userId}" />' +
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>' +
			'<div class="lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'<a>{lastName}, {firstName}</a>' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'{emailAddress}' +
			'</div>' +
			'<div class="lfr-social-relations">' +
			'{connectionRequested}' +
			'{connected}' +
			'{following}' +
			'{block}' +
			'</div>' +
			'</div>' +
			'<div class="clear"></div>' +
			'</div>';

		var ContactsResult = A.Base.create(
			'contactsResult',
			A.Base,
			[A.AutoCompleteBase],
			{
				initializer(config) {
					this._listNode = A.one(config.listNode);

					this._bindUIACBase();
					this._syncUIACBase();
				},
			}
		);

		var ContactsCenter = A.Component.create({
			AUGMENTS: [Liferay.PortletBase],

			EXTENDS: A.Base,

			NAME: 'contactscenter',

			prototype: {
				_clearContactResult() {
					var instance = this;

					instance._detailUserView.empty();

					instance._selectedUsersView.empty();

					instance._messageContainer.empty();

					A.all('.lfr-contact .lfr-contact-checkbox input').set(
						'checked',
						false
					);

					instance._buttonAddConnectionUserIds.length = 0;
					instance._buttonBlockUserIds.length = 0;
					instance._buttonExportUserIds.length = 0;
					instance._buttonFollowUserIds.length = 0;
					instance._buttonRemoveConnectionUserIds.length = 0;
					instance._buttonUnBlockUserIds.length = 0;
					instance._buttonUnFollowUserIds.length = 0;

					A.all('.contacts-container .toolbar button').hide();

					if (instance._sendMessageButton) {
						instance._showButton(instance._sendMessageButton);
					}

					instance._contactCenterToolbar.hide();

					instance._numSelectedContacts = 0;

					instance._checkAll.set('checked', false);
				},

				_createContactList(config) {
					var instance = this;

					var contactsResult = config.contactsResult;
					var contactsResultURL = config.contactsResultURL;
					var contactsSearchInput = config.contactsSearchInput;

					var contactList = new ContactsResult({
						inputNode: contactsSearchInput,
						listNode: contactsResult,
						minQueryLength: 0,
						requestTemplate(query) {
							var data = {};

							data[instance._namespace + 'keywords'] = query;

							return data;
						},
						resultTextLocator(response) {
							var result = '';

							if (typeof response.toString != STR_UNDEFINED) {
								result = response.toString();
							}
							else if (
								typeof response.responseText != STR_UNDEFINED
							) {
								result = response.responseText;
							}

							return result;
						},
						source: instance._createDataSource(contactsResultURL),
					});

					contactList.on(
						'results',
						A.bind(instance._updateContactsResult, instance)
					);

					instance._contactList = contactList;
				},

				_createDataSource(url) {
					var instance = this;

					return new A.DataSource.IO({
						ioConfig: {
							method: 'POST',
						},
						on: {
							request(event) {
								var contactFilter = A.one(
									'#' + instance._namespace + 'filterBy'
								);

								var filterBy = '0';

								if (contactFilter) {
									filterBy = contactFilter.get('value');
								}

								var eventData = {};

								var data = event.request;

								eventData[instance._namespace + 'end'] =
									data[instance._namespace + 'end'] ||
									instance._maxResultCount;
								eventData[instance._namespace + 'filterBy'] =
									data[instance._namespace + 'filterBy'] ||
									filterBy;
								eventData[instance._namespace + 'keywords'] =
									data[instance._namespace + 'keywords'] ||
									'';
								eventData[instance._namespace + 'start'] =
									data[instance._namespace + 'start'] || 0;

								event.cfg.data = eventData;
							},
						},
						source: url,
					});
				},

				_deleteEntry(contact) {
					var instance = this;

					var config = instance._config;

					var confirmMessageText = Lang.sub(
						Liferay.Language.get(
							'are-you-sure-you-want-to-delete-x-from-your-contacts'
						),
						[contact.fullName]
					);

					if (confirm(confirmMessageText)) {
						var url = Liferay.Util.PortletURL.createActionURL(
							config.baseActionURL,
							{
								entryId: contact.entryId,
								'javax.portlet.action': 'deleteEntry',
								p_p_state: 'NORMAL',
							}
						);

						Liferay.Util.fetch(url)
							.then((response) => {
								return response.text();
							})
							.then(() => {
								location.href = contact.redirect;
							})
							.catch(() => {
								instance.showMessage(false);
							});
					}
				},

				_editEntry(contact) {
					var instance = this;

					var config = instance._config;

					var url = Liferay.Util.PortletURL.createRenderURL(
						config.baseRenderURL,
						{
							entryId: contact.entryId,
							mvcPath: '/contacts_center/edit_entry.jsp',
							p_p_state: 'EXCLUSIVE',
							redirect: contact.redirect,
						}
					);

					instance.showPopup(
						Liferay.Language.get('update-contact'),
						url.toString()
					);
				},

				_getPopup() {
					var instance = this;

					if (!instance._popup) {
						var contactsPortlet = A.one('.contacts-portlet');

						instance._popup = Liferay.Util.Window.getWindow({
							dialog: {
								align: {
									node: contactsPortlet,
									points: ['tc', 'tc'],
								},
								constrain2view: true,
								cssClass: 'contact-dialog',
								modal: true,
								resizable: false,
								width: 500,
							},
						})
							.plug(A.Plugin.IO, {
								autoLoad: false,
							})
							.render();
					}
				},

				_getRequestTemplate(filterBy) {
					var instance = this;

					return function (query) {
						var data = {};

						data[instance._namespace + 'end'] =
							instance._maxResultCount;
						data[instance._namespace + 'filterBy'] = filterBy;
						data[instance._namespace + 'keywords'] = query;
						data[instance._namespace + 'start'] = 0;

						return data;
					};
				},

				_onCheckAll(event) {
					var instance = this;

					var config = instance._config;

					var checkBox = event.target;

					var contacts;

					if (checkBox.get('checked')) {
						contacts = instance._contactsCheckBox.all(
							CSS_NOT_SELECTED_CHECKBOX
						);

						if (contacts.size() > 0) {
							contacts.set('checked', true);

							var uri = config.getSelectedContactsURL;

							var userIds = contacts.val();

							uri =
								Liferay.Util.addParams(
									instance._namespace +
										'userIds=' +
										userIds.join(),
									uri
								) || uri;

							Liferay.Util.fetch(uri)
								.then((response) => {
									return response.json();
								})
								.then((data) => {
									instance.addContactResults(data);
								})
								.catch(() => {
									instance.showMessage(false);
								});
						}
					}
					else {
						contacts = instance._contactsCheckBox.all(
							CSS_SELECTED_CHECKBOX
						);

						if (contacts.size() > 0) {
							contacts.set('checked', false);

							instance.deleteContactResults(contacts.val());
						}
					}
				},

				_renderContact(data) {
					var instance = this;

					var user = data.user;

					return Lang.sub(TPL_USER_DETAIL_DATA, {
						block: user.block ? TPL_BLOCK_IMG : '',
						connected: user.connected ? TPL_CONNECTED_IMG : '',
						connectionRequested: user.connectionRequested
							? TPL_CONNECTION_REQUESTED_IMG
							: '',
						cssClass:
							instance._numSelectedContacts % 2 == 0 ? '' : 'alt',
						emailAddress: user.emailAddress,
						firstName: user.firstName,
						following: user.following ? TPL_FOLLOWING_IMG : '',
						fullName: user.fullName,
						lastName: user.lastName,
						portraitURL: user.portraitURL,
						userId: user.userId,
						viewSummaryURL: user.viewSummaryURL,
					});
				},

				_renderEntryDetailView(contact) {
					var instance = this;

					var icon = '';

					if (instance._showIcon) {
						icon = Lang.sub(TPL_ICON, {
							fullName: contact.fullName,
							portraitURL: contact.portraitURL,
						});
					}

					var contactSummary = Lang.sub(TPL_ENTRY_DETAIL_DATA, {
						comments: contact.comments,
						cssClass: instance._showIcon ? '' : 'no-icon',
						emailAddress: contact.emailAddress,
						fullName: contact.fullName,
						icon: instance._showIcon ? icon : '',
					});

					instance._detailUserView.setContent(contactSummary);
				},

				_renderEntryToolbar(contact) {
					var instance = this;

					instance._userToolbar.empty();

					instance._toolbar = new A.Toolbar({
						activeState: false,
						boundingBox: instance._userToolbar,
						children: [
							{
								icon: 'icon-edit',
								label: Liferay.Language.get('edit'),
								on: {
									click() {
										instance._editEntry(contact);
									},
								},
							},
							{
								icon: 'icon-remove',
								label: Liferay.Language.get('delete'),
								on: {
									click() {
										instance._deleteEntry(contact);
									},
								},
							},
						],
					}).render();
				},

				_renderResult(data, displayMessage, lastNameAnchor) {
					var count = data.count;
					var results = data.users;

					var options = data.options;

					var buffer = [];

					if (results.length == 0 && displayMessage) {
						buffer.push(TPL_NO_RESULTS);
					}
					else {
						var selectedUsersNodes = A.all(
							'.lfr-contact-grid-item input'
						);

						var selectedUsersIds = [];

						if (selectedUsersNodes.size() > 0) {
							selectedUsersIds = selectedUsersNodes.val();
						}

						buffer.push(
							results
								.map((result) => {
									var displayLastNameAnchor = false;

									var nameAnchor;

									var name;

									if (result.portalUser) {
										name = result.lastName;
									}
									else {
										name = result.fullName;
									}

									if (name) {
										nameAnchor = name
											.substring(0, 1)
											.toUpperCase();
									}
									else {
										nameAnchor = Liferay.Language.get(
											'no-last-name'
										);
									}

									if (nameAnchor != lastNameAnchor) {
										displayLastNameAnchor = true;

										lastNameAnchor = nameAnchor;
									}

									var str;

									if (result.portalUser) {
										str = Lang.sub(TPL_USER_DATA, {
											checked:
												selectedUsersIds.indexOf(
													result.userId
												) != -1
													? 'checked="true"'
													: '',
											disabled:
												themeDisplay.getUserId() ==
												result.userId
													? 'disabled="true"'
													: '',
											emailAddress: result.emailAddress
												? result.emailAddress
												: '',
											firstName: result.firstName
												? result.firstName
												: '',
											fullName: result.fullName
												? result.fullName
												: '',
											lastName: result.lastName
												? result.lastName + ','
												: '',
											lastNameAnchor: displayLastNameAnchor
												? '<div class="last-name-anchor"><a>' +
												  lastNameAnchor +
												  '</a></div>'
												: '',
											portraitURL: result.portraitURL
												? result.portraitURL
												: '',
											userId: result.userId,
											viewSummaryURL: result.viewSummaryURL
												? result.viewSummaryURL
												: '',
										});
									}
									else {
										str = Lang.sub(TPL_ENTRY_DATA, {
											emailAddress: result.emailAddress
												? result.emailAddress
												: '',
											entryId: result.entryId,
											fullName: result.fullName
												? result.fullName
												: '',
											lastNameAnchor: displayLastNameAnchor
												? '<div class="last-name-anchor"><a>' +
												  lastNameAnchor +
												  '</a></div>'
												: '',
											portraitURL: result.portraitURL
												? result.portraitURL
												: '',
											viewSummaryURL: result.viewSummaryURL
												? result.viewSummaryURL
												: '',
										});
									}

									return str;
								})
								.join('')
						);
					}

					if (count > options.end) {
						buffer.push(
							'<div class="more-results">' +
								'<a href="javascript:;" data-end="' +
								options.end +
								'" data-lastNameAnchor="' +
								lastNameAnchor +
								'">' +
								Liferay.Language.get('view-more') +
								' (' +
								(count - options.end) +
								')' +
								'</a>' +
								'</div>'
						);
					}

					return buffer;
				},

				_setVisibleDetailUserView() {
					var instance = this;

					instance._contactCenterToolbar.hide();

					instance._detailUserView.show();

					instance._selectedUsersView.hide();
				},

				_setVisibleSelectedUsersView() {
					var instance = this;

					instance._userToolbar.empty();

					instance._contactCenterToolbar.show();

					instance._detailUserView.hide();

					instance._selectedUsersView.show();
				},

				_showButton(node) {
					node.show();

					node.removeClass('hidden');
				},

				_updateContactsResult(event) {
					var instance = this;

					var data = JSON.parse(event.data.responseText);

					var buffer = instance._renderResult(data, true, ' ');

					event.currentTarget._listNode.html(buffer.join(''));

					instance._contactsCheckBox = A.one('.contacts-result');

					instance._checkAll.set(
						'checked',
						instance._contactsCheckBox
							.all(CSS_NOT_SELECTED_CHECKBOX)
							.size() <= 0
					);
				},

				_updateToolbarButtonsAdd(data) {
					var instance = this;

					var user = data.user;

					if (user.block) {
						if (instance._unblockButton) {
							instance._showButton(instance._unblockButton);

							instance._buttonUnBlockUserIds.push(user.userId);
						}
					}
					else {
						if (instance._blockButton) {
							instance._showButton(instance._blockButton);

							instance._buttonBlockUserIds.push(user.userId);
						}

						if (!user.connectionRequested) {
							if (user.connected) {
								if (instance._removeConnectionButton) {
									instance._showButton(
										instance._removeConnectionButton
									);

									instance._buttonRemoveConnectionUserIds.push(
										user.userId
									);
								}
							}
							else if (instance._addConnectionButton) {
								instance._showButton(
									instance._addConnectionButton
								);

								instance._buttonAddConnectionUserIds.push(
									user.userId
								);
							}
						}

						if (user.following) {
							if (instance._unfollowButton) {
								instance._showButton(instance._unfollowButton);

								instance._buttonUnFollowUserIds.push(
									user.userId
								);
							}
						}
						else if (instance._followButton) {
							instance._showButton(instance._followButton);

							instance._buttonFollowUserIds.push(user.userId);
						}

						if (instance._exportButton) {
							instance._showButton(instance._exportButton);

							instance._buttonExportUserIds.push(user.userId);
						}
					}
				},

				_updateToolbarButtonsRemove(userId) {
					var instance = this;

					var blockUserIdIndex = instance._buttonBlockUserIds.indexOf(
						userId
					);

					if (blockUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonBlockUserIds,
							blockUserIdIndex
						);

						if (
							instance._blockButton &&
							instance._buttonBlockUserIds.length <= 0
						) {
							instance._blockButton.hide();
						}
					}

					var unblockUserIdIndex = instance._buttonUnBlockUserIds.indexOf(
						userId
					);

					if (unblockUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonUnBlockUserIds,
							unblockUserIdIndex
						);

						if (
							instance._unblockButton &&
							instance._buttonUnBlockUserIds.length <= 0
						) {
							instance._unblockButton.hide();
						}
					}

					var addConnectionUserIdIndex = instance._buttonAddConnectionUserIds.indexOf(
						userId
					);

					if (addConnectionUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonAddConnectionUserIds,
							addConnectionUserIdIndex
						);

						if (
							instance._addConnectionButton &&
							instance._buttonAddConnectionUserIds.length <= 0
						) {
							instance._addConnectionButton.hide();
						}
					}

					var removeConnectionUserIdIndex = instance._buttonRemoveConnectionUserIds.indexOf(
						userId
					);

					if (removeConnectionUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonRemoveConnectionUserIds,
							removeConnectionUserIdIndex
						);

						if (
							instance._removeConnectionButton &&
							instance._buttonRemoveConnectionUserIds.length <= 0
						) {
							instance._removeConnectionButton.hide();
						}
					}

					var followUserIdIndex = instance._buttonFollowUserIds.indexOf(
						userId
					);

					if (followUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonFollowUserIds,
							followUserIdIndex
						);

						if (
							instance._followButton &&
							instance._buttonFollowUserIds.length <= 0
						) {
							instance._followButton.hide();
						}
					}

					var unFollowUserIdIndex = instance._buttonUnFollowUserIds.indexOf(
						userId
					);

					if (unFollowUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonUnFollowUserIds,
							unFollowUserIdIndex
						);

						if (
							instance._unfollowButton &&
							instance._buttonUnFollowUserIds.length <= 0
						) {
							instance._unfollowButton.hide();
						}
					}

					var exportUserIdIndex = instance._buttonExportUserIds.indexOf(
						userId
					);

					if (exportUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonExportUserIds,
							exportUserIdIndex
						);

						if (
							instance._exportButton &&
							instance._buttonExportUserIds.length <= 0
						) {
							instance._exportButton.hide();
						}
					}
				},

				_updateUserIcons(user) {
					var contactsAction = A.one('.contacts-action');

					contactsAction.hide();

					var blockIcon = contactsAction.one('.block');
					var connectedIcon = contactsAction.one('.connected');
					var disabledIcon = contactsAction.one('.disabled');
					var followingIcon = contactsAction.one('.following');

					if (user.block) {
						blockIcon.show();
						connectedIcon.hide();
						disabledIcon.hide();
						followingIcon.hide();
					}
					else {
						blockIcon.hide();

						if (user.connectionRequested) {
							connectedIcon.hide();
							disabledIcon.show();
						}
						else if (user.connected) {
							connectedIcon.show();
							disabledIcon.hide();
						}
						else {
							connectedIcon.hide();
							disabledIcon.hide();
						}

						if (user.following) {
							followingIcon.show();
						}
						else {
							followingIcon.hide();
						}
					}

					contactsAction.show();
				},

				_updateUserToolBar(user) {
					var instance = this;

					instance._userToolbar.hide();

					var addConnectionButton = instance.byId(
						'addConnectionButton'
					);
					var blockButton = instance.byId('blockButton');
					var followButton = instance.byId('followButton');
					var removeConnectionButton = instance.byId(
						'removeConnectionButton'
					);
					var unblockButton = instance.byId('unblockButton');
					var unfollowButton = instance.byId('unfollowButton');

					if (user.block) {
						addConnectionButton.hide();
						blockButton.hide();
						followButton.hide();
						removeConnectionButton.hide();
						instance._showButton(unblockButton);
						unfollowButton.hide();
					}
					else {
						instance._showButton(blockButton);
						unblockButton.hide();

						if (user.connectionRequested) {
							addConnectionButton.hide();
							removeConnectionButton.hide();
						}
						else if (user.connected) {
							addConnectionButton.hide();
							instance._showButton(removeConnectionButton);
						}
						else {
							instance._showButton(addConnectionButton);
							removeConnectionButton.hide();
						}

						if (user.following) {
							followButton.hide();
							instance._showButton(unfollowButton);
						}
						else {
							instance._showButton(followButton);
							unfollowButton.hide();
						}
					}

					instance._userToolbar.show();
				},

				addContactResult(data) {
					var instance = this;

					instance._setVisibleSelectedUsersView();

					var contact = instance._renderContact(data);

					if (instance._numSelectedContacts <= 0) {
						instance._selectedUsersView.html(contact);

						instance._contactCenterToolbar.show();

						instance._userToolbar.empty();
					}
					else {
						instance._selectedUsersView.append(contact);
					}

					instance._numSelectedContacts++;

					instance._updateToolbarButtonsAdd(data);

					instance._messageContainer.empty();

					instance._checkAll.set(
						'checked',
						instance._contactsCheckBox
							.all(CSS_NOT_SELECTED_CHECKBOX)
							.size() <= 0
					);
				},

				addContactResults(data) {
					var instance = this;

					var contacts = data.contacts;

					contacts.map((contact) => {
						instance.addContactResult(contact);
					});
				},

				closePopup() {
					var instance = this;

					if (instance._popup) {
						instance._popup.hide();
					}
				},

				deleteContactResult(userId) {
					var instance = this;

					instance._setVisibleSelectedUsersView();

					var user = A.one('#user-' + userId);

					if (user) {
						user.remove(true);

						instance._numSelectedContacts--;

						instance._updateToolbarButtonsRemove(userId);

						if (instance._numSelectedContacts <= 0) {
							instance._clearContactResult();
						}

						instance._messageContainer.empty();

						instance._checkAll.set('checked', false);
					}
				},

				deleteContactResults(userIds) {
					var instance = this;

					userIds.map((userId) => {
						instance.deleteContactResult(userId);
					});
				},

				initializer(config) {
					var instance = this;

					instance._config = config;

					instance._namespace = config.namespace;

					instance._contactCenterToolbar = instance.byId(
						'contactCenterToolbarButtons'
					);
					instance._userToolbar = instance.byId('userToolbarButtons');

					instance._detailUserView = instance.byId('detailUserView');
					instance._selectedUsersView = instance.byId(
						'selectedUsersView'
					);

					instance._messageContainer = instance.byId(
						'messageContainer'
					);

					instance._addConnectionButton = instance.byId(
						'addConnectionButton'
					);
					instance._blockButton = instance.byId('blockButton');
					instance._exportButton = instance.byId('exportButton');
					instance._followButton = instance.byId('followButton');
					instance._removeConnectionButton = instance.byId(
						'removeConnectionButton'
					);
					instance._sendMessageButton = instance.byId(
						'sendMessageButton'
					);
					instance._unblockButton = instance.byId('unblockButton');
					instance._unfollowButton = instance.byId('unfollowButton');

					instance._contactsCheckBox = A.one('.contacts-result');

					instance._checkAll = instance.byId('checkAll');

					instance._checkAll.on(
						'click',
						instance._onCheckAll,
						instance
					);

					instance._buttonAddConnectionUserIds = [];
					instance._buttonBlockUserIds = [];
					instance._buttonExportUserIds = [];
					instance._buttonFollowUserIds = [];
					instance._buttonRemoveConnectionUserIds = [];
					instance._buttonUnBlockUserIds = [];
					instance._buttonUnFollowUserIds = [];

					instance._numSelectedContacts = 0;

					instance._createContactList(config);

					instance._defaultMessageError = config.defaultMessageError;
					instance._defaultMessageSuccess =
						config.defaultMessageSuccess;

					instance._maxResultCount = config.maxResultCount;

					instance._showIcon = config.showIcon;
				},

				renderContent(data, clear) {
					var instance = this;

					if (clear) {
						instance._clearContactResult();
					}

					instance._setVisibleDetailUserView();

					var content = Node.create(data);

					var contactSummary = instance.one(
						'#contactSummary',
						content
					);

					if (contactSummary) {
						instance._detailUserView.empty();

						instance._detailUserView.plug(ParseContent);

						instance._detailUserView.setContent(contactSummary);
					}

					var userToolbar = instance.one('#contactsToolbar', content);

					if (userToolbar) {
						instance._userToolbar.empty();

						instance._userToolbar.plug(ParseContent);

						instance._userToolbar.setContent(userToolbar);
					}
				},

				renderEntry(data, lastNameAnchor) {
					var instance = this;

					var contact = data.contact;

					instance._renderEntryDetailView(contact);
					instance._renderEntryToolbar(contact);

					var contactList = data.contactList;

					var contactUserHTML = instance._renderResult(
						contactList,
						true,
						lastNameAnchor
					);

					var contactResultContent = A.one(
						'.contacts-portlet .contacts-result'
					);

					contactResultContent.html(contactUserHTML.join(''));

					instance.showMessage(true, data.message);
				},

				renderSelectedContacts(data, lastNameAnchor) {
					var instance = this;

					var contacts = data.contacts;

					if (contacts && contacts.length > 0) {
						if (
							!instance._detailUserView.hasClass('hide') &&
							contacts.length == 1
						) {
							var user = contacts[0].user;

							instance._updateUserToolBar(user);
							instance._updateUserIcons(user);
						}
						else {
							instance._clearContactResult();

							contacts.map((contact) => {
								instance.addContactResult(contact);
							});
						}
					}

					var contactList = data.contactList;

					var contactUserHTML = instance._renderResult(
						contactList,
						true,
						lastNameAnchor
					);

					var contactResultContent = A.one(
						'.contacts-portlet .contacts-result'
					);

					contactResultContent.html(contactUserHTML.join(''));

					instance.showMessage(true, data.message);
				},

				showMessage(success, message) {
					var instance = this;

					if (instance._messageContainer) {
						if (success) {
							if (!message || message == '') {
								message = instance._defaultMessageSuccess;
							}

							instance._messageContainer.html(
								'<span class="alert alert-success">' +
									message +
									'</span>'
							);
						}
						else {
							if (!message || message == '') {
								message = instance._defaultMessageError;
							}

							instance._messageContainer.html(
								'<span class="alert alert-danger">' +
									message +
									'</span>'
							);
						}
					}
				},

				showMoreResult(responseData, lastNameAnchor) {
					var instance = this;

					var contactUserHTML = instance._renderResult(
						responseData,
						false,
						lastNameAnchor
					);

					var contactResultContent = A.one(
						'.contacts-portlet .contacts-result'
					);
					var moreResults = A.one('.contacts-portlet .more-results');

					moreResults.remove();

					contactResultContent.append(contactUserHTML.join(''));
				},

				showPopup(title, uri) {
					var instance = this;

					instance._getPopup();

					instance._popup.show();

					instance._popup.titleNode.html(title);

					instance._popup.io.set('uri', uri);

					instance._popup.io.start();
				},

				updateContacts(keywords, filterBy) {
					var instance = this;

					if (instance._contactList) {
						instance._contactList.sendRequest(
							keywords,
							instance._getRequestTemplate(filterBy)
						);
					}
				},
			},
		});

		Liferay.ContactsCenter = ContactsCenter;
	},
	'',
	{
		requires: [
			'aui-io-plugin-deprecated',
			'aui-toolbar',
			'autocomplete-base',
			'datasource-io',
			'json-parse',
			'liferay-portlet-base',
			'liferay-util-window',
		],
	}
);
