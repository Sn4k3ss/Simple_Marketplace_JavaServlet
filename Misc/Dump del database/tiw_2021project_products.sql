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
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `productId` int NOT NULL AUTO_INCREMENT,
  `productName` varchar(65) NOT NULL,
  `productDescription` varchar(45) DEFAULT NULL,
  `categoryId` int DEFAULT NULL,
  `photoPath` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`productId`),
  KEY `fk_idCat_idx` (`categoryId`),
  CONSTRAINT `fk_catId` FOREIGN KEY (`categoryId`) REFERENCES `productsCategory` (`categoryId`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Nike Air Max 270 WHITE EU 46','Sneakers troppo belle',9,'nike-air-max-270-white.jpeg'),(14,'Eastpak - Astuccio','Astuccio per tenere le penne',13,'eastpak-astuccio.jpg'),(15,'BIC - Multicolor pack','Pacco da 10 penne nere, blu e rosse',13,'bic-multicolor-pen.jpg'),(16,'Tratto - Evidenziatore giallo','Evidenziatore giallo per EVIDENZIARE il testo',13,'tratto-evidenziatore-giallo.jpg'),(17,'Tratto - Evidenziatore verde','Evidenziatore giallo per EVIDENZIARE il testo',13,'tratto-evidenziatore-verde.jpg'),(18,'Tratto - Evidenziatore rosa','Evidenziatore giallo per EVIDENZIARE il testo',13,'tratto-evidenziatore-rosa.jpg'),(19,'BIC - Fogli di protocollo - Pacco da 100','100 fogli di protocollo per le tue scritture ',13,'bic-fogli-protocollo.jpg'),(20,'Samsung TV RU8000 55\"','Televisore fantastico crystalUHD',4,'samsung-ru-8000-55.jpeg'),(21,'LG TV NanoCell 49\"','Televisore fantastico con tecnologia nanoIPS',4,'lg-nanocell-49.jpg'),(22,'JK Rowling - Harry Potter e La Pietra Filosofale','I libro della saga',1,'harry-potter-pietra.jpg'),(23,'JK Rowling - Harry Potter e La Camera dei Segreti','II libro della saga',1,'harry-potter-camera.jpg'),(24,'JK Rowling - Harry Potter e Il Prigioniero di Azkaban','III libro della saga',1,'harry-potter-prigioniero.jpg'),(25,'JK Rowling - Harry Potter e Il Calice di Fuoco','IV libro della saga',1,'harry-potter-calice.jpg'),(26,'JK Rowling - Harry Potter e l\'Ordine della Fenice','V libro della saga',1,'harry-potter-ordine.jpg'),(27,'JK Rowling - Harry Potter e il Principe Mezzosangue','VI libro della saga',1,'harry-potter-principe.jpg'),(28,'JK Rowling - Harry Potter e I Doni della Morte','VII e ultimo libro della saga',1,'harry-potter-doni.jpg'),(29,'Umberto Galimberti - Le cose dell\'amore','Libro pubblicato nel 2004',1,NULL),(30,'Umberto Galimberti - L\'ospite inquietante','Libro pubblicato nel 2007',1,NULL),(31,'Umberto Galimberti - La parola ai giovani','Libro pubblicato nel 2018',1,NULL),(32,'Umberto Galimberti - L\'uomo nell\'età della tecnica','Libro pubblicato nel 2011',1,'umberto-galimberti-uomo-nell-eta-della-tecnica.jpeg'),(33,'Oasis - Definitely Maybe','I album degli Oasis',2,'oasis-definetly-maybe-album-cover.jpg'),(34,'Oasis - (What\'s the story) Morning Glory?','II album degli Oasis',2,'oasis-morning-glory-album-cover.jpg'),(35,'Oasis - Be Here Now','III album degli Oasis',2,'oasis-be-here-now-album-cover.jpg'),(36,'Oasis - Standing on the Shoulder of Giants','IV album degli Oasis',2,'oasis-standing-on-the-shoulder-giants-album-cover.jpg'),(37,'Oasis - Heathen Chemistry','V album degli Oasis',2,'oasis-heathen-chemistry-album-cover.jpeg'),(38,'Oasis - Don\'t Believe The Truth','VI album degli Oasis',2,'oasis-dont-believe-the-truth-album-cover.jpg'),(39,'Oasis - Dig Out Your Soul','VII album degli Oasis',2,'oasis-dig-out-your-soul-album-cover.jpg'),(40,'Oasis - Familiar to Millions','The great live at Wembley Stadium - 2000',2,'oasis-familiar-to-millions-album-cover.jpeg');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
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
