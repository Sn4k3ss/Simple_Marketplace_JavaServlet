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
-- Table structure for table `shippingPolicy`
--

DROP TABLE IF EXISTS `shippingPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shippingPolicy` (
  `supplierId` int NOT NULL,
  `range` int NOT NULL,
  `minItem` int NOT NULL,
  `maxItem` int DEFAULT NULL,
  `price` float NOT NULL,
  PRIMARY KEY (`supplierId`,`range`),
  CONSTRAINT `fk_supplierId_shippingPolicy` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shippingPolicy`
--

LOCK TABLES `shippingPolicy` WRITE;
/*!40000 ALTER TABLE `shippingPolicy` DISABLE KEYS */;
INSERT INTO `shippingPolicy` VALUES (1,1,1,3,9.99),(1,2,4,10,19.99),(1,3,11,NULL,29.99),(2,1,1,5,9.99),(2,2,6,10,14.99),(2,3,11,NULL,19.99),(3,1,1,3,19.99),(3,2,4,7,24.99),(3,3,8,12,29.99),(3,4,13,NULL,39.99),(4,1,1,5,9.98),(4,2,6,NULL,19.98),(5,1,1,NULL,4.99),(6,1,1,3,9.99),(6,2,4,NULL,14.99);
/*!40000 ALTER TABLE `shippingPolicy` ENABLE KEYS */;
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

-- Dump completed on 2021-05-27 20:27:01
