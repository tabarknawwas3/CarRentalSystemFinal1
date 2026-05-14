USE car_rental_system;


-- 1) SELECT OPERATIONS

-- Admin login validation
SELECT admin_id, first_name, last_name, username, password, email, phone_number, created_at
FROM Admin
WHERE username = 'hoor_abumsallam'
  AND password = '123456';

-- Customer login validation
SELECT customer_id, first_name, last_name, date_of_birth, gender,
       phone_number, email, address, national_id_or_passport,
       username, password, registration_date
FROM Customer
WHERE username = 'mohammad_cust'
  AND password = '123456';

-- Employee login validation
SELECT employee_id, first_name, last_name, job_title, phone_number, email,
       salary, hire_date, username, password, branch_id, admin_id
FROM Employee
WHERE username = 'ali_emp'
  AND password = '123456';

-- View all cars with category and branch details
SELECT c.car_id, c.plate_number, c.brand, c.model, c.color,
       c.daily_price, c.manufacture_year, c.fuel_type,
       c.transmission_type, c.mileage,
       cc.category_name, b.branch_name, b.city
FROM Car c
JOIN Car_Category cc ON c.category_id = cc.category_id
JOIN Branch b ON c.branch_id = b.branch_id
ORDER BY c.car_id;

-- View available cars only
SELECT c.car_id, c.plate_number, c.brand, c.model, c.daily_price,
       cc.category_name, b.branch_name, cs.status
FROM Car c
JOIN Car_Category cc ON c.category_id = cc.category_id
JOIN Branch b ON c.branch_id = b.branch_id
JOIN Car_Status cs ON c.car_id = cs.car_id
WHERE cs.status = 'Available'
ORDER BY c.daily_price;

-- Search customers by name, phone, email, username, or national ID
SELECT customer_id, first_name, last_name, phone_number, email, address,
       national_id_or_passport, username
FROM Customer
WHERE first_name LIKE '%Mohammad%'
   OR last_name LIKE '%Mohammad%'
   OR phone_number LIKE '%059%'
   OR email LIKE '%example%'
   OR username LIKE '%cust%'
   OR national_id_or_passport LIKE '%P100%';

-- View reservations with customer, branch, and category details
SELECT r.reservation_id, r.reservation_date, r.start_date, r.end_date,
       r.reservation_status,
       CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
       cc.category_name, b.branch_name
FROM Reservation r
JOIN Customer c ON r.customer_id = c.customer_id
JOIN Car_Category cc ON r.category_id = cc.category_id
JOIN Branch b ON r.branch_id = b.branch_id
ORDER BY r.reservation_id;

-- View reservations for a specific customer
SELECT reservation_id, reservation_date, start_date, end_date,
       reservation_status, customer_id, category_id, branch_id
FROM Reservation
WHERE customer_id = 1
ORDER BY reservation_id DESC;

-- View extras selected for a reservation
SELECT e.extra_name, re.quantity, e.price_per_day,
       (re.quantity * e.price_per_day) AS estimated_daily_extra_cost
FROM Reservation_Extras re
JOIN Extras e ON re.extra_id = e.extra_id
WHERE re.reservation_id = 1;

-- View rental contracts with customer and car information
SELECT rc.contract_id, rc.start_date, rc.expected_return_date,
       rc.actual_return_date, rc.mileage_at_pickup, rc.mileage_at_return,
       rc.contract_status,
       CONCAT(cu.first_name, ' ', cu.last_name) AS customer_name,
       CONCAT(car.brand, ' ', car.model, ' - ', car.plate_number) AS car_info,
       rc.reservation_id
FROM Rental_Contract rc
JOIN Customer cu ON rc.customer_id = cu.customer_id
JOIN Car car ON rc.car_id = car.car_id
ORDER BY rc.contract_id;

-- View invoices with contract details
SELECT i.invoice_id, i.issue_date, i.rental_cost, i.extra_charges,
       i.late_fees, i.discount, i.tax, i.total_amount,
       i.invoice_status, i.contract_id
FROM Invoice i
JOIN Rental_Contract rc ON i.contract_id = rc.contract_id
ORDER BY i.invoice_id;

-- Reports: total revenue
SELECT COALESCE(SUM(total_amount), 0) AS total_revenue
FROM Invoice
WHERE invoice_status = 'Paid';

-- Reports: active rentals
SELECT COUNT(*) AS active_rentals
FROM Rental_Contract
WHERE contract_status = 'Active';

