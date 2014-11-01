SELECT * FROM Books ORDER BY ISBN;
SELECT * FROM Customers ORDER BY login;
SELECT * FROM Orders ORDER BY order_id;
SELECT * FROM Feedbacks ORDER BY date;
SELECT * FROM Likes;

-- INSERT INTO Books VALUES ('isbn 15C','title 140C','authors 140C','publisher 140C',year_of_pub INT,copies_avail INT,price REAL,'format 9C','keywords 140C','subject 140C');
INSERT INTO Books VALUES ('978-0590353427','Harry Potter and the Sorcerers Stone','J.K. Rowling','Scholastic',1999,100,6.92,'softcover','Harry Potter','Children');
INSERT INTO Books VALUES ('978-0439064873','Harry Potter and the Chamber of Secrets','J.K. Rowling','Scholastic',2000,100,6.64,'softcover','Harry Potter','Children');
INSERT INTO Books VALUES ('978-0439136365','Harry Potter and the Prisoner of Azkaban','J.K. Rowling','Scholastic',2001,100,7.85,'softcover','Harry Potter','Children');
INSERT INTO Books VALUES ('978-0439139601','Harry Potter and the Goblet of Fire','J.K. Rowling','Scholastic',2002,100,8.99,'softcover','Harry Potter','Children');
INSERT INTO Books VALUES ('978-0439358071','Harry Potter and the Order of the Phoenix','J.K. Rowling','Scholastic',2004,100,8.93,'softcover','Harry Potter','Children');
INSERT INTO Books VALUES ('978-0439785969','Harry Potter and the Half-Blood Prince','J.K. Rowling','Scholastic',2006,100,7.86,'softcover','Harry Potter','Children');
INSERT INTO Books VALUES ('978-0545139700','Harry Potter and the Deathly Hallows','J.K. Rowling','Scholastic',2009,100,9.81,'softcover','Harry Potter','Children');
INSERT INTO Books VALUES ('978-1597775083','The Theory of Everything','Stephen W Hawking','Phoenix Books',2006,100,21.73,'hardcover','Hawking','Science');
INSERT INTO Books VALUES ('978-1631060243','The Complete Works of William Shakespeare','William Shakespeare','Race Point Publishing',2014,100,25.66,'hardcover','Shakespeare','Literature');
INSERT INTO Books VALUES ('978-0486411217','The Republic','Plato','Dover Publications',2000,100,3.50,'softcover','Plato','Philosophy');

-- INSERT INTO Customers VALUES ('login 20C','password 20C','name 100C','credit_card_no 20C','address 140C',phone BIGINT);
INSERT INTO Customers VALUES ('super95','password1','Infinity Lim','1234567891011120','123 Amoy Street',99991230);
INSERT INTO Customers VALUES ('sexyboy99','password1','Legend Goh','1234567891011121','456 Arab Road',99991231);
INSERT INTO Customers VALUES ('throw322','password1','Yodon Wan','1234567891011122','77 Sultan Drive',99991232);
INSERT INTO Customers VALUES ('director','password1','Malaysia Natasha','1234567891011123','8 Junction',99991233);
INSERT INTO Customers VALUES ('mail','password1','Shithead Smith','1234567891011124','9 Bukit Buttocks',99991234);
INSERT INTO Customers VALUES ('contact','password1','Tan Ku Ku','1234567891011125','100 Days of Summer Drive',99991235);
INSERT INTO Customers VALUES ('support','password1','Kong Tin Yew','1234567891011126','300 Spartans Avenue',99991236);
INSERT INTO Customers VALUES ('contactus','password1','Batman Bin Suparman','1234567891011127','22 Jump Street',99991237);
INSERT INTO Customers VALUES ('job','password1','Berger Macdonald','1234567891011128','23 Jump Street',99991238);
INSERT INTO Customers VALUES ('sale','password1','Chris P. Bacon','1234567891011129','2001 Space Odyssey Road',99991239);

-- INSERT INTO Orders VALUES ('user_id 20C','book_id 15C',order_id BIGINT,copies INT,date DATE,'status 10C');
INSERT INTO Orders VALUES ('super95','978-1597775083',1,2,'20141027','Delivered');
INSERT INTO Orders VALUES ('super95','978-1631060243',2,3,'20141028','Delivered');
INSERT INTO Orders VALUES ('job','978-0486411217',3,4,'20141028','Delivered');
INSERT INTO Orders VALUES ('sale','978-0486411217',4,1,'20141029','Delivered');
INSERT INTO Orders VALUES ('mail','978-0439358071',5,1,'20141101','Delivered');

-- INSERT INTO Feedbacks VALUES ('book_id 15C','user_id 20C',score INT,'comment 140C','date DATE',useless INT,useful INT,very_useful INT);
INSERT INTO Feedbacks VALUES ('978-1597775083','super95',2,'lousy book','20141026',1,2,0);
INSERT INTO Feedbacks VALUES ('978-1597775083','support',4,'decent','20141027',0,0,0);
INSERT INTO Feedbacks VALUES ('978-0486411217','job',6,'entertaining','20141027',0,1,0);
INSERT INTO Feedbacks VALUES ('978-0439358071','sale',7,'great','20141028',0,0,1);
INSERT INTO Feedbacks VALUES ('978-1631060243','sale',10,'best book ever','20141028',0,0,0);

-- INSERT INTO Likes VALUES ('book_id 15C','commenter_id 20C','liker_id 20C',rating INT);
INSERT INTO Likes VALUES ('978-1597775083','super95','director',0);
INSERT INTO Likes VALUES ('978-1597775083','super95','mail',1);
INSERT INTO Likes VALUES ('978-1597775083','super95','sexyboy99',1);
INSERT INTO Likes VALUES ('978-0439358071','sale','mail',2);
INSERT INTO Likes VALUES ('978-0486411217','job','throw322',1);