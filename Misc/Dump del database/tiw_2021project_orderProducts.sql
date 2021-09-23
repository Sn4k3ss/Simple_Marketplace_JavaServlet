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
-- Table structure for table `orderProducts`
--

DROP TABLE IF EXISTS `orderProducts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderProducts` (
  `orderId` int NOT NULL,
  `supplierId` int NOT NULL,
  `productId` int NOT NULL,
  `unitCost` float DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  PRIMARY KEY (`orderId`,`productId`,`supplierId`),
  KEY `fk_idProduct:orderProducts_idx` (`productId`),
  KEY `fk_idSupplier_orderProducts_idx` (`supplierId`),
  CONSTRAINT `fk_orderId_orderProducts` FOREIGN KEY (`orderId`) REFERENCES `orders` (`orderId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_productId_orderProducts` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`) ON UPDATE CASCADE,
  CONSTRAINT `fk_supplierId_orderProducts` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderProducts`
--

LOCK TABLES `orderProducts` WRITE;
/*!40000 ALTER TABLE `orderProducts` DISABLE KEYS */;
INSERT INTO `orderProducts` VALUES (11,1,20,449.99,1),(12,1,19,9.99,5),(12,1,33,8.99,1),(14,4,20,429.99,1),(15,2,34,7.99,1),(16,4,20,429.99,1),(17,4,20,429.99,1),(17,4,21,749.99,1),(18,1,22,19.99,2),(18,1,23,14.99,1),(19,2,21,694.99,1),(19,2,34,7.99,3),(20,4,20,429.99,3),(20,4,39,8.99,3);
/*!40000 ALTER TABLE `orderProducts` ENABLE KEYS */;
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

-- Dump completed on 2021-05-27 20:27:02
