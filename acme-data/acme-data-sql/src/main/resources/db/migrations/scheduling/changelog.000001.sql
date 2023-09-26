--liquibase formatted sql
--changeset author:mwright

create schema scheduling;

create table scheduling.practitioners
(
    id varchar primary key,
    aggregate jsonb not null,
    revision integer not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table scheduling.practices
(
    id varchar primary key,
    aggregate jsonb not null,
    revision integer not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table scheduling.clients
(
    id varchar primary key,
    aggregate jsonb not null,
    revision integer not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table scheduling.appointments
(
    id varchar primary key,
    aggregate jsonb not null,
    revision integer not null,
    created_at timestamp not null,
    updated_at timestamp not null
);
