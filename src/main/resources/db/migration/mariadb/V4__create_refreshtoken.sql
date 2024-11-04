DROP TABLE IF EXISTS refresh_token;

create table refresh_token (
	id integer not null auto_increment,
	user_id integer,
	expiry_date datetime(6),
	token varchar(255),
	primary key (id)
) engine=InnoDB;

alter table if exists refresh_token add constraint UK_userid unique (user_id);
alter table if exists refresh_token add constraint FK_userid foreign key (user_id) references user_info (id);