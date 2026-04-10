CREATE DATABASE  IF NOT EXISTS `passiton` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `passiton`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: passiton
-- ------------------------------------------------------
-- Server version	9.6.0

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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '33818208-0c20-11f1-8e9c-dcfb4800e5a5:1-166';

--
-- Table structure for table `deletedaccounts`
--

DROP TABLE IF EXISTS `deletedaccounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deletedaccounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `deleted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deletedaccounts`
--

LOCK TABLES `deletedaccounts` WRITE;
/*!40000 ALTER TABLE `deletedaccounts` DISABLE KEYS */;
INSERT INTO `deletedaccounts` VALUES (1,5,'bradleybalramcts@gmail.com','Bradley','Balram','2026-03-28 21:44:42'),(2,6,'bradleybalramcts@gmail.com','Bradley','Balram','2026-03-28 21:46:17');
/*!40000 ALTER TABLE `deletedaccounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblclaim`
--

DROP TABLE IF EXISTS `tblclaim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblclaim` (
  `claimid` int NOT NULL AUTO_INCREMENT,
  `request_id` int NOT NULL,
  `benefactor_id` int NOT NULL,
  `claimed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`claimid`),
  KEY `request_id` (`request_id`),
  KEY `benefactor_id` (`benefactor_id`),
  CONSTRAINT `tblclaim_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `tblrequest` (`requestid`),
  CONSTRAINT `tblclaim_ibfk_2` FOREIGN KEY (`benefactor_id`) REFERENCES `tblusers` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblclaim`
--

