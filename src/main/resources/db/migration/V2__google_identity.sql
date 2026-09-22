-- Local password accounts remain unchanged. Google-only accounts have no password.
ALTER TABLE app_user ALTER COLUMN password_hash DROP NOT NULL;
ALTER TABLE app_user ADD COLUMN google_subject VARCHAR(255);
ALTER TABLE app_user ADD CONSTRAINT uq_user_google_subject UNIQUE (google_subject);
ALTER TABLE app_user ADD CONSTRAINT ck_user_authentication
    CHECK (password_hash IS NOT NULL OR google_subject IS NOT NULL);
