CREATE TABLE group_security_privileges (
  group_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  period_security_id BIGINT UNSIGNED NOT NULL,
  role VARCHAR(20) NOT NULL,
  PRIMARY KEY(group_id, period_security_id),
  INDEX group_security_priveleges_FKIndex1(group_id),
  INDEX group_security_priveleges_FKIndex3(period_security_id)
)
ENGINE=InnoDB;

CREATE TABLE jm_user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  email VARCHAR(45) NOT NULL,
  fname VARCHAR(20) NOT NULL,
  lname VARCHAR(45) NOT NULL,
  phone VARCHAR(20) NULL,
  passwd VARCHAR(45) NOT NULL,
  comments VARCHAR(255) NULL,
  regdate DATE NOT NULL,
  uid VARCHAR(45) NULL,
  valid TINYINT UNSIGNED NOT NULL DEFAULT 0,
  role INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  UNIQUE INDEX subjects_email(email),
  INDEX subjects_name(lname, fname)
)
ENGINE=InnoDB;

CREATE TABLE market_groups (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  group_name VARCHAR(20) NOT NULL,
  PRIMARY KEY(id)
)
ENGINE=InnoDB;

CREATE TABLE offer_book (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  subject_id BIGINT UNSIGNED NOT NULL,
  pricelevel_id INTEGER UNSIGNED NOT NULL,
  offer_type TINYINT UNSIGNED NOT NULL,
  offer_units INTEGER UNSIGNED NOT NULL,
  offer_status VARCHAR(20) NOT NULL DEFAULT 0,
  entry_type VARCHAR(20) NOT NULL,
  time_entry BIGINT UNSIGNED NOT NULL,
  time_changestatus BIGINT UNSIGNED NULL,
  PRIMARY KEY(id),
  INDEX offer_book_FKIndex1(pricelevel_id),
  INDEX offer_book_FKIndex2(subject_id)
)
ENGINE=InnoDB;

CREATE TABLE periods (
  session_id INTEGER UNSIGNED NOT NULL,
  period_id INTEGER UNSIGNED NOT NULL,
  market_type VARCHAR(20) NOT NULL,
  duration BIGINT UNSIGNED NOT NULL,
  open_delay BIGINT UNSIGNED NOT NULL DEFAULT 0,
  start_time DATETIME NULL,
  end_time DATETIME NULL,
  PRIMARY KEY(session_id, period_id),
  INDEX periods_FKIndex1(session_id)
)
ENGINE=InnoDB;

CREATE TABLE period_cash_initials (
  session_id INTEGER UNSIGNED NOT NULL,
  period_id INTEGER UNSIGNED NOT NULL,
  group_id INTEGER UNSIGNED NOT NULL,
  cash_allocation FLOAT NOT NULL,
  PRIMARY KEY(session_id, period_id, group_id),
  INDEX period_intials_FKIndex1(session_id, period_id),
  INDEX period_cash_initials_FKIndex2(group_id)
)
ENGINE=InnoDB;

CREATE TABLE period_securities (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  session_id INTEGER UNSIGNED NOT NULL,
  period_id INTEGER UNSIGNED NOT NULL,
  security_id INTEGER UNSIGNED NOT NULL,
  tick_price FLOAT NOT NULL,
  min_price FLOAT NOT NULL,
  max_price FLOAT NOT NULL,
  duration BIGINT UNSIGNED NOT NULL,
  open_delay BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX period_securities_FKIndex2(security_id),
  UNIQUE INDEX period_securities_PK(period_id, session_id, security_id),
  INDEX period_securities_FKIndex3(session_id, period_id)
)
ENGINE=InnoDB;

CREATE TABLE period_security_initials (
  period_security_id BIGINT UNSIGNED NOT NULL,
  group_id INTEGER UNSIGNED NOT NULL,
  security_units INTEGER NOT NULL,
  PRIMARY KEY(period_security_id, group_id),
  INDEX period_security_intials_FKIndex1(period_security_id),
  INDEX period_security_initials_FKIndex2(group_id)
)
ENGINE=InnoDB;

CREATE TABLE rules (
  session_id INTEGER UNSIGNED NOT NULL,
  period_id INTEGER UNSIGNED NOT NULL,
  group_id INTEGER UNSIGNED NOT NULL,
  bankruptcy_fn VARCHAR(45) NOT NULL,
  payoff_fn VARCHAR(45) NOT NULL,
  bankruptcy_cutoff FLOAT NOT NULL,
  addCash TINYINT NOT NULL,
  UNIQUE INDEX rules_PK2751(session_id, period_id, group_id),
  INDEX rules_FKIndex2(group_id),
  INDEX rules_FKIndex4(session_id, period_id)
)
ENGINE=InnoDB;

CREATE TABLE securities (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  security_name VARCHAR(45) NOT NULL,
  PRIMARY KEY(id)
)
ENGINE=InnoDB;

