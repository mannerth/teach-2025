package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.CourseEx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseExRepository extends JpaRepository<CourseEx, Integer> {
    @Query(value = "from CourseEx where ?1='' or course_num like %?1% ")
    List<CourseEx> findCourseExListByNum(String num);

    @Query(value = "from CourseEx where ?1='' or teacher.person.num like %?1% ")
    List<CourseEx> findCourseExListByTeacher(String num);

    @Query(value = "from CourseEx where ?1='' or course.num like %?1% ")
    List<CourseEx> findCourseExListByCourse(String num);

    @Query(value = "from CourseEx where is_choosable = True ")
    List<CourseEx> findChoosableCourseExList();

    @Query(value = "from CourseEx where is_choosable = False ")
    List<CourseEx> findNotChoosableCourseExList();

    @Query(value = "from CourseEx where (?1=0 or course.id=?1) and (teacher.person.num = ?2 or ?2='')")
    List<CourseEx> findCourseByTeacherCourse_num(Integer cid, String num);

    Optional<CourseEx> findByCourse_num(String num);
}
