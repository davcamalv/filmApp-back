truncate table chat_function; 
truncate table chat_session;
truncate table chat_user;
truncate table configuration;
truncate table media_content_genre;
truncate table genre;
truncate table genre_list;
truncate table price;
truncate table media_content;
truncate table media_content_list;
truncate table media_content_platform;
truncate table message;
truncate table option_selectable;
truncate table option_value;
truncate table platform;
truncate table premiere;
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

INSERT INTO configuration (id, property, value) VALUES (1, 'tmdb.apikey', 'ff482dee982fe1d0541efec24c60d43c');

