USE car_rental_system;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Review;
DROP TABLE IF EXISTS Maintenance;
DROP TABLE IF EXISTS Invoice;
DROP TABLE IF EXISTS Rental_Contract;
DROP TABLE IF EXISTS Reservation_Extras;
DROP TABLE IF EXISTS Reservation;
DROP TABLE IF EXISTS Car_Status;
DROP TABLE IF EXISTS Extras;
DROP TABLE IF EXISTS Car;
DROP TABLE IF EXISTS Driving_License;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Car_Category;
DROP TABLE IF EXISTS Branch;
DROP TABLE IF EXISTS Admin;

SET FOREIGN_KEY_CHECKS = 1;

USE car_rental_system;

CREATE TABLE Admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Branch (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    manager_name VARCHAR(100),
    open_hour TIME,
    close_hour TIME
);

CREATE TABLE Car_Category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE               
);

CREATE TABLE Employee (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    job_title VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    salary DECIMAL(10,2),
    hire_date DATE,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    branch_id INT NOT NULL,
    admin_id INT,
    CONSTRAINT fk_emp_branch FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    CONSTRAINT fk_emp_admin FOREIGN KEY (admin_id) REFERENCES Admin(admin_id)
);

CREATE TABLE Customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender ENUM('Male','Female'),
    phone_number VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address VARCHAR(255),
    national_id_or_passport VARCHAR(50) UNIQUE,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100),
    registration_date DATE NOT NULL DEFAULT (CURRENT_DATE)
);

CREATE TABLE Driving_License (
    license_id INT AUTO_INCREMENT PRIMARY KEY,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    country_of_issue VARCHAR(100),
    customer_id INT NOT NULL UNIQUE,          
    CONSTRAINT fk_dl_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);
 
