package cn.edu.sdu.java.server.repositorys;


import cn.edu.sdu.java.server.models.EHonorType;
import cn.edu.sdu.java.server.models.HonorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HonorTypeRepository extends JpaRepository<HonorType,Integer> {
    @Query("select ht from HonorType ht where ht.type=?1")
    Optional<HonorType> findByEHonorType(EHonorType eHonorType);
}
