-- schema.sql
CREATE DATABASE dec_storage;
USE dec_storage;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  wallet_address VARCHAR(42),
  role VARCHAR(50) NOT NULL,           -- Doctor, Admin, Student
  department VARCHAR(100) NOT NULL,    -- Medical, IT, CSE
  clearance_level VARCHAR(20) NOT NULL -- High, Medium, Low
);

CREATE TABLE files_meta (
  id INT AUTO_INCREMENT PRIMARY KEY,
  owner_id INT NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  ipfs_cid VARCHAR(100) NOT NULL,
  policy_json TEXT NOT NULL,           -- {"role":"Doctor","dept":"Medical"}
  tx_hash VARCHAR(66),                 -- Ethereum tx hash
  uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE access_logs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  file_id INT NOT NULL,
  action VARCHAR(20) NOT NULL,         -- ALLOW / DENY
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (file_id) REFERENCES files_meta(id)
);