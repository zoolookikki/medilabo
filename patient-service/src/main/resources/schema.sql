-- -----------------------------------------------------
-- Schema medilabo_patient
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS medilabo_patient DEFAULT CHARACTER SET utf8mb4 ;
USE medilabo_patient ;

-- -----------------------------------------------------
-- Table medilabo_patient.patients
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS medilabo_patient.patients (
  id BIGINT NOT NULL AUTO_INCREMENT,
  last_name VARCHAR(50) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  birth_date DATE NOT NULL,
  gender VARCHAR(10) NOT NULL,
  address VARCHAR(255),
  phone_number VARCHAR(12),
  PRIMARY KEY (id)
) ENGINE=InnoDB;
