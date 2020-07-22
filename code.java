package project1;

import java.sql.*;

import org.postgresql.util.LruCache.CreateAction;

enum tableNames {
	movie, director, actor, customer, award, genre, movieGenre, movieObtain, actorObtain, directorObtain, casting, make,
	customerRate;

}

public class Main {

	static Connection connection = null;

	static boolean[] created = new boolean[13];

	static void createTables(Statement stmt) throws SQLException {
		System.out.print("Create table started\n\n");

		try {

			// movie

			String CreateMovie = "create table movie(movieID integer primary key, movieName varchar(30), releaseYear char(5), releaseMonth char(3), releaseDate char(3), publisherName varchar(30), avgRate numeric(2,1)) ";
			stmt.executeUpdate(CreateMovie);

			// director
			String CreateDirector = "create table director(directorID integer primary key, directorName varchar(20),"
					+ "dateOfBirth char(12),dateOfDeath char(12))";
			stmt.executeUpdate(CreateDirector);

			// actor

			String CreateActor = "create table actor(actorID integer primary key, actorName varchar(20),"
					+ "dateOfBirth char(12), dateOfDeath char(12), gender varchar(6))";
			stmt.executeUpdate(CreateActor);

			// customer

			String CreateCustomer = "create table customer(customerID integer primary key, customerName varchar(20),"
					+ "dateOfBirth char(12), gender char(6))";
			stmt.executeUpdate(CreateCustomer);

			// award

			String CreateAward = "create table award(awardID integer, awardName varchar(30),primary key(awardID))";
			stmt.executeUpdate(CreateAward);

			// genre

			String CreateGenre = "create table genre(genreName varchar(15) primary key)";
			stmt.executeUpdate(CreateGenre);

			// movie genre
			String CreateMovieGenre = "create table movieGenre(movieID integer, genreName varchar(15),"
					+ "foreign key(movieID) references movie(movieID) on delete cascade,foreign key(genreName) references genre(genreName) on delete cascade"
					+ ", primary key(movieID, genreName))";
			stmt.executeUpdate(CreateMovieGenre);

			// movie obtain
			stmt.executeUpdate("create table movieObtain(movieID integer, awardID integer,"
					+ "year integer, primary key(movieID, awardID),"
					+ "foreign key(movieID) references movie(movieID) on delete cascade, foreign key(awardID) references award(awardID) on delete cascade)");

			// actor obtain

			stmt.executeUpdate("create table actorObtain(actorID integer, awardID integer,"
					+ "year integer,primary key(actorID,awardID), foreign key(actorID) references actor(actorID) on delete cascade,"
					+ " foreign key(awardID)  references award(awardID) on delete cascade)");

			// directorObtain
			stmt.executeUpdate("create table directorObtain(directorID integer, awardID integer,"
					+ "year integer,primary key(directorID, awardID) , foreign key(directorID) references director(directorID) on delete cascade,"
					+ " foreign key(awardID) references award(awardID) on delete cascade)");

			// casting
			stmt.executeUpdate("create table casting(movieID integer, actorID integer, role varchar(30),"
					+ "primary key(movieID, actorID),foreign key(movieID) references movie(movieID) on delete cascade,"
					+ "foreign key(actorID) references actor(actorID) on delete cascade)");

			// make
			stmt.executeUpdate("create table make(movieID integer,directorID integer,primary key(movieID, directorID),"
					+ " foreign key(movieID) references movie(movieID) on delete cascade,"
					+ "foreign key(directorID) references director(directorID) on delete cascade);");

			// customerRate
			stmt.executeUpdate("create table customerRate(customerID integer, movieID integer, rate numeric(2,1),"
					+ "primary key(customerID, movieID),"
					+ "foreign key(customerID) references customer(customerID) on delete cascade, foreign key(movieID) references movie(movieID) on delete cascade)");

		} catch (SQLException e) {
			System.out.println("SQLException" + e);
			e.printStackTrace();
		}

		System.out.print("Create table ended\n\n");
	}

