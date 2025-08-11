USE medilabo_patient ;
DELETE FROM patients;
ALTER TABLE patients AUTO_INCREMENT = 1;
INSERT INTO patients (last_name, first_name, birth_date, gender, address, phone_number) VALUES
('TestNone', 'Test', '1966-12-31', 'FEMALE', '1 Brookside St', '100-222-3333'),
('TestBorderline', 'Test',  '1945-06-24', 'MALE', '2 High St', '200-333-4444'),
('TestInDanger', 'Test','2004-06-18', 'MALE', '3 Club Road', '300-444-5555'),
('TestEarlyOnset', 'Test', '2002-06-28', 'FEMALE', '4 Valley Dr', '400-555-6666');
