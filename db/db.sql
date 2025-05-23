CREATE TABLE USERS(
	id bigserial not null primary key,
	name varchar (100) not null,
	birthday varchar (100) not null,
	cpf varchar(20) not null
);

CREATE TABLE VEHICLE(
	id bigserial not null primary key,
	type_vehicle varchar (20) not null,
	plate varchar (10) not null

);

CREATE TABLE PARKING(
	id bigserial not null primary key,
	user_id bigint not null references USERS(id),
	vehicles_id bigint not null references VEHICLE(id),
 	entry_time varchar (100) not null
);

CREATE TABLE HISTORY(
	id bigserial not null primary key,
	user_id bigint not null references USERS(id),
	vehicles_id bigint not null references VEHICLE(id),
	entry_time varchar (100) not null,
	exit_time varchar (100) not null
);

ALTER TABLE USERS ADD email VARCHAR(100);
ALTER TABLE HISTORY ADD user_cpf VARCHAR(20)
ALTER TABLE HISTORY ADD user_name VARCHAR(100);
ALTER TABLE HISTORY ADD user_email VARCHAR(30);
ALTER TABLE HISTORY DROP COLUMN user_id;
