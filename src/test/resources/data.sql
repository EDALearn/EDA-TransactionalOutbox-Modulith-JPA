
select nextval('customer_seq');
select nextval('customer_seq');
-- Insert test data for Customer entity
INSERT INTO customer (id, version, name, email, addresses, created_by, created_date, last_modified_by, last_modified_date)
VALUES
    (1, 1, 'John Doe', 'john.doe@example.com', '[{"street":"123 Main St","city":"Anytown"}]', 'system', '2023-01-01 00:00:00', 'system', '2023-01-01 00:00:00'),
    (2, 1, 'Jane Smith', 'jane.smith@example.com', '[{"street":"456 Elm St","city":"Othertown"}]', 'system', '2023-01-01 00:00:00', 'system', '2023-01-01 00:00:00');


select nextval('payment_method_seq');
select nextval('payment_method_seq');
-- Insert test data for PaymentMethod entity
INSERT INTO payment_method (id, version, type, card_number, customer_id, created_by, created_date, last_modified_by, last_modified_date)
VALUES
    (1, 1, 1, '1234567890123456', 1, 'system', '2023-01-01 00:00:00', 'system', '2023-01-01 00:00:00'),
    (2, 1, 2, '6543210987654321', 2, 'system', '2023-01-01 00:00:00', 'system', '2023-01-01 00:00:00');
