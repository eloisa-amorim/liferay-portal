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

package com.liferay.commerce.account.admin.web.internal.portlet.action;

import com.liferay.commerce.account.constants.CommerceAccountPortletKeys;
import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.account.service.CommerceAccountService;
import com.liferay.commerce.exception.CommerceAddressCityException;
import com.liferay.commerce.exception.CommerceAddressCountryException;
import com.liferay.commerce.exception.CommerceAddressStreetException;
import com.liferay.commerce.exception.NoSuchAddressException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	enabled = false, immediate = true,
	property = {
		"javax.portlet.name=" + CommerceAccountPortletKeys.COMMERCE_ACCOUNT_ADMIN,
		"mvc.command.name=/commerce_account_admin/edit_commerce_address"
	},
	service = MVCActionCommand.class
)
public class EditCommerceAddressMVCActionCommand extends BaseMVCActionCommand {

	protected void deleteCommerceAddress(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCommerceAddressIds = null;

		long commerceAddressId = ParamUtil.getLong(
			actionRequest, "commerceAddressId");

		if (commerceAddressId > 0) {
			deleteCommerceAddressIds = new long[] {commerceAddressId};
		}
		else {
			deleteCommerceAddressIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "deleteCommerceAddressIds"),
				0L);
		}

		for (long deleteCommerceAddressId : deleteCommerceAddressIds) {
			_commerceAddressService.deleteCommerceAddress(
				deleteCommerceAddressId);
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		try {
			if (cmd.equals(Constants.DELETE)) {
				deleteCommerceAddress(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				updateCommerceAddress(actionRequest);
			}

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchAddressException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CommerceAddressCityException ||
					 exception instanceof CommerceAddressCountryException ||
					 exception instanceof CommerceAddressStreetException) {

				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	protected void updateCommerceAddress(ActionRequest actionRequest)
		throws Exception {

		long commerceAddressId = ParamUtil.getLong(
			actionRequest, "commerceAddressId");

		long commerceAccountId = ParamUtil.getLong(
			actionRequest, "commerceAccountId");

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		String street1 = ParamUtil.getString(actionRequest, "street1");
		String street2 = ParamUtil.getString(actionRequest, "street2");
		String street3 = ParamUtil.getString(actionRequest, "street3");
		String city = ParamUtil.getString(actionRequest, "city");
		String zip = ParamUtil.getString(actionRequest, "zip");
		long regionId = ParamUtil.getLong(actionRequest, "regionId");
		long countryId = ParamUtil.getLong(actionRequest, "countryId");
		String phoneNumber = ParamUtil.getString(actionRequest, "phoneNumber");
		int type = ParamUtil.getInteger(actionRequest, "type");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceAddress.class.getName(), actionRequest);

		if (commerceAddressId <= 0) {
			_commerceAddressService.addCommerceAddress(
				CommerceAccount.class.getName(), commerceAccountId, name,
				description, street1, street2, street3, city, zip, regionId,
				countryId, phoneNumber, type, serviceContext);
		}
		else {
			_commerceAddressService.updateCommerceAddress(
				commerceAddressId, name, description, street1, street2, street3,
				city, zip, regionId, countryId, phoneNumber, type,
				serviceContext);
		}

		CommerceAccount commerceAccount =
			_commerceAccountService.getCommerceAccount(commerceAccountId);

		boolean defaultBilling = ParamUtil.getBoolean(
			actionRequest, "defaultBilling");

		if (defaultBilling) {
			_commerceAccountService.updateDefaultBillingAddress(
				commerceAccountId, commerceAddressId);
		}
		else if (commerceAccount.getDefaultBillingAddressId() ==
					commerceAddressId) {

			_commerceAccountService.updateDefaultBillingAddress(
				commerceAccountId, 0);
		}

		boolean defaultShipping = ParamUtil.getBoolean(
			actionRequest, "defaultShipping");

		if (defaultShipping) {
			_commerceAccountService.updateDefaultShippingAddress(
				commerceAccountId, commerceAddressId);
		}
		else if (commerceAccount.getDefaultShippingAddressId() ==
					commerceAddressId) {

			_commerceAccountService.updateDefaultShippingAddress(
				commerceAccountId, 0);
		}
	}

	@Reference
	private CommerceAccountService _commerceAccountService;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private Portal _portal;

}