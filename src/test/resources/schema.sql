create table customers (
    id int primary key,
    name varchar(100) not null,
    status varchar(20) not null
);

create table orders (
    id int primary key,
    customer_id int not null,
    total decimal(10,2) not null,
    status varchar(20) not null
);

create view active_customers as
select id, name
from customers
where status = 'ACTIVE';
