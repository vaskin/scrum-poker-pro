ALTER TABLE meeting ADD CONSTRAINT unique_id_user_id UNIQUE (id, user_id);