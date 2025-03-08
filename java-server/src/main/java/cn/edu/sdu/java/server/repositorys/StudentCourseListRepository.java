package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentCourseList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentCourseListRepository extends JpaRepository<StudentCourseList,Integer>{
    Optional<StudentCourseList> findByPersonId(Integer personId);

}
