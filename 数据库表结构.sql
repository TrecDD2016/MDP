/*
Navicat MySQL Data Transfer

Source Server         : mydb
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : mdp

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-04-25 21:50:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dlength
-- ----------------------------
DROP TABLE IF EXISTS `dlength`;
CREATE TABLE `dlength` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `doc` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `length` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for idf
-- ----------------------------
DROP TABLE IF EXISTS `idf`;
CREATE TABLE `idf` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `term` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `idf` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for path
-- ----------------------------
DROP TABLE IF EXISTS `path`;
CREATE TABLE `path` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `doc` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `path` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for pstd, doc name is unique now(is id )
-- ----------------------------
DROP TABLE IF EXISTS `pstd`;
CREATE TABLE `pstd` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `doc` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `term` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `pstd` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for pustd
-- ----------------------------
DROP TABLE IF EXISTS `pustd`;
CREATE TABLE `pustd` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `doc` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `term` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `pustd` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for tc
-- ----------------------------
DROP TABLE IF EXISTS `tc`;
CREATE TABLE `tc` (
   `id` INT NOT NULL AUTO_INCREMENT,
  `term` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `tc` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for td
-- ----------------------------
DROP TABLE IF EXISTS `td`;
CREATE TABLE `td` (
   `id` INT NOT NULL AUTO_INCREMENT,
  `term` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `doc` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `td` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for docs
-- ----------------------------
DROP TABLE IF EXISTS `docs`;
CREATE TABLE `docs` (
  -- `doc` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
   `id` INT NOT NULL AUTO_INCREMENT,
  `doc_id` varchar(250) COLLATE utf8mb4_bin NOT NULL,
  `content` LONGTEXT COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



# must chage to utf-8_bin, or mysql will consider 'médicament' as 'medicament'
ALTER TABLE dlength CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
ALTER TABLE idf CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
ALTER TABLE path CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
ALTER TABLE pstd CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
ALTER TABLE pustd CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
ALTER TABLE tc CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
ALTER TABLE td CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
ALTER TABLE docs CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
