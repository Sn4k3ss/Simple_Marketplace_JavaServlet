-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: aws-db1.cq19dtt44nal.eu-south-1.rds.amazonaws.com    Database: tiw_2021project
-- ------------------------------------------------------
-- Server version	8.0.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `orderId` int NOT NULL AUTO_INCREMENT,
  `supplierId` int NOT NULL,
  `userId` int NOT NULL,
  `orderPlacementDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `userAddressId` int NOT NULL,
  `orderAmount` float NOT NULL,
  `shippingFees` float NOT NULL,
  `deliveryDate` date NOT NULL,
  PRIMARY KEY (`orderId`),
  KEY `fk_idUser_orders_idx` (`userId`),
  KEY `fk_idSupplier_orders_idx` (`supplierId`),
  KEY `fk_deliveryAddress_idx` (`userAddressId`),
  KEY `fk_userAddressId_orders_idx` (`userAddressId`,`userId`),
  KEY `fk_userId_userAddressId_orders` (`userId`,`userAddressId`),
  CONSTRAINT `fk_supplierId_orders` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`),
  CONSTRAINT `fk_userId_orders` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`),
  CONSTRAINT `fk_userId_userAddressId_orders` FOREIGN KEY (`userId`, `userAddressId`) REFERENCES `shippingAddresses` (`userId`, `userAddressId`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (11,1,1,'2021-05-17 13:11:13',1,449.99,0,'2021-05-21'),(12,1,1,'2021-05-17 13:21:00',2,58.94,0,'2021-05-22'),(14,4,1,'2021-05-18 15:31:15',1,429.99,0,'2021-05-23'),(15,2,1,'2021-05-18 21:08:42',1,17.98,9.99,'2021-05-23'),(16,4,1,'2021-05-19 13:01:19',2,429.99,0,'2021-05-24'),(17,4,1,'2021-05-19 13:11:31',1,1179.98,0,'2021-05-24'),(18,1,1,'2021-05-19 14:32:16',2,54.97,0,'2021-05-24'),(19,2,1,'2021-05-20 13:43:22',1,718.96,0,'2021-05-25'),(20,4,1,'2021-05-27 16:21:43',2,1316.94,0,'2021-06-01');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-27 20:27:00
