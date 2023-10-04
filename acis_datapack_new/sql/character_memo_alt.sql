CREATE TABLE `character_memo_alt` (
  `obj_id` int(11) NOT NULL DEFAULT 0,
  `name` varchar(255) NOT NULL DEFAULT '0',
  `value` text NOT NULL,
  `expire_time` bigint(20) NOT NULL DEFAULT 0,
  UNIQUE KEY `prim` (`obj_id`,`name`),
  KEY `obj_id` (`obj_id`),
  KEY `name` (`name`),
  KEY `value` (`value`(333)),
  KEY `expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
