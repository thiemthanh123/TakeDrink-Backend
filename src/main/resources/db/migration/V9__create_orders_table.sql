CREATE TABLE orders (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        order_code VARCHAR(50) NOT NULL,
                        user_id BIGINT,
                        order_type VARCHAR(20) NOT NULL,
                        dining_table_id BIGINT,
                        address_id BIGINT,

                        recipient_name VARCHAR(150),
                        recipient_phone VARCHAR(20),
                        delivery_address VARCHAR(500),

                        status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                        total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
                        note VARCHAR(1000),

                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,

                        CONSTRAINT pk_orders PRIMARY KEY (id),
                        CONSTRAINT uk_orders_order_code UNIQUE (order_code),

                        CONSTRAINT chk_orders_type
                            CHECK (order_type IN ('DINE_IN', 'TAKEAWAY', 'DELIVERY')),

                        CONSTRAINT chk_orders_status
                            CHECK (status IN (
                                              'PENDING',
                                              'CONFIRMED',
                                              'PREPARING',
                                              'READY',
                                              'DELIVERING',
                                              'COMPLETED',
                                              'CANCELLED'
                                )),

                        CONSTRAINT chk_orders_total
                            CHECK (total_amount >= 0),

                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id)
                                ON DELETE SET NULL,

                        CONSTRAINT fk_orders_dining_table
                            FOREIGN KEY (dining_table_id)
                                REFERENCES dining_tables(id)
                                ON DELETE SET NULL,

                        CONSTRAINT fk_orders_address
                            FOREIGN KEY (address_id)
                                REFERENCES addresses(id)
                                ON DELETE SET NULL,

                        INDEX idx_orders_user_id (user_id),
                        INDEX idx_orders_status (status),
                        INDEX idx_orders_created_at (created_at),
                        INDEX idx_orders_order_type (order_type)
);
