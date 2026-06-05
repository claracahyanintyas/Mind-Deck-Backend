-- Migration to add refresh token state to the users table
ALTER TABLE users
ADD COLUMN refresh_token VARCHAR(255) UNIQUE,
ADD COLUMN refresh_token_expiry_date TIMESTAMP WITH TIME ZONE;

-- Optional: Add an index to make token lookups lightning fast during rotation checks
CREATE INDEX idx_users_refresh_token ON users(refresh_token);