	static void init(Statement stmt) throws SQLException {

		try {
			// director
			stmt.executeUpdate("insert into director values(1, 'Tim Burton', '1958.8.25', null)");

			stmt.executeUpdate("insert into director values(2, 'David Fincher', '1962.8.28', null)");

			stmt.executeUpdate("insert into director values(3, 'Christopher Nolan', '1970.7.30', null)");

			// actor
			stmt.executeUpdate("insert into actor values(1,'Johnny Depp','1963.6.9',null,'Male')");

			stmt.executeUpdate("insert into actor values(2, 'Winona Ryder','1971.10.29',null,'Female')");

			stmt.executeUpdate("insert into actor values(3,'Anne Hathaway','1982.11.12',null,'Female')");

			stmt.executeUpdate("insert into actor values(4, 'Christian Bale','1974.1.30',null,'Male')");

			stmt.executeUpdate("insert into actor values(5,'Heath Ledger','1794.4.4','2008.1.22','Male')");

			stmt.executeUpdate("insert into actor values(6,'Jesse Eisenberg','1983.10.5',null,'Male')");

			stmt.executeUpdate("insert into actor values(7,'Andrew Garfield','1983.8.20',null,'Male')");

			// customer

			stmt.executeUpdate("insert into customer values(1,'Bob','1997.11.14','Male')");

			stmt.executeUpdate("insert into customer values(2,'John','1978.01.23','Male')");

			stmt.executeUpdate("insert into customer values(3,'Jack','1980.05.04','Male')");

			stmt.executeUpdate("insert into customer values(4,'Jill','1981.04.17','Female')");

			stmt.executeUpdate("insert into customer values(5,'Bell','1990.05.14','Female')");

			// movie
			stmt.executeUpdate("insert into movie values(1,'Edward Scissorhands','1991','06',"
					+ "'29','20th Century Fox Presents',0.0)");

			stmt.executeUpdate("insert into movie " + "values(2, 'Alice In Wonderland','2010','03','04',"
					+ "'Korea Sonry Pictures',0.0)");

			stmt.executeUpdate("insert into movie " + "values(3,'The Social Network','2010','11','18',"
					+ "'Korea Sony Pictures',0.0)");

			stmt.executeUpdate("insert into movie " + "values(4, 'The Dark Knight','2008','08','06',"
					+ "'Warner Brothers Korea',0.0)");

			// genre

			stmt.executeUpdate("insert into genre " + "values('Fantasy')");

			stmt.executeUpdate("insert into genre " + "values('Romance')");

			stmt.executeUpdate("insert into genre " + "values('Adventure')");

			stmt.executeUpdate("insert into genre " + "values('Family')");

			stmt.executeUpdate("insert into genre " + "values('Drama')");

			stmt.executeUpdate("insert into genre " + "values('Action')");

			stmt.executeUpdate("insert into genre " + "values('Mystery')");

			stmt.executeUpdate("insert into genre " + "values('Thriller')");

			// make
			stmt.executeUpdate("insert into make " + "values(1,1)");

			stmt.executeUpdate("insert into make " + "values(2,1)");

			stmt.executeUpdate("insert into make " + "values(3,2)");

			stmt.executeUpdate("insert into make " + "values(4,3)");

			// casting
			stmt.executeUpdate("insert into casting " + "values(1,1,'Main actor')");

			stmt.executeUpdate("insert into casting " + "values(1,2,'Main actor')");

			stmt.executeUpdate("insert into casting " + "values(2,2,'Main actor')");

			stmt.executeUpdate("insert into casting " + "values(2,3,'Main actor')");

			stmt.executeUpdate("insert into casting " + "values(3,6,'Main actor')");

			stmt.executeUpdate("insert into casting " + "values(3,7,'Supporting Actor')");

			stmt.executeUpdate("insert into casting " + "values(4,4,'Main actor')");

			stmt.executeUpdate("insert into casting " + "values(4,5,'Main actor')");

			// movieGenre
			stmt.executeUpdate("insert into movieGenre " + "values(1,'Fantasy')," + "(1,'Romance')");

			stmt.executeUpdate(
					"insert into movieGenre " + "values(2,'Fantasy')," + "(2,'Adventure')," + "(2,'Family')");

			stmt.executeUpdate("insert into movieGenre " + "values(3,'Drama')");

			stmt.executeUpdate("insert into movieGenre " + "values(4,'Action')," + "(4,'Drama')," + "(4,'Mystery'),"
					+ "(4,'Thriller')");

		} catch (SQLException e) {
			System.out.println("init error" + e.getErrorCode());
		}
		System.out.println("init end");

	}

