--liquibase formatted sql
--changeset author:mwright

create schema web_server;

create table web_server.practices
(
    id text primary key,
    name text
);

create table web_server.practice_contact_points
(
    practice_id text references web_server.practices(id),
    system text,
    value text,
    verified_at timestamp
);

create table web_server.practitioners
(
    id text primary key,
    gender text
);

create table web_server.practitioner_names
(
    practitioner_id text references web_server.practitioners(id),
    given text,
    family text,
    prefix text,
    suffix text,
    period_start timestamp,
    period_end timestamp
);

create table web_server.practitioner_contact_points
(
    practitioner_id text references web_server.practitioners(id),
    system text,
    value text,
    verified_at timestamp
);

create table web_server.practice_memberships
(
    practice_id text references web_server.practices(id),
    practitioner_id text references web_server.practitioners(id),
    role text
);

create table web_server.clients
(
    id text primary key,
    gender text
);

create table web_server.client_names
(
    client_id text references web_server.clients(id),
    given text,
    family text,
    prefix text,
    suffix text,
    period_start timestamp,
    period_end timestamp
);

create table web_server.client_contact_points
(
    client_id text references web_server.clients(id),
    system text,
    value text,
    verified_at timestamp
);

create table web_server.appointments
(
    id text primary key,
    practitioner_id text references web_server.practitioners(id),
    client_id text references web_server.clients(id),
    practice_id text references web_server.practices(id),
    state varchar,
    period_start timestamp,
    period_end timestamp
);
