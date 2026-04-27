CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name varchar(255) not null ,
    middle_name varchar(255) ,
    last_name varchar(255) not null ,
    birth_date DATE,
    role VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table specialization (
    id SERIAL primary key ,
    name varchar(255) not null unique
);

CREATE TABLE doctors (
     user_id INT PRIMARY KEY,
     specialization_id int references specialization(id),
     description TEXT,

     CONSTRAINT fk_doctor_user
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE doctor_schedule_templates (
       id SERIAL PRIMARY KEY,
       doctor_id INT NOT NULL,
       day_of_week varchar(32) NOT NULL,
       start_time TIME NOT NULL,
       end_time TIME NOT NULL,
       slot_duration INT NOT NULL, -- в минутах (15, 30 и т.д.)

       CONSTRAINT fk_template_doctor
           FOREIGN KEY (doctor_id) REFERENCES doctors(user_id) ON DELETE CASCADE,
       UNIQUE (doctor_id, day_of_week)
);

CREATE TABLE schedules (
       id SERIAL PRIMARY KEY,
       doctor_id INT NOT NULL,
       work_date DATE NOT NULL,
       start_time TIME NOT NULL,
       end_time TIME NOT NULL,

       CONSTRAINT fk_schedule_doctor
           FOREIGN KEY (doctor_id) REFERENCES doctors(user_id) ON DELETE CASCADE
);

CREATE TABLE time_slots (
        id SERIAL PRIMARY KEY,
        schedule_id INT NOT NULL,
        start_time TIMESTAMP NOT NULL,
        end_time TIMESTAMP NOT NULL,
        is_booked BOOLEAN DEFAULT FALSE,
        version INT NOT NULL,

        CONSTRAINT fk_slot_schedule
            FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE
);

CREATE TABLE appointments (
      id SERIAL PRIMARY KEY,
      patient_id INT NOT NULL,
      doctor_id INT NOT NULL,
      time_slot_id INT UNIQUE NOT NULL,
      status VARCHAR(50) DEFAULT 'BOOKED',
      consultation_result TEXT,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_appointment_patient
          FOREIGN KEY (patient_id) REFERENCES users(id),

      CONSTRAINT fk_appointment_doctor
          FOREIGN KEY (doctor_id) REFERENCES doctors(user_id),

      CONSTRAINT fk_appointment_slot
          FOREIGN KEY (time_slot_id) REFERENCES time_slots(id)
);