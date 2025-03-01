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

import {
	VALIDATION_PROPERTY_NAME_MAXIMUM_LENGTH,
	VALIDATION_PROPERTY_VALUE_MAXIMUM_LENGTH,
} from './constants';

const isValidEvent = ({eventId, eventProps}) => {
	const validationsEventId = _validate([
		validateEmptyString('eventId'),
		validateMaxLength(),
	]);
	const validationsKey = _validate([
		validateEmptyString('eventPropKey'),
		validateMaxLength(),
	]);
	const validationsValue = _validate([
		validateMaxLength(VALIDATION_PROPERTY_VALUE_MAXIMUM_LENGTH),
	]);
	let errors = [];

	errors = errors.concat(validationsEventId(eventId));

	for (const key in eventProps) {
		errors = errors.concat(
			validationsKey(key),
			validationsValue(eventProps[key])
		);
	}

	if (errors.length) {
		_showErrors(errors);

		return false;
	}

	return true;
};

const validateEmptyString = (labelField) => (str) => {
	let error = '';

	if (!String(str).length) {
		error = `${labelField} is required.`;
	}

	return error;
};

const validateMaxLength = (
	maxAllowed = VALIDATION_PROPERTY_NAME_MAXIMUM_LENGTH
) => (str) => {
	let error = '';

	if (String(str).length > maxAllowed) {
		error = `${str} exceeds maximum length of ${maxAllowed}`;
	}

	return error;
};

const _validate = (validators) => (value) =>
	validators
		.map((validator) => {
			if (typeof validator === 'function') {
				return validator(value);
			}
		})
		.filter(Boolean);

const _showErrors = (errorsArr) =>
	errorsArr.forEach((errMsg) => console.error(new Error(errMsg)));

export {isValidEvent, validateEmptyString, validateMaxLength};
