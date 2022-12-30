create sequence hibernate_sequence start 1 increment 1;

create table users (
    id uuid primary key,
    avatar varchar(255),
    email varchar(255) unique,
    name1 varchar(255),
    password varchar(255),
    role varchar(255)
    );