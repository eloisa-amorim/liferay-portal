/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.saml.persistence.internal.upgrade.v1_1_1;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.saml.persistence.internal.upgrade.v1_1_1.util.SamlSpSessionTable;

import java.sql.SQLException;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
public class SamlSpSessionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try {
			runSQL(
				"alter_column_type SamlSpSession samlSpSessionKey " +
					"VARCHAR(75) null");
			runSQL("alter_column_type SamlSpSession assertionXml TEXT null");
			runSQL(
				"alter_column_type SamlSpSession sessionIndex VARCHAR(75) " +
					"null");
		}
		catch (SQLException sqlException) {
			if (_log.isDebugEnabled()) {
				_log.debug(sqlException, sqlException);
			}

			upgradeTable(
				SamlSpSessionTable.TABLE_NAME, SamlSpSessionTable.TABLE_COLUMNS,
				SamlSpSessionTable.TABLE_SQL_CREATE,
				SamlSpSessionTable.TABLE_SQL_ADD_INDEXES);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SamlSpSessionUpgradeProcess.class);

}