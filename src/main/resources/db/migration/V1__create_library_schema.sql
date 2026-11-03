CREATE TABLE digital_contents (
    id UUID PRIMARY KEY,
    content_type VARCHAR(30) NOT NULL CHECK (content_type IN ('BOOK')),
    title VARCHAR(300) NOT NULL CHECK (length(trim(title)) > 0),
    description TEXT,
    language_tag VARCHAR(35),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    content_id UUID PRIMARY KEY REFERENCES digital_contents(id),
    isbn13 VARCHAR(13) UNIQUE CHECK (isbn13 ~ '^[0-9]{13}$'),
    publisher VARCHAR(200),
    published_on DATE
);

CREATE TABLE book_authors (
    content_id UUID NOT NULL REFERENCES books(content_id),
    author_order INTEGER NOT NULL CHECK (author_order >= 0),
    name VARCHAR(200) NOT NULL CHECK (length(trim(name)) > 0),
    PRIMARY KEY (content_id, author_order)
);

CREATE TABLE content_inventory (
    content_id UUID PRIMARY KEY REFERENCES digital_contents(id),
    total_licenses INTEGER NOT NULL CHECK (total_licenses >= 0)
);

CREATE TABLE loans (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES content_inventory(content_id),
    borrower_subject TEXT NOT NULL CHECK (length(borrower_subject) > 0),
    borrowed_at TIMESTAMPTZ NOT NULL,
    due_at TIMESTAMPTZ NOT NULL CHECK (due_at > borrowed_at),
    returned_at TIMESTAMPTZ,
    CONSTRAINT chk_loan_return_after_borrow
        CHECK (returned_at IS NULL OR returned_at >= borrowed_at)
);

CREATE UNIQUE INDEX uq_active_loan_per_borrower_content
    ON loans (borrower_subject, content_id)
    WHERE returned_at IS NULL;

CREATE INDEX ix_active_loans_by_content
    ON loans (content_id)
    WHERE returned_at IS NULL;

CREATE INDEX ix_active_loans_by_borrower
    ON loans (borrower_subject, borrowed_at DESC, id DESC)
    WHERE returned_at IS NULL;
