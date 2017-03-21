-- LAS FUNCIONES SE HACEN PORQUE EL USUARIO NO TIENE PRIVILEGIOS PARA HACER SELECT EN LA
-- TABLA USUARIOS (YA QUE CONTIENE USERNAMES Y PWDS) 


-- CREA LA FUNCION PARA OBTENER EL ID USUARIO SIN HACER SELECT EN LA TABLA USUARIOS --
USE `regalator`;
DROP function IF EXISTS `getuserID`;

DELIMITER $$
USE `regalator`$$
CREATE FUNCTION getuserID (nom VARCHAR(20), pwd VARCHAR(20))
RETURNS INTEGER
BEGIN
	DECLARE id_found INT DEFAULT -1; -- DEVUELVE -1 SI NO ESTA EL USUARIO EN LA BBDD
	SELECT id INTO id_found FROM usuarios WHERE nom = nombre AND pwd = password;
	RETURN id_found;
END$$

DELIMITER ;

-- CREA LA FUNCION AGREGAR UN NUEVO USUARIO A LA TABLA, DEVUELVE SU NUEVA ID --
USE `regalator`;
DROP function IF EXISTS `addUser`;

DELIMITER $$
USE `regalator`$$
CREATE FUNCTION addUser (nom VARCHAR(20), pwd VARCHAR(20))
RETURNS INTEGER
BEGIN
	DECLARE newUserID INT DEFAULT -1;
    DECLARE existantID INT;
    SELECT id INTO existantID FROM usuarios WHERE nom = nombre; 
    IF existantID IS NOT NULL THEN RETURN -2; -- Si el usuario existe ya (igual nombre), devuelve este numero
    END IF; -- si no existe, agregalo
    INSERT INTO usuarios VALUES(null,nom,pwd);
	SELECT id INTO newUserID FROM usuarios WHERE nom = nombre AND pwd = password;
	RETURN newUserID;
END$$

DELIMITER ;

-- CREA LA PROCEDURE PARA ELIMINAR UN USUARIO, NOTA: SI QUIERES HAZLA FUNCION PARA QUE --
-- HAGA SELECT LUEGO Y DEVUELVA UN ENTERO SI SE HA CONSEGUIDO BORRAR ETC --
USE `regalator`;
DROP PROCEDURE IF EXISTS `removeUser`;

DELIMITER $$
USE `regalator`$$
CREATE PROCEDURE removeUser (nom VARCHAR(20), pwd VARCHAR(20))
BEGIN
    DELETE FROM usuarios WHERE nombre = nom AND pwd = password; 
END$$

DELIMITER ;

-- CREA LA PROCEDURE PARA BUSCAR LOS EVENTOS DEL USUARIO, HAY UN SELECT EN
-- LA TABLA USUARIOS POR ESO LA CREO.
USE `regalator`;
DROP PROCEDURE IF EXISTS `getEventos`;

DELIMITER $$
USE `regalator`$$
CREATE PROCEDURE getEventos (idUser VARCHAR(20))
BEGIN
	SELECT eventos.id,fecha,descripcion,diasAviso FROM eventos,usuarios WHERE usuarios.id = idUser AND usuarios.id = eventos.id_usuario;
END$$

DELIMITER ;


-- CREA EL USUARIO SIN PRIVILEGIOS EXTRA QUE USARA LOS COMANDOS Y FUNCIONES --
-- GRANTS POTENCIALMENTE PELIGROSOS: UPDATE/INSERT/DELETE en eventos
-- INSERT/DELETE EN eventos_marcas y eventos_categorias
-- EN EL FUTURO HACER MAS FUNCIONES PROBABLEMENTE PARA EVITAR COSAS RARAS
-- COMO SQL INJECTIONS O NOSE
DROP USER IF EXISTS 'usuarioMedio';
CREATE USER 'usuarioMedio' IDENTIFIED BY 'sinprivilegios1' PASSWORD EXPIRE NEVER;
GRANT SELECT ON regalator.eventos TO 'usuarioMedio';
GRANT SELECT ON regalator.categorias TO 'usuarioMedio';
GRANT SELECT ON regalator.marcas TO 'usuarioMedio';
GRANT SELECT ON regalator.regalos TO 'usuarioMedio';
GRANT SELECT ON regalator.eventos_categorias TO 'usuarioMedio';
GRANT SELECT ON regalator.eventos_marcas TO 'usuarioMedio';
GRANT UPDATE ON regalator.eventos TO 'usuarioMedio';
GRANT INSERT ON regalator.eventos TO 'usuarioMedio'; 
GRANT DELETE ON regalator.eventos TO 'usuarioMedio';
GRANT INSERT ON regalator.eventos_categorias TO 'usuarioMedio'; 
GRANT DELETE ON regalator.eventos_categorias TO 'usuarioMedio';
GRANT INSERT ON regalator.eventos_marcas TO 'usuarioMedio'; 
GRANT DELETE ON regalator.eventos_marcas TO 'usuarioMedio';
GRANT EXECUTE ON FUNCTION regalator.getuserID TO 'usuarioMedio';
GRANT EXECUTE ON FUNCTION regalator.addUser TO 'usuarioMedio';
GRANT EXECUTE ON PROCEDURE regalator.removeUser TO 'usuarioMedio';
GRANT EXECUTE ON PROCEDURE regalator.getEventos TO 'usuarioMedio';



-- para llamar a la funcion USE regalator; select getuserID(nombre,usuario) as id; o si es procedure call nombrefuncion(parametros); --
