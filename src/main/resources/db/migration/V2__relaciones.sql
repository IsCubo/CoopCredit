-- Create user_role junction table
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES coop_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- Add foreign key constraints
ALTER TABLE affiliate ADD CONSTRAINT fk_affiliate_user 
    FOREIGN KEY (user_id) REFERENCES coop_user(id) ON DELETE SET NULL;

ALTER TABLE credit_application ADD CONSTRAINT fk_credit_app_affiliate 
    FOREIGN KEY (affiliate_id) REFERENCES affiliate(id) ON DELETE CASCADE;

ALTER TABLE risk_evaluation ADD CONSTRAINT fk_risk_eval_app 
    FOREIGN KEY (application_id) REFERENCES credit_application(id) ON DELETE CASCADE;

-- Create indexes for better performance
CREATE INDEX idx_user_username ON coop_user(username);
CREATE INDEX idx_affiliate_document ON affiliate(document);
CREATE INDEX idx_affiliate_email ON affiliate(email);
CREATE INDEX idx_affiliate_user_id ON affiliate(user_id);
CREATE INDEX idx_credit_app_affiliate_id ON credit_application(affiliate_id);
CREATE INDEX idx_credit_app_status ON credit_application(status);
CREATE INDEX idx_risk_eval_app_id ON risk_evaluation(application_id);