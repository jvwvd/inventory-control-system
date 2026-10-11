PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;


-- SAMPLE USERS Passwords are stored as SHA-256 hashes

-- admin password: admin123
-- staff password: staff123


INSERT OR IGNORE INTO users (
    username,
    password_hash,
    full_name,
    is_active
)
VALUES (
    'admin',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
    'System Administrator',
    1
);

INSERT OR IGNORE INTO users (
    username,
    password_hash,
    full_name,
    is_active
)
VALUES (
    'staff',
    '10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6',
    'Store Staff',
    1
);



-- SAMPLE CATEGORIES


INSERT OR IGNORE INTO categories (
    category_name,
    description
)
VALUES (
    'Stationery',
    'Office and school stationery products'
);

INSERT OR IGNORE INTO categories (
    category_name,
    description
)
VALUES (
    'Food & Beverages',
    'Food items and beverages'
);

INSERT OR IGNORE INTO categories (
    category_name,
    description
)
VALUES (
    'Electronics',
    'Electronic devices and accessories'
);



-- SAMPLE SUPPLIERS


INSERT INTO suppliers (
    supplier_name,
    contact_person,
    phone,
    email,
    address
)
SELECT
    'Prime Office Supplies',
    'Ahmed Khan',
    '+44 20 7946 0101',
    'ahmed@primeoffice.example',
    '12 Office Street, London'
WHERE NOT EXISTS (
    SELECT 1
    FROM suppliers
    WHERE supplier_name = 'Prime Office Supplies'
);

INSERT INTO suppliers (
    supplier_name,
    contact_person,
    phone,
    email,
    address
)
SELECT
    'Fresh Foods Ltd',
    'Sara Malik',
    '+44 20 7946 0102',
    'sara@freshfoods.example',
    '25 Market Road, London'
WHERE NOT EXISTS (
    SELECT 1
    FROM suppliers
    WHERE supplier_name = 'Fresh Foods Ltd'
);

INSERT INTO suppliers (
    supplier_name,
    contact_person,
    phone,
    email,
    address
)
SELECT
    'TechSource Solutions',
    'Ali Raza',
    '+44 20 7946 0103',
    'ali@techsource.example',
    '40 Technology Avenue, London'
WHERE NOT EXISTS (
    SELECT 1
    FROM suppliers
    WHERE supplier_name = 'TechSource Solutions'
);



-- GENERAL PRODUCT


INSERT OR IGNORE INTO products (
    sku,
    product_name,
    product_type,
    category_id,
    supplier_id,
    quantity,
    reorder_level,
    unit_price,
    description,
    expiry_date,
    warranty_months
)
VALUES (
    'GEN-001',
    'A5 Notebook',
    'GENERAL',

    (
        SELECT category_id
        FROM categories
        WHERE category_name = 'Stationery'
    ),

    (
        SELECT supplier_id
        FROM suppliers
        WHERE supplier_name = 'Prime Office Supplies'
        ORDER BY supplier_id
        LIMIT 1
    ),

    45,
    10,
    3.50,
    'A5 ruled notebook for office and school use',
    NULL,
    NULL
);



-- PERISHABLE PRODUCT

INSERT OR IGNORE INTO products (
    sku,
    product_name,
    product_type,
    category_id,
    supplier_id,
    quantity,
    reorder_level,
    unit_price,
    description,
    expiry_date,
    warranty_months
)
VALUES (
    'PER-001',
    'Fresh Milk 1L',
    'PERISHABLE',

    (
        SELECT category_id
        FROM categories
        WHERE category_name = 'Food & Beverages'
    ),

    (
        SELECT supplier_id
        FROM suppliers
        WHERE supplier_name = 'Fresh Foods Ltd'
        ORDER BY supplier_id
        LIMIT 1
    ),

    26,
    8,
    2.25,
    'One-litre bottle of fresh milk',
    '2027-01-31',
    NULL
);



-- ELECTRONIC PRODUCT

INSERT OR IGNORE INTO products (
    sku,
    product_name,
    product_type,
    category_id,
    supplier_id,
    quantity,
    reorder_level,
    unit_price,
    description,
    expiry_date,
    warranty_months
)
VALUES (
    'ELE-001',
    'Wireless Mouse',
    'ELECTRONIC',

    (
        SELECT category_id
        FROM categories
        WHERE category_name = 'Electronics'
    ),

    (
        SELECT supplier_id
        FROM suppliers
        WHERE supplier_name = 'TechSource Solutions'
        ORDER BY supplier_id
        LIMIT 1
    ),

    18,
    5,
    19.99,
    'Wireless optical mouse with USB receiver',
    NULL,
    24
);



-- SAMPLE STOCK MOVEMENTS FOR GENERAL PRODUCT - Final product quantity: 45

