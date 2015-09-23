select Sid, Sname, MajorId, GradYear from student
select DId, DName from dept
select CId, Title, DeptId from course
select SectId, CourseId, Prof, YearOffered from section
select EId, StudentId, SectionId, Grade from enroll
select Sid, Sname from student where sid = 3 and majorid = 10
insert into student(sid, sname, majorid, gradyear) values (1, 'sukmadic', 10, 2016)
