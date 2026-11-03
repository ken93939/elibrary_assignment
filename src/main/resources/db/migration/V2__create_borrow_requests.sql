CREATE TABLE borrow_requests (
    borrower_subject TEXT NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    content_id UUID NOT NULL,
    loan_id UUID UNIQUE REFERENCES loans(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (borrower_subject, idempotency_key)
);
