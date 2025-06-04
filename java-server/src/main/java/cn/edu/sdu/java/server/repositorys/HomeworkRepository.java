package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
 * Homework 数据操作接口，主要实现Homework数据的查询操作
 * List<Homework> findByCourseId(Integer courseId)
 */

@Repository
public interface HomeworkRepository extends JpaRepository<Homework,Integer> {
    @Query(value="from Homework where (?1=0 or course.courseId=?1)")
    List<Homework> findByCourseId(Integer courseId);

    @Query(value="from Homework where (?1=0 or courseEx.courseExId=?1)")
    List<Homework> findByCourseExId(Integer courseExId);

    @Query(value="from Homework where (?1=0 or student.person.personId=?1)")
    List<Homework> findByStudentId(Integer studentId);

    @Query(value= "from Homework h where (?1 = '' or h.student.person.name like %?1% or h.teacher.person.name like %?1%) and (?2='' or h.student.person.num = ?2) and (?3= '' or h.teacher.person.num = ?3)")
    List<Homework> getHomeworkList(String search, String studentNum, String teacherNum);
}
