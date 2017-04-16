-- Basic data for testing

Use regalator;
SET AUTOCOMMIT=1;

DELETE FROM usuarios;
DELETE FROM regalos;
DELETE FROM categorias;
DELETE FROM marcas;
DELETE FROM eventos;


-- DATOS USUARIOS --
INSERT INTO usuarios values(1,"Juan","A2445D","juan@mail.com");
INSERT INTO usuarios values(2,"Felipe","VV3RGF","felipe@yahoo.com"); 
INSERT INTO usuarios values(3,"Luis","HH31223HHT","luis_S_A@gmail.com");
INSERT INTO usuarios values(4,"Ramon","P12-dF","rmorenauer@hotmail.com"); 
INSERT INTO usuarios values(5,"Josu","55/985HT","aloha@mail.com");
INSERT INTO usuarios values(6,"Oscar","L`12-22","OCDC@gmail.com"); 
-- ----------------------------

-- DATOS CATEGORIAS --
INSERT INTO categorias values(1,"Vales de regalo");
INSERT INTO categorias values(2,"Libreria");
INSERT INTO categorias values(3,"Electrodomesticos");
INSERT INTO categorias values(4,"Viajes");
INSERT INTO categorias values(5,"Joyeria");
INSERT INTO categorias values(6,"Moda");
INSERT INTO categorias values(7,"Restaurantes");
INSERT INTO categorias values(8,"Otros");
INSERT INTO categorias values(9,"Exoticos");
INSERT INTO categorias values(10,"Acongojantes");
-- ----------------------------

-- DATOS MARCAS --
INSERT INTO marcas values(1,"El Corte Inglés");
INSERT INTO marcas values(2,"La Casa del Libro");
INSERT INTO marcas values(3,"Zara");
INSERT INTO marcas values(4,"Swarovski");
INSERT INTO marcas values(5,"Tagliattella");
INSERT INTO marcas values(6,"Adidas");
INSERT INTO marcas values(7,"World Peace");
-- ----------------------------

-- DATOS REGALOS --
INSERT INTO regalos values(1,"Vale Spa",35,1,1,"img01.jpg","https://www.google.com","");
INSERT INTO regalos values(2,"Libro de misterio",12,2,1,"img02.jpg","https://www.google.com","");
INSERT INTO regalos values(3,"Viaje a Londres",345,4,1,"img03.jpg","https://www.google.com","");
INSERT INTO regalos values(4,"Libro de cocina",13,2,1,"img04.jpg","https://www.google.com","");
INSERT INTO regalos values(5,"Collar de brillantes",102,5,4,"img05.jpg","https://www.google.com","");
INSERT INTO regalos values(6,"Jersey",23,6,3,"img06.jpg","https://www.google.com","");
INSERT INTO regalos values(7,"Cafetera",52.40,3,1,"img07.jpg","https://www.google.com","");
INSERT INTO regalos values(8,"Cena para dos",62.35,7,5,"img08.jpg","https://www.google.com","");
INSERT INTO regalos values(9,"Kit de pintura",36.7,8,1,"img09.jpg","https://www.google.com","");
-- ----------------------------

-- DATOS eventos --
INSERT INTO eventos values(1,1,"2016-12-11","BODA PEPA",1,NULL);
INSERT INTO eventos values(2,1,"2017-02-01","FIESTA LOCA",1,NULL);
INSERT INTO eventos values(3,2,"2018-05-23","se me va la olla nen",5,NULL);
INSERT INTO eventos values(4,2,"2018-05-23","prueba testing testing",7,NULL);
INSERT INTO eventos values(5,2,"2017-03-08","no macuerdo paque pongo esto",1,NULL);
INSERT INTO eventos values(6,2,"2017-10-25","joer tio",1,NULL);
INSERT INTO eventos values(7,3,"2018-12-15","Casamiento de Jesus",1,NULL);
INSERT INTO eventos values(8,5,"2017-12-29","bautizo hija de Ana",1,NULL);
INSERT INTO eventos values(9,6,"2017-06-02","Dia de pillar la farla",0,NULL);
INSERT INTO eventos values(10,6,"2016-08-14","Josechu te debe dinero",2,NULL);
-- ----------------------------

-- DATOS eventos_marcas -- sin datos en eventos 1-2 que son sobre los que testeo
INSERT INTO eventos_marcas values(3,6);
INSERT INTO eventos_marcas values(4,1);
INSERT INTO eventos_marcas values(5,3);
INSERT INTO eventos_marcas values(6,1);
INSERT INTO eventos_marcas values(6,3);
INSERT INTO eventos_marcas values(6,6);


-- DATOS eventos_categorias --
INSERT INTO eventos_categorias values(5,2);
INSERT INTO eventos_categorias values(5,1);
INSERT INTO eventos_categorias values(7,1);
INSERT INTO eventos_categorias values(8,4);
INSERT INTO eventos_categorias values(9,5);
INSERT INTO eventos_categorias values(9,8);

