DROP DATABASE IF EXISTS entregable3;

CREATE DATABASE entregable3;

USE entregable3;

DROP TABLE IF EXISTS PLACAS;

CREATE TABLE IF NOT EXISTS PLACAS(
		valueId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		groupId INT NOT NULL, 
		placaId INT NOT NULL
);

DROP TABLE IF EXISTS SENSORES;

SET FOREIGN_KEY_CHECKS= 0;

CREATE TABLE IF NOT EXISTS SENSORES(
		valueId INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
		groupId INT NOT NULL, 
		placaId INT NOT NULL, 
		sensorId INT NOT NULL,
		valor FLOAT NOT NULL,
		fechaHora DATETIME NOT NULL, 
		nombre VARCHAR(25) NOT NULL
);

DROP TABLE IF EXISTS ACTUADORES;

CREATE TABLE IF NOT EXISTS ACTUADORES(
	
	valueId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	groupId INT NOT NULL,
	placaId INT NOT NULL,
	actuadorId INT NOT NULL,
	activo BOOLEAN NOT NULL,
	fechaHora DATETIME,
	nombre VARCHAR(25) NOT NULL

);

INSERT INTO SENSORES(valueId, 
							groupId, 
							placaId, 
							sensorId, 
							valor, 
							fechaHora, 
							nombre) 
							VALUES(0, 1, 1, 4, 2.554, "2024-04-17 12:54:02", "LDR"),
									(1, 3, 2, 1, 8.7, "2024-04-18 12:54:10", "Ultrasonido");
									
SELECT * FROM sensores;

INSERT INTO actuadores(valueId, 
							  groupId, 
							  placaId, 
							  actuadorId, 
							  activo, 
							  fechaHora, 
							  nombre)
							  VALUES(0, 3, 2, 0, TRUE, "2024-04-14 15:30:45", "Relé"),
							  		  (1, 1, 1, 1, FALSE, "2024-04-18 18:04:23", "LED");
							  		  
SELECT * FROM actuadores;

INSERT INTO placas(valueId,
						 groupId,
						 placaId)
						 VALUES(0,2,4),
						 (1,2,1);


SELECT actuadorId, COUNT(*) 
FROM entregable3.actuadores 
GROUP BY actuadorId 
HAVING COUNT(*) > 1;


DELETE * FROM actuadores;