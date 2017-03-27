-- Basic BBDD Schema runs on mySQL
 
DROP SCHEMA IF EXISTS `regalator`;
 
CREATE SCHEMA IF NOT EXISTS `regalator` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `regalator` ;

DROP TABLE IF EXISTS `regalator`.`usuarios`;
CREATE TABLE IF NOT EXISTS `regalator`.`usuarios`(
	`id`INT NOT NULL AUTO_INCREMENT,
    `nombre`VARCHAR(45),
    `password`VARCHAR(30),
    PRIMARY KEY(`id`)
    )ENGINE=InnoDB;

DROP TABLE IF EXISTS `regalator`.`categorias`;
CREATE TABLE IF NOT EXISTS `regalator`.`categorias`(
	`id`INT NOT NULL,
    `tipo`VARCHAR(45),
    PRIMARY KEY(`id`)
)ENGINE=InnoDB;

DROP TABLE IF EXISTS `regalator`.`marcas`;
CREATE TABLE IF NOT EXISTS `regalator`.`marcas`(
	`id`INT NOT NULL,
    `nombre`VARCHAR(45),
    PRIMARY KEY(`id`)
    )ENGINE=InnoDB;


DROP TABLE IF EXISTS `regalator`.`regalos`;
CREATE TABLE IF NOT EXISTS `regalator`.`regalos`(
	`id`INT NOT NULL,
    `nombre` VARCHAR(45),
    `precio`float,
    `id_categoria` int,
    `id_marca` int,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_regalos_categorias` FOREIGN KEY(`id_categoria`) REFERENCES `regalator`.`categorias`(`id`) 
    ON UPDATE CASCADE 
    ON DELETE CASCADE,
    CONSTRAINT `FK_regalos_marcas` FOREIGN KEY(`id_marca`) REFERENCES `regalator`.`marcas`(`id`)
	ON UPDATE CASCADE 
    ON DELETE CASCADE
    )ENGINE = InnoDB;
    
    CREATE INDEX `fk_regalos_categoria_idx` ON `regalator`.`regalos` (`id_categoria` ASC);
	CREATE INDEX `fk_regalos_marca_idx` ON `regalator`.`regalos` (`id_marca` ASC);
    
    DROP TABLE IF EXISTS `regalator`.`eventos`;
	CREATE TABLE IF NOT EXISTS `regalator`.`eventos`(
		`id` INT NOT NULL AUTO_INCREMENT,
        `id_usuario` INT NOT NULL,
        `fecha` DATE,
        `descripcion` VARCHAR(45),
		`diasAviso` INT NOT NULL,  -- de 0 a X dias antes de la fecha
        `regaloConcreto` INT, -- si es null el regalo es aleatorio
        PRIMARY KEY(`id`),  
        CONSTRAINT `FK_eventos_usuarios` FOREIGN KEY(`id_usuario`) REFERENCES `regalator`.`usuarios`(`id`)
		ON UPDATE CASCADE 
		ON DELETE CASCADE
	)ENGINE=InnoDB;
    CREATE INDEX `fk_eventos_usuarios_idx` ON `regalator`.`eventos` (`id_usuario` ASC);
    
	DROP TABLE IF EXISTS `regalator`.`eventos_marcas`;
	CREATE TABLE IF NOT EXISTS `regalator`.`eventos_marcas`(
		`id_evento` INT NOT NULL,
        `id_marca` INT NOT NULL,
        PRIMARY KEY(`id_evento`,`id_marca`), -- no puede haber dos eventos con igual id i categoria
		CONSTRAINT `FK_eventos_marcas_eventos` FOREIGN KEY(`id_evento`) REFERENCES `regalator`.`eventos`(`id`)
		ON UPDATE CASCADE 
		ON DELETE CASCADE,
		CONSTRAINT `FK_eventos_marcas_marcas` FOREIGN KEY(`id_marca`) REFERENCES `regalator`.`marcas`(`id`)
		ON UPDATE CASCADE 
		ON DELETE CASCADE
	)ENGINE = InnoDB;    
	CREATE INDEX `fk_eventos_marcas_eventos_idx` ON `regalator`.`eventos_marcas` (`id_evento` ASC);
	CREATE INDEX `fk_eventos_marcas_marcas_idx` ON `regalator`.`eventos_marcas` (`id_marca` ASC);

    
	DROP TABLE IF EXISTS `regalator`.`eventos_categorias`;
	CREATE TABLE IF NOT EXISTS `regalator`.`eventos_categorias`(
		`id_evento` INT NOT NULL,
        `id_categoria` INT NOT NULL,
		PRIMARY KEY(`id_evento`,`id_categoria`),
		CONSTRAINT `FK_eventos_categorias_eventos` FOREIGN KEY(`id_evento`) REFERENCES `regalator`.`eventos`(`id`)
		ON UPDATE CASCADE 
		ON DELETE CASCADE,
		CONSTRAINT `FK_eventos_categorias_categorias` FOREIGN KEY(`id_categoria`) REFERENCES `regalator`.`categorias`(`id`)
		ON UPDATE CASCADE 
		ON DELETE CASCADE
	)ENGINE = InnoDB;    
	CREATE INDEX `fk_eventos_marcas_eventos_idx` ON `regalator`.`eventos_categorias` (`id_evento` ASC);
	CREATE INDEX `fk_eventos_marcas_categorias_idx` ON `regalator`.`eventos_categorias` (`id_categoria` ASC);
