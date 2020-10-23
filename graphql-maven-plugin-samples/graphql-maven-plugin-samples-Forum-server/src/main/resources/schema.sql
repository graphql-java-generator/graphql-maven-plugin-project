
-- This sequence allows Hibernate to generate id values, thanks to the @GeneratedValue annotation 
CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1000 INCREMENT BY 1;

create table member (
	id long not null,
	name varchar(255) not null,
	alias varchar(255),
	email varchar(255) not null,
	type varchar(255),
	primary key (id)
);

create table board (
	id long not null,
	name varchar(255) not null,
	publicly_available boolean,
	primary key (id)
);

create table topic (
	id long not null,
	board_id long not null,
	date datetime not null,
	author_id long,
	publicly_available boolean,
	nb_posts int,
	title varchar(255) not null,
	content varchar(255) not null,
	primary key (id)
);

create table post (
	id long not null,
	date datetime not null,
	topic_id long not null,
	author_id long,
	publicly_available boolean,
	title varchar(255) not null,
	content varchar(255) not null,
	primary key (id)
);

alter table topic 
   add constraint topic_fk_board
   foreign key (board_id) 
   references board
;
alter table topic 
   add constraint topic_fk_member
   foreign key (author_id) 
   references member
;
alter table post 
   add constraint post_fk_topic
   foreign key (topic_id) 
   references topic
;
alter table post 
   add constraint post_fk_member
   foreign key (author_id) 
   references member
;
