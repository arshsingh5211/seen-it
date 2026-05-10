#!/bin/bash

BASEDIR=$(dirname $0)
DATABASE=seenit
HOST=localhost
PORT=5432

psql -h $HOST -p $PORT -U postgres -f "$BASEDIR/dropdb.sql" &&
createdb -h $HOST -p $PORT -U postgres $DATABASE &&
psql -h $HOST -p $PORT -U postgres -d $DATABASE -f "$BASEDIR/schema.sql" &&
psql -h $HOST -p $PORT -U postgres -d $DATABASE -f "$BASEDIR/user.sql"