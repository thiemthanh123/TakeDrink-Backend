CREATE TABLE order_status_history (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      order_id BIGINT NOT NULL,
                                      from_status VARCHAR(30),
                                      to_status VARCHAR(30) NOT NULL,
                                      changed_by BIGINT,
                                      note VARCHAR(500),
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT pk_order_status_history PRIMARY KEY (id),

                                      CONSTRAINT fk_order_status_history_order
                                          FOREIGN KEY (order_id)
                                              REFERENCES orders(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_order_status_history_user
                                          FOREIGN KEY (changed_by)
                                              REFERENCES users(id)
                                              ON DELETE SET NULL,

                                      INDEX idx_order_status_history_order_created
                                          (order_id, created_at),

                                      INDEX idx_order_status_history_changed_by (changed_by)
);
