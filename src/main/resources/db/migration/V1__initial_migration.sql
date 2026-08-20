CREATE TABLE users
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE addresses
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    zip VARCHAR(255) NOT NULL,
    user_id BIGINT,

    CONSTRAINT addresses_users_id_fk
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);

CREATE TABLE profiles
(
    id BIGINT PRIMARY KEY,
    phone_number VARCHAR(255),
    date_of_birth DATE,
    bio VARCHAR(500),
    loyalty_points INT,

    CONSTRAINT profiles_users_id_fk
        FOREIGN KEY (id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE TABLE tags
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE user_tags
(
    user_id BIGINT NOT NULL,
    tag_id INT NOT NULL,

    PRIMARY KEY(user_id, tag_id),

    CONSTRAINT user_tags_users_fk
        FOREIGN KEY(user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT user_tags_tags_fk
        FOREIGN KEY(tag_id)
            REFERENCES tags(id)
            ON DELETE CASCADE
);

CREATE TABLE categories
(
    id TINYINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE products
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    category_id TINYINT,

    CONSTRAINT products_categories_fk
        FOREIGN KEY(category_id)
            REFERENCES categories(id)
);