truncate table premiere;
truncate table chat_function; 
truncate table chat_session;
truncate table message;
truncate table chat_user;
truncate table configuration;
truncate table media_content_genre;
truncate table genre;
truncate table genre_list;
truncate table price;
truncate table media_content;
truncate table media_content_list;
truncate table media_content_platform;
truncate table option_selectable;
truncate table option_value;
truncate table platform;
truncate table rol;
truncate table selectable;
truncate table user_rol;

INSERT INTO platform (id, name, logo, short_name) VALUES (1, 'Amazon Prime Video', 'https://www.justwatch.com/images/icon/52449861/s100', 'prv');

INSERT INTO genre (id, name, short_name, tmdb_id) VALUES (1, 'Comedia', 'cmy', 35);
INSERT INTO genre (id, name, short_name, tmdb_id) VALUES (2, 'Animación', 'ani', 16);
INSERT INTO genre (id, name, short_name, tmdb_id) VALUES (3, 'Fantasía', 'fnt', 14);
INSERT INTO genre (id, name, short_name, tmdb_id) VALUES (4, 'Acción & Aventura', 'act', 12);
INSERT INTO genre (id, name, short_name, tmdb_id) VALUES (5, 'Familia', 'fml', 10751);
INSERT INTO genre (id, name, short_name, tmdb_id) VALUES (6, 'Drama', 'drm', 18);
INSERT INTO genre (id, name, short_name, tmdb_id) VALUES (7, 'Acción & Aventura', 'act', 10759);

INSERT INTO configuration (id, property, value) VALUES(1, 'watson.version.date', '2021-11-27');
INSERT INTO configuration (id, property, value) VALUES(2, 'watson.assistant.id', 'e79aae05-0d8a-4393-b81a-f3fa09dab417');
INSERT INTO configuration (id, property, value) VALUES(3, 'watson.apikey', 'DPsPIgWI7FywhTMK2T8s6qsGFyG0E9B-bWJO4iMqGeBq');
INSERT INTO configuration (id, property, value) VALUES(4, 'watson.service.url', 'https://api.eu-gb.assistant.watson.cloud.ibm.com');
INSERT INTO configuration (id, property, value) VALUES (5, 'tmdb.apikey', 'ff482dee982fe1d0541efec24c60d43c');

INSERT INTO chat_function (id, description, button_value, button_label) VALUES(1, '- <b>Búsqueda de estrenos</b>: mediante esta acción podrá buscar los estrenos tanto de series como de películas filtrando si lo desea por fecha y plataforma.', '/buscar-estrenos', 'Buscar estrenos');
INSERT INTO chat_function (id, description, button_value, button_label) VALUES(2, '- <b>Búsqueda de contenido</b>: mediante esta acción podrá buscar series o películas ya sea mediante filtros o a través del título.', '/buscar-contenido', 'Buscar contenido');
INSERT INTO chat_function (id, description, button_value, button_label) VALUES(3, '- <b>Información de contacto</b>: con esta acción le daré información de contacto.', '/informacion-contacto', 'Información de contacto');

INSERT INTO media_content (id, title, description, media_type, creation_date, just_watch_url, imdb_id, poster, score, search_performed, tmdb_id) VALUES (9174, 'El club de la lucha', 'Un joven sin ilusiones lucha contra su insomnio, consecuencia quizás de su hastío por su gris y rutinaria vida. En un viaje en avión conoce a Tyler Durden, un carismático vendedor de jabón que sostiene una filosofía muy particular: el perfeccionismo es cosa de gentes débiles; en cambio, la autodestrucción es lo único que hace que realmente la vida merezca la pena. Ambos deciden entonces formar un club secreto de lucha donde descargar sus frustaciones y su ira que tendrá un éxito arrollador.', 'MOVIE', '(1999)', 'https://www.justwatch.com/es/pelicula/el-club-de-la-lucha', 'tt0137523', 'https://images.justwatch.com/poster/66105154/s718', '8.8 (1m)', 1, 550);

INSERT INTO chat_user (id, name, email, username, password, birth_date, avatar) VALUES(7, 'admin', 'dcamalv@gmail.com', 'admin', '$2a$10$iFmD6ZPuGsOg2meIdVxI4OB8NJIRZ8561.QEIz50VPlPhqA.MKA/K', NULL, NULL);

INSERT INTO premiere (id, premiere_date, season, news, media_content_id, platform_id) VALUES (1, '2021-11-12', 'Temporada 1', 'Capítulo 1', 9174, 1);
