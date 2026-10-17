CREATE TABLE payments (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          order_id BIGINT NOT NULL,
                          payment_method VARCHAR(30) NOT NULL,
                          payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                          amount DECIMAL(12, 2) NOT NULL,
                          transaction_reference VARCHAR(255),
                          paid_at TIMESTAMP NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT pk_payments PRIMARY KEY (id),

                          CONSTRAINT chk_payments_amount
                              CHECK (amount >= 0),

                          CONSTRAINT chk_payments_method
                              CHECK (payment_method IN (
                                                        'CASH',
                                                        'BANK_TRANSFER',
                                                        'ONLINE'
                                  )),

                          CONSTRAINT chk_payments_status
                              CHECK (payment_status IN (
                                                        'PENDING',
                                                        'PAID',
                                                        'FAILED',
                                                        'REFUNDED'
                                  )),

                          CONSTRAINT fk_payments_order
                              FOREIGN KEY (order_id)
                                  REFERENCES orders(id)
                                  ON DELETE CASCADE,

                          INDEX idx_payments_order_id (order_id),
                          INDEX idx_payments_status (payment_status)
);
