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
