INSERT INTO digital_contents (id, content_type, title, description, language_tag)
VALUES
    ('10000000-0000-4000-8000-000000000001', 'BOOK', 'Building APIs',
     'A sample book about designing APIs.', 'en'),
    ('10000000-0000-4000-8000-000000000002', 'BOOK', 'Building APIs',
     NULL, NULL),
    ('10000000-0000-4000-8000-000000000003', 'BOOK', 'Designing Systems',
     'A sample book about software architecture.', 'en')
ON CONFLICT (id) DO NOTHING;

INSERT INTO books (content_id, isbn13, publisher, published_on)
VALUES
    ('10000000-0000-4000-8000-000000000001', '9780134685991', 'Example Press', '2020-01-01'),
    ('10000000-0000-4000-8000-000000000002', NULL, NULL, NULL),
    ('10000000-0000-4000-8000-000000000003', NULL, 'Example Press', '2021-01-01')
ON CONFLICT (content_id) DO NOTHING;

INSERT INTO book_authors (content_id, author_order, name)
VALUES
    ('10000000-0000-4000-8000-000000000001', 0, 'Alice Example'),
    ('10000000-0000-4000-8000-000000000001', 1, 'Bob Example'),
    ('10000000-0000-4000-8000-000000000002', 0, 'Chris Example'),
    ('10000000-0000-4000-8000-000000000003', 0, 'Dana Example')
ON CONFLICT (content_id, author_order) DO NOTHING;

INSERT INTO content_inventory (content_id, total_licenses)
VALUES
    ('10000000-0000-4000-8000-000000000001', 2),
    ('10000000-0000-4000-8000-000000000002', 1),
    ('10000000-0000-4000-8000-000000000003', 0)
ON CONFLICT (content_id) DO NOTHING;
