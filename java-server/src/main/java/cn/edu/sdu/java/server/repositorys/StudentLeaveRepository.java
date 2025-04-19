package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentLeave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

//public interface StudentLeaveRepository extends JpaRepository<StudentLeave, Integer> {
//    Optional<StudentLeave> findByLeaveId(Integer id);
//    @Query(value = "from StudentLeave where ?1='' ")
//    List<StudentLeave> findStudentLeaveListByNumName(String numName);
//}


public interface StudentLeaveRepository extends JpaRepository<StudentLeave,Integer> {

    @Query(value = "from StudentLeave where ?1='' or  name like %?1% ")
    List<StudentLeave> findStudentLeaveListByNumName(String numName);


    @Query(value = "from StudentLeave where ?1='' or name like %?1% ",
            countQuery = "SELECT count(leaveId) from StudentLeave where ?1='' or  name like %?1% ")
    Page<StudentLeave> findStudentLeavePageByNumName(String numName, Pageable pageable);
}

