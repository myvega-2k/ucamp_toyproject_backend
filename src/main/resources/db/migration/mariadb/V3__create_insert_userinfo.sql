DROP TABLE IF EXISTS user_info;

create table user_info (
    id integer not null auto_increment,
    email varchar(255) not null,
    name varchar(255) not null,
    password varchar(255) not null,
    roles varchar(255) not null,
    primary key (id)
) engine=InnoDB;

insert into user_info (email,name,password,roles) values
('admin@aa.com','adminboot','pwd1','ROLE_ADMIN,ROLE_USER'),
('user@aa.com','userboot','pwd2','ROLE_USER');
