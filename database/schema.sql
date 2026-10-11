PRAGMA foreign_keys = ON;


-- TABLE 1: users Stores application login users


CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,

    username TEXT NOT NULL
        COLLATE NOCASE
        UNIQUE
        CHECK (length(trim(username)) > 0),

    password_hash TEXT NOT NULL
        CHECK (length(trim(password_hash)) > 0),

    full_name TEXT NOT NULL
        CHECK (length(trim(full_name)) > 0),

    is_active INTEGER NOT NULL DEFAULT 1
        CHECK (is_active IN (0, 1)),

    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);



-- TABLE 2: categories Stores product categories


CREATE TABLE IF NOT EXISTS categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,

    category_name TEXT NOT NULL
        COLLATE NOCASE
        UNIQUE
        CHECK (length(trim(category_name)) > 0),

    description TEXT,

    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);



-- TABLE 3: suppliers Stores supplier details

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INTEGER PRIMARY KEY AUTOINCREMENT,

    supplier_name TEXT NOT NULL
        CHECK (length(trim(supplier_name)) > 0),

    contact_person TEXT,

    phone TEXT,

    email TEXT,

    address TEXT,

    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);



-- TABLE 4: products Stores general, perishable and electronic products


CREATE TABLE IF NOT EXISTS products (
    product_id INTEGER PRIMARY KEY AUTOINCREMENT,

    sku TEXT NOT NULL
        COLLATE NOCASE
        UNIQUE
        CHECK (length(trim(sku)) > 0),

    product_name TEXT NOT NULL
        CHECK (length(trim(product_name)) > 0),

    product_type TEXT NOT NULL
        CHECK (
            product_type IN (
                'GENERAL',
                'PERISHABLE',
                'ELECTRONIC'
            )
        ),

    category_id INTEGER NOT NULL,

    supplier_id INTEGER NOT NULL,

    quantity INTEGER NOT NULL DEFAULT 0
        CHECK (quantity >= 0),

    reorder_level INTEGER NOT NULL DEFAULT 0
        CHECK (reorder_level >= 0),

    unit_price REAL NOT NULL
        CHECK (unit_price > 0),

    description TEXT,

    expiry_date TEXT,

    warranty_months INTEGER,

    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_products_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_product_special_fields
        CHECK (
            (
                product_type = 'GENERAL'
                AND expiry_date IS NULL
                AND warranty_months IS NULL
            )
            OR
            (
                product_type = 'PERISHABLE'
                AND expiry_date IS NOT NULL
                AND warranty_months IS NULL
            )
            OR
            (
                product_type = 'ELECTRONIC'
                AND expiry_date IS NULL
                AND warranty_months IS NOT NULL
                AND warranty_months > 0
            )
        )
);



-- TABLE 5: stock_movements Stores every stock-in and stock-out operation

CREATE TABLE IF NOT EXISTS stock_movements (
    movement_id INTEGER PRIMARY KEY AUTOINCREMENT,

    product_id INTEGER NOT NULL,

    user_id INTEGER NOT NULL,

    movement_type TEXT NOT NULL
        CHECK (movement_type IN ('IN', 'OUT')),

    quantity INTEGER NOT NULL
        CHECK (quantity > 0),

    quantity_before INTEGER NOT NULL
        CHECK (quantity_before >= 0),

    quantity_after INTEGER NOT NULL
        CHECK (quantity_after >= 0),

    notes TEXT,

    movement_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_stock_movements_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_stock_movements_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_stock_movement_calculation
        CHECK (
            (
                movement_type = 'IN'
                AND quantity_after = quantity_before + quantity
            )
            OR
            (
                movement_type = 'OUT'
                AND quantity_before >= quantity
                AND quantity_after = quantity_before - quantity
            )
        )
);