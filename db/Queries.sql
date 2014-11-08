-- !!! These are working SQL codes but to implement them requires JAVA to replace key parameters in the queries !!!

-- 1. check for unique username

	-- implement in java, query for row with login=username
	-- if row exists, then return error that login is already taken
	-- else insert into table


-- 2. ordering books

	-- obtain next order number; next entry should take this number
	SELECT MAX(order_id) + 1 AS next_order_id
	FROM Orders;

	-- after this just need to insert into orders table
	INSERT INTO Orders VALUES (user_id, book_id, new_order_id, copies_bought, date, status);

	-- remove the number of copies from books stock
	-- DO WE NEED TO DO THIS?? DOES NOT MAKE SENSE IF NUMBER GOES NEGATIVE
	UPDATE Books
	SET copies = copies - copies_bought
	WHERE ISBN = book_id;


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
	-- JAVA needs to perform total_ratings/num_ppl_rated to prevent division by zero in SQL
	SELECT book_id, score, comment, date, (useful + (very_useful*2.0)) AS total_ratings, (useless + useful +very_useful) AS num_ppl_rated
	FROM Feedbacks
	WHERE user_id = 'user3';

	-- history of liked feedbacks
	-- JAVA needs to perform total_ratings/num_ppl_rated to prevent division by zero in SQL
	SELECT f.book_id, f.user_id, f.score, f.comment, f.date, (f.useful + (f.very_useful*2.0)) AS total_ratings, (f.useless + f.useful + f.very_useful) AS num_ppl_rated, l.rating
	FROM Feedbacks f, (SELECT book_id, commenter_id, rating
					   FROM Likes
					   WHERE liker_id = 'user3') l
	WHERE f.book_id = l.book_id
	AND f.user_id = l.commenter_id;


-- 4. adding new book into bookstore.
	-- need to ask meihui what it means by "along with the number of new books that have arrived in the warehouse"
	INSERT INTO Books VALUES (isbn, title, authors, publisher, year_of_pub, copies_available, price, format, keywords, subject);





-- 9. for a specific book, top n most useful feedbacks.
	-- JAVA to display the top n results
	-- query below assumes book is 978-1597775083
	SELECT user_id, score, comment, date,
	(useful + (very_useful*2.0)) / (useless + useful + very_useful) AS avg_usefulness,
	useless + useful + very_useful AS num_of_voters
	FROM (SELECT * FROM Feedbacks
	      WHERE book_id = '978-1597775083'
		  AND (useless + useful + very_useful) > 0) book_comments
	ORDER BY avg_usefulness DESC;

-- 10. this is assrape difficult to query.
    -- recommend other books to user after he bought a book
	-- based on what other users have bought
	-- order by copies sold to users who have also bought the book he just bought

	-- SELECT * FROM 
	-- and a hero comes along
	-- with the strength to carry on 
	-- la la la la la la la ~ ~ ~


-- 11. statistics for each month's sale (popularity in terms of copies sold)

	-- list of m most popular books
	-- JAVA to display the top m books
	-- date must be precise i.e. cannot query for 2014-10-33
	-- query below assumes month of october
	-- problem with this query is ISBN/book_id column will appear twice. JAVA needs to display only one of them.
	select * from Orders;
	SELECT *
	FROM Books JOIN (SELECT book_id, SUM(copies) AS sold_this_mth
					 FROM Orders
					 WHERE date BETWEEN '2014-10-01' AND '2014-11-30'
					 GROUP BY book_id) Sale
	ON ISBN = book_id
	ORDER BY sold_this_mth DESC;

	-- list of m most popular authors
	-- JAVA to display the top m books
	SELECT authors, SUM(copies) AS sold_this_mth
	FROM Books JOIN (SELECT book_id, copies
					 FROM Orders
					 WHERE date BETWEEN '2014-10-01' AND '2014-11-30') Sale
	ON ISBN = book_id
	GROUP BY authors
	ORDER BY sold_this_mth DESC;

	-- list of m most popular publishers
	-- JAVA to display the top m books
	SELECT publisher, SUM(copies) AS sold_this_mth
	FROM Books JOIN (SELECT book_id, copies
					 FROM Orders
					 WHERE date BETWEEN '2014-10-01' AND '2014-11-30') Sale
	ON ISBN = book_id
	GROUP BY publisher
	ORDER BY sold_this_mth DESC;

----------------------------------------------------------------------------
	-- test query:
	-- UPDATE Orders
	-- SET book_id = '978-0590353427'
	-- WHERE book_id = '978-1631060243';