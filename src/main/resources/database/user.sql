-- ********************************************************************************
-- This script creates the database users and grants them the necessary permissions
-- ********************************************************************************

CREATE USER seenit_owner
WITH PASSWORD 'seenitowner';

GRANT ALL
ON ALL TABLES IN SCHEMA public
TO seenit_owner;

GRANT ALL
ON ALL SEQUENCES IN SCHEMA public
TO seenit_owner;

CREATE USER seenit_appuser
WITH PASSWORD 'seenituser';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON ALL TABLES IN SCHEMA public
          TO seenit_appuser;

GRANT USAGE, SELECT
             ON ALL SEQUENCES IN SCHEMA public
                 TO seenit_appuser;