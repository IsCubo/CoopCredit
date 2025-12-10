-- Insert default roles
INSERT INTO role (name) VALUES ('ROLE_AFILIADO');
INSERT INTO role (name) VALUES ('ROLE_ANALISTA');
INSERT INTO role (name) VALUES ('ROLE_ADMIN');

-- Insert default admin user (password: admin123 hashed with BCrypt)
-- BCrypt hash of "admin123": $2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy6QFDO
INSERT INTO coop_user (username, password, is_enabled, created_at) 
VALUES ('admin@coopcredit.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy6QFDO', true, CURRENT_TIMESTAMP);

-- Assign ROLE_ADMIN to admin user
INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id FROM coop_user u, role r 
WHERE u.username = 'admin@coopcredit.com' AND r.name = 'ROLE_ADMIN';

-- Insert default affiliate user (password: affiliate123 hashed with BCrypt)
-- BCrypt hash of "affiliate123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm
INSERT INTO coop_user (username, password, is_enabled, created_at) 
VALUES ('afiliado@coopcredit.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm', true, CURRENT_TIMESTAMP);

-- Assign ROLE_AFILIADO to affiliate user
INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id FROM coop_user u, role r 
WHERE u.username = 'afiliado@coopcredit.com' AND r.name = 'ROLE_AFILIADO';

-- Create affiliate profile for the default affiliate user
INSERT INTO affiliate (document, first_name, last_name, email, annual_income, registration_date, user_id)
SELECT u.id, 'Afiliado', 'Prueba', 'afiliado@coopcredit.com', 3500000.00, CURRENT_DATE, u.id
FROM coop_user u
WHERE u.username = 'afiliado@coopcredit.com';