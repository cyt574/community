alter table user
	add account_type int(11) null;

alter table user
	add salt varchar(100) null;

alter table user
	add password varchar(100) null;

alter table user
	add email varchar(100) null;


alter table user
	add phone varchar(100) null;
