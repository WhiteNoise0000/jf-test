DROP TABLE IF EXISTS 入港予定明細;
DROP TABLE IF EXISTS 入港予定;
DROP TABLE IF EXISTS 魚種;
DROP TABLE IF EXISTS 漁船;

CREATE TABLE 漁船 (
    漁船ID      VARCHAR(6)  PRIMARY KEY
    ,漁船名     VARCHAR(20) UNIQUE NOT NULL 
);

CREATE TABLE 魚種 (
    魚種ID      SERIAL      PRIMARY KEY
    ,名称       VARCHAR(20) UNIQUE NOT NULL 
);

CREATE TABLE 入港予定 (
    予定ID      SERIAL      PRIMARY KEY
    ,漁船ID     VARCHAR(6)  NOT NULL REFERENCES 漁船 (漁船ID)
    ,入港予定日 DATE        NOT NULL
);

CREATE TABLE 入港予定明細 (
    明細ID      SERIAL      PRIMARY KEY
    ,予定ID     INTEGER     REFERENCES 入港予定 (予定ID)
    ,魚種       VARCHAR(20) NOT NULL
    ,出荷予定日 DATE        NOT NULL
    ,数量       SMALLINT    NOT NULL
    ,UNIQUE (魚種, 出荷予定日)
);