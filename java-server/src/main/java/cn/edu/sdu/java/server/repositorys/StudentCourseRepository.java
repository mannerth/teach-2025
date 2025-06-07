package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Integer> {
    @Query(value = "from StudentCourse where courseEx.courseExId=?1")
    List<StudentCourse> findByCourseEx(Integer id);

    @Query(value = "from StudentCourse where student.personId=?1")
    List<StudentCourse> findByStudent(Integer id);
}
