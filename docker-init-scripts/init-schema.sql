-- Create the study schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS study;
-- Set the search path to use the study schema by default
ALTER ROLE postgres
SET search_path TO study,
	public;