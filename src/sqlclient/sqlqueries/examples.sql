/* 
 * Initial database setup 
 *
 * Literally copied from CreateStudentDB provided with simpledb
 */

create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)

insert into STUDENT(SId, SName, MajorId, GradYear) values (1, 'joe', 10, 2004)
insert into STUDENT(SId, SName, MajorId, GradYear) values (2, 'amy', 20, 2004)
insert into STUDENT(SId, SName, MajorId, GradYear) values (3, 'max', 10, 2005)
insert into STUDENT(SId, SName, MajorId, GradYear) values (4, 'sue', 20, 2005)
insert into STUDENT(SId, SName, MajorId, GradYear) values (5, 'bob', 30, 2003)
insert into STUDENT(SId, SName, MajorId, GradYear) values (6, 'kim', 20, 2001)
insert into STUDENT(SId, SName, MajorId, GradYear) values (7, 'art', 30, 2004)
insert into STUDENT(SId, SName, MajorId, GradYear) values (8, 'pat', 20, 2001)
insert into STUDENT(SId, SName, MajorId, GradYear) values (9, 'lee', 10, 2004)

create table DEPT(DId int, DName varchar(8))

insert into DEPT(DId, DName) values (10, 'compsci')
insert into DEPT(DId, DName) values (20, 'math')
insert into DEPT(DId, DName) values (30, 'drama')

create table COURSE(CId int, Title varchar(20), DeptId int)

insert into COURSE(CId, Title, DeptId) values (12, 'db systems', 10)
insert into COURSE(CId, Title, DeptId) values (22, 'compilers', 10)
insert into COURSE(CId, Title, DeptId) values (32, 'calculus', 20)
insert into COURSE(CId, Title, DeptId) values (42, 'algebra', 20)
insert into COURSE(CId, Title, DeptId) values (52, 'acting', 30)
insert into COURSE(CId, Title, DeptId) values (62, 'elocution', 30)

create table SECTION(SectId int, CourseId int, Prof varchar(8), YearOffered int)

insert into SECTION(SectId, CourseId, Prof, YearOffered) values (13, 12, 'turing', 2004)
insert into SECTION(SectId, CourseId, Prof, YearOffered) values (23, 12, 'turing', 2005)
insert into SECTION(SectId, CourseId, Prof, YearOffered) values (33, 32, 'newton', 2000)
insert into SECTION(SectId, CourseId, Prof, YearOffered) values (43, 32, 'einstein', 2001)
insert into SECTION(SectId, CourseId, Prof, YearOffered) values (53, 62, 'brando', 2001)

create table ENROLL(EId int, StudentId int, SectionId int, Grade varchar(2))

insert into ENROLL(EId, StudentId, SectionId, Grade) values (14, 1, 13, 'A')
insert into ENROLL(EId, StudentId, SectionId, Grade) values (24, 1, 43, 'C' )
insert into ENROLL(EId, StudentId, SectionId, Grade) values (34, 2, 43, 'B+')
insert into ENROLL(EId, StudentId, SectionId, Grade) values (44, 4, 33, 'B' )
insert into ENROLL(EId, StudentId, SectionId, Grade) values (54, 4, 53, 'A' )
insert into ENROLL(EId, StudentId, SectionId, Grade) values (64, 6, 53, 'A' )

/* 
 * Selection and insertion statements
 * 
 * These statements are tested using the same setup as above, so modifications before and after are tested to see if the same output is produced.
 */


/*
 * Ensures that the correct data is in each table
 * The data returned should match the inserts executed above.
 */

select Sid, Sname, MajorId, GradYear from student
select DId, DName from dept
select CId, Title, DeptId from course
select SectId, CourseId, Prof, YearOffered from section
select EId, StudentId, SectionId, Grade from enroll

/*
 * Check that conditionals work
 */

select Sid, Sname from student where sid = 3 and majorid = 10
select Sid, Sname from student where majorid = 10
select DId, DName from dept where Did = 10

/*
 * For testing queries on course
 */
select Cid, title from course

/*
 * Additional conditional testing
 */
select Sid, Sname from student where gradyear = 2005 and sname = 'sue'

/*
 * Testing an insertion
 */
insert into student(sid, sname, majorid, gradyear) values (1, 'test', 10, 2016)
select Sid, Sname from student where gradyear = 2016 and majorid = 10