CREATE TABLE security_pricelevels (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  period_security_id BIGINT UNSIGNED NOT NULL,
  price_level FLOAT NOT NULL,
  PRIMARY KEY(id),
  INDEX security_pricelevels_FKIndex1(period_security_id)
)
ENGINE=InnoDB;

CREATE TABLE security_rules (
  period_security_id BIGINT UNSIGNED NOT NULL,
  group_id INTEGER UNSIGNED NULL,
  shortsale_constraint FLOAT NOT NULL,
  add_surplus TINYINT UNSIGNED NULL,
  add_dividend TINYINT UNSIGNED NULL,
  INDEX security_rules_FKIndex3(period_security_id),
  UNIQUE INDEX security_rules_PK2757(period_security_id, group_id),
  INDEX security_rules_FKIndex2(group_id)
)
ENGINE=InnoDB;

CREATE TABLE sessions (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  experimenter_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(45) NOT NULL,
  num_periods INTEGER UNSIGNED NOT NULL,
  num_traders INTEGER UNSIGNED NOT NULL,
  open_delay BIGINT UNSIGNED NOT NULL,
  start_time DATETIME NULL,
  end_time DATETIME NULL,
  session_status VARCHAR(20) NOT NULL,
  def LONGTEXT NOT NULL,
  PRIMARY KEY(id),
  INDEX sessions_FKIndex1(experimenter_id)
)
ENGINE=InnoDB;

CREATE TABLE subject_cash_holdings (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  session_id INTEGER UNSIGNED NOT NULL,
  period_id INTEGER UNSIGNED NOT NULL,
  subject_id BIGINT UNSIGNED NOT NULL,
  cash_holding FLOAT NOT NULL,
  timestamp BIGINT NULL,
  PRIMARY KEY(id),
  INDEX subject_cash_holdings_FKIndex1(subject_id),
  INDEX subject_cash_holdings_FKIndex2(session_id, period_id)
)
ENGINE=InnoDB;

CREATE TABLE subject_groups (
  session_id INTEGER UNSIGNED NOT NULL,
  period_id INTEGER UNSIGNED NOT NULL,
  subject_id BIGINT UNSIGNED NOT NULL,
  group_id INTEGER UNSIGNED NOT NULL,
  INDEX subject_roles_FKIndex1(subject_id),
  INDEX subject_roles_FKIndex2(session_id, period_id),
  UNIQUE INDEX subject_roles_PK2743(session_id, period_id, subject_id),
  INDEX subject_groups_FKIndex3(group_id)
)
ENGINE=InnoDB;

CREATE TABLE subject_payoffs (
  session_id INTEGER UNSIGNED NOT NULL,
  period_id INTEGER UNSIGNED NOT NULL,
  subject_id BIGINT UNSIGNED NOT NULL,
  payoff FLOAT NOT NULL,
  PRIMARY KEY(session_id, period_id, subject_id),
  INDEX subject_payoffs_FKIndex1(subject_id),
  INDEX subject_payoffs_FKIndex2(session_id, period_id)
)
ENGINE=InnoDB;

CREATE TABLE subject_security_holdings (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  period_security_id BIGINT UNSIGNED NOT NULL,
  subject_id BIGINT UNSIGNED NOT NULL,
  security_units INTEGER NOT NULL,
  timestamp BIGINT NOT NULL,
  PRIMARY KEY(id),
  INDEX subject_security_holdings_FKIndex1(subject_id),
  INDEX subject_security_holdings_FKIndex2(period_security_id)
)
ENGINE=InnoDB;

CREATE TABLE ticker_tape (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  offer_book_id BIGINT UNSIGNED NOT NULL,
  units_changed INTEGER UNSIGNED NOT NULL,
  offer_status VARCHAR(20) NOT NULL,
  time_stamp BIGINT NOT NULL,
  PRIMARY KEY(id),
  INDEX ticker_tape_FKIndex1(offer_book_id)
)
ENGINE=InnoDB;

CREATE TABLE transaction_book (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  units INTEGER UNSIGNED NOT NULL,
  time_entry BIGINT UNSIGNED NOT NULL,
  price FLOAT NOT NULL,
  PRIMARY KEY(id)
)
ENGINE=InnoDB;

CREATE TABLE transaction_sides (
  transaction_id INTEGER UNSIGNED NOT NULL,
  offer_id BIGINT UNSIGNED NOT NULL,
  offer_type TINYINT UNSIGNED NOT NULL DEFAULT 0,
  units_contributed INTEGER UNSIGNED NOT NULL,
  UNIQUE INDEX transaction_parties_index2411(transaction_id, offer_id),
  INDEX transaction_parties_FKIndex2(transaction_id),
  INDEX transaction_sides_FKIndex2(offer_id)
)
ENGINE=InnoDB;

