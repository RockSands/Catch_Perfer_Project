-- ----------------------------
-- Table structure for R_RECORD_JOB
-- ----------------------------
DROP TABLE IF EXISTS R_RECORD_JOB;
CREATE TABLE R_RECORD_JOB (
UUID  varchar(64) NOT NULL ,
ID_JOB  varchar(255) NOT NULL ,
NAME_JOB  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
ID_RUN  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
STATUS  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
HOSTNAME  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
EXECUTION_TYPE varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'ONCE',
CRON_EXPRESSION  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL  ,
ERROR_MSG  varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
CREATE_TIME  datetime NOT NULL,
UPDATE_TIME  datetime NOT NULL,
PRIMARY KEY (ID_JOB,UUID)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for R_RECORD_HISTORY
-- ----------------------------
DROP TABLE IF EXISTS R_RECORD_DEPENDENT;
CREATE TABLE R_RECORD_DEPENDENT (
MASTER_UUID_ID  varchar(64) NOT NULL ,
META_ID  varchar(255) NOT NULL ,
META_TYPE  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
CREATE_TIME  datetime NOT NULL
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for SQY_RZDK_SPTJ
-- ----------------------------
DROP TABLE IF EXISTS SQY_RZDK_JYYH_SPTJ;
CREATE TABLE SQY_RZDK_SPTJ (
YH_ID  bigint NOT NULL ,
SP_ID  bigint NOT NULL ,
SCORE  double NOT NULL ,
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for SQY_RZDK_SPTJ
-- ----------------------------
DROP TABLE IF EXISTS SQY_RZDK_JYSP_SPTJ;
CREATE TABLE SQY_RZDK_SPTJ (
SP_ID  bigint NOT NULL ,
SP_ID2  bigint NOT NULL ,
SCORE  double NOT NULL ,
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=DYNAMIC;
