package cn.edu.sdu.java.server.repositorys;


import cn.edu.sdu.java.server.models.EHonorType;
import cn.edu.sdu.java.server.models.Honor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {
    @Query("select h from Honor h where h.student.personId=?1")
    List<Honor> findByStudentId(Integer studentId);

    @Query( value = "from Honor where (?1=null or ?1='' or student.person.name like %?1% or student.person.num like %?1% ) and (?2 is null or honorType.type=?2)")
    List<Honor> findByNumNameAndType(String numName, EHonorType type, Pageable pageable);

    @Query("select count(h) from Honor h where (?1=null or ?1='' or h.student.person.name like %?1% or h.student.person.num like %?1% ) and (?2 is null or h.honorType.type=?2) ")
    int countByNumNameAndType(String numName, EHonorType type);
}
