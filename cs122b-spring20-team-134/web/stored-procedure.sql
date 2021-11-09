DELIMITER $$

CREATE PROCEDURE add_movie (
	IN movietitle VARCHAR(100),
	IN tempmovieyear VARCHAR(11),
	IN moviedirector VARCHAR(100),
	IN star_name VARCHAR(100),
	IN genre_name VARCHAR(32)
)
BEGIN
declare star_id VARCHAR(10) default 0;
declare genre_id INT(11) default 0;
declare currentmovieid varchar(10);
declare tempint int default 0;
declare movieid varchar(10) default '';
declare movieyear int(11) default 0;
set movieyear = cast(tempmovieyear as SIGNED);
select max(id) into currentmovieid from movies;
set tempint = (select substring(currentmovieid,3)) + 1;
set movieid = concat("tt0", CAST(tempint as char));


insert into movies(id,title,year,director) values(movieid,movietitle,movieyear,moviedirector);
insert into ratings(movieId, rating, numVotes) values(movieid, 0.0, 0);
call form_starId(movieid, star_name);
call form_genreId(movieid, genre_name);

END
$$

CREATE PROCEDURE form_starId(
	IN movieid VARCHAR(10),
	IN starName VARCHAR(100)
)
BEGIN
declare starFlag int default 0;
declare tempStarID varchar(10);
declare string varchar(20) default '';
declare tempint int default 0;
select count(*) into starFlag from stars where stars.name = starName;

if(starFlag = 1) then 
	select id into tempStarID from stars where stars.name = starName;
	insert into stars_in_movies(starId, movieId) values(tempStarID, movieid);
else
	select max(id) into string from stars;
	set tempint = (select substring(string,3) as rank) + 1;
	set string = concat("nm",CASt(tempint as char));
	insert into stars(id,name,birthYear) values(string,starName,null);
	insert into stars_in_movies(starId,movieId) values(string, movieid);
END IF;
END
$$

CREATE PROCEDURE form_genreId(
	IN movieid VARCHAR(10),
	IN genre_name VARCHAR(32)
)
BEGIN
declare genreFlag int default 0;
declare tempGenreId int(11);
declare currentId int(11) default 0;
declare tempint int(11) default 0;
select count(*) into genreFlag from genres where genres.name = genre_name;

if(genreFlag = 1) then
	select id into tempGenreId from genres where genres.name = genre_name;
	insert into genres_in_movies(genreId, movieId) values (tempGenreId, movieid);
else
	select max(id) into currentId from genres;
	set tempint = currentId +1 ;
	insert into genres(id, name) values(tempint, genre_name);
	insert into genres_in_movies(genreId, movieId) values(tempint, movieid);
END IF;
END
$$

DELIMITER ;