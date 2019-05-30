
create table droid (
	id uuid not null,
	name varchar(255),
	primary_function varchar(255),
	primary key (id)
);

create table human (
	id uuid not null,
	home_planet varchar(255),
	name varchar(255),
	primary key (id)
);

create table episode (
	id int not null,
	label varchar(255),
	primary key (id)
); 

create table droid_appears_in (
	droid_id uuid not null,
	episode_id int
); 

create table human_appears_in (
	human_id uuid not null,
	episode_id int
); 

create table character_friends (
	character_id uuid not null,
	friend_id uuid not null
);

alter table droid_appears_in 
   add constraint droid_appears_in_fk_character 
   foreign key (droid_id) 
   references droid
;

alter table human_appears_in 
   add constraint human_appears_in_fk_character 
   foreign key (human_id) 
   references human
;


