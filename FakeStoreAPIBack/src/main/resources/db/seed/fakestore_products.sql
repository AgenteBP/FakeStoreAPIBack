-- Catálogo inicial copiado de https://fakestoreapi.com/products (20 productos).
-- Lo ejecuta ProductSeeder al arrancar, solo si la tabla products está vacía.
-- Las imágenes se sirven desde el backend: src/main/resources/static/images/products/
-- El stock inicial es el rating.count de fakestoreapi.

-- Categorías (se insertan solo si no existe una con el mismo nombre)
INSERT INTO categories (name, description, creation_date, update_date)
SELECT 'men''s clothing', NULL, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'men''s clothing');
INSERT INTO categories (name, description, creation_date, update_date)
SELECT 'jewelery', NULL, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'jewelery');
INSERT INTO categories (name, description, creation_date, update_date)
SELECT 'electronics', NULL, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'electronics');
INSERT INTO categories (name, description, creation_date, update_date)
SELECT 'women''s clothing', NULL, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'women''s clothing');

-- Productos
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops', 'Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday', 109.95, 'FS-0001', '/images/products/1.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'men''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Mens Casual Premium Slim Fit T-Shirts ', 'Slim-fitting style, contrast raglan long sleeve, three-button henley placket, light weight & soft fabric for breathable and comfortable wearing. And Solid stitched shirts with round neck made for durability and a great fit for casual fashion wear and diehard baseball fans. The Henley style round neckline includes a three-button placket.', 22.30, 'FS-0002', '/images/products/2.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'men''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Mens Cotton Jacket', 'great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day.', 55.99, 'FS-0003', '/images/products/3.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'men''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Mens Casual Slim Fit', 'The color could be slightly different between on the screen and in practice. / Please note that body builds vary by person, therefore, detailed size information should be reviewed below on the product description.', 15.99, 'FS-0004', '/images/products/4.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'men''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'John Hardy Women''s Legends Naga Gold & Silver Dragon Station Chain Bracelet', 'From our Legends Collection, the Naga was inspired by the mythical water dragon that protects the ocean''s pearl. Wear facing inward to be bestowed with love and abundance, or outward for protection.', 695.00, 'FS-0005', '/images/products/5.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'jewelery';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Solid Gold Petite Micropave ', 'Satisfaction Guaranteed. Return or exchange any order within 30 days.Designed and sold by Hafeez Center in the United States. Satisfaction Guaranteed. Return or exchange any order within 30 days.', 168.00, 'FS-0006', '/images/products/6.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'jewelery';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'White Gold Plated Princess', 'Classic Created Wedding Engagement Solitaire Diamond Promise Ring for Her. Gifts to spoil your love more for Engagement, Wedding, Anniversary, Valentine''s Day...', 9.99, 'FS-0007', '/images/products/7.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'jewelery';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Pierced Owl Rose Gold Plated Stainless Steel Double', 'Rose Gold Plated Double Flared Tunnel Plug Earrings. Made of 316L Stainless Steel', 10.99, 'FS-0008', '/images/products/8.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'jewelery';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'WD 2TB Elements Portable External Hard Drive - USB 3.0 ', 'USB 3.0 and USB 2.0 Compatibility Fast data transfers Improve PC Performance High Capacity; Compatibility Formatted NTFS for Windows 10, Windows 8.1, Windows 7; Reformatting may be required for other operating systems; Compatibility may vary depending on user’s hardware configuration and operating system', 64.00, 'FS-0009', '/images/products/9.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'electronics';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'SanDisk SSD PLUS 1TB Internal SSD - SATA III 6 Gb/s', 'Easy upgrade for faster boot up, shutdown, application load and response (As compared to 5400 RPM SATA 2.5” hard drive; Based on published specifications and internal benchmarking tests using PCMark vantage scores) Boosts burst write performance, making it ideal for typical PC workloads The perfect balance of performance and reliability Read/write speeds of up to 535MB/s/450MB/s (Based on internal testing; Performance may vary depending upon drive capacity, host device, OS and application.)', 109.00, 'FS-0010', '/images/products/10.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'electronics';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Silicon Power 256GB SSD 3D NAND A55 SLC Cache Performance Boost SATA III 2.5', '3D NAND flash are applied to deliver high transfer speeds Remarkable transfer speeds that enable faster bootup and improved overall system performance. The advanced SLC Cache Technology allows performance boost and longer lifespan 7mm slim design suitable for Ultrabooks and Ultra-slim notebooks. Supports TRIM command, Garbage Collection technology, RAID, and ECC (Error Checking & Correction) to provide the optimized performance and enhanced reliability.', 109.00, 'FS-0011', '/images/products/11.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'electronics';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'WD 4TB Gaming Drive Works with Playstation 4 Portable External Hard Drive', 'Expand your PS4 gaming experience, Play anywhere Fast and easy, setup Sleek design with high capacity, 3-year manufacturer''s limited warranty', 114.00, 'FS-0012', '/images/products/12.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'electronics';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Acer SB220Q bi 21.5 inches Full HD (1920 x 1080) IPS Ultra-Thin', '21. 5 inches Full HD (1920 x 1080) widescreen IPS display And Radeon free Sync technology. No compatibility for VESA Mount Refresh Rate: 75Hz - Using HDMI port Zero-frame design | ultra-thin | 4ms response time | IPS panel Aspect ratio - 16: 9. Color Supported - 16. 7 million colors. Brightness - 250 nit Tilt angle -5 degree to 15 degree. Horizontal viewing angle-178 degree. Vertical viewing angle-178 degree 75 hertz', 599.00, 'FS-0013', '/images/products/13.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'electronics';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Samsung 49-Inch CHG90 144Hz Curved Gaming Monitor (LC49HG90DMNXZA) – Super Ultrawide Screen QLED ', '49 INCH SUPER ULTRAWIDE 32:9 CURVED GAMING MONITOR with dual 27 inch screen side by side QUANTUM DOT (QLED) TECHNOLOGY, HDR support and factory calibration provides stunningly realistic and accurate color and contrast 144HZ HIGH REFRESH RATE and 1ms ultra fast response time work to eliminate motion blur, ghosting, and reduce input lag', 999.99, 'FS-0014', '/images/products/14.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'electronics';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'BIYLACLESEN Women''s 3-in-1 Snowboard Jacket Winter Coats', 'Note:The Jackets is US standard size, Please choose size as your usual wear Material: 100% Polyester; Detachable Liner Fabric: Warm Fleece. Detachable Functional Liner: Skin Friendly, Lightweigt and Warm.Stand Collar Liner jacket, keep you warm in cold weather. Zippered Pockets: 2 Zippered Hand Pockets, 2 Zippered Pockets on Chest (enough to keep cards or keys)and 1 Hidden Pocket Inside.Zippered Hand Pockets and Hidden Pocket keep your things secure. Humanized Design: Adjustable and Detachable Hood and Adjustable cuff to prevent the wind and water,for a comfortable fit. 3 in 1 Detachable Design provide more convenience, you can separate the coat and inner as needed, or wear it together. It is suitable for different season and help you adapt to different climates', 56.99, 'FS-0015', '/images/products/15.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'women''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Lock and Love Women''s Removable Hooded Faux Leather Moto Biker Jacket', '100% POLYURETHANE(shell) 100% POLYESTER(lining) 75% POLYESTER 25% COTTON (SWEATER), Faux leather material for style and comfort / 2 pockets of front, 2-For-One Hooded denim style faux leather jacket, Button detail on waist / Detail stitching at sides, HAND WASH ONLY / DO NOT BLEACH / LINE DRY / DO NOT IRON', 29.95, 'FS-0016', '/images/products/16.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'women''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Rain Jacket Women Windbreaker Striped Climbing Raincoats', 'Lightweight perfet for trip or casual wear---Long sleeve with hooded, adjustable drawstring waist design. Button and zipper front closure raincoat, fully stripes Lined and The Raincoat has 2 side pockets are a good size to hold all kinds of things, it covers the hips, and the hood is generous but doesn''t overdo it.Attached Cotton Lined Hood with Adjustable Drawstrings give it a real styled look.', 39.99, 'FS-0017', '/images/products/17.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'women''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'MBJ Women''s Solid Short Sleeve Boat Neck V ', '95% RAYON 5% SPANDEX, Made in USA or Imported, Do Not Bleach, Lightweight fabric with great stretch for comfort, Ribbed on sleeves and neckline / Double stitching on bottom hem', 9.85, 'FS-0018', '/images/products/18.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'women''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'Opna Women''s Short Sleeve Moisture', '100% Polyester, Machine wash, 100% cationic polyester interlock, Machine Wash & Pre Shrunk for a Great Fit, Lightweight, roomy and highly breathable with moisture wicking fabric which helps to keep moisture away, Soft Lightweight Fabric with comfortable V-neck collar and a slimmer fit, delivers a sleek, more feminine silhouette and Added Comfort', 7.95, 'FS-0019', '/images/products/19.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'women''s clothing';
INSERT INTO products (name, description, price, sku, image_url, id_category, active, creation_date, update_date)
SELECT 'DANVOUY Womens T Shirt Casual Cotton Short', '95%Cotton,5%Spandex, Features: Casual, Short Sleeve, Letter Print,V-Neck,Fashion Tees, The fabric is soft and has some stretch., Occasion: Casual/Office/Beach/School/Home/Street. Season: Spring,Summer,Autumn,Winter.', 12.99, 'FS-0020', '/images/products/20.png', id_category, TRUE, NOW(), NOW()
FROM categories WHERE name = 'women''s clothing';

-- Stock de cada producto
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 120, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0001';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 259, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0002';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 500, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0003';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 430, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0004';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 400, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0005';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 70, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0006';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 400, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0007';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 100, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0008';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 203, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0009';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 470, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0010';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 319, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0011';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 400, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0012';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 250, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0013';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 140, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0014';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 235, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0015';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 340, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0016';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 679, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0017';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 130, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0018';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 146, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0019';
INSERT INTO inventory (id_product, available_quantity, reserved_quantity, creation_date, update_date)
SELECT id_product, 145, 0, NOW(), NOW() FROM products WHERE sku = 'FS-0020';
