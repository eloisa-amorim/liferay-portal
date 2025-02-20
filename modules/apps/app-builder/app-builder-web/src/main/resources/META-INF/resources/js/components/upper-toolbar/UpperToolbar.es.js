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

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import classNames from 'classnames';
import React from 'react';

const UpperToolbar = ({children, className = ''}) => {
	return (
		<nav
			className={classNames(
				'app-builder-upper-toolbar',
				'component-tbar',
				'subnav-tbar-light',
				'tbar',
				className
			)}
		>
			<div className="container-fluid container-fluid-max-xl">
				<ul className="tbar-nav">{children}</ul>
			</div>
		</nav>
	);
};

const UpperToolbarButton = ({children, ...otherProps}) => {
	return (
		<ClayInput.GroupItem>
			<ClayButton className="ml-3" small {...otherProps}>
				{children}
			</ClayButton>
		</ClayInput.GroupItem>
	);
};

const UpperToolbarInput = ({
	autoFocus = true,
	onChange = () => {},
	placeholder,
	...otherProps
}) => {
	return (
		<UpperToolbarItem expand>
			<ClayInput.Group>
				<ClayInput.GroupItem>
					<ClayInput
						aria-label={placeholder}
						autoFocus={autoFocus}
						className="form-control-inline"
						onChange={onChange}
						placeholder={placeholder}
						type="text"
						{...otherProps}
					/>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</UpperToolbarItem>
	);
};

const UpperToolbarItem = ({children, className, expand}) => {
	return (
		<li
			className={classNames(className, 'tbar-item', {
				'tbar-item-expand': expand,
			})}
		>
			<div className="tbar-section">{children}</div>
		</li>
	);
};

const UpperToolbarGroup = ({children}) => {
	return (
		<UpperToolbarItem>
			<ClayInput.Group>{children}</ClayInput.Group>
		</UpperToolbarItem>
	);
};

UpperToolbar.Button = UpperToolbarButton;
UpperToolbar.Group = UpperToolbarGroup;
UpperToolbar.Input = UpperToolbarInput;
UpperToolbar.Item = UpperToolbarItem;

export default UpperToolbar;

export {
	UpperToolbarButton,
	UpperToolbarGroup,
	UpperToolbarInput,
	UpperToolbarItem,
};
