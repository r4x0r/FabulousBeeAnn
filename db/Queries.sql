-- 1. check for unique username
	-- implement in java, query for row with login=username
	-- if row exists, then return error that login is already taken
	-- else insert into table

-- 2. ordering books
	-- obtain next order number; next entry should take this number
	SELECT MAX(order_id) + 1 AS next_order_id
	FROM Orders;
	-- after this just need to insert into table

-- 3. TBC