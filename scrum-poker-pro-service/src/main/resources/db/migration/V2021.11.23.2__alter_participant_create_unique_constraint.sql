ALTER TABLE meeting DROP CONSTRAINT unique_id_user_id;
ALTER TABLE participant ADD CONSTRAINT unique_meeting_id_user_id UNIQUE (meeting_id, user_id);