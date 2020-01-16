create table category
(
	id bigint auto_increment,
	question_id bigint not null,
	category_name char(12) not null,
	category_status tinyint default 1 null,
	constraint category_pk
		primary key (id)
);
