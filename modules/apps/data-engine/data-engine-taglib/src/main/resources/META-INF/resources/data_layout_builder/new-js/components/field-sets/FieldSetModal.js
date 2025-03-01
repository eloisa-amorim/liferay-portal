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
import ClayModal, {useModal} from '@clayui/modal';
import {
	ConfigProvider,
	EVENT_TYPES as EVENT_TYPES_CORE,
	FormProvider,
	PagesVisitor,
	useConfig,
	useForm,
	useFormState,
} from 'dynamic-data-mapping-form-renderer';
import {
	dragAndDropReducer,
	fieldEditableReducer,
	languageReducer,
	pagesStructureReducer,
} from 'dynamic-data-mapping-form-renderer/js/core/reducers/index.es';
import {pageReducer} from 'dynamic-data-mapping-form-renderer/js/custom/form/reducers/index.es';
import {default as React, useCallback, useState} from 'react';

import getFieldsWithoutOptions from '../../../js/components/field-sets/actions/getFieldsWithoutOptions.es';
import {usePropagateFieldSet} from '../../../js/components/field-sets/actions/usePropagateFieldSet.es';
import TranslationManager from '../../../js/components/translation-manager/TranslationManager.es';
import {addItem, updateItem} from '../../../js/utils/client.es';
import {getFieldSetDDMForm} from '../../../js/utils/dataConverter.es';
import {isDataLayoutEmpty} from '../../../js/utils/dataLayoutVisitor.es';
import {errorToast, successToast} from '../../../js/utils/toast.es';
import {FormBuilder} from '../../FormBuilder';
import {INITIAL_STATE} from '../../config/initialState';
import {EVENT_TYPES} from '../../eventTypes';
import sidebarReducer from '../../reducers/sidebarReducer';

