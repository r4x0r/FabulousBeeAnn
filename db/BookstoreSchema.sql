-- CREATE DATABASE IF NOT EXISTS Bookstore;

-- USE Bookstore;


CREATE TABLE Books (
	ISBN CHAR(15) PRIMARY KEY,
	title CHAR(140),
	authors	CHAR(140),
	publisher CHAR(140),
	year_of_pub DATE,
	copies_avail INTEGER,
	price REAL,
	format CHAR(9), -- CHECK(format="hardcover" or format="softcover"),
	keywords CHAR(140),
	subject CHAR(140)
);

CREATE TABLE Customers (
	login CHAR(20) PRIMARY KEY,
	password CHAR(20),
	name CHAR(100),
	credit_card_no CHAR(20),
	address CHAR(140),
	phone BIGINT
);

CREATE TABLE Orders (
	user_id CHAR(20),
	book_id CHAR(15),
	order_id BIGINT,
	copies INTEGER,
	date DATE,
	status CHAR(10), -- CHECK(status="Confirmed" or status="Shipping" or status = "Pending" or status="Delivered"),
	PRIMARY KEY (user_id, book_id, order_id),
	FOREIGN KEY (user_id) REFERENCES Customers(login),
	FOREIGN KEY (book_id) REFERENCES Books(ISBN)
);

CREATE TABLE Feedbacks (
	book_id CHAR(15),
	user_id CHAR(20),
	score INTEGER,
	comment CHAR(140),
	date DATE,
	useless INTEGER, -- DEFAULT 0,
	useful INTEGER, -- DEFAULT 0,
	very_useful INTEGER, -- DEFAULT 0,
	PRIMARY KEY (book_id, user_id),
	FOREIGN KEY (book_id) REFERENCES Books(ISBN),
	FOREIGN KEY (user_id) REFERENCES Customers(login)
);

CREATE TABLE Likes (
	book_id CHAR(15),
	commenter_id CHAR(20),
	liker_id CHAR(20),
	rating INTEGER, -- CHECK(rating=0 or rating=1 or rating=2),
	PRIMARY KEY (book_id, commenter_id, liker_id),
	FOREIGN KEY (book_id, commenter_id) REFERENCES Feedbacks(book_id, user_id),
	FOREIGN KEY (liker_id) REFERENCES Customers(login)
);
