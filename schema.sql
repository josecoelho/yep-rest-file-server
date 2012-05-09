CREATE TABLE `fsrv_file_metadata` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `path` varchar(255) CHARACTER SET latin1 NOT NULL DEFAULT '' COMMENT 'path where file is',
  `name` varchar(255) CHARACTER SET latin1 NOT NULL DEFAULT '' COMMENT 'Name of file, only for reference',
  `is_dir` tinyint(1) DEFAULT '0' COMMENT 'true if its a directory',
  `mimetype` varchar(50) CHARACTER SET latin1 NOT NULL DEFAULT '' COMMENT 'the mime type of file',
  `current_revision` int(11) DEFAULT '1' COMMENT 'the number of current revision of this file',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_file_metadata` (`path`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `fsrv_file_revision` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `metadata_id` int(11) unsigned NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `file_id` (`file_id`),
  CONSTRAINT `fsrv_file_revision_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `fsrv_file_metadata` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;