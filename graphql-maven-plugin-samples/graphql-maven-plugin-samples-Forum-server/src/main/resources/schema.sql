
create table member (
	id uuid not null,
	name varchar(255) not null,
	alias varchar(255),
	email varchar(255) not null,
	type varchar(255),
	primary key (id)
);

create table board (
	id uuid not null,
	name varchar(255) not null,
	publicly_available boolean,
	primary key (id)
);

create table topic (
	id uuid not null,
	board_id uuid not null,
	date datetime not null,
	author_id uuid,
	publicly_available boolean,
	nb_posts int,
	title varchar(255) not null,
	content varchar(255) not null,
	primary key (id)
);

create table post (
	id uuid not null,
	date datetime not null,
	topic_id uuid not null,
	author_id uuid,
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
