SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS user_info;

create table user_info (
    id integer not null auto_increment,
    email varchar(255) not null,
    name varchar(255) not null,
    password varchar(255) not null,
    roles varchar(255) not null,
    primary key (id)
) engine=InnoDB;

alter table IF EXISTS user_info add constraint UK_email unique (email);

insert into user_info (email,name,password,roles) values
('admin@aa.com','adminboot','$2a$10$5gkZZbCFEi7Onc15bsx5I.1Q9vasc8zR.e2Ej3opLWkc3IjUkOVce','ROLE_ADMIN,ROLE_USER'),
('user@aa.com','userboot','$2a$10$IAg5Vihyhc96AGi0xq6pIO1Fw.e.jwIjMG/d3gjXyj3SgbaoovFEq','ROLE_USER');
