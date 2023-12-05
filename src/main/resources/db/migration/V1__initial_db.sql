CREATE TABLE conference_room (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_name VARCHAR(100) NOT NULL,
  room_description VARCHAR(100) NOT NULL,
  room_capacity INTEGER NOT NULL
);

INSERT INTO conference_room(room_name,room_description,room_capacity) VALUES
 ('Amaze','Amazing room with a capacity of 3 persons',3),
 ('Beauty','Beautiful room with a capacity of 7 persons',7),
 ('Inspire','Get inspired with this room with a capacity of 12 persons',12),
 ('Strive','Strive to book this nice room with a capacity of 20 persons',20);

 CREATE TABLE RESERVATION_DETAIL(
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 room_id BIGINT not null,
 START_TIME TIME not null,
  END_TIME TIME not null,
  MEETING_DATE DATE not null,
  TIME_DURATION BIGINT not null,
  ATTENDEES_COUNT BIGINT not null,
   MEETING_TITLE VARCHAR(255),
   EMAIL_ID VARCHAR(255) NOT NULL,
   UUID VARCHAR(50) not null
 );

CREATE TABLE maintenance_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    START_TIME TIME NOT NULL,
    END_TIME TIME NOT NULL,
    IS_ACTIVE BOOLEAN
);

INSERT INTO maintenance_schedule(START_TIME,END_TIME,IS_ACTIVE) VALUES
('09:00:00', '09:15:00',true),
('13:00:00', '13:15:00',true),
('17:00:00', '17:15:00',true);