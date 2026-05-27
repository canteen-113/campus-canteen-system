-- 校园智慧食堂管理系统 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS campus_canteen DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_canteen;
CREATE DATABASE  IF NOT EXISTS `campus_canteen` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `campus_canteen`;
-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: localhost    Database: campus_canteen
-- ------------------------------------------------------
-- Server version	8.0.27

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
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `role` enum('CANTEEN_MANAGER','SUPERVISOR','SUPER_ADMIN') NOT NULL COMMENT '角色',
  `canteen_id` bigint DEFAULT NULL COMMENT '关联食堂(食堂经理专属)',
  `status` enum('ACTIVE','DISABLED') DEFAULT 'ACTIVE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `username_2` (`username`),
  UNIQUE KEY `username_3` (`username`),
  UNIQUE KEY `username_4` (`username`),
  UNIQUE KEY `username_5` (`username`),
  KEY `canteen_id` (`canteen_id`),
  CONSTRAINT `admins_ibfk_1` FOREIGN KEY (`canteen_id`) REFERENCES `canteens` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (1,'admin_canteen_01','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','第一食堂经理','CANTEEN_MANAGER',1,'ACTIVE','2026-05-14 22:44:58'),(2,'admin_supervisor_01','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','后勤处领导','SUPERVISOR',NULL,'ACTIVE','2026-05-14 22:44:58'),(3,'admin_sa_01','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','超级管理员','SUPER_ADMIN',NULL,'ACTIVE','2026-05-14 22:44:58');
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcements`
--

DROP TABLE IF EXISTS `announcements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `publish_date` date DEFAULT NULL COMMENT '发布日期',
  `status` enum('PUBLISHED','DRAFT') DEFAULT 'PUBLISHED',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcements`
--

LOCK TABLES `announcements` WRITE;
/*!40000 ALTER TABLE `announcements` DISABLE KEYS */;
INSERT INTO `announcements` VALUES (1,'五一期间食堂供应调整','五一期间食堂供应调整：5月1日至5月3日仅一食堂二楼开放，请各位同学提前预约。','2026-04-28','PUBLISHED','2026-05-14 22:44:58');
/*!40000 ALTER TABLE `announcements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_records`
--

DROP TABLE IF EXISTS `approval_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `po_id` bigint NOT NULL,
  `step_name` varchar(50) DEFAULT NULL COMMENT '审批环节名称',
  `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人',
  `action` enum('APPROVE','REJECT') DEFAULT NULL COMMENT '审批动作',
  `comment` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `approval_records_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_records`
--

LOCK TABLES `approval_records` WRITE;
/*!40000 ALTER TABLE `approval_records` DISABLE KEYS */;
INSERT INTO `approval_records` VALUES (1,1,'test','test','APPROVE',NULL,'2026-05-17 17:27:50'),(2,19,'审批','超级管理员','APPROVE',NULL,'2026-05-17 18:52:24'),(3,2,'审批','超级管理员','APPROVE',NULL,'2026-05-17 18:52:57'),(4,25,'审批','超级管理员','REJECT','','2026-05-17 19:06:59');
/*!40000 ALTER TABLE `approval_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `canteens`
--

DROP TABLE IF EXISTS `canteens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `canteens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '食堂名称',
  `location` varchar(200) DEFAULT NULL COMMENT '位置描述',
  `manager` varchar(50) DEFAULT NULL COMMENT '负责人',
  `status` enum('OPEN','CLOSED') DEFAULT 'OPEN',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `name_2` (`name`),
  UNIQUE KEY `name_3` (`name`),
  UNIQUE KEY `name_4` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='食堂表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `canteens`
--

