package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Homework;
import cn.edu.sdu.java.server.models.Score;
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
}
