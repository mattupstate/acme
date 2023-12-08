--liquibase formatted sql
--changeset author:mwright

create schema web;

create table web.practices
(
    id text primary key,
    name text not null
);

create table web.practice_contact_points
(
    practice_id text references web.practices(id),
    system text not null,
    value text not null,
    verified_at timestamp
);

create table web.practitioners
(
    id text primary key,
    gender text not null
);

create table web.practitioner_names
(
    practitioner_id text references web.practitioners(id),
    given text not null,
    family text not null,
    prefix text not null,
    suffix text not null,
    period_start timestamp,
    period_end timestamp
);

create table web.practitioner_contact_points
(
    practitioner_id text references web.practitioners(id),
    system text not null,
    value text not null,
    verified_at timestamp
);

create table web.practice_memberships
(
    practice_id text references web.practices(id),
    practitioner_id text references web.practitioners(id),
    role text not null
);

create table web.clients
(
    id text primary key,
    gender text not null
);

create table web.client_names
(
    client_id text references web.clients(id),
    given text not null,
    family text not null,
    prefix text not null,
    suffix text not null,
    period_start timestamp,
    period_end timestamp
);

create table web.client_contact_points
(
    client_id text references web.clients(id),
    system text not null,
    value text not null,
    verified_at timestamp
);

create table web.appointments
(
    id text primary key,
    practitioner_id text references web.practitioners(id),
    client_id text references web.clients(id),
    practice_id text references web.practices(id),
    state varchar not null,
    period_start timestamp not null,
    period_end timestamp
);
