alter table question
	add hot_in_30d int default 0 not null;

alter table question
	add hot_in_15d int default 0 not null;

alter table question
	add hot_in_7d int default 0 not null;