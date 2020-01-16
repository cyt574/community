create table user_detail
(
	id bigint auto_increment,
	gender tinyint null,
	industry varchar(50) null,
	profession varchar(50) null,
	signature varchar(200) null,
	background_image varchar(200) null,
	constraint user_detail_pk
		primary key (id)
);

