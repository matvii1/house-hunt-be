-- This script runs after the schema is created and populates the database with some initial data with docker
-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('LANDLORD');
INSERT INTO roles (name) VALUES ('TENANT');

-- Insert users
INSERT INTO app_user (id, name, surname, email, phone_number, password, role, status)
VALUES
    ('user1', 'John', 'Doe', 'admin@admin.com', '+1234567890', '$2a$10$peVWSM29zeq95fC5MpvYg.mnSPwUy.CyyxNZ452TcILGezbZraQKS', 'ADMIN', 'ACTIVE'),
    ('user2', 'Jane', 'Smith', 'landlord@landlord.com', '+0987654321', '$2a$10$peVWSM29zeq95fC5MpvYg.mnSPwUy.CyyxNZ452TcILGezbZraQKS', 'LANDLORD', 'ACTIVE'),
    ('user3', 'Mike', 'Johnson', 'tenant@tenant.com', '+1111111111', '$2a$10$peVWSM29zeq95fC5MpvYg.mnSPwUy.CyyxNZ452TcILGezbZraQKS', 'TENANT', 'ACTIVE');

-- Insert properties
INSERT INTO properties (id, title, address, price, square_meters, type, is_furnished, floor_number, move_in_date, owner_id)
VALUES
    ('prop1', 'Cozy Apartment', '123 Main St', 1000.00, 80, 'APARTMENT', true, 2, '2023-06-01', 'user2'),
    ('prop2', 'Spacious House', '456 Elm St', 2500.00, 200, 'HOUSE', false, 0, '2023-07-01', 'user2');

-- Insert images
INSERT INTO images (id, url, property_id)
VALUES
    ('img1', 'https://example.com/image1.jpg', 'prop1'),
    ('img2', 'https://example.com/image2.jpg', 'prop1'),
    ('img3', 'https://example.com/image3.jpg', 'prop2');

-- Insert documents
INSERT INTO document (id, url, document_type, user_id)
VALUES
    ('doc1', 'https://example.com/doc2.pdf', 'PASSPORT', 'user2'),
    ('doc2', 'https://example.com/doc3.pdf', 'DRIVERS_LICENSE', 'user3');