CREATE TABLE Car (
    car_id INT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    color VARCHAR(30),
    daily_price DECIMAL(10,2) NOT NULL,
    manufacture_year YEAR,
    fuel_type ENUM('Petrol','Diesel','Electric','Hybrid'),
    transmission_type ENUM('Automatic','Manual'),
    mileage INT DEFAULT 0,
    category_id INT NOT NULL,
    branch_id INT NOT NULL,
    CONSTRAINT fk_car_category FOREIGN KEY (category_id) REFERENCES Car_Category(category_id),
    CONSTRAINT fk_car_branch FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

CREATE TABLE Car_Status (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    car_id INT NOT NULL,
    status ENUM('Available','Reserved','Rented','Maintenance') NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    employee_id INT,
    CONSTRAINT fk_cs_car FOREIGN KEY (car_id) REFERENCES Car(car_id),
    CONSTRAINT fk_cs_employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE Extras (
    extra_id INT AUTO_INCREMENT PRIMARY KEY,
    extra_name VARCHAR(100) NOT NULL,
    description TEXT,
    price_per_day DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE Reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reservation_status ENUM('Pending','Confirmed','Cancelled','Converted') NOT NULL DEFAULT 'Pending',
    customer_id INT NOT NULL,
    category_id INT NOT NULL,
    branch_id INT NOT NULL,
    CONSTRAINT fk_res_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    CONSTRAINT fk_res_category FOREIGN KEY (category_id) REFERENCES Car_Category(category_id),
    CONSTRAINT fk_res_branch FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

CREATE TABLE Reservation_Extras (
    reservation_extra_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    extra_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_re_reservation FOREIGN KEY (reservation_id) REFERENCES Reservation(reservation_id),
    CONSTRAINT fk_re_extra FOREIGN KEY (extra_id) REFERENCES Extras(extra_id),
    CONSTRAINT uq_re UNIQUE (reservation_id, extra_id)
); 

CREATE TABLE Rental_Contract (
    contract_id INT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE NOT NULL,
    expected_return_date DATE NOT NULL,
    actual_return_date DATE,
    mileage_at_pickup INT,
    mileage_at_return INT,
    contract_status ENUM('Active','Completed','Cancelled') NOT NULL DEFAULT 'Active',
    customer_id INT NOT NULL,
    car_id INT NOT NULL,
    employee_id INT NOT NULL,
    reservation_id INT UNIQUE,             
    CONSTRAINT fk_rc_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    CONSTRAINT fk_rc_car FOREIGN KEY (car_id) REFERENCES Car(car_id),
    CONSTRAINT fk_rc_employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
    CONSTRAINT fk_rc_reservation FOREIGN KEY (reservation_id) REFERENCES Reservation(reservation_id)
);

CREATE TABLE Invoice (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    rental_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    extra_charges DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    late_fees DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    invoice_status ENUM('Unpaid','PartiallyPaid','Paid') NOT NULL DEFAULT 'Unpaid',
    contract_id INT NOT NULL UNIQUE,             -- 1-to-1
    CONSTRAINT fk_inv_contract FOREIGN KEY (contract_id) REFERENCES Rental_Contract(contract_id)
);

CREATE TABLE Maintenance (
    maintenance_id INT AUTO_INCREMENT PRIMARY KEY,
    maintenance_type VARCHAR(100) NOT NULL,
    description TEXT,
    maintenance_cost DECIMAL(10,2),
    maintenance_date_in DATE NOT NULL,
    maintenance_date_out DATE,
    car_id INT NOT NULL,
    employee_id INT,
    CONSTRAINT fk_maint_car FOREIGN KEY (car_id) REFERENCES Car(car_id),
    CONSTRAINT fk_maint_employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE Review (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    review_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    customer_id INT NOT NULL,
    contract_id INT NOT NULL UNIQUE,            
    CONSTRAINT fk_rev_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    CONSTRAINT fk_rev_contract FOREIGN KEY (contract_id) REFERENCES Rental_Contract(contract_id)
);




USE car_rental_system;

INSERT INTO Admin (first_name, last_name, username, password, email, phone_number)
VALUES
('Hoor', 'abumsallam', 'hoor_abumsallam', '123456', 'hoor_abumsallam@example.com', '0599000001'),
('Deema', 'issa', 'deema_issa', '123123', 'deema_issa@example.com', '0599000002'), 
('Tabark', 'nawwas', 'tabark_nawwas', '123232', 'tabark_nawwas@example.com', '0599000003');
SELECT * FROM Admin;

INSERT INTO Branch (branch_name, city, address, phone_number, manager_name, open_hour, close_hour)
VALUES
('Main Branch', 'Nablus', 'Rafedia', '082800001', 'Ahmad Manager', '08:00:00', '18:00:00'),
('North Branch', 'Hebron', 'Ain Sarah Street', '082800002', 'Sami Manager', '08:00:00', '18:00:00'),
('South Branch', 'Jenin', 'Nablus Street', '082800003', 'Lina Manager', '08:00:00', '18:00:00');
SELECT * FROM Branch;

INSERT INTO Car_Category (category_name)
VALUES
('Small'),
('Medium'),
('Large'),
('Luxury');
SELECT * FROM Car_Category
ORDER BY category_id;


INSERT INTO Customer 
(first_name, last_name, date_of_birth, gender, phone_number, email, address, national_id_or_passport, username, password)
VALUES
('Mohammad', 'Saleh', '2001-05-10', 'Male', '0591111111', 'mohammad@example.com', 'Nablus', 'P10001', 'mohammad_cust', '123456'),
('Sara', 'Khaled', '2000-09-15', 'Female', '0592222222', 'sara@example.com', 'Hebron', 'P10002', 'sara_cust', '123456'),
('Omar', 'Yousef', '1999-12-01', 'Male', '0593333333', 'omar@example.com', 'Jenin', 'P10003', 'omar_cust', '123456');
SELECT * FROM Customer;

INSERT INTO Driving_License (license_number, issue_date, expiry_date, country_of_issue, customer_id)
VALUES
('LIC1001', '2023-01-01', '2028-01-01', 'Palestine', 1),
('LIC1002', '2022-06-15', '2027-06-15', 'Palestine', 2),
('LIC1003', '2021-03-20', '2026-03-20', 'Palestine', 3);
SELECT * FROM Driving_License;

INSERT INTO Employee (first_name, last_name, job_title, phone_number, email, salary, hire_date, username, password, branch_id, admin_id)
VALUES
('Ali', 'Hassan', 'Receptionist', '0594444441', 'ali.employee@example.com', 700.00, '2024-01-10', 'ali_emp', '123456', 1, 1),
('Mona', 'Adel', 'Supervisor', '0594444442', 'mona.employee@example.com', 900.00, '2023-11-05', 'mona_emp', '123456', 2, 1),
('Kareem', 'Naser', 'Clerk', '0594444443', 'kareem.employee@example.com', 650.00, '2024-02-15', 'kareem_emp', '123456', 3, 2);
SELECT * FROM Employee;

INSERT INTO Car (plate_number, brand, model, color, daily_price, manufacture_year, fuel_type, transmission_type, mileage, category_id, branch_id)
VALUES
('GZ-1001', 'Toyota', 'Yaris', 'White', 35.00, 2022, 'Petrol', 'Automatic', 25000, 1, 1),
('GZ-1002', 'Hyundai', 'Elantra', 'Black', 45.00, 2021, 'Petrol', 'Automatic', 40000, 3, 1),
('GZ-1003', 'Kia', 'Sportage', 'Silver', 60.00, 2023, 'Hybrid', 'Automatic', 12000, 2, 2),
('GZ-1004', 'BMW', '5 Series', 'Blue', 120.00, 2024, 'Petrol', 'Automatic', 5000, 4, 3);
SELECT * FROM Car;

INSERT INTO Extras (extra_name, description, price_per_day, is_available)
VALUES
('GPS', 'Navigation device', 5.00, TRUE),
('Child Seat', 'Seat for children', 3.00, TRUE),
('Extra Insurance', 'Additional coverage', 10.00, TRUE);
SELECT * FROM Extras;

INSERT INTO Car_Status (car_id, status, start_date, end_date, employee_id)
VALUES
(1, 'Available', '2026-04-20 08:00:00', NULL, 1),
(2, 'Available', '2026-04-20 08:00:00', NULL, 1),
(3, 'Maintenance', '2026-04-18 10:00:00', NULL, 2),
(4, 'Available', '2026-04-20 09:00:00', NULL, 3);
SELECT * FROM Car_Status;

INSERT INTO Reservation (start_date, end_date, reservation_status, customer_id, category_id, branch_id)
VALUES
('2026-05-01', '2026-05-05', 'Confirmed', 1, 1, 1),
('2026-05-03', '2026-05-08', 'Pending', 2, 2, 2),
('2026-05-10', '2026-05-12', 'Confirmed', 3, 3, 1);
SELECT * FROM Reservation;

INSERT INTO Reservation_Extras (reservation_id, extra_id, quantity)
VALUES
(1, 1, 1),
(1, 2, 1),
(2, 3, 1),
(3, 1, 2);
SELECT * FROM Reservation_Extras;

INSERT INTO Rental_Contract (start_date, expected_return_date, actual_return_date, mileage_at_pickup, mileage_at_return, contract_status,
 customer_id, car_id, employee_id, reservation_id)
VALUES
('2026-05-01', '2026-05-05', '2026-05-05', 25000, 25500, 'Completed', 1, 1, 1, 1),
('2026-05-10', '2026-05-12', NULL, 40000, 40500, 'Active', 3, 2, 1, 3);
SELECT * FROM Rental_Contract;

INSERT INTO Invoice (rental_cost, extra_charges, late_fees, discount, tax, total_amount, invoice_status, contract_id)
VALUES
(140.00, 8.00, 0.00, 5.00, 19.95, 162.95, 'Paid', 1),
(90.00, 5.00, 0.00, 0.00, 14.25, 109.25, 'Unpaid', 2);
SELECT * FROM Invoice;

INSERT INTO Maintenance (maintenance_type, description, maintenance_cost, maintenance_date_in, maintenance_date_out, car_id, employee_id)
VALUES
('Oil Change', 'Regular maintenance service', 50.00, '2026-04-18', '2026-04-19', 3, 2);
SELECT * FROM Maintenance;

INSERT INTO Review (rating, comment, customer_id, contract_id)
VALUES
(5, 'Excellent service and clean car', 1, 1);
SELECT * FROM Review;




