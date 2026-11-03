CREATE TABLE content_files (
    content_id UUID PRIMARY KEY REFERENCES digital_contents(id),
    object_key TEXT NOT NULL CHECK (length(trim(object_key)) > 0)
);
