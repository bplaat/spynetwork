# Supabase database tables
To run this project create a new Supabase project and create the following database tabels:

## devices
|Name|Data Type|Format|
|-|-|-|
|id|bigint|int8|
|name|character varying|varchar|
|created_at|timestamp with time zone|timestamptz|

## device_messages
|Name|Data Type|Format|
|-|-|-|
|id|bigint|int8|
|device_id|bigint|int8|
|sender|character varying|varchar|
|message|text|text|
|created_at|timestamp with time zone|timestamptz|
