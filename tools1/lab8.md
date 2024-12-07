Member
+------------+
| MemberID   | PK
| Name       |
| StudentNum | (optional)
| Email      | UNIQUE
| SkillLevel |
+------------+
    |1
    |------------+  
    |1           |  
Committee       Event
+------------+  +------------+
| Role       |  | EventID    | PK
| MemberID   |  | Date       | 
+------------+  | Name       |
     1 |         | Location   | UNIQUE(Date+Location)
       |         | Desc       | (optional)
       +<--------| OrganiserID|
       1         +------------+
                  |     |       |
         Many-to-Many   | 1
                        |
            EventAttendance
            +------------+
            | EventID    | FK
            | MemberID   | FK
            +------------+
