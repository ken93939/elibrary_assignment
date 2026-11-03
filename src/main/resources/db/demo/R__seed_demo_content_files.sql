INSERT INTO content_files (content_id, object_key)
VALUES
    ('10000000-0000-4000-8000-000000000001', 'books/building-apis.txt'),
    ('10000000-0000-4000-8000-000000000002', 'books/building-apis-notes.txt'),
    ('10000000-0000-4000-8000-000000000003', 'books/designing-systems.txt')
ON CONFLICT (content_id) DO NOTHING;
