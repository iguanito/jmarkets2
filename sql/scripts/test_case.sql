#create a session
INSERT INTO sessions (id, num_of_periods, timeout_length, is_template) VALUES(1, 8, 60, 'n'); 

#create periods for the session
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 1, 1020, 18:00:00, 18:15:00, 0);  
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 2, 1020, 60, 960, 0);   
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 3, 1020, 60, 960, 0);   
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 4, 1020, 60, 960, 0);   
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 5, 1020, 60, 960, 0);   
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 6, 1020, 60, 960, 0);   
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 7, 1020, 60, 960, 0);   
INSERT INTO periods (session_id, period_id, time_length, time_open, time_close, time_delay) VALUES(1, 8, 1020, 60, 960, 0);  


#create securities for trading
INSERT INTO securities (id, security_name) VALUES(1, 'Stock');  
INSERT INTO securities (id, security_name) VALUES(2, 'Bond'); 


INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(11, 1, 1, 1, 1, 1, 250, 900, NO, NO);  
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(12, 1, 1, 2, 1, 1, 100, 900, NO, NO);  
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(21, 1, 2, 1, 1, 1, 250, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(22, 1, 2, 2, 1, 1, 100, 900, NO, NO);    
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(31, 1, 3, 1, 1, 1, 250, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(32, 1, 3, 2, 1, 1, 100, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(41, 1, 4, 1, 1, 1, 250, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(42, 1, 4, 2, 1, 1, 100, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(51, 1, 5, 1, 1, 1, 250, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(52, 1, 5, 2, 1, 1, 100, 900, NO, NO);    
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(61, 1, 6, 1, 1, 1, 250, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(62, 1, 6, 2, 1, 1, 100, 900, NO, NO);    
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(71, 1, 7, 1, 1, 1, 250, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(72, 1, 7, 2, 1, 1, 100, 900, NO, NO);    
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(81, 1, 8, 1, 1, 1, 250, 900, NO, NO);   
INSERT INTO period_securities (id, session_id, period_id, security_id, tick_price, min_price, max_price, time_length, add_dividend, add_surplus) VALUES(82, 1, 8, 2, 1, 1, 100, 900, NO, NO);    


INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 1, 1, 11, 0); 
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 1, 1, 12, 0); 
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 2, 1, 21, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 2, 1, 22, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 3, 1, 31, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 3, 1, 32, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 4, 1, 41, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 4, 1, 42, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 5, 1, 51, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 5, 1, 52, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 6, 1, 61, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 6, 1, 62, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 7, 1, 71, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 7, 1, 72, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(1, 8, 1, 81, 0);  
INSERT INTO rules (role_id, periods_period_id, periods_session_id, payoff_fn_id, bankruptcy_cutoff) VALUES(2, 8, 1, 82, 0);  

INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(11, 1, 2, 10); 
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(11, 2, 2, 10);  
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(12, 1, 2, 10);  
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(12, 2, 2, 10);  
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(21, 1, 2, 10);  
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(21, 2, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(22, 1, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(22, 2, 2, 10); 
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(31, 1, 2, 10);  
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(31, 2, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(32, 1, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(32, 2, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(41, 1, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(41, 2, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(42, 1, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(42, 2, 2, 10); 
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(51, 1, 2, 10);  
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(51, 2, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(52, 1, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(52, 2, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(61, 1, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(61, 2, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(62, 1, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(62, 2, 2, 10);  
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(71, 1, 2, 10);   
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(71, 2, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(72, 1, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(72, 2, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(81, 1, 2, 10);    
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(81, 2, 2, 10);     
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(82, 1, 2, 10);     
INSERT INTO security_rules (period_securities_id, role_id, trading_rule, shortsale_constraint) VALUES(82, 2, 2, 10); 



INSERT INTO market_roles (id, role_name) VALUES(1, 'Type1'); 
INSERT INTO market_roles (id, role_name) VALUES(2, 'Type2'); 


INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(51, , , , , , , , , ); 
INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(52, , , , , , , , , ); 
INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(53, , , , , , , , , ); 
INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(54, , , , , , , , , );   
INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(55, , , , , , , , , );   
INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(56, , , , , , , , , ); 
INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(57, , , , , , , , , );   
INSERT INTO subjects (id, email, fname, lname, phone, passwd, comments, regdate, uid, valid) VALUES(58, , , , , , , , , );   


INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 1, 1, 400);  
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 1, 2, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 2, 1, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 2, 2, 400);  
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 3, 1, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 3, 2, 400);  
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 4, 1, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 4, 2, 400);  
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 5, 1, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 5, 2, 400);  
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 6, 1, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 6, 2, 400);  
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 7, 1, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 7, 2, 400);  
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 8, 1, 400);    
INSERT INTO period_cash_initials (session_id, period_id, role_id, cash_allocation) VALUES(1, 8, 2, 400);  


INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(11, 1, 6);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(12, 1, 8);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(11, 2, 4);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(12, 2, 12);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(21, 1, 6);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(22, 1, 8);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(21, 2, 4);     
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(22, 2, 12);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(31, 1, 6);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(32, 1, 8);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(31, 2, 4);     
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(32, 2, 12);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(41, 1, 6);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(42, 1, 8);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(41, 2, 4);     
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(42, 2, 12);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(51, 1, 6);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(52, 1, 8);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(51, 2, 4);     
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(52, 2, 12);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(61, 1, 6);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(62, 1, 8);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(61, 2, 4);     
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(62, 2, 12);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(71, 1, 6);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(72, 1, 8);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(71, 2, 4);     
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(72, 2, 12);  
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(81, 1, 6);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(82, 1, 8);    
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(81, 2, 4);     
INSERT INTO period_security_initials (period_security_id, role_id, security_units) VALUES(82, 2, 12);  


INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 51, 1); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 52, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 52, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 52, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 52, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 52, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 52, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 52, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 52, 2); 
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 53, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 53, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 53, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 53, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 53, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 53, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 53, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 53, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 54, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 54, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 54, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 54, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 54, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 54, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 54, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 54, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 55, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 55, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 55, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 55, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 55, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 55, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 55, 1);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 55, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 56, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 56, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 56, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 56, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 56, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 56, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 56, 2);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 56, 2);  
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 57, 1);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 57, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 57, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 57, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 57, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 57, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 57, 1);   
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 57, 1);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 1, 58, 2);     
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 2, 58, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 3, 58, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 4, 58, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 5, 58, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 6, 58, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 7, 58, 2);    
INSERT INTO subject_roles (session_id, period_id, subject_id, role_id) VALUES(1, 8, 58, 2);   

INSERT INTO payoff_functions (id, function_name) VALUES(11, 'pay_p1_t1'); 
INSERT INTO payoff_functions (id, function_name) VALUES(12, 'pay_p1_t2'); 
INSERT INTO payoff_functions (id, function_name) VALUES(21, 'pay_p2_t1');   
INSERT INTO payoff_functions (id, function_name) VALUES(22, 'pay_p2_t2'); 
INSERT INTO payoff_functions (id, function_name) VALUES(31, 'pay_p3_t1');   
INSERT INTO payoff_functions (id, function_name) VALUES(32, 'pay_p3_t2'); 
INSERT INTO payoff_functions (id, function_name) VALUES(41, 'pay_p4_t1');   
INSERT INTO payoff_functions (id, function_name) VALUES(42, 'pay_p4_t2'); 
INSERT INTO payoff_functions (id, function_name) VALUES(51, 'pay_p5_t1');   
INSERT INTO payoff_functions (id, function_name) VALUES(52, 'pay_p5_t2'); 
INSERT INTO payoff_functions (id, function_name) VALUES(61, 'pay_p6_t1');   
INSERT INTO payoff_functions (id, function_name) VALUES(62, 'pay_p6_t2'); 
INSERT INTO payoff_functions (id, function_name) VALUES(71, 'pay_p7_t1');   
INSERT INTO payoff_functions (id, function_name) VALUES(72, 'pay_p7_t2'); 
INSERT INTO payoff_functions (id, function_name) VALUES(81, 'pay_p8_t1');   
INSERT INTO payoff_functions (id, function_name) VALUES(82, 'pay_p8_t2'); 

