CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE doctors (
     id SERIAL PRIMARY KEY,
     user_id INT UNIQUE NOT NULL,
     specialization VARCHAR(255),
     description TEXT,

     CONSTRAINT fk_doctor_user
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE patients (
      id SERIAL PRIMARY KEY,
      user_id INT UNIQUE NOT NULL,
      full_name VARCHAR(255),
      birth_date DATE,

      CONSTRAINT fk_patient_user
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE schedules (
       id SERIAL PRIMARY KEY,
       doctor_id INT NOT NULL,
       work_date DATE NOT NULL,
       start_time TIME NOT NULL,
       end_time TIME NOT NULL,

       CONSTRAINT fk_schedule_doctor
           FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE TABLE time_slots (
        id SERIAL PRIMARY KEY,
        schedule_id INT NOT NULL,
        start_time TIMESTAMP NOT NULL,
        end_time TIMESTAMP NOT NULL,
        is_booked BOOLEAN DEFAULT FALSE,

        CONSTRAINT fk_slot_schedule
            FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE
);

CREATE TABLE appointments (
      id SERIAL PRIMARY KEY,
      patient_id INT NOT NULL,
      doctor_id INT NOT NULL,
      time_slot_id INT UNIQUE NOT NULL,
      status VARCHAR(50) DEFAULT 'BOOKED',
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_appointment_patient
          FOREIGN KEY (patient_id) REFERENCES patients(id),

      CONSTRAINT fk_appointment_doctor
          FOREIGN KEY (doctor_id) REFERENCES doctors(id),

      CONSTRAINT fk_appointment_slot
          FOREIGN KEY (time_slot_id) REFERENCES time_slots(id)
);