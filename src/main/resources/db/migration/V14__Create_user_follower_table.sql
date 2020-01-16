create table user_follower
(
	id bigint auto_increment,
	uid int not null,
	fid int not null,
	constraint user_follower_pk
		primary key (id)
);
