# Password-Authenticater-in-Java


Here is the Schema for the data base and the table which are being used in the code.
Please refer to the scripts below. 

create database user;

create table login(userId int(20),userPassword varchar(30),userName varchar(30),userPswChangedDate varchar(30), primary key(userId,userPassword);


+--------+--------------+-------------+--------------------+
| userId | userPassword | userName    | userPswChangedDate |
+--------+--------------+-------------+--------------------+
|    100 | Fish@1230    | Demom123    | 10/04/2020         |
|    100 | Sunny@123    | Demom123    | 01/12/2019         |
|    100 | Sunny@124    | Demom123    | 25/03/2020         |
|    100 | Sunny@125    | Demom123    | 25/02/2020         |
|    100 | Sunny@126    | Demom123    | 25/01/2020         |
|    101 | Neon@123     | Nikita@12   | 10/04/2020         |


