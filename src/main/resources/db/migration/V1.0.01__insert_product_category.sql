BEGIN;


INSERT INTO CATEGORY (NAME, CREATED_BY, CREATED_DATE, LAST_MODIFIED_BY, LAST_MODIFIED_DATE)
VALUES ('Electronics', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Clothing', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Home & Garden', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Toys & Games', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Sports & Outdoors', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Books', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Furniture', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Jewelry', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Health & Beauty', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year'),
       ('Food', 'inventory-service-v1.0', NOW() - INTERVAL '8 year' + RANDOM() * INTERVAL '1 year',
        'inventory-service-v1.0', NOW() - INTERVAL '6 year' + RANDOM() * INTERVAL '1 year');


CREATE TEMP TABLE IF NOT EXISTS CATEGORY_ASSIGNMENTS AS
SELECT P.PRODUCT_ID,
       CASE
           WHEN P.PRODUCT_ID % 10 = 1 THEN 1
           WHEN P.PRODUCT_ID % 10 = 2 THEN 2
           WHEN P.PRODUCT_ID % 10 = 3 THEN 3
           WHEN P.PRODUCT_ID % 10 = 4 THEN 4
           WHEN P.PRODUCT_ID % 10 = 5 THEN 5
           WHEN P.PRODUCT_ID % 10 = 6 THEN 6
           WHEN P.PRODUCT_ID % 10 = 7 THEN 7
           WHEN P.PRODUCT_ID % 10 = 8 THEN 8
           WHEN P.PRODUCT_ID % 10 = 9 THEN 9
           WHEN P.PRODUCT_ID % 10 = 0 THEN 10
           END                               AS CATEGORY_ID,
       CASE
           WHEN P.PRODUCT_ID % 10 = 1 THEN 'iPhone'
           WHEN P.PRODUCT_ID % 10 = 2 THEN 'Jeans'
           WHEN P.PRODUCT_ID % 10 = 3 THEN 'LED TV'
           WHEN P.PRODUCT_ID % 10 = 4 THEN 'Toy Train Set'
           WHEN P.PRODUCT_ID % 10 = 5 THEN 'Football'
           WHEN P.PRODUCT_ID % 10 = 6 THEN 'Mystery Novel'
           WHEN P.PRODUCT_ID % 10 = 7 THEN 'Coffee Table'
           WHEN P.PRODUCT_ID % 10 = 8 THEN 'Diamond Necklace'
           WHEN P.PRODUCT_ID % 10 = 9 THEN 'Supplement'
           WHEN P.PRODUCT_ID % 10 = 0 THEN 'Beef'
           END || ' ' || (GEN_RANDOM_UUID()) AS PRODUCT_NAME
FROM GENERATE_SERIES(1, 150) AS P(PRODUCT_ID);


INSERT INTO PRODUCT (NAME, DESCRIPTION, PRICE, CREATED_BY, CREATED_DATE, LAST_MODIFIED_BY, LAST_MODIFIED_DATE,
                     CATEGORY_ID)
SELECT CA.PRODUCT_NAME,
       'Description for ' || CA.PRODUCT_NAME,
       (RANDOM() * 1000)::NUMERIC(19, 4),
       'inventory-service-v1.0',
       NOW() - INTERVAL '4 year' + RANDOM() * INTERVAL '1 year',
       'inventory-service-v1.0',
       NOW() - INTERVAL '2 year' + RANDOM() * INTERVAL '1 year',
       CATEGORY_ID
FROM CATEGORY_ASSIGNMENTS CA;


DROP TABLE IF EXISTS CATEGORY_ASSIGNMENTS;

CREATE TEMP TABLE IF NOT EXISTS PRODUCT_IDS AS
SELECT ID AS PRODUCT_ID
FROM PRODUCT;


INSERT INTO INVENTORY (PRODUCT_ID, QUANTITY, CREATED_BY, CREATED_DATE, LAST_MODIFIED_BY, LAST_MODIFIED_DATE)
SELECT PI.PRODUCT_ID,
       (FLOOR(RANDOM() * 10000) + 1)::INTEGER                   AS QUANTITY,
       'inventory-service-v1.0'                                 AS CREATED_BY,
       NOW() - INTERVAL '4 year' + RANDOM() * INTERVAL '1 year' AS CREATED_DATE,
       'inventory-service-v1.0'                                 AS LAST_MODIFIED_BY,
       NOW() - INTERVAL '2 year' + RANDOM() * INTERVAL '1 year' AS LAST_MODIFIED_DATE
FROM PRODUCT_IDS PI;

COMMIT;