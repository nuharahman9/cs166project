CREATE OR REPLACE LANGUAGE plpgsql; 

CREATE OR REPLACE FUNCTION set_timestamp()
RETURNS "trigger" AS 
$BODY$
BEGIN 
	NEW.updatedOn = CURRENT_TIMESTAMP;  
	RETURN NEW; 
END; 
$BODY$
LANGUAGE plpgsql VOLATILE; 

DROP TRIGGER IF EXISTS roomupdate_trigger ON RoomUpdatesLog;
CREATE TRIGGER roomupdate_trigger
BEFORE INSERT 
ON RoomUpdatesLog 
FOR EACH ROW 
EXECUTE PROCEDURE set_timestamp(); 

CREATE OR REPLACE FUNCTION set_date()
RETURNS "trigger" AS 
$BODY$
BEGIN 
	NEW.repairDate = GETDATE();  
	RETURN NEW; 
END; 
$BODY$
LANGUAGE plpgsql VOLATILE; 

DROP TRIGGER IF EXISTS roomrepair_trigger ON RoomRepairs;
CREATE TRIGGER roomrepair_trigger
BEFORE INSERT 
ON RoomRepairs 
FOR EACH ROW 
EXECUTE PROCEDURE set_date(); 
