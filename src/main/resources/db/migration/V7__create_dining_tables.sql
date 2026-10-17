CREATE TABLE dining_tables (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               table_number VARCHAR(30) NOT NULL,
                               qr_token VARCHAR(100) NOT NULL,
                               capacity INT NOT NULL DEFAULT 2,
                               is_active BOOLEAN NOT NULL DEFAULT TRUE,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP,

                               CONSTRAINT pk_dining_tables PRIMARY KEY (id),
                               CONSTRAINT uk_dining_tables_number UNIQUE (table_number),
                               CONSTRAINT uk_dining_tables_qr_token UNIQUE (qr_token),
                               CONSTRAINT chk_dining_tables_capacity CHECK (capacity > 0)
);
