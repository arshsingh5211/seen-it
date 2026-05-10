-- **************************************************************
-- This script destroys the database and associated users
-- **************************************************************

SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'seenit';

DROP DATABASE IF EXISTS seenit;

DROP ROLE IF EXISTS seenit_owner;
DROP ROLE IF EXISTS seenit_appuser;