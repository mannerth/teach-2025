package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Integer> {

}
