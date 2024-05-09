
-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('LANDLORD');
INSERT INTO roles (name) VALUES ('TENANT');

-- Insert users
INSERT INTO app_user (id, name, surname, email, phone_number, password, role, status)
VALUES
    ('123e4567-e89b-12d3-a456-426614174000', 'John', 'Doe', 'admin@admin.com', '+1234567890', '$2a$10$peVWSM29zeq95fC5MpvYg.mnSPwUy.CyyxNZ452TcILGezbZraQKS', 'ADMIN', 'ACTIVE'),
    ('123e4567-e89b-12d3-a456-426614174001', 'Jane', 'Smith', 'landlord@landlord.com', '+0987654321', '$2a$10$peVWSM29zeq95fC5MpvYg.mnSPwUy.CyyxNZ452TcILGezbZraQKS', 'LANDLORD', 'ACTIVE'),
    ('123e4567-e89b-12d3-a456-426614174002', 'Mike', 'Johnson', 'tenant@tenant.com', '+1111111111', '$2a$10$peVWSM29zeq95fC5MpvYg.mnSPwUy.CyyxNZ452TcILGezbZraQKS', 'TENANT', 'ACTIVE');

-- Insert properties
INSERT INTO properties (id, title, address, price, square_meters, description, is_furnished, number_of_rooms, floor_number, available_from, ad_type, apartment_type, owner_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174100', 'Cozy Apartment', '123 Main St', 1000.00, 80, 'A nice cozy apartment.', 'FURNISHED', 3, 2, '2023-06-01', 'RENTAL', 'ONE_KK', (SELECT id FROM app_user WHERE email = 'tenant@tenant.com')),
    ('123e4567-e89b-12d3-a456-426614174101', 'Spacious House', '456 Elm St', 2500.00, 200, 'A spacious house perfect for families.', 'UNFURNISHED', 5, 0, '2023-07-01', 'SALE', 'TWO_KK', (SELECT id FROM app_user WHERE email = 'tenant@tenant.com'));

-- Insert images
INSERT INTO images (id, property_id, file_name)
VALUES
    ('d1262906-b628-4639-b965-c1d8524079e2', '123e4567-e89b-12d3-a456-426614174100', '383f5705-7fe9-4c60-8033-64ca5aacbbe1_houses_and_land-5bfc3326c9e77c0051812eb3.jpg'),
    ('f3b32b44-8ea6-4b45-be61-747e8bc321db', '123e4567-e89b-12d3-a456-426614174100', '5566a85b-4fae-4e3c-bf4d-9099bedb4b96_images.jpeg'),
    ('42dee11c-83d5-4d98-a932-9aaa05c1a9d0', '123e4567-e89b-12d3-a456-426614174100', '03b6657e-64fc-4489-b4ed-0cd8f8102009_pexels-binyaminmellish-186077.jpg');

-- Insert document
INSERT INTO document (id, user_id, document_type, file_name)
VALUES ('1f1ce31c-83ab-463b-80c9-125509b95028', '123e4567-e89b-12d3-a456-426614174002', 'ID_CARD', '0802cbdb-a437-4c81-a950-1cc4e6f07a3f_password.pdf');

