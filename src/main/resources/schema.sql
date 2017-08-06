CREATE DATABASE `QuidMate`;

USE `QuidMate`;

CREATE TABLE `TransactionType` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `TransactionLabel` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int,
  `name` varchar(30) NOT NULL,
  `pattern` varchar(250) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `Transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `type_id` int NOT NULL,
  `description` varchar(255) NOT NULL,
  `debit_credit` double DEFAULT 0.0,
  `balance` double DEFAULT 0.0,
  PRIMARY KEY (`id`),
  FOREIGN KEY (type_id) REFERENCES TransactionType(id)
);

CREATE TABLE `TransactionLabelList` (
  `transaction_id` int NOT NULL,
  `label_id` int NOT NULL ,
  PRIMARY KEY (`transaction_id`, `label_id`),
  FOREIGN KEY (transaction_id) REFERENCES Transaction(id) ON DELETE CASCADE,
  FOREIGN KEY (label_id) REFERENCES TransactionLabel(id)
);

INSERT INTO TransactionType (type) VALUES ('CARD_PAYMENT');
INSERT INTO TransactionType (type) VALUES ('CASH_AVAILABILITY_-PCAS');
INSERT INTO TransactionType (type) VALUES ('CASH_WDL');
INSERT INTO TransactionType (type) VALUES ('CASH_WDL_REV');
INSERT INTO TransactionType (type) VALUES ('CASHBACK');
INSERT INTO TransactionType (type) VALUES ('CHEQUE_PAID_IN');
INSERT INTO TransactionType (type) VALUES ('CLEARED_CHEQUE');
INSERT INTO TransactionType (type) VALUES ('CREDIT_IN');
INSERT INTO TransactionType (type) VALUES ('DEPOSIT');
INSERT INTO TransactionType (type) VALUES ('DD');
INSERT INTO TransactionType (type) VALUES ('FAST_PAYMENT');
INSERT INTO TransactionType (type) VALUES ('FEES');
INSERT INTO TransactionType (type) VALUES ('INTEREST');
INSERT INTO TransactionType (type) VALUES ('MONTHLY_ACCOUNT_FEE');
INSERT INTO TransactionType (type) VALUES ('NON-STERLING_PURCHASE_FEE');
INSERT INTO TransactionType (type) VALUES ('PAYMENTS');
INSERT INTO TransactionLabel VALUES (1, null, 'Bills', 'SKY|EDF|E\\.ON');
INSERT INTO TransactionLabel VALUES (2, 1, 'Electricity', 'EDF');