	static void printsql(String s) {
		System.out.println("Translated SQL :" + s);
	}

	static void printtitle(String s) {

		System.out.println("---------------< " + s + " >---------------");

	}

	static void printvalue(ResultSet rs) throws SQLException {
		try {
			ResultSetMetaData metaData = rs.getMetaData();

			int count = metaData.getColumnCount();

			for (int i = 1; i <= count; i++)

			{
				String columnName = metaData.getColumnName(i);
				System.out.print("\t\t" + columnName);

			}
			System.out.println();

			while (rs.next()) {
				for (int i = 1; i <= count; i++) {
					String s = rs.getString(i);
					System.out.print("\t\t" + s);
				}

				System.out.println();
			}

			System.out.println();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	static void changeAvgRate(Statement stmt) throws SQLException {
		try {
			String sql = "update movie M  set avgRate=(select sum(rate)/count(rate)\n"
					+ "						   from customerRate \n"
					+ "								where M.movieID=customerRate.movieID\n"
					+ "								)";
			printsql(sql);
			stmt.executeUpdate(sql);

			ResultSet rs = stmt.executeQuery("select *from movie");
			printtitle("movie");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
			e.printStackTrace();
			return;
		}
		System.out.println("PostgreSQL JDBC Driver Registered!");
		/// if you have a error in this part, check jdbc driver(.jar file)

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/project_movie", "postgres",
					"7538");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		/// if you have a error in this part, check DB information (db_name, user name,
		/// password)

		if (connection != null) {
			System.out.println(connection);
			System.out.println("You made it, take control your database now!\n");
		} else {
			System.out.println("Failed to make connection!");
		}

		Statement stmt = connection.createStatement();

		 createTables(stmt);
		 init(stmt);

		//// 2.1. Winona Ryder won the ¡°Best supporting actor¡± award in 1994

		System.out.println("2.1 Statement : Winona Ryder won the ¡°Best supporting actor¡± award in 1994\n");
		try {
			stmt.executeUpdate(
					"insert  into award values(1,'Best supporting actor') except select awardID,awardName from award");
			stmt.executeUpdate(
					"insert  into actorObtain values(2, 1,1994) except select actorID,awardID,year from actorobtain ");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		printsql("insert  into award values(1,'Best supporting actor') except select awardID,awardName from award')");

		printsql("insert  into actorobtain values(2, 1,'1994') except select* from actorobtain");
		printsql("select *from award");

		ResultSet rs = stmt.executeQuery("select *from award");
		printtitle("award");
		printvalue(rs);

		rs = stmt.executeQuery("select *from actorObtain");
		printtitle("arctorObtain");
		printvalue(rs);

		// 2.2. Andrew Garfield won the ¡°Best supporting actor¡± award in 2011

		try {
			System.out.println("2.2 Statement : Andrew Garfield won the ¡°Best supporting actor¡± award in 2011\n");

			String s;

			s = "select actorID from actor where actorName='Andrew Garfield'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs);

			s = "insert into award(awardName) values('Best supporting actor') except select awardName from award";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			s = "select awardID from award where awardName='Best supporting actor'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs);

			s = "insert into actorObtain(actorID,awardID,year) values(7,1,2011) except select* from actorObtain";
			printsql(s);

			stmt.executeUpdate(s);// update insert

			rs = stmt.executeQuery("select *from award");
			printtitle("award");
			printvalue(rs);

			rs = stmt.executeQuery("select *from actorObtain");
			printtitle("arctorObtain");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2.3. Jesse Eisenberg won the ¡°Best main actor¡± award in 2011

		try {
			System.out.println("2.3 Statement :  Jesse Eisenberg won the ¡°Best main actor¡± award in 2011  \n");

			String s;

			s = "select actorID from actor where actorName='Jesse Eisenberg'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //actorID 6

			s = "insert into award(awardID,awardName) values(2,'Best main actor') except select* from award";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			s = "select awardID from award where awardName='Best supporting actor'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs);

			s = "insert into actorObtain(actorID,awardID,year) values(6,2,2011) except select* from actorObtain";

			printsql(s);

			stmt.executeUpdate(s);// update insert

			rs = stmt.executeQuery("select *from award");
			printtitle("award");
			printvalue(rs);

			rs = stmt.executeQuery("select *from actorObtain");
			printtitle("arctorObtain");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2.4. Johnny Depp won the ¡°Best villain actor¡± award in 2011

		try {
			System.out.println("2.4 Statement :   Johnny Depp won the ¡°Best villain actor¡± award in 2011  \n");

			String s;

			s = "select actorID from actor where actorName='Johnny Depp'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //actorID 1

			s = "insert into award(awardID,awardName) values(3,'Best villain actor') except select* from award";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			s = "select awardID from award where awardName='Best villain actor'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs);

			s = "insert into actorObtain(actorID,awardID,year) values(1,3,2011) except select* from actorObtain";

			printsql(s);

			stmt.executeUpdate(s);// update insert

			rs = stmt.executeQuery("select *from award");
			printtitle("award");
			printvalue(rs);

			rs = stmt.executeQuery("select *from actorObtain");
			printtitle("arctorObtain");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2.5. Edward Scissorhands won the ¡°Best fantasy movie¡± award in 1991
		try {
			System.out.println("  2.5. Edward Scissorhands won the ¡°Best fantasy movie¡± award in 1991 \n");

			String s;
			s = "select movieID from movie where movieName='Edward Scissorhands'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs);//movieID =1

			s = "insert into award(awardID,awardName) values(6,'Best fantasy movie') except select* from award";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			s = "insert into movieObtain(movieID,awardID,year) values(1,6,1991) except select* from movieObtain";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			rs = stmt.executeQuery("select *from award");
			printtitle("award");
			printvalue(rs);

			rs = stmt.executeQuery("select *from movieObtain");
			printtitle("movieObtain");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2.6. Alice In Wonderland won the ¡°Best fantasy movie¡± award in 2011

		try {
			System.out.println("   2.6. Alice In Wonderland won the ¡°Best fantasy movie¡± award in 2011 \n");

			String s;
			s = "select movieID from movie where movieName='Edward Scissorhands'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs);//movieID =2

			s = "insert into award(awardID,awardName) values(6,'Best fantasy movie') except select* from award";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			s = "insert into movieObtain(movieID,awardID,year) values(2,6,2011) except select* from movieObtain";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			rs = stmt.executeQuery("select *from award");
			printtitle("award");
			printvalue(rs);

			rs = stmt.executeQuery("select *from movieObtain");
			printtitle("movieObtain");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2.7. The Dark Knight won the ¡°Best picture¡± award in 2009
		try {
			System.out.println("   2.7. The Dark Knight won the ¡°Best picture¡± award in 2009 \n");

			String s;
			s = "select movieID from movie where movieName='Edward Scissorhands'";
			printsql(s);

			rs = stmt.executeQuery(s);// select
			// printvalue(rs);//movieID =4

			s = "insert into award(awardID,awardName) values(7,'Best picture') except select* from award";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			s = "insert into movieObtain(movieID,awardID,year) values(4,7,2009) except select* from movieObtain";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			rs = stmt.executeQuery("select *from award");
			printtitle("award");
			printvalue(rs);

			rs = stmt.executeQuery("select *from movieObtain");
			printtitle("movieObtain");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2.8 David Fincher won the ¡°Best director¡± award in 2011

		try {
			System.out.println(" 2.8 David Fincher won the ¡°Best director¡± award in 2011 \n");

			String s;

			s = "insert into award(awardID,awardName) values(5,'Best director') except select * from award";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			s = "select directorID from director where directorName='David Fincher'";
			printsql(s);

			rs = stmt.executeQuery(s);// select

			s = "insert into directorObtain(directorID,awardID,year) values(2,5,2011) except select* from directorObtain";

			printsql(s);
			stmt.executeUpdate(s);// update insert

			rs = stmt.executeQuery("select *from actor");
			printtitle("actor");
			printvalue(rs);

			rs = stmt.executeQuery("select *from award");
			printtitle("award");
			printvalue(rs);

			rs = stmt.executeQuery("select *from directorObtain");
			printtitle("directorObtain");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 3.1 Bob rates 5 to ¡°The Dark Knight"
		try {

			System.out.println(" 3.1 Bob rates 5 to 'The Dark Knight' \n");

			String s;
			s = "select customerID from customer where customerName='Bob'";
			printsql(s);
			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //customerID 1

			s = "select movieID from movie where movieName='The Dark Knight'";
			printsql(s);
			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //movieID 4

			s = "insert into customerRate(customerID,movieID,rate) values(1,4,5) except select* from customerRate";
			printsql(s);
			stmt.executeUpdate(s);// update insert

			changeAvgRate(stmt);

			rs = stmt.executeQuery("select * from customerRate");
			printtitle("customerRate");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 3.2 Bell rates 5 to the movies whose director is ¡°Tim Burton¡±

		try {

			System.out.println(" 3.2 Bell rates 5 to the movies whose director is ¡°Tim Burton¡±' \n");

			String s;
			s = "select directorID from director where directorName='Tim Burton'";
			printsql(s);
			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //directorID 1

			s = "select customerID\n" + "from customer\n" + "where customerName='Bell'";
			printsql(s);
			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //customerID 5

			s = "insert into customerRate \n"
					+ "select 5 as customerID,movieID,5.0 as rate from make where directorID=1"
					+ " except select* from customerRate";
			printsql(s);
			stmt.executeUpdate(s);// update insert

			changeAvgRate(stmt);

			rs = stmt.executeQuery("select * from customerRate");
			printtitle("customerRate");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 3.3 Jill rates 4 to the movies whose main actor is female.

		try {

			System.out.println("  3.3 Jill rates 4 to the movies whose main actor is female. \n");

			String s;

			s = "select customerID\n" + "from customer\n" + "where customerName='Jill'";
			printsql(s);
			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //customerID 4

			s = "insert into customerRate \n" + "select distinct 4 as customerID, movieID ,4.0 as rate\n"
					+ "from casting natural join actor\n" + "where gender='Female' \n" + "and role='Main actor'"
					+ " except select* from customerRate";
			printsql(s);
			stmt.executeUpdate(s);// update insert

			changeAvgRate(stmt);

			rs = stmt.executeQuery("select * from customerRate");
			printtitle("customerRate");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 3.4 Jack rates 4 to the fantasy movies.
		try {

			System.out.println("  Jack rates 4 to the fantasy movies. \n");

			String s;

			s = "select customerID\n" + "from customer\n" + "where customerName='Jack'";
			printsql(s);
			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //customerID 3

			s = "insert into customerRate select distinct 3 as customerID,movieID,4.0 as rate "
					+ "from movieGenre where genreName='Fantasy'" + " except select* from customerRate";

			printsql(s);

			stmt.executeUpdate(s);// update insert

			changeAvgRate(stmt);

			rs = stmt.executeQuery("select * from customerRate");
			printtitle("customerRate");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 3.5 John rates 5 to the movies whose director won the ¡°Best director¡± award

		try {

			System.out.println("  3.5 John rates 5 to the movies whose director won the ¡°Best director¡± award\n");

			String s;

			s = "select customerID\n" + "from customer\n" + "where customerName='John'";
			printsql(s);
			rs = stmt.executeQuery(s);// select
			// printvalue(rs); //customerID 2

			s = "insert into customerRate select distinct 2 as customerID,"
					+ "movieID, 5.0 as rate from award natural join directorObtain "
					+ "natural join make where awardName='Best director' except select* from customerRate";

			printsql(s);

			stmt.executeUpdate(s);// update insert

			changeAvgRate(stmt);

			rs = stmt.executeQuery("select * from customerRate");
			printtitle("customerRate");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 4. Select the names of the movies whose actor are dead.

		try {

			System.out.println("  4. Select the names of the movies whose actor are dead.\n");

			String s;

			s = "select movieName from actor natural join casting natural join movie "
					+ "where dateofdeath is not null";

			printsql(s);
			rs = stmt.executeQuery(s);// select

			printvalue(rs); // customerID 2

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 5. Select the names of the directors who cast the same actor more than once.

		try {

			System.out.println(" 5. Select the names of the directors who cast the same actor more than once.\n");

			String s;

			s = "select directorID,actorID,count (actorID) " + "from casting natural join make natural join director "
					+ "group by actorID,directorID having count(*)>1";

			printsql(s);
			rs = stmt.executeQuery(s);// select

			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 6. Select the names of the movies and the genres, where movies have the
		// common genre.

		try {

			System.out.println(
					"  6. Select the names of the movies and the genres, where movies have the common genre.\n");

			String s;

			s = "select genreName,movieName " + "from movie natural join movieGenre "
					+ "where genreName in(select genreName " + "from movie natural join movieGenre "
					+ "group by genreName having count(genreName)>1)";

			printsql(s);
			rs = stmt.executeQuery(s);// select

			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 7. Delete the movies whose director or actor did not get any award and delete
		// data from related tables.

		try {

			System.out.println(
					"  7. Delete the movies whose director or actor did not get any award and delete data from related tables.\n");

			String s;

			s = "delete from movie where movie.movieID not in (select movieID from casting natural join actorObtain) "
					+ "and movie.movieID not in(select movieID from make natural join directorObtain) ";

			printsql(s);
			stmt.executeUpdate(s);// select

			rs = stmt.executeQuery("select *from movie");
			printtitle("movie");
			printvalue(rs);

			rs = stmt.executeQuery("select *from casting");
			printtitle("casting");
			printvalue(rs);

			rs = stmt.executeQuery("select *from make");
			printtitle("make");
			printvalue(rs);

			rs = stmt.executeQuery("select *from movieGenre");
			printtitle("movieGenre");
			printvalue(rs);

			rs = stmt.executeQuery("select *from movieObtain");
			printtitle("movieObtain");
			printvalue(rs);

			rs = stmt.executeQuery("select *from customerRate");
			printtitle("customerRate");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 8. Delete all customers and delete data from related tables.

		try {

			System.out.println(" 8. Delete all customers and delete data from related tables.\n");

			String s;

			s = "delete  from customer";

			printsql(s);
			stmt.executeUpdate(s);// select

			rs = stmt.executeQuery("select *from movie");
			printtitle("movie");
			printvalue(rs);

			rs = stmt.executeQuery("select *from casting");
			printtitle("casting");
			printvalue(rs);

			rs = stmt.executeQuery("select *from make");
			printtitle("make");
			printvalue(rs);

			rs = stmt.executeQuery("select *from movieGenre");
			printtitle("movieGenre");
			printvalue(rs);

			rs = stmt.executeQuery("select *from movieObtain");
			printtitle("movieObtain");
			printvalue(rs);

			rs = stmt.executeQuery("select *from customerRate");
			printtitle("customerRate");
			printvalue(rs);
			rs = stmt.executeQuery("select *from customer");
			printtitle("customer");
			printvalue(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 9. Delete all tables and data.

		try {

			System.out.println("9.  Delete all customers and delete data from related tables.\n");

			String s;

			s = "drop table customerRate";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table make";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table casting";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table directorObtain";

			printsql(s);
			stmt.executeUpdate(s);// select
			s = "drop table actorObtain";

			printsql(s);
			stmt.executeUpdate(s);// select
			s = "drop table movieObtain";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table movieGenre";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table movie";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table director";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table actor";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table customer";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table award";

			printsql(s);
			stmt.executeUpdate(s);// select

			s = "drop table genre";

			printsql(s);
			stmt.executeUpdate(s);// select
			System.out.println("table deleted");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		stmt.close();
		connection.close();
	}
}