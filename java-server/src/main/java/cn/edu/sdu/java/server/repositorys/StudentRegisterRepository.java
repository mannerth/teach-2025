package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/*
 * StudentRegister 数据操作接口，主要实现 StudentRegister 数据的查询操作
 * Optional<StudentRegister> findByPersonPersonId(Integer personId); 根据关联的 Person 的 personId 查询获得 Option<StudentRegister> 对象 命名规范
 * List<StudentRegister> findByPersonName(String name); 根据关联的 Person 的 name 查询获得 List<StudentRegister> 对象集合  可能存在相同姓名的多个学生 命名规范
 * List<StudentRegister> findStudentListByNumName(String numName); 根据输入的参数 如果参数为空，查询所有的学生，输入参数不为空，查询学号或姓名包含输入参数串的所有学生集合
 * Page<StudentRegister> findStudentPageByNumName(String numName,  Pageable pageable); 分页查询，根据输入的参数 如果参数为空，查询所有的学生，输入参数不为空，查询学号或姓名包含输入参数串的所有学生集合
 */
public interface StudentRegisterRepository extends JpaRepository<StudentRegister, Integer> {
    Optional<StudentRegister> findByPersonPersonId(Integer personId);

    // 假设 StudentRegister 关联的 Person 有 name 属性
    List<StudentRegister> findByPersonName(String name);

    @Query(value = "from StudentRegister where ?1='' or person.name like %?1% or studentNumber like %?1% ",
            countQuery = "SELECT count(personId) from StudentRegister where ?1='' or person.name like %?1% or studentNumber like %?1% ")
    List<StudentRegister> findStudentListByNumName(String numName);

    @Query(value = "from StudentRegister where ?1='' or person.name like %?1% or studentNumber like %?1% ",
            countQuery = "SELECT count(personId) from StudentRegister where ?1='' or person.name like %?1% or studentNumber like %?1% ")
    Page<StudentRegister> findStudentPageByNumName(String numName, Pageable pageable);
}