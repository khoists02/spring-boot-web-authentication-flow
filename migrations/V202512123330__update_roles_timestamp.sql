alter table roles add column created_at timestamp default now();
alter table roles add column updated_at timestamp default now();