LOCK TABLES `canteens` WRITE;
/*!40000 ALTER TABLE `canteens` DISABLE KEYS */;
INSERT INTO `canteens` VALUES (1,'第一食堂','校园东区一楼','王经理','OPEN','2026-05-14 22:44:58'),(2,'第二食堂','校园西区二楼','赵经理','OPEN','2026-05-14 22:44:58'),(3,'教工餐厅','行政楼B1层','陈经理','OPEN','2026-05-14 22:44:58'),(4,'清真食堂','校园东区二楼','马经理','OPEN','2026-05-14 22:44:58');
/*!40000 ALTER TABLE `canteens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `name_2` (`name`),
  UNIQUE KEY `name_3` (`name`),
  UNIQUE KEY `name_4` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'特色大荤',1,'2026-05-14 22:44:58'),(2,'精美小荤',2,'2026-05-14 22:44:58'),(3,'时令蔬菜',3,'2026-05-14 22:44:58'),(4,'主食面点',4,'2026-05-14 22:44:58'),(5,'汤羹',5,'2026-05-14 22:44:58'),(6,'水果甜点',6,'2026-05-14 22:44:58'),(7,'川湘味',7,'2026-05-14 22:44:58'),(8,'特色小吃',8,'2026-05-14 22:44:58'),(9,'清真',9,'2026-05-14 22:44:58');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dishes`
--

DROP TABLE IF EXISTS `dishes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dishes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dish_no` varchar(20) NOT NULL COMMENT '菜品编号',
  `name` varchar(100) NOT NULL COMMENT '菜品名称',
  `category_id` bigint DEFAULT NULL COMMENT '所属分类',
  `canteen_id` bigint DEFAULT NULL COMMENT '所属食堂',
  `window_id` bigint DEFAULT NULL COMMENT '所属窗口',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `image` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `calories` int DEFAULT NULL COMMENT '热量(kcal)',
  `nutrition_tags` varchar(200) DEFAULT NULL COMMENT '营养标签(逗号分隔)',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `daily_stock` int DEFAULT '0' COMMENT '当日库存',
  `total_sold` int DEFAULT '0' COMMENT '累计销量',
  `status` enum('ON','OFF') DEFAULT 'ON' COMMENT '上架状态',
  `is_recommended` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dish_no` (`dish_no`),
  UNIQUE KEY `dish_no_2` (`dish_no`),
  UNIQUE KEY `dish_no_3` (`dish_no`),
  UNIQUE KEY `dish_no_4` (`dish_no`),
  UNIQUE KEY `dish_no_5` (`dish_no`),
  KEY `category_id` (`category_id`),
  KEY `canteen_id` (`canteen_id`),
  KEY `window_id` (`window_id`),
  CONSTRAINT `dishes_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `dishes_ibfk_2` FOREIGN KEY (`canteen_id`) REFERENCES `canteens` (`id`),
  CONSTRAINT `dishes_ibfk_3` FOREIGN KEY (`window_id`) REFERENCES `service_windows` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dishes`
--

LOCK TABLES `dishes` WRITE;
/*!40000 ALTER TABLE `dishes` DISABLE KEYS */;
INSERT INTO `dishes` VALUES (1,'FD-2026-001','秘制红烧牛腩',1,NULL,NULL,12.00,'https://modao.cc/agent-py/media/generated_images/2026-04-30/0867b32ab91f46e6bebcc1e0774bd519.jpg',450,'高蛋白','',85,1202,'ON',1,'2026-05-14 22:44:58','2026-05-16 00:22:32'),(2,'FD-2026-002','宫保鸡丁套饭',2,1,3,10.00,'https://modao.cc/agent-py/media/generated_images/2026-04-30/252859d8eeb54530ae1ac9edfcb941a3.jpg',380,'',NULL,11,891,'OFF',1,'2026-05-14 22:44:58','2026-05-16 19:39:00'),(3,'FD-2026-003','清蒸鲈鱼片',1,3,5,15.00,'https://modao.cc/agent-py/media/generated_images/2026-04-30/8e50eb8d4716443684ccad60f9798a69.jpg',280,'健康轻食',NULL,21,459,'ON',1,'2026-05-14 22:44:58','2026-05-16 19:42:31'),(4,'FD-2026-004','番茄炒蛋面',4,2,5,8.50,'https://modao.cc/agent-py/media/generated_images/2026-04-30/0f60c624817749e199bfc275a3a3329b.jpg',380,'营养均衡',NULL,45,720,'ON',0,'2026-05-14 22:44:58','2026-05-16 00:04:33'),(5,'FD-2026-005','糖醋排骨',1,4,1,14.00,'https://modao.cc/agent-py/media/generated_images/2026-04-30/3821939504fd4fb98a412f322590733d.jpg',520,'招牌必点',NULL,13,982,'ON',1,'2026-05-14 22:44:58','2026-05-16 19:42:31'),(6,'FD-2026-006','手撕包菜',3,1,2,4.50,'https://modao.cc/agent-py/media/generated_images/2026-04-30/35b9a2fda2634e3791981bb293bd4fd4.jpg',120,'',NULL,0,350,'ON',0,'2026-05-14 22:44:58','2026-05-16 00:04:33'),(7,'FD-2026-007','银耳雪梨汤',5,1,5,3.50,'https://modao.cc/agent-py/media/generated_images/2026-04-30/bcbc220d37b74ff9bb04b08a84a7cf3d.jpg',80,'',NULL,60,1500,'ON',0,'2026-05-14 22:44:58','2026-05-16 00:04:33'),(8,'FD-2026-008','麻婆豆腐套饭',7,3,1,8.50,'https://modao.cc/agent-py/media/generated_images/2026-04-30/9210c0fca13041b895c14aaac42a3d08.jpg',420,'',NULL,25,680,'ON',0,'2026-05-14 22:44:58','2026-05-16 00:04:33'),(9,'FD-2026-009','鲜虾小馄饨',8,2,5,6.00,'https://modao.cc/agent-py/media/generated_images/2026-04-30/a62173a763ea4eba9c0861c91d34da2f.jpg',200,'',NULL,40,520,'ON',0,'2026-05-14 22:44:58','2026-05-16 00:04:33');
/*!40000 ALTER TABLE `dishes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `emp_no` varchar(20) NOT NULL COMMENT '工号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `position` varchar(50) DEFAULT NULL COMMENT '岗位',
  `canteen_id` bigint DEFAULT NULL COMMENT '所属食堂',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `health_cert_expiry` date DEFAULT NULL COMMENT '健康证有效期',
  `hire_date` date DEFAULT NULL COMMENT '入职时间',
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `emp_no` (`emp_no`),
  UNIQUE KEY `emp_no_2` (`emp_no`),
  UNIQUE KEY `emp_no_3` (`emp_no`),
  UNIQUE KEY `emp_no_4` (`emp_no`),
  UNIQUE KEY `emp_no_5` (`emp_no`),
  KEY `canteen_id` (`canteen_id`),
  CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`canteen_id`) REFERENCES `canteens` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (1,'EMP-2026-001','王大锤','主理厨师',1,'13811110001','2027-04-12','2026-06-27','ACTIVE','2026-05-14 22:44:58','2026-05-17 20:42:58'),(2,'EMP-2026-015','李红霞','保洁员',1,'13933332234','2026-05-15','2025-01-10','ACTIVE','2026-05-14 22:44:58','2026-05-17 20:43:06'),(3,'EMP-2026-003','库管老张','仓库管理',1,'1373422678','2027-08-20','2024-03-15','ACTIVE','2026-05-14 22:44:58','2026-05-17 20:43:17'),(4,'EMP-2026-008','李建国','食品安全员',1,'13634569012','2027-01-10','2024-06-01','ACTIVE','2026-05-14 22:44:58','2026-05-17 20:43:24');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedbacks`
--

DROP TABLE IF EXISTS `feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedbacks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单',
  `dish_id` bigint DEFAULT NULL COMMENT '关联菜品',
  `rating` int NOT NULL COMMENT '评分(1-5)',
  `content` text COMMENT '评价内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片URL(逗号分隔)',
  `tags` varchar(200) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `is_anonymous` int DEFAULT NULL,
  `reply` text COMMENT '官方回复',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `status` enum('PENDING','REPLIED','CLOSED') DEFAULT 'PENDING',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `order_id` (`order_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `feedbacks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `feedbacks_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `feedbacks_ibfk_3` FOREIGN KEY (`dish_id`) REFERENCES `dishes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedbacks`
--

LOCK TABLES `feedbacks` WRITE;
/*!40000 ALTER TABLE `feedbacks` DISABLE KEYS */;
INSERT INTO `feedbacks` VALUES (1,1,3,2,5,'今天的宫保鸡丁味道特别正宗，鸡肉很嫩，花生米也很脆。分量给得也很足，3号窗口的阿姨态度特别好，赞一个！',NULL,'咸淡适中,分量足',0,'感谢您的好评！我们会继续保持，期待您的再次光临。','2026-04-30 13:00:00','REPLIED','2026-05-20 18:39:45'),(2,3,2,3,2,'排队太长了，预约了之后到现场还是要排很久，希望能优化取餐动线。',NULL,'',0,NULL,NULL,'PENDING','2026-05-20 18:39:45');
/*!40000 ALTER TABLE `feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `health_checks`
--

DROP TABLE IF EXISTS `health_checks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_checks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `check_date` date NOT NULL COMMENT '检查日期',
  `temperature` decimal(4,1) DEFAULT NULL COMMENT '体温',
  `is_qualified` int DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `health_checks_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工晨检表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_checks`
--

LOCK TABLES `health_checks` WRITE;
/*!40000 ALTER TABLE `health_checks` DISABLE KEYS */;
INSERT INTO `health_checks` VALUES (1,1,'2026-05-20',36.5,1,NULL,'2026-05-20 18:18:19');
/*!40000 ALTER TABLE `health_checks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingredients`
--

DROP TABLE IF EXISTS `ingredients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingredients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '物料名称',
  `spec` varchar(50) DEFAULT NULL COMMENT '规格',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `category` varchar(50) DEFAULT NULL COMMENT '分类(肉禽类/粮油类/蛋奶类等)',
  `current_stock` decimal(10,2) DEFAULT '0.00' COMMENT '现有库存',
  `alert_threshold` decimal(10,2) DEFAULT '0.00' COMMENT '预警线',
  `avg_price` decimal(10,2) DEFAULT '0.00' COMMENT '平均单价',
  `expiry_date` date DEFAULT NULL COMMENT '保质期至',
  `status` enum('SUFFICIENT','LOW','EXPIRING','OUT') DEFAULT 'SUFFICIENT' COMMENT '库存状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`spec`),
  UNIQUE KEY `name_2` (`name`,`spec`),
  UNIQUE KEY `name_3` (`name`,`spec`),
  UNIQUE KEY `name_4` (`name`,`spec`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='食材库存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingredients`
--

LOCK TABLES `ingredients` WRITE;
/*!40000 ALTER TABLE `ingredients` DISABLE KEYS */;
INSERT INTO `ingredients` VALUES (48,'精选前腿肉','kg','公斤','肉禽类',25.50,50.00,30.95,'2026-05-02','LOW','2026-05-17 00:16:31','2026-05-17 17:22:00'),(49,'东北长粒香米','50kg/袋','袋','粮油类',240.00,100.00,215.00,'2026-12-15','SUFFICIENT','2026-05-17 00:16:31','2026-05-17 00:16:31'),(50,'本地土鸡蛋','30枚/箱','箱','蛋奶类',42.00,30.00,24.50,'2026-05-01','EXPIRING','2026-05-17 00:16:31','2026-05-17 00:16:31'),(51,'金龙鱼调和油','5L/桶','桶','粮油类',85.00,20.00,68.00,'2027-01-20','SUFFICIENT','2026-05-17 00:16:31','2026-05-17 00:16:31'),(52,'食盐','500g/袋','袋','调味品',200.00,50.00,2.50,'2027-06-30','SUFFICIENT','2026-05-17 00:16:31','2026-05-17 00:16:31');
/*!40000 ALTER TABLE `ingredients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `dish_name` varchar(100) DEFAULT NULL COMMENT '菜品名称(冗余)',
  `quantity` int NOT NULL DEFAULT '1',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`dish_id`) REFERENCES `dishes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,'秘制红烧牛腩',1,12.00),(2,1,7,'银耳雪梨汤',1,3.50),(3,2,3,'清蒸鲈鱼片',1,12.00),(4,3,2,'宫保鸡丁套饭',1,10.00),(5,1,1,'秘制红烧牛腩',1,12.00),(6,1,7,'银耳雪梨汤',1,3.50),(7,2,3,'清蒸鲈鱼片',1,12.00),(8,3,2,'宫保鸡丁套饭',1,10.00),(9,1,1,'秘制红烧牛腩',1,12.00),(10,1,7,'银耳雪梨汤',1,3.50),(11,2,3,'清蒸鲈鱼片',1,12.00),(12,3,2,'宫保鸡丁套饭',1,10.00),(13,1,1,'秘制红烧牛腩',1,12.00),(14,1,7,'银耳雪梨汤',1,3.50),(15,2,3,'清蒸鲈鱼片',1,12.00),(16,3,2,'宫保鸡丁套饭',1,10.00),(17,1,1,'秘制红烧牛腩',1,12.00),(18,1,7,'银耳雪梨汤',1,3.50),(19,2,3,'清蒸鲈鱼片',1,12.00),(20,3,2,'宫保鸡丁套饭',1,10.00),(21,1,1,'秘制红烧牛腩',1,12.00),(22,1,7,'银耳雪梨汤',1,3.50),(23,2,3,'清蒸鲈鱼片',1,12.00),(24,3,2,'宫保鸡丁套饭',1,10.00),(25,1,1,'秘制红烧牛腩',1,12.00),(26,1,7,'银耳雪梨汤',1,3.50),(27,2,3,'清蒸鲈鱼片',1,12.00),(28,3,2,'宫保鸡丁套饭',1,10.00),(29,1,1,'秘制红烧牛腩',1,12.00),(30,1,7,'银耳雪梨汤',1,3.50),(31,2,3,'清蒸鲈鱼片',1,12.00),(32,3,2,'宫保鸡丁套饭',1,10.00),(33,1,1,'秘制红烧牛腩',1,12.00),(34,1,7,'银耳雪梨汤',1,3.50),(35,2,3,'清蒸鲈鱼片',1,12.00),(36,3,2,'宫保鸡丁套饭',1,10.00),(37,28,1,'Beef',1,12.00),(38,29,1,'test',1,12.00),(39,30,5,'糖醋排骨',1,14.00),(40,31,2,'test',1,10.00),(41,32,3,'清蒸鲈鱼片',1,12.00),(42,32,3,'清蒸鲈鱼片',1,12.00),(43,32,3,'清蒸鲈鱼片',1,12.00),(44,32,3,'清蒸鲈鱼片',1,12.00),(45,32,3,'清蒸鲈鱼片',1,12.00),(46,32,3,'清蒸鲈鱼片',1,12.00),(47,32,3,'清蒸鲈鱼片',1,12.00),(48,32,3,'清蒸鲈鱼片',1,12.00),(49,32,3,'清蒸鲈鱼片',1,12.00),(50,32,5,'糖醋排骨',1,14.00);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(20) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '下单用户',
  `canteen_id` bigint DEFAULT NULL COMMENT '食堂',
  `window_id` bigint DEFAULT NULL COMMENT '取餐窗口',
  `total_amount` decimal(10,2) NOT NULL COMMENT '商品总计',
  `deposit` decimal(10,2) DEFAULT '0.00' COMMENT '预约押金',
  `discount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `meal_period` enum('BREAKFAST','LUNCH','DINNER') DEFAULT NULL COMMENT '就餐时段',
  `reserve_date` date DEFAULT NULL COMMENT '预约日期',
  `reserve_time_start` time DEFAULT NULL COMMENT '预约开始时间',
  `reserve_time_end` time DEFAULT NULL COMMENT '预约结束时间',
  `pickup_code` varchar(10) DEFAULT NULL COMMENT '取餐码',
  `status` enum('PENDING','PREPARING','READY','PICKED_UP','CANCELLED') DEFAULT 'PENDING',
  `payment_method` varchar(20) DEFAULT 'CARD' COMMENT '支付方式',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  UNIQUE KEY `order_no_2` (`order_no`),
  UNIQUE KEY `order_no_3` (`order_no`),
  UNIQUE KEY `order_no_4` (`order_no`),
  UNIQUE KEY `order_no_5` (`order_no`),
  KEY `user_id` (`user_id`),
  KEY `canteen_id` (`canteen_id`),
  KEY `window_id` (`window_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`canteen_id`) REFERENCES `canteens` (`id`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`window_id`) REFERENCES `service_windows` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'#202604300085',1,1,3,15.50,2.00,1.50,14.00,'LUNCH','2026-04-30','11:30:00','12:30:00','#A128','PREPARING','CARD','2026-05-14 22:44:58','2026-05-14 22:44:58'),(2,'#202604300084',3,1,5,12.00,0.00,0.00,12.00,'LUNCH','2026-04-30','11:30:00','12:30:00','#A127','PICKED_UP','CARD','2026-05-14 22:44:58','2026-05-14 22:44:58'),(3,'#202604300083',4,1,2,10.00,0.00,0.00,10.00,'LUNCH','2026-04-30','11:30:00','12:30:00','#A126','PICKED_UP','CARD','2026-05-14 22:44:58','2026-05-14 22:44:58'),(28,'#202605160004',1,1,3,12.00,2.00,0.00,14.00,'LUNCH','2026-05-16',NULL,NULL,'#A173','PENDING','CARD','2026-05-16 00:04:55','2026-05-16 00:04:55'),(29,'#202605160005',1,NULL,NULL,12.00,0.00,0.00,12.00,'LUNCH','2026-05-16',NULL,NULL,'#A572','PENDING','CARD','2026-05-16 00:22:31','2026-05-16 00:22:31'),(30,'#202605160006',1,NULL,NULL,14.00,2.00,1.50,14.50,'LUNCH','2026-05-16',NULL,NULL,'#A348','PENDING','CARD','2026-05-16 00:23:28','2026-05-16 00:23:28'),(31,'#202605160007',1,1,3,10.00,2.00,0.00,12.00,'LUNCH','2026-05-16',NULL,NULL,'#A214','PENDING','CARD','2026-05-16 19:39:00','2026-05-16 19:39:00'),(32,'#202605160008',1,NULL,NULL,122.00,2.00,1.50,122.50,'LUNCH','2026-05-16',NULL,NULL,'#A575','PENDING','CARD','2026-05-16 19:42:31','2026-05-16 19:42:31');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_items`
--

DROP TABLE IF EXISTS `purchase_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `po_id` bigint NOT NULL,
  `material_name` varchar(100) NOT NULL COMMENT '物料名称',
  `spec` varchar(50) DEFAULT NULL COMMENT '规格',
  `quantity` decimal(10,2) NOT NULL COMMENT '数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '预估单价',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计',
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `purchase_items_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采购明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_items`
--

LOCK TABLES `purchase_items` WRITE;
/*!40000 ALTER TABLE `purchase_items` DISABLE KEYS */;
INSERT INTO `purchase_items` VALUES (1,1,'精选前腿肉','kg',50.00,32.00,1600.00),(2,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(3,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(4,2,'食盐','500g/袋',100.00,2.50,250.00),(5,2,'味精','500g/袋',50.00,5.00,250.00),(6,1,'精选前腿肉','kg',50.00,32.00,1600.00),(7,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(8,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(9,2,'食盐','500g/袋',100.00,2.50,250.00),(10,2,'味精','500g/袋',50.00,5.00,250.00),(11,1,'精选前腿肉','kg',50.00,32.00,1600.00),(12,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(13,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(14,2,'食盐','500g/袋',100.00,2.50,250.00),(15,2,'味精','500g/袋',50.00,5.00,250.00),(16,1,'精选前腿肉','kg',50.00,32.00,1600.00),(17,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(18,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(19,2,'食盐','500g/袋',100.00,2.50,250.00),(20,2,'味精','500g/袋',50.00,5.00,250.00),(21,1,'精选前腿肉','kg',50.00,32.00,1600.00),(22,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(23,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(24,2,'食盐','500g/袋',100.00,2.50,250.00),(25,2,'味精','500g/袋',50.00,5.00,250.00),(26,1,'精选前腿肉','kg',50.00,32.00,1600.00),(27,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(28,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(29,2,'食盐','500g/袋',100.00,2.50,250.00),(30,2,'味精','500g/袋',50.00,5.00,250.00),(31,1,'精选前腿肉','kg',50.00,32.00,1600.00),(32,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(33,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(34,2,'食盐','500g/袋',100.00,2.50,250.00),(35,2,'味精','500g/袋',50.00,5.00,250.00),(36,1,'精选前腿肉','kg',50.00,32.00,1600.00),(37,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(38,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(39,2,'食盐','500g/袋',100.00,2.50,250.00),(40,2,'味精','500g/袋',50.00,5.00,250.00),(41,1,'精选前腿肉','kg',50.00,32.00,1600.00),(42,1,'东北大米','50kg/袋',10.00,215.00,2150.00),(43,2,'酱油','1.8L/瓶',20.00,18.00,360.00),(44,2,'食盐','500g/袋',100.00,2.50,250.00),(45,2,'味精','500g/袋',50.00,5.00,250.00),(46,19,'test item',NULL,1.00,100.00,100.00),(47,24,'test item',NULL,3.00,100.00,300.00),(48,25,'白菜','5箱',500.00,30.00,15000.00),(49,26,'白菜','5箱',500.00,1.00,500.00);
/*!40000 ALTER TABLE `purchase_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_orders`
--

DROP TABLE IF EXISTS `purchase_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `po_no` varchar(20) NOT NULL COMMENT '单据编号',
  `applicant_id` bigint NOT NULL COMMENT '申请人',
  `department` varchar(100) DEFAULT NULL COMMENT '申请部门',
  `summary` varchar(500) DEFAULT NULL COMMENT '物料摘要',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总预估金额',
  `expected_date` date DEFAULT NULL COMMENT '期望到货日期',
  `reason` varchar(500) DEFAULT NULL COMMENT '采购事由',
  `status` enum('PENDING_MANAGER','PENDING_FINANCE','PENDING_DIRECTOR','APPROVED','REJECTED') DEFAULT 'PENDING_MANAGER',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `po_no` (`po_no`),
  UNIQUE KEY `po_no_2` (`po_no`),
  UNIQUE KEY `po_no_3` (`po_no`),
  UNIQUE KEY `po_no_4` (`po_no`),
  UNIQUE KEY `po_no_5` (`po_no`),
  KEY `applicant_id` (`applicant_id`),
  CONSTRAINT `purchase_orders_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采购申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_orders`
--

LOCK TABLES `purchase_orders` WRITE;
/*!40000 ALTER TABLE `purchase_orders` DISABLE KEYS */;
INSERT INTO `purchase_orders` VALUES (1,'PR-20260430-001',3,'第一食堂','精选前腿肉(50kg), 东北大米(10袋) 等 5 项',4250.00,'2026-05-02','应对五一假期前食材补库','APPROVED','2026-05-14 22:44:58','2026-05-17 17:27:50'),(2,'PR-20260429-012',3,'第一食堂','调味品补货(油盐酱醋)',860.00,'2026-05-05','月度常规补货','APPROVED','2026-05-14 22:44:58','2026-05-17 18:52:57'),(19,'PR-20260516-001',1,'test','test',100.00,NULL,NULL,'APPROVED','2026-05-16 19:38:19','2026-05-17 18:52:24'),(22,'PR-20260430-002',3,'第一食堂','精选前腿肉(50kg), 东北大米(10袋) 等 5 项',4250.00,'2026-05-02','应对五一假期前食材补库','PENDING_MANAGER','2026-05-17 18:59:38','2026-05-17 18:59:38'),(23,'PR-20260429-003',3,'第一食堂','调味品补货(油盐酱醋)',860.00,'2026-05-05','月度常规补货','PENDING_FINANCE','2026-05-17 18:59:38','2026-05-17 18:59:38'),(24,'PR-20260517-001',1,'test','test',300.00,'2026-06-01','test','PENDING_MANAGER','2026-05-17 19:05:35','2026-05-17 19:05:35'),(25,'PR-20260517-002',1,'第一食堂','白菜(500)',15000.00,'2026-05-17','','REJECTED','2026-05-17 19:06:40','2026-05-17 19:06:59'),(26,'PR-20260517-003',1,'第一食堂','白菜(500)',500.00,'2026-05-17','','PENDING_MANAGER','2026-05-17 19:06:54','2026-05-17 19:06:54');
/*!40000 ALTER TABLE `purchase_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refunds`
--

DROP TABLE IF EXISTS `refunds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refunds` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `applicant_name` varchar(50) DEFAULT NULL COMMENT '申请人姓名',
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `refunds_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `refunds_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退款申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refunds`
--

LOCK TABLES `refunds` WRITE;
/*!40000 ALTER TABLE `refunds` DISABLE KEYS */;
INSERT INTO `refunds` VALUES (1,2,4,12.00,'菜品内发现异物，要求退全款。','张华','PENDING','2026-05-14 22:44:58','2026-05-14 22:44:58');
/*!40000 ALTER TABLE `refunds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `safety_samples`
--

DROP TABLE IF EXISTS `safety_samples`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `safety_samples` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `canteen_id` bigint NOT NULL,
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `meal_period` enum('BREAKFAST','LUNCH','DINNER') DEFAULT NULL COMMENT '时段',
  `inspector` varchar(50) DEFAULT NULL COMMENT '留样人',
  `image` varchar(500) DEFAULT NULL COMMENT '留样照片',
  `status` enum('SAMPLED','PENDING') DEFAULT 'PENDING' COMMENT '留样状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `canteen_id` (`canteen_id`),
  CONSTRAINT `safety_samples_ibfk_1` FOREIGN KEY (`canteen_id`) REFERENCES `canteens` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='食品安全留样表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `safety_samples`
--

LOCK TABLES `safety_samples` WRITE;
/*!40000 ALTER TABLE `safety_samples` DISABLE KEYS */;
INSERT INTO `safety_samples` VALUES (1,1,'秘制红烧牛腩','LUNCH','李建国',NULL,'SAMPLED','2026-05-20 18:24:12'),(2,1,'清蒸鲈鱼片','LUNCH','李建国',NULL,'SAMPLED','2026-05-20 18:24:12'),(3,1,'银耳雪梨汤','LUNCH',NULL,NULL,'PENDING','2026-05-20 18:24:12');
/*!40000 ALTER TABLE `safety_samples` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_windows`
--

DROP TABLE IF EXISTS `service_windows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_windows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `canteen_id` bigint NOT NULL COMMENT '所属食堂',
  `window_no` varchar(10) NOT NULL COMMENT '窗口编号',
  `name` varchar(100) DEFAULT NULL COMMENT '窗口名称',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  `status` enum('OPEN','CLOSED') DEFAULT 'OPEN',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `canteen_id` (`canteen_id`,`window_no`),
  UNIQUE KEY `canteen_id_2` (`canteen_id`,`window_no`),
  UNIQUE KEY `canteen_id_3` (`canteen_id`,`window_no`),
  UNIQUE KEY `canteen_id_4` (`canteen_id`,`window_no`),
  CONSTRAINT `service_windows_ibfk_1` FOREIGN KEY (`canteen_id`) REFERENCES `canteens` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='服务窗口表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_windows`
--

LOCK TABLES `service_windows` WRITE;
/*!40000 ALTER TABLE `service_windows` DISABLE KEYS */;
INSERT INTO `service_windows` VALUES (1,1,'01','01号窗-大荤','李建国','OPEN','2026-05-14 22:44:58'),(2,1,'02','02号窗-小荤','张红','OPEN','2026-05-14 22:44:58'),(3,1,'03','03号窗-特色','王大锤','OPEN','2026-05-14 22:44:58'),(4,1,'05','05号窗-汤羹','赵丽','OPEN','2026-05-14 22:44:58'),(5,2,'01','01号窗-主食','刘伟','OPEN','2026-05-14 22:44:58'),(6,2,'05','05号窗-面点','陈芳','OPEN','2026-05-14 22:44:58'),(7,3,'01','01号窗-套餐','孙明','OPEN','2026-05-14 22:44:58'),(8,3,'05','05号窗-轻食','周华','OPEN','2026-05-14 22:44:58'),(9,4,'01','01号窗-清真','马师傅','OPEN','2026-05-14 22:44:58');
/*!40000 ALTER TABLE `service_windows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settlements`
--

DROP TABLE IF EXISTS `settlements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settlements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `canteen_id` bigint NOT NULL,
  `period_start` date DEFAULT NULL COMMENT '结算开始日期',
  `period_end` date DEFAULT NULL COMMENT '结算结束日期',
  `total_orders` int DEFAULT '0' COMMENT '总单数',
  `total_revenue` decimal(12,2) DEFAULT '0.00' COMMENT '总营收',
  `refund_amount` decimal(12,2) DEFAULT '0.00' COMMENT '退款扣减',
  `settle_amount` decimal(12,2) DEFAULT '0.00' COMMENT '应结算金额',
  `account_no` varchar(50) DEFAULT NULL COMMENT '收款账户',
  `status` enum('PENDING','SETTLED') DEFAULT 'PENDING',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `canteen_id` (`canteen_id`),
  CONSTRAINT `settlements_ibfk_1` FOREIGN KEY (`canteen_id`) REFERENCES `canteens` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='财务结算表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settlements`
--

LOCK TABLES `settlements` WRITE;
/*!40000 ALTER TABLE `settlements` DISABLE KEYS */;
INSERT INTO `settlements` VALUES (1,1,'2026-04-01','2026-04-30',12408,248500.00,1250.00,247250.00,'6222...0012','SETTLED','2026-05-14 22:44:58'),(2,3,'2026-04-01','2026-04-30',4336,98200.00,420.00,97780.00,'6222...0045','PENDING','2026-05-14 22:44:58');
/*!40000 ALTER TABLE `settlements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_records`
--

DROP TABLE IF EXISTS `stock_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ingredient_id` bigint NOT NULL,
  `type` enum('IN','OUT','LOSS') NOT NULL COMMENT '出入库类型',
  `quantity` decimal(10,2) NOT NULL COMMENT '数量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '总金额',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `ingredient_id` (`ingredient_id`),
  CONSTRAINT `stock_records_ibfk_1` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存出入库记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_records`
--

LOCK TABLES `stock_records` WRITE;
/*!40000 ALTER TABLE `stock_records` DISABLE KEYS */;
INSERT INTO `stock_records` VALUES (5,48,'IN',30.00,30.00,900.00,'1','','2026-05-17 00:17:11'),(6,48,'LOSS',20.00,NULL,NULL,'管理员','','2026-05-17 17:22:00');
/*!40000 ALTER TABLE `stock_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL COMMENT '关联订单',
  `amount` decimal(10,2) NOT NULL COMMENT '金额(正=充值,负=消费)',
  `type` enum('CONSUME','RECHARGE','REFUND') NOT NULL COMMENT '交易类型',
  `description` varchar(200) DEFAULT NULL COMMENT '交易描述',
  `payment_method` varchar(20) DEFAULT 'CARD',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 22:44:58'),(2,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 22:44:58'),(3,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 22:44:58'),(4,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 22:44:58'),(5,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 22:44:58'),(6,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 22:44:58'),(7,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 22:44:58'),(8,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 22:59:29'),(9,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 22:59:29'),(10,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 22:59:29'),(11,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 22:59:29'),(12,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 22:59:29'),(13,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 22:59:29'),(14,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 22:59:29'),(15,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 23:03:58'),(16,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 23:03:58'),(17,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:03:58'),(18,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:03:58'),(19,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:03:58'),(20,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 23:03:58'),(21,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 23:03:58'),(22,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 23:13:57'),(23,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 23:13:57'),(24,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:13:57'),(25,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:13:57'),(26,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:13:57'),(27,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 23:13:57'),(28,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 23:13:57'),(29,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 23:14:16'),(30,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 23:14:16'),(31,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:14:16'),(32,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:14:16'),(33,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:14:16'),(34,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 23:14:16'),(35,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 23:14:16'),(36,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 23:15:06'),(37,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 23:15:06'),(38,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:15:06'),(39,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:15:06'),(40,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:15:06'),(41,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 23:15:06'),(42,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 23:15:06'),(43,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 23:17:04'),(44,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 23:17:04'),(45,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:17:04'),(46,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:17:04'),(47,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:17:04'),(48,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 23:17:04'),(49,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 23:17:04'),(50,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 23:56:05'),(51,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 23:56:05'),(52,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:56:05'),(53,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:56:05'),(54,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:56:05'),(55,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 23:56:05'),(56,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 23:56:05'),(57,2,NULL,200.00,'RECHARGE','自助机充值','CARD','2026-05-14 23:58:42'),(58,1,NULL,200.00,'RECHARGE','农行转入','CARD','2026-05-14 23:58:42'),(59,1,1,-14.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:58:42'),(60,3,2,-12.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:58:42'),(61,4,3,-10.00,'CONSUME','第一食堂消费','CARD','2026-05-14 23:58:42'),(62,3,NULL,-8.50,'CONSUME','教工餐厅消费','CARD','2026-05-14 23:58:42'),(63,1,NULL,-6.00,'CONSUME','第二食堂消费','CARD','2026-05-14 23:58:42'),(64,1,NULL,200.00,'RECHARGE','账户充值','CARD','2026-05-15 20:38:23'),(65,1,NULL,100.00,'RECHARGE','账户充值','CARD','2026-05-15 20:38:27'),(66,1,NULL,100.00,'RECHARGE','账户充值','CARD','2026-05-15 23:22:45'),(67,1,28,14.00,'CONSUME','订餐消费 - #202605160004','CARD','2026-05-16 00:04:55'),(68,1,29,12.00,'CONSUME','订餐消费 - #202605160005','CARD','2026-05-16 00:22:32'),(69,1,30,14.50,'CONSUME','订餐消费 - #202605160006','CARD','2026-05-16 00:23:28'),(70,1,31,12.00,'CONSUME','订餐消费 - #202605160007','CARD','2026-05-16 19:39:00'),(71,1,32,122.50,'CONSUME','订餐消费 - #202605160008','CARD','2026-05-16 19:42:31');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_no` varchar(20) NOT NULL COMMENT '学号/工号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `role` enum('STUDENT','TEACHER','STAFF') DEFAULT 'STUDENT' COMMENT '身份类型',
  `department` varchar(100) DEFAULT NULL COMMENT '院系/部门',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '一卡通余额',
  `face_data` int DEFAULT NULL,
  `status` enum('NORMAL','DISABLED') DEFAULT 'NORMAL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_no` (`user_no`),
  UNIQUE KEY `uk_user_no` (`user_no`),
  UNIQUE KEY `user_no_2` (`user_no`),
  UNIQUE KEY `user_no_3` (`user_no`),
  UNIQUE KEY `user_no_4` (`user_no`),
  UNIQUE KEY `user_no_5` (`user_no`),
  UNIQUE KEY `user_no_6` (`user_no`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'20261021','张明伟','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','STUDENT','计算机学院',NULL,511.50,1,'NORMAL','2026-05-14 22:44:58','2026-05-16 19:42:31'),(2,'2026102401','李华','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','STUDENT','艺术设计学院',NULL,286.50,0,'NORMAL','2026-05-14 22:44:58','2026-05-14 22:44:58'),(3,'20261034','李梦雨','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','STUDENT','外语学院',NULL,520.00,1,'NORMAL','2026-05-14 22:44:58','2026-05-14 22:44:58'),(4,'20261102','王晓冬','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','STUDENT','计算机学院',NULL,150.00,1,'NORMAL','2026-05-14 22:44:58','2026-05-14 22:44:58'),(5,'20261104','张华','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','STUDENT','计算机学院',NULL,88.00,0,'NORMAL','2026-05-14 22:44:58','2026-05-14 22:44:58');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-21 10:05:03
