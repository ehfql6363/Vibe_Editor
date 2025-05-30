-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: vibeeditor
-- ------------------------------------------------------
-- Server version	8.4.5

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ai_post`
--

DROP TABLE IF EXISTS `ai_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_post_id` bigint DEFAULT NULL,
  `prompt_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_ai_provider_id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `post_type` enum('TECH_CONCEPT','TROUBLE_SHOOTING') DEFAULT NULL,
  `document_id` varchar(255) DEFAULT NULL,
  `content` text,
  `is_modified` tinyint(1) DEFAULT '0',
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`),
  KEY `IDX_AI_POST_USER` (`user_id`),
  KEY `IDX_AI_POST_PROMPT` (`prompt_id`),
  KEY `IDX_AI_POST_PARENT` (`parent_post_id`),
  KEY `ai_post_ibfk_4` (`user_ai_provider_id`),
  CONSTRAINT `ai_post_ibfk_1` FOREIGN KEY (`prompt_id`) REFERENCES `prompt` (`prompt_id`),
  CONSTRAINT `ai_post_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `ai_post_ibfk_3` FOREIGN KEY (`parent_post_id`) REFERENCES `ai_post` (`post_id`),
  CONSTRAINT `ai_post_ibfk_4` FOREIGN KEY (`user_ai_provider_id`) REFERENCES `user_ai_provider` (`user_ai_provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `ai_provider`
--

DROP TABLE IF EXISTS `ai_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_provider` (
  `ai_provider_id` bigint NOT NULL AUTO_INCREMENT,
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ai_provider_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_provider`
--

LOCK TABLES `ai_provider` WRITE;
/*!40000 ALTER TABLE `ai_provider` DISABLE KEYS */;
INSERT INTO `ai_provider` VALUES (1,'Anthropic','claude-3-7-sonnet-latest','2025-05-14 02:25:00','2025-05-15 01:34:14',0),(3,'OpenAI','gpt-4.1-mini','2025-05-14 02:25:00','2025-05-16 08:32:24',0),(4,'OpenAI','gpt-4o-mini','2025-05-14 02:25:00','2025-05-16 08:32:25',0);
/*!40000 ALTER TABLE `ai_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notion_database`
--

DROP TABLE IF EXISTS `notion_database`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notion_database` (
  `notion_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `database_name` varchar(255) NOT NULL,
  `database_uid` varchar(255) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`notion_id`),
  UNIQUE KEY `UK_NOTION_DB_UID` (`user_id`,`database_uid`),
  CONSTRAINT `notion_database_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notion_upload`
--

DROP TABLE IF EXISTS `notion_upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notion_upload` (
  `upload_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `notion_id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `upload_status` enum('SUCCESS','FAIL') DEFAULT NULL,
  `post_url` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`upload_id`),
  KEY `IDX_NOTION_UPLOAD_USER` (`user_id`),
  KEY `IDX_NOTION_UPLOAD_NOTION` (`notion_id`),
  KEY `IDX_NOTION_UPLOAD_POST` (`post_id`),
  CONSTRAINT `notion_upload_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `notion_upload_ibfk_2` FOREIGN KEY (`notion_id`) REFERENCES `notion_database` (`notion_id`),
  CONSTRAINT `notion_upload_ibfk_3` FOREIGN KEY (`post_id`) REFERENCES `ai_post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `options`
--

DROP TABLE IF EXISTS `options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options` (
  `option_id` bigint NOT NULL AUTO_INCREMENT,
  `option_name` enum('EMOJI','TONE') DEFAULT NULL,
  `value` varchar(255) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`option_id`),
  UNIQUE KEY `UK_OPTION_NAME_VALUE` (`option_name`,`value`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `options`
--

LOCK TABLES `options` WRITE;
/*!40000 ALTER TABLE `options` DISABLE KEYS */;
INSERT INTO `options` VALUES (1,'EMOJI','포함',0,'2025-05-09 01:22:50','2025-05-09 01:22:50'),(2,'EMOJI','제외',0,'2025-05-09 01:22:50','2025-05-09 01:22:50'),(3,'TONE','존댓말',0,'2025-05-09 01:22:50','2025-05-09 01:22:50'),(4,'TONE','반말',0,'2025-05-09 01:22:50','2025-05-09 01:22:50'),(5,'TONE','~해요 체',0,'2025-05-09 01:22:50','2025-05-09 01:22:50'),(6,'TONE','~했습니다 체',0,'2025-05-09 01:22:50','2025-05-09 01:22:50');
/*!40000 ALTER TABLE `options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prompt`
--

DROP TABLE IF EXISTS `prompt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prompt` (
  `prompt_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_prompt_id` bigint DEFAULT NULL,
  `template_id` bigint NOT NULL,
  `notion_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_ai_provider_id` bigint DEFAULT NULL,
  `prompt_name` varchar(255) NOT NULL,
  `post_type` enum('TECH_CONCEPT','TROUBLE_SHOOTING') DEFAULT NULL,
  `comment` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`prompt_id`),
  KEY `IDX_PROMPT_USER` (`user_id`),
  KEY `IDX_PROMPT_NOTION` (`notion_id`),
  KEY `IDX_PROMPT_TEMPLATE` (`template_id`),
  KEY `IDX_PROMPT_PARENT` (`parent_prompt_id`),
  KEY `prompt_ibfk_5` (`user_ai_provider_id`),
  CONSTRAINT `prompt_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `prompt_ibfk_2` FOREIGN KEY (`notion_id`) REFERENCES `notion_database` (`notion_id`),
  CONSTRAINT `prompt_ibfk_3` FOREIGN KEY (`template_id`) REFERENCES `template` (`template_id`),
  CONSTRAINT `prompt_ibfk_4` FOREIGN KEY (`parent_prompt_id`) REFERENCES `prompt` (`prompt_id`),
  CONSTRAINT `prompt_ibfk_5` FOREIGN KEY (`user_ai_provider_id`) REFERENCES `user_ai_provider` (`user_ai_provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prompt_attach`
--

DROP TABLE IF EXISTS `prompt_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prompt_attach` (
  `attach_id` bigint NOT NULL AUTO_INCREMENT,
  `prompt_id` bigint NOT NULL,
  `snapshot_id` bigint NOT NULL,
  `description` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`attach_id`),
  KEY `IDX_PROMPT_ATTACH_PROMPT` (`prompt_id`),
  KEY `IDX_PROMPT_ATTACH_SNAPSHOT` (`snapshot_id`),
  CONSTRAINT `prompt_attach_ibfk_1` FOREIGN KEY (`prompt_id`) REFERENCES `prompt` (`prompt_id`),
  CONSTRAINT `prompt_attach_ibfk_2` FOREIGN KEY (`snapshot_id`) REFERENCES `snapshot` (`snapshot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prompt_option`
--

DROP TABLE IF EXISTS `prompt_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prompt_option` (
  `prompt_option_id` bigint NOT NULL AUTO_INCREMENT,
  `prompt_id` bigint NOT NULL,
  `option_id` bigint NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`prompt_option_id`),
  KEY `IDX_PROMPT_OPTION_PROMPT` (`prompt_id`),
  KEY `IDX_PROMPT_OPTION_OPTION` (`option_id`),
  CONSTRAINT `prompt_option_ibfk_1` FOREIGN KEY (`prompt_id`) REFERENCES `prompt` (`prompt_id`),
  CONSTRAINT `prompt_option_ibfk_2` FOREIGN KEY (`option_id`) REFERENCES `options` (`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `snapshot`
--

DROP TABLE IF EXISTS `snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `snapshot` (
  `snapshot_id` bigint NOT NULL AUTO_INCREMENT,
  `template_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `snapshot_name` varchar(255) NOT NULL,
  `snapshot_type` enum('BLOCK','FILE','LOG','DIRECTORY') DEFAULT NULL,
  `content` mediumtext,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`snapshot_id`),
  KEY `IDX_SNAPSHOT_TEMPLATE` (`template_id`),
  KEY `IDX_SNAPSHOT_USER` (`user_id`),
  CONSTRAINT `snapshot_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `template` (`template_id`),
  CONSTRAINT `snapshot_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `template_name` varchar(255) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`template_id`),
  KEY `IDX_TEMPLATE_USER` (`user_id`),
  CONSTRAINT `template_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_ai_provider`
--

DROP TABLE IF EXISTS `user_ai_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_ai_provider` (
  `user_ai_provider_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `ai_provider_id` bigint NOT NULL,
  `is_default` tinyint(1) NOT NULL DEFAULT '1',
  `api_key` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `temperature` float NOT NULL DEFAULT '0.5',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`user_ai_provider_id`),
  KEY `user_ai_provider_users_FK` (`user_id`),
  KEY `user_ai_provider_ai_provider_FK` (`ai_provider_id`),
  CONSTRAINT `user_ai_provider_ai_provider_FK` FOREIGN KEY (`ai_provider_id`) REFERENCES `ai_provider` (`ai_provider_id`),
  CONSTRAINT `user_ai_provider_users_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `provider_name` enum('google','github','ssafy') NOT NULL,
  `provider_uid` varchar(255) NOT NULL,
  `notion_secret_key` varchar(255) DEFAULT NULL,
  `notion_active` tinyint(1) DEFAULT '0',
  `is_deleted` tinyint(1) DEFAULT '0',
  `last_login_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_USERS_PROVIDER` (`provider_name`,`provider_uid`),
  KEY `IDX_USERS_EMAIL` (`email`),
  KEY `IDX_USERS_PROVIDER_UID` (`provider_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-20 15:16:25
