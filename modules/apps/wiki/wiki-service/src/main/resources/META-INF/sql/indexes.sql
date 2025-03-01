create index IX_B54332D6 on WikiNode (companyId, status);
create unique index IX_920CD8B1 on WikiNode (groupId, name[$COLUMN_LENGTH:75$]);
create index IX_23325358 on WikiNode (groupId, status);
create index IX_E0E6D12C on WikiNode (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_7609B2AE on WikiNode (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_B65BBC83 on WikiPage (companyId);
create index IX_A2001730 on WikiPage (format[$COLUMN_LENGTH:75$]);
create index IX_BA72B89A on WikiPage (groupId, nodeId, head, parentTitle[$COLUMN_LENGTH:255$], status);
create index IX_E0092FF0 on WikiPage (groupId, nodeId, head, status);
create index IX_941E429C on WikiPage (groupId, nodeId, status);
create index IX_5FF21CE6 on WikiPage (groupId, nodeId, title[$COLUMN_LENGTH:255$], head);
create index IX_CAA451D6 on WikiPage (groupId, userId, nodeId, status);
create index IX_9F7655DA on WikiPage (nodeId, head, parentTitle[$COLUMN_LENGTH:255$], status);
create index IX_40F94F68 on WikiPage (nodeId, head, redirectTitle[$COLUMN_LENGTH:255$], status);
create index IX_432F0AB0 on WikiPage (nodeId, head, status);
create index IX_46EEF3C8 on WikiPage (nodeId, parentTitle[$COLUMN_LENGTH:255$]);
create index IX_1ECC7656 on WikiPage (nodeId, redirectTitle[$COLUMN_LENGTH:255$]);
create index IX_546F2D5C on WikiPage (nodeId, status);
create index IX_E745EA26 on WikiPage (nodeId, title[$COLUMN_LENGTH:255$], head);
create index IX_BEA33AB8 on WikiPage (nodeId, title[$COLUMN_LENGTH:255$], status);
create unique index IX_3D4AF476 on WikiPage (nodeId, title[$COLUMN_LENGTH:255$], version);
create index IX_E1F55FB on WikiPage (resourcePrimKey, nodeId, head);
create index IX_94D1054D on WikiPage (resourcePrimKey, nodeId, status);
create unique index IX_2CD67C81 on WikiPage (resourcePrimKey, nodeId, version);
create index IX_1725355C on WikiPage (resourcePrimKey, status);
create index IX_FBBE7C96 on WikiPage (userId, nodeId, status);
create index IX_5DC4BD39 on WikiPage (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_899D3DFB on WikiPage (uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_21277664 on WikiPageResource (nodeId, title[$COLUMN_LENGTH:255$]);
create index IX_13319367 on WikiPageResource (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_F705C7A9 on WikiPageResource (uuid_[$COLUMN_LENGTH:75$], groupId);