INSERT INTO stock_movements (
    product_id,
    user_id,
    movement_type,
    quantity,
    quantity_before,
    quantity_after,
    notes,
    movement_date
)
SELECT
    (
        SELECT product_id
        FROM products
        WHERE sku = 'GEN-001'
    ),

    (
        SELECT user_id
        FROM users
        WHERE username = 'admin'
    ),

    'IN',
    50,
    0,
    50,
    'Initial notebook stock received',
    '2026-07-20 09:00:00'
WHERE NOT EXISTS (
    SELECT 1
    FROM stock_movements AS movement
    INNER JOIN products AS product
        ON movement.product_id = product.product_id
    WHERE product.sku = 'GEN-001'
      AND movement.movement_type = 'IN'
      AND movement.movement_date = '2026-07-20 09:00:00'
);

INSERT INTO stock_movements (
    product_id,
    user_id,
    movement_type,
    quantity,
    quantity_before,
    quantity_after,
    notes,
    movement_date
)
SELECT
    (
        SELECT product_id
        FROM products
        WHERE sku = 'GEN-001'
    ),

    (
        SELECT user_id
        FROM users
        WHERE username = 'staff'
    ),

    'OUT',
    5,
    50,
    45,
    'Notebooks issued for office use',
    '2026-07-21 11:30:00'
WHERE NOT EXISTS (
    SELECT 1
    FROM stock_movements AS movement
    INNER JOIN products AS product
        ON movement.product_id = product.product_id
    WHERE product.sku = 'GEN-001'
      AND movement.movement_type = 'OUT'
      AND movement.movement_date = '2026-07-21 11:30:00'
);



-- SAMPLE STOCK MOVEMENTS FOR PERISHABLE PRODUCT
-- Final product quantity: 26


INSERT INTO stock_movements (
    product_id,
    user_id,
    movement_type,
    quantity,
    quantity_before,
    quantity_after,
    notes,
    movement_date
)
SELECT
    (
        SELECT product_id
        FROM products
        WHERE sku = 'PER-001'
    ),

    (
        SELECT user_id
        FROM users
        WHERE username = 'admin'
    ),

    'IN',
    30,
    0,
    30,
    'Initial milk delivery received',
    '2026-07-22 08:30:00'
WHERE NOT EXISTS (
    SELECT 1
    FROM stock_movements AS movement
    INNER JOIN products AS product
        ON movement.product_id = product.product_id
    WHERE product.sku = 'PER-001'
      AND movement.movement_type = 'IN'
      AND movement.movement_date = '2026-07-22 08:30:00'
);

INSERT INTO stock_movements (
    product_id,
    user_id,
    movement_type,
    quantity,
    quantity_before,
    quantity_after,
    notes,
    movement_date
)
SELECT
    (
        SELECT product_id
        FROM products
        WHERE sku = 'PER-001'
    ),

    (
        SELECT user_id
        FROM users
        WHERE username = 'staff'
    ),

    'OUT',
    4,
    30,
    26,
    'Milk units sold',
    '2026-07-23 13:15:00'
WHERE NOT EXISTS (
    SELECT 1
    FROM stock_movements AS movement
    INNER JOIN products AS product
        ON movement.product_id = product.product_id
    WHERE product.sku = 'PER-001'
      AND movement.movement_type = 'OUT'
      AND movement.movement_date = '2026-07-23 13:15:00'
);



-- SAMPLE STOCK MOVEMENTS FOR ELECTRONIC PRODUCT
-- Final product quantity: 18

INSERT INTO stock_movements (
    product_id,
    user_id,
    movement_type,
    quantity,
    quantity_before,
    quantity_after,
    notes,
    movement_date
)
SELECT
    (
        SELECT product_id
        FROM products
        WHERE sku = 'ELE-001'
    ),

    (
        SELECT user_id
        FROM users
        WHERE username = 'admin'
    ),

    'IN',
    20,
    0,
    20,
    'Initial wireless mouse stock received',
    '2026-07-24 10:00:00'
WHERE NOT EXISTS (
    SELECT 1
    FROM stock_movements AS movement
    INNER JOIN products AS product
        ON movement.product_id = product.product_id
    WHERE product.sku = 'ELE-001'
      AND movement.movement_type = 'IN'
      AND movement.movement_date = '2026-07-24 10:00:00'
);

INSERT INTO stock_movements (
    product_id,
    user_id,
    movement_type,
    quantity,
    quantity_before,
    quantity_after,
    notes,
    movement_date
)
SELECT
    (
        SELECT product_id
        FROM products
        WHERE sku = 'ELE-001'
    ),

    (
        SELECT user_id
        FROM users
        WHERE username = 'staff'
    ),

    'OUT',
    2,
    20,
    18,
    'Wireless mouse units issued',
    '2026-07-25 14:45:00'
WHERE NOT EXISTS (
    SELECT 1
    FROM stock_movements AS movement
    INNER JOIN products AS product
        ON movement.product_id = product.product_id
    WHERE product.sku = 'ELE-001'
      AND movement.movement_type = 'OUT'
      AND movement.movement_date = '2026-07-25 14:45:00'
);

COMMIT;