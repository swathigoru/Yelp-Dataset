/*CREATE STMTS */

CREATE TABLE users(
				user_id varchar2(30),
				user_name varchar2(50),
				PRIMARY KEY (user_id)
);

/* business table columns:
loc = city + state
checkin_count = sum of all checkins for that bid */
CREATE TABLE business(
    bid varchar2(30),
    full_address varchar2(255),
    open_status varchar2(10),
    breview_count number,
    bname varchar2(100),
    loc varchar2(60),
    bstars number,
    checkin_count number,
    PRIMARY KEY (bid)
);

CREATE TABLE categories(
    cat_name varchar2(50),
    subcat_name varchar2(40),
    bid varchar2(30),
    FOREIGN KEY (bid) REFERENCES business(bid) ON DELETE CASCADE
);

CREATE TABLE battributes(
    bid varchar2(30),
    att_name varchar2(255),
    PRIMARY KEY (bid, att_name),
    FOREIGN KEY (bid) REFERENCES business(bid) ON DELETE CASCADE
);
    
CREATE TABLE reviews(
				review_id varchar2(30),
				user_id varchar2(30),
                stars number,
                publish_date varchar2(10),
				bid varchar2(30),
				text clob,
                funny_votes number,
                useful_votes number,
                cool_votes number,
				PRIMARY KEY (review_id),
				FOREIGN KEY (bid) REFERENCES business(bid) ON DELETE CASCADE
);

CREATE TABLE business_hours(
    bid varchar2(30),
    dayname varchar2(10),
    openhr float,
    closehr float,
    loc varchar2(60),
    PRIMARY KEY (bid, dayname),
    FOREIGN KEY (bid) REFERENCES business(bid) ON DELETE CASCADE
);



CREATE INDEX cat_index
ON categories (cat_name);

CREATE INDEX sub_index
ON categories (subcat_name);

CREATE INDEX att_index
ON battributes (att_name);




 
 