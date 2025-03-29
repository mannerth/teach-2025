package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentLeaveRepository extends JpaRepository<StudentLeave, Integer> {
    @Query(value = "from StudentLeave where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<StudentLeave> findStudentLeaveListByNumName(String numName);
}
