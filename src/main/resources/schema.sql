CREATE TABLE IF NOT EXISTS BUDGET
(
  id               INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  pay_amount       DOUBLE          NOT NULL,
  amount_left      DOUBLE          NOT NULL,
  spending_money   DOUBLE          NOT NULL,
  primary_saving   DOUBLE          NOT NULL,
  secondary_saving DOUBLE          NOT NULL,
  pay_period       DATE            NOT NULL
);


CREATE TABLE IF NOT EXISTS EXPENSE
(
  id        INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  expense   VARCHAR(45)     NOT NULL,
  amount    DOUBLE          NOT NULL,
  date      DATE            NOT NULL,
  completed TINYINT(1)      NOT NULL,
  budget_id INT,
  FOREIGN KEY (budget_id) REFERENCES BUDGET (id)
);