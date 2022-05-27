--liquibase formatted sql
--changeset author:mwright

create schema scheduling;

create table scheduling.practitioners
(
    id varchar primary key,
    aggregate jsonb not null,
    version_number integer not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table scheduling.practices
(
    id varchar primary key,
    aggregate jsonb not null,
    version_number integer not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table scheduling.clients
(
    id varchar primary key,
    aggregate jsonb not null,
    version_number integer not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table scheduling.appointments
(
    id varchar primary key,
    aggregate jsonb not null,
    version_number integer not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create trigger practitioners_update
    before update on scheduling.practitioners
    for each row execute procedure set_updated_at();

create trigger practices_update
    before update on scheduling.practices
    for each row execute procedure set_updated_at();

create trigger clients_update
    before update on scheduling.clients
    for each row execute procedure set_updated_at();

create trigger appointments_update
    before update on scheduling.appointments
    for each row execute procedure set_updated_at();
