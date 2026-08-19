create table refresh_tokens (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    token text not null unique,
    expires_at timestamp with time zone not null,
    revoked boolean not null default false,
    created_at timestamp with time zone not null default current_timestamp,

    constraint fk_refresh_token_user
        foreign key (user_id)
        references users(public_id)
        on delete cascade
);

create index idx_refresh_tokens_user_id
    on refresh_tokens(user_id);