LOCK TABLES `tblclaim` WRITE;
/*!40000 ALTER TABLE `tblclaim` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblclaim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblitems`
--

DROP TABLE IF EXISTS `tblitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblitems` (
  `itemid` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL,
  `category` varchar(45) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`itemid`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblitems`
--

LOCK TABLES `tblitems` WRITE;
/*!40000 ALTER TABLE `tblitems` DISABLE KEYS */;
INSERT INTO `tblitems` VALUES (1,'How to Code','Books',1),(2,'Understanding Information Technology','Books',1),(3,'Fundamental of Hardware and Software','Books',1),(4,'The History of the Computer','Books',1),(5,'Screwdriver','Tools',1),(6,'Anti-Static Wrist Wrap','Tools',1),(7,'Goggles','Tools',1),(8,'Thermal Paste','Tools',1),(9,'Raspberry Pi','Computer Components',1),(10,'Breadboards','Computer Components',1),(11,'Microcontrollers','Computer Components',1);
/*!40000 ALTER TABLE `tblitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblrequest`
--

DROP TABLE IF EXISTS `tblrequest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblrequest` (
  `requestid` int NOT NULL AUTO_INCREMENT,
  `requester_id` int NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `category` varchar(45) NOT NULL,
  `urgency` enum('LOW','MEDIUM','HIGH') DEFAULT 'LOW',
  `status` enum('OPEN','CLAIMED','AGREED','SATISFIED') DEFAULT 'OPEN',
  `location` varchar(100) DEFAULT NULL,
  `swap_time` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `claimed_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT ((now() + interval 24 hour)),
  `benefactor_id` int DEFAULT NULL,
  `requester_confirmed` tinyint(1) DEFAULT '0',
  `benefactor_confirmed` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`requestid`),
  KEY `requester_id` (`requester_id`),
  KEY `benefactor_id` (`benefactor_id`),
  CONSTRAINT `tblrequest_ibfk_1` FOREIGN KEY (`requester_id`) REFERENCES `tblusers` (`user_id`),
  CONSTRAINT `tblrequest_ibfk_2` FOREIGN KEY (`benefactor_id`) REFERENCES `tblusers` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblrequest`
--

LOCK TABLES `tblrequest` WRITE;
/*!40000 ALTER TABLE `tblrequest` DISABLE KEYS */;
INSERT INTO `tblrequest` VALUES (4,1,'Fundamental of Hardware and Software','Books','HIGH','AGREED','School Cafeteria','12:00 to 12:15','2026-04-10 21:00:55','2026-04-10 21:06:07','2026-04-11 21:06:07',7,0,0),(5,7,'Goggles','Tools','LOW','CLAIMED',NULL,NULL,'2026-04-10 21:05:50','2026-04-10 21:13:29','2026-04-11 21:13:29',9,0,0),(6,3,'Understanding Information Technology','Books','LOW','OPEN',NULL,NULL,'2026-04-10 21:08:54',NULL,'2026-04-11 21:08:54',NULL,0,0),(7,3,'Microcontrollers','Computer Components','MEDIUM','CLAIMED','School Cafeteria','12:00 to 12:15','2026-04-10 21:08:59','2026-04-10 21:10:16','2026-04-11 21:10:16',7,0,0),(8,3,'How to Code','Books','HIGH','OPEN',NULL,NULL,'2026-04-10 21:09:03',NULL,'2026-04-11 21:09:03',NULL,0,0);
/*!40000 ALTER TABLE `tblrequest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblusers`
--

DROP TABLE IF EXISTS `tblusers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblusers` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblusers`
--

LOCK TABLES `tblusers` WRITE;
/*!40000 ALTER TABLE `tblusers` DISABLE KEYS */;
INSERT INTO `tblusers` VALUES (1,'sallyleftfieldcts@gmail.com','Sally','Leftfield','sally1'),(3,'joshuahowardcts@gmail.com','Joshua','Howard','howard1'),(7,'bradleybalramcts@gmail.com','Bradley','Balram','bradley1'),(9,'sarahcoppercts@gmail.com','Sarah','Copper','sarah1');
/*!40000 ALTER TABLE `tblusers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblusersupplies`
--

DROP TABLE IF EXISTS `tblusersupplies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblusersupplies` (
  `supplyid` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `itemid` int NOT NULL,
  `owned` tinyint(1) DEFAULT '0',
  `acquired_via` enum('MANUAL','TRADE') DEFAULT 'MANUAL',
  `acquired_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplyid`),
  UNIQUE KEY `unique_user_item` (`user_id`,`itemid`),
  KEY `itemid` (`itemid`),
  CONSTRAINT `tblusersupplies_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tblusers` (`user_id`),
  CONSTRAINT `tblusersupplies_ibfk_2` FOREIGN KEY (`itemid`) REFERENCES `tblitems` (`itemid`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblusersupplies`
--

LOCK TABLES `tblusersupplies` WRITE;
/*!40000 ALTER TABLE `tblusersupplies` DISABLE KEYS */;
INSERT INTO `tblusersupplies` VALUES (1,3,3,0,'MANUAL','2026-03-29 17:58:04'),(2,3,2,0,'MANUAL','2026-03-29 19:35:48'),(3,3,1,1,'MANUAL','2026-04-08 15:00:29'),(4,3,4,1,'MANUAL','2026-04-08 15:00:29'),(5,1,1,1,'MANUAL','2026-04-10 21:00:22'),(6,1,10,1,'MANUAL','2026-04-10 21:00:27'),(7,1,7,1,'MANUAL','2026-04-10 21:00:28'),(8,1,5,1,'MANUAL','2026-04-10 21:00:29'),(9,1,8,1,'MANUAL','2026-04-10 21:00:30'),(10,9,9,1,'MANUAL','2026-04-10 21:04:24'),(11,9,11,1,'MANUAL','2026-04-10 21:04:24'),(12,9,2,1,'MANUAL','2026-04-10 21:04:25'),(13,9,10,1,'MANUAL','2026-04-10 21:04:25'),(14,9,4,1,'MANUAL','2026-04-10 21:04:26'),(15,9,3,1,'MANUAL','2026-04-10 21:04:30'),(16,9,1,1,'MANUAL','2026-04-10 21:04:31'),(17,9,6,1,'MANUAL','2026-04-10 21:04:31'),(18,9,7,1,'MANUAL','2026-04-10 21:04:32'),(19,9,5,1,'MANUAL','2026-04-10 21:04:32'),(20,9,8,1,'MANUAL','2026-04-10 21:04:32'),(21,7,3,1,'MANUAL','2026-04-10 21:06:39'),(22,7,8,1,'MANUAL','2026-04-10 21:07:04'),(23,7,10,1,'MANUAL','2026-04-10 21:07:04'),(24,7,2,1,'MANUAL','2026-04-10 21:07:05'),(25,3,11,1,'MANUAL','2026-04-10 21:08:10'),(26,3,6,1,'MANUAL','2026-04-10 21:08:10'),(27,3,7,1,'MANUAL','2026-04-10 21:08:10'),(28,3,5,1,'MANUAL','2026-04-10 21:08:11');
/*!40000 ALTER TABLE `tblusersupplies` ENABLE KEYS */;
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

-- Dump completed on 2026-04-10 17:15:31
