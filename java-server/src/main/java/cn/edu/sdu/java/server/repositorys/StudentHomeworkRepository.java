package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentHomework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentHomeworkRepository extends JpaRepository<StudentHomework,Integer> {
    @Query( value = "from StudentHomework where ?1=0 or homework.homeworkId=?1")
    List<StudentHomework> findByHomework(Integer id);

    @Query(value = "from StudentHomework where student.personId=?1 and homework.homeworkId=?2")
    Optional<StudentHomework> findByPersonHomework(Integer personId, Integer homeworkId);
}
