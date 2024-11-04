
select nextval('application_user_seq');

insert into application_user (id, account_non_expired, account_non_locked, additional_properties, address, city, country, created_by, created_date, credentials_non_expired, email, enabled, language, last_modified_by, last_modified_date, name, password, username, version) values
(1, true, true, null, 'string', 'string', 'string', 'user', '2024-08-02 13:36:47.389586', true, 'user@email.com', true, 'es', 'user', '2024-08-02 13:36:47.389586', 'First User', '$2a$10$I86fM/OvHg8ounuxGq2oBufCecu3NFO4vT1SWIvd4hnM72lrzdXBG', 'user', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO application_user_role (user_id, role) VALUES (1, 'ADMINISTRATOR')
ON CONFLICT (user_id, role) DO NOTHING;
