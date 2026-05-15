-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: thesisdefensedb
-- ------------------------------------------------------
-- Server version	8.0.44

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

--
-- Table structure for table `adviser`
--

DROP TABLE IF EXISTS `adviser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adviser` (
  `user_ID` int NOT NULL,
  PRIMARY KEY (`user_ID`),
  CONSTRAINT `adviser_ibfk_1` FOREIGN KEY (`user_ID`) REFERENCES `teachers` (`user_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adviser`
--

LOCK TABLES `adviser` WRITE;
/*!40000 ALTER TABLE `adviser` DISABLE KEYS */;
INSERT INTO `adviser` VALUES (2),(3),(4);
/*!40000 ALTER TABLE `adviser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `availability`
--

DROP TABLE IF EXISTS `availability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `availability` (
  `availability_ID` int NOT NULL AUTO_INCREMENT,
  `user_ID` int NOT NULL,
  `time_beg` time DEFAULT NULL,
  `time_end` time DEFAULT NULL,
  `days` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`availability_ID`),
  KEY `user_ID` (`user_ID`),
  CONSTRAINT `availability_ibfk_1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`user_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `availability`
--

LOCK TABLES `availability` WRITE;
/*!40000 ALTER TABLE `availability` DISABLE KEYS */;
INSERT INTO `availability` VALUES (1,2,'08:00:00','12:00:00','Monday, Wednesday'),(2,5,'08:00:00','12:00:00','Monday, Wednesday'),(3,3,'13:00:00','17:00:00','Tuesday, Thursday'),(4,6,'13:00:00','17:00:00','Tuesday, Thursday'),(5,4,'09:00:00','11:00:00','Friday'),(6,7,'09:00:00','11:00:00','Friday');
/*!40000 ALTER TABLE `availability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `defense_period`
--

DROP TABLE IF EXISTS `defense_period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `defense_period` (
  `period_ID` int NOT NULL AUTO_INCREMENT,
  `head_user_ID` int DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`period_ID`),
  KEY `head_user_ID` (`head_user_ID`),
  CONSTRAINT `defense_period_ibfk_1` FOREIGN KEY (`head_user_ID`) REFERENCES `head` (`user_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `defense_period`
--

LOCK TABLES `defense_period` WRITE;
/*!40000 ALTER TABLE `defense_period` DISABLE KEYS */;
INSERT INTO `defense_period` VALUES (1,NULL,'2026-05-20','2026-05-20'),(2,NULL,'2026-05-18','2026-05-18'),(3,NULL,'2026-05-17','2026-05-24'),(4,NULL,'2026-05-19','2026-05-19');
/*!40000 ALTER TABLE `defense_period` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `defense_schedule`
--

DROP TABLE IF EXISTS `defense_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `defense_schedule` (
  `schedule_ID` int NOT NULL AUTO_INCREMENT,
  `team_ID` int NOT NULL,
  `period_ID` int NOT NULL,
  `room_ID` int DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `indiv_verification` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`schedule_ID`),
  KEY `team_ID` (`team_ID`),
  KEY `period_ID` (`period_ID`),
  KEY `room_ID` (`room_ID`),
  CONSTRAINT `defense_schedule_ibfk_1` FOREIGN KEY (`team_ID`) REFERENCES `thesis_team` (`team_ID`),
  CONSTRAINT `defense_schedule_ibfk_2` FOREIGN KEY (`period_ID`) REFERENCES `defense_period` (`period_ID`),
  CONSTRAINT `defense_schedule_ibfk_3` FOREIGN KEY (`room_ID`) REFERENCES `room` (`room_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `defense_schedule`
--

LOCK TABLES `defense_schedule` WRITE;
/*!40000 ALTER TABLE `defense_schedule` DISABLE KEYS */;
INSERT INTO `defense_schedule` VALUES (12,4,4,1,NULL,NULL,0);
/*!40000 ALTER TABLE `defense_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `head`
--

DROP TABLE IF EXISTS `head`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `head` (
  `user_ID` int NOT NULL,
  PRIMARY KEY (`user_ID`),
  CONSTRAINT `head_ibfk_1` FOREIGN KEY (`user_ID`) REFERENCES `teachers` (`user_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `head`
--

LOCK TABLES `head` WRITE;
/*!40000 ALTER TABLE `head` DISABLE KEYS */;
INSERT INTO `head` VALUES (1);
/*!40000 ALTER TABLE `head` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `panel_assignment`
--

DROP TABLE IF EXISTS `panel_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `panel_assignment` (
  `panel_ID` int NOT NULL,
  `team_ID` int NOT NULL,
  PRIMARY KEY (`panel_ID`,`team_ID`),
  KEY `team_ID` (`team_ID`),
  CONSTRAINT `panel_assignment_ibfk_1` FOREIGN KEY (`panel_ID`) REFERENCES `panel_member` (`user_ID`),
  CONSTRAINT `panel_assignment_ibfk_2` FOREIGN KEY (`team_ID`) REFERENCES `thesis_team` (`team_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `panel_assignment`
--

LOCK TABLES `panel_assignment` WRITE;
/*!40000 ALTER TABLE `panel_assignment` DISABLE KEYS */;
INSERT INTO `panel_assignment` VALUES (5,1),(6,2),(5,4),(7,4),(7,5),(5,6);
/*!40000 ALTER TABLE `panel_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `panel_member`
--

DROP TABLE IF EXISTS `panel_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `panel_member` (
  `user_ID` int NOT NULL,
  PRIMARY KEY (`user_ID`),
  CONSTRAINT `panel_member_ibfk_1` FOREIGN KEY (`user_ID`) REFERENCES `teachers` (`user_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `panel_member`
--

LOCK TABLES `panel_member` WRITE;
/*!40000 ALTER TABLE `panel_member` DISABLE KEYS */;
INSERT INTO `panel_member` VALUES (5),(6),(7);
/*!40000 ALTER TABLE `panel_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `room_ID` int NOT NULL AUTO_INCREMENT,
  `room_code` varchar(20) NOT NULL,
  `building` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`room_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,'D204','Devesse Building'),(2,'D205','Devesse Building'),(3,'D206','Devesse Building');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `user_ID` int NOT NULL,
  `year_level` int DEFAULT NULL,
  `team_id` int DEFAULT NULL,
  PRIMARY KEY (`user_ID`),
  KEY `team_id` (`team_id`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`user_ID`) ON DELETE CASCADE,
  CONSTRAINT `student_ibfk_2` FOREIGN KEY (`team_id`) REFERENCES `thesis_team` (`team_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (8,2,1),(9,2,1),(10,2,1),(11,2,1),(12,2,1),(13,2,1),(14,2,2),(15,2,2),(51,2,NULL),(52,2,NULL),(53,2,NULL),(54,2,NULL),(55,2,NULL),(56,2,NULL),(57,2,NULL),(58,2,NULL),(59,2,NULL),(60,2,NULL),(61,2,NULL),(62,2,NULL),(63,2,NULL),(64,2,NULL),(65,2,NULL),(66,2,NULL),(67,2,NULL),(68,2,NULL),(69,2,NULL),(70,2,NULL),(71,2,NULL),(72,2,NULL),(73,2,NULL),(74,2,NULL),(75,2,NULL),(76,2,NULL),(77,2,NULL),(78,2,NULL),(79,2,NULL),(80,2,NULL),(81,2,NULL),(82,2,NULL),(83,2,NULL),(84,2,NULL),(85,2,NULL),(86,2,NULL),(87,2,NULL),(88,2,NULL),(89,2,NULL),(90,2,6),(91,2,NULL),(92,2,5),(93,2,4),(94,2,4),(95,2,4),(96,2,4),(97,2,4),(98,2,4),(99,2,4),(100,2,4);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `user_ID` int NOT NULL,
  `position` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_ID`),
  CONSTRAINT `teachers_ibfk_1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`user_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (1,'Department Head'),(2,'Instructor'),(3,'Professor'),(4,'Instructor'),(5,'Assistant Professor'),(6,'Professor'),(7,'Instructor');
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thesis_team`
--

DROP TABLE IF EXISTS `thesis_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thesis_team` (
  `team_ID` int NOT NULL AUTO_INCREMENT,
  `team_name` varchar(100) NOT NULL,
  `status` enum('PENDING','PASSED','FAILED','REDEFENSE') DEFAULT 'PENDING',
  `class_code` varchar(20) DEFAULT NULL,
  `remarks` text,
  `adviser_user_ID` int DEFAULT NULL,
  `head_user_ID` int DEFAULT NULL,
  PRIMARY KEY (`team_ID`),
  KEY `adviser_user_ID` (`adviser_user_ID`),
  KEY `head_user_ID` (`head_user_ID`),
  CONSTRAINT `thesis_team_ibfk_1` FOREIGN KEY (`adviser_user_ID`) REFERENCES `adviser` (`user_ID`),
  CONSTRAINT `thesis_team_ibfk_2` FOREIGN KEY (`head_user_ID`) REFERENCES `head` (`user_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thesis_team`
--

LOCK TABLES `thesis_team` WRITE;
/*!40000 ALTER TABLE `thesis_team` DISABLE KEYS */;
INSERT INTO `thesis_team` VALUES (1,'Group 6: Serenity','PENDING','IT311',NULL,2,1),(2,'Group 8: JobTrack','PASSED','IT312',NULL,3,1),(3,'Group 1: EcoScan','PENDING','IT311',NULL,4,1),(4,'404 Brain Not Found','PASSED','IT221','IT Project',4,NULL),(5,'Awacanation','PENDING','IT221','Offline Bus Booking',4,NULL),(6,'qwerty','PENDING','IT221','Program',2,NULL);
/*!40000 ALTER TABLE `thesis_team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_ID` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `availability` text,
  PRIMARY KEY (`user_ID`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Manny','Serafica','manny.head@slu.edu.ph',NULL),(2,'Alice','Guillermo','alice.adv@slu.edu.ph',NULL),(3,'Bob','Magno','bob.adv@slu.edu.ph',NULL),(4,'Charlie','Reyes','charlie.adv@slu.edu.ph',NULL),(5,'Diana','Cruz','diana.pan@slu.edu.ph',NULL),(6,'Edward','Luna','edward.pan@slu.edu.ph',NULL),(7,'Fiona','Santos','fiona.pan@slu.edu.ph',NULL),(8,'Rainiel Luis','Serafica','rain.serafica@slu.edu.ph',NULL),(9,'Lexbher','Lumiwes','lex.lumi@slu.edu.ph',NULL),(10,'Earvin','Cabanban','earv.cab@slu.edu.ph',NULL),(11,'Zymon','Ganaden','zy.gan@slu.edu.ph',NULL),(12,'Kisha','Abalos','kish.ab@slu.edu.ph',NULL),(13,'Jenny','Awacan','jen.awa@slu.edu.ph',NULL),(14,'Mark','Rivera','mark.riv@slu.edu.ph',NULL),(15,'Joy','Ramos','joy.ram@slu.edu.ph',NULL),(51,'Kisha','Abalos','kish.ab2@slu.edu.ph',NULL),(52,'Jenny','Awacan','jen.awa2@slu.edu.ph',NULL),(53,'Zymon','Ganaden','zy.gan2@slu.edu.ph',NULL),(54,'Mark','Rivera','mark.riv2@slu.edu.ph',NULL),(55,'Joy','Ramos','joy.ram2@slu.edu.ph',NULL),(56,'Leo','Garcia','leo.gar@slu.edu.ph',NULL),(57,'Nina','Castro','nina.cas@slu.edu.ph',NULL),(58,'Paul','Navarro','paul.nav@slu.edu.ph',NULL),(59,'Ella','Mendoza','ella.men@slu.edu.ph',NULL),(60,'Tom','Aquino','tom.aqu@slu.edu.ph',NULL),(61,'Kim','Salazar','kim.sal@slu.edu.ph',NULL),(62,'Josh','Bautista','josh.bau@slu.edu.ph',NULL),(63,'Anne','Diaz','anne.dia@slu.edu.ph',NULL),(64,'Ryan','Velasco','ryan.vel@slu.edu.ph',NULL),(65,'Kate','Morales','kate.mor@slu.edu.ph',NULL),(66,'Ivan','Pineda','ivan.pin@slu.edu.ph',NULL),(67,'Lara','Gutierrez','lara.gut@slu.edu.ph',NULL),(68,'Noel','Domingo','noel.dom@slu.edu.ph',NULL),(69,'Zara','Ferrer','zara.fer@slu.edu.ph',NULL),(70,'Mico','Padilla','mico.pad@slu.edu.ph',NULL),(71,'Bea','Serrano','bea.ser@slu.edu.ph',NULL),(72,'Evan','Chua','evan.chu@slu.edu.ph',NULL),(73,'Ivy','Tan','ivy.tan@slu.edu.ph',NULL),(74,'Sean','Uy','sean.uy@slu.edu.ph',NULL),(75,'Gina','Sy','gina.sy@slu.edu.ph',NULL),(76,'Carl','Lim','carl.lim@slu.edu.ph',NULL),(77,'Ruth','Go','ruth.go@slu.edu.ph',NULL),(78,'Neil','Co','neil.co@slu.edu.ph',NULL),(79,'Faith','Ang','faith.ang@slu.edu.ph',NULL),(80,'Liam','Santos','liam.san@slu.edu.ph',NULL),(81,'Mia','Reyes','mia.rey@slu.edu.ph',NULL),(82,'Noah','Cruz','noah.cru@slu.edu.ph',NULL),(83,'Ava','Luna','ava.lun@slu.edu.ph',NULL),(84,'Lucas','Perez','lucas.per@slu.edu.ph',NULL),(85,'Sofia','Torres','sofia.tor@slu.edu.ph',NULL),(86,'Ethan','Dizon','ethan.diz@slu.edu.ph',NULL),(87,'Isabella','Sotto','isabel.sot@slu.edu.ph',NULL),(88,'Mason','Vidal','mason.vid@slu.edu.ph',NULL),(89,'Amelia','Pascual','amel.pas@slu.edu.ph',NULL),(90,'James','Ocampo','james.oca@slu.edu.ph',NULL),(91,'Charlotte','Buan','char.buan@slu.edu.ph',NULL),(92,'Oliver','Guinto','olive.gui@slu.edu.ph',NULL),(93,'Harper','Laxamana','harp.lax@slu.edu.ph',NULL),(94,'Elijah','Mabini','elij.mab@slu.edu.ph',NULL),(95,'Evelyn','Rizal','eve.riz@slu.edu.ph',NULL),(96,'Daniel','Bonifacio','dan.boni@slu.edu.ph',NULL),(97,'Abigail','Aguinaldo','abig.agu@slu.edu.ph',NULL),(98,'Jacob','Luna','jaco.lun@slu.edu.ph',NULL),(99,'Emily','Jacinto','emily.jac@slu.edu.ph',NULL),(100,'Logan','Mabini','logan.mab@slu.edu.ph',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-15 16:27:19
