CREATE TABLE products (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(12, 2) NOT NULL,
                          image_url VARCHAR(1000),
                          category_id BIGINT,
                          is_available BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT pk_products PRIMARY KEY (id),

                          CONSTRAINT chk_products_price
                              CHECK (price >= 0),

                          CONSTRAINT fk_products_category
                              FOREIGN KEY (category_id)
                                  REFERENCES categories(id)
                                  ON DELETE SET NULL
                                  ON UPDATE CASCADE,

                          INDEX idx_products_name (name),
                          INDEX idx_products_category_id (category_id)
);
