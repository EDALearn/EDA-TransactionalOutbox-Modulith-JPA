create table IF NOT EXISTS application_user
(
    id                      bigint not null primary key,
    account_non_expired     boolean,
    account_non_locked      boolean,
    additional_properties   jsonb,
    address                 varchar(255),
    city                    varchar(255),
    country                 varchar(255),
    created_by              varchar(255),
    created_date            timestamp,
    credentials_non_expired boolean,
    email                   varchar(255),
    enabled                 boolean,
    language                varchar(255),
    last_modified_by        varchar(255),
    last_modified_date      timestamp,
    name                    varchar(255),
    password                varchar(255),
    username                varchar(255),
    version                 integer
);

create table IF NOT EXISTS application_user_role
(
    user_id bigint not null constraint fk_application_user_role_user_id references application_user,
    role varchar(255) not null,
    primary key(user_id, role)
);
