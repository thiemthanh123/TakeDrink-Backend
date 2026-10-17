CREATE TABLE addresses (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           user_id BIGINT NOT NULL,
                           recipient_name VARCHAR(150) NOT NULL,
                           phone VARCHAR(20) NOT NULL,
                           address_line VARCHAR(255) NOT NULL,
                           ward VARCHAR(100),
                           district VARCHAR(100),
                           province VARCHAR(100),
                           is_default BOOLEAN NOT NULL DEFAULT FALSE,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,

                           CONSTRAINT pk_addresses PRIMARY KEY (id),

                           CONSTRAINT fk_addresses_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,

                           INDEX idx_addresses_user_id (user_id)
);
