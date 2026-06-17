USE food_bridge;

-- Add payment columns to orders table
ALTER TABLE orders
ADD COLUMN payment_method ENUM('Online', 'Offline') DEFAULT 'Offline',
ADD COLUMN payment_status ENUM('Pending', 'Paid') DEFAULT 'Pending';

-- Add delivery_partner_id to donations
ALTER TABLE donations
ADD COLUMN delivery_partner_id INT NULL,
ADD FOREIGN KEY (delivery_partner_id) REFERENCES delivery_partners(id) ON DELETE SET NULL;

-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    user_id INT NOT NULL,
    hotel_id INT NOT NULL,
    rating FLOAT NOT NULL,
    comment TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE
);
