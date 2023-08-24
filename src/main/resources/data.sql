-- password: 1q2w3e4r!
insert into "user" (username, password, nickname, activated) values ('admin', '$2a$10$7ytFP0zxyCmdPOtm.07sw.RTFHGL4UESRYg0v.Yhh2iydXGWjkZci', 'admin', 1);
insert into "user" (username, password, nickname, activated) values ('user', '$2a$10$7ytFP0zxyCmdPOtm.07sw.RTFHGL4UESRYg0v.Yhh2iydXGWjkZci', 'user', 1);

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into user_authority (user_id, authority_name) values (1, 'ROLE_USER');
insert into user_authority (user_id, authority_name) values (1, 'ROLE_ADMIN');
insert into user_authority (user_id, authority_name) values (2, 'ROLE_USER');