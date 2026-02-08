create table customers (
    id serial primary key,
    name text not null,
    status text not null
);

create table orders (
    id serial primary key,
    customer_id int not null,
    total numeric(10,2) not null,
    status text not null
);

insert into customers (name, status) values
('Ada Lovelace', 'ACTIVE'),
('Grace Hopper', 'ACTIVE'),
('Alan Turing', 'INACTIVE');

insert into orders (customer_id, total, status) values
(1, 120.50, 'OPEN'),
(1, 42.00, 'SHIPPED'),
(2, 84.00, 'OPEN');

create view active_customers as
select id, name
from customers
where status = 'ACTIVE';