-- Reports: fleet utilization
SELECT
    COUNT(*) AS total_cars,
    SUM(CASE WHEN cs.status IN ('Rented', 'Reserved') THEN 1 ELSE 0 END) AS used_cars,
    ROUND((SUM(CASE WHEN cs.status IN ('Rented', 'Reserved') THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) AS utilization_percentage
FROM Car c
JOIN Car_Status cs ON c.car_id = cs.car_id;

-- Reports: average review rating
SELECT COALESCE(AVG(rating), 0) AS average_rating
FROM Review;


-- 2) INSERT / UPDATE / DELETE OPERATIONS


START TRANSACTION;

-- Add a new branch
INSERT INTO Branch (branch_name, city, address, phone_number, manager_name, open_hour, close_hour)
VALUES ('Demo Branch', 'Ramallah', 'Al-Bireh Street', '0598000000', 'Demo Manager', '08:00:00', '18:00:00');

-- Add a new category
INSERT INTO Car_Category (category_name)
VALUES ('Demo Category');

-- Add a new customer account
INSERT INTO Customer
(first_name, last_name, date_of_birth, gender, phone_number, email, address,
 national_id_or_passport, username, password)
VALUES
('Lana', 'Ahmad', '2002-04-12', 'Female', '0595555555',
 'lana.demo@example.com', 'Ramallah', 'P-DEMO-10004', 'lana_demo_cust', '123456');

-- Add a new car
INSERT INTO Car
(plate_number, brand, model, color, daily_price, manufacture_year,
 fuel_type, transmission_type, mileage, category_id, branch_id)
VALUES
('DEMO-2001', 'Honda', 'Civic', 'Gray', 50.00, 2022,
 'Petrol', 'Automatic', 18000,
 (SELECT category_id FROM Car_Category WHERE category_name = 'Demo Category'),
 (SELECT branch_id FROM Branch WHERE branch_name = 'Demo Branch'));

-- Add car status for the new car
INSERT INTO Car_Status (car_id, status, start_date, end_date, employee_id)
VALUES
((SELECT car_id FROM Car WHERE plate_number = 'DEMO-2001'),
 'Available', NOW(), NULL, 1);

-- Customer creates a reservation
INSERT INTO Reservation
(start_date, end_date, reservation_status, customer_id, category_id, branch_id)
VALUES
('2026-06-01', '2026-06-05', 'Pending',
 (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust'),
 (SELECT category_id FROM Car_Category WHERE category_name = 'Demo Category'),
 (SELECT branch_id FROM Branch WHERE branch_name = 'Demo Branch'));

-- Customer adds an extra service to the reservation
INSERT INTO Reservation_Extras (reservation_id, extra_id, quantity)
VALUES
((SELECT reservation_id FROM Reservation
  WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
  ORDER BY reservation_id DESC LIMIT 1),
 1, 1);

-- Admin confirms the reservation
UPDATE Reservation
SET reservation_status = 'Confirmed'
WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
ORDER BY reservation_id DESC
LIMIT 1;

-- Admin updates car price and mileage
UPDATE Car
SET daily_price = 55.00,
    mileage = 19000
WHERE plate_number = 'DEMO-2001';

-- Admin creates a rental contract after accepting the reservation
INSERT INTO Rental_Contract
(start_date, expected_return_date, actual_return_date, mileage_at_pickup,
 mileage_at_return, contract_status, customer_id, car_id, employee_id, reservation_id)
VALUES
('2026-06-01', '2026-06-05', NULL, 19000, NULL, 'Active',
 (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust'),
 (SELECT car_id FROM Car WHERE plate_number = 'DEMO-2001'),
 1,
 (SELECT reservation_id FROM Reservation
  WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
  ORDER BY reservation_id DESC LIMIT 1));

-- Mark the reservation as converted to contract
UPDATE Reservation
SET reservation_status = 'Converted'
WHERE reservation_id =
    (SELECT reservation_id FROM Rental_Contract
     WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
     ORDER BY contract_id DESC LIMIT 1);

-- Update car status to rented
UPDATE Car_Status
SET status = 'Rented', start_date = NOW(), end_date = NULL
WHERE car_id = (SELECT car_id FROM Car WHERE plate_number = 'DEMO-2001');

-- Generate invoice for the contract
INSERT INTO Invoice
(rental_cost, extra_charges, late_fees, discount, tax, total_amount, invoice_status, contract_id)
VALUES
(220.00, 20.00, 0.00, 0.00, 36.00, 276.00, 'Unpaid',
 (SELECT contract_id FROM Rental_Contract
  WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
  ORDER BY contract_id DESC LIMIT 1));

-- Update invoice payment status
UPDATE Invoice
SET invoice_status = 'Paid'
WHERE contract_id =
    (SELECT contract_id FROM Rental_Contract
     WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
     ORDER BY contract_id DESC LIMIT 1);

-- Customer submits a review after contract completion
INSERT INTO Review (rating, comment, customer_id, contract_id)
VALUES
(5, 'Demo review for testing DML operations.',
 (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust'),
 (SELECT contract_id FROM Rental_Contract
  WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
  ORDER BY contract_id DESC LIMIT 1));

-- Update customer profile
UPDATE Customer
SET phone_number = '0599999999',
    address = 'Ramallah - Updated Address'
WHERE username = 'lana_demo_cust';

-- Delete a selected extra from a reservation
DELETE FROM Reservation_Extras
WHERE reservation_id =
    (SELECT reservation_id FROM Reservation
     WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
     ORDER BY reservation_id DESC LIMIT 1)
  AND extra_id = 1;

-- Delete the demo review
DELETE FROM Review
WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust');

-- Logical cancel operation used instead of deleting important reservation history
UPDATE Reservation
SET reservation_status = 'Cancelled'
WHERE customer_id = (SELECT customer_id FROM Customer WHERE username = 'lana_demo_cust')
ORDER BY reservation_id DESC
LIMIT 1;


