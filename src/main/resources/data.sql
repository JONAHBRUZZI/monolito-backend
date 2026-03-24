INSERT INTO products (id, sku, name, price) VALUES (1, 'SKU-100', 'Caja Organizadora', 12.50);
INSERT INTO products (id, sku, name, price) VALUES (2, 'SKU-200', 'Cinta de Embalaje', 4.30);
INSERT INTO products (id, sku, name, price) VALUES (3, 'SKU-300', 'Etiquetas Termicas', 9.90);

INSERT INTO inventory_items (id, product_id, available) VALUES (1, 1, 35);
INSERT INTO inventory_items (id, product_id, available) VALUES (2, 2, 80);
INSERT INTO inventory_items (id, product_id, available) VALUES (3, 3, 60);
