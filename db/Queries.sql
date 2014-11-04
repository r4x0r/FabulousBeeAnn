-- 1. check for unique username
	-- implement in java, query for row with login=username
	-- if row exists, then return error that login is already taken
	-- else insert into table

-- 2. ordering books
	-- obtain next order number; next entry should take this number
	SELECT MAX(order_id) + 1 AS next_order_id
	FROM Orders;
	-- after this just need to insert into table

-- 3. checking user record. let user be "user3"
	-- account info
	SELECT name, credit_card_no, address, phone
	FROM Customers
	WHERE login = 'user3';

	-- history of orders
	SELECT order_id, book_id, copies, date, status
	FROM Orders
	WHERE user_id = 'user3';

	-- history of feedbacks
	SELECT book_id, score, comment, date, (useful + (very_useful*2.0)) AS sum_rates, (useless + useful +very_useful) AS num_rates
	FROM Feedbacks
	WHERE user_id = 'user3';

	-- history of liked feedbacks
	SELECT f.book_id, f.user_id, f.score, f.comment, f.date, (f.useful + (f.very_useful*2.0)) AS sum_rates, (f.useless + f.useful + f.very_useful) AS num_rates, l.rating
	FROM Feedbacks f, (SELECT book_id, commenter_id, rating
					   FROM Likes
					   WHERE liker_id = 'mail') l
	WHERE f.book_id = l.book_id
	AND f.user_id = l.commenter_id;

-- 4. TBC

-- SELECT * FROM Feedbacks;
-- SELECT * FROM Likes;