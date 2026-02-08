insert into customers (id, name, status) values
(1, 'Ada Lovelace', 'ACTIVE'),
(2, 'Grace Hopper', 'ACTIVE'),
(3, 'Alan Turing', 'INACTIVE');

insert into orders (id, customer_id, total, status) values
(10, 1, 120.50, 'OPEN'),
(11, 1, 42.00, 'SHIPPED'),
(12, 2, 84.00, 'OPEN');
