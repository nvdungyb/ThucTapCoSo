package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUserWithOneRole() {
        Role role = entityManager.find(Role.class, 2);
        User userAdmin = new User("dung@codejava", "123", "Nguyen", "dung");
        userAdmin.addRole(role);

//		Thực hiện lưu đối tượng vào db thông qua phương thức save, kq là trả về một đối tượng.
        User savedUser = repo.save(userAdmin);
//		Kiểm tra xem id của đối tượng đã được tạo và lưu vào db hay không.
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithTwoRole() {
        User user = new User("ravi@gmail.com", "1234", "ravi", "david");
//		Lưu ý role phải tồn tại trong table roles thì khóa ngoại mới tham chiếu đc.
        Role roleShiper = new Role(3);
        Role roleAssistant = new Role(4);

        user.addRole(roleShiper);
        user.addRole(roleAssistant);

        User savedUser = repo.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = repo.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        User user = repo.findById(1).get();
        System.out.println(user);

        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User user = repo.findById(1).get();
        user.setEnabled(true);
        user.setEmail("nguyenvandungk49a1@gmail.com");
        repo.save(user);
    }

    @Test
    public void testUpdateUserRoles() {
        User user = repo.findById(1).get();
        Role roleSalesPerson = new Role(2);
        user.getRoles().remove(roleSalesPerson);

        Role roleAdmin = new Role(1);
        Role roleAssistant = new Role(4);
        user.addRole(roleAssistant);
        repo.save(user);
    }

    @Test
    public void testDeleteUser() {
        Integer userUd = 7;
        repo.deleteById(userUd);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "nguyenvandungk49a1@gmail.com";
        User user = repo.getUserByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    public void countById() {
        Integer id = 1;
        Long countById = repo.countById(id);

        assertThat(countById).isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 1;
        repo.updateEnabledStatus(1, false);
    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<User> page = repo.findAll(pageable);
        List<User> listUsers = page.getContent();

        for (User user : listUsers) {
            System.out.println(user);
        }

        assertThat(listUsers.size()).isEqualTo(pageSize);
    }
}
