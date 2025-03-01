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

package com.liferay.portal.model.impl;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.RecentLayoutRevision;
import com.liferay.portal.kernel.model.RecentLayoutRevisionModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import java.sql.Types;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base model implementation for the RecentLayoutRevision service. Represents a row in the &quot;RecentLayoutRevision&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface <code>RecentLayoutRevisionModel</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link RecentLayoutRevisionImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutRevisionImpl
 * @generated
 */
public class RecentLayoutRevisionModelImpl
	extends BaseModelImpl<RecentLayoutRevision>
	implements RecentLayoutRevisionModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a recent layout revision model instance should use the <code>RecentLayoutRevision</code> interface instead.
	 */
	public static final String TABLE_NAME = "RecentLayoutRevision";

	public static final Object[][] TABLE_COLUMNS = {
		{"mvccVersion", Types.BIGINT}, {"recentLayoutRevisionId", Types.BIGINT},
		{"groupId", Types.BIGINT}, {"companyId", Types.BIGINT},
		{"userId", Types.BIGINT}, {"layoutRevisionId", Types.BIGINT},
		{"layoutSetBranchId", Types.BIGINT}, {"plid", Types.BIGINT}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
		new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("recentLayoutRevisionId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("groupId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("layoutRevisionId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("layoutSetBranchId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("plid", Types.BIGINT);
	}

	public static final String TABLE_SQL_CREATE =
		"create table RecentLayoutRevision (mvccVersion LONG default 0 not null,recentLayoutRevisionId LONG not null primary key,groupId LONG,companyId LONG,userId LONG,layoutRevisionId LONG,layoutSetBranchId LONG,plid LONG)";

	public static final String TABLE_SQL_DROP =
		"drop table RecentLayoutRevision";

	public static final String ORDER_BY_JPQL =
		" ORDER BY recentLayoutRevision.recentLayoutRevisionId ASC";

	public static final String ORDER_BY_SQL =
		" ORDER BY RecentLayoutRevision.recentLayoutRevisionId ASC";

	public static final String DATA_SOURCE = "liferayDataSource";

	public static final String SESSION_FACTORY = "liferaySessionFactory";

	public static final String TX_MANAGER = "liferayTransactionManager";

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static final boolean ENTITY_CACHE_ENABLED = true;

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static final boolean FINDER_CACHE_ENABLED = true;

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static final boolean COLUMN_BITMASK_ENABLED = true;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long GROUPID_COLUMN_BITMASK = 1L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long LAYOUTREVISIONID_COLUMN_BITMASK = 2L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long LAYOUTSETBRANCHID_COLUMN_BITMASK = 4L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long PLID_COLUMN_BITMASK = 8L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long USERID_COLUMN_BITMASK = 16L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *		#getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long RECENTLAYOUTREVISIONID_COLUMN_BITMASK = 32L;

	public static final long LOCK_EXPIRATION_TIME = GetterUtil.getLong(
		com.liferay.portal.util.PropsUtil.get(
			"lock.expiration.time.com.liferay.portal.kernel.model.RecentLayoutRevision"));

	public RecentLayoutRevisionModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _recentLayoutRevisionId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setRecentLayoutRevisionId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _recentLayoutRevisionId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return RecentLayoutRevision.class;
	}

	@Override
	public String getModelClassName() {
		return RecentLayoutRevision.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		Map<String, Function<RecentLayoutRevision, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		for (Map.Entry<String, Function<RecentLayoutRevision, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<RecentLayoutRevision, Object> attributeGetterFunction =
				entry.getValue();

			attributes.put(
				attributeName,
				attributeGetterFunction.apply((RecentLayoutRevision)this));
		}

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Map<String, BiConsumer<RecentLayoutRevision, Object>>
			attributeSetterBiConsumers = getAttributeSetterBiConsumers();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attributeName = entry.getKey();

			BiConsumer<RecentLayoutRevision, Object> attributeSetterBiConsumer =
				attributeSetterBiConsumers.get(attributeName);

			if (attributeSetterBiConsumer != null) {
				attributeSetterBiConsumer.accept(
					(RecentLayoutRevision)this, entry.getValue());
			}
		}
	}

	public Map<String, Function<RecentLayoutRevision, Object>>
		getAttributeGetterFunctions() {

		return _attributeGetterFunctions;
	}

	public Map<String, BiConsumer<RecentLayoutRevision, Object>>
		getAttributeSetterBiConsumers() {

		return _attributeSetterBiConsumers;
	}

	private static Function<InvocationHandler, RecentLayoutRevision>
		_getProxyProviderFunction() {

		Class<?> proxyClass = ProxyUtil.getProxyClass(
			RecentLayoutRevision.class.getClassLoader(),
			RecentLayoutRevision.class, ModelWrapper.class);

		try {
			Constructor<RecentLayoutRevision> constructor =
				(Constructor<RecentLayoutRevision>)proxyClass.getConstructor(
					InvocationHandler.class);

			return invocationHandler -> {
				try {
					return constructor.newInstance(invocationHandler);
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					throw new InternalError(reflectiveOperationException);
				}
			};
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new InternalError(noSuchMethodException);
		}
	}

	private static final Map<String, Function<RecentLayoutRevision, Object>>
		_attributeGetterFunctions;
	private static final Map<String, BiConsumer<RecentLayoutRevision, Object>>
		_attributeSetterBiConsumers;

	static {
		Map<String, Function<RecentLayoutRevision, Object>>
			attributeGetterFunctions =
				new LinkedHashMap
					<String, Function<RecentLayoutRevision, Object>>();
		Map<String, BiConsumer<RecentLayoutRevision, ?>>
			attributeSetterBiConsumers =
				new LinkedHashMap
					<String, BiConsumer<RecentLayoutRevision, ?>>();

		attributeGetterFunctions.put(
			"mvccVersion", RecentLayoutRevision::getMvccVersion);
		attributeSetterBiConsumers.put(
			"mvccVersion",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setMvccVersion);
		attributeGetterFunctions.put(
			"recentLayoutRevisionId",
			RecentLayoutRevision::getRecentLayoutRevisionId);
		attributeSetterBiConsumers.put(
			"recentLayoutRevisionId",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setRecentLayoutRevisionId);
		attributeGetterFunctions.put(
			"groupId", RecentLayoutRevision::getGroupId);
		attributeSetterBiConsumers.put(
			"groupId",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setGroupId);
		attributeGetterFunctions.put(
			"companyId", RecentLayoutRevision::getCompanyId);
		attributeSetterBiConsumers.put(
			"companyId",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setCompanyId);
		attributeGetterFunctions.put("userId", RecentLayoutRevision::getUserId);
		attributeSetterBiConsumers.put(
			"userId",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setUserId);
		attributeGetterFunctions.put(
			"layoutRevisionId", RecentLayoutRevision::getLayoutRevisionId);
		attributeSetterBiConsumers.put(
			"layoutRevisionId",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setLayoutRevisionId);
		attributeGetterFunctions.put(
			"layoutSetBranchId", RecentLayoutRevision::getLayoutSetBranchId);
		attributeSetterBiConsumers.put(
			"layoutSetBranchId",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setLayoutSetBranchId);
		attributeGetterFunctions.put("plid", RecentLayoutRevision::getPlid);
		attributeSetterBiConsumers.put(
			"plid",
			(BiConsumer<RecentLayoutRevision, Long>)
				RecentLayoutRevision::setPlid);

		_attributeGetterFunctions = Collections.unmodifiableMap(
			attributeGetterFunctions);
		_attributeSetterBiConsumers = Collections.unmodifiableMap(
			(Map)attributeSetterBiConsumers);
	}

	@Override
	public long getMvccVersion() {
		return _mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_mvccVersion = mvccVersion;
	}

	@Override
	public long getRecentLayoutRevisionId() {
		return _recentLayoutRevisionId;
	}

	@Override
	public void setRecentLayoutRevisionId(long recentLayoutRevisionId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_recentLayoutRevisionId = recentLayoutRevisionId;
	}

	@Override
	public long getGroupId() {
		return _groupId;
	}

	@Override
	public void setGroupId(long groupId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_groupId = groupId;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalGroupId() {
		return GetterUtil.getLong(this.<Long>getColumnOriginalValue("groupId"));
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_companyId = companyId;
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_userId = userId;
	}

	@Override
	public String getUserUuid() {
		try {
			User user = UserLocalServiceUtil.getUserById(getUserId());

			return user.getUuid();
		}
		catch (PortalException portalException) {
			return "";
		}
	}

	@Override
	public void setUserUuid(String userUuid) {
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalUserId() {
		return GetterUtil.getLong(this.<Long>getColumnOriginalValue("userId"));
	}

	@Override
	public long getLayoutRevisionId() {
		return _layoutRevisionId;
	}

	@Override
	public void setLayoutRevisionId(long layoutRevisionId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_layoutRevisionId = layoutRevisionId;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalLayoutRevisionId() {
		return GetterUtil.getLong(
			this.<Long>getColumnOriginalValue("layoutRevisionId"));
	}

	@Override
	public long getLayoutSetBranchId() {
		return _layoutSetBranchId;
	}

	@Override
	public void setLayoutSetBranchId(long layoutSetBranchId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_layoutSetBranchId = layoutSetBranchId;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalLayoutSetBranchId() {
		return GetterUtil.getLong(
			this.<Long>getColumnOriginalValue("layoutSetBranchId"));
	}

	@Override
	public long getPlid() {
		return _plid;
	}

	@Override
	public void setPlid(long plid) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_plid = plid;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalPlid() {
		return GetterUtil.getLong(this.<Long>getColumnOriginalValue("plid"));
	}

	public long getColumnBitmask() {
		if (_columnBitmask > 0) {
			return _columnBitmask;
		}

		if ((_columnOriginalValues == null) ||
			(_columnOriginalValues == Collections.EMPTY_MAP)) {

			return 0;
		}

		for (Map.Entry<String, Object> entry :
				_columnOriginalValues.entrySet()) {

			if (!Objects.equals(
					entry.getValue(), getColumnValue(entry.getKey()))) {

				_columnBitmask |= _columnBitmasks.get(entry.getKey());
			}
		}

		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(
			getCompanyId(), RecentLayoutRevision.class.getName(),
			getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public RecentLayoutRevision toEscapedModel() {
		if (_escapedModel == null) {
			Function<InvocationHandler, RecentLayoutRevision>
				escapedModelProxyProviderFunction =
					EscapedModelProxyProviderFunctionHolder.
						_escapedModelProxyProviderFunction;

			_escapedModel = escapedModelProxyProviderFunction.apply(
				new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		RecentLayoutRevisionImpl recentLayoutRevisionImpl =
			new RecentLayoutRevisionImpl();

		recentLayoutRevisionImpl.setMvccVersion(getMvccVersion());
		recentLayoutRevisionImpl.setRecentLayoutRevisionId(
			getRecentLayoutRevisionId());
		recentLayoutRevisionImpl.setGroupId(getGroupId());
		recentLayoutRevisionImpl.setCompanyId(getCompanyId());
		recentLayoutRevisionImpl.setUserId(getUserId());
		recentLayoutRevisionImpl.setLayoutRevisionId(getLayoutRevisionId());
		recentLayoutRevisionImpl.setLayoutSetBranchId(getLayoutSetBranchId());
		recentLayoutRevisionImpl.setPlid(getPlid());

		recentLayoutRevisionImpl.resetOriginalValues();

		return recentLayoutRevisionImpl;
	}

	@Override
	public int compareTo(RecentLayoutRevision recentLayoutRevision) {
		long primaryKey = recentLayoutRevision.getPrimaryKey();

		if (getPrimaryKey() < primaryKey) {
			return -1;
		}
		else if (getPrimaryKey() > primaryKey) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RecentLayoutRevision)) {
			return false;
		}

		RecentLayoutRevision recentLayoutRevision =
			(RecentLayoutRevision)object;

		long primaryKey = recentLayoutRevision.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isEntityCacheEnabled() {
		return ENTITY_CACHE_ENABLED;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isFinderCacheEnabled() {
		return FINDER_CACHE_ENABLED;
	}

	@Override
	public void resetOriginalValues() {
		_columnOriginalValues = Collections.emptyMap();

		_columnBitmask = 0;
	}

	@Override
	public CacheModel<RecentLayoutRevision> toCacheModel() {
		RecentLayoutRevisionCacheModel recentLayoutRevisionCacheModel =
			new RecentLayoutRevisionCacheModel();

		recentLayoutRevisionCacheModel.mvccVersion = getMvccVersion();

		recentLayoutRevisionCacheModel.recentLayoutRevisionId =
			getRecentLayoutRevisionId();

		recentLayoutRevisionCacheModel.groupId = getGroupId();

		recentLayoutRevisionCacheModel.companyId = getCompanyId();

		recentLayoutRevisionCacheModel.userId = getUserId();

		recentLayoutRevisionCacheModel.layoutRevisionId = getLayoutRevisionId();

		recentLayoutRevisionCacheModel.layoutSetBranchId =
			getLayoutSetBranchId();

		recentLayoutRevisionCacheModel.plid = getPlid();

		return recentLayoutRevisionCacheModel;
	}

	@Override
	public String toString() {
		Map<String, Function<RecentLayoutRevision, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			(4 * attributeGetterFunctions.size()) + 2);

		sb.append("{");

		for (Map.Entry<String, Function<RecentLayoutRevision, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<RecentLayoutRevision, Object> attributeGetterFunction =
				entry.getValue();

			sb.append(attributeName);
			sb.append("=");
			sb.append(
				attributeGetterFunction.apply((RecentLayoutRevision)this));
			sb.append(", ");
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		Map<String, Function<RecentLayoutRevision, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			(5 * attributeGetterFunctions.size()) + 4);

		sb.append("<model><model-name>");
		sb.append(getModelClassName());
		sb.append("</model-name>");

		for (Map.Entry<String, Function<RecentLayoutRevision, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<RecentLayoutRevision, Object> attributeGetterFunction =
				entry.getValue();

			sb.append("<column><column-name>");
			sb.append(attributeName);
			sb.append("</column-name><column-value><![CDATA[");
			sb.append(
				attributeGetterFunction.apply((RecentLayoutRevision)this));
			sb.append("]]></column-value></column>");
		}

		sb.append("</model>");

		return sb.toString();
	}

	private static class EscapedModelProxyProviderFunctionHolder {

		private static final Function<InvocationHandler, RecentLayoutRevision>
			_escapedModelProxyProviderFunction = _getProxyProviderFunction();

	}

	private long _mvccVersion;
	private long _recentLayoutRevisionId;
	private long _groupId;
	private long _companyId;
	private long _userId;
	private long _layoutRevisionId;
	private long _layoutSetBranchId;
	private long _plid;

	public <T> T getColumnValue(String columnName) {
		Function<RecentLayoutRevision, Object> function =
			_attributeGetterFunctions.get(columnName);

		if (function == null) {
			throw new IllegalArgumentException(
				"No attribute getter function found for " + columnName);
		}

		return (T)function.apply((RecentLayoutRevision)this);
	}

	public <T> T getColumnOriginalValue(String columnName) {
		if (_columnOriginalValues == null) {
			return null;
		}

		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		return (T)_columnOriginalValues.get(columnName);
	}

	private void _setColumnOriginalValues() {
		_columnOriginalValues = new HashMap<String, Object>();

		_columnOriginalValues.put("mvccVersion", _mvccVersion);
		_columnOriginalValues.put(
			"recentLayoutRevisionId", _recentLayoutRevisionId);
		_columnOriginalValues.put("groupId", _groupId);
		_columnOriginalValues.put("companyId", _companyId);
		_columnOriginalValues.put("userId", _userId);
		_columnOriginalValues.put("layoutRevisionId", _layoutRevisionId);
		_columnOriginalValues.put("layoutSetBranchId", _layoutSetBranchId);
		_columnOriginalValues.put("plid", _plid);
	}

	private transient Map<String, Object> _columnOriginalValues;

	public static long getColumnBitmask(String columnName) {
		return _columnBitmasks.get(columnName);
	}

	private static final Map<String, Long> _columnBitmasks;

	static {
		Map<String, Long> columnBitmasks = new HashMap<>();

		columnBitmasks.put("mvccVersion", 1L);

		columnBitmasks.put("recentLayoutRevisionId", 2L);

		columnBitmasks.put("groupId", 4L);

		columnBitmasks.put("companyId", 8L);

		columnBitmasks.put("userId", 16L);

		columnBitmasks.put("layoutRevisionId", 32L);

		columnBitmasks.put("layoutSetBranchId", 64L);

		columnBitmasks.put("plid", 128L);

		_columnBitmasks = Collections.unmodifiableMap(columnBitmasks);
	}

	private long _columnBitmask;
	private RecentLayoutRevision _escapedModel;

}