INSERT INTO categories (
    name,
    description,
    is_active,
    created_at,
    updated_at
)
VALUES
    (
        'Coffee',
        'Coffee and espresso drinks',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Tea',
        'Tea and fruit tea drinks',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Food',
        'Food and snacks',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );