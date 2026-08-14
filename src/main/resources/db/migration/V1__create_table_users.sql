create table if not exists users (
    id bigserial primary key,
    public_id uuid not null unique,
    name varchar(50) not null,
    email varchar(100) not null unique,
    password varchar(255) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);