const ModalContent = ({
	fieldSet: fieldSetProp,
	onClose,
	onUpdate,
	updateFieldSetList,
}) => {
	const dispatch = useForm();
	const {contentType} = useConfig();
	const {
		dataDefinitionId,
		defaultLanguageId,
		editingLanguageId,
	} = useFormState();
	const {dataDefinition, dataLayout} = useFormState({
		schema: ['dataDefinition', 'dataLayout'],
	});

	const [name, setName] = useState(dataDefinition.name ?? {});
	const changeEditingLanguageId = (editingLanguageId) =>
		dispatch({
			payload: {editingLanguageId},
			type: EVENT_TYPES_CORE.LANGUAGE.CHANGE,
		});

	const onSave = async () => {
		const {
			availableLanguageIds,
			dataDefinitionFields,
		} = dataDefinition.serialize();

		const {dataLayoutPages} = dataLayout.serialize();

		try {
			if (!name[defaultLanguageId]) {
				changeEditingLanguageId(defaultLanguageId);
				throw new Error(
					Liferay.Language.get('please-enter-a-valid-title')
				);
			}

			const fieldsWithoutOptions = getFieldsWithoutOptions(
				dataDefinitionFields,
				defaultLanguageId
			);

			if (fieldsWithoutOptions.length) {
				throw new Error(
					Liferay.Util.sub(
						Liferay.Language.get(
							'at-least-one-option-should-be-set-for-field-x'
						),
						fieldsWithoutOptions[0].label[defaultLanguageId]
					)
				);
			}

			const fieldSet = {
				availableLanguageIds,
				dataDefinitionFields,
				defaultDataLayout: {
					dataLayoutPages,
					name,
				},
				name,
			};

			if (dataDefinitionId) {

				// update fieldSet

				const updatedFieldSet = {
					...fieldSetProp,
					...fieldSet,
				};

				const onPropagate = async () => {
					const savedFieldset = await updateItem(
						`/o/data-engine/v2.0/data-definitions/${dataDefinitionId}`,
						updatedFieldSet
					);

					updateFieldSetList({fieldSet: savedFieldset});
					successToast(Liferay.Language.get('fieldset-saved'));
				};

				onClose();
				onUpdate({onPropagate, updatedFieldSet});
			}
			else {

				// create new fieldSet from scratch

				const updatedFieldSet = await addItem(
					`/o/data-engine/v2.0/data-definitions/by-content-type/${contentType}`,
					fieldSet
				);
				onClose();
				updateFieldSetList({fieldSet: updatedFieldSet, isAdding: true});
				successToast(Liferay.Language.get('fieldset-saved'));
			}
		}
		catch ({message}) {
			errorToast(message);
		}
	};

	const isFieldSetInvalid = () =>
		Object.keys(name).length == 0 ||
		!dataLayout?.dataLayoutPages ||
		isDataLayoutEmpty(dataLayout.dataLayoutPages);

	return (
		<>
			<ClayModal.Header>
				{dataDefinitionId
					? Liferay.Language.get('edit-fieldset')
					: Liferay.Language.get('create-new-fieldset')}
			</ClayModal.Header>
			<ClayModal.Header withTitle={false}>
				<TranslationManager
					defaultLanguageId={defaultLanguageId}
					editingLanguageId={editingLanguageId}
					onEditingLanguageIdChange={changeEditingLanguageId}
					translatedLanguageIds={name}
				/>
				<ClayInput.Group className="pl-4 pr-4">
					<ClayInput.GroupItem>
						<ClayInput
							aria-label={Liferay.Language.get(
								'untitled-fieldset'
							)}
							autoFocus
							className="form-control-inline"
							dir={
								Liferay.Language.direction[editingLanguageId] ||
								'ltr'
							}
							onChange={({target: {value}}) =>
								setName((name) => ({
									...name,
									[editingLanguageId]: value,
								}))
							}
							placeholder={Liferay.Language.get(
								'untitled-fieldset'
							)}
							type="text"
							value={
								name[editingLanguageId] ??
								name[defaultLanguageId] ??
								''
							}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayModal.Header>
			<ClayModal.Body>
				<div className="pl-4 pr-4">
					<FormBuilder />
				</div>
			</ClayModal.Body>
			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onClose()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
						<ClayButton
							disabled={isFieldSetInvalid()}
							onClick={onSave}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

const FieldSetModal = ({fieldSet, onClose: onCloseProp}) => {
	const {observer, onClose} = useModal({onClose: onCloseProp});
	const config = useConfig();
	const {
		allowInvalidAvailableLocalesForProperty,
		fieldSets,
		sidebarPanels,
	} = useFormState();
	const dispatch = useForm();

	const updateFieldSetList = useCallback(
		({fieldSet, isAdding = false}) => {
			const newFieldSetList = isAdding
				? [...fieldSets]
				: fieldSets.filter(({id}) => id !== fieldSet.id);
			newFieldSetList.push(fieldSet);
			dispatch({
				payload: {fieldSets: newFieldSetList},
				type: EVENT_TYPES.FIELD_SET.UPDATE_LIST,
			});
			dispatch({
				payload: {fieldSet},
				type: EVENT_TYPES.FIELD_SET.UPDATE,
			});
		},
		[dispatch, fieldSets]
	);

	const propagateFieldSet = usePropagateFieldSet();

	const showPropagationModal = useCallback(
		({onPropagate, updatedFieldSet}) => {
			const modal = {
				actionMessage: Liferay.Language.get('propagate'),
				allowReferencedDataDefinitionDeletion: true,
				fieldSetMessage: Liferay.Language.get(
					'do-you-want-to-propagate-the-changes-to-other-objects-views-using-this-fieldset'
				),
				headerMessage: Liferay.Language.get('propagate-changes'),
			};

			const fieldNames = new Set(
				updatedFieldSet.dataDefinitionFields.map(({name}) => name)
			);

			const hasRemovedAnyField = fieldSet.dataDefinitionFields.some(
				({name}) => !fieldNames.has(name)
			);

			if (hasRemovedAnyField) {
				modal.warningMessage = Liferay.Language.get(
					'the-changes-include-the-deletion-of-fields-and-may-erase-the-data-collected-permanently'
				);
			}

			return propagateFieldSet({fieldSet, modal, onPropagate});
		},
		[fieldSet, propagateFieldSet]
	);

	let data = {};
	if (fieldSet) {
		const ddmForm = getFieldSetDDMForm({
			allowInvalidAvailableLocalesForProperty,
			editingLanguageId: fieldSet.defaultLanguageId,
			fieldSet,
			fieldTypes: config.fieldTypes,
		});

		// TODO: check for issue on getFieldSetDDMForm converting label property

		new PagesVisitor(ddmForm.pages).mapFields((field) => {
			field.label = field.label[fieldSet.defaultLanguageId];
		});

		const {
			defaultDataLayout,
			defaultLanguageId,
			name,
			...dataDefinition
		} = fieldSet;
		const {paginationMode, ...dataLayout} = defaultDataLayout;

		delete dataLayout.dataLayoutPages;
		delete dataLayout.dataRules;

		data = {
			dataDefinition,
			dataDefinitionId: dataDefinition.id,
			dataLayout,
			dataLayoutId: dataLayout.id,
			editingLanguageId: defaultLanguageId,
			name,
			pages: ddmForm.pages,
			paginationMode,
		};
	}

	return (
		<ClayModal
			className="data-layout-builder-editor-modal fieldset-modal"
			observer={observer}
			size="full-screen"
		>
			<ConfigProvider
				config={{
					...config,
					allowFieldSets: false,
					dataDefinitionId: null,
				}}
			>
				<FormProvider
					initialState={INITIAL_STATE}
					reducers={[
						dragAndDropReducer,
						fieldEditableReducer,
						languageReducer,
						pageReducer,
						pagesStructureReducer,
						sidebarReducer,
					]}
					value={{
						...data,
						sidebarPanels,
					}}
				>
					<ModalContent
						fieldSet={fieldSet}
						onClose={onClose}
						onUpdate={showPropagationModal}
						updateFieldSetList={updateFieldSetList}
					/>
				</FormProvider>
			</ConfigProvider>
		</ClayModal>
	);
};

export default FieldSetModal;
