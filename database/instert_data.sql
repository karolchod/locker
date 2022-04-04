INSERT INTO userrole(name) VALUES ('ROLE_USER');
INSERT INTO userrole(name) VALUES ('ROLE_ADMIN');
INSERT INTO userrole(name) VALUES ('ROLE_LOCKER');
INSERT INTO userrole(name) VALUES ('ROLE_NEW');

INSERT INTO appuser(name,username,password,role_id) values ('Admin 1','admin@locker.com','$2a$12$TDSHeesCMJwsWeyeghhCFurR.hdFFC4hz9SlEeFFiqa09uKCxjOES',1);
INSERT INTO appuser(name,username,password,role_id) values ('Test User 1','user1@user.us','$2a$12$TDSHeesCMJwsWeyeghhCFurR.hdFFC4hz9SlEeFFiqa09uKCxjOES',2);
INSERT INTO appuser(name,username,password,role_id) values ('Test User 2','user2@user.us','$2a$12$TDSHeesCMJwsWeyeghhCFurR.hdFFC4hz9SlEeFFiqa09uKCxjOES',2);


INSERT INTO locker(name VALUES ('Szafa 1');

INSERT INTO box (locker_id) VALUES ('1');
INSERT INTO box (locker_id) VALUES ('1');
INSERT INTO box (locker_id) VALUES ('1');
INSERT INTO box (locker_id) VALUES ('1');
INSERT INTO box (locker_id) VALUES ('1');
INSERT INTO box (locker_id) VALUES ('1');
INSERT INTO box (locker_id) VALUES ('1');
INSERT INTO box (locker_id) VALUES ('1');

INSERT INTO parcel(box_id, user_sender_id, user_recipient_id, createddate) 
values ('2','1','2','2021-11-25');

UPDATE box
SET isused='true'
WHERE id='2';

