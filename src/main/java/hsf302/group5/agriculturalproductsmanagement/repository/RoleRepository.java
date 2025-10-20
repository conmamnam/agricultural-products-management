package hsf302.group5.agriculturalproductsmanagement.repository;

import hsf302.group5.agriculturalproductsmanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    public Role findByRoleId(int roleId);
}
