
CREATE TABLE IF NOT EXISTS appmember(
  account varchar(10) primary key,
  name   varchar(16) not null,
  passwd  varchar(255) not null,
  enabled varchar(1) default 'Y' check(enabled='Y' or enabled='N'), 
  birthday date
);        
insert into appmember(account,name,passwd,enabled,birthday)
select 'admin','admin','$6$85ffb036e286938c$cnKMlBkoTiO7xKb6159SV7jOrfETUCsPls7WNEH3PECx3iHCZJvm1xtSlziZT0.tQCrhJNbNIo4HyammGIp160','Y',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='admin');
insert into appmember(account,name,passwd,enabled,birthday)
select 'user1','user1','$6$7a2367a68ab39e11$s/3iktIsllp5RLyiW4g6.GkG90pRVaKKMmxbomsSOgZoKIP8n1878bkSP.YhWp4d4H5pIUYpecQHm2y6pJG3N0','Y',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='user1');
insert into appmember(account,name,passwd,enabled,birthday)
select 'user2','user2','$6$9734ed32a6e37c21$c85uXJQHkBNewUWRxu1F5KllNrJ2XXHuyz0bWUYO5XoUlThpMxU/t9Ovk/N2HF/gTLXsS.Ls4SNE42otu3i3N/','Y',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='user2');
insert into appmember(account,name,passwd,enabled,birthday)
select 'user3','user3','$6$2f35fe5e9fccb76f$YPSYfE6OeDlFVJGAqIZT/0nXRO9DxfEkt4tjgXANiA90iQ3uuCZNIWe4wrWk3wo6FaWb7zX9v8MmTnAVA4AiH1','Y',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='user3');
insert into appmember(account,name,passwd,enabled,birthday)
select 'user4','user4','$6$32095175aad0f6dd$7szZcaupvktA/WQmjdwQDmwBCJmKHMxJ88w4Usgsu2hyCU9CK.noEXvE/RmC6se.ld3R9dRaNHhEFNWB6hrXA/','Y',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='user4');
insert into appmember(account,name,passwd,enabled,birthday)
select 'user5','user5','$6$6b450356984b7abd$MM2EjK42uOEjttg2f9HXO0Tu.2oP8D471HMayoZKP54VJ32acV5zyFp85mWMHC5q2ICdK4.VoeTMiaFz/ecDY/','Y',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='user5');
insert into appmember(account,name,passwd,enabled,birthday)
select 'userx','userx','$6$3cbc9261a6c367fa$wLFf6swqXKkkDAmbB5OFcvCzpA0lck1FIRLwaa2RbNar2kOrrilqQbLhKrRA3Gjfi/H6TjnO1izNdiu6P1Z8e.','N',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='userx');
insert into appmember(account,name,passwd,enabled,birthday)
select 'secretary','secretary','$6$504350e259d8e219$KkYkc1yP7V.tB.7/fOS0J6HegY.llGVI5354eGsxIp9YSo44hgROGE5TN/jIy9PzVbqmYkHexHYkMhWVpgNRp/','N',CURRENT_DATE()
 WHERE NOT EXISTS(SELECT 1 FROM appmember where account='secretary');

CREATE TABLE IF NOT EXISTS authorities(
  aid SERIAL primary key,
  account varchar(10) references appmember on update cascade on delete cascade,
  authority varchar(50) not null
);
create unique index IF NOT EXISTS authorities_idx on authorities(account,authority);

insert into authorities(account,authority) select 'admin','ROLE_ADMIN'
 where not exists(select 1 from authorities where account='admin' and authority='ROLE_ADMIN');
insert into authorities(account,authority) select 'admin','ROLE_USER'
 where not exists(select 1 from authorities where account='admin' and authority='ROLE_USER');
insert into authorities(account,authority) select 'user1','ROLE_USER'
 where not exists(select 1 from authorities where account='user1' and authority='ROLE_USER');
insert into authorities(account,authority) select 'user2','ROLE_USER'
 where not exists(select 1 from authorities where account='user2' and authority='ROLE_USER');
insert into authorities(account,authority) select 'user3','ROLE_USER'
 where not exists(select 1 from authorities where account='user3' and authority='ROLE_USER');
insert into authorities(account,authority) select 'user4','ROLE_USER'
 where not exists(select 1 from authorities where account='user4' and authority='ROLE_USER');
insert into authorities(account,authority) select 'user5','ROLE_USER'
 where not exists(select 1 from authorities where account='user5' and authority='ROLE_USER');
insert into authorities(account,authority) select 'userx','ROLE_USER'
 where not exists(select 1 from authorities where account='userx' and authority='ROLE_USER');
insert into authorities(account,authority) select 'secretary','ROLE_SECRETARY'
 where not exists(select 1 from authorities where account='secretary' and authority='ROLE_SECRETARY');