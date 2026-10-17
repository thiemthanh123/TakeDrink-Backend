CREATE TABLE order_items (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT,
                             product_name VARCHAR(255) NOT NULL,
                             unit_price DECIMAL(12, 2) NOT NULL,
                             quantity INT NOT NULL,
                             subtotal DECIMAL(12, 2) NOT NULL,
                             note VARCHAR(500),

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT pk_order_items PRIMARY KEY (id),

                             CONSTRAINT chk_order_items_price
                                 CHECK (unit_price >= 0),

                             CONSTRAINT chk_order_items_quantity
                                 CHECK (quantity > 0),

                             CONSTRAINT chk_order_items_subtotal
                                 CHECK (subtotal >= 0),

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_order_items_product
                                 FOREIGN KEY (product_id)
                                     REFERENCES products(id)
                                     ON DELETE SET NULL,

                             INDEX idx_order_items_order_id (order_id),
                             INDEX idx_order_items_product_id (product_id)
);
